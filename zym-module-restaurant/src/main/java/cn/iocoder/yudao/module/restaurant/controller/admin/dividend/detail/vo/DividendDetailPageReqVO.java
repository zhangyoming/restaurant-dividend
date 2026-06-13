package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.detail.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.restaurant.enums.DividendDetailStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 分红明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DividendDetailPageReqVO extends PageParam {

    @Schema(description = "分红账期编号", example = "1024")
    private Long periodId;

    @Schema(description = "门店编号", example = "1")
    private Long storeId;

    @Schema(description = "部门编号", example = "100")
    private Long deptId;

    @Schema(description = "账期月份，格式 yyyy-MM", example = "2026-06")
    private String periodMonth;

    @Schema(description = "股东编号", example = "10")
    private Long shareholderId;

    @Schema(description = "状态，参见 DividendDetailStatusEnum 枚举", example = "0")
    @InEnum(value = DividendDetailStatusEnum.class, message = "分红明细状态必须是 {value}")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}