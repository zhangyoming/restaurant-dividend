package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 分红提交审批 Request VO")
@Data
public class DividendApproveSubmitReqVO {

    @Schema(description = "分红账期编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "分红账期编号不能为空")
    private Long periodId;

    @Schema(description = "备注", example = "申请发放 2026-06 分红")
    private String remark;

}