package cn.iocoder.yudao.module.restaurant.controller.admin.shareholder.vo;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 餐饮股东分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ShareholderPageReqVO extends PageParam {

    @Schema(description = "股东姓名，模糊匹配", example = "张三")
    private String name;

    @Schema(description = "手机号，模糊匹配", example = "13800138000")
    private String phone;

    @Schema(description = "关联系统用户编号", example = "1024")
    private Long userId;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", example = "0")
    @InEnum(value = CommonStatusEnum.class, message = "股东状态必须是 {value}")
    private Integer status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}