# LeetCode 39 — Combination Sum

## Problem

Given an array of **distinct** integers `candidates` and a target integer `target`, return a list of all **unique combinations** where the chosen numbers sum to `target`.

You may use the same number an **unlimited number of times**. Two combinations are unique if the frequency of at least one of the chosen numbers is different.

**Constraints:**
- `1 <= candidates.length <= 30`
- `2 <= candidates[i] <= 40`
- `1 <= target <= 40`

---

## Solution: Backtracking (DFS)

```java
import java.util.*;

/**
 * LeetCode 39 — Combination Sum
 * Backtracking with pruning.
 *
 * Time: O(2^(target/min(candidate))) | Space: O(target/min(candidate))
 */
public class CombinationSum {

    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        backtrack(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] candidates, int remaining, int start,
                           List<Integer> path, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remaining) break;

            path.add(candidates[i]);
            backtrack(candidates, remaining - candidates[i], i, path, result);
            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) {
        CombinationSum s = new CombinationSum();

        // Test 1: Standard case
        System.out.println("Test 1: " + s.combinationSum(new int[]{2,3,6,7}, 7)
            + " (expected: [[2,2,3],[7]])");

        // Test 2: Multiple combinations
        System.out.println("Test 2: " + s.combinationSum(new int[]{2,3,5}, 8)
            + " (expected: [[2,2,2,2],[2,3,3],[3,5]])");

        // Test 3: Single candidate
        System.out.println("Test 3: " + s.combinationSum(new int[]{2}, 1)
            + " (expected: [])");

        // Test 4: Target equals candidate
        System.out.println("Test 4: " + s.combinationSum(new int[]{3,5}, 5)
            + " (expected: [[5]])");

        // Test 5: Multiple paths
        System.out.println("Test 5: " + s.combinationSum(new int[]{2,3,7}, 18)
            + " (expected: [[2,2,2,2,2,2,2,2,2],[2,2,2,2,2,2,2,3],[2,2,2,2,2,3,3],[2,2,2,3,3,3],[2,3,3,3,3],[3,3,3,3,3],[2,2,2,2,2,7],[2,2,2,7,7],[2,3,7,7],[7,7,3]])");
    }
}
```

---

## Complexity Analysis

| Aspect | Value |
|--------|-------|
| Time Complexity | O(N^(T/M+1)) where N = candidates length, T = target, M = min candidate |
| Space Complexity | O(T/M) for recursion stack and path |

### Pruning Strategy

1. **Sort candidates** — once `candidates[i] > remaining`, all subsequent candidates will also exceed remaining, so we break.
2. **Start index** — `i = start` ensures we don't revisit previous candidates, preventing `[2,3]` and `[3,2]` as duplicates. Since we can reuse the same candidate, we pass `i` not `i + 1` to the recursive call.
3. **Early termination** — if `remaining < 0`, we stop exploring that branch.

### Backtracking Pattern

```
function backtrack(remaining, start, path):
    if remaining == 0:
        add path to result
        return
    
    for i from start to n-1:
        if candidates[i] > remaining: break  (prune)
        path.add(candidates[i])
        backtrack(remaining - candidates[i], i, path)  (i, not i+1, for reuse)
        path.removeLast()
```

The key difference from subset problems: we allow reusing the same element by passing `i` instead of `i + 1` to the recursive call.
