package cn.iocoder.yudao.module.restaurant.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 股东分红排行 Response VO")
@Data
public class DashboardShareholderRankRespVO {

    @Schema(description = "股东编号", example = "10")
    private Long shareholderId;

    @Schema(description = "股东姓名", example = "张三")
    private String shareholderName;

    @Schema(description = "分红金额", example = "16000.00")
    private BigDecimal dividendAmount;

    @Schema(description = "分红次数", example = "3")
    private Long dividendCount;

}