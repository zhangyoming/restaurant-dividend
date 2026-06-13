package cn.iocoder.yudao.module.restaurant.controller.admin.revenue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 营业收入汇总 Response VO")
@Data
public class RevenueSummaryRespVO {

    @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long storeId;

    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate startDate;

    @Schema(description = "结束日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate endDate;

    @Schema(description = "收入总金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "100000.00")
    private BigDecimal totalAmount;

}