package cn.iocoder.yudao.module.system.controller.admin.dict.vo.data;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
//新增或修改字典数据时，前端传给后端的请求对象
@Schema(description = "管理后台 - 字典数据创建/修改 Request VO")
@Data
//@Valid 就是触发这个类里的校验注解。
//如果没有 @Valid，这些：
//@NotBlank
//@NotNull
//@Size
//@InEnum
//就不会自动生效。
public class DictDataSaveReqVO {
//    为什么 id 没有加 @NotNull？见txt
//    新增时：id 不需要前端传 修改时：id 必须传
    @Schema(description = "字典数据编号", example = "1024")
    private Long id;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
//    sort 不能是 null。
//
//    如果前端不传 sort，后端会校验失败，返回：
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(description = "字典标签", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋道")
//    表示字符串不能为空，并且不能全是空格。适合校验 String 类型
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String label;

    @Schema(description = "字典值", requiredMode = Schema.RequiredMode.REQUIRED, example = "iocoder")
    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    private String value;

    @Schema(description = "字典类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "sys_common_sex")
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

    @Schema(description = "状态,见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
//    status 必须传，不能为 null
//    状态必须传，并且只能是合法状态值
//    status 的值必须属于 CommonStatusEnum 允许的值
    @NotNull(message = "状态不能为空")
    @InEnum(value = CommonStatusEnum.class, message = "修改状态必须是 {value}")
    private Integer status;

    @Schema(description = "颜色类型,default、primary、success、info、warning、danger", example = "default")
    private String colorType;

    @Schema(description = "css 样式", example = "btn-visible")
    private String cssClass;

    @Schema(description = "备注", example = "我是一个角色")
    private String remark;

}
