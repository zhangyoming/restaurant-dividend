package cn.iocoder.yudao.module.restaurant.controller.admin.shareholder.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.module.system.enums.DictTypeConstants;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 餐饮股东 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ShareholderRespVO {

    @Schema(description = "股东编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("股东编号")
    private Long id;

    @Schema(description = "关联系统用户编号", example = "1001")
    @ExcelProperty("系统用户编号")
    private Long userId;

    @Schema(description = "股东姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("股东姓名")
    private String name;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @ExcelProperty("手机号")
    private String phone;

    @Schema(description = "身份证号", example = "510xxxxxxxxxxxxxxx")
    @ExcelProperty("身份证号")
    private String idCard;

    @Schema(description = "开户行", example = "中国工商银行成都春熙路支行")
    @ExcelProperty("开户行")
    private String bankName;

    @Schema(description = "银行卡号", example = "622202xxxxxxxxxxxx")
    @ExcelProperty("银行卡号")
    private String bankAccount;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(description = "备注", example = "早期投资人")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}