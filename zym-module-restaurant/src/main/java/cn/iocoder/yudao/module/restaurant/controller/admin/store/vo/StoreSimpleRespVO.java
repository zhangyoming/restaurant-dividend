package cn.iocoder.yudao.module.restaurant.controller.admin.store.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 餐饮门店精简 Response VO")
@Data
public class StoreSimpleRespVO {

    @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "门店名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "春熙路火锅店")
    private String name;

    @Schema(description = "门店编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "CD001")
    private String code;

}