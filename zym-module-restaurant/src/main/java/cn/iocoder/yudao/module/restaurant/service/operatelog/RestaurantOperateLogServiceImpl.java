package cn.iocoder.yudao.module.restaurant.service.operatelog;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.operatelog.vo.RestaurantOperateLogPageReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.operatelog.RestaurantOperateLogDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.operatelog.RestaurantOperateLogMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.module.restaurant.enums.RestaurantOperateBizTypeConstants.DIVIDEND_PERIOD;

/**
 * 餐饮业务操作日志 Service 实现类
 *
 * @author zhangyoming
 */
@Service
@Validated
public class RestaurantOperateLogServiceImpl implements RestaurantOperateLogService {

    @Resource
    private RestaurantOperateLogMapper restaurantOperateLogMapper;

    @Override
    public void createOperateLog(String bizType, Long bizId, Long storeId, Long deptId,
                                 Long periodId, String periodMonth, String operateType,
                                 Integer beforeStatus, Integer afterStatus, String remark) {
        RestaurantOperateLogDO log = RestaurantOperateLogDO.builder()
                .bizType(bizType)
                .bizId(bizId)
                .storeId(storeId)
                .deptId(deptId)
                .periodId(periodId)
                .periodMonth(periodMonth)
                .operateType(operateType)
                .beforeStatus(beforeStatus)
                .afterStatus(afterStatus)
                .operateUserId(getLoginUserIdSafe())
                .operateTime(LocalDateTime.now())
                .remark(remark)
                .build();
        restaurantOperateLogMapper.insert(log);
    }

    @Override
    public void createDividendPeriodLog(DividendPeriodDO period, String operateType,
                                        Integer beforeStatus, Integer afterStatus, String remark) {
        if (period == null) {
            return;
        }
        createOperateLog(DIVIDEND_PERIOD, period.getId(), period.getStoreId(), period.getDeptId(),
                period.getId(), period.getPeriodMonth(), operateType,
                beforeStatus, afterStatus, remark);
    }

    @Override
    public PageResult<RestaurantOperateLogDO> getOperateLogPage(RestaurantOperateLogPageReqVO pageReqVO) {
        return restaurantOperateLogMapper.selectPage(pageReqVO);
    }

    /**
     * 获取当前登录用户编号。
     *
     * 这里做安全兜底，是为了兼容定时任务、系统内部调用等没有登录用户的场景。
     */
    private Long getLoginUserIdSafe() {
        try {
            return SecurityFrameworkUtils.getLoginUserId();
        } catch (Exception ex) {
            return null;
        }
    }

}