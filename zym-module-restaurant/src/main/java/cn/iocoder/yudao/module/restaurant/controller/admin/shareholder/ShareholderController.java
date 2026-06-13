package cn.iocoder.yudao.module.restaurant.controller.admin.shareholder;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.shareholder.vo.ShareholderPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.shareholder.vo.ShareholderRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.shareholder.vo.ShareholderSaveReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.shareholder.vo.ShareholderSimpleRespVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.shareholder.ShareholderDO;
import cn.iocoder.yudao.module.restaurant.service.shareholder.ShareholderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 餐饮股东")
@RestController
@RequestMapping("/restaurant/shareholder")
@Validated
public class ShareholderController {

    @Resource
    private ShareholderService shareholderService;

    @PostMapping("/create")
    @Operation(summary = "创建股东")
    @PreAuthorize("@ss.hasPermission('restaurant:shareholder:create')")
    public CommonResult<Long> createShareholder(@Valid @RequestBody ShareholderSaveReqVO createReqVO) {
        Long shareholderId = shareholderService.createShareholder(createReqVO);
        return success(shareholderId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改股东")
    @PreAuthorize("@ss.hasPermission('restaurant:shareholder:update')")
    public CommonResult<Boolean> updateShareholder(@Valid @RequestBody ShareholderSaveReqVO updateReqVO) {
        shareholderService.updateShareholder(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除股东")
    @Parameter(name = "id", description = "股东编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:shareholder:delete')")
    public CommonResult<Boolean> deleteShareholder(@RequestParam("id") Long id) {
        shareholderService.deleteShareholder(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得股东信息")
    @Parameter(name = "id", description = "股东编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:shareholder:query')")
    public CommonResult<ShareholderRespVO> getShareholder(@RequestParam("id") Long id) {
        ShareholderDO shareholder = shareholderService.getShareholder(id);
        return success(BeanUtils.toBean(shareholder, ShareholderRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得股东分页列表")
    @PreAuthorize("@ss.hasPermission('restaurant:shareholder:query')")
    public CommonResult<PageResult<ShareholderRespVO>> getShareholderPage(@Valid ShareholderPageReqVO pageReqVO) {
        PageResult<ShareholderDO> pageResult = shareholderService.getShareholderPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ShareholderRespVO.class));
    }

    @GetMapping(value = {"/list-all-simple", "/simple-list"})
    @Operation(summary = "获得股东精简列表", description = "只包含启用的股东，主要用于前端下拉选项")
    public CommonResult<List<ShareholderSimpleRespVO>> getSimpleShareholderList() {
        List<ShareholderDO> list = shareholderService.getSimpleShareholderList();
        list.sort(Comparator.comparing(ShareholderDO::getId));
        return success(BeanUtils.toBean(list, ShareholderSimpleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出股东 Excel")
    @PreAuthorize("@ss.hasPermission('restaurant:shareholder:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportShareholderExcel(HttpServletResponse response,
                                       @Valid ShareholderPageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ShareholderDO> list = shareholderService.getShareholderPage(exportReqVO).getList();
        ExcelUtils.write(response, "股东数据.xls", "股东列表", ShareholderRespVO.class,
                BeanUtils.toBean(list, ShareholderRespVO.class));
    }

}