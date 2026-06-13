package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 分红审批待办分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DividendApproveTaskPageReqVO extends PageParam {

    @Schema(description = "账期月份", example = "2026-06")
    private String periodMonth;

    @Schema(description = "门店编号", example = "1")
    private Long storeId;

}
