package cn.iocoder.yudao.module.restaurant.service.dividend;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.*;

/**
 * 分红分布式锁 Service 实现类
 *
 * @author zhangyoming
 */
@Slf4j
@Service
@Validated
public class DividendLockServiceImpl implements DividendLockService {

    /**
     * 自动生成分红任务锁前缀。
     */
    private static final String AUTO_GENERATE_LOCK_KEY_PREFIX = "restaurant:dividend:auto-generate:";

    /**
     * 单门店分红生成锁前缀。
     */
    private static final String STORE_PERIOD_LOCK_KEY_PREFIX = "restaurant:dividend:generate:";

    /**
     * 尝试获取锁等待时间。
     *
     * 这里不建议等太久，避免用户一直卡住。
     */
    private static final long WAIT_TIME_SECONDS = 3L;

    /**
     * 锁自动释放时间。
     *
     * 自动生成所有门店可能比较久，所以给 30 分钟。
     */
    private static final long AUTO_GENERATE_LEASE_TIME_SECONDS = 30 * 60L;

    /**
     * 单门店分红生成一般较快，给 2 分钟。
     */
    private static final long STORE_PERIOD_LEASE_TIME_SECONDS = 2 * 60L;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public <T> T executeWithAutoGenerateLock(String periodMonth, Supplier<T> supplier) {
        if (StrUtil.isBlank(periodMonth)) {
            throw exception(DIVIDEND_LOCK_PERIOD_MONTH_EMPTY);
        }
        String lockKey = AUTO_GENERATE_LOCK_KEY_PREFIX + periodMonth;
        return executeWithLock(lockKey, WAIT_TIME_SECONDS, AUTO_GENERATE_LEASE_TIME_SECONDS,
                () -> exception(DIVIDEND_AUTO_GENERATE_LOCKED), supplier);
    }

    @Override
    public <T> T executeWithStorePeriodLock(Long storeId, String periodMonth, Supplier<T> supplier) {
        if (storeId == null) {
            throw exception(DIVIDEND_LOCK_STORE_ID_EMPTY);
        }
        if (StrUtil.isBlank(periodMonth)) {
            throw exception(DIVIDEND_LOCK_PERIOD_MONTH_EMPTY);
        }
        String lockKey = STORE_PERIOD_LOCK_KEY_PREFIX + storeId + ":" + periodMonth;
        return executeWithLock(lockKey, WAIT_TIME_SECONDS, STORE_PERIOD_LEASE_TIME_SECONDS,
                () -> exception(DIVIDEND_STORE_PERIOD_GENERATE_LOCKED), supplier);
    }

    private <T> T executeWithLock(String lockKey, long waitTimeSeconds, long leaseTimeSeconds,
                                  Supplier<RuntimeException> lockFailureExceptionSupplier,
                                  Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTimeSeconds, leaseTimeSeconds, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("[executeWithLock][获取锁失败，lockKey({})]", lockKey);
                throw lockFailureExceptionSupplier.get();
            }

            log.info("[executeWithLock][获取锁成功，lockKey({})]", lockKey);
            return supplier.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("[executeWithLock][获取锁被中断，lockKey({})]", lockKey, ex);
            throw lockFailureExceptionSupplier.get();
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("[executeWithLock][释放锁成功，lockKey({})]", lockKey);
            }
        }
    }

}