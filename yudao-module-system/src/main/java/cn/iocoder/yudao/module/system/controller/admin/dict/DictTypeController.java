package cn.iocoder.yudao.module.system.controller.admin.dict;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.type.DictTypeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.type.DictTypeSimpleRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictTypeDO;
import cn.iocoder.yudao.module.system.service.dict.DictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
//字典类型接口控制器，负责管理字典类型
@Tag(name = "管理后台 - 字典类型")
//它等价于@Controller，@ResponseBody
// 这个类里的接口返回 JSON 数据
@RestController
//接口的统一前缀
@RequestMapping("/system/dict-type")
//开启参数校验见txt
@Validated
public class   DictTypeController {
//    Controller 依赖 Service见txt
    @Resource
    private DictTypeService dictTypeService;

    @PostMapping("/create")
//    接口文档说明 不同版本的 Swagger / OpenAPI 注解
//    OpenAPI 3
//    新 Spring Boot 3 项目
    @Operation(summary = "创建字典类型")
//    权限控制，当前登录用户必须拥有 system:dict:create 权限，才能调用这个接口
    @PreAuthorize("@ss.hasPermission('system:dict:create')")
//    前端传 JSON 数据，后端用 DictTypeSaveReqVO 接收，并进行参数校验
//    CommonResult<Long>，返回给前端的是统一格式
    public CommonResult<Long> createDictType(@Valid @RequestBody DictTypeSaveReqVO createReqVO) {
        Long dictTypeId = dictTypeService.createDictType(createReqVO);
        return success(dictTypeId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改字典类型")
//    调用 ss 这个权限校验对象的 hasPermission 方法
//    判断当前用户有没有 system:dict:create 权限
    @PreAuthorize("@ss.hasPermission('system:dict:update')")
//   @Valid 对 DictTypeSaveReqVO 里面的字段进行参数校验
    public CommonResult<Boolean> updateDictType(@Valid @RequestBody DictTypeSaveReqVO updateReqVO) {
//        具体校验要去 DictTypeSaveReqVO 里看
        dictTypeService.updateDictType(updateReqVO);
//        修改成功就返回 true true为boolean值
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除字典类型")
//    接口文档注解，不是业务逻辑
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
//    Spring 也可以根据参数名绑定请求参数，(@RequestParam("id") Long id
    public CommonResult<Boolean> deleteDictType(Long id) {
        dictTypeService.deleteDictType(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除字典类型")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
//    @RequestParam表示从 URL 参数中取出 id
    public CommonResult<Boolean> deleteDictTypeList(@RequestParam("ids") List<Long> ids) {
        dictTypeService.deleteDictTypeList(ids);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得字典类型的分页列表")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<PageResult<DictTypeRespVO>> pageDictTypes(@Valid DictTypePageReqVO pageReqVO) {
//        DictTypeDO 是数据库实体对象
        PageResult<DictTypeDO> pageResult = dictTypeService.getDictTypePage(pageReqVO);
//        数据库对象转换成前端响应对象DictTypeDO -> DictTypeRespVO
        return success(BeanUtils.toBean(pageResult, DictTypeRespVO.class));
    }

    @Operation(summary = "/查询字典类型详细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @GetMapping(value = "/get") @GetMapping("/get") 基本等价
    @GetMapping(value = "/get")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<DictTypeRespVO> getDictType(@RequestParam("id") Long id) {
        DictTypeDO dictType = dictTypeService.getDictType(id);
        return success(BeanUtils.toBean(dictType, DictTypeRespVO.class));
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
//    两个都能访问同一个方法
    @Operation(summary = "获得全部字典类型列表", description = "包括开启 + 禁用的字典类型，主要用于前端的下拉选项")
    // 无需添加权限认证，因为前端全局都需要
    public CommonResult<List<DictTypeSimpleRespVO>> getSimpleDictTypeList() {
        List<DictTypeDO> list = dictTypeService.getDictTypeList();
        return success(BeanUtils.toBean(list, DictTypeSimpleRespVO.class));
    }

    @Operation(summary = "导出数据类型")
    @GetMapping("/export-excel")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
//    API 访问日志，记录接口访问日志，并标记操作类型为导出操作
    @ApiAccessLog(operateType = EXPORT)
    public void export(HttpServletResponse response, @Valid DictTypePageReqVO exportReqVO) throws IOException {
//        导出时不分页，查询所有符合条件的数据。
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DictTypeDO> list = dictTypeService.getDictTypePage(exportReqVO).getList();
//        把数据写到 HTTP 响应里，让浏览器下载 Excel 文件
        ExcelUtils.write(response, "字典类型.xls", "数据", DictTypeRespVO.class,
                BeanUtils.toBean(list, DictTypeRespVO.class));
    }
}

