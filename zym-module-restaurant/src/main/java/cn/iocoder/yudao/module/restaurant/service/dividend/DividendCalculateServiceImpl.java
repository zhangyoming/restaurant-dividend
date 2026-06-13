package cn.iocoder.yudao.module.restaurant.service.dividend;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.shareholder.ShareholderDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.storeshareholder.StoreShareholderDO;
import cn.iocoder.yudao.module.restaurant.service.cost.CostService;
import cn.iocoder.yudao.module.restaurant.service.dividend.bo.DividendDetailCalculateRespBO;
import cn.iocoder.yudao.module.restaurant.service.dividend.bo.DividendPeriodCalculateRespBO;
import cn.iocoder.yudao.module.restaurant.service.revenue.RevenueService;
import cn.iocoder.yudao.module.restaurant.service.shareholder.ShareholderService;
import cn.iocoder.yudao.module.restaurant.service.store.StoreService;
import cn.iocoder.yudao.module.restaurant.service.storeshareholder.StoreShareholderService;
import cn.iocoder.yudao.module.restaurant.util.RestaurantMoneyUtils;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.*;

/**
 * 分红计算 Service 实现类
 *
 * 只负责计算，不负责落库。
 *
 * @author zhangyoming
 */
@Service
@Validated
public class DividendCalculateServiceImpl implements DividendCalculateService {

    /**
     * 默认预留金额。
     *
     * 第一阶段没有分红规则模块，先默认 0。
     * 后续可以改成：利润 * 预留比例。
     */
    private static final BigDecimal DEFAULT_RESERVE_AMOUNT = BigDecimal.ZERO;

    @Resource
    private StoreService storeService;

    @Resource
    private StoreShareholderService storeShareholderService;

    @Resource
    private ShareholderService shareholderService;

    @Resource
    private RevenueService revenueService;

    @Resource
    private CostService costService;

    @Override
    public DividendPeriodCalculateRespBO calculateDividendPeriod(Long storeId, String periodMonth) {
        // 1. 校验门店存在且启用
        storeService.validateStoreList(Collections.singleton(storeId));

        // 2. 解析账期月份，计算开始/结束日期
        YearMonth yearMonth = parsePeriodMonth(periodMonth);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 3. 汇总收入和成本：只统计已确认数据
        BigDecimal totalRevenue = revenueService.getRevenueSummaryAmount(storeId, startDate, endDate);
        BigDecimal totalCost = costService.getCostSummaryAmount(storeId, startDate, endDate);

        // 4. 计算利润和可分红金额
        BigDecimal profitAmount = RestaurantMoneyUtils.scaleMoney(
                RestaurantMoneyUtils.nullToZero(totalRevenue)
                        .subtract(RestaurantMoneyUtils.nullToZero(totalCost)));
        BigDecimal reserveAmount = RestaurantMoneyUtils.scaleMoney(DEFAULT_RESERVE_AMOUNT);
        BigDecimal distributableProfit = RestaurantMoneyUtils.scaleMoney(profitAmount.subtract(reserveAmount));

        // 5. 统一金额精度后返回
        DividendPeriodCalculateRespBO result = new DividendPeriodCalculateRespBO();
        result.setStoreId(storeId);
        result.setPeriodMonth(periodMonth);
        result.setStartDate(startDate);
        result.setEndDate(endDate);
        result.setTotalRevenue(RestaurantMoneyUtils.scaleMoney(totalRevenue));
        result.setTotalCost(RestaurantMoneyUtils.scaleMoney(totalCost));
        result.setProfitAmount(profitAmount);
        result.setReserveAmount(reserveAmount);
        result.setDistributableProfit(distributableProfit);
        return result;
    }

    @Override
    public List<DividendDetailCalculateRespBO> calculateDividendDetails(DividendPeriodDO period) {
        // 1. 可分红金额必须大于 0
        BigDecimal distributableProfit = RestaurantMoneyUtils.scaleMoney(period.getDistributableProfit());
        if (!RestaurantMoneyUtils.isPositive(distributableProfit)) {
            throw exception(DIVIDEND_DETAIL_PROFIT_NOT_POSITIVE);
        }

        // 2. 查询门店正常持股股东，并按 id 排序
        // 排序很重要：只有顺序固定，最后一个承担尾差的人才是固定的，结果才可预测。
        List<StoreShareholderDO> storeShareholders = storeShareholderService
                .getStoreShareholderListByStoreId(period.getStoreId())
                .stream()
                .filter(item -> CommonStatusEnum.ENABLE.getStatus().equals(item.getStatus()))
                .sorted(Comparator.comparing(StoreShareholderDO::getId))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(storeShareholders)) {
            throw exception(DIVIDEND_DETAIL_NO_SHAREHOLDER);
        }

        // 3. 校验持股比例
        validateShareRatios(storeShareholders);

