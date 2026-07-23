# Google Interview Guide — Data Structures Academy

## Interview Process for DS Roles
- **Rounds**: 5 total: 1 phone screen (45 min coding) + 4 on-site rounds (2 coding, 1 system design, 1 googleyness/behavioral). Data structures tested in 3-4 rounds.
- **Timeline**: Phone screen → on-site within 2-4 weeks. Feedback in 2 weeks.
- **Format**: Google Docs shared document for coding. No IDE, no syntax highlighting, no autocomplete. Whiteboard-style typed code.
- **Focus**: Optimal time/space complexity first. Clean, readable code second. Must analyze trade-offs unprompted.
- **Coding Environment**: Google Docs. Practice writing code without autocomplete. Indentation matters.

## Top Problems by Lab

### Lab 04-trees (Binary Trees)
#### Problem: Binary Tree Maximum Path Sum (LC 124)
- **Difficulty**: Hard | **Frequency**: Very High
- **Statement**: Given a binary tree, find the maximum path sum. The path may start and end at any node.
- **How the interview goes**: Clarify path definition (any node to any node, must be contiguous). Derive recursive formula step by step.
- **Approach 1**: Recursive Post-Order — O(n) time, O(h) space. Compute max gain from each subtree.
- **Approach 2**: Iterative Post-Order Stack — O(n) time, O(n) space. Avoids recursion depth issues on skewed trees.
- **Java**:
```java
/**
 * Maximum path sum using post-order traversal.
 * Time: O(n), Space: O(h)
 */
public class MaxPathSum {
    private int max = Integer.MIN_VALUE;
    public int maxPathSum(TreeNode root) {
        maxGain(root);
        return max;
    }
    private int maxGain(TreeNode node) {
        if (node == null) return 0;
        int left = Math.max(0, maxGain(node.left));
        int right = Math.max(0, maxGain(node.right));
        max = Math.max(max, left + right + node.val);
        return node.val + Math.max(left, right);
    }
}
```
- **What Google evaluates**: Correct recursive formulation, handling negatives, clean state management.
- **Follow-up**: Print the path. Generalize to N-ary tree. Solve iteratively.

#### Problem: Serialize and Deserialize Binary Tree (LC 297)
- **Difficulty**: Hard | **Frequency**: High
- **Statement**: Design algorithms to serialize/deserialize a binary tree into a string (nulls included).
- **How the interview goes**: Discuss DFS vs BFS approaches first. Always handle null children explicitly.
- **Approach 1**: DFS Pre-order — O(n) time, O(n) space. Simple recursion with "null" markers.
- **Approach 2**: BFS Level-order — O(n) time, O(n) space. More complex but handles wide trees efficiently.
- **Java**:
```java
/**
 * Serialize/deserialize using pre-order DFS.
 * Time: O(n), Space: O(n)
 */
public class Codec {
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serialize(root, sb);
        return sb.toString();
    }
    private void serialize(TreeNode node, StringBuilder sb) {
        if (node == null) { sb.append("null,"); return; }
        sb.append(node.val).append(",");
        serialize(node.left, sb);
        serialize(node.right, sb);
    }
    public TreeNode deserialize(String data) {
        Queue<String> q = new LinkedList<>(Arrays.asList(data.split(",")));
        return deserialize(q);
    }
    private TreeNode deserialize(Queue<String> q) {
        String val = q.poll();
        if (val.equals("null")) return null;
        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left = deserialize(q);
        node.right = deserialize(q);
        return node;
    }
}
```
- **What Google evaluates**: Edge cases (empty, large depth), traversal choice, code modularity.
- **Follow-up**: Compact encoding (Lehmann code). Support BST optimization. Iterative serialization.

