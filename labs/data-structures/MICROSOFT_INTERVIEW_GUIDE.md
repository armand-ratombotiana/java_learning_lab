# Microsoft Interview Guide — Data Structures Academy

## Interview Process for DS Roles
- **Rounds**: Recruiter screen → 1-2 phone screens (coding + problem-solving) → 4-5 on-site rounds (2-3 coding, 1 system design, 1 behavioral/Azure-fit).
- **Timeline**: Phone screen → on-site within 3-4 weeks. Decision in 1-2 weeks.
- **Format**: Microsoft Teams live coding (VS Code or similar IDE shared via screen).
- **Focus**: Problem-solving process over final answer. Microsoft values how you think through problems.
- **Coding Environment**: Shared IDE (often VS Code). Some teams use Codility or HackerRank for phone screens.
- **Culture**: Growth mindset. They want to see you learn from hints and iterate your approach.

## Top Problems by Lab

### Lab 04-trees (Binary Trees)
#### Problem: Invert Binary Tree (LC 226)
- **Difficulty**: Easy | **Frequency**: Very High
- **Statement**: Invert a binary tree (mirror horizontally).
- **How the interview goes**: Microsoft's most famous tree question. Tests recursion fundamentals.
- **Approach 1**: Recursive DFS — O(n) time, O(h) space. Swap children, recurse.
- **Approach 2**: Iterative BFS — O(n) time, O(n) space. Queue-based level-order swap.
- **Java**:
```java
/**
 * Invert binary tree using recursion.
 * Time: O(n), Space: O(h)
 */
public TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    TreeNode temp = root.left;
    root.left = invertTree(root.right);
    root.right = invertTree(temp);
    return root;
}
```
- **What Microsoft evaluates**: Recursive thinking, understanding side effects vs return values, base case handling.
- **Follow-up**: Iterative solution. Symmetric tree check (LC 101). Make a tree from invert.

#### Problem: Maximum Depth of Binary Tree (LC 104)
- **Difficulty**: Easy | **Frequency**: Very High
- **Statement**: Find the maximum depth of a binary tree.
- **How the interview goes**: Microsoft uses this to test both recursion and iteration knowledge.
- **Approach 1**: Recursive — O(n) time, O(h) space. Depth = 1 + max(left, right).
- **Approach 2**: BFS Level-Order — O(n) time, O(n) space. Count levels.
- **Java**:
```java
/**
 * Maximum depth of binary tree using recursion.
 * Time: O(n), Space: O(h)
 */
public int maxDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
}
```
- **What Microsoft evaluates**: Clear recursive formulation, handling empty tree, knowing when recursion is appropriate.
- **Follow-up**: Balanced binary tree check (LC 110). Minimum depth (LC 111). Diameter (LC 543).

### Lab 13-dynamic-programming
#### Problem: Coin Change (LC 322)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Minimum number of coins to make a given amount with unlimited coins.
- **How the interview goes**: Microsoft tests bottom-up DP. Expect to discuss greedy counterexample first.
- **Approach 1**: Bottom-Up DP — O(amount·n) time, O(amount) space. Standard DP table.
- **Approach 2**: Top-Down Memoization — O(amount·n) time, O(amount) space. Recursive with cache.
- **Java**:
```java
/**
 * Coin change using bottom-up DP.
 * Time: O(amount*n), Space: O(amount)
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
- **What Microsoft evaluates**: DP initialization, unreachable state handling, infinite coins assumption.
- **Follow-up**: Ways to make change (LC 518). Limited coin supply. Return the combination.

#### Problem: Climbing Stairs (LC 70)
- **Difficulty**: Easy | **Frequency**: Very High
- **Statement**: Ways to climb n stairs taking 1 or 2 steps at a time.
- **How the interview goes**: Microsoft's DP warm-up. Fibonacci pattern with iterative optimization.
- **Approach 1**: DP Array — O(n) time, O(n) space. dp[i] = dp[i-1] + dp[i-2].
- **Approach 2**: Space-Optimized — O(n) time, O(1) space. Two variables.
- **Java**:
```java
/**
 * Climbing stairs using space-optimized DP.
 * Time: O(n), Space: O(1)
 */
