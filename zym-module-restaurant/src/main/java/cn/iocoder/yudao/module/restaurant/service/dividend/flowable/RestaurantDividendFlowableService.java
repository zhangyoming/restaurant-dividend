package cn.iocoder.yudao.module.restaurant.service.dividend.flowable;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveTaskPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveTaskRespVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;

import java.util.Collection;

/**
 * 餐饮分红 Flowable 工作流 Service。
 *
 * @author zhangyoming
 */
public interface RestaurantDividendFlowableService {

    /**
     * 启动分红审批流程。
     *
     * @param period 分红账期
     * @param recordId 业务审批记录编号
     * @param submitUserId 提交人编号
     * @param approveUserIds 审批人编号集合
     * @return 流程信息
     */
    RestaurantDividendApproveProcessInfo startApproveProcess(DividendPeriodDO period,
                                                              Long recordId,
                                                              Long submitUserId,
                                                              Collection<Long> approveUserIds);

    /**
     * 完成分红审批任务。
     *
     * @param periodId 分红账期编号
     * @param approveUserId 审批人编号
     * @param approved 是否通过
     * @param approveReason 审批意见
     * @return 流程信息
     */
    RestaurantDividendApproveProcessInfo completeApproveTask(Long periodId,
                                                              Long approveUserId,
                                                              Boolean approved,
                                                              String approveReason);

    /**
     * 校验当前用户是否存在该账期的可审批任务。
     */
    boolean hasActiveApproveTask(Long periodId, Long approveUserId);

    /**
     * 获得当前登录用户的分红审批待办。
     */
    PageResult<DividendApproveTaskRespVO> getTodoTaskPage(DividendApproveTaskPageReqVO pageReqVO, Long loginUserId);

}
