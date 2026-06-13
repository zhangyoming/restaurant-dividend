package cn.iocoder.yudao.module.restaurant.service.dividend;

import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo.DividendAutoGenerateRespVO;

/**
 * 分红自动生成 Service 接口
 *
 * @author zhangyoming
 */
public interface DividendAutoGenerateService {

    /**
     * 自动生成上个月分红
     *
     * @return 自动生成结果
     */
    DividendAutoGenerateRespVO generateLastMonth();

    /**
     * 自动生成指定账期月份分红
     *
     * @param periodMonth 账期月份，格式 yyyy-MM；为空时默认上个月
     * @return 自动生成结果
     */
    DividendAutoGenerateRespVO generateByPeriodMonth(String periodMonth);

}