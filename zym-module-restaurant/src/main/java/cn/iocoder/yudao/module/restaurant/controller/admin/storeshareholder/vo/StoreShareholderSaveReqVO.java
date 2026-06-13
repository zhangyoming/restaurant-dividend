package cn.iocoder.yudao.module.restaurant.controller.admin.storeshareholder.vo;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 门店股东持股创建/修改 Request VO")
@Data
public class StoreShareholderSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "门店编号不能为空")
    private Long storeId;

    @Schema(description = "股东编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "股东编号不能为空")
    private Long shareholderId;

    @Schema(description = "持股比例，例如 40.00 表示 40%", requiredMode = Schema.RequiredMode.REQUIRED, example = "40.00")
    @NotNull(message = "持股比例不能为空")
    @DecimalMin(value = "0.01", message = "持股比例必须大于 0")
    @DecimalMax(value = "100.00", message = "持股比例不能超过 100")
    @Digits(integer = 3, fraction = 2, message = "持股比例最多 3 位整数、2 位小数")
    private BigDecimal shareRatio;

    @Schema(description = "出资金额", example = "100000.00")
    @DecimalMin(value = "0.00", message = "出资金额不能小于 0")
    @Digits(integer = 16, fraction = 2, message = "出资金额最多 16 位整数、2 位小数")
    private BigDecimal investAmount;

    @Schema(description = "入股时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "入股时间不能为空")
    private LocalDateTime joinTime;

    @Schema(description = "退出时间")
    private LocalDateTime exitTime;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "持股状态不能为空")
    @InEnum(value = CommonStatusEnum.class, message = "持股状态必须是 {value}")
    private Integer status;

    @Schema(description = "备注", example = "创始股东")
    @Size(max = 500, message = "备注不能超过 500 个字符")
    private String remark;

}