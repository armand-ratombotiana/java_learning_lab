package com.algo.lab34;

import java.util.*;

public class SlidingWindowCount {

    private final int windowSize;
    private final Deque<Set<Integer>> buckets = new ArrayDeque<>();
    private final Deque<Long> timestamps = new ArrayDeque<>();
    private long currentTime = 0;

    public SlidingWindowCount(int windowSize) {
        this.windowSize = windowSize;
    }

    public void add(int value) {
        cleanup();
        if (buckets.isEmpty() || buckets.getLast().size() >= 4) {
            buckets.addLast(new HashSet<>());
            timestamps.addLast(currentTime);
        }
        buckets.getLast().add(value);
        currentTime++;
    }

    public int getDistinctCount() {
        cleanup();
        Set<Integer> all = new HashSet<>();
        for (Set<Integer> bucket : buckets) all.addAll(bucket);
        return all.size();
    }

    private void cleanup() {
        while (!timestamps.isEmpty() && currentTime - timestamps.getFirst() > windowSize) {
            timestamps.removeFirst();
            buckets.removeFirst();
        }
    }
}
