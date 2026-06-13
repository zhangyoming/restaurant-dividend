package cn.iocoder.yudao.module.restaurant.dal.dataobject.shareholder;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 餐饮股东 DO
 *
 * @author zhangyoming
 */
@TableName("restaurant_shareholder")
@KeySequence("restaurant_shareholder_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareholderDO extends BaseDO {

    /**
     * 股东编号
     */
    @TableId
    private Long id;

    /**
     * 关联系统用户编号
     *
     * 如果绑定 system_user，后续股东可以登录系统查看自己的分红记录
     */
    private Long userId;

    /**
     * 股东姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 银行卡号
     */
    private String bankAccount;

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
    /**
     * 部门编号
     *
     * 冗余门店 deptId，用于门店级数据权限。
     */
    private Long deptId;

}