### Lab 05-graphs (Graph Algorithms)
#### Problem: Word Ladder (LC 127)
- **Difficulty**: Hard | **Frequency**: Very High
- **Statement**: Shortest transformation from beginWord to endWord, changing one letter at a time, each intermediate word in dictionary.
- **How the interview goes**: Recognize as unweighted shortest path → BFS. Google expects bidirectional BFS optimization.
- **Approach 1**: BFS — O(m²·n) time, O(m²·n) space. Generate all one-edit neighbors.
- **Approach 2**: Bidirectional BFS — Same complexity, 2× faster in practice. Two frontiers meet in the middle.
- **Java**:
```java
/**
 * Word Ladder using bidirectional BFS.
 * Time: O(m^2 * n), Space: O(m^2 * n)
 */
public int ladderLength(String beginWord, String endWord, List<String> wordList) {
    Set<String> dict = new HashSet<>(wordList);
    if (!dict.contains(endWord)) return 0;
    Set<String> bSet = new HashSet<>(Collections.singleton(beginWord));
    Set<String> eSet = new HashSet<>(Collections.singleton(endWord));
    int steps = 1;
    while (!bSet.isEmpty() && !eSet.isEmpty()) {
        if (bSet.size() > eSet.size()) { Set<String> t = bSet; bSet = eSet; eSet = t; }
        Set<String> next = new HashSet<>();
        for (String w : bSet) {
            char[] ch = w.toCharArray();
            for (int i = 0; i < ch.length; i++) {
                char orig = ch[i];
                for (char c = 'a'; c <= 'z'; c++) {
                    ch[i] = c; String nw = new String(ch);
                    if (eSet.contains(nw)) return steps + 1;
                    if (dict.contains(nw)) { next.add(nw); dict.remove(nw); }
                }
                ch[i] = orig;
            }
        }
        bSet = next; steps++;
    }
    return 0;
}
```
- **What Google evaluates**: Choosing BFS over DFS, neighbor generation optimization, bidirectional technique.
- **Follow-up**: Return all shortest paths. Optimize for very large dictionaries (wildcard neighbors).

#### Problem: Number of Islands (LC 200)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Count connected components of '1's in a 2D binary grid.
- **How the interview goes**: Google expects a quick BFS/DFS solution (~10 min) then optimization discussion.
- **Approach 1**: DFS Flood Fill — O(m·n) time, O(m·n) worst-case recursion space.
- **Approach 2**: Union-Find — O(m·n·α(mn)) time, O(m·n) space. Preferred for dynamic grid scenarios.
- **Java**:
```java
/**
 * Count islands using DFS flood fill.
 * Time: O(m*n), Space: O(m*n)
 */
public int numIslands(char[][] grid) {
    if (grid == null || grid.length == 0) return 0;
    int count = 0, m = grid.length, n = grid[0].length;
    for (int i = 0; i < m; i++)
        for (int j = 0; j < n; j++)
            if (grid[i][j] == '1') { dfs(grid, i, j); count++; }
    return count;
}
private void dfs(char[][] g, int i, int j) {
    if (i < 0 || i >= g.length || j < 0 || j >= g[0].length || g[i][j] == '0') return;
    g[i][j] = '0';
    dfs(g, i - 1, j); dfs(g, i + 1, j); dfs(g, i, j - 1); dfs(g, i, j + 1);
}
```
- **What Google evaluates**: Handling boundary cells, in-place vs visited array, space complexity awareness.
- **Follow-up**: Max island area (LC 695). Count islands that touch border. 3D grid.

### Lab 08-hashing (Hash Maps)
#### Problem: Longest Substring Without Repeating Characters (LC 3)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Find length of longest substring with all distinct characters.
- **How the interview goes**: Google wants the O(n) sliding window immediately. Show both naive and optimized.
- **Approach 1**: Sliding Window + HashMap — O(n) time, O(min(m,n)) space. Maintain last-seen index.
- **Approach 2**: Sliding Window + int[128] — O(n) time, O(1) space. Faster for ASCII constraint.
- **Java**:
```java
/**
 * Sliding window with hash map.
 * Time: O(n), Space: O(min(m,n))
 */
public int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> map = new HashMap<>();
    int max = 0, left = 0;
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        if (map.containsKey(c) && map.get(c) >= left) left = map.get(c) + 1;
        map.put(c, right);
        max = Math.max(max, right - left + 1);
    }
    return max;
}
```
- **What Google evaluates**: Sliding window correctness, character set assumptions, edge case of empty string.
- **Follow-up**: Return the substring. Longest substring with at most K distinct (LC 340).

