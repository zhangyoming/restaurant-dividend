package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.restaurant.enums.DividendPeriodStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 分红账期分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DividendPeriodPageReqVO extends PageParam {

    @Schema(description = "门店编号", example = "1")
    private Long storeId;

    @Schema(description = "部门编号", example = "100")
    private Long deptId;

    @Schema(description = "账期月份，格式 yyyy-MM", example = "2026-06")
    private String periodMonth;

    @Schema(description = "状态，参见 DividendPeriodStatusEnum 枚举", example = "0")
    @InEnum(value = DividendPeriodStatusEnum.class, message = "分红账期状态必须是 {value}")
    private Integer status;

    @Schema(description = "账期开始日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] startDate;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}