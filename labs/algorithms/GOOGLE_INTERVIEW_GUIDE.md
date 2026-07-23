# Google Interview Guide — Algorithms Academy

## Interview Process for Algorithm-Heavy Roles
- **Rounds**: 4-5 rounds total: 2-3 technical algorithm rounds, 1 system design, 1 behavioral (Googleyness). Each technical round is 45 minutes.
- **Timeline**: Phone screen (1 round) → Onsite (4-5 rounds). Feedback process takes 1-2 weeks.
- **Algorithm Difficulty**: Varies from Medium to Hard LC. Focus on DP, Graphs, Arrays, Strings, and Recursion. Google values optimal solutions with clean code.
- **How algorithm-heavy?**: Extremely heavy. 80% of technical rounds are pure algorithm and data structure problems. Expect at least 2-3 algorithm-heavy rounds.

## Top Problems by Algorithm Category

### Category: Dynamic Programming
#### Problem: Coin Change (LC 322)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Given an array of coin denominations and a total amount, return the fewest number of coins needed to make that amount. If impossible, return -1.
- **Interview walkthrough**: Start by understanding the problem: we need minimum coins. Recognize optimal substructure: the optimal solution for amount `a` uses one coin plus the optimal solution for `a - coin`. Derive DP formula: `dp[i] = min(dp[i], dp[i - coin] + 1)`. Initialize `dp[0] = 0` and all others to infinity. Iterate over amounts, then coins. Return `dp[amount]` if not infinity.
- **Solution approaches**: Brute force recursion O(2^n) — explore all combinations. Top-down memoization O(n * amount). Bottom-up DP O(n * amount) time, O(amount) space. For follow-ups, BFS can be used to find shortest path in state space.
- **Java code**:
```java
import java.util.Arrays;

/**
 * Solution for Coin Change problem.
 * Computes minimum number of coins to make a given amount.
 */
public class CoinChange {

    /**
     * Returns the minimum number of coins needed to make up the amount.
     *
     * @param coins  array of coin denominations
     * @param amount target amount
     * @return minimum coins needed, or -1 if impossible
     */
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }
}
```
- **What Google evaluates**: Whether you recognize optimal substructure, ability to optimize from recursion to DP, handling of edge cases (amount = 0, impossible amounts).
- **Follow-ups**: Print the coin combination used. What if each coin can be used at most once (0/1 knapsack variant)? What if amounts are very large, requiring mathematical optimization?

#### Problem: Longest Increasing Subsequence (LC 300)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given an unsorted array of integers, find the length of the longest strictly increasing subsequence.
- **Interview walkthrough**: Clarify that subsequence is not contiguous. Discuss DP: `dp[i]` = length of LIS ending at index i. For each i, check all j < i and update if nums[j] < nums[i]. Then optimize with patience sorting using binary search for O(n log n).
- **Solution approaches**: O(n^2) DP using nested loops. O(n log n) using binary search on a tails array: maintain the smallest possible tail for each length. Google expects the optimized version.
- **Java code**:
```java
/**
 * Solution for Longest Increasing Subsequence.
 */
public class LongestIncreasingSubsequence {

    /**
     * Returns the length of the longest strictly increasing subsequence.
     *
     * @param nums input array
     * @return length of LIS
     */
    public int lengthOfLIS(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;
        for (int x : nums) {
            int i = 0, j = size;
            while (i < j) {
                int m = (i + j) / 2;
                if (tails[m] < x) {
                    i = m + 1;
                } else {
                    j = m;
                }
            }
            tails[i] = x;
            if (i == size) {
                size++;
            }
        }
        return size;
    }
}
```
- **What Google evaluates**: Ability to identify DP, then push for nlogn optimization. Clarity of binary search reasoning.
- **Follow-ups**: Return the actual subsequence. What if the array is circular? What about non-strictly increasing (allow equals)?

