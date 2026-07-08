package com.distributed.timeordering;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LamportClockTest {

    @Test
    void testTickIncrements() {
        LamportClock clock = new LamportClock();
        assertEquals(1, clock.tick());
        assertEquals(2, clock.tick());
        assertEquals(3, clock.tick());
    }

    @Test
    void testReceiveUpdatesClock() {
        LamportClock clock = new LamportClock();
        clock.tick();
        assertEquals(1, clock.getValue());
        clock.receive(5, System.currentTimeMillis());
        assertEquals(6, clock.getValue());
    }

    @Test
    void testMultipleThreads() throws InterruptedException {
        LamportClock clock = new LamportClock();
        Thread t1 = new Thread(() -> { for (int i = 0; i < 1000; i++) clock.tick(); });
        Thread t2 = new Thread(() -> { for (int i = 0; i < 1000; i++) clock.tick(); });
        t1.start(); t2.start();
        t1.join(); t2.join();
        assertEquals(2000, clock.getValue());
    }

    @Test
    void testMonotonicity() {
        LamportClock clock = new LamportClock();
        int prev = 0;
        for (int i = 0; i < 100; i++) {
            int val = clock.tick();
            assertTrue(val > prev);
            prev = val;
        }
    }
}
