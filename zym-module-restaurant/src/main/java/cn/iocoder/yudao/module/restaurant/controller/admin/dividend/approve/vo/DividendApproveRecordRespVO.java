package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 分红审批记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DividendApproveRecordRespVO {

    @Schema(description = "审批记录编号", example = "1024")
    @ExcelProperty("审批记录编号")
    private Long id;

    @Schema(description = "分红账期编号", example = "1024")
    @ExcelProperty("分红账期编号")
    private Long periodId;

    @Schema(description = "门店编号", example = "1")
    @ExcelProperty("门店编号")
    private Long storeId;

    @Schema(description = "部门编号", example = "100")
    @ExcelProperty("部门编号")
    private Long deptId;

    @Schema(description = "账期月份", example = "2026-06")
    @ExcelProperty("账期月份")
    private String periodMonth;

    @Schema(description = "审批状态", example = "0")
    @ExcelProperty("审批状态")
    private Integer approveStatus;

    @Schema(description = "审批状态名称", example = "审批中")
    @ExcelProperty("审批状态名称")
    private String approveStatusName;

    @Schema(description = "提交人编号", example = "1")
    @ExcelProperty("提交人编号")
    private Long submitUserId;

    @Schema(description = "提交时间")
    @ExcelProperty("提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "审批人编号", example = "1")
    @ExcelProperty("审批人编号")
    private Long approveUserId;

    @Schema(description = "审批时间")
    @ExcelProperty("审批时间")
    private LocalDateTime approveTime;

    @Schema(description = "审批意见", example = "同意")
    @ExcelProperty("审批意见")
    private String approveReason;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}