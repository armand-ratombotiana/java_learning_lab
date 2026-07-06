package com.javaacademy.lab38.memorymodel;

import sun.misc.Contended;

public class FalseSharingExample {

    @Contended
    public static class ContendedCounter {
        public volatile long value = 0;
    }

    public static class PlainCounter {
        public volatile long value = 0;
    }

    private final ContendedCounter[] contendedCounters;
    private final PlainCounter[] plainCounters;
    private final int size;

    public FalseSharingExample(int size) {
        this.size = size;
        contendedCounters = new ContendedCounter[size];
        plainCounters = new PlainCounter[size];
        for (int i = 0; i < size; i++) {
            contendedCounters[i] = new ContendedCounter();
            plainCounters[i] = new PlainCounter();
        }
    }

    public long runContended(int threadIndex, int iterations) {
        ContendedCounter counter = contendedCounters[threadIndex];
        for (int i = 0; i < iterations; i++) {
            counter.value++;
        }
        return counter.value;
    }

    public long runPlain(int threadIndex, int iterations) {
        PlainCounter counter = plainCounters[threadIndex];
        for (int i = 0; i < iterations; i++) {
            counter.value++;
        }
        return counter.value;
    }

    public int getSize() { return size; }
}
