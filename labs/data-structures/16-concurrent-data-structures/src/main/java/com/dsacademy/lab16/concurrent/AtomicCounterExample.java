package com.dsacademy.lab16.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicCounterExample {

    private final AtomicInteger counter = new AtomicInteger(0);
    private final AtomicLong total = new AtomicLong(0);

    public int increment() {
        total.incrementAndGet();
        return counter.incrementAndGet();
    }

    public int decrement() {
        total.incrementAndGet();
        return counter.decrementAndGet();
    }

    public int get() { return counter.get(); }

    public long getTotalOperations() { return total.get(); }

    public boolean compareAndSet(int expected, int newValue) {
        return counter.compareAndSet(expected, newValue);
    }

    public void reset() {
        counter.set(0);
        total.set(0);
    }
}