#### Problem: Edit Distance (LC 72)
- **Difficulty/Frequency**: Hard / High
- **Problem statement**: Given two strings, return the minimum number of operations (insert, delete, replace) to convert one string to the other.
- **Interview walkthrough**: Classic DP on strings. Define `dp[i][j]` = min edit distance between first i chars of word1 and first j chars of word2. If chars match, `dp[i][j] = dp[i-1][j-1]`. Otherwise, min of insert, delete, replace.
- **Solution approaches**: 2D DP O(m*n) time and space. Optimize to O(n) space using only two rows.
- **Java code**:
```java
/**
 * Solution for Edit Distance.
 */
public class EditDistance {

    /**
     * Returns minimum edit distance between two words.
     *
     * @param word1 first string
     * @param word2 second string
     * @return minimum number of operations
     */
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[] prev = new int[n + 1];
        for (int j = 0; j <= n; j++) {
            prev[j] = j;
        }
        for (int i = 1; i <= m; i++) {
            int[] curr = new int[n + 1];
            curr[0] = i;
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    curr[j] = prev[j - 1];
                } else {
                    curr[j] = 1 + Math.min(curr[j - 1],
                            Math.min(prev[j], prev[j - 1]));
                }
            }
            prev = curr;
        }
        return prev[n];
    }
}
```
- **What Google evaluates**: DP formulation reasoning, space optimization, base case handling.
- **Follow-ups**: Output the actual edits. What if only insert and delete are allowed (no replace)?

### Category: Graphs
#### Problem: Number of Islands (LC 200)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Given a 2D grid of '1's (land) and '0's (water), count the number of islands. An island is surrounded by water and formed by connecting adjacent lands horizontally or vertically.
- **Interview walkthrough**: Discuss DFS traversal. Every time we see a '1', increment count and recursively visit all connected '1's, marking them as visited. Alternatively use BFS or Union-Find.
- **Solution approaches**: DFS O(m*n). BFS O(m*n) using queue. Union-Find O(m*n * α(m*n)). Google usually expects DFS first, then discuss alternatives.
- **Java code**:
```java
/**
 * Solution for Number of Islands.
 */
public class NumberOfIslands {

    /**
     * Counts the number of islands in a 2D grid.
     *
     * @param grid 2D char array of '1' and '0'
     * @return number of islands
     */
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        int m = grid.length, n = grid[0].length, count = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    dfs(grid, i, j);
                }
            }
        }
        return count;
    }

    private void dfs(char[][] grid, int i, int j) {
        if (i < 0 || i >= grid.length || j < 0
                || j >= grid[0].length || grid[i][j] == '0') {
            return;
        }
        grid[i][j] = '0';
        dfs(grid, i - 1, j);
        dfs(grid, i + 1, j);
        dfs(grid, i, j - 1);
        dfs(grid, i, j + 1);
    }
}
```
- **What Google evaluates**: Graph traversal basics, in-place marking vs visited array, recursion depth considerations.
- **Follow-ups**: Count islands of different shapes. Max area of an island. Number of distinct islands (canonical forms).

#### Problem: Course Schedule (LC 207)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given numCourses and prerequisites pairs, determine if it is possible to finish all courses.
- **Interview walkthrough**: This is cycle detection in a directed graph. Build adjacency list, then run DFS with three-color marking (0=unvisited, 1=visiting, 2=visited). If we encounter a node in visiting state, there is a cycle. Alternatively, use Kahn's algorithm (topological sort with indegree array and queue).
- **Solution approaches**: DFS cycle detection O(V+E). Topological sort with indegree O(V+E). Google accepts both but expects discussion of trade-offs.
- **Java code**:
```java
import java.util.ArrayList;
import java.util.List;

/**
 * Solution for Course Schedule using DFS cycle detection.
 */
public class CourseSchedule {

    /**
     * Determines if all courses can be finished given prerequisites.
     *
     * @param numCourses    number of courses
     * @param prerequisites array of prerequisite pairs
     * @return true if possible to finish all courses
     */
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] p : prerequisites) {
            graph.get(p[1]).add(p[0]);
        }
        int[] visited = new int[numCourses];
        for (int i = 0; i < numCourses; i++) {
            if (!dfs(graph, visited, i)) return false;
        }
        return true;
    }

    private boolean dfs(List<List<Integer>> graph, int[] visited, int c) {
        if (visited[c] == 1) return false;
        if (visited[c] == 2) return true;
        visited[c] = 1;
        for (int neighbor : graph.get(c)) {
            if (!dfs(graph, visited, neighbor)) return false;
        }
        visited[c] = 2;
        return true;
    }
}
```
- **What Google evaluates**: Graph modeling skill, cycle detection knowledge, clear recursion reasoning.
- **Follow-ups**: Return the topological order. What if prerequisites have weights (minimum time needed per course)?

