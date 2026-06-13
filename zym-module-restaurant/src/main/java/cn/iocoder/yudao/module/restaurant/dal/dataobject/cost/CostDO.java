package cn.iocoder.yudao.module.restaurant.dal.dataobject.cost;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.restaurant.enums.CostStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 成本支出 DO
 *
 * @author zhangyoming
 */
@TableName("restaurant_cost")
@KeySequence("restaurant_cost_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostDO extends BaseDO {

    /**
     * 成本支出编号
     */
    @TableId
    private Long id;

    /**
     * 门店编号
     */
    private Long storeId;

    /**
     * 成本日期
     */
    private LocalDate bizDate;

    /**
     * 成本类型
     *
     * 字典：restaurant_cost_type
     */
    private String costType;

    /**
     * 成本金额
     */
    private BigDecimal amount;

    /**
     * 状态
     *
     * 枚举 {@link CostStatusEnum}
     */
    private Integer status;

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