package cn.iocoder.yudao.module.restaurant.controller.admin.cost.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 成本支出 Excel 导入 VO
 *
 * @author zhangyoming
 */
@Schema(description = "管理后台 - 成本支出 Excel 导入 VO")
@Data
public class CostImportExcelVO {

    @ExcelProperty("门店编码")
    private String storeCode;

    @ExcelProperty("成本日期")
    private LocalDate bizDate;

    @ExcelProperty("成本类型")
    private String costType;

    @ExcelProperty("成本金额")
    private BigDecimal amount;

    @ExcelProperty("备注")
    private String remark;

}