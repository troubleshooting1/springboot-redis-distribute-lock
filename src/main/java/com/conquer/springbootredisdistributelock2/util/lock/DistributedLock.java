package com.conquer.springbootredisdistributelock2.util.lock;

/**
 * Description:
 * author: chenqiang
 * date: 2018/8/1 14:52
 */
public interface DistributedLock {

    //获取锁
    public boolean acquire();

    //释放锁
    public void release();
}
