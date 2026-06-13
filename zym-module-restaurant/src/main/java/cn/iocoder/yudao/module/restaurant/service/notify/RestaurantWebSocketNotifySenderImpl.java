package cn.iocoder.yudao.module.restaurant.service.notify;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.websocket.core.sender.WebSocketMessageSender;
import cn.iocoder.yudao.module.restaurant.service.notify.dto.RestaurantNotifyMessageDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 餐饮 WebSocket 通知发送器实现
 *
 * @author zhangyoming
 */
@Component
public class RestaurantWebSocketNotifySenderImpl implements RestaurantWebSocketNotifySender {

    /**
     * 前端监听的消息类型。
     */
    private static final String MESSAGE_TYPE = "restaurant-notify";

    @Resource
    private WebSocketMessageSender webSocketMessageSender;

    @Override
    public void sendToUser(Long userId, RestaurantNotifyMessageDTO message) {
        webSocketMessageSender.sendObject(UserTypeEnum.ADMIN.getValue(), userId, MESSAGE_TYPE, message);
    }

}