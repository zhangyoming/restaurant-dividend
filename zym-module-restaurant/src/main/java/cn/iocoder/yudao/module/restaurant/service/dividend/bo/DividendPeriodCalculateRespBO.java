package cn.iocoder.yudao.module.restaurant.service.dividend.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 分红账期计算结果 BO
 *
 * @author zhangyoming
 */
@Data
public class DividendPeriodCalculateRespBO {

    /**
     * 门店编号
     */
    private Long storeId;

    /**
     * 账期月份，例如 2026-06
     */
    private String periodMonth;

    /**
     * 账期开始日期
     */
    private LocalDate startDate;

    /**
     * 账期结束日期
     */
    private LocalDate endDate;

    /**
     * 收入总额
     */
    private BigDecimal totalRevenue;

    /**
     * 成本总额
     */
    private BigDecimal totalCost;

    /**
     * 利润金额
     */
    private BigDecimal profitAmount;

    /**
     * 预留金额
     */
    private BigDecimal reserveAmount;

    /**
     * 可分红金额
     */
    private BigDecimal distributableProfit;

}