public int climbStairs(int n) {
    if (n <= 2) return n;
    int prev2 = 1, prev1 = 2;
    for (int i = 3; i <= n; i++) {
        int curr = prev1 + prev2;
        prev2 = prev1;
        prev1 = curr;
    }
    return prev1;
}
```
- **What Microsoft evaluates**: Recognizing Fibonacci pattern, base cases (n=1, n=2), space optimization.
- **Follow-up**: Min cost climbing stairs (LC 746). 3-step variant. Print all ways (backtracking).

### Lab 01-arrays
#### Problem: Spiral Matrix (LC 54)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Return all elements of a matrix in spiral order.
- **How the interview goes**: Microsoft tests boundary handling and directional logic.
- **Approach 1**: Layer-by-Layer — O(m·n) time, O(1) extra space. Four boundaries shrink inward.
- **Approach 2**: Direction Vector — O(m·n) time, O(1) extra space. Right/Down/Left/Up cycling.
- **Java**:
```java
/**
 * Spiral matrix traversal using layer-by-layer approach.
 * Time: O(m*n), Space: O(1) extra
 */
public List<Integer> spiralOrder(int[][] matrix) {
    List<Integer> result = new ArrayList<>();
    int top = 0, bottom = matrix.length - 1, left = 0, right = matrix[0].length - 1;
    while (top <= bottom && left <= right) {
        for (int j = left; j <= right; j++) result.add(matrix[top][j]);
        top++;
        for (int i = top; i <= bottom; i++) result.add(matrix[i][right]);
        right--;
        if (top <= bottom) for (int j = right; j >= left; j--) result.add(matrix[bottom][j]);
        bottom--;
        if (left <= right) for (int i = bottom; i >= top; i--) result.add(matrix[i][left]);
        left++;
    }
    return result;
}
```
- **What Microsoft evaluates**: Boundary management, off-by-one errors, handling single-row/column matrices.
- **Follow-up**: Spiral matrix II (LC 59) — generate spiral. Diagonal traversal (LC 498).

#### Problem: Rotate Image (LC 48)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Rotate N×N matrix 90 degrees clockwise in place.
- **How the interview goes**: Microsoft tests in-place matrix manipulation. Group-of-four rotation.
- **Approach 1**: Transpose + Reverse — O(n²) time, O(1) space. Mathematics-based.
- **Approach 2**: Layer Rotation — O(n²) time, O(1) space. Rotate one ring at a time.
- **Java**:
```java
/**
 * Rotate image using transpose then reverse.
 * Time: O(n^2), Space: O(1)
 */
public void rotate(int[][] matrix) {
    int n = matrix.length;
    for (int i = 0; i < n; i++)
        for (int j = i; j < n; j++) {
            int temp = matrix[i][j]; matrix[i][j] = matrix[j][i]; matrix[j][i] = temp;
        }
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n / 2; j++) {
            int temp = matrix[i][j]; matrix[i][j] = matrix[i][n - 1 - j]; matrix[i][n - 1 - j] = temp;
        }
}
```
- **What Microsoft evaluates**: In-place constraint, loop bounds, transpose understanding.
- **Follow-up**: Counter-clockwise rotation. Rotate non-square matrix (resize + rotate).

### Lab 03-linked-lists
#### Problem: Reverse Nodes in K-Group (LC 25)
- **Difficulty**: Hard | **Frequency**: High
- **Statement**: Reverse nodes of a linked list in groups of K.
- **How the interview goes**: Microsoft's hardest linked list problem. Must handle partial groups.
- **Approach 1**: Recursive — O(n) time, O(n/k) stack space. Process group, reverse, connect.
- **Approach 2**: Iterative — O(n) time, O(1) space. Dummy node + group reversal.
- **Java**:
```java
/**
 * Reverse nodes in K-Group using recursion.
 * Time: O(n), Space: O(n/k)
 */
public ListNode reverseKGroup(ListNode head, int k) {
    ListNode curr = head;
    int count = 0;
    while (curr != null && count < k) { curr = curr.next; count++; }
    if (count < k) return head;
    ListNode prev = reverseKGroup(curr, k);
    ListNode node = head;
    for (int i = 0; i < k; i++) {
        ListNode next = node.next;
        node.next = prev;
        prev = node;
        node = next;
    }
    return prev;
}
```
- **What Microsoft evaluates**: Pointer manipulation, base case handling, recursion depth awareness.
- **Follow-up**: Swap nodes in pairs (LC 24). Reverse linked list II (LC 92).

#### Problem: Add Two Numbers (LC 2)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Add two numbers represented as linked lists (digits in reverse order).
- **How the interview goes**: Microsoft tests basic pointer traversal with carry handling.
- **Approach 1**: Iterative with Carry — O(max(n,m)) time, O(max(n,m)) space. Dummy head pattern.
- **Approach 2**: Recursive — O(max(n,m)) time, O(max(n,m)) stack space. Cleaner but more space.
- **Java**:
```java
/**
 * Add two numbers from linked lists.
 * Time: O(max(n,m)), Space: O(max(n,m))
 */
