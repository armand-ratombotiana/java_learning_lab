# Palantir Interview Guide — Algorithms Academy

## Interview Process for Algorithm-Heavy Roles
- **Rounds**: Recruiter screen, 1-2 technical phone screens (60 min each), onsite (5-6 rounds, 45-60 min each): 2-3 algorithm/coding, 1 system design, 1 decomposition, 1 behavioral. Palantir has a unique "decomp" round where you decompose a vague, ambiguous problem.
- **Timeline**: Recruiter (1 week), phone screen (1-2 weeks), onsite (2-4 weeks). Decision within 1-2 weeks.
- **Algorithm Difficulty**: LeetCode Medium to Hard. Palantir emphasizes graph algorithms, data modeling, and problem decomposition. They value Decomposition and Design (DD) as much as raw algorithm skill.
- **How algorithm-heavy?**: Heavy, but intertwined with system thinking. 60% of rounds are algorithm-focused but framed as real-world analysis problems. Palantir wants to see you handle ambiguity and constraint discovery.

## Top Problems by Algorithm Category

### Category: Graphs and Flow
#### Problem: Course Schedule II (LC 210)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Return the ordering of courses to take given prerequisites (topological order).
- **Interview walkthrough**: Kahn's algorithm using indegree array and queue. Build adjacency list, compute indegree for each course. Queue courses with indegree 0. Process queue, decrement indegree of neighbors. If processed count less than numCourses, return empty array (cycle detected).
- **Solution approaches**: Kahn's O(V+E). DFS post-order O(V+E). Palantir expects Kahn's algorithm.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Course Schedule II using Kahn's algorithm.
 */
public class CourseScheduleII {

    /**
     * Returns topological ordering of courses.
     *
     * @param numCourses    number of courses
     * @param prerequisites array of [course, prerequisite] pairs
     * @return ordering of courses, or empty if cycle exists
     */
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        int[] indegree = new int[numCourses];
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] p : prerequisites) {
            graph.get(p[1]).add(p[0]);
            indegree[p[0]]++;
        }
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) queue.offer(i);
        }
        int[] order = new int[numCourses];
        int idx = 0;
        while (!queue.isEmpty()) {
            int c = queue.poll();
            order[idx++] = c;
            for (int neighbor : graph.get(c)) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) queue.offer(neighbor);
            }
        }
        return idx == numCourses ? order : new int[0];
    }
}
```
- **What Palantir evaluates**: Graph modeling for dependency resolution, cycle detection for constraint validation, understanding of partial orderings.
- **Follow-ups**: Minimum height trees. Sequence reconstruction. Parallel course scheduling (minimum semesters).

#### Problem: Network Delay Time (LC 743)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given a network of N nodes and travel times for edges, find the minimum time for a signal to reach all nodes starting from K.
- **Interview walkthrough**: Dijkstra's algorithm from source K. Use priority queue (min-heap). Track distances. After processing all reachable nodes, return max distance if all nodes reached, else -1.
- **Solution approaches**: Dijkstra O(E log V). Bellman-Ford O(VE). Floyd-Warshall O(V^3). Palantir expects Dijkstra.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Network Delay Time using Dijkstra.
 */
public class NetworkDelayTime {

    /**
     * Returns minimum time for signal to reach all nodes.
     *
     * @param times  edge array [u, v, w]
     * @param n      number of nodes
     * @param k      source node
     * @return minimum time, or -1 if not all nodes reachable
     */
    public int networkDelayTime(int[][] times, int n, int k) {
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int[] t : times) {
            graph.computeIfAbsent(t[0],
                    x -> new ArrayList<>()).add(new int[]{t[1], t[2]});
        }
        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(
                (a, b) -> a[1] - b[1]);
        pq.offer(new int[]{k, 0});
        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int node = curr[0], time = curr[1];
            if (time > dist[node]) continue;
            if (!graph.containsKey(node)) continue;
            for (int[] edge : graph.get(node)) {
                int next = edge[0], weight = edge[1];
                if (dist[node] + weight < dist[next]) {
                    dist[next] = dist[node] + weight;
                    pq.offer(new int[]{next, dist[next]});
                }
            }
        }
        int max = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] == Integer.MAX_VALUE) return -1;
            max = Math.max(max, dist[i]);
        }
        return max;
    }
}
```
- **What Palantir evaluates**: Dijkstra's algorithm, graph construction, priority queue proficiency, all-nodes-reachable check.
- **Follow-ups**: Cheapest flights within K stops (Bellman-Ford). Find the city with the smallest number of neighbors at a threshold distance.

