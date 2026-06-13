package cn.iocoder.yudao.module.restaurant.service.dividend;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo.DividendAutoGenerateRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo.DividendPeriodGenerateReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendPeriodMapper;
import cn.iocoder.yudao.module.restaurant.enums.RestaurantOperateBizTypeConstants;
import cn.iocoder.yudao.module.restaurant.enums.RestaurantOperateTypeEnum;
import cn.iocoder.yudao.module.restaurant.service.operatelog.RestaurantOperateLogService;
import cn.iocoder.yudao.module.restaurant.service.store.StoreService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.DIVIDEND_PERIOD_FORMAT_ERROR;

/**
 * 分红自动生成 Service 实现类
 *
 * @author zhangyoming
 */
@Service
@Validated
public class DividendAutoGenerateServiceImpl implements DividendAutoGenerateService {

    @Resource
    private StoreService storeService;

    @Resource
    private DividendPeriodService dividendPeriodService;

    @Resource
    private DividendPeriodMapper dividendPeriodMapper;

    @Resource
    private RestaurantOperateLogService restaurantOperateLogService;

    @Resource
    private DividendLockService dividendLockService;

    @Override
    @DataPermission(enable = false)
    public DividendAutoGenerateRespVO generateLastMonth() {
        return generateByPeriodMonth(null);
    }

    @Override
    @DataPermission(enable = false)
    public DividendAutoGenerateRespVO generateByPeriodMonth(String periodMonth) {
        String targetPeriodMonth = getTargetPeriodMonth(periodMonth);
        return dividendLockService.executeWithAutoGenerateLock(targetPeriodMonth,
                () -> doGenerateByPeriodMonth(targetPeriodMonth));
    }

    private DividendAutoGenerateRespVO doGenerateByPeriodMonth(String targetPeriodMonth) {
        List<StoreDO> stores = storeService.getStoreListByStatus(CommonStatusEnum.ENABLE.getStatus());

        DividendAutoGenerateRespVO respVO = new DividendAutoGenerateRespVO();
        respVO.setPeriodMonth(targetPeriodMonth);
        respVO.setTotalStoreCount(CollUtil.isEmpty(stores) ? 0 : stores.size());

        if (CollUtil.isEmpty(stores)) {
            respVO.setSuccessCount(0);
            respVO.setSkipCount(0);
            respVO.setFailureCount(0);
            createAutoGenerateLog(respVO);
            return respVO;
        }

        for (StoreDO store : stores) {
            String storeKey = buildStoreKey(store);

            try {
                // 1. 幂等校验：已经生成过则跳过
                DividendPeriodDO existsPeriod = dividendPeriodMapper.selectByStoreIdAndPeriodMonth(
                        store.getId(), targetPeriodMonth);
                if (existsPeriod != null) {
                    respVO.getSkipStores().put(storeKey, "该门店该账期已经生成过分红账期");
                    continue;
                }

                // 2. 调用现有分红生成主流程
                // 注意：generateDividendPeriod 内部还有单门店分布式锁
                DividendPeriodGenerateReqVO generateReqVO = new DividendPeriodGenerateReqVO();
                generateReqVO.setStoreId(store.getId());
                generateReqVO.setPeriodMonth(targetPeriodMonth);
                generateReqVO.setRemark("定时任务自动生成 " + targetPeriodMonth + " 分红");

                Long periodId = dividendPeriodService.generateDividendPeriod(generateReqVO);

                respVO.getSuccessStores().add(storeKey + "，账期编号：" + periodId);
            } catch (Exception ex) {
                respVO.getFailureStores().put(storeKey, ex.getMessage());
            }
        }

        respVO.setSuccessCount(respVO.getSuccessStores().size());
        respVO.setSkipCount(respVO.getSkipStores().size());
        respVO.setFailureCount(respVO.getFailureStores().size());

        createAutoGenerateLog(respVO);
        return respVO;
    }

    private String getTargetPeriodMonth(String periodMonth) {
        if (StrUtil.isBlank(periodMonth)) {
            return YearMonth.now().minusMonths(1).toString();
        }
        try {
            return YearMonth.parse(periodMonth).toString();
        } catch (DateTimeParseException ex) {
            throw exception(DIVIDEND_PERIOD_FORMAT_ERROR);
        }
    }

    private String buildStoreKey(StoreDO store) {
        if (store == null) {
            return "未知门店";
        }
        return "门店ID：" + store.getId() + "，门店名称：" + store.getName();
    }

    private void createAutoGenerateLog(DividendAutoGenerateRespVO respVO) {
        restaurantOperateLogService.createOperateLog(
                RestaurantOperateBizTypeConstants.DIVIDEND_PERIOD,
                null,
                null,
                null,
                null,
                respVO.getPeriodMonth(),
                RestaurantOperateTypeEnum.DIVIDEND_AUTO_GENERATE.getType(),
                null,
                null,
                "定时自动生成分红：账期 " + respVO.getPeriodMonth()
                        + "，门店总数 " + respVO.getTotalStoreCount()
                        + "，成功 " + respVO.getSuccessCount()
                        + "，跳过 " + respVO.getSkipCount()
                        + "，失败 " + respVO.getFailureCount()
        );
    }

}