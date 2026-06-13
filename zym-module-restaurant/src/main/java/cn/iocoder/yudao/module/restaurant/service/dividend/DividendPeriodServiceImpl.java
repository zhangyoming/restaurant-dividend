package cn.iocoder.yudao.module.restaurant.service.dividend;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo.DividendPeriodGenerateReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo.DividendPeriodPageReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendDetailDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.shareholder.ShareholderDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.storeshareholder.StoreShareholderDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendPeriodMapper;
import cn.iocoder.yudao.module.restaurant.enums.DividendPeriodStatusEnum;
import cn.iocoder.yudao.module.restaurant.enums.RestaurantOperateTypeEnum;
import cn.iocoder.yudao.module.restaurant.service.dividend.bo.DividendPeriodCalculateRespBO;
import cn.iocoder.yudao.module.restaurant.service.notify.RestaurantNotifyService;
import cn.iocoder.yudao.module.restaurant.service.operatelog.RestaurantOperateLogService;
import cn.iocoder.yudao.module.restaurant.service.shareholder.ShareholderService;
import cn.iocoder.yudao.module.restaurant.service.store.StoreService;
import cn.iocoder.yudao.module.restaurant.service.storeshareholder.StoreShareholderService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.*;

/**
 * 分红账期 Service 实现类
 *
 * @author zhangyoming
 */
@Service
@Validated
public class DividendPeriodServiceImpl implements DividendPeriodService {

    /**
     * 已经参与分红业务的账期状态。
     *
     * 已生成、已确认、已发放的账期，都说明对应收入/成本已经参与过分红计算。
     * 已作废账期不算有效分红账期。
     */
    private static final List<Integer> ACTIVE_PERIOD_STATUSES = Arrays.asList(
            DividendPeriodStatusEnum.GENERATED.getStatus(),
            DividendPeriodStatusEnum.CONFIRMED.getStatus(),
            DividendPeriodStatusEnum.PAID.getStatus()
    );

    @Resource
    private DividendPeriodMapper dividendPeriodMapper;

    @Resource
    private StoreShareholderService storeShareholderService;

    @Resource
    private DividendDetailService dividendDetailService;

    @Resource
    private DividendCalculateService dividendCalculateService;

    @Resource
    private StoreService storeService;

    @Resource
    private RestaurantOperateLogService restaurantOperateLogService;

    @Resource
    private DividendLockService dividendLockService;

    @Resource
    private RestaurantNotifyService restaurantNotifyService;

    @Resource
    private ShareholderService shareholderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateDividendPeriod(DividendPeriodGenerateReqVO generateReqVO) {
        return dividendLockService.executeWithStorePeriodLock(
                generateReqVO.getStoreId(),
                generateReqVO.getPeriodMonth(),
                () -> doGenerateDividendPeriod(generateReqVO));
    }