#### Problem: Max Area of Island (LC 695)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given a 2D grid of 0s and 1s, find the maximum area of an island (connected 1s).
- **Interview walkthrough**: BFS or DFS on every unvisited 1. Count area of each island. Track max. Use iterative BFS to avoid stack overflow on large grids.
- **Solution approaches**: DFS recursion O(m*n). BFS iterative O(m*n). Palantir expects BFS.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Max Area of Island.
 */
public class MaxAreaOfIsland {

    /**
     * Returns the maximum area of an island in the grid.
     *
     * @param grid 2D binary grid
     * @return maximum island area
     */
    public int maxAreaOfIsland(int[][] grid) {
        int m = grid.length, n = grid[0].length, max = 0;
        int[][] dirs = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    int area = 0;
                    Queue<int[]> queue = new LinkedList<>();
                    queue.offer(new int[]{i, j});
                    grid[i][j] = 0;
                    while (!queue.isEmpty()) {
                        int[] cell = queue.poll();
                        area++;
                        for (int[] d : dirs) {
                            int r = cell[0] + d[0];
                            int c = cell[1] + d[1];
                            if (r >= 0 && r < m && c >= 0 && c < n
                                    && grid[r][c] == 1) {
                                grid[r][c] = 0;
                                queue.offer(new int[]{r, c});
                            }
                        }
                    }
                    max = Math.max(max, area);
                }
            }
        }
        return max;
    }
}
```
- **What Palantir evaluates**: Graph traversal in grids, in-place marking, BFS queue management, handling large maps.
- **Follow-ups**: Surrounding regions. Number of distinct islands with rotation. Island perimeter.

### Category: Dynamic Programming
#### Problem: Regular Expression Matching (LC 10)
- **Difficulty/Frequency**: Hard / High
- **Problem statement**: Implement regular expression matching with support for '.' and '*'.
- **Interview walkthrough**: DP where dp[i][j] = true if s[0..i] matches p[0..j]. If pattern char is '*', dp[i][j] = dp[i][j-2] (zero occurrences) OR dp[i-1][j] (one or more, if char matches). Otherwise if char matches, dp[i][j] = dp[i-1][j-1].
- **Solution approaches**: DP O(m*n). Recursion with memoization. Palantir expects the DP approach.
- **Java code**:
```java
/**
 * Solution for Regular Expression Matching.
 */
public class RegularExpressionMatching {

    /**
     * Checks if string s matches pattern p with '.' and '*'.
     *
     * @param s input string
     * @param p pattern
     * @return true if matches
     */
    public boolean isMatch(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        for (int j = 2; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 2];
            }
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char sc = s.charAt(i - 1);
                char pc = p.charAt(j - 1);
                if (pc == '*') {
                    char prev = p.charAt(j - 2);
                    dp[i][j] = dp[i][j - 2];
                    if (prev == '.' || prev == sc) {
                        dp[i][j] = dp[i][j] || dp[i - 1][j];
                    }
                } else if (pc == '.' || pc == sc) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }
        return dp[m][n];
    }
}
```
- **What Palantir evaluates**: Complex DP formulation, handling of '*' (zero vs multiple matching), edge cases (empty pattern, empty string).
- **Follow-ups**: Wildcard matching (with '?' and '*'). Longest common subsequence. Distinct subsequences.

#### Problem: Burst Balloons (LC 312)
- **Difficulty/Frequency**: Hard / Medium-High
- **Problem statement**: Given an array of balloons with coins, burst each balloon to maximize coins. When you burst balloon i, you get nums[i-1] * nums[i] * nums[i+1].
- **Interview walkthrough**: Divide and conquer DP. Add 1 at both ends. dp[i][j] = max coins from bursting all balloons between i and j (exclusive). For each k between i and j, consider k as the last balloon burst in this range.
- **Solution approaches**: DP O(n^3). Palantir expects DP with the reverse thinking approach.
- **Java code**:
```java
/**
 * Solution for Burst Balloons.
 */
