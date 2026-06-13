package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.detail.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 股东分红对账单 Response VO
 *
 * @author zhangyoming
 */
@Schema(description = "管理后台 - 股东分红对账单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ShareholderDividendStatementRespVO {

    @Schema(description = "股东编号", example = "10")
    @ExcelProperty("股东编号")
    private Long shareholderId;

    @Schema(description = "股东姓名", example = "张三")
    @ExcelProperty("股东姓名")
    private String shareholderName;

    @Schema(description = "门店编号", example = "1")
    @ExcelProperty("门店编号")
    private Long storeId;

    @Schema(description = "门店名称", example = "春熙路火锅店")
    @ExcelProperty("门店名称")
    private String storeName;

    @Schema(description = "账期月份", example = "2026-06")
    @ExcelProperty("账期月份")
    private String periodMonth;

    @Schema(description = "持股比例", example = "40.00")
    @ExcelProperty("持股比例")
    private BigDecimal shareRatio;

    @Schema(description = "可分红金额", example = "40000.00")
    @ExcelProperty("可分红金额")
    private BigDecimal profitAmount;

    @Schema(description = "股东分红金额", example = "16000.00")
    @ExcelProperty("分红金额")
    private BigDecimal dividendAmount;

    @Schema(description = "尾差金额", example = "0.01")
    @ExcelProperty("尾差金额")
    private BigDecimal roundingDiffAmount;

    @Schema(description = "状态", example = "1")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "状态名称", example = "已发放")
    @ExcelProperty("状态名称")
    private String statusName;

    @Schema(description = "发放时间")
    @ExcelProperty("发放时间")
    private LocalDateTime paidTime;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}