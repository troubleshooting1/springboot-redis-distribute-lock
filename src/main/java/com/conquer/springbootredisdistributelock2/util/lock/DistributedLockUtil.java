package com.conquer.springbootredisdistributelock2.util.lock;

import com.conquer.springbootredisdistributelock2.util.lock.impl.JedisLock;

/**
 * Description:
 * author: chenqiang
 * date: 2018/8/1 15:10
 */
public class DistributedLockUtil {
    /**
     * 获取分布式锁
     * 默认获取锁10s超时，锁过期时间60s
     * @param lockKey
     * @return
     */
    public static DistributedLock getDistributedLock(String lockKey) {
        lockKey = assembleKey(lockKey);
        JedisLock lock = new JedisLock(lockKey);
        return lock;
    }

    /**
     * 正式环境、测试环境共用一个redis时，避免key相同造成影响
     * @param lockKey
     * @return
     */
    private static String assembleKey(String lockKey) {
        return String.format("lock_%s", lockKey);
    }

    /** 获取分布式锁
     * 默认获取锁10s超时，锁过期时间60s
     * @param lockKey
     * @param timeoutMsecs 指定获取锁超时时间
     * @return
     */
    public static DistributedLock getDistributedLock(String lockKey, int timeoutMsecs) {
        lockKey = assembleKey(lockKey);
        JedisLock lock = new JedisLock(lockKey, timeoutMsecs);
        return lock;
    }

    /**
     * 获取分布式锁
     * 默认获取锁10s超时，锁过期时间60s
     * @param lockKey
     * @param timeoutMsecs 指定获取锁超时时间
     * @param expireMsecs 指定锁过期时间
     * @return
     */
    public static DistributedLock getDistributedLock(String lockKey, int timeoutMsecs, int expireMsecs) {
        lockKey = assembleKey(lockKey);
        JedisLock lock = new JedisLock(lockKey, expireMsecs, timeoutMsecs);
        return lock;
    }
}
