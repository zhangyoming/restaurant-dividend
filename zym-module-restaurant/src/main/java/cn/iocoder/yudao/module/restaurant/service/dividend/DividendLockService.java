package cn.iocoder.yudao.module.restaurant.service.dividend;

import java.util.function.Supplier;

/**
 * 分红分布式锁 Service 接口
 *
 * @author zhangyoming
 */
public interface DividendLockService {

    /**
     * 使用自动生成分红任务锁执行
     *
     * @param periodMonth 账期月份，例如 2026-06
     * @param supplier 执行业务
     * @return 执行结果
     */
    <T> T executeWithAutoGenerateLock(String periodMonth, Supplier<T> supplier);

    /**
     * 使用单门店分红生成锁执行
     *
     * @param storeId 门店编号
     * @param periodMonth 账期月份，例如 2026-06
     * @param supplier 执行业务
     * @return 执行结果
     */
    <T> T executeWithStorePeriodLock(Long storeId, String periodMonth, Supplier<T> supplier);

}