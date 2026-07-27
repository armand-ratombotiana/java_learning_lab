# Problem Walkthrough: 05-Graphs

## Problem 1: Number of Islands (LC 200) — Amazon

### Interview Scenario
"Amazon interviewer: 'Given an m x n 2D binary grid where '1' is land and '0' is water, count the number of islands.' An island is surrounded by water and formed by connecting adjacent land cells horizontally or vertically."

### The Problem
Count connected components of '1's in a grid. Connection is 4-directional (up, down, left, right).

### Step 1: Clarify (30 seconds)
- **Q:** Can the grid be empty? **A:** Yes, return 0.
- **Q:** Diagonal connections? **A:** No, only horizontal/vertical.
- **Q:** Can I modify the input grid? **A:** Yes, or use a visited set.
- **Q:** Grid dimensions? **A:** Up to 300 x 300.
- **Edge cases:** Empty grid, all water, all land, single cell, checkerboard pattern.

### Step 2: Brute Force (2 min)
- For each cell with '1', BFS/DFS to mark the entire island visited. Count each time we start a new BFS.
- This IS the actual solution — for graphs, the brute force is often close to optimal.

### Step 3: Optimize (5 min)
- "DFS approach: iterate over every cell. When we find a '1', increment count and run DFS to sink the entire island (marking visited). DFS explores all 4 neighbors recursively."
- O(m * n) time since each cell is visited once. O(m * n) worst-case space for recursion stack (if all land).
- **Why Amazon values this:** Grid-based connected component analysis is used in AWS image recognition, map services, and fraud detection.

### Step 4: Code (10 min)

```java
/**
 * Counts the number of islands in a 2D binary grid.
 * <p>
 * Time: O(m * n) | Space: O(m * n)
 */
public class Solution {
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;

        int m = grid.length, n = grid[0].length;
        int count = 0;

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    dfs(grid, r, c);
                }
            }
        }

        return count;
    }

    private void dfs(char[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length
                || grid[r][c] != '1') {
            return;
        }

        grid[r][c] = '0'; // sink the island

        dfs(grid, r - 1, c);
        dfs(grid, r + 1, c);
        dfs(grid, r, c - 1);
        dfs(grid, r, c + 1);
    }
}
```

### Step 5: Test (3 min)
- **Edge:** grid = [] → 0
- **Example:** Single island in a 4x5 grid → 1
- **Example:** grid = [['1','1','0','0','0'],['1','1','0','0','0'],['0','0','1','0','0'],['0','0','0','1','1']] → 3
- **Corner:** grid = [['1']] → 1
- **Corner:** grid = [['0']] → 0
- Walk through the recursion path on one island.

### Step 6: Follow-ups
- "Largest island (by area)?" — Track size during DFS, return max (LC 695).
- "Number of distinct islands (different shapes)?" — Record the DFS path as a signature, store in set (LC 694).
- "What if the grid is too large for recursion?" — Use iterative BFS with a queue or explicit stack.
- **What Amazon looks for:** Can you adapt the pattern to variants? Do you handle recursion limits?

### Company Evaluation Criteria
- **Amazon:** Scalability and correctness. They'd ask about space optimization (sink vs. visited set).
- **Google:** Would ask about counting islands in a dynamically changing grid.
- **Meta:** Would ask about union-find solution.

---

## Problem 2: Clone Graph (LC 133) — Meta

### Interview Scenario
"Meta interviewer: 'Given a reference to a node in a connected undirected graph, return a deep copy of the graph.'"

### The Problem
Each Node contains a value and a list of neighbors. Create a completely independent copy of the entire graph structure.

