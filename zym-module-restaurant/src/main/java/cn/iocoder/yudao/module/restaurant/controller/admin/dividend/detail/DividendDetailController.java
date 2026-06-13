package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.detail;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.detail.vo.DividendDetailPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.detail.vo.DividendDetailRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.detail.vo.ShareholderDividendStatementRespVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendDetailDO;
import cn.iocoder.yudao.module.restaurant.service.dividend.DividendDetailService;
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
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 分红明细")
@RestController
@RequestMapping("/restaurant/dividend-detail")
@Validated
public class DividendDetailController {

    @Resource
    private DividendDetailService dividendDetailService;

    @PostMapping("/generate")
    @Operation(summary = "生成分红明细", description = "正常情况下生成分红账期时会自动生成明细，该接口主要用于测试或后续重新生成场景")
    @Parameter(name = "periodId", description = "分红账期编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-detail:generate')")
    public CommonResult<Integer> generateDividendDetails(@RequestParam("periodId") Long periodId) {
        Integer count = dividendDetailService.generateDividendDetails(periodId);
        return success(count);
    }
    @DeleteMapping("/delete")
    @Operation(summary = "删除分红明细")
    @Parameter(name = "id", description = "分红明细编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-detail:delete')")
    public CommonResult<Boolean> deleteDividendDetail(@RequestParam("id") Long id) {
        dividendDetailService.deleteDividendDetail(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得分红明细")
    @Parameter(name = "id", description = "分红明细编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-detail:query')")
    public CommonResult<DividendDetailRespVO> getDividendDetail(@RequestParam("id") Long id) {
        DividendDetailDO detail = dividendDetailService.getDividendDetail(id);
        return success(BeanUtils.toBean(detail, DividendDetailRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得分红明细分页列表")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-detail:query')")
    public CommonResult<PageResult<DividendDetailRespVO>> getDividendDetailPage(
            @Valid DividendDetailPageReqVO pageReqVO) {
        PageResult<DividendDetailDO> pageResult = dividendDetailService.getDividendDetailPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DividendDetailRespVO.class));
    }

    @GetMapping("/list-by-period")
    @Operation(summary = "获得指定账期的分红明细列表")
    @Parameter(name = "periodId", description = "分红账期编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-detail:query')")
    public CommonResult<List<DividendDetailRespVO>> getListByPeriodId(@RequestParam("periodId") Long periodId) {
        List<DividendDetailDO> list = dividendDetailService.getDividendDetailListByPeriodId(periodId);
        return success(BeanUtils.toBean(list, DividendDetailRespVO.class));
    }

    @GetMapping("/list-by-shareholder")
    @Operation(summary = "获得指定股东的分红明细列表")
    @Parameter(name = "shareholderId", description = "股东编号", required = true, example = "10")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-detail:query')")
    public CommonResult<List<DividendDetailRespVO>> getListByShareholderId(
            @RequestParam("shareholderId") Long shareholderId) {
        List<DividendDetailDO> list = dividendDetailService.getDividendDetailListByShareholderId(shareholderId);
        return success(BeanUtils.toBean(list, DividendDetailRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出分红明细 Excel")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-detail:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDividendDetailExcel(HttpServletResponse response,
                                          @Valid DividendDetailPageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DividendDetailDO> list = dividendDetailService.getDividendDetailPage(exportReqVO).getList();

        ExcelUtils.write(response, "分红明细数据.xls", "分红明细列表", DividendDetailRespVO.class,
                dividendDetailService.buildDividendDetailRespList(list));
    }
    @GetMapping("/export-shareholder-statement")
    @Operation(summary = "导出股东分红对账单")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-detail:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportShareholderStatementExcel(HttpServletResponse response,
                                                @Valid DividendDetailPageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DividendDetailDO> list = dividendDetailService.getDividendDetailPage(exportReqVO).getList();

        ExcelUtils.write(response, "股东分红对账单.xls", "股东分红对账单",
                ShareholderDividendStatementRespVO.class,
                dividendDetailService.buildShareholderStatementRespList(list));
    }

}