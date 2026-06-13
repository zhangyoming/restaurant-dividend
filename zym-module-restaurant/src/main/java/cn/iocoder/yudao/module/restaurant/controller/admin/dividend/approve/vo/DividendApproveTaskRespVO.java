package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 分红审批待办 Response VO")
@Data
public class DividendApproveTaskRespVO {

    @Schema(description = "Flowable 任务编号", example = "7501")
    private String taskId;

    @Schema(description = "Flowable 流程实例编号", example = "5001")
    private String processInstanceId;

    @Schema(description = "审批记录编号", example = "1001")
    private Long recordId;

    @Schema(description = "分红账期编号", example = "2001")
    private Long periodId;

    @Schema(description = "门店编号", example = "1")
    private Long storeId;

    @Schema(description = "部门编号", example = "100")
    private Long deptId;

    @Schema(description = "账期月份", example = "2026-06")
    private String periodMonth;

    @Schema(description = "提交人编号", example = "1")
    private Long submitUserId;

    @Schema(description = "利润金额")
    private BigDecimal profitAmount;

    @Schema(description = "可分红金额")
    private BigDecimal distributableProfit;

    @Schema(description = "任务名称", example = "分红发放审批")
    private String taskName;

    @Schema(description = "任务创建时间")
    private LocalDateTime taskCreateTime;

}
