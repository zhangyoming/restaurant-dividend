package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 分红审批记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DividendApproveRecordPageReqVO extends PageParam {

    @Schema(description = "分红账期编号", example = "1024")
    private Long periodId;

    @Schema(description = "门店编号", example = "1")
    private Long storeId;

    @Schema(description = "部门编号", example = "100")
    private Long deptId;

    @Schema(description = "账期月份", example = "2026-06")
    private String periodMonth;

    @Schema(description = "审批状态", example = "0")
    private Integer approveStatus;

    @Schema(description = "提交人编号", example = "1")
    private Long submitUserId;

    @Schema(description = "审批人编号", example = "1")
    private Long approveUserId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}