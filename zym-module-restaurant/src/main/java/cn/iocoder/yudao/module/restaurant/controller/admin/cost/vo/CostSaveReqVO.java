package cn.iocoder.yudao.module.restaurant.controller.admin.cost.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.restaurant.enums.CostStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 成本支出创建/修改 Request VO")
@Data
public class CostSaveReqVO {

    @Schema(description = "成本支出编号", example = "1024")
    private Long id;

    @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "门店编号不能为空")
    private Long storeId;

    @Schema(description = "成本日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "成本日期不能为空")
    private LocalDate bizDate;

    @Schema(description = "成本类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "material")
    @NotBlank(message = "成本类型不能为空")
    @Size(max = 50, message = "成本类型不能超过 50 个字符")
    private String costType;

    @Schema(description = "成本金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "5000.00")
    @NotNull(message = "成本金额不能为空")
    @DecimalMin(value = "0.01", message = "成本金额必须大于 0")
    @Digits(integer = 16, fraction = 2, message = "成本金额最多 16 位整数、2 位小数")
    private BigDecimal amount;

    @Schema(description = "状态，参见 CostStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "成本状态不能为空")
    @InEnum(value = CostStatusEnum.class, message = "成本状态必须是 {value}")
    private Integer status;

    @Schema(description = "备注", example = "原材料采购支出")
    @Size(max = 500, message = "备注不能超过 500 个字符")
    private String remark;

}