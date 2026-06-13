package cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 我的分红汇总 Response VO")
@Data
public class MyDividendSummaryRespVO {

    @Schema(description = "股东编号", example = "1001")
    private Long shareholderId;

    @Schema(description = "股东姓名", example = "张三")
    private String shareholderName;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "持股门店数量", example = "2")
    private Integer holdingStoreCount;

    @Schema(description = "正常持股门店数量", example = "1")
    private Integer activeHoldingStoreCount;

    @Schema(description = "累计出资金额", example = "100000.00")
    private BigDecimal totalInvestAmount;

    @Schema(description = "累计分红金额", example = "50000.00")
    private BigDecimal totalDividendAmount;

    @Schema(description = "已发放分红金额", example = "30000.00")
    private BigDecimal paidDividendAmount;

    @Schema(description = "待发放分红金额", example = "20000.00")
    private BigDecimal unpaidDividendAmount;

    @Schema(description = "最近分红账期", example = "2026-06")
    private String latestPeriodMonth;

}
