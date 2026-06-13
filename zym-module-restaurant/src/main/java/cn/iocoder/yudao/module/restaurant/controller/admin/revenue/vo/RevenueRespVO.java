package cn.iocoder.yudao.module.restaurant.controller.admin.revenue.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 营业收入 Response VO")
@Data
@ExcelIgnoreUnannotated
public class RevenueRespVO {

    @Schema(description = "营业收入编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("收入编号")
    private Long id;

    @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("门店编号")
    private Long storeId;

    @Schema(description = "部门编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @ExcelProperty("部门编号")
    private Long deptId;

    @Schema(description = "营业日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("营业日期")
    private LocalDate bizDate;

    @Schema(description = "收入来源", requiredMode = Schema.RequiredMode.REQUIRED, example = "meituan")
    @ExcelProperty("收入来源")
    @DictFormat("restaurant_revenue_source")
    private String source;

    @Schema(description = "收入金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000.00")
    @ExcelProperty("收入金额")
    private BigDecimal amount;

    @Schema(description = "状态，参见 RevenueStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "备注", example = "美团外卖收入")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}