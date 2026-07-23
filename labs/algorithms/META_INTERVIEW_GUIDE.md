# Meta Interview Guide — Algorithms Academy

## Interview Process for Algorithm-Heavy Roles
- **Rounds**: 2 technical phone screens (45 min each) → Onsite: 3-4 rounds (coding, system design, behavioral). Meta now uses a "signal-based" hiring process.
- **Timeline**: Recruiter screen (1 week) → Phone screens (2 weeks) → Onsite (3 weeks). Decision within 1 week. Total ~6-8 weeks.
- **Algorithm Difficulty**: LeetCode Medium to Hard. Meta emphasizes arrays, strings, DP, recursion, and graphs. They focus on problems with multiple solution paths.
- **How algorithm-heavy?**: Very heavy. All 3-4 onsite rounds are coding with algorithms. No whiteboard — you code on a laptop in a text editor. No run button — they evaluate by reading your code.

## Top Problems by Algorithm Category

### Category: Strings
#### Problem: Valid Palindrome II (LC 680)
- **Difficulty/Frequency**: Easy / High
- **Problem statement**: Given a string, determine if it can be a palindrome after deleting at most one character.
- **Interview walkthrough**: Two-pointer from ends. When mismatch found, check if the substring without left char is palindrome OR substring without right char is palindrome. Use a helper function.
- **Solution approaches**: Brute force O(n^2) deleting each char. Two-pointer O(n). Meta expects O(n) with clear helper.
- **Java code**:
```java
/**
 * Solution for Valid Palindrome II.
 */
public class ValidPalindromeII {

    /**
     * Checks if the string can be a palindrome after deleting at most one char.
     *
     * @param s input string
     * @return true if can be palindrome
     */
    public boolean validPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return isPalindrome(s, left + 1, right)
                        || isPalindrome(s, left, right - 1);
            }
            left++;
            right--;
        }
        return true;
    }

    private boolean isPalindrome(String s, int l, int r) {
        while (l < r) {
            if (s.charAt(l++) != s.charAt(r--)) return false;
        }
        return true;
    }
}
```
- **What Meta evaluates**: Clean two-pointer logic, helper function extraction, handling of the delete-one constraint.
- **Follow-ups**: Allow k deletions. Return the palindrome if possible. Valid palindrome after removing non-alphanumeric chars.

#### Problem: Minimum Window Substring (LC 76)
- **Difficulty/Frequency**: Hard / High
- **Problem statement**: Given two strings s and t, return the minimum window in s that contains all characters of t.
- **Interview walkthrough**: Sliding window with two pointers and a frequency map. Expand right to include enough chars, then shrink left to minimize window while still meeting requirement.
- **Solution approaches**: Sliding window with hashmap O(n). Meta expects perfect implementation with detailed tracking.
- **Java code**:
```java
import java.util.HashMap;
import java.util.Map;

/**
 * Solution for Minimum Window Substring.
 */
public class MinimumWindowSubstring {

    /**
     * Returns minimum window substring containing all chars of t.
     *
     * @param s source string
     * @param t target character set
     * @return minimum window, or empty string
     */
    public String minWindow(String s, String t) {
        Map<Character, Integer> need = new HashMap<>();
        for (char c : t.toCharArray()) {
            need.put(c, need.getOrDefault(c, 0) + 1);
        }
        int left = 0, right = 0, matched = 0;
        int start = 0, minLen = Integer.MAX_VALUE;
        Map<Character, Integer> window = new HashMap<>();
        while (right < s.length()) {
            char c = s.charAt(right);
            window.put(c, window.getOrDefault(c, 0) + 1);
            if (need.containsKey(c)
                    && window.get(c).intValue() == need.get(c).intValue()) {
                matched++;
            }
            right++;
            while (matched == need.size()) {
                if (right - left < minLen) {
                    minLen = right - left;
                    start = left;
                }
                char leftChar = s.charAt(left);
                window.put(leftChar, window.get(leftChar) - 1);
                if (need.containsKey(leftChar)
                        && window.get(leftChar) < need.get(leftChar)) {
                    matched--;
                }
                left++;
            }
        }
        return minLen == Integer.MAX_VALUE ? "" : s.substring(start, start + minLen);
    }
}
```
- **What Meta evaluates**: Complex sliding window, frequency map tracking, edge cases (characters not in t), integer comparison pitfalls.
- **Follow-ups**: What if t has duplicates? Return all minimum windows. Longest substring with at least K repeating characters.

