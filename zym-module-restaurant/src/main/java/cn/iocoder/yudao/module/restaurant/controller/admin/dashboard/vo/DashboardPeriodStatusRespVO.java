package cn.iocoder.yudao.module.restaurant.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 分红账期状态统计 Response VO")
@Data
public class DashboardPeriodStatusRespVO {

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "状态名称", example = "已生成")
    private String statusName;

    @Schema(description = "数量", example = "5")
    private Long count;

}