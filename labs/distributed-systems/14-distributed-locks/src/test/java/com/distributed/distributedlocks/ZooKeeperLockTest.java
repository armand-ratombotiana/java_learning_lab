package com.distributed.distributedlocks;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

class ZooKeeperLockTest {

    @Test
    void testLockAcquisition() {
        ZooKeeperLock lock = new ZooKeeperLock();
        assertTrue(lock.tryLock("node1", Duration.ofSeconds(30)));
        assertFalse(lock.tryLock("node1", Duration.ofSeconds(1)));
        lock.unlock("node1");
        assertTrue(lock.tryLock("node1", Duration.ofSeconds(30)));
    }

    @Test
    void testFencingTokenMonotonic() {
        ZooKeeperLock lock = new ZooKeeperLock();
        lock.tryLock("x", Duration.ofSeconds(10));
        long t1 = lock.getFencingToken("x");
        lock.unlock("x");
        lock.tryLock("x", Duration.ofSeconds(10));
        long t2 = lock.getFencingToken("x");
        assertTrue(t2 > t1);
    }
}
