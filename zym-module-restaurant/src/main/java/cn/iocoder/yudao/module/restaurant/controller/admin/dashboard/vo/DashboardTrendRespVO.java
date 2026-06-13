package cn.iocoder.yudao.module.restaurant.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 收入成本利润趋势 Response VO")
@Data
public class DashboardTrendRespVO {

    @Schema(description = "月份", example = "2026-06")
    private String month;

    @Schema(description = "收入金额", example = "100000.00")
    private BigDecimal revenueAmount;

    @Schema(description = "成本金额", example = "60000.00")
    private BigDecimal costAmount;

    @Schema(description = "利润金额", example = "40000.00")
    private BigDecimal profitAmount;

}