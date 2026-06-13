package cn.iocoder.yudao.module.restaurant.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 门店利润排行 Response VO")
@Data
public class DashboardStoreRankRespVO {

    @Schema(description = "门店编号", example = "1")
    private Long storeId;

    @Schema(description = "门店名称", example = "春熙路火锅店")
    private String storeName;

    @Schema(description = "收入金额", example = "100000.00")
    private BigDecimal revenueAmount;

    @Schema(description = "成本金额", example = "60000.00")
    private BigDecimal costAmount;

    @Schema(description = "利润金额", example = "40000.00")
    private BigDecimal profitAmount;

}