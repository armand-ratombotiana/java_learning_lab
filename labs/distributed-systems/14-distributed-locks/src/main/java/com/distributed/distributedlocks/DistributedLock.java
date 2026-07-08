package com.distributed.distributedlocks;

import java.time.Duration;

public interface DistributedLock {
    boolean tryLock(String key, Duration timeout);
    void unlock(String key);
    long getFencingToken(String key);
}