#### Problem: Group Anagrams (LC 49)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Group strings that are anagrams of each other.
- **How the interview goes**: Discuss sorting vs counting approaches. Google prefers counting based on frequency array.
- **Approach 1**: Sort + HashMap — O(n·k log k) time, O(n·k) space. Sort each string as key.
- **Approach 2**: Frequency Count + HashMap — O(n·k) time, O(n·k) space. Use int[26] as key.
- **Java**:
```java
/**
 * Group anagrams using frequency counting.
 * Time: O(n*k), Space: O(n*k)
 */
public List<List<String>> groupAnagrams(String[] strs) {
    Map<String, List<String>> map = new HashMap<>();
    for (String s : strs) {
        int[] count = new int[26];
        for (char c : s.toCharArray()) count[c - 'a']++;
        String key = Arrays.toString(count);
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
    }
    return new ArrayList<>(map.values());
}
```
- **What Google evaluates**: Understanding string canonical forms, trade-off between counting and sorting.
- **Follow-up**: Large input streaming. Unicode characters. Compare with trie-based grouping.

### Lab 13-dynamic-programming
#### Problem: Longest Increasing Subsequence (LC 300)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Find length of longest strictly increasing subsequence.
- **How the interview goes**: Google expects both O(n²) DP and O(n log n) patience sorting. Demonstrate understanding of both.
- **Approach 1**: DP — O(n²) time, O(n) space. dp[i] = LIS ending at i.
- **Approach 2**: Patience Sorting (Binary Search) — O(n log n) time, O(n) space. Maintain tails array.
- **Java**:
```java
/**
 * LIS using patience sorting (binary search on tails).
 * Time: O(n log n), Space: O(n)
 */
public int lengthOfLIS(int[] nums) {
    int[] tails = new int[nums.length];
    int size = 0;
    for (int num : nums) {
        int lo = 0, hi = size;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (tails[mid] < num) lo = mid + 1; else hi = mid;
        }
        tails[lo] = num;
        if (lo == size) size++;
    }
    return size;
}
```
- **What Google evaluates**: Understanding DP optimal substructure, knowing when binary search applies.
- **Follow-up**: Print LIS. Longest bitonic subsequence. Russian doll envelopes (LC 354).

#### Problem: Coin Change (LC 322)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Minimum number of coins to make a given amount.
- **How the interview goes**: Google tests bottom-up DP. Start with greedy counterexample, then derive DP recurrence.
- **Approach 1**: Top-Down DP with Memoization — O(amount·n) time, O(amount) space. Recursive with cache.
- **Approach 2**: Bottom-Up DP — O(amount·n) time, O(amount) space. Iterative, usually preferred in interviews.
- **Java**:
```java
/**
 * Coin change using bottom-up DP.
 * Time: O(amount * n), Space: O(amount)
 */
public int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1);
    dp[0] = 0;
    for (int i = 1; i <= amount; i++) {
        for (int coin : coins) {
            if (coin <= i) dp[i] = Math.min(dp[i], dp[i - coin] + 1);
        }
    }
    return dp[amount] > amount ? -1 : dp[amount];
}
```
- **What Google evaluates**: Setting up DP array dimensions, handling unreachable states, infinite coin assumption.
- **Follow-up**: Return the coin combination. Limited supply of each coin. Coin change ways (LC 518).

