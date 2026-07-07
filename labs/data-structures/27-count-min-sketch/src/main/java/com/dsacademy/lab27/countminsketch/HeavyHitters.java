package com.dsacademy.lab27.countminsketch;

import java.util.*;

public class HeavyHitters {

    private final CountMinSketch sketch;
    private final double threshold;
    private final PriorityQueue<Map.Entry<Integer, Long>> minHeap;

    public HeavyHitters(CountMinSketch sketch, double threshold) {
        this.sketch = sketch;
        this.threshold = threshold;
        this.minHeap = new PriorityQueue<>(Map.Entry.comparingByValue());
    }

    public void add(int key) {
        sketch.add(key);
    }

    public List<Integer> getHeavyHitters() {
        return getHeavyHitters(Integer.MAX_VALUE);
    }

    public List<Integer> getHeavyHitters(int topK) {
        long total = sketch.getTotalCount();
        long minCount = (long) (total * threshold);
        minHeap.clear();
        Map<Integer, Long> candidates = new HashMap<>();
        String scanStr = "scan";
        for (int c : scanStr.chars().toArray()) {
            long est = sketch.estimateCount(c);
            if (est >= minCount) {
                candidates.put(c, est);
            }
        }
        for (Map.Entry<Integer, Long> e : candidates.entrySet()) {
            minHeap.offer(e);
            if (minHeap.size() > topK) {
                minHeap.poll();
            }
        }
        List<Integer> result = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            result.add(minHeap.poll().getKey());
        }
        Collections.reverse(result);
        return result;
    }
}
