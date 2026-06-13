package cn.iocoder.yudao.module.restaurant.service.dividend.flowable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分红审批 Flowable 流程信息。
 *
 * @author zhangyoming
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDividendApproveProcessInfo {

    /**
     * 流程实例编号
     */
    private String processInstanceId;

    /**
     * 当前任务编号
     */
    private String taskId;

}
