# LeetCode Solution: Parallel Task Execution (Structured Concurrency)

**Problem:** [1654. Minimum Jumps to Reach Home](https://leetcode.com/problems/minimum-jumps-to-reach-home/) (adapted for concurrency)

Demonstrates parallel exploration of branching paths using structured concurrency.

## Approach

Use `ShutdownOnSuccess` to race multiple search strategies. The first strategy to find the answer cancels the others.

## Java 21 Solution

```java
import java.util.*;
import java.util.concurrent.*;

class Solution {

    record Position(int pos, boolean backward) {}

    public int minimumJumps(int[] forbidden, int a, int b, int x) {
        Set<Integer> forbiddenSet = new HashSet<>();
        for (int f : forbidden) forbiddenSet.add(f);

        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<Integer>()) {
            scope.fork(() -> bfsSearch(forbiddenSet, a, b, x, true));
            scope.fork(() -> bfsSearch(forbiddenSet, a, b, x, false));
            scope.join();
            return scope.result();
        } catch (ExecutionException e) {
            return -1;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }
    }

    private Integer bfsSearch(Set<Integer> forbidden, int a, int b,
                              int target, boolean forwardFirst) {
        Set<String> visited = new HashSet<>();
        Queue<Position> queue = new ArrayDeque<>();
        queue.add(new Position(0, false));
        visited.add("0,false");

        int jumps = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Position cur = queue.poll();
                if (cur.pos() == target) return jumps;

                // Move forward
                int fwd = cur.pos() + a;
                String fwdKey = fwd + ",false";
                if (fwd <= 6000 && !forbidden.contains(fwd)
                        && visited.add(fwdKey)) {
                    queue.add(new Position(fwd, false));
                }

                // Move backward (only if last move wasn't backward)
                int bwd = cur.pos() - b;
                String bwdKey = bwd + ",true";
                if (!cur.backward() && bwd >= 0
                        && !forbidden.contains(bwd)
                        && visited.add(bwdKey)) {
                    queue.add(new Position(bwd, true));
                }
            }
            jumps++;
        }
        return null; // not found; scope will try the other fork
    }
}
```

## Key Takeaway

Structured concurrency with `ShutdownOnSuccess` enables **race-based parallelism** — the first successful strategy returns immediately, and remaining work is automatically cancelled.
