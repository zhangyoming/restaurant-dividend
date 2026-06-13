package cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 我的持股门店 Response VO")
@Data
public class MyDividendHoldingRespVO {

    @Schema(description = "门店股东持股关系编号", example = "1001")
    private Long storeShareholderId;

    @Schema(description = "门店编号", example = "10")
    private Long storeId;

    @Schema(description = "门店名称", example = "春熙路火锅店")
    private String storeName;

    @Schema(description = "门店编码", example = "CX-HG-001")
    private String storeCode;

    @Schema(description = "持股比例", example = "40.00")
    private BigDecimal shareRatio;

    @Schema(description = "出资金额", example = "100000.00")
    private BigDecimal investAmount;

    @Schema(description = "入股时间")
    private LocalDateTime joinTime;

    @Schema(description = "退出时间")
    private LocalDateTime exitTime;

    @Schema(description = "状态，0 正常，1 退出/禁用", example = "0")
    private Integer status;

}
