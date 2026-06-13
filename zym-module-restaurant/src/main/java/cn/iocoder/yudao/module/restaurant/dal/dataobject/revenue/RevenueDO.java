package cn.iocoder.yudao.module.restaurant.dal.dataobject.revenue;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.restaurant.enums.RevenueStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 营业收入 DO
 *
 * @author zhangyoming
 */
@TableName("restaurant_revenue")
@KeySequence("restaurant_revenue_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueDO extends BaseDO {

    /**
     * 营业收入编号
     */
    @TableId
    private Long id;

    /**
     * 门店编号
     */
    private Long storeId;

    /**
     * 营业日期
     */
    private LocalDate bizDate;

    /**
     * 收入来源
     *
     * 字典：restaurant_revenue_source
     */
    private String source;

    /**
     * 收入金额
     */
    private BigDecimal amount;

    /**
     * 状态
     *
     * 枚举 {@link RevenueStatusEnum}
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