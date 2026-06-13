package cn.iocoder.yudao.module.restaurant.service.dividend;

import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import cn.iocoder.yudao.module.restaurant.service.dividend.bo.DividendDetailCalculateRespBO;
import cn.iocoder.yudao.module.restaurant.service.dividend.bo.DividendPeriodCalculateRespBO;

import java.util.List;

/**
 * 分红计算 Service 接口
 *
 * 专门负责分红计算，不负责数据库插入。
 *
 * @author zhangyoming
 */
public interface DividendCalculateService {

    /**
     * 计算分红账期汇总数据
     *
     * @param storeId 门店编号
     * @param periodMonth 账期月份，例如 2026-06
     * @return 分红账期计算结果
     */
    DividendPeriodCalculateRespBO calculateDividendPeriod(Long storeId, String periodMonth);

    /**
     * 计算分红明细数据
     *
     * @param period 分红账期
     * @return 分红明细计算结果列表
     */
    List<DividendDetailCalculateRespBO> calculateDividendDetails(DividendPeriodDO period);

}