# LeetCode 621 — Task Scheduler

## Problem

Given a characters array `tasks` representing the tasks a CPU needs to do, where each letter represents a different task. Tasks could be done in any order. Each task takes one unit of time, and the CPU must wait **`n` units of time** between two identical tasks.

Return the **minimum number of time units** the CPU will take to finish all the given tasks.

**Constraints:**
- `1 <= tasks.length <= 10^4`
- `0 <= n <= 100`

---

## Solution: Greedy + Mathematical Formula

```java
import java.util.*;

/**
 * LeetCode 621 — Task Scheduler
 *
 * Greedy approach: schedule the most frequent task first, then fill idle slots.
 *
 * Time: O(n) | Space: O(1)
 */
public class TaskScheduler {

    public int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char c : tasks) {
            freq[c - 'A']++;
        }

        int maxFreq = 0;
        int maxCount = 0;
        for (int f : freq) {
            if (f > maxFreq) {
                maxFreq = f;
                maxCount = 1;
            } else if (f == maxFreq) {
                maxCount++;
            }
        }

        int partCount = maxFreq - 1;
        int partLength = n - (maxCount - 1);
        int emptySlots = partCount * partLength;
        int availableTasks = tasks.length - maxFreq * maxCount;
        int idles = Math.max(0, emptySlots - availableTasks);

        return tasks.length + idles;
    }

    public static void main(String[] args) {
        TaskScheduler s = new TaskScheduler();

        // Test 1: Standard case
        char[] t1 = {'A','A','A','B','B','B'};
        System.out.println("Test 1: " + s.leastInterval(t1, 2) + " (expected: 8)");

        // Test 2: No cooldown
        char[] t2 = {'A','A','A','B','B','B'};
        System.out.println("Test 2: " + s.leastInterval(t2, 0) + " (expected: 6)");

        // Test 3: Single task
        char[] t3 = {'A','A','A','A','A','A'};
        System.out.println("Test 3: " + s.leastInterval(t3, 2) + " (expected: 6)");

        // Test 4: All unique tasks
        char[] t4 = {'A','B','C','D','E','F','G'};
        System.out.println("Test 4: " + s.leastInterval(t4, 2) + " (expected: 7)");

        // Test 5: Large cooldown
        char[] t5 = {'A','A','A','B','C'};
        System.out.println("Test 5: " + s.leastInterval(t5, 3) + " (expected: 9)");

        // Test 6: Edge case with many same-frequency tasks
        char[] t6 = {'A','A','B','B','C','C','D','D'};
        System.out.println("Test 6: " + s.leastInterval(t6, 2) + " (expected: 8)");
    }
}
```

---

## Complexity Analysis

| Aspect | Value |
|--------|-------|
| Time Complexity | O(n) — single pass to count frequencies, then O(26) to find max |
| Space Complexity | O(1) — fixed-size frequency array of 26 |

### Intuition

The greedy insight: we should always schedule the most frequent task first because it creates the most constraints. We arrange the schedule in frames of `(n + 1)` slots, placing the most frequent task at the start of each frame.

### Formula Explained

```
Let maxFreq  = frequency of the most frequent task(s)
Let maxCount = number of tasks that appear maxFreq times

Arrange tasks in a grid:
- maxFreq rows (one for each occurrence of the most frequent task)
- partLength = n - (maxCount - 1) columns of work per row except the last

Empty slots = (maxFreq - 1) * (n - (maxCount - 1))
Available tasks = tasks.length - maxFreq * maxCount
Idles needed = max(0, emptySlots - availableTasks)
Total time = tasks.length + idles
```

When `n` is small or there are many distinct tasks, the total time is simply `tasks.length` (no idle slots needed). The formula captures exactly when idle slots must be inserted.
