package cn.iocoder.yudao.module.restaurant.controller.admin.mydividend;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo.MyDividendDetailPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo.MyDividendDetailRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo.MyDividendHoldingRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo.MyDividendSummaryRespVO;
import cn.iocoder.yudao.module.restaurant.service.mydividend.MyDividendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 我的分红")
@RestController
@RequestMapping("/restaurant/my-dividend")
@Validated
public class MyDividendController {

    @Resource
    private MyDividendService myDividendService;

    @GetMapping("/summary")
    @Operation(summary = "获得我的分红汇总")
    @PreAuthorize("@ss.hasPermission('restaurant:my-dividend:query')")
    public CommonResult<MyDividendSummaryRespVO> getSummary() {
        return success(myDividendService.getSummary());
    }

    @GetMapping("/holding-list")
    @Operation(summary = "获得我的持股门店列表")
    @PreAuthorize("@ss.hasPermission('restaurant:my-dividend:query')")
    public CommonResult<List<MyDividendHoldingRespVO>> getHoldingList() {
        return success(myDividendService.getHoldingList());
    }

    @GetMapping("/detail-page")
    @Operation(summary = "获得我的分红明细分页")
    @PreAuthorize("@ss.hasPermission('restaurant:my-dividend:query')")
    public CommonResult<PageResult<MyDividendDetailRespVO>> getDetailPage(@Valid MyDividendDetailPageReqVO pageReqVO) {
        return success(myDividendService.getDetailPage(pageReqVO));
    }

}
