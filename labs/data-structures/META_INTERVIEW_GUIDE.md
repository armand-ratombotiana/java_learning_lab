# Meta Interview Guide — Data Structures Academy

## Interview Process for DS Roles
- **Rounds**: 2 phone screens (coding + behavioral) → 4 on-site rounds (2 coding, 1 system design, 1 behavioral). Data structures central to 3 rounds.
- **Timeline**: Recruiter reach-out → phone screen within 1 week → on-site within 2-3 weeks. Fast feedback loop.
- **Format**: CoderPad shared coding environment. No local IDE. Supports multiple languages side-by-side.
- **Focus**: Speed and correctness. Meta expects a working solution in 20-25 minutes. Optimization is second priority.
- **Coding Environment**: CoderPad with basic autocomplete. Practice typing code without full IDE support.
- **Meta's Culture**: Move fast. They want to see you arrive at the solution quickly while communicating clearly.

## Top Problems by Lab

### Lab 01-arrays (Arrays)
#### Problem: Merge Intervals (LC 56)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Merge overlapping intervals in an array of [start, end] pairs.
- **How the interview goes**: Meta expects sorting + linear merge. Must discuss comparator and interval overlap logic.
- **Approach 1**: Sort + Merge — O(n log n) time, O(n) space. Sort by start, merge if overlap.
- **Approach 2**: Sort + In-Place — O(n log n) time, O(1) extra space. Modify input array.
- **Java**:
```java
/**
 * Merge overlapping intervals.
 * Time: O(n log n), Space: O(n)
 */
public int[][] merge(int[][] intervals) {
    Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
    List<int[]> merged = new ArrayList<>();
    for (int[] interval : intervals) {
        if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
            merged.add(interval);
        } else {
            merged.get(merged.size() - 1)[1] = Math.max(merged.get(merged.size() - 1)[1], interval[1]);
        }
    }
    return merged.toArray(new int[merged.size()][]);
}
```
- **What Meta evaluates**: Sorting comparator, merge condition logic, handling unsorted input.
- **Follow-up**: Insert interval (LC 57). Meeting rooms II (LC 253). Interval list intersections (LC 986).

#### Problem: Product of Array Except Self (LC 238)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Compute product of all elements except self without division.
- **How the interview goes**: Meta loves this problem. Must solve without division in O(n). Left/right pass pattern.
- **Approach 1**: Left/Right Pass — O(n) time, O(n) space. Compute prefix and suffix products.
- **Approach 2**: Single Pass with Output Array — O(n) time, O(1) extra space. Store result in output array.
- **Java**:
```java
/**
 * Product of array except self using two passes.
 * Time: O(n), Space: O(1) extra
 */
public int[] productExceptSelf(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    result[0] = 1;
    for (int i = 1; i < n; i++) result[i] = result[i - 1] * nums[i - 1];
    int right = 1;
    for (int i = n - 1; i >= 0; i--) {
        result[i] *= right;
        right *= nums[i];
    }
    return result;
}
```
- **What Meta evaluates**: Division constraint handling, prefix/suffix technique, O(1) space optimization.
- **Follow-up**: Product of last K numbers (LC 1352). Division-based approach discussion.

### Lab 02-strings (Strings)
#### Problem: Valid Palindrome II (LC 680)
- **Difficulty**: Easy | **Frequency**: Very High
- **Statement**: Can you make a string palindrome by deleting at most one character?
- **How the interview goes**: Meta tests two-pointer with tolerance. Must handle the deletion decision correctly.
- **Approach 1**: Two-Pointer with Subproblem — O(n) time, O(1) space. On mismatch, check both options.
- **Approach 2**: Recursive with Skip — O(n) time, O(n) stack space. Elegant but uses more space.
- **Java**:
```java
/**
 * Valid palindrome II with at most one deletion.
 * Time: O(n), Space: O(1)
 */
public boolean validPalindrome(String s) {
    int lo = 0, hi = s.length() - 1;
    while (lo < hi) {
        if (s.charAt(lo) != s.charAt(hi)) {
            return isPalindrome(s, lo + 1, hi) || isPalindrome(s, lo, hi - 1);
        }
        lo++; hi--;
    }
    return true;
}
private boolean isPalindrome(String s, int lo, int hi) {
    while (lo < hi) {
        if (s.charAt(lo++) != s.charAt(hi--)) return false;
    }
    return true;
}
```
- **What Meta evaluates**: Two-pointer correctness, handling delete edge case, short-circuit logic.
- **Follow-up**: Valid palindrome III (LC 1216) — delete K characters (DP). Palindrome linked list (LC 234).

