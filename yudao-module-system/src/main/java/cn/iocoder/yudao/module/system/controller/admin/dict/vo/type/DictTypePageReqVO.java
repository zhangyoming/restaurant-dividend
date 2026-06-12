package cn.iocoder.yudao.module.system.controller.admin.dict.vo.type;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;
//查询字典类型分页列表时用
//字典类型分页查询的请求参数对象
@Schema(description = "管理后台 - 字典类型分页列表 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictTypePageReqVO extends PageParam {

    @Schema(description = "字典类型名称，模糊匹配", example = "芋道")
    private String name;

    @Schema(description = "字典类型，模糊匹配", example = "sys_common_sex")
    @Size(max = 100, message = "字典类型类型长度不能超过100个字符")
    private String type;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "1")
    private Integer status;
//    告诉 Spring：前端传来的字符串时间，要按照指定格式转换成 LocalDateTime
//    常量代表 yyyy-MM-dd HH:mm:ss 全项目统一时间格式
//    以后想统一修改时间格式会很麻烦。
//   以项目把它封装成常量。
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "创建时间")
//    它是数组
//    这个字段表示创建时间范围
//    createTime[0] = 开始时间
//    createTime[1] = 结束时间
    private LocalDateTime[] createTime;

}
