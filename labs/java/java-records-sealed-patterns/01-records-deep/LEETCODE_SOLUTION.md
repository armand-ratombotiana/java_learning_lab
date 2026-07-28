# LeetCode Solution: Merge Intervals (Using Records)

**Problem:** [56. Merge Intervals](https://leetcode.com/problems/merge-intervals/)

This solution demonstrates Java 21 records, record patterns, and compact constructors.

## Approach

Use a record `Interval` with a compact constructor for validation, then apply a sweep-line merge using streams and record patterns.

## Java 21 Solution

```java
import java.util.*;

public class MergeIntervals {

    record Interval(int start, int end) {
        Interval { // compact constructor with validation
            if (start > end) {
                throw new IllegalArgumentException(
                    "start must be <= end, got: " + start + " > " + end);
            }
        }
    }

    public int[][] merge(int[][] intervals) {
        if (intervals.length == 0) return new int[0][];

        var sorted = Arrays.stream(intervals)
            .map(arr -> new Interval(arr[0], arr[1]))
            .sorted(Comparator.comparingInt(Interval::start))
            .toList();

        List<Interval> merged = new ArrayList<>();
        Interval current = sorted.getFirst();

        for (Interval next : sorted.subList(1, sorted.size())) {
            if (current.end() >= next.start()) {
                current = new Interval(
                    current.start(),
                    Math.max(current.end(), next.end())
                );
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);

        return merged.stream()
            .map(iv -> new int[]{iv.start(), iv.end()})
            .toArray(int[][]::new);
    }
}
```

## Key Takeaway

Records make data aggregation (like intervals) **immutable, readable, and safe**. The compact constructor enforces invariants at construction time, eliminating defensive checks elsewhere.
