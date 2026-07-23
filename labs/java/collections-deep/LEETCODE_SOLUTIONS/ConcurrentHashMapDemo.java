package collectionsdeep;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * ConcurrentHashMap deep dive demonstration.
 * 
 * Shows:
 * - Java 8+ ConcurrentHashMap uses CAS + synchronized for tree bins
 * - Lock striping (default 16 segments in Java 7, removed in Java 8+)
 * - computeIfAbsent for atomic lazy initialization
 * - LongAdder (preferred over AtomicLong for high-contention counters)
 * - forEach, search, reduce with parallelism threshold
 * 
 * Time: O(1) average per operation, highly scalable
 * Space: O(n)
 */
public class ConcurrentHashMapDemo {

    public static void main(String[] args) throws Exception {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // Basic operations
        map.put("a", 1);
        map.put("b", 2);
        assert map.get("a") == 1;
        assert map.size() == 2;

        // computeIfAbsent — atomic lazy init
        map.computeIfAbsent("c", k -> k.length());
        assert map.get("c") == 1;

        // compute — atomic read-modify-write
        map.compute("a", (k, v) -> v == null ? 0 : v + 1);
        assert map.get("a") == 2;

        // merge — atomic upsert
        map.merge("d", 10, Integer::sum);
        map.merge("d", 5, Integer::sum);
        assert map.get("d") == 15;

        // LongAdder for high-contention counters
        var counter = new LongAdder();
        java.util.stream.IntStream.range(0, 1000).parallel().forEach(i -> counter.increment());
        assert counter.sum() == 1000;

        // Parallel bulk operations
        ConcurrentHashMap<String, Integer> big = new ConcurrentHashMap<>();
        for (int i = 0; i < 1000; i++) big.put("k" + i, i);

        // forEach with parallelism threshold
        big.forEach(16, (k, v) -> { /* process */ });

        // search
        String found = big.search(16, (k, v) -> v == 500 ? k : null);
        assert found != null && found.equals("k500");

        // reduce
        int sum = big.reduceValues(16, Integer::intValue, Integer::sum);
        assert sum == 499_500 : "Sum of 0..999 = " + 499_500 + " but got " + sum;

        System.out.println("All ConcurrentHashMapDemo tests passed.");
    }
}