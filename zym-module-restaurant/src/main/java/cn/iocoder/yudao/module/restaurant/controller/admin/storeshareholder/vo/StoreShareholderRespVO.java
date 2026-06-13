package cn.iocoder.yudao.module.restaurant.controller.admin.storeshareholder.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.module.system.enums.DictTypeConstants;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 门店股东持股 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StoreShareholderRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("门店编号")
    private Long storeId;

    @Schema(description = "部门编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @ExcelProperty("部门编号")
    private Long deptId;

    @Schema(description = "股东编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @ExcelProperty("股东编号")
    private Long shareholderId;

    @Schema(description = "持股比例", requiredMode = Schema.RequiredMode.REQUIRED, example = "40.00")
    @ExcelProperty("持股比例")
    private BigDecimal shareRatio;

    @Schema(description = "出资金额", example = "100000.00")
    @ExcelProperty("出资金额")
    private BigDecimal investAmount;

    @Schema(description = "入股时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("入股时间")
    private LocalDateTime joinTime;

    @Schema(description = "退出时间")
    @ExcelProperty("退出时间")
    private LocalDateTime exitTime;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(description = "备注", example = "创始股东")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}