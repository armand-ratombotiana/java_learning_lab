package com.javaacademy.lab52.performanceantipatterns;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PerformanceAntipatternsUltraDeepTest {

    @Test
    void stringConcatInLoopCreatesManyObjects() {
        String result = "";
        int iterations = 100;
        for (int i = 0; i < iterations; i++) {
            result += String.valueOf(i);
        }
        assertEquals(100, result.length());
    }

    @Test
    void stringBuilderIsMoreEfficient() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append(i);
        }
        assertEquals(100, sb.length());
    }

    @Test
    void boxingUnboxingOverhead() {
        Integer sum = 0;
        for (int i = 0; i < 1000; i++) {
            sum += i;
        }
        assertEquals(499500, sum.intValue());
    }

    @Test
    void primitiveSumIsFaster() {
        int sum = 0;
        for (int i = 0; i < 1000; i++) {
            sum += i;
        }
        assertEquals(499500, sum);
    }

    @Test
    void threadLocalRemovalPreventsLeak() {
        var tl = new ThreadLocal<String>();
        tl.set("value");
        assertEquals("value", tl.get());
        tl.remove();
        assertNull(tl.get());
    }

    @Test
    void synchronizedBlockContention() throws InterruptedException {
        var lock = new Object();
        var counter = new int[1];
        var threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    synchronized (lock) { counter[0]++; }
                }
            });
            threads[i].start();
        }
        for (var t : threads) t.join();
        assertEquals(1000, counter[0]);
    }
}
