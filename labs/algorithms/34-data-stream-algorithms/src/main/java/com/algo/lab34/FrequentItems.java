package com.algo.lab34;

import java.util.*;

public class FrequentItems {

    private final int k;
    private final Map<Integer, Integer> counters;

    public FrequentItems(int k) {
        this.k = k;
        this.counters = new HashMap<>();
    }

    public void add(int value) {
        if (counters.containsKey(value)) {
            counters.put(value, counters.get(value) + 1);
        } else if (counters.size() < k - 1) {
            counters.put(value, 1);
        } else {
            Iterator<Map.Entry<Integer, Integer>> it = counters.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Integer> e = it.next();
                int newCount = e.getValue() - 1;
                if (newCount == 0) it.remove();
                else e.setValue(newCount);
            }
        }
    }

    public int getEstimatedFrequency(int value) {
        return counters.getOrDefault(value, 0);
    }

    public Set<Integer> getCandidates() {
        return counters.keySet();
    }
}
