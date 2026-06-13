package cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.restaurant.enums.DividendDetailStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 分红明细 DO
 *
 * @author zhangyoming
 */
@TableName("restaurant_dividend_detail")
@KeySequence("restaurant_dividend_detail_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DividendDetailDO extends BaseDO {

    /**
     * 分红明细编号
     */
    @TableId
    private Long id;

    /**
     * 分红账期编号
     */
    private Long periodId;

    /**
     * 门店编号
     */
    private Long storeId;

    /**
     * 账期月份，例如 2026-06
     */
    private String periodMonth;

    /**
     * 门店股东持股关系编号
     */
    private Long storeShareholderId;

    /**
     * 股东编号
     */
    private Long shareholderId;

    /**
     * 股东姓名快照
     *
     * 保存快照是为了避免后续股东改名后，历史分红明细展示被影响。
     */
    private String shareholderName;

    /**
     * 持股比例快照
     *
     * 例如：40.00 表示 40%
     */
    private BigDecimal shareRatio;

    /**
     * 可分红金额快照
     */
    private BigDecimal profitAmount;

    /**
     * 股东分红金额
     */
    private BigDecimal dividendAmount;

    /**
     * 尾差金额
     *
     * 持股比例合计 100% 时，最后一个股东承担尾差，保证明细合计等于账期可分红金额。
     */
    private BigDecimal roundingDiffAmount;
    /**
     * 状态
     *
     * 枚举 {@link DividendDetailStatusEnum}
     */
    private Integer status;

    /**
     * 发放时间
     */
    private LocalDateTime paidTime;

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