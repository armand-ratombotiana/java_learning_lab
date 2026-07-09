package com.javaacademy.lab38.memorymodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Ultra-deep tests for Memory Model lab.
 * Explores happens-before, volatile semantics, DCL, final field guarantees.
 */
class UltraDeepTest {

    @Test
    void finalFieldGuarantee() {
        class FinalExample {
            final int x;
            FinalExample() { x = 42; }
        }
        FinalExample obj = new FinalExample();
        assertEquals(42, obj.x, "Final field is properly initialized");
    }

    @Test
    void volatileVisibilityAcrossThreads() throws InterruptedException {
        class SharedFlag {
            volatile boolean flag;
            int counter;
        }
        SharedFlag shared = new SharedFlag();
        Thread writer = new Thread(() -> {
            shared.counter = 100;
            shared.flag = true;
        });
        Thread reader = new Thread(() -> {
            while (!shared.flag) { Thread.onSpinWait(); }
            // When flag is visible, counter should also be visible (happens-before)
            assertTrue(shared.counter == 100, 
                "Volatile write ensures prior non-volatile writes are visible");
        });
        writer.start();
        reader.start();
        writer.join();
        reader.join(5000);
    }

    @Test
    void dclSingletonWithVolatile() {
        class Singleton {
            private static volatile Singleton instance;
            Singleton() {}
            static Singleton getInstance() {
                if (instance == null) {
                    synchronized (Singleton.class) {
                        if (instance == null) {
                            instance = new Singleton();
                        }
                    }
                }
                return instance;
            }
        }
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        assertSame(s1, s2);
    }
}