        // 4. 批量查询股东，避免循环查数据库
        List<Long> shareholderIds = storeShareholders.stream()
                .map(StoreShareholderDO::getShareholderId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, ShareholderDO> shareholderMap = shareholderService.getShareholderMap(shareholderIds);

        // 5. 判断持股比例是否刚好等于 100%，用于尾差处理
        BigDecimal totalShareRatio = sumShareRatio(storeShareholders);
        boolean fullRatio = totalShareRatio.compareTo(RestaurantMoneyUtils.ONE_HUNDRED) == 0;

        // 6. 逐个股东计算分红金额
        BigDecimal allocatedAmount = BigDecimal.ZERO;
        List<DividendDetailCalculateRespBO> results = new java.util.ArrayList<>(storeShareholders.size());

        for (int i = 0; i < storeShareholders.size(); i++) {
            StoreShareholderDO storeShareholder = storeShareholders.get(i);

            ShareholderDO shareholder = shareholderMap.get(storeShareholder.getShareholderId());
            if (shareholder == null) {
                throw exception(SHAREHOLDER_NOT_EXISTS);
            }

            BigDecimal normalAmount = RestaurantMoneyUtils.calculateDividendAmount(
                    distributableProfit, storeShareholder.getShareRatio());

            BigDecimal dividendAmount = normalAmount;
            BigDecimal roundingDiffAmount = BigDecimal.ZERO;

            // 如果比例合计刚好 100%，最后一个股东承担尾差
            if (fullRatio && i == storeShareholders.size() - 1) {
                BigDecimal finalAmount = RestaurantMoneyUtils.calculateRoundingDiff(
                        distributableProfit, allocatedAmount);
                dividendAmount = RestaurantMoneyUtils.scaleMoney(finalAmount);
                roundingDiffAmount = RestaurantMoneyUtils.scaleMoney(dividendAmount.subtract(normalAmount));
            }

            allocatedAmount = RestaurantMoneyUtils.scaleMoney(allocatedAmount.add(dividendAmount));

            DividendDetailCalculateRespBO result = new DividendDetailCalculateRespBO();
            result.setStoreShareholderId(storeShareholder.getId());
            result.setShareholderId(storeShareholder.getShareholderId());
            result.setShareholderName(shareholder.getName());
            result.setShareRatio(RestaurantMoneyUtils.scaleRatio(storeShareholder.getShareRatio()));
            result.setProfitAmount(distributableProfit);
            result.setDividendAmount(dividendAmount);
            result.setRoundingDiffAmount(roundingDiffAmount);
            results.add(result);
        }

        // 7. 如果持股比例合计为 100%，校验明细合计必须等于可分红金额
        if (fullRatio) {
            validateDividendAmountTotal(distributableProfit, results);
        }

        return results;
    }

    @VisibleForTesting
    YearMonth parsePeriodMonth(String periodMonth) {
        try {
            return YearMonth.parse(periodMonth);
        } catch (DateTimeParseException ex) {
            throw exception(DIVIDEND_PERIOD_FORMAT_ERROR);
        }
    }

    /**
     * 校验持股比例。
     *
     * 每个股东比例必须：
     * 1. 不为空
     * 2. 大于 0
     * 3. 小于等于 100
     *
     * 合计比例必须：
     * 1. 小于等于 100
     */
    @VisibleForTesting
    void validateShareRatios(List<StoreShareholderDO> storeShareholders) {
        storeShareholders.forEach(item -> {
            BigDecimal shareRatio = item.getShareRatio();
            if (shareRatio == null
                    || shareRatio.compareTo(BigDecimal.ZERO) <= 0
                    || shareRatio.compareTo(RestaurantMoneyUtils.ONE_HUNDRED) > 0) {
                throw exception(DIVIDEND_DETAIL_SHARE_RATIO_INVALID);
            }
        });

        BigDecimal totalShareRatio = sumShareRatio(storeShareholders);
        if (totalShareRatio.compareTo(RestaurantMoneyUtils.ONE_HUNDRED) > 0) {
            throw exception(DIVIDEND_DETAIL_SHARE_RATIO_INVALID);
        }
    }

    /**
     * 汇总持股比例。
     */
    @VisibleForTesting
    BigDecimal sumShareRatio(List<StoreShareholderDO> storeShareholders) {
        return storeShareholders.stream()
                .map(StoreShareholderDO::getShareRatio)
                .filter(Objects::nonNull)
                .map(RestaurantMoneyUtils::scaleRatio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 校验分红明细合计金额。
     *
     * 如果持股比例合计为 100%，分红明细合计必须等于可分红金额。
     */
    @VisibleForTesting
    void validateDividendAmountTotal(BigDecimal distributableProfit,
                                     List<DividendDetailCalculateRespBO> details) {
        BigDecimal totalDividendAmount = details.stream()
                .map(DividendDetailCalculateRespBO::getDividendAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (RestaurantMoneyUtils.scaleMoney(totalDividendAmount)
                .compareTo(RestaurantMoneyUtils.scaleMoney(distributableProfit)) != 0) {
            throw exception(DIVIDEND_DETAIL_SHARE_RATIO_INVALID);
        }
    }

}