public class BurstBalloons {

    /**
     * Returns maximum coins from bursting all balloons.
     *
     * @param nums array of coin values
     * @return maximum coins
     */
    public int maxCoins(int[] nums) {
        int n = nums.length;
        int[] vals = new int[n + 2];
        vals[0] = 1;
        vals[n + 1] = 1;
        for (int i = 0; i < n; i++) vals[i + 1] = nums[i];
        int[][] dp = new int[n + 2][n + 2];
        for (int len = 1; len <= n; len++) {
            for (int i = 1; i <= n - len + 1; i++) {
                int j = i + len - 1;
                for (int k = i; k <= j; k++) {
                    dp[i][j] = Math.max(dp[i][j],
                            dp[i][k - 1] + dp[k + 1][j]
                                    + vals[i - 1] * vals[k] * vals[j + 1]);
                }
            }
        }
        return dp[1][n];
    }
}
```
- **What Palantir evaluates**: Advanced DP reasoning (reverse thinking — last balloon burst), interval DP structure, optimal substructure identification.
- **Follow-ups**: Minimum cost to merge stones. Remove boxes. Strange printer.

### Category: Problem Decomposition
#### Problem: LRU Cache (LC 146)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Design and implement an LRU (Least Recently Used) cache with get and put in O(1).
- **Interview walkthrough**: Use doubly linked list + HashMap. Map key to node. Get: move accessed node to head. Put: if exists, update value and move to head. If full, remove tail node and add new node at head.
- **Solution approaches**: LinkedHashMap in Java. Custom doubly linked list + HashMap. Palantir expects the custom implementation to demonstrate pointer manipulation.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for LRU Cache using doubly linked list and HashMap.
 */
public class LRUCache {

    static class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private int capacity;
    private Map<Integer, Node> map;
    private Node head, tail;

    /**
     * Initializes LRU cache with given capacity.
     *
     * @param capacity max number of entries
     */
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Gets value for key, moves to front.
     *
     * @param key lookup key
     * @return value, or -1 if not found
     */
    public int get(int key) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            remove(node);
            insertToFront(node);
            return node.value;
        }
        return -1;
    }

    /**
     * Inserts or updates key-value pair.
     *
     * @param key   key
     * @param value value
     */
    public void put(int key, int value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value;
            remove(node);
            insertToFront(node);
        } else {
            if (map.size() >= capacity) {
                Node lru = tail.prev;
                remove(lru);
                map.remove(lru.key);
            }
            Node node = new Node(key, value);
            map.put(key, node);
            insertToFront(node);
        }
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void insertToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }
}
```
- **What Palantir evaluates**: Object-oriented design with algorithm efficiency, pointer manipulation, interface design, trade-offs between different implementations.
- **Follow-ups**: LFU Cache. Time-based key-value store. Design a versioned key-value store.

#### Problem: Find the Celebrity (LC 277)
- **Difficulty/Frequency**: Medium / Medium-High
- **Problem statement**: In a party of N people, find the celebrity (someone who knows nobody and everybody knows them) using minimum API calls.
- **Interview walkthrough**: Elimination approach: start with candidate 0. For each person i, if candidate knows i, candidate = i (candidate is not celebrity, i could be). After one pass, verify the candidate by checking all know relationships.
- **Solution approaches**: Brute force O(n^2). Elimination O(n) with O(n) verification. Palantir expects the two-pass elimination approach.
- **Java code**:
```java
/**
 * Solution for Find the Celebrity.
 */
public class FindCelebrity {

    private boolean knows(int a, int b) {
        return false; // stub - API call
    }

    /**
     * Returns the celebrity at the party.
     *
     * @param n number of people
     * @return celebrity label, or -1 if none
     */
    public int findCelebrity(int n) {
        int candidate = 0;
        for (int i = 1; i < n; i++) {
            if (knows(candidate, i)) {
                candidate = i;
            }
        }
        for (int i = 0; i < n; i++) {
            if (i == candidate) continue;
            if (knows(candidate, i) || !knows(i, candidate)) {
                return -1;
            }
        }
        return candidate;
    }
}
```
- **What Palantir evaluates**: Logical elimination, problem decomposition, API call optimization, verification step design.
- **Follow-ups**: Find the town judge. Find the center of star graph. Minimum API calls variant with adjacency matrix.

