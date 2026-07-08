package com.distributed.timeordering;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class LamportClock implements EventClock<Integer> {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Integer tick() {
        return counter.incrementAndGet();
    }

    @Override
    public Integer send() {
        return counter.incrementAndGet();
    }

    @Override
    public void receive(Integer other, long currentTimeMillis) {
        int max = Math.max(counter.get(), other);
        counter.set(max + 1);
    }

    @Override
    public Integer getValue() {
        return counter.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LamportClock that)) return false;
        return counter.get() == that.counter.get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(counter.get());
    }

    @Override
    public String toString() {
        return "LamportClock{counter=" + counter.get() + "}";
    }
}
