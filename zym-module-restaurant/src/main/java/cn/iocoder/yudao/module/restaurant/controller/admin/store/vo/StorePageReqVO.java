package cn.iocoder.yudao.module.restaurant.controller.admin.store.vo;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 餐饮门店分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class StorePageReqVO extends PageParam {

    @Schema(description = "门店名称，模糊匹配", example = "春熙路火锅店")
    private String name;

    @Schema(description = "门店编码，模糊匹配", example = "CD001")
    private String code;

    @Schema(description = "关联部门编号", example = "100")
    private Long deptId;

    @Schema(description = "负责人用户编号", example = "1024")
    private Long managerUserId;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", example = "0")
    @InEnum(value = CommonStatusEnum.class, message = "门店状态必须是 {value}")
    private Integer status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}