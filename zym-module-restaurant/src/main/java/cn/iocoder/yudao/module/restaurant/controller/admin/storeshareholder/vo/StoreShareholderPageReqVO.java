package cn.iocoder.yudao.module.restaurant.controller.admin.storeshareholder.vo;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 门店股东持股分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class StoreShareholderPageReqVO extends PageParam {

    @Schema(description = "门店编号", example = "1")
    private Long storeId;

    @Schema(description = "股东编号", example = "10")
    private Long shareholderId;

    @Schema(description = "部门编号", example = "100")
    private Long deptId;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", example = "0")
    @InEnum(value = CommonStatusEnum.class, message = "持股状态必须是 {value}")
    private Integer status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "入股时间")
    private LocalDateTime[] joinTime;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}