package cn.iocoder.yudao.module.restaurant.controller.admin.revenue.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.restaurant.enums.RevenueStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 营业收入分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class RevenuePageReqVO extends PageParam {

    @Schema(description = "门店编号", example = "1")
    private Long storeId;


    @Schema(description = "部门编号", example = "100")
    private Long deptId;

    @Schema(description = "营业日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] bizDate;

    @Schema(description = "收入来源", example = "meituan")
    private String source;

    @Schema(description = "状态，参见 RevenueStatusEnum 枚举", example = "1")
    @InEnum(value = RevenueStatusEnum.class, message = "收入状态必须是 {value}")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}