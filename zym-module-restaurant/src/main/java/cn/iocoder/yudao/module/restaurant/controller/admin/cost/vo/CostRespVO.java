package cn.iocoder.yudao.module.restaurant.controller.admin.cost.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 成本支出 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CostRespVO {

    @Schema(description = "成本支出编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("成本编号")
    private Long id;

    @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("门店编号")
    private Long storeId;

    @Schema(description = "部门编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @ExcelProperty("部门编号")
    private Long deptId;

    @Schema(description = "成本日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("成本日期")
    private LocalDate bizDate;

    @Schema(description = "成本类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "material")
    @ExcelProperty(value = "成本类型", converter = DictConvert.class)
    @DictFormat("restaurant_cost_type")
    private String costType;

    @Schema(description = "成本金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "5000.00")
    @ExcelProperty("成本金额")
    private BigDecimal amount;

    @Schema(description = "状态，参见 CostStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "备注", example = "原材料采购支出")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}