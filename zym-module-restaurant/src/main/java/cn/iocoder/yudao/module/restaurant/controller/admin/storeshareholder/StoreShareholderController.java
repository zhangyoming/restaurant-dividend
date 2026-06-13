package cn.iocoder.yudao.module.restaurant.controller.admin.storeshareholder;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.storeshareholder.vo.StoreShareholderPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.storeshareholder.vo.StoreShareholderRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.storeshareholder.vo.StoreShareholderSaveReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.storeshareholder.StoreShareholderDO;
import cn.iocoder.yudao.module.restaurant.service.storeshareholder.StoreShareholderService;
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

@Tag(name = "管理后台 - 门店股东持股")
@RestController
@RequestMapping("/restaurant/store-shareholder")
@Validated
public class StoreShareholderController {

    @Resource
    private StoreShareholderService storeShareholderService;

    @PostMapping("/create")
    @Operation(summary = "创建门店股东持股关系")
    @PreAuthorize("@ss.hasPermission('restaurant:store-shareholder:create')")
    public CommonResult<Long> createStoreShareholder(@Valid @RequestBody StoreShareholderSaveReqVO createReqVO) {
        Long id = storeShareholderService.createStoreShareholder(createReqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "修改门店股东持股关系")
    @PreAuthorize("@ss.hasPermission('restaurant:store-shareholder:update')")
    public CommonResult<Boolean> updateStoreShareholder(@Valid @RequestBody StoreShareholderSaveReqVO updateReqVO) {
        storeShareholderService.updateStoreShareholder(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除门店股东持股关系")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:store-shareholder:delete')")
    public CommonResult<Boolean> deleteStoreShareholder(@RequestParam("id") Long id) {
        storeShareholderService.deleteStoreShareholder(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得门店股东持股关系")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:store-shareholder:query')")
    public CommonResult<StoreShareholderRespVO> getStoreShareholder(@RequestParam("id") Long id) {
        StoreShareholderDO storeShareholder = storeShareholderService.getStoreShareholder(id);
        return success(BeanUtils.toBean(storeShareholder, StoreShareholderRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得门店股东持股分页列表")
    @PreAuthorize("@ss.hasPermission('restaurant:store-shareholder:query')")
    public CommonResult<PageResult<StoreShareholderRespVO>> getStoreShareholderPage(
            @Valid StoreShareholderPageReqVO pageReqVO) {
        PageResult<StoreShareholderDO> pageResult = storeShareholderService.getStoreShareholderPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, StoreShareholderRespVO.class));
    }

    @GetMapping("/list-by-store")
    @Operation(summary = "获得指定门店的股东持股列表")
    @Parameter(name = "storeId", description = "门店编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('restaurant:store-shareholder:query')")
    public CommonResult<List<StoreShareholderRespVO>> getListByStoreId(@RequestParam("storeId") Long storeId) {
        List<StoreShareholderDO> list = storeShareholderService.getStoreShareholderListByStoreId(storeId);
        list.sort(Comparator.comparing(StoreShareholderDO::getId));
        return success(BeanUtils.toBean(list, StoreShareholderRespVO.class));
    }

    @GetMapping("/list-by-shareholder")
    @Operation(summary = "获得指定股东的门店持股列表")
    @Parameter(name = "shareholderId", description = "股东编号", required = true, example = "10")
    @PreAuthorize("@ss.hasPermission('restaurant:store-shareholder:query')")
    public CommonResult<List<StoreShareholderRespVO>> getListByShareholderId(
            @RequestParam("shareholderId") Long shareholderId) {
        List<StoreShareholderDO> list = storeShareholderService.getStoreShareholderListByShareholderId(shareholderId);
        list.sort(Comparator.comparing(StoreShareholderDO::getId));
        return success(BeanUtils.toBean(list, StoreShareholderRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出门店股东持股 Excel")
    @PreAuthorize("@ss.hasPermission('restaurant:store-shareholder:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportStoreShareholderExcel(HttpServletResponse response,
                                            @Valid StoreShareholderPageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<StoreShareholderDO> list = storeShareholderService.getStoreShareholderPage(exportReqVO).getList();
        ExcelUtils.write(response, "门店股东持股数据.xls", "门店股东持股列表", StoreShareholderRespVO.class,
                BeanUtils.toBean(list, StoreShareholderRespVO.class));
    }
    @PutMapping("/exit")
    @Operation(summary = "股东退出门店")
    @Parameter(name = "id", description = "持股关系编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:store-shareholder:update')")
    public CommonResult<Boolean> exitStoreShareholder(@RequestParam("id") Long id) {
        storeShareholderService.exitStoreShareholder(id);
        return success(true);
    }
}