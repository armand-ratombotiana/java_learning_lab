package com.distributed.timeordering;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HybridLogicalClock implements EventClock<HybridLogicalClock.HlcTimestamp> {
    private final AtomicLong physicalTime = new AtomicLong(0);
    private final AtomicInteger logicalCounter = new AtomicInteger(0);

    public record HlcTimestamp(long physical, int logical) implements java.io.Serializable {}

    @Override
    public HlcTimestamp tick() {
        long now = System.currentTimeMillis();
        while (true) {
            long pt = physicalTime.get();
            int lc = logicalCounter.get();
            if (now > pt) {
                if (physicalTime.compareAndSet(pt, now)) {
                    logicalCounter.set(0);
                    return new HlcTimestamp(now, 0);
                }
            } else {
                if (logicalCounter.compareAndSet(lc, lc + 1)) {
                    return new HlcTimestamp(pt, lc + 1);
                }
            }
        }
    }

    @Override
    public HlcTimestamp send() {
        return tick();
    }

    @Override
    public void receive(HlcTimestamp other, long currentTimeMillis) {
        long now = Math.max(currentTimeMillis, System.currentTimeMillis());
        long pt = physicalTime.get();
        int lc = logicalCounter.get();

        long newPt = Math.max(pt, Math.max(now, other.physical()));
        int newLc;
        if (newPt == pt && newPt == other.physical()) {
            newLc = Math.max(lc, other.logical()) + 1;
        } else if (newPt == other.physical()) {
            newLc = other.logical() + 1;
        } else if (newPt == pt) {
            newLc = lc + 1;
        } else {
            newLc = 0;
        }
        physicalTime.set(newPt);
        logicalCounter.set(newLc);
    }

    @Override
    public HlcTimestamp getValue() {
        return new HlcTimestamp(physicalTime.get(), logicalCounter.get());
    }

    @Override
    public String toString() {
        return "HLC{physical=" + physicalTime.get() + ", logical=" + logicalCounter.get() + "}";
    }
}