### Category: Dynamic Programming
#### Problem: Decode Ways (LC 91)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given a string of digits, return the number of ways to decode it (A=1, B=2, ..., Z=26).
- **Interview walkthrough**: DP where dp[i] = ways to decode first i chars. dp[0] = 1. For each position, check if single digit (1-9) is valid, then dp[i] += dp[i-1]. Check if two digits (10-26) are valid, then dp[i] += dp[i-2].
- **Solution approaches**: Recursion with memo O(n). Bottom-up DP O(n) time, O(1) space with rolling variables. Meta expects clean iterative DP.
- **Java code**:
```java
/**
 * Solution for Decode Ways.
 */
public class DecodeWays {

    /**
     * Returns number of ways to decode the digit string.
     *
     * @param s string of digits
     * @return number of decodings
     */
    public int numDecodings(String s) {
        if (s == null || s.length() == 0 || s.charAt(0) == '0') return 0;
        int n = s.length();
        int prev2 = 1, prev1 = 1;
        for (int i = 1; i < n; i++) {
            int current = 0;
            int oneDigit = s.charAt(i) - '0';
            if (oneDigit >= 1 && oneDigit <= 9) {
                current += prev1;
            }
            int twoDigits = Integer.parseInt(s.substring(i - 1, i + 1));
            if (twoDigits >= 10 && twoDigits <= 26) {
                current += prev2;
            }
            prev2 = prev1;
            prev1 = current;
        }
        return prev1;
    }
}
```
- **What Meta evaluates**: DP formulation, handling of leading zeros, parsing edge cases, space optimization.
- **Follow-ups**: Decode Ways II (with wildcard * character). Return the actual decoded strings. Generalization with any encoding mapping.

#### Problem: Unique Paths (LC 62)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: A robot is at top-left of an m x n grid. It can only move down or right. How many unique paths to bottom-right?
- **Interview walkthrough**: DP where dp[i][j] = ways to reach (i,j). dp[i][j] = dp[i-1][j] + dp[i][j-1]. Base case: first row and first column are all 1. Optimize to 1D array.
- **Solution approaches**: 2D DP O(m*n). 1D DP O(m*n) with O(n) space. Combinatorics (m+n-2 choose m-1) O(min(m,n)). Meta expects DP first.
- **Java code**:
```java
/**
 * Solution for Unique Paths.
 */
public class UniquePaths {

    /**
     * Returns number of unique paths from top-left to bottom-right.
     *
     * @param m rows
     * @param n columns
     * @return number of unique paths
     */
    public int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        for (int j = 0; j < n; j++) {
            dp[j] = 1;
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[j] = dp[j] + dp[j - 1];
            }
        }
        return dp[n - 1];
    }
}
```
- **What Meta evaluates**: DP space optimization, base case reasoning, combinatorial reasoning as alternative.
- **Follow-ups**: Unique Paths II (with obstacles). Minimum path sum. Unique paths with k turns allowed.

### Category: Recursion and Backtracking
#### Problem: Generate Parentheses (LC 22)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Given n pairs of parentheses, generate all well-formed combinations.
- **Interview walkthrough**: Backtracking with two counters: open and close. Always add '(' if open < n. Add ')' if close < open. When open == close == n, add to result.
- **Solution approaches**: Backtracking O(2^(2n) / sqrt(n)) — Catalan number complexity. Meta expects clean backtracking.
- **Java code**:
```java
import java.util.ArrayList;
import java.util.List;

/**
 * Solution for Generate Parentheses.
 */
public class GenerateParentheses {

    /**
     * Generates all well-formed parentheses combinations.
     *
     * @param n number of pairs
     * @return list of valid parentheses strings
     */
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(result, new StringBuilder(), 0, 0, n);
        return result;
    }

    private void backtrack(List<String> result, StringBuilder sb,
                           int open, int close, int max) {
        if (sb.length() == max * 2) {
            result.add(sb.toString());
            return;
        }
        if (open < max) {
            sb.append('(');
            backtrack(result, sb, open + 1, close, max);
            sb.deleteCharAt(sb.length() - 1);
        }
        if (close < open) {
            sb.append(')');
            backtrack(result, sb, open, close + 1, max);
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}
```
- **What Meta evaluates**: Recursive tree visualization, backtracking state management, StringBuilder efficiency vs String concatenation.
- **Follow-ups**: Generate all combinations of well-formed brackets with different bracket types. Check if a string is valid parentheses. Longest valid parentheses.

#### Problem: Combination Sum (LC 39)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given an array of distinct integers and a target, return all unique combinations that sum to target. Same number can be used unlimited times.
- **Interview walkthrough**: Sort array (optional but enables pruning). Backtracking: maintain current combination and remaining target. For each candidate, include it and recurse, then backtrack.
- **Solution approaches**: Backtracking O(2^(target/min(cand))) worst case. Meta expects clear recursion with proper pruning.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Combination Sum.
 */
