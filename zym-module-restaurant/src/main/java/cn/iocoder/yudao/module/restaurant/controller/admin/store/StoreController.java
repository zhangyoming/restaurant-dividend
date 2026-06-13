package cn.iocoder.yudao.module.restaurant.controller.admin.store;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StorePageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StoreRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StoreSaveReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StoreSimpleRespVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.service.store.StoreService;
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

@Tag(name = "管理后台 - 餐饮门店")
@RestController
@RequestMapping("/restaurant/store")
@Validated
public class StoreController {

    @Resource
    private StoreService storeService;

    @PostMapping("/create")
    @Operation(summary = "创建门店")
    @PreAuthorize("@ss.hasPermission('restaurant:store:create')")
    public CommonResult<Long> createStore(@Valid @RequestBody StoreSaveReqVO createReqVO) {
        Long storeId = storeService.createStore(createReqVO);
        return success(storeId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改门店")
    @PreAuthorize("@ss.hasPermission('restaurant:store:update')")
    public CommonResult<Boolean> updateStore(@Valid @RequestBody StoreSaveReqVO updateReqVO) {
        storeService.updateStore(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除门店")
    @Parameter(name = "id", description = "门店编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:store:delete')")
    public CommonResult<Boolean> deleteStore(@RequestParam("id") Long id) {
        storeService.deleteStore(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得门店信息")
    @Parameter(name = "id", description = "门店编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:store:query')")
    public CommonResult<StoreRespVO> getStore(@RequestParam("id") Long id) {
        StoreDO store = storeService.getStore(id);
        return success(BeanUtils.toBean(store, StoreRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得门店分页列表")
    @PreAuthorize("@ss.hasPermission('restaurant:store:query')")
    public CommonResult<PageResult<StoreRespVO>> getStorePage(@Valid StorePageReqVO pageReqVO) {
        PageResult<StoreDO> pageResult = storeService.getStorePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, StoreRespVO.class));
    }

    @GetMapping(value = {"/list-all-simple", "/simple-list"})
    @Operation(summary = "获得门店精简列表", description = "只包含启用的门店，主要用于前端下拉选项")
    public CommonResult<List<StoreSimpleRespVO>> getSimpleStoreList() {
        List<StoreDO> list = storeService.getSimpleStoreList();
        list.sort(Comparator.comparing(StoreDO::getId));
        return success(BeanUtils.toBean(list, StoreSimpleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出门店 Excel")
    @PreAuthorize("@ss.hasPermission('restaurant:store:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportStoreExcel(HttpServletResponse response, @Valid StorePageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<StoreDO> list = storeService.getStorePage(exportReqVO).getList();
        ExcelUtils.write(response, "门店数据.xls", "门店列表", StoreRespVO.class,
                BeanUtils.toBean(list, StoreRespVO.class));
    }

}