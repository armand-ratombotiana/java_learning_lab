package com.javaacademy.lab38.memorymodel;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HappensBeforeExample {

    private int x = 0;
    private boolean ready = false;

    public void writer() {
        x = 42;
        ready = true;
    }

    public int reader() {
        if (ready) return x;
        return -1;
    }

    public int demonstrateHappensBefore() throws Exception {
        x = 0;
        ready = false;
        final int[] result = new int[1];

        Thread writerThread = new Thread(this::writer);
        Thread readerThread = new Thread(() -> result[0] = reader());

        writerThread.start();
        readerThread.start();
        writerThread.join();
        readerThread.join();

        return result[0];
    }

    private final AtomicInteger atomicX = new AtomicInteger(0);
    private final AtomicBoolean atomicReady = new AtomicBoolean(false);

    public void atomicWriter() {
        atomicX.set(42);
        atomicReady.set(true);
    }

    public int atomicReader() {
        if (atomicReady.get()) return atomicX.get();
        return -1;
    }

    public int demonstrateAtomic() throws Exception {
        atomicX.set(0);
        atomicReady.set(false);
        final int[] result = new int[1];

        Thread t1 = new Thread(this::atomicWriter);
        Thread t2 = new Thread(() -> result[0] = atomicReader());

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        return result[0];
    }

    private final Object lock = new Object();
    private int guardedX = 0;
    private boolean guardedReady = false;

    public void synchronizedWriter() {
        synchronized (lock) {
            guardedX = 42;
            guardedReady = true;
        }
    }

    public int synchronizedReader() {
        synchronized (lock) {
            if (guardedReady) return guardedX;
        }
        return -1;
    }
}
