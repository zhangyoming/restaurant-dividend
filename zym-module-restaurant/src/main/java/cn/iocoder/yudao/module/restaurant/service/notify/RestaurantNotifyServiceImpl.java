package cn.iocoder.yudao.module.restaurant.service.notify;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import cn.iocoder.yudao.module.restaurant.enums.RestaurantNotifyTypeEnum;
import cn.iocoder.yudao.module.restaurant.mq.producer.RestaurantNotifyMqProducer;
import cn.iocoder.yudao.module.restaurant.service.notify.dto.RestaurantNotifyMessageDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 餐饮业务实时通知 Service 实现类
 *
 * 现在不直接推送 WebSocket，而是发送 MQ，由 MQ Consumer 异步推送。
 *
 * @author zhangyoming
 */
@Slf4j
@Service
@Validated
public class RestaurantNotifyServiceImpl implements RestaurantNotifyService {

    @Resource
    private RestaurantNotifyMqProducer restaurantNotifyMqProducer;

    @Override
    public void notifyUser(Long userId, String type, String title, String content,
                           Long bizId, Long storeId, Long periodId, String periodMonth) {
        if (userId == null) {
            return;
        }

        RestaurantNotifyMessageDTO message = new RestaurantNotifyMessageDTO();
        message.setReceiverUserId(userId);
        message.setType(type);
        message.setTitle(title);
        message.setContent(content);
        message.setBizId(bizId);
        message.setStoreId(storeId);
        message.setPeriodId(periodId);
        message.setPeriodMonth(periodMonth);
        message.setNotifyTime(LocalDateTime.now());

        try {
            restaurantNotifyMqProducer.sendNotifyMessage(message);
        } catch (Exception ex) {
            // MQ 发送失败不能影响主业务
            log.warn("[notifyUser][发送餐饮通知 MQ 失败 userId({}) type({})]", userId, type, ex);
        }
    }

    @Override
    public void notifyUsers(Collection<Long> userIds, String type, String title, String content,
                            Long bizId, Long storeId, Long periodId, String periodMonth) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        userIds.stream()
                .filter(userId -> userId != null)
                .distinct()
                .forEach(userId -> notifyUser(userId, type, title, content,
                        bizId, storeId, periodId, periodMonth));
    }

    @Override
    public void notifyDividendApproveSubmit(DividendPeriodDO period, Collection<Long> approveUserIds) {
        if (period == null) {
            return;
        }
        notifyUsers(approveUserIds,
                RestaurantNotifyTypeEnum.DIVIDEND_APPROVE_SUBMIT.getType(),
                "分红审批待处理",
                "门店账期 " + period.getPeriodMonth() + " 的分红发放申请待审批",
                period.getId(),
                period.getStoreId(),
                period.getId(),
                period.getPeriodMonth());
    }

    @Override
    public void notifyDividendApprovePass(DividendPeriodDO period, Long submitUserId) {
        if (period == null) {
            return;
        }
        notifyUser(submitUserId,
                RestaurantNotifyTypeEnum.DIVIDEND_APPROVE_PASS.getType(),
                "分红审批通过",
                "门店账期 " + period.getPeriodMonth() + " 的分红审批已通过，可以进行发放",
                period.getId(),
                period.getStoreId(),
                period.getId(),
                period.getPeriodMonth());
    }

    @Override
    public void notifyDividendApproveReject(DividendPeriodDO period, Long submitUserId, String reason) {
        if (period == null) {
            return;
        }
        notifyUser(submitUserId,
                RestaurantNotifyTypeEnum.DIVIDEND_APPROVE_REJECT.getType(),
                "分红审批驳回",
                "门店账期 " + period.getPeriodMonth() + " 的分红审批被驳回，原因：" + reason,
                period.getId(),
                period.getStoreId(),
                period.getId(),
                period.getPeriodMonth());
    }

    @Override
    public void notifyDividendPaid(DividendPeriodDO period, Collection<Long> shareholderUserIds) {
        if (period == null) {
            return;
        }
        notifyUsers(shareholderUserIds,
                RestaurantNotifyTypeEnum.DIVIDEND_PAID.getType(),
                "分红已发放",
                "门店账期 " + period.getPeriodMonth() + " 的分红已发放，请查看分红明细",
                period.getId(),
                period.getStoreId(),
                period.getId(),
                period.getPeriodMonth());
    }

    @Override
    public void notifyImportFinish(Long userId, String type, String title, String content) {
        notifyUser(userId, type, title, content,
                null, null, null, null);
    }

}