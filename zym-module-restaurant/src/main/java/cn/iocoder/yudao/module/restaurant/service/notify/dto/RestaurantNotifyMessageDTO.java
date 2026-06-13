package cn.iocoder.yudao.module.restaurant.service.notify.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 餐饮业务实时通知消息 DTO
 *
 * @author zhangyoming
 */
@Data
public class RestaurantNotifyMessageDTO {

    /**
     * 接收用户编号
     */
    private Long receiverUserId;

    /**
     * 通知类型
     */
    private String type;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 业务编号
     */
    private Long bizId;

    /**
     * 门店编号
     */
    private Long storeId;

    /**
     * 分红账期编号
     */
    private Long periodId;

    /**
     * 账期月份
     */
    private String periodMonth;

    /**
     * 通知时间
     */
    private LocalDateTime notifyTime;

}