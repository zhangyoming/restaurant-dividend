package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo.DividendAutoGenerateRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo.DividendPeriodGenerateReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo.DividendPeriodPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo.DividendPeriodRespVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import cn.iocoder.yudao.module.restaurant.service.dividend.DividendAutoGenerateService;
import cn.iocoder.yudao.module.restaurant.service.dividend.DividendPeriodService;
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

@Tag(name = "管理后台 - 分红账期")
@RestController
@RequestMapping("/restaurant/dividend-period")
@Validated
public class DividendPeriodController {

    @Resource
    private DividendPeriodService dividendPeriodService;
    @Resource
    private DividendAutoGenerateService dividendAutoGenerateService;

    @PostMapping("/generate")
    @Operation(summary = "生成分红账期", description = "自动汇总收入成本、生成分红账期，并同步生成分红明细")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-period:generate')")
    public CommonResult<Long> generateDividendPeriod(@Valid @RequestBody DividendPeriodGenerateReqVO generateReqVO) {
        Long periodId = dividendPeriodService.generateDividendPeriod(generateReqVO);
        return success(periodId);
    }

    @PutMapping("/confirm")
    @Operation(summary = "确认分红账期")
    @Parameter(name = "id", description = "分红账期编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-period:confirm')")
    public CommonResult<Boolean> confirmDividendPeriod(@RequestParam("id") Long id) {
        dividendPeriodService.confirmDividendPeriod(id);
        return success(true);
    }

    @PutMapping("/pay")
    @Operation(summary = "发放分红账期")
    @Parameter(name = "id", description = "分红账期编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-period:pay')")
    public CommonResult<Boolean> payDividendPeriod(@RequestParam("id") Long id) {
        dividendPeriodService.payDividendPeriod(id);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "作废分红账期")
    @Parameter(name = "id", description = "分红账期编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-period:cancel')")
    public CommonResult<Boolean> cancelDividendPeriod(@RequestParam("id") Long id) {
        dividendPeriodService.cancelDividendPeriod(id);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除分红账期")
    @Parameter(name = "id", description = "分红账期编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-period:delete')")
    public CommonResult<Boolean> deleteDividendPeriod(@RequestParam("id") Long id) {
        dividendPeriodService.deleteDividendPeriod(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得分红账期")
    @Parameter(name = "id", description = "分红账期编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-period:query')")
    public CommonResult<DividendPeriodRespVO> getDividendPeriod(@RequestParam("id") Long id) {
        DividendPeriodDO dividendPeriod = dividendPeriodService.getDividendPeriod(id);
        return success(BeanUtils.toBean(dividendPeriod, DividendPeriodRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得分红账期分页列表")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-period:query')")
    public CommonResult<PageResult<DividendPeriodRespVO>> getDividendPeriodPage(
            @Valid DividendPeriodPageReqVO pageReqVO) {
        PageResult<DividendPeriodDO> pageResult = dividendPeriodService.getDividendPeriodPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DividendPeriodRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出分红账期 Excel")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-period:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDividendPeriodExcel(HttpServletResponse response,
                                          @Valid DividendPeriodPageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DividendPeriodDO> list = dividendPeriodService.getDividendPeriodPage(exportReqVO).getList();
        ExcelUtils.write(response, "分红账期数据.xls", "分红账期列表", DividendPeriodRespVO.class,
                BeanUtils.toBean(list, DividendPeriodRespVO.class));
    }
    @PostMapping("/auto-generate")
    @Operation(summary = "手动触发自动生成分红", description = "periodMonth 为空时默认生成上个月，格式 yyyy-MM")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-period:generate')")
    public CommonResult<DividendAutoGenerateRespVO> autoGenerateDividendPeriod(
            @RequestParam(value = "periodMonth", required = false) String periodMonth) {
        return success(dividendAutoGenerateService.generateByPeriodMonth(periodMonth));
    }

}