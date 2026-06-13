package cn.iocoder.yudao.module.restaurant.service.notify;

import cn.iocoder.yudao.module.restaurant.service.notify.dto.RestaurantNotifyMessageDTO;

/**
 * 餐饮 WebSocket 通知发送器
 *
 * 这里单独抽一层，是为了兼容不同 Yudao 版本的 WebSocket 发送实现。
 *
 * @author zhangyoming
 */
public interface RestaurantWebSocketNotifySender {

    /**
     * 发送给指定用户
     *
     * @param userId 用户编号
     * @param message 通知消息
     */
    void sendToUser(Long userId, RestaurantNotifyMessageDTO message);

}