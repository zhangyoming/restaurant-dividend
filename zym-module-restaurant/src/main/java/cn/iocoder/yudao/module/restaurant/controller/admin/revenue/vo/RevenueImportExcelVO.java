package cn.iocoder.yudao.module.restaurant.controller.admin.revenue.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 营业收入 Excel 导入 VO
 *
 * @author zhangyoming
 */
@Schema(description = "管理后台 - 营业收入 Excel 导入 VO")
@Data
public class RevenueImportExcelVO {

    @ExcelProperty("门店编码")
    private String storeCode;

    @ExcelProperty("收入日期")
    private LocalDate bizDate;

    @ExcelProperty("收入来源")
    private String source;

    @ExcelProperty("收入金额")
    private BigDecimal amount;

    @ExcelProperty("备注")
    private String remark;

}