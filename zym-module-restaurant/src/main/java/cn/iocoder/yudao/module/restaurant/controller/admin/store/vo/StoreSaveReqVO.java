package cn.iocoder.yudao.module.restaurant.controller.admin.store.vo;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 餐饮门店创建/修改 Request VO")
@Data
public class StoreSaveReqVO {

    @Schema(description = "门店编号", example = "1024")
    private Long id;

    @Schema(description = "门店名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "春熙路火锅店")
    @NotBlank(message = "门店名称不能为空")
    @Size(max = 100, message = "门店名称不能超过 100 个字符")
    private String name;

    @Schema(description = "门店编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "CD001")
    @NotBlank(message = "门店编码不能为空")
    @Size(max = 50, message = "门店编码不能超过 50 个字符")
    private String code;

    @Schema(description = "关联部门编号", example = "100")
    private Long deptId;

    @Schema(description = "负责人用户编号", example = "1024")
    private Long managerUserId;

    @Schema(description = "门店地址", example = "成都市锦江区春熙路 88 号")
    @Size(max = 255, message = "门店地址不能超过 255 个字符")
    private String address;

    @Schema(description = "联系电话", example = "13800138000")
    @Size(max = 30, message = "联系电话不能超过 30 个字符")
    private String phone;

    @Schema(description = "开业日期", example = "2026-06-01")
    private LocalDate openDate;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "门店状态不能为空")
    @InEnum(value = CommonStatusEnum.class, message = "门店状态必须是 {value}")
    private Integer status;

    @Schema(description = "备注", example = "直营门店")
    @Size(max = 500, message = "备注不能超过 500 个字符")
    private String remark;

}