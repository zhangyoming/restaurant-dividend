package cn.iocoder.yudao.module.restaurant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 餐饮业务实时通知类型枚举
 *
 * @author zhangyoming
 */
@Getter
@AllArgsConstructor
public enum RestaurantNotifyTypeEnum {

    DIVIDEND_APPROVE_SUBMIT("DIVIDEND_APPROVE_SUBMIT", "分红审批待处理"),
    DIVIDEND_APPROVE_PASS("DIVIDEND_APPROVE_PASS", "分红审批通过"),
    DIVIDEND_APPROVE_REJECT("DIVIDEND_APPROVE_REJECT", "分红审批驳回"),
    DIVIDEND_PAID("DIVIDEND_PAID", "分红已发放"),

    REVENUE_IMPORT_FINISH("REVENUE_IMPORT_FINISH", "营业收入导入完成"),
    COST_IMPORT_FINISH("COST_IMPORT_FINISH", "成本支出导入完成");

    /**
     * 类型
     */
    private final String type;

    /**
     * 名称
     */
    private final String name;

}