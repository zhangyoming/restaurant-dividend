package cn.iocoder.yudao.module.restaurant.dal.dataobject.store;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * 餐饮门店 DO
 *
 * @author zhangyoming
 */
@TableName("restaurant_store")
@KeySequence("restaurant_store_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDO extends BaseDO {

    /**
     * 门店编号
     */
    @TableId
    private Long id;

    /**
     * 门店名称
     */
    private String name;

    /**
     * 门店编码
     *
     * 同一租户下唯一，用于系统内部识别门店
     */
    private String code;

    /**
     * 关联部门编号
     *
     * 用于后续实现门店级数据权限
     */
    private Long deptId;

    /**
     * 负责人用户编号
     *
     * 一般是店长、区域负责人等 system_user 用户
     */
    private Long managerUserId;

    /**
     * 门店地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 开业日期
     */
    private LocalDate openDate;

    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}