### Category: Arrays and Strings
#### Problem: Container With Most Water (LC 11)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given an array of heights, find two lines that together with the x-axis form a container that holds the most water.
- **Interview walkthrough**: Two-pointer approach. Start left at 0, right at n-1. Calculate area as `min(height[l], height[r]) * (r - l)`. Move the pointer with the smaller height inward. Track max area.
- **Solution approaches**: Brute force O(n^2). Two-pointer O(n). The proof: narrowing the width reduces area, so only moving the smaller height can increase the area.
- **Java code**:
```java
/**
 * Solution for Container With Most Water.
 */
public class ContainerWithMostWater {

    /**
     * Returns max area of water a container can hold.
     *
     * @param height array of heights
     * @return maximum area
     */
    public int maxArea(int[] height) {
        int left = 0, right = height.length - 1, max = 0;
        while (left < right) {
            int area = Math.min(height[left], height[right])
                    * (right - left);
            max = Math.max(max, area);
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return max;
    }
}
```
- **What Google evaluates**: Two-pointer intuition, proof of correctness, edge cases (all equal heights, ascending, descending).
- **Follow-ups**: What if the container is 3D (rain water trapping)? What if we need to find the largest area among any pair of lines?

#### Problem: Longest Substring Without Repeating Characters (LC 3)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Given a string, find the length of the longest substring without repeating characters.
- **Interview walkthrough**: Sliding window with character index map. Expand right pointer, if char is seen within the window, move left pointer past its last occurrence. Track max length.
- **Solution approaches**: Brute force O(n^3). Sliding window with set O(2n). Optimized sliding window with map O(n). Google expects the optimized map version.
- **Java code**:
```java
import java.util.HashMap;
import java.util.Map;

/**
 * Solution for Longest Substring Without Repeating Characters.
 */
public class LongestSubstring {

    /**
     * Returns length of longest substring without repeating chars.
     *
     * @param s input string
     * @return length of longest substring
     */
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> map = new HashMap<>();
        int max = 0, left = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (map.containsKey(c)) {
                left = Math.max(left, map.get(c) + 1);
            }
            map.put(c, right);
            max = Math.max(max, right - left + 1);
        }
        return max;
    }
}
```
- **What Google evaluates**: Sliding window technique, map usage for O(1) lookups, handling string with all unique vs all same chars.
- **Follow-ups**: Longest substring with at most K distinct characters. Longest substring with at most two distinct characters.

### Category: Recursion and Backtracking
#### Problem: Word Search (LC 79)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given a 2D board and a word, determine if the word exists in the grid. The word can be constructed from adjacent cells (horizontal or vertical).
- **Interview walkthrough**: DFS with backtracking. For each cell matching first char, run DFS exploring four directions. Mark visited by modifying board (or using a visited array). Unmark after exploring.
- **Solution approaches**: Backtracking O(m*n * 4^L) where L = word length. Pruning with trie for multiple words. Google expects clean recursion with proper backtracking.
- **Java code**:
```java
/**
 * Solution for Word Search.
 */
public class WordSearch {

    /**
     * Determines if a word exists in the given board.
     *
     * @param board 2D char array
     * @param word  target word
     * @return true if word exists
     */
    public boolean exist(char[][] board, String word) {
        int m = board.length, n = board[0].length;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (backtrack(board, word, i, j, 0)) return true;
            }
        }
        return false;
    }

    private boolean backtrack(char[][] board, String word,
                              int i, int j, int index) {
        if (index == word.length()) return true;
        if (i < 0 || i >= board.length || j < 0
                || j >= board[0].length
                || board[i][j] != word.charAt(index)) {
            return false;
        }
        char temp = board[i][j];
        board[i][j] = '#';
        boolean found = backtrack(board, word, i + 1, j, index + 1)
                || backtrack(board, word, i - 1, j, index + 1)
                || backtrack(board, word, i, j + 1, index + 1)
                || backtrack(board, word, i, j - 1, index + 1);
        board[i][j] = temp;
        return found;
    }
}
```
- **What Google evaluates**: Clean backtracking, in-place marking to avoid extra memory, early termination conditions.
- **Follow-ups**: Word Search II (multiple words with trie). Return all occurrences. What if diagonal moves are allowed?

