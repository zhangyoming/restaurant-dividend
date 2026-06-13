package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.detail.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 分红明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DividendDetailRespVO {

    @Schema(description = "分红明细编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("明细编号")
    private Long id;

    @Schema(description = "分红账期编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @ExcelProperty("账期编号")
    private Long periodId;

    @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("门店编号")
    private Long storeId;

    @Schema(description = "门店名称", example = "春熙路火锅店")
    @ExcelProperty("门店名称")
    private String storeName;

    @Schema(description = "部门编号", example = "100")
    @ExcelProperty("部门编号")
    private Long deptId;

    @Schema(description = "账期月份", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-06")
    @ExcelProperty("账期月份")
    private String periodMonth;

    @Schema(description = "门店股东持股关系编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    @ExcelProperty("持股关系编号")
    private Long storeShareholderId;

    @Schema(description = "股东编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @ExcelProperty("股东编号")
    private Long shareholderId;

    @Schema(description = "股东姓名快照", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("股东姓名")
    private String shareholderName;

    @Schema(description = "持股比例快照", requiredMode = Schema.RequiredMode.REQUIRED, example = "40.00")
    @ExcelProperty("持股比例")
    private BigDecimal shareRatio;

    @Schema(description = "可分红金额快照", requiredMode = Schema.RequiredMode.REQUIRED, example = "40000.00")
    @ExcelProperty("可分红金额")
    private BigDecimal profitAmount;

    @Schema(description = "股东分红金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "16000.00")
    @ExcelProperty("分红金额")
    private BigDecimal dividendAmount;

    @Schema(description = "尾差金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.01")
    @ExcelProperty("尾差金额")
    private BigDecimal roundingDiffAmount;

    @Schema(description = "状态，参见 DividendDetailStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "状态名称", example = "已生成")
    @ExcelProperty("状态名称")
    private String statusName;

    @Schema(description = "发放时间")
    @ExcelProperty("发放时间")
    private LocalDateTime paidTime;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}