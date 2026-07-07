package com.algo.lab31;

import java.util.*;

public class OnlinePaging {

    public static class LRU {
        private final int capacity;
        private final LinkedHashMap<Integer, Boolean> cache;

        public LRU(int capacity) {
            this.capacity = capacity;
            this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<Integer, Boolean> eldest) {
                    return size() > capacity;
                }
            };
        }

        public boolean access(int page) {
            if (cache.containsKey(page)) {
                return true;
            }
            cache.put(page, true);
            return false;
        }

        public int simulate(int[] pages) {
            int faults = 0;
            for (int p : pages) {
                if (!access(p)) faults++;
            }
            return faults;
        }
    }

    public static class FIFO {
        private final int capacity;
        private final Queue<Integer> queue;
        private final Set<Integer> set;

        public FIFO(int capacity) {
            this.capacity = capacity;
            this.queue = new ArrayDeque<>();
            this.set = new HashSet<>();
        }

        public int simulate(int[] pages) {
            int faults = 0;
            for (int p : pages) {
                if (!set.contains(p)) {
                    if (queue.size() == capacity) {
                        int evicted = queue.poll();
                        set.remove(evicted);
                    }
                    queue.offer(p);
                    set.add(p);
                    faults++;
                }
            }
            return faults;
        }
    }

    public static class Marker {
        private final int capacity;
        private final Map<Integer, Boolean> cache = new HashMap<>();
        private final Random random = new Random();

        public Marker(int capacity) {
            this.capacity = capacity;
        }

        public int simulate(int[] pages) {
            int faults = 0;
            cache.clear();
            for (int p : pages) {
                if (cache.containsKey(p)) {
                    cache.put(p, true);
                } else {
                    faults++;
                    if (cache.size() == capacity) {
                        List<Integer> unmarked = new ArrayList<>();
                        for (Map.Entry<Integer, Boolean> e : cache.entrySet()) {
                            if (!e.getValue()) unmarked.add(e.getKey());
                        }
                        if (unmarked.isEmpty()) {
                            cache.replaceAll((k, v) -> false);
                            unmarked.addAll(cache.keySet());
                        }
                        int victim = unmarked.get(random.nextInt(unmarked.size()));
                        cache.remove(victim);
                    }
                    cache.put(p, true);
                }
            }
            return faults;
        }
    }
}