    private Long doGenerateDividendPeriod(DividendPeriodGenerateReqVO generateReqVO) {
        Long storeId = generateReqVO.getStoreId();
        String periodMonth = generateReqVO.getPeriodMonth();

        // 1. Service 层幂等校验
        validateDividendPeriodUnique(storeId, periodMonth);

        // 2. 计算分红账期数据
        DividendPeriodCalculateRespBO calculateResult = dividendCalculateService
                .calculateDividendPeriod(storeId, periodMonth);

        // 3. 校验门店是否存在正常持股股东
        validateStoreHasShareholders(storeId);

        // 4. 获取门店 deptId
        StoreDO store = storeService.getStore(storeId);

        // 5. 插入分红账期
        DividendPeriodDO dividendPeriod = DividendPeriodDO.builder()
                .storeId(storeId)
                .deptId(store.getDeptId())
                .periodMonth(periodMonth)
                .startDate(calculateResult.getStartDate())
                .endDate(calculateResult.getEndDate())
                .totalRevenue(calculateResult.getTotalRevenue())
                .totalCost(calculateResult.getTotalCost())
                .profitAmount(calculateResult.getProfitAmount())
                .reserveAmount(calculateResult.getReserveAmount())
                .distributableProfit(calculateResult.getDistributableProfit())
                .status(DividendPeriodStatusEnum.GENERATED.getStatus())
                .generatedTime(LocalDateTime.now())
                .remark(generateReqVO.getRemark())
                .build();

        try {
            dividendPeriodMapper.insert(dividendPeriod);
        } catch (DuplicateKeyException ex) {
            throw exception(DIVIDEND_PERIOD_GENERATING_DUPLICATE);
        }

        // 6. 自动生成分红明细
        dividendDetailService.generateDividendDetails(dividendPeriod.getId());

        // 7. 记录操作日志
        restaurantOperateLogService.createDividendPeriodLog(dividendPeriod,
                RestaurantOperateTypeEnum.DIVIDEND_GENERATE.getType(),
                null,
                dividendPeriod.getStatus(),
                "生成分红账期，并自动生成分红明细");

        return dividendPeriod.getId();
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmDividendPeriod(Long id) {
        DividendPeriodDO dividendPeriod = validateDividendPeriodExists(id);

        if (!DividendPeriodStatusEnum.GENERATED.getStatus().equals(dividendPeriod.getStatus())) {
            throw exception(DIVIDEND_PERIOD_STATUS_NOT_ALLOW_CONFIRM);
        }

        validateDividendDetailsExists(id);

        Integer beforeStatus = dividendPeriod.getStatus();
        Integer afterStatus = DividendPeriodStatusEnum.CONFIRMED.getStatus();

        DividendPeriodDO updateObj = new DividendPeriodDO();
        updateObj.setId(id);
        updateObj.setStatus(afterStatus);
        updateObj.setConfirmedTime(LocalDateTime.now());
        dividendPeriodMapper.updateById(updateObj);

        dividendPeriod.setStatus(afterStatus);
        restaurantOperateLogService.createDividendPeriodLog(dividendPeriod,
                RestaurantOperateTypeEnum.DIVIDEND_CONFIRM.getType(),
                beforeStatus,
                afterStatus,
                "确认分红账期");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payDividendPeriod(Long id) {
        DividendPeriodDO dividendPeriod = validateDividendPeriodExists(id);

        // 审批通过后才允许发放
        if (!DividendPeriodStatusEnum.APPROVED.getStatus().equals(dividendPeriod.getStatus())) {
            throw exception(DIVIDEND_PERIOD_NOT_APPROVED_NOT_ALLOW_PAY);
        }

        validateDividendDetailsExists(id);

        Integer beforeStatus = dividendPeriod.getStatus();
        Integer afterStatus = DividendPeriodStatusEnum.PAID.getStatus();
        LocalDateTime now = LocalDateTime.now();

        DividendPeriodDO updateObj = new DividendPeriodDO();
        updateObj.setId(id);
        updateObj.setStatus(afterStatus);
        updateObj.setPaidTime(now);
        dividendPeriodMapper.updateById(updateObj);

        dividendDetailService.payDividendDetailsByPeriodId(id);

        dividendPeriod.setStatus(afterStatus);
        dividendPeriod.setPaidTime(now);
        restaurantOperateLogService.createDividendPeriodLog(dividendPeriod,
                RestaurantOperateTypeEnum.DIVIDEND_PAY.getType(),
                beforeStatus,
                afterStatus,
                "发放分红账期，并同步发放分红明细");

// WebSocket 通知股东
        notifyShareholdersDividendPaid(dividendPeriod);
    }
    private void notifyShareholdersDividendPaid(DividendPeriodDO period) {
        List<DividendDetailDO> details = dividendDetailService.getDividendDetailListByPeriodId(period.getId());
        if (CollUtil.isEmpty(details)) {
            return;
        }

        List<Long> shareholderIds = details.stream()
                .map(DividendDetailDO::getShareholderId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, ShareholderDO> shareholderMap = shareholderService.getShareholderMap(shareholderIds);

        List<Long> userIds = shareholderMap.values().stream()
                .map(ShareholderDO::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        restaurantNotifyService.notifyDividendPaid(period, userIds);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelDividendPeriod(Long id) {
        DividendPeriodDO dividendPeriod = validateDividendPeriodExists(id);

        if (DividendPeriodStatusEnum.PAID.getStatus().equals(dividendPeriod.getStatus())) {
            throw exception(DIVIDEND_PERIOD_PAID_NOT_ALLOW_CANCEL);
        }
        if (DividendPeriodStatusEnum.CANCELED.getStatus().equals(dividendPeriod.getStatus())
                || DividendPeriodStatusEnum.APPROVING.getStatus().equals(dividendPeriod.getStatus())
                || DividendPeriodStatusEnum.APPROVED.getStatus().equals(dividendPeriod.getStatus())) {
            throw exception(DIVIDEND_PERIOD_STATUS_NOT_ALLOW_CANCEL);
        }

        Integer beforeStatus = dividendPeriod.getStatus();
        Integer afterStatus = DividendPeriodStatusEnum.CANCELED.getStatus();

        DividendPeriodDO updateObj = new DividendPeriodDO();
        updateObj.setId(id);
        updateObj.setStatus(afterStatus);
        updateObj.setCanceledTime(LocalDateTime.now());
        dividendPeriodMapper.updateById(updateObj);

        if (dividendDetailService.getDividendDetailCountByPeriodId(id) > 0) {
            dividendDetailService.cancelDividendDetailsByPeriodId(id);
        }

        dividendPeriod.setStatus(afterStatus);
        restaurantOperateLogService.createDividendPeriodLog(dividendPeriod,
                RestaurantOperateTypeEnum.DIVIDEND_CANCEL.getType(),
                beforeStatus,
                afterStatus,
                "作废分红账期，并同步作废分红明细");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDividendPeriod(Long id) {
        DividendPeriodDO dividendPeriod = validateDividendPeriodExists(id);

        if (DividendPeriodStatusEnum.CONFIRMED.getStatus().equals(dividendPeriod.getStatus())
                || DividendPeriodStatusEnum.PAID.getStatus().equals(dividendPeriod.getStatus())) {
            throw exception(DIVIDEND_PERIOD_CONFIRMED_NOT_ALLOW_DELETE);
        }

        Integer beforeStatus = dividendPeriod.getStatus();

        dividendDetailService.deleteDividendDetailsByPeriodId(id);

        restaurantOperateLogService.createDividendPeriodLog(dividendPeriod,
                RestaurantOperateTypeEnum.DIVIDEND_DELETE.getType(),
                beforeStatus,
                null,
                "删除分红账期，并删除其分红明细");

        dividendPeriodMapper.deleteById(id);
    }

    @Override
    public DividendPeriodDO getDividendPeriod(Long id) {
        return dividendPeriodMapper.selectById(id);
    }

    @Override
    public PageResult<DividendPeriodDO> getDividendPeriodPage(DividendPeriodPageReqVO pageReqVO) {
        return dividendPeriodMapper.selectPage(pageReqVO);
    }

    @Override
    public List<DividendPeriodDO> getDividendPeriodList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return dividendPeriodMapper.selectListByIds(ids);
    }

    @Override
    public void validateRevenueNotInDividendPeriod(Long storeId, LocalDate bizDate) {
        Long count = dividendPeriodMapper.selectCountByStoreIdAndBizDate(storeId, bizDate, ACTIVE_PERIOD_STATUSES);
        if (count != null && count > 0) {
            throw exception(REVENUE_ALREADY_IN_DIVIDEND_PERIOD);
        }
    }

    @Override
    public void validateCostNotInDividendPeriod(Long storeId, LocalDate bizDate) {
        Long count = dividendPeriodMapper.selectCountByStoreIdAndBizDate(storeId, bizDate, ACTIVE_PERIOD_STATUSES);
        if (count != null && count > 0) {
            throw exception(COST_ALREADY_IN_DIVIDEND_PERIOD);
        }
    }

    @Override
    public long getDividendPeriodCountByStoreId(Long storeId) {
        if (storeId == null) {
            return 0;
        }
        Long count = dividendPeriodMapper.selectCountByStoreId(storeId);
        return count == null ? 0 : count;
    }

    @VisibleForTesting
    DividendPeriodDO validateDividendPeriodExists(Long id) {
        if (id == null) {
            throw exception(DIVIDEND_PERIOD_NOT_EXISTS);
        }
        DividendPeriodDO dividendPeriod = dividendPeriodMapper.selectById(id);
        if (dividendPeriod == null) {
            throw exception(DIVIDEND_PERIOD_NOT_EXISTS);
        }
        return dividendPeriod;
    }

    @VisibleForTesting
    void validateDividendPeriodUnique(Long storeId, String periodMonth) {
        DividendPeriodDO dividendPeriod = dividendPeriodMapper.selectByStoreIdAndPeriodMonth(storeId, periodMonth);
        if (dividendPeriod != null) {
            throw exception(DIVIDEND_PERIOD_DUPLICATE);
        }
    }

    @VisibleForTesting
    void validateStoreHasShareholders(Long storeId) {
        List<StoreShareholderDO> list = storeShareholderService.getStoreShareholderListByStoreId(storeId);
        boolean hasEnabledShareholder = CollUtil.isNotEmpty(list) && list.stream()
                .anyMatch(item -> CommonStatusEnum.ENABLE.getStatus().equals(item.getStatus()));
        if (!hasEnabledShareholder) {
            throw exception(DIVIDEND_PERIOD_NO_SHAREHOLDER);
        }
    }

    @VisibleForTesting
    void validateDividendDetailsExists(Long periodId) {
        if (dividendDetailService.getDividendDetailCountByPeriodId(periodId) <= 0) {
            throw exception(DIVIDEND_PERIOD_DETAIL_NOT_EXISTS);
        }
    }

}