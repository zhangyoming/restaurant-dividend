package cn.iocoder.yudao.module.restaurant.service.notify;

import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;

import java.util.Collection;

/**
 * 餐饮业务实时通知 Service 接口
 *
 * @author zhangyoming
 */
public interface RestaurantNotifyService {

    /**
     * 通知指定用户
     *
     * @param userId 用户编号
     * @param type 通知类型
     * @param title 通知标题
     * @param content 通知内容
     * @param bizId 业务编号
     * @param storeId 门店编号
     * @param periodId 分红账期编号
     * @param periodMonth 账期月份
     */
    void notifyUser(Long userId, String type, String title, String content,
                    Long bizId, Long storeId, Long periodId, String periodMonth);

    /**
     * 批量通知用户
     */
    void notifyUsers(Collection<Long> userIds, String type, String title, String content,
                     Long bizId, Long storeId, Long periodId, String periodMonth);

    /**
     * 分红提交审批通知
     */
    void notifyDividendApproveSubmit(DividendPeriodDO period, Collection<Long> approveUserIds);

    /**
     * 分红审批通过通知
     */
    void notifyDividendApprovePass(DividendPeriodDO period, Long submitUserId);

    /**
     * 分红审批驳回通知
     */
    void notifyDividendApproveReject(DividendPeriodDO period, Long submitUserId, String reason);

    /**
     * 分红发放通知
     */
    void notifyDividendPaid(DividendPeriodDO period, Collection<Long> shareholderUserIds);

    /**
     * 导入结果通知
     */
    void notifyImportFinish(Long userId, String type, String title, String content);

}