### Category: Backtracking and Constraints
#### Problem: N-Queens (LC 51)
- **Difficulty/Frequency**: Hard / High
- **Problem statement**: Place N queens on an NxN chessboard such that no two queens attack each other. Return all distinct solutions.
- **Interview walkthrough**: Backtracking row by row. Track columns, main diagonals (row-col), and anti-diagonals (row+col) with boolean arrays or sets. For each row, try placing queen in each valid column, recurse, backtrack.
- **Solution approaches**: Backtracking O(N!) with pruning. Palantir expects clean backtracking with efficient diagonal checking using arrays.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for N-Queens.
 */
public class NQueens {

    /**
     * Returns all distinct solutions to the N-Queens puzzle.
     *
     * @param n board size
     * @return list of solutions, each solution is list of row strings
     */
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        boolean[] cols = new boolean[n];
        boolean[] diag1 = new boolean[2 * n - 1];
        boolean[] diag2 = new boolean[2 * n - 1];
        char[][] board = new char[n][n];
        for (char[] row : board) Arrays.fill(row, '.');
        backtrack(result, board, cols, diag1, diag2, 0, n);
        return result;
    }

    private void backtrack(List<List<String>> result, char[][] board,
                           boolean[] cols, boolean[] diag1, boolean[] diag2,
                           int row, int n) {
        if (row == n) {
            List<String> solution = new ArrayList<>();
            for (char[] r : board) solution.add(new String(r));
            result.add(solution);
            return;
        }
        for (int col = 0; col < n; col++) {
            int d1 = row - col + n - 1;
            int d2 = row + col;
            if (cols[col] || diag1[d1] || diag2[d2]) continue;
            board[row][col] = 'Q';
            cols[col] = diag1[d1] = diag2[d2] = true;
            backtrack(result, board, cols, diag1, diag2, row + 1, n);
            board[row][col] = '.';
            cols[col] = diag1[d1] = diag2[d2] = false;
        }
    }
}
```
- **What Palantir evaluates**: Backtracking efficiency, constraint propagation using O(1) lookups, board representation.
- **Follow-ups**: N-Queens II (count only). Solve Sudoku. Valid Sudoku checker.

#### Problem: Letter Combinations of a Phone Number (LC 17)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Given a string of digits 2-9, return all possible letter combinations that the number could represent on a phone keypad.
- **Interview walkthrough**: Backtracking with digit-to-letters mapping. For each digit, try each corresponding letter, append to current combination, recurse on next digit, backtrack.
- **Solution approaches**: Backtracking O(4^n * n). BFS iterative. Palantir expects the backtracking approach.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Letter Combinations of a Phone Number.
 */
public class LetterCombinations {

    private static final String[] MAPPING = {
        "", "", "abc", "def", "ghi", "jkl", "mno",
        "pqrs", "tuv", "wxyz"
    };

    /**
     * Returns all letter combinations for the given digits.
     *
     * @param digits string of digits 2-9
     * @return list of letter combinations
     */
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.isEmpty()) return result;
        backtrack(result, new StringBuilder(), digits, 0);
        return result;
    }

    private void backtrack(List<String> result, StringBuilder sb,
                           String digits, int index) {
        if (index == digits.length()) {
            result.add(sb.toString());
            return;
        }
        String letters = MAPPING[digits.charAt(index) - '0'];
        for (char c : letters.toCharArray()) {
            sb.append(c);
            backtrack(result, sb, digits, index + 1);
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}
```
- **What Palantir evaluates**: Recursive enumeration, mapping representation, StringBuilder efficiency.
- **Follow-ups**: Generate all combinations of well-formed parentheses. Combination sum. Generate IP addresses.

