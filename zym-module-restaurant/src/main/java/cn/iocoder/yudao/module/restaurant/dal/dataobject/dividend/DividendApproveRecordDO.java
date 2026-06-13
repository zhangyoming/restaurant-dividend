package cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 分红发放审批记录 DO
 *
 * @author zhangyoming
 */
@TableName("restaurant_dividend_approve_record")
@KeySequence("restaurant_dividend_approve_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DividendApproveRecordDO extends BaseDO {

    /**
     * 审批记录编号
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
     * 部门编号
     */
    private Long deptId;

    /**
     * 账期月份
     */
    private String periodMonth;

    /**
     * 审批状态
     */
    private Integer approveStatus;

    /**
     * 提交人编号
     */
    private Long submitUserId;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 审批人编号
     */
    private Long approveUserId;

    /**
     * 审批时间
     */
    private LocalDateTime approveTime;

    /**
     * 审批意见
     */
    private String approveReason;

    /**
     * 备注
     */
    private String remark;

}