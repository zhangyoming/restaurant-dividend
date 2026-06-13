package cn.iocoder.yudao.module.restaurant.controller.admin.store.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.module.system.enums.DictTypeConstants;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 餐饮门店 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StoreRespVO {

    @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("门店编号")
    private Long id;

    @Schema(description = "门店名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "春熙路火锅店")
    @ExcelProperty("门店名称")
    private String name;

    @Schema(description = "门店编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "CD001")
    @ExcelProperty("门店编码")
    private String code;

    @Schema(description = "关联部门编号", example = "100")
    @ExcelProperty("部门编号")
    private Long deptId;

    @Schema(description = "负责人用户编号", example = "1024")
    @ExcelProperty("负责人编号")
    private Long managerUserId;

    @Schema(description = "门店地址", example = "成都市锦江区春熙路 88 号")
    @ExcelProperty("门店地址")
    private String address;

    @Schema(description = "联系电话", example = "13800138000")
    @ExcelProperty("联系电话")
    private String phone;

    @Schema(description = "开业日期", example = "2026-06-01")
    @ExcelProperty("开业日期")
    private LocalDate openDate;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(description = "备注", example = "直营门店")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}