package cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 我的分红明细 Response VO")
@Data
public class MyDividendDetailRespVO {

    @Schema(description = "分红明细编号", example = "1001")
    private Long id;

    @Schema(description = "分红账期编号", example = "2001")
    private Long periodId;

    @Schema(description = "门店编号", example = "10")
    private Long storeId;

    @Schema(description = "门店名称", example = "春熙路火锅店")
    private String storeName;

    @Schema(description = "账期月份", example = "2026-06")
    private String periodMonth;

    @Schema(description = "股东编号", example = "100")
    private Long shareholderId;

    @Schema(description = "股东姓名快照", example = "张三")
    private String shareholderName;

    @Schema(description = "持股比例快照", example = "40.00")
    private BigDecimal shareRatio;

    @Schema(description = "可分红金额快照", example = "50000.00")
    private BigDecimal profitAmount;

    @Schema(description = "分红金额", example = "20000.00")
    private BigDecimal dividendAmount;

    @Schema(description = "尾差金额", example = "0.01")
    private BigDecimal roundingDiffAmount;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "发放时间")
    private LocalDateTime paidTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
