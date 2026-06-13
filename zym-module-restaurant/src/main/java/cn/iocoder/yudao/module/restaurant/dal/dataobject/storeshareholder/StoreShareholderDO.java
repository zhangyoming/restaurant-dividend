package cn.iocoder.yudao.module.restaurant.dal.dataobject.storeshareholder;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 门店股东持股 DO
 *
 * @author zhangyoming
 */
@TableName("restaurant_store_shareholder")
@KeySequence("restaurant_store_shareholder_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreShareholderDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 门店编号
     */
    private Long storeId;

    /**
     * 股东编号
     */
    private Long shareholderId;

    /**
     * 持股比例
     *
     * 例如：40.00 表示 40%
     */
    private BigDecimal shareRatio;

    /**
     * 出资金额
     */
    private BigDecimal investAmount;

    /**
     * 入股时间
     */
    private LocalDateTime joinTime;

    /**
     * 退出时间
     */
    private LocalDateTime exitTime;

    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     * 0 正常，1 退出/禁用
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