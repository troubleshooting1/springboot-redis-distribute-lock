package com.conquer.springbootredisdistributelock2.util.lock.impl;

import com.conquer.springbootredisdistributelock2.util.SpringContextUtil;
import com.conquer.springbootredisdistributelock2.util.lock.DistributedLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Description:
 * author: chenqiang
 * date: 2018/8/1 14:53
 */
public class JedisLock implements DistributedLock {
    private static Logger logger = LoggerFactory.getLogger(JedisLock.class);

    private static StringRedisTemplate redisTemplate;

    /**
     * 分布式锁的键值
     */
    String lockKey;     //锁的键
    int expireMsecs = 10 * 1000;        //锁超时，防止线程在入锁以后，无限的执行等待，10秒
    int timeoutMsecs = 10 * 1000;       //锁等待，防止线程饥饿，10秒
    boolean locked = false;       //是否已经获取锁

    /**
     * 获取指定键值的锁
     *
     * @param lockKey
     */
    public JedisLock(String lockKey) {
        this.lockKey = lockKey;
    }

    public JedisLock(String lockKey, int timeoutMsecs) {
        this.lockKey = lockKey;
        this.timeoutMsecs = timeoutMsecs;
    }

    public JedisLock(String lockKey, int timeoutMsecs, int expireMsecs) {
        this.lockKey = lockKey;
        this.timeoutMsecs = timeoutMsecs;
        this.expireMsecs = expireMsecs;
    }

    public String getLockKey() {
        return lockKey;
    }

    @Override
    public synchronized boolean acquire() {
        int timeout = timeoutMsecs;
        if (redisTemplate == null) {
            redisTemplate = SpringContextUtil.getBean(StringRedisTemplate.class);
        }
        try {
            while (timeout > 0) {
                long expires = System.currentTimeMillis() + expireMsecs + 1;        //当前时间+超时时间+1
                String expiresStr = String.valueOf(expires);      //锁到期时间

                if (redisTemplate.opsForValue().setIfAbsent(lockKey, expiresStr)) {
                    locked = true;
                    return true;
                }

                String currentValueStr = redisTemplate.opsForValue().get(lockKey);        //redis里的时间
                //如果过期了
                if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                    //判断是否为空，不为空的情况下，如果被其他线程设置了值，则第二个条件判断是过不去的
                    // lock is expired

                    //类似于update xxx set num=num-1 where num=num
                    String oldValueStr = redisTemplate.opsForValue().getAndSet(lockKey, expiresStr);       //获取旧值，赋给新值
                    if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                        locked = true;
                        return true;
                    }
                }
                timeout -= 100;
                Thread.sleep(100);
            }
        } catch (Exception e) {
            logger.error("release lock due to error", e);
        }
        return false;
    }

    @Override
    public synchronized void release() {
        if (redisTemplate == null) {
            redisTemplate = SpringContextUtil.getBean(StringRedisTemplate.class);
        }
        try {
            if (locked) {
                String currentValueStr = redisTemplate.opsForValue().get(lockKey);        //redis里的时间
                //校验是否超过有效期，如果不在有效期，那说明当前锁已经失效，不能进行删除锁操作
                if (currentValueStr != null && Long.parseLong(currentValueStr) > System.currentTimeMillis()) {
                    redisTemplate.delete(lockKey);
                    locked = false;
                }
            }
        } catch (Exception e) {
            logger.error("release lock due to error", e);
        }
    }

//    @Override
//    public boolean acquire() {
//        return false;
//    }
//
//    @Override
//    public void release() {
//
//    }
}
