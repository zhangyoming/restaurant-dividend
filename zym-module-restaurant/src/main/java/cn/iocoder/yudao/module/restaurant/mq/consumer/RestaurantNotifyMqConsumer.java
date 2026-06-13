package cn.iocoder.yudao.module.restaurant.mq.consumer;

import cn.iocoder.yudao.module.restaurant.mq.RestaurantMqConstants;
import cn.iocoder.yudao.module.restaurant.service.notify.RestaurantWebSocketNotifySender;
import cn.iocoder.yudao.module.restaurant.service.notify.dto.RestaurantNotifyMessageDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 餐饮业务通知 MQ Consumer
 *
 * @author zhangyoming
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = RestaurantMqConstants.TOPIC_RESTAURANT_NOTIFY,
        consumerGroup = RestaurantMqConstants.CONSUMER_GROUP_RESTAURANT_NOTIFY
)
public class RestaurantNotifyMqConsumer implements RocketMQListener<RestaurantNotifyMessageDTO> {

    @Resource
    private RestaurantWebSocketNotifySender restaurantWebSocketNotifySender;

    @Override
    public void onMessage(RestaurantNotifyMessageDTO message) {
        if (message == null || message.getReceiverUserId() == null) {
            return;
        }

        try {
            restaurantWebSocketNotifySender.sendToUser(message.getReceiverUserId(), message);
            log.info("[onMessage][消费餐饮通知 MQ 成功 receiverUserId({}) type({})]",
                    message.getReceiverUserId(), message.getType());
        } catch (Exception ex) {
            log.warn("[onMessage][消费餐饮通知 MQ 失败 receiverUserId({}) type({})]",
                    message.getReceiverUserId(), message.getType(), ex);
            // 抛出异常，让 RocketMQ 有机会重试
            throw ex;
        }
    }

}