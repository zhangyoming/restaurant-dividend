package cn.iocoder.yudao.module.system.dal.dataobject.dict;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import net.sf.jsqlparser.expression.operators.relational.Plus;

import java.time.LocalDateTime;

/**
 * 字典类型表
 *
 * @author ruoyi
 */
//这是 MyBatis-Plus 注解
@TableName("system_dict_type")
@KeySequence("system_dict_type_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
//        这几个都是 Lombok 注解：
@Data
//表示生成 equals() 和 hashCode() 时，把父类字段也算进去
@EqualsAndHashCode(callSuper = true)
//表示生成 toString() 时，也把父类字段打印出来
@ToString(callSuper = true)
//可以用建造者模式创建对象
@Builder
@NoArgsConstructor
@AllArgsConstructor
//当前表忽略租户隔离，不按 tenant_id 过滤 表示这个表的数据不受租户过滤影响
@TenantIgnore
public class DictTypeDO extends BaseDO
//  BaseDO 里一般会有      createTime
//        updateTime
//        creator
//        updater
//        deleted
{

    /**
     * 字典主键
     */
//    @TableId 标记主键
    @TableId
    private Long id;
    /**
     * 字典名称
     */
    private String name;
    /**
     * 字典类型
     */
    private String type;
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
     * 删除时间
     */
    private LocalDateTime deletedTime;

}
