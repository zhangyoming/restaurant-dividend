package cn.iocoder.yudao.module.restaurant.controller.admin.shareholder.vo;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 餐饮股东创建/修改 Request VO")
@Data
public class ShareholderSaveReqVO {

    @Schema(description = "股东编号", example = "1024")
    private Long id;

    @Schema(description = "关联系统用户编号", example = "1001")
    private Long userId;

    @Schema(description = "股东姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "股东姓名不能为空")
    @Size(max = 100, message = "股东姓名不能超过 100 个字符")
    private String name;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "手机号不能为空")
    @Size(max = 30, message = "手机号不能超过 30 个字符")
    private String phone;

    @Schema(description = "身份证号", example = "510xxxxxxxxxxxxxxx")
    @Size(max = 30, message = "身份证号不能超过 30 个字符")
    private String idCard;

    @Schema(description = "开户行", example = "中国工商银行成都春熙路支行")
    @Size(max = 100, message = "开户行不能超过 100 个字符")
    private String bankName;

    @Schema(description = "银行卡号", example = "622202xxxxxxxxxxxx")
    @Size(max = 64, message = "银行卡号不能超过 64 个字符")
    private String bankAccount;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "股东状态不能为空")
    @InEnum(value = CommonStatusEnum.class, message = "股东状态必须是 {value}")
    private Integer status;

    @Schema(description = "备注", example = "早期投资人")
    @Size(max = 500, message = "备注不能超过 500 个字符")
    private String remark;

}