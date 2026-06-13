package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 分红审批 Request VO")
@Data
public class DividendApproveAuditReqVO {

    @Schema(description = "分红账期编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "分红账期编号不能为空")
    private Long periodId;

    @Schema(description = "是否通过", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "审批结果不能为空")
    private Boolean approved;

    @Schema(description = "审批意见", example = "同意发放")
    private String approveReason;

}