public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0), curr = dummy;
    int carry = 0;
    while (l1 != null || l2 != null || carry != 0) {
        int sum = carry;
        if (l1 != null) { sum += l1.val; l1 = l1.next; }
        if (l2 != null) { sum += l2.val; l2 = l2.next; }
        curr.next = new ListNode(sum % 10);
        curr = curr.next;
        carry = sum / 10;
    }
    return dummy.next;
}
```
- **What Microsoft evaluates**: Carry propagation, handling uneven list lengths, dummy node pattern.
- **Follow-up**: Digits forward order (LC 445) — reverse first. Multiply two numbers (LC 43).

### Lab 11-recursion
#### Problem: Reverse String (LC 344)
- **Difficulty**: Easy | **Frequency**: Very High
- **Statement**: Reverse a string in place.
- **How the interview goes**: Microsoft tests recursion fundamentals and two-pointer iteration.
- **Approach 1**: Recursive — O(n) time, O(n) stack. Swap ends, recurse inward.
- **Approach 2**: Two-Pointer — O(n) time, O(1) space. Iterative swap.
- **Java**:
```java
/**
 * Reverse string using two-pointer approach.
 * Time: O(n), Space: O(1)
 */
public void reverseString(char[] s) {
    int lo = 0, hi = s.length - 1;
    while (lo < hi) {
        char temp = s[lo];
        s[lo] = s[hi];
        s[hi] = temp;
        lo++; hi--;
    }
}
```
- **What Microsoft evaluates**: In-place constraint, two-pointer technique, recursion vs iteration.
- **Follow-up**: Reverse words in string (LC 151). Reverse vowels (LC 345). Recursive only.

#### Problem: Fibonacci Number (LC 509)
- **Difficulty**: Easy | **Frequency**: High
- **Statement**: Compute the Nth Fibonacci number.
- **How the interview goes**: Microsoft tests recursion understanding (naive vs optimized).
- **Approach 1**: Recursive — O(2^n) time, O(n) stack. Naive, shows why DP is needed.
- **Approach 2**: Iterative — O(n) time, O(1) space. Optimal.
- **Java**:
```java
/**
 * Fibonacci using iterative approach.
 * Time: O(n), Space: O(1)
 */
public int fib(int n) {
    if (n <= 1) return n;
    int a = 0, b = 1;
    for (int i = 2; i <= n; i++) {
        int sum = a + b;
        a = b; b = sum;
    }
    return b;
}
```
- **What Microsoft evaluates**: Understanding of recursion tree, exponential blowup, iterative optimization.
- **Follow-up**: Fibonacci with memoization. Fibonacci with matrix exponentiation (O(log n)).

### Lab 15-sorting
#### Problem: Sort Colors (LC 75)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Sort array of 0s, 1s, and 2s in place (Dutch national flag problem).
- **How the interview goes**: Microsoft tests three-way partitioning. Must avoid counting sort (uses extra pass).
- **Approach 1**: Dutch National Flag (One-pass) — O(n) time, O(1) space. Three pointers.
- **Approach 2**: Counting Sort — O(n) time, O(1) space. Two passes — count then overwrite.
- **Java**:
```java
/**
 * Sort colors using Dutch national flag algorithm.
 * Time: O(n), Space: O(1)
 */
public void sortColors(int[] nums) {
    int lo = 0, mid = 0, hi = nums.length - 1;
    while (mid <= hi) {
        if (nums[mid] == 0) {
            int temp = nums[lo]; nums[lo] = nums[mid]; nums[mid] = temp;
            lo++; mid++;
        } else if (nums[mid] == 1) {
            mid++;
        } else {
            int temp = nums[hi]; nums[hi] = nums[mid]; nums[mid] = temp;
            hi--;
        }
    }
}
```
- **What Microsoft evaluates**: Three-pointer logic, invariant maintenance, single-pass understanding.
- **Follow-up**: 4-way partitioning (0,1,2,3). Sort by parity (LC 905). Wiggle sort (LC 324).

#### Problem: Merge Sorted Array (LC 88)
- **Difficulty**: Easy | **Frequency**: Very High
- **Statement**: Merge two sorted arrays into first array (in-place, extra space at end).
- **How the interview goes**: Microsoft tests merge portion of mergesort. Must fill from end.
- **Approach 1**: Fill from End — O(m+n) time, O(1) space. Compare and place at end.
- **Approach 2**: Copy and Merge — O(m+n) time, O(m) space. Copy nums1, merge into nums1.
- **Java**:
```java
/**
 * Merge sorted arrays filling from end.
 * Time: O(m+n), Space: O(1)
 */
