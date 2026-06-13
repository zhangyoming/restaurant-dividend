package cn.iocoder.yudao.module.restaurant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 餐饮业务操作类型枚举
 *
 * @author zhangyoming
 */
@Getter
@AllArgsConstructor
public enum RestaurantOperateTypeEnum {

    DIVIDEND_GENERATE("DIVIDEND_GENERATE", "生成分红"),
    DIVIDEND_DETAIL_GENERATE("DIVIDEND_DETAIL_GENERATE", "生成分红明细"),
    DIVIDEND_CONFIRM("DIVIDEND_CONFIRM", "确认分红"),
    DIVIDEND_SUBMIT_APPROVE("DIVIDEND_SUBMIT_APPROVE", "提交分红审批"),
    DIVIDEND_APPROVE_PASS("DIVIDEND_APPROVE_PASS", "分红审批通过"),
    DIVIDEND_APPROVE_REJECT("DIVIDEND_APPROVE_REJECT", "分红审批驳回"),
    DIVIDEND_PAY("DIVIDEND_PAY", "发放分红"),
    DIVIDEND_CANCEL("DIVIDEND_CANCEL", "作废分红"),
    DIVIDEND_DELETE("DIVIDEND_DELETE", "删除分红账期"),

    DIVIDEND_AUTO_GENERATE("DIVIDEND_AUTO_GENERATE", "定时自动生成分红"),

    STORE_SHAREHOLDER_EXIT("STORE_SHAREHOLDER_EXIT", "股东退出门店"),

    REVENUE_IMPORT("REVENUE_IMPORT", "导入营业收入"),
    COST_IMPORT("COST_IMPORT", "导入成本支出"),

    EXPORT_DIVIDEND_DETAIL("EXPORT_DIVIDEND_DETAIL", "导出分红明细"),
    EXPORT_SHAREHOLDER_STATEMENT("EXPORT_SHAREHOLDER_STATEMENT", "导出股东对账单");

    /**
     * 类型
     */
    private final String type;

    /**
     * 名称
     */
    private final String name;

}