#### Problem: Longest Palindromic Substring (LC 5)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Find the longest palindromic substring in a string.
- **How the interview goes**: Meta expects expand-around-center as optimal. Must handle odd and even length.
- **Approach 1**: Expand Around Center — O(n²) time, O(1) space. Check each center (2n-1 centers).
- **Approach 2**: DP — O(n²) time, O(n²) space. Table-based. More intuitive but wasteful.
- **Java**:
```java
/**
 * Longest palindromic substring using expand around center.
 * Time: O(n^2), Space: O(1)
 */
public String longestPalindrome(String s) {
    if (s == null || s.length() < 2) return s;
    int start = 0, maxLen = 1;
    for (int i = 0; i < s.length(); i++) {
        int len1 = expand(s, i, i);
        int len2 = expand(s, i, i + 1);
        int len = Math.max(len1, len2);
        if (len > maxLen) {
            start = i - (len - 1) / 2;
            maxLen = len;
        }
    }
    return s.substring(start, start + maxLen);
}
private int expand(String s, int l, int r) {
    while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) { l--; r++; }
    return r - l - 1;
}
```
- **What Meta evaluates**: Center expansion insight, odd/even handling, substring extraction.
- **Follow-up**: Count palindromic substrings (LC 647). Manacher's algorithm (O(n)).

### Lab 04-trees
#### Problem: Lowest Common Ancestor of BST (LC 235)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Find LCA of two nodes in a BST.
- **How the interview goes**: Meta expects BST property exploitation. Much simpler than general binary tree LCA.
- **Approach 1**: Iterative — O(h) time, O(1) space. Walk down tree until values diverge.
- **Approach 2**: Recursive — O(h) time, O(h) stack space. Elegant but more space.
- **Java**:
```java
/**
 * LCA in BST using iterative approach.
 * Time: O(h), Space: O(1)
 */
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    TreeNode curr = root;
    while (curr != null) {
        if (p.val < curr.val && q.val < curr.val) curr = curr.left;
        else if (p.val > curr.val && q.val > curr.val) curr = curr.right;
        else return curr;
    }
    return null;
}
```
- **What Meta evaluates**: BST property understanding, iterative approach, null safety.
- **Follow-up**: LCA of binary tree (LC 236). LCA of deepest leaves (LC 1123).

#### Problem: Binary Tree Right Side View (LC 199)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Return the rightmost node at each level of a binary tree.
- **How the interview goes**: Meta tests level-order traversal. BFS is the expected approach.
- **Approach 1**: BFS Level-Order — O(n) time, O(n) space. Track last node of each level.
- **Approach 2**: DFS Pre-order — O(n) time, O(h) space. Visit right first, track depth.
- **Java**:
```java
/**
 * Right side view of binary tree using BFS.
 * Time: O(n), Space: O(n)
 */
public List<Integer> rightSideView(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> q = new LinkedList<>();
    q.offer(root);
    while (!q.isEmpty()) {
        int size = q.size();
        for (int i = 0; i < size; i++) {
            TreeNode node = q.poll();
            if (i == size - 1) result.add(node.val);
            if (node.left != null) q.offer(node.left);
            if (node.right != null) q.offer(node.right);
        }
    }
    return result;
}
```
- **What Meta evaluates**: BFS queue implementation, level boundary detection, handling last node.
- **Follow-up**: Left side view. Top/bottom view. Vertical order traversal (LC 987).

### Lab 11-recursion
#### Problem: Pow(x, n) (LC 50)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Implement pow(x, n) — calculate x raised to power n.
- **How the interview goes**: Meta tests binary exponentiation. Must handle negative exponents and overflow.
- **Approach 1**: Binary Exponentiation (Recursive) — O(log n) time, O(log n) stack space. Divide-and-conquer.
- **Approach 2**: Binary Exponentiation (Iterative) — O(log n) time, O(1) space. Process bits from LSB to MSB.
- **Java**:
```java
/**
 * Pow(x, n) using iterative binary exponentiation.
 * Time: O(log n), Space: O(1)
 */
public double myPow(double x, int n) {
    long N = n;
    if (N < 0) { x = 1 / x; N = -N; }
    double result = 1, base = x;
    while (N > 0) {
        if ((N & 1) == 1) result *= base;
        base *= base;
        N >>= 1;
    }
    return result;
}
```
- **What Meta evaluates**: Negative exponent handling, Integer.MIN_VALUE edge case, bit manipulation.
- **Follow-up**: Super pow (LC 372). Sqrt(x) (LC 69).

