package cn.iocoder.yudao.module.restaurant.mq.producer;

import cn.iocoder.yudao.module.restaurant.enums.RestaurantNotifyTypeEnum;
import cn.iocoder.yudao.module.restaurant.mq.RestaurantMqConstants;
import cn.iocoder.yudao.module.restaurant.service.notify.dto.RestaurantNotifyMessageDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 餐饮业务通知 RocketMQ Producer。
 *
 * <p>启用条件：</p>
 * <pre>
 * yudao.restaurant.mq.enabled=true
 * rocketmq.name-server=127.0.0.1:9876
 * rocketmq.producer.group=restaurant_notify_producer_group
 * </pre>
 *
 * <p>职责：只负责把通知消息发送到 RocketMQ。事务后发送、失败降级等编排逻辑在
 * {@code RestaurantNotifyServiceImpl} 中完成。</p>
 *
 * @author zhangyoming
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "yudao.restaurant.mq", name = "enabled", havingValue = "true")
public class RestaurantNotifyMqProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送餐饮通知消息。
     *
     * @param message 通知消息
     * @return true：发送成功；false：发送失败，调用方可降级为直接 WebSocket 推送
     */
    public boolean sendNotifyMessage(RestaurantNotifyMessageDTO message) {
        if (message == null || message.getReceiverUserId() == null) {
            return false;
        }

        String destination = RestaurantMqConstants.TOPIC_RESTAURANT_NOTIFY + ":" + chooseTag(message);
        try {
            rocketMQTemplate.convertAndSend(destination, message);
            log.info("[sendNotifyMessage][发送餐饮通知 MQ 成功 receiverUserId({}) type({}) destination({})]",
                    message.getReceiverUserId(), message.getType(), destination);
            return true;
        } catch (Exception ex) {
            log.warn("[sendNotifyMessage][发送餐饮通知 MQ 失败 receiverUserId({}) type({}) destination({})]",
                    message.getReceiverUserId(), message.getType(), destination, ex);
            return false;
        }
    }

    /**
     * 根据通知类型选择 RocketMQ Tag。
     */
    private String chooseTag(RestaurantNotifyMessageDTO message) {
        String type = message.getType();
        if (type == null) {
            return RestaurantMqConstants.TAG_RESTAURANT_COMMON;
        }

        if (RestaurantNotifyTypeEnum.DIVIDEND_APPROVE_SUBMIT.getType().equals(type)
                || RestaurantNotifyTypeEnum.DIVIDEND_APPROVE_PASS.getType().equals(type)
                || RestaurantNotifyTypeEnum.DIVIDEND_APPROVE_REJECT.getType().equals(type)) {
            return RestaurantMqConstants.TAG_DIVIDEND_APPROVE;
        }
        if (RestaurantNotifyTypeEnum.DIVIDEND_PAID.getType().equals(type)) {
            return RestaurantMqConstants.TAG_DIVIDEND_PAID;
        }
        if (RestaurantNotifyTypeEnum.REVENUE_IMPORT_FINISH.getType().equals(type)
                || RestaurantNotifyTypeEnum.COST_IMPORT_FINISH.getType().equals(type)
                || type.endsWith("IMPORT_FINISH")) {
            return RestaurantMqConstants.TAG_IMPORT_FINISH;
        }
        return RestaurantMqConstants.TAG_RESTAURANT_COMMON;
    }

}
