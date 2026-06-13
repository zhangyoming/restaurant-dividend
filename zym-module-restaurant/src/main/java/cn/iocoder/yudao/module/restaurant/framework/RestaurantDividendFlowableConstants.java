package cn.iocoder.yudao.module.restaurant.framework;

/**
 * 餐饮分红 Flowable 工作流常量。
 *
 * @author zhangyoming
 */
public interface RestaurantDividendFlowableConstants {

    /**
     * 分红审批流程定义 Key，对应 resources/processes/restaurant_dividend_approve.bpmn20.xml。
     */
    String PROCESS_KEY_DIVIDEND_APPROVE = "restaurantDividendApproveProcess";

    /**
     * 审批任务节点 Key。
     */
    String TASK_KEY_APPROVE = "approveTask";

    String VAR_PERIOD_ID = "periodId";
    String VAR_RECORD_ID = "recordId";
    String VAR_STORE_ID = "storeId";
    String VAR_DEPT_ID = "deptId";
    String VAR_PERIOD_MONTH = "periodMonth";
    String VAR_SUBMIT_USER_ID = "submitUserId";
    String VAR_APPROVE_USER_IDS = "approveUserIds";
    String VAR_APPROVED = "approved";
    String VAR_APPROVE_USER_ID = "approveUserId";
    String VAR_APPROVE_REASON = "approveReason";

}