#### Problem: K-th Symbol in Grammar (LC 779)
- **Difficulty**: Medium | **Frequency**: Medium
- **Statement**: Build a grammar (row 1 = "0", each row replaces 0→01, 1→10). Find Kth symbol.
- **How the interview goes**: Meta tests recursive pattern recognition. Parent mapping is key insight.
- **Approach 1**: Recursive Parent Mapping — O(n) time, O(n) stack. Each position's value depends on its parent.
- **Approach 2**: Bit Counting — O(1) time, O(1) space. Count bits in K-1, parity determines value.
- **Java**:
```java
/**
 * K-th symbol using recursion.
 * Time: O(n), Space: O(n) stack
 */
public int kthGrammar(int n, int k) {
    if (n == 1) return 0;
    int parent = kthGrammar(n - 1, (k + 1) / 2);
    if (parent == 0) return (k % 2 == 1) ? 0 : 1;
    else return (k % 2 == 1) ? 1 : 0;
}
```
- **What Meta evaluates**: Recursive thinking, parent-child relationship, odd/even mapping.
- **Follow-up**: Iterative solution. Optimize with bit manipulation.

### Lab 13-dynamic-programming
#### Problem: Decode Ways (LC 91)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Count ways to decode a numeric string (A=1, B=2, ..., Z=26).
- **How the interview goes**: Meta tests DP with constraints (two-digit decoding). Must handle leading zeros.
- **Approach 1**: DP Array — O(n) time, O(n) space. dp[i] = ways for string of length i.
- **Approach 2**: Space-Optimized DP — O(n) time, O(1) space. Only need last two states.
- **Java**:
```java
/**
 * Decode ways using space-optimized DP.
 * Time: O(n), Space: O(1)
 */
public int numDecodings(String s) {
    if (s == null || s.length() == 0 || s.charAt(0) == '0') return 0;
    int prev2 = 1, prev1 = 1;
    for (int i = 1; i < s.length(); i++) {
        int curr = 0;
        if (s.charAt(i) != '0') curr += prev1;
        int twoDigit = Integer.parseInt(s.substring(i - 1, i + 1));
        if (twoDigit >= 10 && twoDigit <= 26) curr += prev2;
        prev2 = prev1; prev1 = curr;
    }
    return prev1;
}
```
- **What Meta evaluates**: Zero handling, two-digit constraint, space optimization.
- **Follow-up**: Decode ways II (LC 639) with wildcards. Numbers with repeated decoding.

#### Problem: House Robber (LC 198)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Max sum from non-adjacent elements in an array.
- **How the interview goes**: Meta's intro to DP. Tests the can't-rob-adjacent constraint.
- **Approach 1**: DP Array — O(n) time, O(n) space. dp[i] = max up to house i.
- **Approach 2**: Space-Optimized — O(n) time, O(1) space. Track rob/not-rob states.
- **Java**:
```java
/**
 * House robber using space-optimized DP.
 * Time: O(n), Space: O(1)
 */
public int rob(int[] nums) {
    int prev = 0, curr = 0;
    for (int n : nums) {
        int temp = curr;
        curr = Math.max(curr, prev + n);
        prev = temp;
    }
    return curr;
}
```
- **What Meta evaluates**: DP state definition, adjacent constraint, space optimization.
- **Follow-up**: House robber II (LC 213) circular. House robber III (LC 337) tree. Delete and earn (LC 740).

### Lab 12-backtracking
#### Problem: Subsets (LC 78)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Generate all possible subsets of a distinct integer array.
- **How the interview goes**: Meta tests combinatorial generation. Must handle iteration vs recursion.
- **Approach 1**: Cascading — O(n·2^n) time, O(n·2^n) space. Iterative, building from empty.
- **Approach 2**: Backtracking — O(n·2^n) time, O(n·2^n) space. DFS with include/skip.
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
- **What Meta evaluates**: Iterative vs recursive trade-off, avoiding duplicates, space complexity.
- **Follow-up**: Subsets with duplicates (LC 90). Subset sum equals target. Combination sum (LC 39).

#### Problem: Combination Sum (LC 39)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Find all unique combinations of numbers that sum to target (unlimited use of each).
- **How the interview goes**: Meta tests backtracking with pruning. Must handle infinite supply constraint.
- **Approach 1**: Backtracking with Pruning — O(2^(t/m)) time, O(t/m) space. t = target, m = min value.
- **Approach 2**: DP (for counting only) — O(n·t) time. Not suitable for listing combinations.
- **Java**:
```java
/**
 * Combination sum using backtracking.
 * Time: O(2^(t/m)), Space: O(t/m)
 */
public List<List<Integer>> combinationSum(int[] candidates, int target) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(candidates, target, 0, new ArrayList<>(), result);
    return result;
}
private void backtrack(int[] cand, int remain, int start, List<Integer> path, List<List<Integer>> res) {
    if (remain < 0) return;
    if (remain == 0) { res.add(new ArrayList<>(path)); return; }
    for (int i = start; i < cand.length; i++) {
        path.add(cand[i]);
        backtrack(cand, remain - cand[i], i, path, res);
        path.remove(path.size() - 1);
    }
}
```
- **What Meta evaluates**: Starting index to avoid duplicates, pruning strategy, infinite use handling.
- **Follow-up**: Combination sum II (LC 40) with limited supply. Combination sum III (LC 216) with K length.

