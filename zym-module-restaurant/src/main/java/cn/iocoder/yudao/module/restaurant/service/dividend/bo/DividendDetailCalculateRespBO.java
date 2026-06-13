package cn.iocoder.yudao.module.restaurant.service.dividend.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 分红明细计算结果 BO
 *
 * @author zhangyoming
 */
@Data
public class DividendDetailCalculateRespBO {

    /**
     * 门店股东持股关系编号
     */
    private Long storeShareholderId;

    /**
     * 股东编号
     */
    private Long shareholderId;

    /**
     * 股东姓名快照
     */
    private String shareholderName;

    /**
     * 持股比例快照
     */
    private BigDecimal shareRatio;

    /**
     * 可分红金额快照
     */
    private BigDecimal profitAmount;

    /**
     * 股东分红金额
     */
    private BigDecimal dividendAmount;
    /**
     * 尾差金额
     */
    private BigDecimal roundingDiffAmount;

}