### Step 1: Clarify (30 seconds)
- **Q:** Are there cycles? **A:** Yes, the graph may have cycles.
- **Q:** Is the graph connected? **A:** Yes, it's given as a connected graph.
- **Q:** Can nodes have duplicate values? **A:** Values are unique (equal to the node's index).
- **Edge cases:** Single node with no neighbors, two nodes connected to each other, complex cycles.

### Step 2: Brute Force (2 min)
- Serialize and deserialize: convert graph to JSON string, then parse it back. Not practical.
- **Time:** O(n) — but serialization is overly complex.
- **Space:** O(n).

### Step 3: Optimize (5 min)
- "Use a HashMap to map original nodes to cloned nodes. DFS from the given node. For each neighbor, either return the existing clone (if already visited) or create a new clone and recursively clone its neighbors."
- O(n + e) time where n is nodes and e is edges. O(n) space for the map + recursion stack.
- **Why Meta values this:** Deep copying is fundamental — Facebook/Meta uses it for state management, immutable data structures, and React reconciliation.

### Step 4: Code (10 min)

```java
import java.util.HashMap;
import java.util.Map;

/**
 * Deep copies a connected undirected graph.
 * <p>
 * Time: O(n + e) | Space: O(n)
 */
public class Solution {
    private Map<Node, Node> visited = new HashMap<>();

    public Node cloneGraph(Node node) {
        if (node == null) return null;

        if (visited.containsKey(node)) {
            return visited.get(node);
        }

        Node clone = new Node(node.val);
        visited.put(node, clone);

        for (Node neighbor : node.neighbors) {
            clone.neighbors.add(cloneGraph(neighbor));
        }

        return clone;
    }
}
```

### Step 5: Test (3 min)
- **Edge:** node = null → null
- **Example:** Single node with no neighbors → new node with same val, empty list
- **Example:** Two nodes [[2], [1]] → both cloned with cross-references preserved
- Show how cycles are handled by the visited map.

### Step 6: Follow-ups
- "Iterative BFS approach?" — Same visited map, use a queue to process nodes level by level.
- "Deep copy with random pointer (like LC 138)?" — Same pattern, but map old to new, two passes.
- "What if the graph is disconnected?" — Need to collect all nodes first, then copy each component.
- **What Meta looks for:** Reference handling. Many candidates miss that you need to create all neighbors of a clone, not just the node itself.

### Company Evaluation Criteria
- **Meta:** Pointer/reference management. They love graph problems that test object relationships.
- **Amazon:** Would ask about detecting whether two graphs are isomorphic.
- **Google:** Would ask about copying an undirected weighted graph.

---

## Problem 3: Course Schedule (LC 207) — Google

### Interview Scenario
"Google interviewer: 'There are n courses labeled 0 to n-1. You are given prerequisites where prerequisites[i] = [a, b] means you must take course b before course a. Can you finish all courses?'"

### The Problem
Determine if a directed graph of course prerequisites has a cycle. If there's a cycle, it's impossible.

### Step 1: Clarify (30 seconds)
- **Q:** Can there be duplicate prerequisites? **A:** No.
- **Q:** Course range? **A:** 0 to n-1, n up to 2000.
- **Q:** Can a course be its own prerequisite? **A:** No.
- **Edge cases:** No prerequisites (true), single course, linear chain, cycle, disconnected graph.

### Step 2: Brute Force (2 min)
- For each course, DFS to see if you can return to it. This is essentially the optimal approach but naive implementation might be O(n²).

### Step 3: Optimize (5 min)
- "Convert to adjacency list. Use Kahn's algorithm (BFS topological sort): compute indegree of each node. Start with nodes of indegree 0. Process them, decrement neighbors' indegrees. If count of processed nodes equals n, no cycle exists."
- OR use DFS with three-color marking (0=unvisited, 1=visiting, 2=visited).
- O(n + e) time, O(n + e) space for adjacency list.
- **Why Google values this:** Topological sort is a core algorithm. Google uses dependency resolution in build systems (Bazel), job scheduling, and distributed task management.

### Step 4: Code (10 min)

```java
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Determines if all courses can be finished given prerequisites.
 * <p>
 * Time: O(n + e) | Space: O(n + e)
 */
public class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<Integer>[] graph = new ArrayList[numCourses];
        int[] indegree = new int[numCourses];

        for (int i = 0; i < numCourses; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int[] prereq : prerequisites) {
            int course = prereq[0];
            int prereqCourse = prereq[1];
            graph[prereqCourse].add(course);
            indegree[course]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
            }
        }

        int processed = 0;
        while (!queue.isEmpty()) {
            int course = queue.poll();
            processed++;
            for (int neighbor : graph[course]) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        return processed == numCourses;
    }
}
```

### Step 5: Test (3 min)
- **Edge:** numCourses = 1, prerequisites = [] → true
- **Example:** numCourses = 2, prerequisites = [[1, 0]] → true
- **Example:** numCourses = 2, prerequisites = [[1, 0], [0, 1]] → false (cycle)
- **Example:** numCourses = 4, prerequisites = [[1, 0], [2, 1], [3, 2]] → true (linear chain)
- Show processed count and queue state.

### Step 6: Follow-ups
- "Return the order of courses (LC 210: Course Schedule II)?" — Return processed list instead of count.
- "What if courses have multiple times/semesters?" — Add weight to edges, find minimum semesters (LC 1136).
- "Detect cycle in a directed graph — DFS three-color approach?" — Implement and compare with Kahn's.
- **What Google looks for:** Can you implement both Kahn's and DFS cycle detection? Do you understand the trade-off (Kahn's gives topological order, DFS detects cycles with less code)?

### Company Evaluation Criteria
- **Google:** Algorithm choice and justification. Why Kahn's over DFS? How would you modify for different constraints?
- **Amazon:** Would ask about parallel course scheduling (minimum time).
- **Meta:** Would ask about longest path in a DAG.

---

## Study Notes

### Key Patterns
- **Grid DFS:** Connected components, island variations, flood fill
- **Graph deep copy:** HashMap + recursive/iterative traversal
- **Topological sort (Kahn's):** Indegree-based BFS for dependency resolution
- **DFS cycle detection:** Three-color marking for directed graphs
- **Union-Find (Disjoint Set):** Connected components in undirected graphs, Kruskal's MST

### Common Mistakes
- Not handling cycles in graph traversal (infinite recursion)
- Modifying input when asked not to (ask first)
- Forgetting visited set in undirected graph BFS/DFS
- Confusing indegree and outdegree in topological sort
- Stack overflow on deep recursion in large graphs (use BFS/iterative)

### Time Complexity Cheat Sheet
| Pattern | Time | Space |
|---|---|---|
| Grid DFS/BFS | O(m * n) | O(m * n) |
| Graph DFS (clone) | O(n + e) | O(n) |
| Kahn's topological sort | O(n + e) | O(n + e) |
| DFS cycle detection | O(n + e) | O(n) |
| Dijkstra | O((n + e) log n) | O(n) |
| Union-Find | O(α(n)) per op | O(n) |