public void merge(int[] nums1, int m, int[] nums2, int n) {
    int i = m - 1, j = n - 1, k = m + n - 1;
    while (j >= 0) {
        if (i >= 0 && nums1[i] > nums2[j]) {
            nums1[k--] = nums1[i--];
        } else {
            nums1[k--] = nums2[j--];
        }
    }
}
```
- **What Microsoft evaluates**: In-place convention, filling from end, handling remaining elements.
- **Follow-up**: Merge sort implementation. Merge K sorted arrays.

### Lab 17-trie
#### Problem: Implement Trie (LC 208)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Implement a Trie with insert, search, and startsWith methods.
- **How the interview goes**: Microsoft tests OOP design + trie structure. Must handle insert and search correctly.
- **Approach 1**: Array-based Children — O(L) per operation, O(total characters) space. Fast access.
- **Approach 2**: HashMap Children — O(L) per operation, more flexible. Better for sparse alphabets.
- **Java**:
```java
/**
 * Trie implementation with array-based children.
 * Time: O(L) per operation, Space: O(total characters)
 */
class Trie {
    class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord;
    }
    private TrieNode root = new TrieNode();
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) node.children[idx] = new TrieNode();
            node = node.children[idx];
        }
        node.isWord = true;
    }
    public boolean search(String word) {
        TrieNode node = find(word);
        return node != null && node.isWord;
    }
    public boolean startsWith(String prefix) {
        return find(prefix) != null;
    }
    private TrieNode find(String s) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return null;
            node = node.children[idx];
        }
        return node;
    }
}
```
- **What Microsoft evaluates**: OOP design, node structure, prefix vs full word distinction.
- **Follow-up**: Word break (LC 139). Design add and search words (LC 211). Map sum pairs (LC 677).

## System Design Questions
1. **Design Azure Cosmos DB** — B-Tree (Lab 30) for indexing, Consistent Hashing (Lab 08) for sharding, Graph (Lab 05) for multi-region replication.
2. **Design Microsoft Teams** — Queue (Lab 07) for message ordering, Trie (Lab 17) for search, Heap (Lab 09) for priority-based notification.
3. **Design GitHub** — Tree (Lab 04) for commit history, Hash Map (Lab 08) for content addressable storage, Sorting (Lab 15) for activity feeds.

## Behavioral Questions (STAR)
1. **Situation**: "Tell me about a time you learned a new technology quickly." — **Task**: Needed to implement a search feature. **Action**: Learned Trie (Lab 17) in 2 days, built prototype. **Result**: 80% faster than existing solution.
2. **Situation**: "How did you use data to make a decision?" — **Task**: Choosing between sorting algorithms. **Action**: Benchmarked quicksort vs mergesort (Lab 15) on real data. **Result**: Mergesort chosen for stability requirement.
3. **Situation**: "Describe a collaborative problem-solving experience." — **Task**: Team debugging a tree traversal bug. **Action**: Pair-programmed iterative BFS (Lab 04/07). **Result**: Found the bug in 30 minutes.
4. **Situation**: "How do you handle feedback?" — **Task**: Peer reviewed my solution as suboptimal. **Action**: Refactored from O(n²) to O(n) using HashMap (Lab 08). **Result**: Performance improved 10×.
5. **Situation**: "Tell me about a time you took a risk." — **Task**: Rewrote core data loading module. **Action**: Replaced nested loops with HashSet (Lab 08). **Result**: Risk paid off — 99.9% uptime maintained.

## Study Plan
1. **Weeks 1-2**: Lab 04-trees, Lab 13-dynamic-programming — Core Microsoft topics
2. **Weeks 3-4**: Lab 01-arrays, Lab 03-linked-lists — Foundational DS manipulation
3. **Week 5**: Lab 11-recursion, Lab 15-sorting — Process-oriented thinking
4. **Week 6**: Lab 17-trie — Niche but growing frequency
5. **Weeks 7-8**: Mock interviews focusing on thorough process, not speed

## Interview Tips
- **Pace**: Walk through your thought process step by step. Microsoft values clarity over speed.
- **Communication**: Ask clarifying questions before solving. Use the "think-aloud" protocol.
- **What impresses**: Explaining trade-offs, showing multiple approaches, gracefully accepting hints.
- **Avoid**: Rushing to answers, dismissing alternative approaches, not testing edge cases.
- **Coding style**: Clean, well-structured code. Microsoft writes a lot of production software and expects production-quality interview code.
