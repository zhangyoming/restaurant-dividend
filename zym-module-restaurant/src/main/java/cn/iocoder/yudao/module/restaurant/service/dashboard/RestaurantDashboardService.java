package cn.iocoder.yudao.module.restaurant.service.dashboard;

import cn.iocoder.yudao.module.restaurant.controller.admin.dashboard.vo.*;

import java.util.List;

/**
 * 经营看板 Service 接口
 *
 * @author zhangyoming
 */
public interface RestaurantDashboardService {

    /**
     * 获得经营汇总
     */
    DashboardSummaryRespVO getSummary(DashboardReqVO reqVO);

    /**
     * 获得收入成本利润趋势
     */
    List<DashboardTrendRespVO> getTrend(DashboardReqVO reqVO);

    /**
     * 获得门店利润排行
     */
    List<DashboardStoreRankRespVO> getStoreProfitRank(DashboardReqVO reqVO);

    /**
     * 获得股东分红排行
     */
    List<DashboardShareholderRankRespVO> getShareholderDividendRank(DashboardReqVO reqVO);

    /**
     * 获得分红账期状态统计
     */
    List<DashboardPeriodStatusRespVO> getPeriodStatusStatistics(DashboardReqVO reqVO);

}