package cn.iocoder.yudao.module.restaurant.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 餐饮模块金额工具类
 *
 * 统一处理金额精度，避免各个 Service 里散落 setScale、divide 等逻辑。
 *
 * @author zhangyoming
 */
public class RestaurantMoneyUtils {

    /**
     * 金额小数位
     */
    public static final int MONEY_SCALE = 2;

    /**
     * 持股比例小数位
     */
    public static final int RATIO_SCALE = 2;

    /**
     * 默认舍入方式：四舍五入
     */
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * 100，用于百分比计算
     */
    public static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private RestaurantMoneyUtils() {
    }

    /**
     * null 转 0
     */
    public static BigDecimal nullToZero(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    /**
     * 金额统一保留 2 位小数
     */
    public static BigDecimal scaleMoney(BigDecimal amount) {
        return nullToZero(amount).setScale(MONEY_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 比例统一保留 2 位小数
     */
    public static BigDecimal scaleRatio(BigDecimal ratio) {
        return nullToZero(ratio).setScale(RATIO_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 判断金额是否大于 0
     */
    public static boolean isPositive(BigDecimal amount) {
        return nullToZero(amount).compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 判断金额是否小于 0
     */
    public static boolean isNegative(BigDecimal amount) {
        return nullToZero(amount).compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * 计算分红金额
     *
     * 公式：
     * 分红金额 = 可分红金额 * 持股比例 / 100
     *
     * @param distributableProfit 可分红金额
     * @param shareRatio 持股比例，例如 40.00 表示 40%
     * @return 分红金额，保留 2 位小数
     */
    public static BigDecimal calculateDividendAmount(BigDecimal distributableProfit, BigDecimal shareRatio) {
        return scaleMoney(distributableProfit)
                .multiply(scaleRatio(shareRatio))
                .divide(ONE_HUNDRED, MONEY_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 计算尾差
     *
     * @param totalAmount 应分配总金额
     * @param allocatedAmount 已分配金额
     * @return 尾差金额
     */
    public static BigDecimal calculateRoundingDiff(BigDecimal totalAmount, BigDecimal allocatedAmount) {
        return scaleMoney(totalAmount).subtract(scaleMoney(allocatedAmount));
    }

}