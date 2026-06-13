package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveAuditReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveRecordPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveRecordRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveSubmitReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendApproveRecordDO;
import cn.iocoder.yudao.module.restaurant.service.dividend.DividendApproveService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "管理后台 - 分红发放审批")
@RestController
@RequestMapping("/restaurant/dividend-approve")
@Validated
public class DividendApproveController {

    @Resource
    private DividendApproveService dividendApproveService;

    @PostMapping("/submit")
    @Operation(summary = "提交分红发放审批")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-approve:submit')")
    public CommonResult<Long> submitDividendApprove(@Valid @RequestBody DividendApproveSubmitReqVO submitReqVO) {
        return success(dividendApproveService.submitDividendApprove(submitReqVO));
    }

    @PostMapping("/audit")
    @Operation(summary = "审批分红发放")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-approve:audit')")
    public CommonResult<Boolean> auditDividendApprove(@Valid @RequestBody DividendApproveAuditReqVO auditReqVO) {
        dividendApproveService.auditDividendApprove(auditReqVO);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得分红审批记录分页")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-approve:query')")
    public CommonResult<PageResult<DividendApproveRecordRespVO>> getApproveRecordPage(
            @Valid DividendApproveRecordPageReqVO pageReqVO) {
        PageResult<DividendApproveRecordDO> pageResult = dividendApproveService.getApproveRecordPage(pageReqVO);
        return success(new PageResult<>(
                dividendApproveService.buildApproveRecordRespList(pageResult.getList()),
                pageResult.getTotal()));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出分红审批记录 Excel")
    @PreAuthorize("@ss.hasPermission('restaurant:dividend-approve:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportApproveRecordExcel(HttpServletResponse response,
                                         @Valid DividendApproveRecordPageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DividendApproveRecordDO> list = dividendApproveService.getApproveRecordPage(exportReqVO).getList();

        ExcelUtils.write(response, "分红审批记录.xls", "分红审批记录",
                DividendApproveRecordRespVO.class,
                dividendApproveService.buildApproveRecordRespList(list));
    }

}