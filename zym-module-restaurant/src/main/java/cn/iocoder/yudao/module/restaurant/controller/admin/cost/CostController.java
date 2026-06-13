package cn.iocoder.yudao.module.restaurant.controller.admin.cost;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.cost.vo.*;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.cost.CostDO;
import cn.iocoder.yudao.module.restaurant.service.cost.CostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Tag(name = "管理后台 - 成本支出")
@RestController
@RequestMapping("/restaurant/cost")
@Validated
public class CostController {

    @Resource
    private CostService costService;

    @PostMapping("/create")
    @Operation(summary = "创建成本支出")
    @PreAuthorize("@ss.hasPermission('restaurant:cost:create')")
    public CommonResult<Long> createCost(@Valid @RequestBody CostSaveReqVO createReqVO) {
        Long costId = costService.createCost(createReqVO);
        return success(costId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改成本支出")
    @PreAuthorize("@ss.hasPermission('restaurant:cost:update')")
    public CommonResult<Boolean> updateCost(@Valid @RequestBody CostSaveReqVO updateReqVO) {
        costService.updateCost(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除成本支出")
    @Parameter(name = "id", description = "成本支出编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:cost:delete')")
    public CommonResult<Boolean> deleteCost(@RequestParam("id") Long id) {
        costService.deleteCost(id);
        return success(true);
    }

    @PutMapping("/confirm")
    @Operation(summary = "确认成本支出")
    @Parameter(name = "id", description = "成本支出编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:cost:confirm')")
    public CommonResult<Boolean> confirmCost(@RequestParam("id") Long id) {
        costService.confirmCost(id);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "作废成本支出")
    @Parameter(name = "id", description = "成本支出编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:cost:cancel')")
    public CommonResult<Boolean> cancelCost(@RequestParam("id") Long id) {
        costService.cancelCost(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得成本支出")
    @Parameter(name = "id", description = "成本支出编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:cost:query')")
    public CommonResult<CostRespVO> getCost(@RequestParam("id") Long id) {
        CostDO cost = costService.getCost(id);
        return success(BeanUtils.toBean(cost, CostRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得成本支出分页列表")
    @PreAuthorize("@ss.hasPermission('restaurant:cost:query')")
    public CommonResult<PageResult<CostRespVO>> getCostPage(@Valid CostPageReqVO pageReqVO) {
        PageResult<CostDO> pageResult = costService.getCostPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CostRespVO.class));
    }

    @GetMapping("/list-by-store-date-range")
    @Operation(summary = "获得门店指定日期范围成本支出列表")
    @PreAuthorize("@ss.hasPermission('restaurant:cost:query')")
    public CommonResult<List<CostRespVO>> getCostListByStoreIdAndDateRange(
            @RequestParam("storeId") Long storeId,
            @RequestParam("startDate") @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY) LocalDate endDate) {
        List<CostDO> list = costService.getCostListByStoreIdAndDateRange(storeId, startDate, endDate);
        return success(BeanUtils.toBean(list, CostRespVO.class));
    }

    @GetMapping("/summary")
    @Operation(summary = "汇总门店指定日期范围成本支出")
    @PreAuthorize("@ss.hasPermission('restaurant:cost:query')")
    public CommonResult<CostSummaryRespVO> getCostSummary(
            @RequestParam("storeId") Long storeId,
            @RequestParam("startDate") @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY) LocalDate endDate) {
        return success(costService.getCostSummary(storeId, startDate, endDate));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出成本支出 Excel")
    @PreAuthorize("@ss.hasPermission('restaurant:cost:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCostExcel(HttpServletResponse response,
                                @Valid CostPageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CostDO> list = costService.getCostPage(exportReqVO).getList();
        ExcelUtils.write(response, "成本支出数据.xls", "成本支出列表", CostRespVO.class,
                BeanUtils.toBean(list, CostRespVO.class));
    }
    @PostMapping("/import-excel")
    @Operation(summary = "导入成本支出 Excel")
    @PreAuthorize("@ss.hasPermission('restaurant:cost:import')")
    public CommonResult<CostImportRespVO> importCostExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "updateSupport", required = false, defaultValue = "false") Boolean updateSupport)
            throws Exception {
        List<CostImportExcelVO> list = ExcelUtils.read(file, CostImportExcelVO.class);
        return success(costService.importCostList(list, updateSupport));
    }
    @GetMapping("/get-import-template")
    @Operation(summary = "获得成本支出导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        List<CostImportExcelVO> list = new ArrayList<>();

        CostImportExcelVO row = new CostImportExcelVO();
        row.setStoreCode("CD001");
        row.setBizDate(LocalDate.now());
        row.setCostType("material");
        row.setAmount(new java.math.BigDecimal("3000.00"));
        row.setRemark("原材料采购");
        list.add(row);

        ExcelUtils.write(response, "成本支出导入模板.xls", "成本支出", CostImportExcelVO.class, list);
    }

}