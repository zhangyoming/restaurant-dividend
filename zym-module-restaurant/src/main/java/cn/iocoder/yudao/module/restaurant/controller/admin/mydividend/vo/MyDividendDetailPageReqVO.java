package cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 我的分红明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MyDividendDetailPageReqVO extends PageParam {

    @Schema(description = "门店编号", example = "10")
    private Long storeId;

    @Schema(description = "账期月份", example = "2026-06")
    private String periodMonth;

    @Schema(description = "状态，参见 DividendDetailStatusEnum", example = "1")
    private Integer status;

}
