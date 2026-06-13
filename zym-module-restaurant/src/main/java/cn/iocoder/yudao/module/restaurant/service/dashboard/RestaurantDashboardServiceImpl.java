package cn.iocoder.yudao.module.restaurant.service.dashboard;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.restaurant.controller.admin.dashboard.vo.*;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.cost.CostDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendDetailDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.revenue.RevenueDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.cost.CostMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendDetailMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendPeriodMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.revenue.RevenueMapper;
import cn.iocoder.yudao.module.restaurant.enums.CostStatusEnum;
import cn.iocoder.yudao.module.restaurant.enums.DividendDetailStatusEnum;
import cn.iocoder.yudao.module.restaurant.enums.DividendPeriodStatusEnum;
import cn.iocoder.yudao.module.restaurant.enums.RevenueStatusEnum;
import cn.iocoder.yudao.module.restaurant.service.store.StoreService;
import cn.iocoder.yudao.module.restaurant.util.RestaurantMoneyUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 经营看板 Service 实现类
 *
 * @author zhangyoming
 */
@Service
@Validated
public class RestaurantDashboardServiceImpl implements RestaurantDashboardService {

    @Resource
    private RevenueMapper revenueMapper;

    @Resource
    private CostMapper costMapper;

    @Resource
    private DividendPeriodMapper dividendPeriodMapper;

    @Resource
    private DividendDetailMapper dividendDetailMapper;

    @Resource
    private StoreService storeService;

