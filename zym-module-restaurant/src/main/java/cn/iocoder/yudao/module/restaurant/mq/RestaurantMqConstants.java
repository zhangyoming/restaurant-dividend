package cn.iocoder.yudao.module.restaurant.mq;

/**
 * 餐饮模块 MQ 常量
 *
 * @author zhangyoming
 */
public interface RestaurantMqConstants {

    /**
     * 餐饮业务通知 Topic
     */
    String TOPIC_RESTAURANT_NOTIFY = "restaurant_notify_topic";

    /**
     * 餐饮业务通知消费者组
     */
    String CONSUMER_GROUP_RESTAURANT_NOTIFY = "restaurant_notify_consumer_group";

    /**
     * 分红审批相关 Tag
     */
    String TAG_DIVIDEND_APPROVE = "dividend_approve";

    /**
     * 分红发放相关 Tag
     */
    String TAG_DIVIDEND_PAID = "dividend_paid";

    /**
     * Excel 导入相关 Tag
     */
    String TAG_IMPORT_FINISH = "import_finish";

}