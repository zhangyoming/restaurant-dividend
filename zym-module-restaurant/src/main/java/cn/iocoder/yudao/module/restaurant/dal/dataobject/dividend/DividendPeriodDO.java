package cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.restaurant.enums.DividendPeriodStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 分红账期 DO
 *
 * @author zhangyoming
 */
@TableName("restaurant_dividend_period")
@KeySequence("restaurant_dividend_period_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DividendPeriodDO extends BaseDO {

    /**
     * 分红账期编号
     */
    @TableId
    private Long id;

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
     *
     * 第一阶段默认 0，后续分红规则模块可扩展
     */
    private BigDecimal reserveAmount;

    /**
     * 可分红金额
     */
    private BigDecimal distributableProfit;

    /**
     * 状态
     *
     * 枚举 {@link DividendPeriodStatusEnum}
     */
    private Integer status;

    /**
     * 生成时间
     */
    private LocalDateTime generatedTime;

    /**
     * 确认时间
     */
    private LocalDateTime confirmedTime;

    /**
     * 发放时间
     */
    private LocalDateTime paidTime;

    /**
     * 作废时间
     */
    private LocalDateTime canceledTime;

    /**
     * 备注
     */
    private String remark;
    /**
     * 部门编号
     *
     * 冗余门店 deptId，用于门店级数据权限。
     */
    private Long deptId;

}