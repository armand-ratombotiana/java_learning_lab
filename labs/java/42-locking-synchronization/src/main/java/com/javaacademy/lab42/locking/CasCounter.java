package com.javaacademy.lab42.locking;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Atomic counter using Unsafe.compareAndSwapInt to demonstrate CAS internals.
 * Shows how atomic classes like AtomicInteger work under the hood.
 */
public class CasCounter {

    private static final Unsafe UNSAFE;
    private static final long VALUE_OFFSET;

    private volatile int value;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
            VALUE_OFFSET = UNSAFE.objectFieldOffset(CasCounter.class.getDeclaredField("value"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CasCounter(int initial) {
        this.value = initial;
    }

    public int incrementAndGet() {
        int prev;
        do {
            prev = value;
        } while (!UNSAFE.compareAndSwapInt(this, VALUE_OFFSET, prev, prev + 1));
        return prev + 1;
    }

    public int get() { return value; }

    public static void main(String[] args) throws Exception {
        System.out.println("=== CAS Counter with Unsafe ===\n");
        CasCounter counter = new CasCounter(0);

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) counter.incrementAndGet();
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println("Final counter value: " + counter.get() + " (expected: 10000)");
    }
}
