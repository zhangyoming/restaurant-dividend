package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 分红账期生成 Request VO")
@Data
public class DividendPeriodGenerateReqVO {

    @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "门店编号不能为空")
    private Long storeId;

    @Schema(description = "账期月份，格式 yyyy-MM", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-06")
    @NotBlank(message = "账期月份不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "账期月份格式必须是 yyyy-MM")
    private String periodMonth;

    @Schema(description = "备注", example = "2026年6月分红账期")
    @Size(max = 500, message = "备注不能超过 500 个字符")
    private String remark;

}