package cn.iocoder.yudao.module.restaurant.controller.admin.shareholder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 餐饮股东精简 Response VO")
@Data
public class ShareholderSimpleRespVO {

    @Schema(description = "股东编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "股东姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String name;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    private String phone;

}