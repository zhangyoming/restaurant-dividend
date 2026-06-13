package cn.iocoder.yudao.module.restaurant.framework.approve.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 餐饮分红审批配置项。
 *
 * <p>用于替代早期代码里固定 userId = 1 的简单写法，让审批人可以来自：
 * 部门负责人、指定角色、指定用户。</p>
 *
 * @author zhangyoming
 */
@Data
@Validated
@ConfigurationProperties(prefix = "yudao.restaurant.dividend-approve")
public class RestaurantDividendApproveProperties {

    /**
     * 是否启用“门店所属部门负责人”为审批人。
     *
     * <p>分红账期里已经保存 deptId，因此可以根据 deptId 查询 system_dept.leader_user_id。
     * 这是最贴近门店管理场景的审批规则。</p>
     */
    private Boolean deptLeaderEnabled = true;

    /**
     * 指定角色编号下的用户作为审批人。
     *
     * <p>适合配置“财务经理”“老板”“分红审批人”等角色。这里使用角色 id，
     * 原因是当前 system 模块对外 API 已支持根据角色 id 查询用户。</p>
     */
    private Set<Long> roleIds = new LinkedHashSet<>();

    /**
     * 指定用户编号作为审批人。
     *
     * <p>适合实习项目、本地演示或小公司固定审批人场景。
     * 注意：这里是配置项，不再写死在 Java 代码里。</p>
     */
    private Set<Long> userIds = new LinkedHashSet<>();

    /**
     * 是否排除提交人自己。
     *
     * <p>true：提交人不能审批自己的分红申请，更符合真实财务内控；
     * false：本地单账号演示更方便。</p>
     */
    private Boolean excludeSubmitUser = false;

}
