package com.java.module.foreign;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * Lab 04: Project Loom Deep — virtual thread internals, carrier
 * threads, pinning, continuations, thread-local vs scoped values.
 */
public class ProjectLoomLab {

    // --- Pinning demonstration ---
    private final Lock lock = new ReentrantLock();
    private int counter = 0;

    // Pins virtual thread — demonstrates pinning
    public synchronized int syncIncrement() {
        return ++counter;
    }

    // Does NOT pin — virtual-thread-friendly
    public int lockIncrement() {
        lock.lock();
        try {
            return ++counter;
        } finally {
            lock.unlock();
        }
    }

    // --- Virtual thread carrier inspection ---
    public void inspectCarrier() {
        var t = Thread.currentThread();
        System.out.println("Thread: " + t);
        System.out.println("Is virtual: " + t.isVirtual());
        if (t.isVirtual()) {
            System.out.println("Carrier: " +
                    java.lang.reflect.Field.class.getClass()); // placeholder
        }
    }

    // --- Simulate continuation ---
    static class SimpleContinuation {
        enum State { RUNNABLE, YIELDED, DONE }
        private State state = State.RUNNABLE;
        private final Runnable task;
        private int yieldPoint = 0;

        SimpleContinuation(Runnable task) {
            this.task = task;
        }

        public boolean run() {
            if (state == State.DONE) return false;
            // In real JVM: restore stack and jump to yield point
            task.run();
            state = State.DONE;
            return true;
        }
    }

    // --- ThreadLocal vs ScopedValue demo ---
    private static final ThreadLocal<String> TL = new ThreadLocal<>();
    private static final ScopedValue<String> SV = ScopedValue.newInstance();

    public void threadLocalDemo() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<?> f1 = executor.submit(() -> {
                TL.set("vt-1");
                System.out.println("TL in vt-1: " + TL.get());
            });
            Future<?> f2 = executor.submit(() -> {
                TL.set("vt-2");
                System.out.println("TL in vt-2: " + TL.get());
            });
            f1.get(); f2.get();
        }
    }

    public void scopedValueDemo() throws Exception {
        ScopedValue.where(SV, "req-123")
                .run(() -> {
                    System.out.println("SV: " + SV.get());
                    // SV is immutable — cannot be reassigned
                });
    }

    // --- Demo ---
    public static void main(String[] args) throws Exception {
        var lab = new ProjectLoomLab();

        // Carrier inspection
        lab.inspectCarrier();

        // Virtual thread carrier
        Thread vt = Thread.ofVirtual().start(() -> {
            System.out.println("Virtual thread carrier: " +
                    Thread.currentThread());
        });
        vt.join();

        // ThreadLocal
        lab.threadLocalDemo();

        // ScopedValue
        lab.scopedValueDemo();

        // Pinning demo
        var result = lab.lockIncrement();
        System.out.println("Count (lock): " + result);
    }
}
