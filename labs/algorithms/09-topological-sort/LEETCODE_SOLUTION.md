# LeetCode 210 — Course Schedule II

## Problem

There are `n` courses labeled `0` to `n - 1`. You are given `prerequisites[i] = (a, b)` meaning you must take `b` before `a` (b → a). Return the **ordering** of courses to take all courses, or an empty array if impossible.

**Constraints:**
- `1 <= n <= 2000`
- `0 <= prerequisites.length <= n * (n - 1) / 2`

---

## Solution: Kahn's Algorithm (BFS Topological Sort)

### Intuition

Kahn's algorithm computes in-degree for each node, processes nodes with zero in-degree in a queue, decreases in-degree of neighbors, and records the order.

```java
import java.util.*;

/**
 * LeetCode 210 — Course Schedule II
 *
 * Kahn's algorithm for topological sort.
 *
 * Time: O(V + E) | Space: O(V + E)
 */
public class CourseScheduleII {

    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<Integer>[] graph = new List[numCourses];
        int[] indegree = new int[numCourses];
        for (int i = 0; i < numCourses; i++) graph[i] = new ArrayList<>();

        for (int[] preq : prerequisites) {
            int course = preq[0], prereq = preq[1];
            graph[prereq].add(course);
            indegree[course]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) queue.offer(i);
        }

        int[] order = new int[numCourses];
        int idx = 0;

        while (!queue.isEmpty()) {
            int cur = queue.poll();
            order[idx++] = cur;
            for (int next : graph[cur]) {
                indegree[next]--;
                if (indegree[next] == 0) queue.offer(next);
            }
        }

        return idx == numCourses ? order : new int[0];
    }

    public static void main(String[] args) {
        CourseScheduleII s = new CourseScheduleII();

        // Test 1: Standard case
        int[][] t1 = {{1,0}};
        System.out.println("Test 1: " + Arrays.toString(s.findOrder(2, t1))
            + " (expected: [0, 1])");

        // Test 2: Two valid orderings
        int[][] t2 = {{1,0},{2,0},{3,1},{3,2}};
        System.out.println("Test 2: " + Arrays.toString(s.findOrder(4, t2))
            + " (expected: [0, 1, 2, 3] or [0, 2, 1, 3])");

        // Test 3: Cycle (impossible)
        int[][] t3 = {{1,0},{0,1}};
        System.out.println("Test 3: " + Arrays.toString(s.findOrder(2, t3))
            + " (expected: [])");

        // Test 4: No prerequisites
        int[][] t4 = {};
        System.out.println("Test 4: " + Arrays.toString(s.findOrder(3, t4))
            + " (expected: any permutation of [0, 1, 2])");

        // Test 5: Single course
        int[][] t5 = {};
        System.out.println("Test 5: " + Arrays.toString(s.findOrder(1, t5))
            + " (expected: [0])");
    }
}
```

---

## Alternative: DFS with Post-order

```java
import java.util.*;

/**
 * DFS-based topological sort for LeetCode 210.
 * Uses a stack to store post-order and detects cycles via states.
 *
 * Time: O(V + E) | Space: O(V + E)
 */
public class CourseScheduleIIDFS {

    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<Integer>[] graph = new List[numCourses];
        for (int i = 0; i < numCourses; i++) graph[i] = new ArrayList<>();
        for (int[] p : prerequisites) graph[p[1]].add(p[0]);

        int[] state = new int[numCourses]; // 0=unvisited, 1=visiting, 2=done
        List<Integer> order = new ArrayList<>();

        for (int i = 0; i < numCourses; i++) {
            if (hasCycle(graph, state, order, i)) return new int[0];
        }

        int[] result = new int[numCourses];
        for (int i = 0; i < numCourses; i++)
            result[i] = order.get(numCourses - 1 - i);
        return result;
    }

    private boolean hasCycle(List<Integer>[] graph, int[] state,
                              List<Integer> order, int node) {
        if (state[node] == 1) return true;
        if (state[node] == 2) return false;
        state[node] = 1;
        for (int next : graph[node]) {
            if (hasCycle(graph, state, order, next)) return true;
        }
        state[node] = 2;
        order.add(node);
        return false;
    }

    public static void main(String[] args) {
        CourseScheduleIIDFS s = new CourseScheduleIIDFS();
        System.out.println(Arrays.toString(s.findOrder(2, new int[][]{{1,0}}))
            + " (expected: [0, 1])");
    }
}
```

## Complexity Comparison

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Kahn's (BFS) | O(V + E) | O(V + E) | Iterative, natural ordering |
| DFS Post-order | O(V + E) | O(V + E) | Recursive, detects cycles via states |

Both are O(V + E). Kahn's is generally preferred for its iterative nature and clearer ordering logic.