    @Override
    public DashboardSummaryRespVO getSummary(DashboardReqVO reqVO) {
        validateDateRange(reqVO);

        List<RevenueDO> revenues = revenueMapper.selectListByBizDateBetween(reqVO.getStartDate(), reqVO.getEndDate(),
                RevenueStatusEnum.CONFIRMED.getStatus(), reqVO.getStoreId());
        List<CostDO> costs = costMapper.selectListByBizDateBetween(reqVO.getStartDate(), reqVO.getEndDate(),
                CostStatusEnum.CONFIRMED.getStatus(), reqVO.getStoreId());
        List<DividendPeriodDO> periods = dividendPeriodMapper.selectListByStartDateBetween(
                reqVO.getStartDate(), reqVO.getEndDate(), reqVO.getStoreId());

        String startPeriodMonth = toPeriodMonth(reqVO.getStartDate());
        String endPeriodMonth = toPeriodMonth(reqVO.getEndDate());
        List<DividendDetailDO> details = dividendDetailMapper.selectListByPeriodMonthBetween(
                startPeriodMonth, endPeriodMonth, reqVO.getStoreId(), null);

        BigDecimal totalRevenue = sumRevenueAmount(revenues);
        BigDecimal totalCost = sumCostAmount(costs);
        BigDecimal profitAmount = RestaurantMoneyUtils.scaleMoney(totalRevenue.subtract(totalCost));

        BigDecimal distributableProfit = periods.stream()
                .filter(period -> !DividendPeriodStatusEnum.CANCELED.getStatus().equals(period.getStatus()))
                .map(DividendPeriodDO::getDistributableProfit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paidDividendAmount = details.stream()
                .filter(detail -> DividendDetailStatusEnum.PAID.getStatus().equals(detail.getStatus()))
                .map(DividendDetailDO::getDividendAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal waitPayDividendAmount = details.stream()
                .filter(detail -> DividendDetailStatusEnum.GENERATED.getStatus().equals(detail.getStatus()))
                .map(DividendDetailDO::getDividendAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DashboardSummaryRespVO respVO = new DashboardSummaryRespVO();
        respVO.setStartDate(reqVO.getStartDate());
        respVO.setEndDate(reqVO.getEndDate());
        respVO.setTotalRevenue(RestaurantMoneyUtils.scaleMoney(totalRevenue));
        respVO.setTotalCost(RestaurantMoneyUtils.scaleMoney(totalCost));
        respVO.setProfitAmount(RestaurantMoneyUtils.scaleMoney(profitAmount));
        respVO.setDistributableProfit(RestaurantMoneyUtils.scaleMoney(distributableProfit));
        respVO.setPaidDividendAmount(RestaurantMoneyUtils.scaleMoney(paidDividendAmount));
        respVO.setWaitPayDividendAmount(RestaurantMoneyUtils.scaleMoney(waitPayDividendAmount));
        respVO.setPeriodCount((long) periods.size());
        return respVO;
    }

    @Override
    public List<DashboardTrendRespVO> getTrend(DashboardReqVO reqVO) {
        validateDateRange(reqVO);

        List<RevenueDO> revenues = revenueMapper.selectListByBizDateBetween(reqVO.getStartDate(), reqVO.getEndDate(),
                RevenueStatusEnum.CONFIRMED.getStatus(), reqVO.getStoreId());
        List<CostDO> costs = costMapper.selectListByBizDateBetween(reqVO.getStartDate(), reqVO.getEndDate(),
                CostStatusEnum.CONFIRMED.getStatus(), reqVO.getStoreId());

        Map<String, BigDecimal> revenueMap = revenues.stream()
                .collect(Collectors.groupingBy(
                        revenue -> toPeriodMonth(revenue.getBizDate()),
                        Collectors.mapping(RevenueDO::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, RestaurantDashboardServiceImpl::safeAdd))));

        Map<String, BigDecimal> costMap = costs.stream()
                .collect(Collectors.groupingBy(
                        cost -> toPeriodMonth(cost.getBizDate()),
                        Collectors.mapping(CostDO::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, RestaurantDashboardServiceImpl::safeAdd))));

        List<String> months = buildMonthList(reqVO.getStartDate(), reqVO.getEndDate());
        return months.stream().map(month -> {
            BigDecimal revenueAmount = RestaurantMoneyUtils.scaleMoney(revenueMap.get(month));
            BigDecimal costAmount = RestaurantMoneyUtils.scaleMoney(costMap.get(month));
            BigDecimal profitAmount = RestaurantMoneyUtils.scaleMoney(revenueAmount.subtract(costAmount));

            DashboardTrendRespVO respVO = new DashboardTrendRespVO();
            respVO.setMonth(month);
            respVO.setRevenueAmount(revenueAmount);
            respVO.setCostAmount(costAmount);
            respVO.setProfitAmount(profitAmount);
            return respVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<DashboardStoreRankRespVO> getStoreProfitRank(DashboardReqVO reqVO) {
        validateDateRange(reqVO);

        List<RevenueDO> revenues = revenueMapper.selectListByBizDateBetween(reqVO.getStartDate(), reqVO.getEndDate(),
                RevenueStatusEnum.CONFIRMED.getStatus(), reqVO.getStoreId());
        List<CostDO> costs = costMapper.selectListByBizDateBetween(reqVO.getStartDate(), reqVO.getEndDate(),
                CostStatusEnum.CONFIRMED.getStatus(), reqVO.getStoreId());

        Map<Long, BigDecimal> revenueMap = revenues.stream()
                .filter(item -> item.getStoreId() != null)
                .collect(Collectors.groupingBy(
                        RevenueDO::getStoreId,
                        Collectors.mapping(RevenueDO::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, RestaurantDashboardServiceImpl::safeAdd))));

        Map<Long, BigDecimal> costMap = costs.stream()
                .filter(item -> item.getStoreId() != null)
                .collect(Collectors.groupingBy(
                        CostDO::getStoreId,
                        Collectors.mapping(CostDO::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, RestaurantDashboardServiceImpl::safeAdd))));

        Set<Long> storeIds = new HashSet<>();
        storeIds.addAll(revenueMap.keySet());
        storeIds.addAll(costMap.keySet());

        if (CollUtil.isEmpty(storeIds)) {
            return Collections.emptyList();
        }

        Map<Long, StoreDO> storeMap = storeService.getStoreMap(storeIds);

        return storeIds.stream().map(storeId -> {
                    BigDecimal revenueAmount = RestaurantMoneyUtils.scaleMoney(revenueMap.get(storeId));
                    BigDecimal costAmount = RestaurantMoneyUtils.scaleMoney(costMap.get(storeId));
                    BigDecimal profitAmount = RestaurantMoneyUtils.scaleMoney(revenueAmount.subtract(costAmount));

                    DashboardStoreRankRespVO respVO = new DashboardStoreRankRespVO();
                    respVO.setStoreId(storeId);
                    StoreDO store = storeMap.get(storeId);
                    respVO.setStoreName(store != null ? store.getName() : "");
                    respVO.setRevenueAmount(revenueAmount);
                    respVO.setCostAmount(costAmount);
                    respVO.setProfitAmount(profitAmount);
                    return respVO;
                })
                .sorted(Comparator.comparing(DashboardStoreRankRespVO::getProfitAmount).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardShareholderRankRespVO> getShareholderDividendRank(DashboardReqVO reqVO) {
        validateDateRange(reqVO);

        String startPeriodMonth = toPeriodMonth(reqVO.getStartDate());
        String endPeriodMonth = toPeriodMonth(reqVO.getEndDate());

        List<DividendDetailDO> details = dividendDetailMapper.selectListByPeriodMonthBetween(
                startPeriodMonth, endPeriodMonth, reqVO.getStoreId(), null);

        if (CollUtil.isEmpty(details)) {
            return Collections.emptyList();
        }

        Map<Long, List<DividendDetailDO>> groupMap = details.stream()
                .filter(item -> item.getShareholderId() != null)
                .collect(Collectors.groupingBy(DividendDetailDO::getShareholderId));

        return groupMap.entrySet().stream().map(entry -> {
                    Long shareholderId = entry.getKey();
                    List<DividendDetailDO> list = entry.getValue();

                    BigDecimal dividendAmount = list.stream()
                            .filter(item -> !DividendDetailStatusEnum.CANCELED.getStatus().equals(item.getStatus()))
                            .map(DividendDetailDO::getDividendAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    DashboardShareholderRankRespVO respVO = new DashboardShareholderRankRespVO();
                    respVO.setShareholderId(shareholderId);
                    respVO.setShareholderName(list.get(0).getShareholderName());
                    respVO.setDividendAmount(RestaurantMoneyUtils.scaleMoney(dividendAmount));
                    respVO.setDividendCount((long) list.size());
                    return respVO;
                })
                .sorted(Comparator.comparing(DashboardShareholderRankRespVO::getDividendAmount).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardPeriodStatusRespVO> getPeriodStatusStatistics(DashboardReqVO reqVO) {
        validateDateRange(reqVO);

        List<DividendPeriodDO> periods = dividendPeriodMapper.selectListByStartDateBetween(
                reqVO.getStartDate(), reqVO.getEndDate(), reqVO.getStoreId());

        Map<Integer, Long> statusCountMap = periods.stream()
                .filter(item -> item.getStatus() != null)
                .collect(Collectors.groupingBy(DividendPeriodDO::getStatus, Collectors.counting()));

        return Arrays.stream(DividendPeriodStatusEnum.values()).map(statusEnum -> {
            DashboardPeriodStatusRespVO respVO = new DashboardPeriodStatusRespVO();
            respVO.setStatus(statusEnum.getStatus());
            respVO.setStatusName(statusEnum.getName());
            respVO.setCount(statusCountMap.getOrDefault(statusEnum.getStatus(), 0L));
            return respVO;
        }).collect(Collectors.toList());
    }

    private void validateDateRange(DashboardReqVO reqVO) {
        if (reqVO.getStartDate() == null || reqVO.getEndDate() == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为空");
        }
        if (reqVO.getStartDate().isAfter(reqVO.getEndDate())) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }
    }

    private BigDecimal sumRevenueAmount(List<RevenueDO> revenues) {
        return revenues.stream()
                .map(RevenueDO::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumCostAmount(List<CostDO> costs) {
        return costs.stream()
                .map(CostDO::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal safeAdd(BigDecimal a, BigDecimal b) {
        return RestaurantMoneyUtils.nullToZero(a).add(RestaurantMoneyUtils.nullToZero(b));
    }

    private String toPeriodMonth(LocalDate date) {
        return YearMonth.from(date).toString();
    }

    private List<String> buildMonthList(LocalDate startDate, LocalDate endDate) {
        List<String> months = new ArrayList<>();
        YearMonth start = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);

        while (!start.isAfter(end)) {
            months.add(start.toString());
            start = start.plusMonths(1);
        }
        return months;
    }

}