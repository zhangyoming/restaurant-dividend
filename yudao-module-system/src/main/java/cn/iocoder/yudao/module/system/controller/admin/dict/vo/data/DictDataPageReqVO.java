package cn.iocoder.yudao.module.system.controller.admin.dict.vo.data;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Size;
import lombok.Lombok;

//查询字典数据分页列表时，前端传给后端的请求参数
@Schema(description = "管理后台 - 字典类型分页列表 Request VO")
//Lombok 注解 见txt
@Data
//这也是 Lombok 注解 见txt
@EqualsAndHashCode(callSuper = true)
//PageParam 是芋道项目封装的分页参数父类
//当前类继承 PageParam 后，就自动拥有分页参数
public class DictDataPageReqVO extends PageParam {
//    @Schema 是 OpenAPI 3 / Swagger 文档注解
    @Schema(description = "字典标签", example  = "芋道")
//    参数校验注解 见txt
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String label;

    @Schema(description = "字典类型，模糊匹配", example = "sys_common_sex")
    @Size(max = 100, message = "字典类型类型长度不能超过100个字符")
    private String dictType;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "1")
//    项目自定义的校验注解
//status 的值必须在 CommonStatusEnum 这个枚举允许的范围内
    @InEnum(value = CommonStatusEnum.class, message = "修改状态必须是 {value}")
    private Integer status;

}