### Lab 12-backtracking
#### Problem: Permutations (LC 46)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Return all permutations of a distinct integer array.
- **How the interview goes**: Google expects you to draw the recursion tree first. Discuss swap vs used-array approaches.
- **Approach 1**: Backtracking with boolean array — O(n·n!) time, O(n) space. Track used elements.
- **Approach 2**: Backtracking with swap — O(n·n!) time, O(n) space. In-place, no extra allocation.
- **Java**:
```java
/**
 * Generate permutations using backtracking with swapping.
 * Time: O(n*n!), Space: O(n)
 */
public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, 0, result);
    return result;
}
private void backtrack(int[] nums, int start, List<List<Integer>> result) {
    if (start == nums.length) {
        List<Integer> list = new ArrayList<>();
        for (int n : nums) list.add(n);
        result.add(list);
        return;
    }
    for (int i = start; i < nums.length; i++) {
        swap(nums, start, i);
        backtrack(nums, start + 1, result);
        swap(nums, start, i);
    }
}
private void swap(int[] nums, int i, int j) { int t = nums[i]; nums[i] = nums[j]; nums[j] = t; }
```
- **What Google evaluates**: Recursion tree understanding, duplicate avoidance, in-place vs extra space trade-offs.
- **Follow-up**: Duplicates (LC 47). Next permutation (LC 31) iterative. Kth permutation (LC 60).

#### Problem: Subsets (LC 78)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Return all possible subsets of a distinct integer array.
- **How the interview goes**: Google uses this to test exhaustive search. Multiple approaches are a plus.
- **Approach 1**: Cascading (Iterative) — O(n·2^n) time, O(n·2^n) space. Build subsets from empty.
- **Approach 2**: Backtracking (Recursive) — O(n·2^n) time, O(n·2^n) space. Classic DFS.
- **Java**:
```java
/**
 * Generate subsets using cascading iteration.
 * Time: O(n*2^n), Space: O(n*2^n)
 */
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    result.add(new ArrayList<>());
    for (int num : nums) {
        int size = result.size();
        for (int i = 0; i < size; i++) {
            List<Integer> subset = new ArrayList<>(result.get(i));
            subset.add(num);
            result.add(subset);
        }
    }
    return result;
}
```
- **What Google evaluates**: Multiple approach generation, space usage awareness, iteration vs recursion.
- **Follow-up**: Duplicates (LC 90). Subsets of size K. Subset sum target.

### Lab 17-trie (Trie)
#### Problem: Word Search II (LC 212)
- **Difficulty**: Hard | **Frequency**: Medium
- **Statement**: Find all words from dictionary that exist on character board.
- **How the interview goes**: Google expects Trie + DFS. Start with Word Search I (DFS only), then add Trie optimization.
- **Approach 1**: Trie + DFS Backtracking — O(m·n·4^L) time, O(total letters) space.
- **Approach 2**: Trie + Pruning — Same complexity. Remove matched words from Trie to avoid duplicates.
- **Java**:
```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];
    String word;
}
/**
 * Word Search II with Trie + DFS.
 * Time: O(m*n*4^L), Space: O(trie nodes)
 */
public List<String> findWords(char[][] board, String[] words) {
    TrieNode root = buildTrie(words);
    List<String> result = new ArrayList<>();
    for (int i = 0; i < board.length; i++)
        for (int j = 0; j < board[0].length; j++)
            dfs(board, i, j, root, result);
    return result;
}
private void dfs(char[][] b, int i, int j, TrieNode node, List<String> res) {
    if (i < 0 || i >= b.length || j < 0 || j >= b[0].length) return;
    char c = b[i][j];
    if (c == '#' || node.children[c - 'a'] == null) return;
    node = node.children[c - 'a'];
    if (node.word != null) { res.add(node.word); node.word = null; }
    b[i][j] = '#';
    dfs(b, i - 1, j, node, res); dfs(b, i + 1, j, node, res);
    dfs(b, i, j - 1, node, res); dfs(b, i, j + 1, node, res);
    b[i][j] = c;
}
```
- **What Google evaluates**: Combining multiple DS, pruning, avoiding duplicate results.
- **Follow-up**: Very large board optimization. Top K frequent found words. Dynamic word list.

