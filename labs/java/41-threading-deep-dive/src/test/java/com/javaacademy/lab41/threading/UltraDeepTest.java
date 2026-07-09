package com.javaacademy.lab41.threading;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.locks.LockSupport;

/**
 * Ultra-deep tests for Threading Deep Dive lab.
 * Explores virtual threads, park/unpark, thread state transitions.
 */
class UltraDeepTest {

    @Test
    void virtualThreadCreationAndJoin() throws Exception {
        var result = new String[1];
        Thread vt = Thread.ofVirtual()
            .name("test-vt")
            .start(() -> result[0] = "ran");
        vt.join();
        assertEquals("ran", result[0]);
        assertEquals("test-vt", vt.getName());
    }

    @Test
    void lockSupportParkUnpark() throws InterruptedException {
        var state = new boolean[1];
        Thread t = new Thread(() -> {
            LockSupport.park();
            state[0] = true;
        });
        t.start();
        Thread.sleep(100);
        assertFalse(state[0], "Thread should be parked");
        LockSupport.unpark(t);
        t.join(2000);
        assertTrue(state[0], "Thread should complete after unpark");
    }

    @Test
    void threadInterruptionCooperative() throws InterruptedException {
        Thread t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                // busy work
            }
        });
        t.start();
        t.interrupt();
        t.join(2000);
        assertFalse(t.isAlive());
    }
}
