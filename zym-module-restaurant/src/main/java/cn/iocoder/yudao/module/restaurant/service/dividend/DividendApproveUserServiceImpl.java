package cn.iocoder.yudao.module.restaurant.service.dividend;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import cn.iocoder.yudao.module.restaurant.framework.approve.config.RestaurantDividendApproveProperties;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分红审批人解析 Service 实现类。
 *
 * <p>审批人来源优先级：</p>
 * <ol>
 *     <li>门店所属部门负责人：period.deptId -> system_dept.leader_user_id</li>
 *     <li>配置角色：yudao.restaurant.dividend-approve.role-ids</li>
 *     <li>配置用户：yudao.restaurant.dividend-approve.user-ids</li>
 * </ol>
 *
 * <p>最后会统一过滤不存在、禁用、为空的用户，并根据配置决定是否排除提交人自己。</p>
 *
 * @author zhangyoming
 */
@Slf4j
@Service
@Validated
public class DividendApproveUserServiceImpl implements DividendApproveUserService {

    @Resource
    private RestaurantDividendApproveProperties approveProperties;

    @Resource
    private DeptApi deptApi;
    @Resource
    private PermissionApi permissionApi;
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public Collection<Long> getApproveUserIds(DividendPeriodDO period, Long submitUserId) {
        if (period == null) {
            return Collections.emptySet();
        }

        LinkedHashSet<Long> userIds = new LinkedHashSet<>();
        addDeptLeaderApproveUser(userIds, period);
        addRoleApproveUsers(userIds);
        addConfiguredApproveUsers(userIds);

        return filterValidApproveUsers(userIds, submitUserId);
    }

    @Override
    public boolean isApproveUser(DividendPeriodDO period, Long submitUserId, Long userId) {
        if (userId == null) {
            return false;
        }
        return getApproveUserIds(period, submitUserId).contains(userId);
    }

    /**
     * 添加门店所属部门负责人。
     */
    private void addDeptLeaderApproveUser(Set<Long> userIds, DividendPeriodDO period) {
        if (!Boolean.TRUE.equals(approveProperties.getDeptLeaderEnabled())) {
            return;
        }
        if (period.getDeptId() == null) {
            return;
        }
        try {
            DeptRespDTO dept = deptApi.getDept(period.getDeptId());
            if (dept == null) {
                return;
            }
            if (!CommonStatusEnum.isEnable(dept.getStatus())) {
                return;
            }
            if (dept.getLeaderUserId() != null) {
                userIds.add(dept.getLeaderUserId());
            }
        } catch (Exception ex) {
            log.warn("[addDeptLeaderApproveUser][deptId({}) 查询部门负责人失败]", period.getDeptId(), ex);
        }
    }

    /**
     * 添加配置角色下的用户。
     */
    private void addRoleApproveUsers(Set<Long> userIds) {
        if (CollUtil.isEmpty(approveProperties.getRoleIds())) {
            return;
        }
        try {
            Set<Long> roleUserIds = permissionApi.getUserRoleIdListByRoleIds(approveProperties.getRoleIds());
            if (CollUtil.isNotEmpty(roleUserIds)) {
                userIds.addAll(roleUserIds);
            }
        } catch (Exception ex) {
            log.warn("[addRoleApproveUsers][roleIds({}) 查询角色用户失败]", approveProperties.getRoleIds(), ex);
        }
    }

    /**
     * 添加配置的固定审批用户。
     */
    private void addConfiguredApproveUsers(Set<Long> userIds) {
        if (CollUtil.isEmpty(approveProperties.getUserIds())) {
            return;
        }
        userIds.addAll(approveProperties.getUserIds());
    }

    /**
     * 过滤无效审批人。
     */
    private Collection<Long> filterValidApproveUsers(Set<Long> userIds, Long submitUserId) {
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptySet();
        }

        userIds.removeIf(userId -> userId == null || userId <= 0);
        if (Boolean.TRUE.equals(approveProperties.getExcludeSubmitUser()) && submitUserId != null) {
            userIds.remove(submitUserId);
        }
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptySet();
        }

        List<AdminUserRespDTO> users = adminUserApi.getUserList(userIds);
        if (CollUtil.isEmpty(users)) {
            return Collections.emptySet();
        }

        Set<Long> validUserIds = users.stream()
                .filter(user -> user != null && user.getId() != null)
                .filter(user -> CommonStatusEnum.isEnable(user.getStatus()))
                .map(AdminUserRespDTO::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        userIds.retainAll(validUserIds);
        return userIds;
    }

}
