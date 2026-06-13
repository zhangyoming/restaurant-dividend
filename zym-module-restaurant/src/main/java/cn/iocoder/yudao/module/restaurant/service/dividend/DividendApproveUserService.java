package cn.iocoder.yudao.module.restaurant.service.dividend;

import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;

import java.util.Collection;

/**
 * 分红审批人解析 Service 接口。
 *
 * <p>把“谁可以审批”从审批主流程中拆出来，避免 Service 里继续写死 userId。</p>
 *
 * @author zhangyoming
 */
public interface DividendApproveUserService {

    /**
     * 获得当前账期的审批人编号集合。
     *
     * @param period 分红账期
     * @param submitUserId 提交人编号，可为空
     * @return 审批人编号集合
     */
    Collection<Long> getApproveUserIds(DividendPeriodDO period, Long submitUserId);

    /**
     * 判断指定用户是否为当前账期审批人。
     *
     * @param period 分红账期
     * @param submitUserId 提交人编号，可为空
     * @param userId 待判断用户编号
     * @return 是否审批人
     */
    boolean isApproveUser(DividendPeriodDO period, Long submitUserId, Long userId);

}
