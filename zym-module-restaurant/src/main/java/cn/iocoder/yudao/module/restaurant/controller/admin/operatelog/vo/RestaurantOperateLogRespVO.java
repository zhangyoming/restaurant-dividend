package cn.iocoder.yudao.module.restaurant.controller.admin.operatelog.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 餐饮业务操作日志 Response VO")
@Data
@ExcelIgnoreUnannotated
public class RestaurantOperateLogRespVO {

    @Schema(description = "操作日志编号", example = "1024")
    @ExcelProperty("日志编号")
    private Long id;

    @Schema(description = "业务类型", example = "DIVIDEND_PERIOD")
    @ExcelProperty("业务类型")
    private String bizType;

    @Schema(description = "业务编号", example = "1024")
    @ExcelProperty("业务编号")
    private Long bizId;

    @Schema(description = "门店编号", example = "1")
    @ExcelProperty("门店编号")
    private Long storeId;

    @Schema(description = "部门编号", example = "100")
    @ExcelProperty("部门编号")
    private Long deptId;

    @Schema(description = "分红账期编号", example = "1024")
    @ExcelProperty("账期编号")
    private Long periodId;

    @Schema(description = "账期月份", example = "2026-06")
    @ExcelProperty("账期月份")
    private String periodMonth;

    @Schema(description = "操作类型", example = "DIVIDEND_PAY")
    @ExcelProperty("操作类型")
    private String operateType;

    @Schema(description = "操作前状态", example = "1")
    @ExcelProperty("操作前状态")
    private Integer beforeStatus;

    @Schema(description = "操作后状态", example = "2")
    @ExcelProperty("操作后状态")
    private Integer afterStatus;

    @Schema(description = "操作人编号", example = "1")
    @ExcelProperty("操作人编号")
    private Long operateUserId;

    @Schema(description = "操作时间")
    @ExcelProperty("操作时间")
    private LocalDateTime operateTime;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}