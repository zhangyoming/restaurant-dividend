package cn.iocoder.yudao.module.restaurant.dal.dataobject.operatelog;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 餐饮业务操作日志 DO
 *
 * @author zhangyoming
 */
@TableName("restaurant_operate_log")
@KeySequence("restaurant_operate_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOperateLogDO extends BaseDO {

    /**
     * 操作日志编号
     */
    @TableId
    private Long id;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务编号
     */
    private Long bizId;

    /**
     * 门店编号
     */
    private Long storeId;

    /**
     * 部门编号
     */
    private Long deptId;

    /**
     * 分红账期编号
     */
    private Long periodId;

    /**
     * 账期月份
     */
    private String periodMonth;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 操作前状态
     */
    private Integer beforeStatus;

    /**
     * 操作后状态
     */
    private Integer afterStatus;

    /**
     * 操作人编号
     */
    private Long operateUserId;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

    /**
     * 操作备注
     */
    private String remark;

}