package cn.iocoder.yudao.module.restaurant.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 经营看板汇总 Response VO")
@Data
public class DashboardSummaryRespVO {

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "收入总额", example = "100000.00")
    private BigDecimal totalRevenue;

    @Schema(description = "成本总额", example = "60000.00")
    private BigDecimal totalCost;

    @Schema(description = "利润金额", example = "40000.00")
    private BigDecimal profitAmount;

    @Schema(description = "可分红金额", example = "40000.00")
    private BigDecimal distributableProfit;

    @Schema(description = "已发放分红金额", example = "30000.00")
    private BigDecimal paidDividendAmount;

    @Schema(description = "待发放分红金额", example = "10000.00")
    private BigDecimal waitPayDividendAmount;

    @Schema(description = "分红账期数量", example = "6")
    private Long periodCount;

}