package cn.iocoder.yudao.module.restaurant.controller.admin.dashboard;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.dashboard.vo.*;
import cn.iocoder.yudao.module.restaurant.service.dashboard.RestaurantDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 餐饮经营看板")
@RestController
@RequestMapping("/restaurant/dashboard")
@Validated
public class RestaurantDashboardController {

    @Resource
    private RestaurantDashboardService restaurantDashboardService;

    @GetMapping("/summary")
    @Operation(summary = "获得经营汇总")
    @PreAuthorize("@ss.hasPermission('restaurant:dashboard:query')")
    public CommonResult<DashboardSummaryRespVO> getSummary(@Valid DashboardReqVO reqVO) {
        return success(restaurantDashboardService.getSummary(reqVO));
    }

    @GetMapping("/trend")
    @Operation(summary = "获得收入成本利润趋势")
    @PreAuthorize("@ss.hasPermission('restaurant:dashboard:query')")
    public CommonResult<List<DashboardTrendRespVO>> getTrend(@Valid DashboardReqVO reqVO) {
        return success(restaurantDashboardService.getTrend(reqVO));
    }

    @GetMapping("/store-profit-rank")
    @Operation(summary = "获得门店利润排行")
    @PreAuthorize("@ss.hasPermission('restaurant:dashboard:query')")
    public CommonResult<List<DashboardStoreRankRespVO>> getStoreProfitRank(@Valid DashboardReqVO reqVO) {
        return success(restaurantDashboardService.getStoreProfitRank(reqVO));
    }

    @GetMapping("/shareholder-dividend-rank")
    @Operation(summary = "获得股东分红排行")
    @PreAuthorize("@ss.hasPermission('restaurant:dashboard:query')")
    public CommonResult<List<DashboardShareholderRankRespVO>> getShareholderDividendRank(@Valid DashboardReqVO reqVO) {
        return success(restaurantDashboardService.getShareholderDividendRank(reqVO));
    }

    @GetMapping("/period-status-statistics")
    @Operation(summary = "获得分红账期状态统计")
    @PreAuthorize("@ss.hasPermission('restaurant:dashboard:query')")
    public CommonResult<List<DashboardPeriodStatusRespVO>> getPeriodStatusStatistics(@Valid DashboardReqVO reqVO) {
        return success(restaurantDashboardService.getPeriodStatusStatistics(reqVO));
    }

}