### Category: Sorting and Searching
#### Problem: Kth Largest Element in an Array (LC 215)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Find the kth largest element in an unsorted array. Note it is the kth largest in sorted order, not the kth distinct.
- **Interview walkthrough**: Discuss QuickSelect (Hoare's selection algorithm). Average O(n), worst O(n^2). Alternatively, use a min-heap of size k. Google expects QuickSelect for optimal average case.
- **Solution approaches**: Sort O(n log n). Min-heap O(n log k). QuickSelect O(n) average. Google may ask for the partition logic.
- **Java code**:
```java
import java.util.Random;

/**
 * Solution for Kth Largest Element using QuickSelect.
 */
public class KthLargest {

    private Random rand = new Random();

    /**
     * Returns the kth largest element in the array.
     *
     * @param nums input array
     * @param k    rank (1-indexed)
     * @return kth largest element
     */
    public int findKthLargest(int[] nums, int k) {
        int left = 0, right = nums.length - 1;
        int target = nums.length - k;
        while (left <= right) {
            int pivot = partition(nums, left, right);
            if (pivot == target) return nums[pivot];
            if (pivot < target) {
                left = pivot + 1;
            } else {
                right = pivot - 1;
            }
        }
        return -1;
    }

    private int partition(int[] nums, int left, int right) {
        int p = left + rand.nextInt(right - left + 1);
        swap(nums, p, right);
        int pivot = nums[right];
        int i = left;
        for (int j = left; j < right; j++) {
            if (nums[j] <= pivot) {
                swap(nums, i++, j);
            }
        }
        swap(nums, i, right);
        return i;
    }

    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
}
```
- **What Google evaluates**: QuickSelect algorithm understanding, pivot selection strategies, handling duplicates.
- **Follow-ups**: Kth smallest element. Find median using similar approach. What if the array is too large for memory (streaming)?

## Company-Specific Algorithm Focus
Google places heavy emphasis on **Dynamic Programming** and **Graphs**. Their interviewers believe DP tests problem decomposition skills, and graphs test system modeling ability. They also love **arrays/strings** with two-pointer and sliding window techniques. Google rarely asks pure math or number theory problems. They value clean, readable code above all.

## System Design with Algorithms
1. **Design Google Maps** — Requires Dijkstra/A* for shortest path, graph traversal for road networks, and quadtrees for spatial indexing. You must discuss time complexity of pathfinding at Google scale.
2. **Design Search Autocomplete** — Requires trie construction and traversal for prefix matching. Discuss top-k retrieval using heap or bucket sort, and rank aggregation algorithms.

## Behavioral Questions
1. **Tell me about a time you designed a complex algorithm**: I optimized a caching layer by implementing LRU using a doubly linked list and hashmap, reducing average lookup time from O(n) to O(1). This required trading space for time and analyzing access patterns.
2. **Tell me about a time you handled a trade-off**: I chose between BFS and DFS for analyzing a dependency graph. I selected DFS with topological sort because it used less memory for deep trees and naturally detected cycles, which was critical for the build system.
3. **Describe a time you made a technical decision**: I decided to use a segment tree over a Fenwick tree for range queries because we needed both range sum and range minimum queries. This added implementation complexity but reduced total query time by 40%.
4. **Tell me about a time you had conflicting data**: Two algorithms gave different results for community detection. I built a statistical significance test using permutation testing to validate which was correct, then documented the methodology for the team.
5. **Describe a time you were wrong**: I initially proposed a greedy algorithm for a scheduling problem that turned out to be incorrect. I walked through counterexamples and pivoted to a DP solution. I learned to always prove greedy correctness before implementation.

## Study Plan
Prioritize these labs in order:
1. Lab 4: Dynamic Programming I (coin change, edit distance)
2. Lab 5: Dynamic Programming II (LIS, knapSack)
3. Lab 7: Graph Algorithms I (DFS, BFS, topological sort)
4. Lab 8: Graph Algorithms II (Dijkstra, Bellman-Ford)
5. Lab 10: Array and String Algorithms
6. Lab 12: Recursion and Backtracking
7. Lab 14: Sorting and Searching

## Tips
- Google interviewers care deeply about **code correctness**. Write compilable, bug-free code on the first attempt.
- Always **explain your thought process** out loud before writing code. They evaluate problem-solving as much as the final solution.
- **Start with brute force** then optimize. Google wants to see you iterate on solutions.
- Use **meaningful variable names** and keep your code clean. Google engineers are sticklers for readability.
- For DP problems, always write the **recurrence relation** on the whiteboard before coding.
- Practice writing code without an IDE — google docs style — since the interview uses a shared doc.
- Expect **follow-ups that increase constraints** (e.g., "now the input is too large for memory").