public class CombinationSum {

    /**
     * Returns all unique combinations that sum to target.
     *
     * @param candidates array of distinct numbers
     * @param target     target sum
     * @return list of combinations
     */
    public List<List<Integer>> combinationSum(int[] candidates,
                                              int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), candidates, target, 0);
        return result;
    }

    private void backtrack(List<List<Integer>> result,
                           List<Integer> current, int[] candidates,
                           int remaining, int start) {
        if (remaining == 0) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remaining) continue;
            current.add(candidates[i]);
            backtrack(result, current, candidates,
                    remaining - candidates[i], i);
            current.remove(current.size() - 1);
        }
    }
}
```
- **What Meta evaluates**: Backtracking pattern, start index for combinations vs permutations, pruning optimization.
- **Follow-ups**: Combination Sum II (each number used once). Combination Sum III (k numbers sum to n). Subsets and permutations variations.

### Category: Graphs
#### Problem: Clone Graph (LC 133)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given a reference to a node in a connected undirected graph, return a deep copy of the graph.
- **Interview walkthrough**: Use a HashMap from original node to cloned node. DFS or BFS traversal. For each neighbor of original, clone if not already in map, then add that clone as neighbor of the current cloned node.
- **Solution approaches**: DFS recursion O(V+E). BFS with queue O(V+E). Meta expects DFS with HashMap.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Clone Graph.
 */
public class CloneGraph {

    static class Node {
        public int val;
        public List<Node> neighbors;
        public Node(int val) {
            this.val = val;
            neighbors = new ArrayList<>();
        }
    }

    private Map<Node, Node> visited = new HashMap<>();

    /**
     * Returns a deep copy of the given graph.
     *
     * @param node reference node of the graph
     * @return deep copy of the graph
     */
    public Node cloneGraph(Node node) {
        if (node == null) return null;
        if (visited.containsKey(node)) return visited.get(node);
        Node clone = new Node(node.val);
        visited.put(node, clone);
        for (Node neighbor : node.neighbors) {
            clone.neighbors.add(cloneGraph(neighbor));
        }
        return clone;
    }
}
```
- **What Meta evaluates**: Deep copy understanding, HashMap for visited/cloned mapping, recursive graph traversal.
- **Follow-ups**: Clone a directed graph. Clone a graph with cycles (this handles it). Serialize and deserialize a graph.

#### Problem: Alien Dictionary (LC 269)
- **Difficulty/Frequency**: Hard / High
- **Problem statement**: Given a sorted list of alien words, return the order of characters in the alien language.
- **Interview walkthrough**: Build a directed graph from adjacent words. Compare first differing character of each pair — add edge from smaller to larger. Run topological sort (DFS with cycle detection or Kahn's algorithm).
- **Solution approaches**: Topological sort O(C) where C = total characters. DFS with post-order. Kahn's with indegree. Meta expects thorough handling of edge cases.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Alien Dictionary.
 */
public class AlienDictionary {

    /**
     * Returns the character order in the alien language.
     *
     * @param words sorted array of alien words
     * @return character order string, or empty if invalid
     */
    public String alienOrder(String[] words) {
        Map<Character, Set<Character>> graph = new HashMap<>();
        Map<Character, Integer> indegree = new HashMap<>();
        for (String w : words) {
            for (char c : w.toCharArray()) {
                graph.putIfAbsent(c, new HashSet<>());
                indegree.putIfAbsent(c, 0);
            }
        }
        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i], w2 = words[i + 1];
            if (w1.length() > w2.length() && w1.startsWith(w2)) {
                return "";
            }
            int minLen = Math.min(w1.length(), w2.length());
            for (int j = 0; j < minLen; j++) {
                if (w1.charAt(j) != w2.charAt(j)) {
                    if (!graph.get(w1.charAt(j)).contains(w2.charAt(j))) {
                        graph.get(w1.charAt(j)).add(w2.charAt(j));
                        indegree.put(w2.charAt(j),
                                indegree.get(w2.charAt(j)) + 1);
                    }
                    break;
                }
            }
        }
        Queue<Character> queue = new LinkedList<>();
        for (char c : indegree.keySet()) {
            if (indegree.get(c) == 0) queue.offer(c);
        }
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            char c = queue.poll();
            sb.append(c);
            for (char next : graph.get(c)) {
                indegree.put(next, indegree.get(next) - 1);
                if (indegree.get(next) == 0) queue.offer(next);
            }
        }
        return sb.length() == indegree.size() ? sb.toString() : "";
    }
}
```
- **What Meta evaluates**: Graph construction from ordering relations, topological sort, cycle detection, edge case handling (prefix rule).
- **Follow-ups**: Verify ordering of multiple sequences. Reconstruct sequence from pairs.

### Category: Binary Search
#### Problem: Find Peak Element (LC 162)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Find a peak element in an array (element greater than its neighbors). Assume nums[-1] = nums[n] = -infinity.
- **Interview walkthrough**: Binary search. Compare mid with mid+1. If nums[mid] < nums[mid+1], peak is on right (since the array must eventually decrease). Otherwise, peak is on left (including mid).
- **Solution approaches**: Linear scan O(n). Binary search O(log n). Meta expects binary search with careful reasoning.
- **Java code**:
```java
/**
 * Solution for Find Peak Element.
 */
