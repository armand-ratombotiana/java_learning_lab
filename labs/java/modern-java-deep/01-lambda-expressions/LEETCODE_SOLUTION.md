# LeetCode 347: Top K Frequent Elements

> **Difficulty**: Medium | **Company**: Amazon, Google, Meta, Apple | **Category**: Modern Java Deep (Lambda / Stream API)

## Problem

Given an integer array `nums` and an integer `k`, return the `k` most frequent elements. You may return the answer in any order.

## Solution

Uses Java Stream API for a functional-programming approach: count frequencies with `Collectors.groupingBy` + `Collectors.counting`, then sort by frequency and limit to k.

```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * LeetCode 347: Top K Frequent Elements
 *
 * Functional approach using Java Stream API.
 *
 * Time: O(N log N) due to sort, can be O(N log k) with min-heap
 * Space: O(N) for frequency map
 */
public class TopKFrequentElements {

    public int[] topKFrequent(int[] nums, int k) {
        return Arrays.stream(nums)
            .boxed()
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
            ))
            .entrySet()
            .stream()
            .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
            .limit(k)
            .mapToInt(Map.Entry::getKey)
            .toArray();
    }

    /**
     * Alternative: PriorityQueue-based (O(N log k)) for better performance on large inputs.
     */
    public int[] topKFrequentHeap(int[] nums, int k) {
        Map<Integer, Long> freq = Arrays.stream(nums)
            .boxed()
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
            ));

        return freq.entrySet().stream()
            .collect(() -> new PriorityQueue<>(Map.Entry.<Integer, Long>comparingByValue()),
                (pq, e) -> { pq.offer(e); if (pq.size() > k) pq.poll(); },
                AbstractQueue::addAll)
            .stream()
            .mapToInt(Map.Entry::getKey)
            .toArray();
    }

    // ─────────────────────
    // Verification
    // ─────────────────────
    public static void main(String[] args) {
        var sol = new TopKFrequentElements();

        // Example 1: nums = [1,1,1,2,2,3], k = 2
        int[] result1 = sol.topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2);
        System.out.println("Top 2: " + Arrays.toString(result1));
        assert containsSame(result1, new int[]{1, 2}) : "Expected [1, 2]";

        // Example 2: nums = [1], k = 1
        int[] result2 = sol.topKFrequent(new int[]{1}, 1);
        assert Arrays.equals(result2, new int[]{1}) : "Expected [1]";

        // Heap version
        int[] result3 = sol.topKFrequentHeap(new int[]{1, 1, 1, 2, 2, 3}, 2);
        assert containsSame(result3, new int[]{1, 2}) : "Heap version mismatch";

        System.out.println("All tests passed.");
    }

    private static boolean containsSame(int[] a, int[] b) {
        Set<Integer> sa = Arrays.stream(a).boxed().collect(Collectors.toSet());
        Set<Integer> sb = Arrays.stream(b).boxed().collect(Collectors.toSet());
        return sa.equals(sb);
    }
}
```

## Complexity

| Version           | Time       | Space |
|-------------------|------------|-------|
| Stream + sort     | O(N log N) | O(N)  |
| PriorityQueue     | O(N log k) | O(N)  |
| Bucket sort       | O(N)       | O(N)  |

## Key Insights

1. **Functional pipeline**: `groupingBy` + `counting()` builds frequency map in one expression.
2. **Declarative style**: The pipeline reads like a specification: group → count → sort → limit → extract.
3. **PriorityQueue alternative**: More efficient for large N but less idiomatic with pure streams.
4. **Bucket sort (optimal O(N))**: Use an array of lists indexed by frequency — avoids sorting entirely.