### Lab 05-graphs
#### Problem: Alien Dictionary (LC 269)
- **Difficulty**: Hard | **Frequency**: High
- **Statement**: Determine the order of alien alphabet letters from a sorted dictionary of words.
- **How the interview goes**: Meta tests graph construction + topological sort. Must build edges from adjacent word comparison.
- **Approach 1**: BFS (Kahn's Algorithm) — O(C) time, O(1) space (26 chars). Build graph, compute indegrees.
- **Approach 2**: DFS Post-Order — O(C) time, O(1) space. Detect cycles via visited states.
- **Java**:
```java
/**
 * Alien dictionary using BFS topological sort.
 * Time: O(C), Space: O(1)
 */
public String alienOrder(String[] words) {
    int[] indegree = new int[26];
    Map<Character, Set<Character>> graph = new HashMap<>();
    for (String w : words) for (char c : w.toCharArray()) graph.putIfAbsent(c, new HashSet<>());
    for (int i = 0; i < words.length - 1; i++) {
        String a = words[i], b = words[i + 1];
        if (a.length() > b.length() && a.startsWith(b)) return "";
        for (int j = 0; j < Math.min(a.length(), b.length()); j++) {
            if (a.charAt(j) != b.charAt(j)) {
                if (graph.get(a.charAt(j)).add(b.charAt(j))) indegree[b.charAt(j) - 'a']++;
                break;
            }
        }
    }
    Queue<Character> q = new LinkedList<>();
    for (char c : graph.keySet()) if (indegree[c - 'a'] == 0) q.offer(c);
    StringBuilder sb = new StringBuilder();
    while (!q.isEmpty()) {
        char c = q.poll(); sb.append(c);
        for (char neighbor : graph.get(c)) {
            if (--indegree[neighbor - 'a'] == 0) q.offer(neighbor);
        }
    }
    return sb.length() == graph.size() ? sb.toString() : "";
}
```
- **What Meta evaluates**: Graph construction from implicit ordering, cycle detection, edge case of prefix comparison.
- **Follow-up**: Verify alien dictionary (LC 953) — given order, check if sorted.

## System Design Questions
1. **Design Facebook News Feed** — Heaps (Lab 09) for ranking, Graph (Lab 05) for social connections, Hashing (Lab 08) for caching feed items.
2. **Design Facebook Messenger** — Queue (Lab 07) for message delivery, Trie (Lab 17) for search, Arrays (Lab 01) for pagination.
3. **Design Instagram Stories** — Stack (Lab 06) for story navigation (LIFO), Heaps (Lab 09) for trending, Sorting (Lab 15) for chronological feeds.

## Behavioral Questions (STAR)
1. **Situation**: "Tell me about a time you moved fast and delivered." — **Task**: Crash fix within hours. **Action**: Replaced O(n²) nested loops with HashMap (Lab 08). **Result**: Fixed in 3 hours, 50× performance gain.
2. **Situation**: "How did you handle technical disagreement?" — **Task**: Team split on traversal method. **Action**: Benchmarked BFS vs DFS (Lab 05) on real data. **Result**: BFS chosen unanimously.
3. **Situation**: "Describe a time you failed and what you learned." — **Task**: Chose ArrayDeque when needing random removal. **Action**: Switched to LinkedHashSet after O(n) removal cost was discovered (Lab 08). **Result**: Reduced remove to O(1).
4. **Situation**: "Tell me about a time you took initiative." — **Task**: Performance bottleneck in search. **Action**: Implemented Trie (Lab 17) for prefix search without being asked. **Result**: Autocomplete latency dropped 70%.
5. **Situation**: "How do you handle ambiguous requirements?" — **Task**: Needed a "best" data structure. **Action**: Prototyped five structures (Labs 01-09) and benchmarked. **Result**: Chose combination of HashMap + Heap.

## Study Plan
1. **Weeks 1-2**: Lab 01-arrays, Lab 02-strings — Meta's most heavily tested areas
2. **Weeks 3-4**: Lab 04-trees, Lab 13-dynamic-programming — Core algorithmic topics
3. **Week 5**: Lab 11-recursion, Lab 12-backtracking — Combinatorial patterns
4. **Week 6**: Lab 05-graphs — Graph construction and traversal
5. **Weeks 7-8**: Mock interviews with time pressure, CoderPad practice focusing on speed

## Interview Tips
- **Pace**: Move fast. Meta interviewers expect you to start coding within 2-3 minutes of hearing the problem.
- **Communication**: Say your reasoning aloud while typing. Meta values real-time collaboration.
- **What impresses**: Clean working code on the first attempt, catching edge cases proactively, finishing with time to spare.
- **Avoid**: Long silences, over-analyzing without coding, writing overly complex solutions.
- **Coding style**: Write production-quality code. Variable names matter. Comments are unnecessary.