### Lab 07-queues
#### Problem: Sliding Window Maximum (LC 239)
- **Difficulty**: Hard | **Frequency**: Medium
- **Statement**: Find maximum in each sliding window of size k across the array.
- **How the interview goes**: Discuss three approaches (brute force, heap, deque). Google expects optimal deque solution.
- **Approach 1**: Monotonic Deque — O(n) time, O(k) space. Stores indices, maintains decreasing values.
- **Approach 2**: Max-Heap — O(n log k) time, O(k) space. Lazy deletion pattern.
- **Java**:
```java
/**
 * Sliding window maximum using monotonic deque.
 * Time: O(n), Space: O(k)
 */
public int[] maxSlidingWindow(int[] nums, int k) {
    if (nums == null || k == 0) return new int[0];
    int[] result = new int[nums.length - k + 1];
    Deque<Integer> dq = new ArrayDeque<>();
    for (int i = 0; i < nums.length; i++) {
        while (!dq.isEmpty() && dq.peekFirst() < i - k + 1) dq.pollFirst();
        while (!dq.isEmpty() && nums[dq.peekLast()] < nums[i]) dq.pollLast();
        dq.offerLast(i);
        if (i >= k - 1) result[i - k + 1] = nums[dq.peekFirst()];
    }
    return result;
}
```
- **What Google evaluates**: Recognizing monotonic queue pattern, index vs value storage, O(n) intuition.
- **Follow-up**: Minimum in window. 2D sliding window. Returning indices instead of values.

## System Design Questions
1. **Design Google Search Autocomplete** — Trie (Lab 17) for prefix matching, Heap (Lab 09) for top K, Hashing (Lab 08) for caching frequent queries.
2. **Design Google Docs Real-Time Collaboration** — Segment Trees (Lab 19) for version tracking, CRDTs, Queue (Lab 07) for operation ordering.
3. **Design Web Crawler** — BFS (Lab 05) for traversal, Hashing (Lab 08) for URL dedup, Trie (Lab 17) for domain routing.

## Behavioral Questions (STAR)
1. **Situation**: "Tell me about optimizing a slow algorithm." — **Task**: Array merge ran O(n²). **Action**: Replaced with merge sort O(n log n) using Lab 15-sorting. **Result**: 40× speedup.
2. **Situation**: "Describe choosing between data structures." — **Task**: Autocomplete feature. **Action**: Compared HashMap vs Trie (Lab 17); chose Trie for prefix efficiency. **Result**: 60% faster prefix queries.
3. **Situation**: "Technical disagreement on approach." — **Task**: Traversal strategy debate. **Action**: Benchmarked BFS vs DFS (Lab 05); presented data to team. **Result**: Chose BFS for shortest path requirement.
4. **Situation**: "Complex bug fix." — **Task**: Production stack overflow. **Action**: Converted recursive DFS (Lab 05) to iterative BFS. **Result**: Handled 10× larger graphs.
5. **Situation**: "Ambiguity in requirements." — **Task**: Sorting library choice. **Action**: Prototyped quicksort, mergesort, heapsort (Lab 15); compared trade-offs. **Result**: Team chose quicksort for in-place needs.

## Study Plan
1. **Weeks 1-2**: Lab 04-trees, Lab 05-graphs — Google's most tested labs
2. **Weeks 3-4**: Lab 13-dynamic-programming, Lab 08-hashing — Core optimization
3. **Week 5**: Lab 07-queues, Lab 12-backtracking — Combination patterns
4. **Week 6**: Lab 17-trie, Lab 09-heaps — Niche high-value topics
5. **Weeks 7-8**: Mock interviews, focus on optimal complexity and Google Docs practice

## Interview Tips
- **Pace**: Narrate your thinking continuously. Google values real-time commentary.
- **Communication**: Always start with brute force, then optimize. Explain why each optimization works.
- **What impresses**: Unprompted complexity analysis, testing edge cases before being asked, writing bug-free code.
- **Avoid**: Silent coding, ignoring hints, jumping to complex solutions without clarifying.
- **Coding style**: Clean, readable, meaningful variable names. Avoid clever one-liners.
