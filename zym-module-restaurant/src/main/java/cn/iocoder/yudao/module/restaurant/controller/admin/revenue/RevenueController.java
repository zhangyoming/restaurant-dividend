package cn.iocoder.yudao.module.restaurant.controller.admin.revenue;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.revenue.vo.*;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.revenue.RevenueDO;
import cn.iocoder.yudao.module.restaurant.service.revenue.RevenueService;
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

@Tag(name = "管理后台 - 营业收入")
@RestController
@RequestMapping("/restaurant/revenue")
@Validated
public class RevenueController {

    @Resource
    private RevenueService revenueService;

    @PostMapping("/create")
    @Operation(summary = "创建营业收入")
    @PreAuthorize("@ss.hasPermission('restaurant:revenue:create')")
    public CommonResult<Long> createRevenue(@Valid @RequestBody RevenueSaveReqVO createReqVO) {
        Long revenueId = revenueService.createRevenue(createReqVO);
        return success(revenueId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改营业收入")
    @PreAuthorize("@ss.hasPermission('restaurant:revenue:update')")
    public CommonResult<Boolean> updateRevenue(@Valid @RequestBody RevenueSaveReqVO updateReqVO) {
        revenueService.updateRevenue(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除营业收入")
    @Parameter(name = "id", description = "收入编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:revenue:delete')")
    public CommonResult<Boolean> deleteRevenue(@RequestParam("id") Long id) {
        revenueService.deleteRevenue(id);
        return success(true);
    }

    @PutMapping("/confirm")
    @Operation(summary = "确认营业收入")
    @Parameter(name = "id", description = "收入编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:revenue:confirm')")
    public CommonResult<Boolean> confirmRevenue(@RequestParam("id") Long id) {
        revenueService.confirmRevenue(id);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "作废营业收入")
    @Parameter(name = "id", description = "收入编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:revenue:cancel')")
    public CommonResult<Boolean> cancelRevenue(@RequestParam("id") Long id) {
        revenueService.cancelRevenue(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得营业收入")
    @Parameter(name = "id", description = "收入编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:revenue:query')")
    public CommonResult<RevenueRespVO> getRevenue(@RequestParam("id") Long id) {
        RevenueDO revenue = revenueService.getRevenue(id);
        return success(BeanUtils.toBean(revenue, RevenueRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得营业收入分页列表")
    @PreAuthorize("@ss.hasPermission('restaurant:revenue:query')")
    public CommonResult<PageResult<RevenueRespVO>> getRevenuePage(@Valid RevenuePageReqVO pageReqVO) {
        PageResult<RevenueDO> pageResult = revenueService.getRevenuePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, RevenueRespVO.class));
    }

    @GetMapping("/list-by-store-date-range")
    @Operation(summary = "获得门店指定日期范围营业收入列表")
    @PreAuthorize("@ss.hasPermission('restaurant:revenue:query')")
    public CommonResult<List<RevenueRespVO>> getRevenueListByStoreIdAndDateRange(
            @RequestParam("storeId") Long storeId,
            @RequestParam("startDate") @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY) LocalDate endDate) {
        List<RevenueDO> list = revenueService.getRevenueListByStoreIdAndDateRange(storeId, startDate, endDate);
        return success(BeanUtils.toBean(list, RevenueRespVO.class));
    }

    @GetMapping("/summary")
    @Operation(summary = "汇总门店指定日期范围营业收入")
    @PreAuthorize("@ss.hasPermission('restaurant:revenue:query')")
    public CommonResult<RevenueSummaryRespVO> getRevenueSummary(
            @RequestParam("storeId") Long storeId,
            @RequestParam("startDate") @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY) LocalDate endDate) {
        return success(revenueService.getRevenueSummary(storeId, startDate, endDate));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出营业收入 Excel")
    @PreAuthorize("@ss.hasPermission('restaurant:revenue:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportRevenueExcel(HttpServletResponse response,
                                   @Valid RevenuePageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<RevenueDO> list = revenueService.getRevenuePage(exportReqVO).getList();
        ExcelUtils.write(response, "营业收入数据.xls", "营业收入列表", RevenueRespVO.class,
                BeanUtils.toBean(list, RevenueRespVO.class));
    }
    @PostMapping("/import-excel")
    @Operation(summary = "导入营业收入 Excel")
    @PreAuthorize("@ss.hasPermission('restaurant:revenue:import')")
    public CommonResult<RevenueImportRespVO> importRevenueExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "updateSupport", required = false, defaultValue = "false") Boolean updateSupport)
            throws Exception {
        List<RevenueImportExcelVO> list = ExcelUtils.read(file, RevenueImportExcelVO.class);
        return success(revenueService.importRevenueList(list, updateSupport));
    }
    @GetMapping("/get-import-template")
    @Operation(summary = "获得营业收入导入模板")
    @PreAuthorize("@ss.hasPermission('restaurant:revenue:import')")
    public void importTemplate(HttpServletResponse response) throws IOException {
        List<RevenueImportExcelVO> list = new ArrayList<>();

        RevenueImportExcelVO row = new RevenueImportExcelVO();
        row.setStoreCode("CD001");
        row.setBizDate(java.time.LocalDate.now());
        row.setSource("dine_in");
        row.setAmount(new java.math.BigDecimal("12000.00"));
        row.setRemark("堂食收入");
        list.add(row);

        ExcelUtils.write(response, "营业收入导入模板.xls", "营业收入", RevenueImportExcelVO.class, list);
    }
}