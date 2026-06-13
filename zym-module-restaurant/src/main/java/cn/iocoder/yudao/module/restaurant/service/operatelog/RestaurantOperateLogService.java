package cn.iocoder.yudao.module.restaurant.service.operatelog;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.operatelog.vo.RestaurantOperateLogPageReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.operatelog.RestaurantOperateLogDO;

/**
 * 餐饮业务操作日志 Service 接口
 *
 * @author zhangyoming
 */
public interface RestaurantOperateLogService {

    /**
     * 记录通用业务操作日志
     */
    void createOperateLog(String bizType, Long bizId, Long storeId, Long deptId,
                          Long periodId, String periodMonth, String operateType,
                          Integer beforeStatus, Integer afterStatus, String remark);

    /**
     * 记录分红账期操作日志
     */
    void createDividendPeriodLog(DividendPeriodDO period, String operateType,
                                 Integer beforeStatus, Integer afterStatus, String remark);

    /**
     * 获得操作日志分页
     */
    PageResult<RestaurantOperateLogDO> getOperateLogPage(RestaurantOperateLogPageReqVO pageReqVO);

}