public class FindPeakElement {

    /**
     * Returns the index of a peak element.
     *
     * @param nums input array
     * @return index of a peak element
     */
    public int findPeakElement(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < nums[mid + 1]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }
}
```
- **What Meta evaluates**: Binary search on non-sorted array, reasoning about inflection points, O(log n) convergence proof.
- **Follow-ups**: Find all peaks. Find peak in 2D matrix.

## Company-Specific Algorithm Focus
Meta emphasizes **strings** and **DP** more than any other company. They frequently test recursion and backtracking. Meta also loves **graph traversal** (clone graph, alien dictionary). Their problems often involve **optimizing from exponential to polynomial** using memoization or DP. Meta cares deeply about **logarithmic time** solutions (binary search variants). They rarely ask pure number theory or cryptography.

## System Design with Algorithms
1. **Design Messenger Chat Search** — Requires text indexing with inverted indices, prefix search using trie with suggestions, ranking algorithms (BM25, TF-IDF), and top-k retrieval using heap-based aggregation.
2. **Design News Feed Ranking Algorithm** — Requires scoring mechanisms: affinity score (EdgeRank), graph-based importance (PageRank variants), real-time stream processing with online algorithms, and machine learning inference at scale.

## Behavioral Questions (STAR Format)
1. **Tell me about a time you took initiative**: I noticed our CI pipeline had a sorting step that was O(n^2) for large datasets. I independently replaced it with a parallel merge sort using ForkJoinPool, reducing sort time from 30 minutes to 4 minutes. I presented this at our engineering all-hands.
2. **Tell me about a time you resolved a conflict**: A teammate argued for DFS while I argued for BFS on a tree problem. I proposed we prototype both with representative data. BFS used more memory but was 3x faster. We documented the trade-off and went with BFS.
3. **Tell me about your impact**: I built a caching layer using LRU with O(1) operations that reduced database reads by 60%. I used a doubly linked list with a hashmap. This required reasoning about access patterns and cache sizing, which aligned with our move to reduce infrastructure costs.
4. **Tell me about a time you failed**: I implemented a greedy algorithm for a scheduling optimizer that passed all tests but failed in production. I had not proven optimality. I went back, implemented a DP solution with proper subproblem formulation, and added property-based tests that caught the earlier issues.
5. **Tell me about a time you learned a new technology**: I had to implement a cryptographic hash for a distributed system but had no crypto background. I studied the Merkle-Damgard construction, implemented SHA-256 from scratch, and wrote unit tests against RFC test vectors. This deepened my understanding of algorithm design in security contexts.

## Study Plan
Prioritize these labs in order:
1. Lab 10: Array and String Algorithms
2. Lab 4: Dynamic Programming I
3. Lab 5: Dynamic Programming II
4. Lab 12: Recursion and Backtracking
5. Lab 7: Graph Algorithms I
6. Lab 11: String Searching (KMP, Rabin-Karp)
7. Lab 14: Sorting and Searching

## Tips
- Meta interviews are **on a laptop with no run button**. Your code must be syntactically perfect. Practice writing code cold.
- **Readability above all**. Meta engineers value clean, readable code that is self-explanatory.
- **Use helper methods** generously. Meta wants to see modular code.
- Meta interviewers look for **problem-solving process** as much as the correct answer. If you are stuck, talk through the brute force approach.
- **Meta asks fewer trick questions** than Google. The problems are more standard, but the bar for code quality is higher.
- Be ready for **Facebook/Meta-specific** problems involving social graph traversal, friend recommendations, and news feed aggregation.
- **Explain optimizations** even if you don't code them. Show that you can see the path to improvement.
