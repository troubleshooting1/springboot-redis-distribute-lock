package com.conquer.springbootredisdistributelock2.controller;

import com.conquer.springbootredisdistributelock2.util.lock.DistributedLock;
import com.conquer.springbootredisdistributelock2.util.lock.DistributedLockUtil;

/**
 * Description:
 * author: chenqiang
 * date: 2018/8/1 15:25
 */
public class test {
    public static void main(String[] args) {
        DistributedLock lock = DistributedLockUtil.getDistributedLock("a");
        try {
            if (lock.acquire()) {
                //获取锁成功业务代码
            } else { // 获取锁失败
                //获取锁失败业务代码
            }
        } finally {
            if (lock != null) {
                lock.release();
            }
        }
    }
}
