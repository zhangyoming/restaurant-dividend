package cn.iocoder.yudao.module.restaurant.service.mydividend;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo.MyDividendDetailPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo.MyDividendDetailRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo.MyDividendHoldingRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo.MyDividendSummaryRespVO;

import java.util.List;

/**
 * 我的分红 Service 接口。
 *
 * <p>面向股东个人端：当前登录用户只能查看自己绑定股东身份下的持股和分红记录。</p>
 *
 * @author zhangyoming
 */
public interface MyDividendService {

    /**
     * 获得我的分红汇总。
     *
     * @return 汇总信息
     */
    MyDividendSummaryRespVO getSummary();

    /**
     * 获得我的持股门店列表。
     *
     * @return 持股门店列表
     */
    List<MyDividendHoldingRespVO> getHoldingList();

    /**
     * 获得我的分红明细分页。
     *
     * @param pageReqVO 查询条件
     * @return 分红明细分页
     */
    PageResult<MyDividendDetailRespVO> getDetailPage(MyDividendDetailPageReqVO pageReqVO);

}