## Company-Specific Algorithm Focus
Palantir emphasizes **graph algorithms** more than any other category — they deal with connected data, intelligence analysis, and relationship mapping. They also focus heavily on **problem decomposition** (how to break vague requirements into implementable algorithms). **Backtracking and constraint satisfaction** are also core. Palantir uniquely values the ability to go from ambiguous description to precise algorithm specification. They rarely ask pure number theory or cryptography.

## System Design with Algorithms
1. **Design a Fraud Detection System** — Requires graph-based link analysis (connected components in transaction graphs), anomaly detection using statistical outlier algorithms (z-score, moving average with thresholding), and pattern matching using finite automata for known fraud sequences. Must handle streaming data with sliding windows.
2. **Design an Intelligence Analysis Platform** — Requires entity resolution using string similarity (Levenshtein distance, Jaccard similarity), relationship extraction using graph traversal (DFS/BFS for degrees of separation), and geospatial clustering using DBSCAN for event grouping. Discuss time complexity of graph matching at intelligence scale.

## Behavioral Questions (STAR)
1. **Tell me about a time you tackled ambiguity**: A client asked for "anomaly detection" without specifying what counts as anomalous. I built a parameterized pipeline with multiple algorithms: z-score, IQR-based, and isolation forest. I then designed a feedback mechanism where analysts could label false positives, refining the detection threshold via binary search on ROC curves.
2. **Tell me about a time you handled a complex data problem**: We had 500 million records with no clear schema for entity resolution. I implemented a blocking strategy using locality-sensitive hashing (MinHash) to group similar records, then applied hierarchical clustering within each block using Jaccard distance. This reduced pairwise comparisons from O(n^2) to O(n log n).
3. **Tell me about a time you made an impact**: I optimized a batch geospatial join that was taking 8 hours. The original used nested loops for polygon containment checks. I replaced it with a quadtree indexing approach: build the tree O(n log n), query O(log n) per point. The job completed in 12 minutes.
4. **Tell me about a time you persisted through difficulty**: A constraint satisfaction algorithm for scheduling was taking exponential time. I reformulated the problem as a graph coloring problem and applied greedy coloring with Welsh-Powell ordering. Then I added simulated annealing for local optimization, reducing solve time from hours to seconds.
5. **Tell me about a time you influenced a design decision**: The team was using BFS for a graph traversal that needed to find the most probable path. I argued for A* search with a domain-specific heuristic that estimated remaining probability. I prototyped both approaches and showed that A* reduced explored nodes by 80% while maintaining optimality.

## Study Plan
Prioritize these labs in order:
1. Lab 7: Graph Algorithms I (DFS, BFS, topological sort)
2. Lab 8: Graph Algorithms II (shortest paths, MST)
3. Lab 12: Recursion and Backtracking
4. Lab 10: Backtracking and Constraint Satisfaction
5. Lab 4: Dynamic Programming I
6. Lab 5: Dynamic Programming II
7. Lab 17: Network Flow

## Tips
- Palantir's **decomp round** is unique. You will receive a vague problem description and must ask clarifying questions, define constraints, decompose the problem, and propose multiple algorithmic approaches with trade-offs.
- **Model the real world** in your algorithm design. Palantir works on intelligence, defense, and anti-fraud problems. Show how your algorithm handles noisy, incomplete, or adversarial data.
- **Prove correctness** with edge cases and invariants. Palantir interviewers are trained in formal methods and expect rigorous reasoning.
- **Discuss failure modes** of your algorithm: what happens with missing data, conflicting constraints, or very large inputs?
- Palantir values **code that tells a story**. Use clear naming, extensive comments, and decompose complex logic into well-named helper methods.
- **Be ready for coding on a whiteboard** or in a bare text editor. Palantir's onsite interviews are on whiteboards.
- **Palantir asks about data structures** that support their domain: trie for text patterns, union-find for connectivity, bloom filters for set membership at scale.
- **Optimize for readability first**, then discuss performance. Palantir collaborates heavily on code and prioritizes team readability.
