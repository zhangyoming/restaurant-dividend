package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 分红账期 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DividendPeriodRespVO {

    @Schema(description = "分红账期编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("账期编号")
    private Long id;

    @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("门店编号")
    private Long storeId;

    @Schema(description = "部门编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @ExcelProperty("部门编号")
    private Long deptId;

    @Schema(description = "账期月份", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-06")
    @ExcelProperty("账期月份")
    private String periodMonth;

    @Schema(description = "账期开始日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("开始日期")
    private LocalDate startDate;

    @Schema(description = "账期结束日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("结束日期")
    private LocalDate endDate;

    @Schema(description = "收入总额", requiredMode = Schema.RequiredMode.REQUIRED, example = "100000.00")
    @ExcelProperty("收入总额")
    private BigDecimal totalRevenue;

    @Schema(description = "成本总额", requiredMode = Schema.RequiredMode.REQUIRED, example = "60000.00")
    @ExcelProperty("成本总额")
    private BigDecimal totalCost;

    @Schema(description = "利润金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "40000.00")
    @ExcelProperty("利润金额")
    private BigDecimal profitAmount;

    @Schema(description = "预留金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.00")
    @ExcelProperty("预留金额")
    private BigDecimal reserveAmount;

    @Schema(description = "可分红金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "40000.00")
    @ExcelProperty("可分红金额")
    private BigDecimal distributableProfit;

    @Schema(description = "状态，参见 DividendPeriodStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "生成时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("生成时间")
    private LocalDateTime generatedTime;

    @Schema(description = "确认时间")
    @ExcelProperty("确认时间")
    private LocalDateTime confirmedTime;

    @Schema(description = "发放时间")
    @ExcelProperty("发放时间")
    private LocalDateTime paidTime;

    @Schema(description = "作废时间")
    @ExcelProperty("作废时间")
    private LocalDateTime canceledTime;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}