# LeetCode Pattern Cheatsheet — Java Implementation Perspective

> **Algorithm patterns with Java-specific implementation techniques, API usage, and templates**
> Not just problem lists — this shows you HOW to implement each pattern in idiomatic Java.

---

## Table of Contents
1. [String Manipulation Patterns](#string-manipulation-patterns)
2. [Array Patterns](#array-patterns)
3. [Hash Table Patterns](#hash-table-patterns)
4. [Stack/Queue Patterns](#stackqueue-patterns)
5. [Tree Patterns](#tree-patterns)
6. [Graph Patterns](#graph-patterns)
7. [Heap Patterns](#heap-patterns)
8. [Dynamic Programming Patterns](#dynamic-programming-patterns)
9. [Sliding Window Patterns](#sliding-window-patterns)
10. [Two Pointer Patterns](#two-pointer-patterns)

---

## String Manipulation Patterns

### StringBuilder Pattern
```java
// Template: Building/modifying strings efficiently
String process(String s) {
    var sb = new StringBuilder();
    for (char c : s.toCharArray()) {
        if (condition) sb.append(c);
    }
    return sb.toString();  // O(n) time, O(n) space
}
```

### char[] vs String Pattern
```java
// Template: In-place string modification (fastest)
char[] chars = s.toCharArray();
// modify chars in-place
return new String(chars);

// When to use char[]: frequent modifications, swapping, two-pointer on string
// When to use String: immutability needed, hash map keys, readability
```

### Substring Tricks
```java
// Template: Sliding substring with char[] (avoids substring O(n) copies pre-Java 7u6)
int start = 0, end = n;
String result = s.substring(start, end);  // O(n) in Java 6, O(1) since Java 7u6

// For Java 21+: use StringBuffer / StringBuilder for mutable string building
// Use s.charAt(i) not s[i] (Java strings aren't arrays)
```

**Java API Tips:**
- `s.charAt(i)` is O(1) — use it over `s.toCharArray()` when reading is the primary operation
- `s.substring()` is O(1) since Java 7u6 (before: O(n) — avoid in loops)
- For palindrome checks: `new StringBuilder(s).reverse().toString().equals(s)`
- `String.join(",", list)` for clean concatenation
- `s.repeat(n)` (Java 11+) to repeat strings

**Complexity:** O(n) time, O(n) space for StringBuilder
**Company Frequency:** Google (30%), Meta (25%), Amazon (20%)
**LeetCode References:** 5 Longest Palindromic Substring, 3 Longest Substring Without Repeating Characters, 424 Longest Repeating Character Replacement, 647 Palindromic Substrings, 791 Custom Sort String, 125 Valid Palindrome

---

## Array Patterns

### Two-Pointer in Java
```java
// Template: Two-pointer with primitive array
int twoSum(int[] nums, int target) {
    int l = 0, r = nums.length - 1;
    while (l < r) {
        int sum = nums[l] + nums[r];
        if (sum == target) return ...;
        if (sum < target) l++; else r--;
    }
    return -1;
}
```

### Sorting with Comparator
```java
// Template: Custom sort with Comparator
Arrays.sort(arr, (a, b) -> Integer.compare(a, b));  // ascending
Arrays.sort(arr, (a, b) -> b - a);                   // ascending (watch for overflow!)
Arrays.sort(arr, Comparator.reverseOrder());          // Objects only, not primitives

// Multi-key sorting
Arrays.sort(intervals, (a, b) -> {
    if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
    return Integer.compare(a[1], b[1]);
});
```

### Arrays Utility Patterns
```java
// Fill & copy
Arrays.fill(arr, -1);
int[] copy = Arrays.copyOf(original, newLength);
int[] partial = Arrays.copyOfRange(arr, from, to);

// Search
int idx = Arrays.binarySearch(arr, key);     // requires sorted array

// Equality & hash
boolean eq = Arrays.equals(a, b);
int hash = Arrays.hashCode(arr);              // used by List, Map

// Parallel prefix (Java 8+)
Arrays.parallelPrefix(arr, Integer::sum);
```

**Java API Tips:**
- Use `Arrays.asList()` for fixed-size list backed by array
- `List.of()` (Java 9+) creates an immutable list — can't sort it
- For ArrayList sorting: `list.sort(Comparator.naturalOrder())`
- `Collections.reverse(list)` for in-place reversal
- Primitive arrays are faster than `List<Integer>` — avoid boxing in hot paths

**Complexity:** O(n) for traversal, O(n log n) for sorting
**Company Frequency:** Amazon (35%), Google (30%), Meta (25%)
**LeetCode References:** 1 Two Sum, 11 Container With Most Water, 15 3Sum, 56 Merge Intervals, 75 Sort Colors, 121 Best Time to Buy and Sell Stock, 283 Move Zeroes

---

## Hash Table Patterns

### HashMap vs TreeMap vs LinkedHashMap

| Map | Ordering | Null Keys | Thread-Safe | Use Case |
|-----|----------|-----------|-------------|----------|
| HashMap | None | One null key | No | General purpose, O(1) lookups |
| LinkedHashMap | Insertion/Access order | One null key | No | LRU cache, predictable iteration |
| TreeMap | Sorted (Comparable) | No null keys | No | Range queries, floor/ceiling |
| ConcurrentHashMap | None | No null keys | Yes | Multi-threaded access |
| IdentityHashMap | Identity (==) | Yes | No | Reference-equality, internals |

### computeIfAbsent Pattern
```java
// Template: Grouping with computeIfAbsent —cleaner than putIfAbsent
Map<Integer, List<String>> map = new HashMap<>();
map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);

// Map of maps pattern
Map<String, Map<String, Integer>> nested = new HashMap<>();
nested.computeIfAbsent(group, k -> new HashMap<>())
      .merge(key, 1, Integer::sum);

// Cache with lazy initialization
private final Map<Integer, int[]> memo = new HashMap<>();
public int[] getResult(int n) {
    return memo.computeIfAbsent(n, this::expensiveCompute);
}
```

### Stream Collectors Patterns
```java
// Counting frequency
Map<String, Long> freq = list.stream()
    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

// Partition into two groups
Map<Boolean, List<Integer>> partitioned = nums.stream()
    .collect(Collectors.partitioningBy(n -> n > 0));

// Multi-level grouping
Map<String, Map<Integer, List<Item>>> grouped = items.stream()
    .collect(Collectors.groupingBy(Item::category,
             Collectors.groupingBy(Item::priority)));
```

**Java API Tips:**
- `Map.merge(key, 1, Integer::sum)` for atomic frequency increment
- `computeIfAbsent` + `computeIfPresent` avoid double map lookups
- `HashMap` with initial capacity: `new HashMap<>(expectedSize / 0.75f + 1)` to avoid resizing
- Use `EnumMap` for enum keys — O(1) and more memory-efficient
- `TreeMap.floorKey()` / `ceilingKey()` for range queries

**Complexity:** O(1) average for HashMap, O(log n) for TreeMap
**Company Frequency:** Amazon (40%), Google (30%), Meta (20%)
**LeetCode References:** 1 Two Sum, 49 Group Anagrams, 128 Longest Consecutive Sequence, 146 LRU Cache, 347 Top K Frequent Elements, 560 Subarray Sum Equals K, 981 Time Based Key-Value Store

---

## Stack/Queue Patterns

### ArrayDeque vs Stack

```java
// DON'T use Stack class (it's synchronized, extends Vector — legacy)
// DO use ArrayDeque for both stack and queue

// As a Stack (LIFO)
Deque<Integer> stack = new ArrayDeque<>();
stack.push(1);          // addFirst
int top = stack.peek(); // peekFirst
int val = stack.pop();  // removeFirst

// As a Queue (FIFO)
Deque<Integer> queue = new ArrayDeque<>();
queue.offer(1);         // addLast
int front = queue.peek(); // peekFirst
int val = queue.poll(); // removeFirst

// As a Deque (double-ended)
deque.addFirst(x);  deque.addLast(y);
deque.removeFirst(); deque.removeLast();
```

### Monotonic Stack/Queue
```java
// Template: Next Greater Element (monotonic decreasing stack)
int[] nextGreaterElement(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);
    Deque<Integer> stack = new ArrayDeque<>();  // stores indices
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
            result[stack.pop()] = nums[i];
        }
        stack.push(i);
    }
    return result;
}

// Template: Monotonic queue for sliding window max
Deque<Integer> dq = new ArrayDeque<>();  // decreasing order indices
for (int i = 0; i < n; i++) {
    while (!dq.isEmpty() && dq.peekFirst() < i - k + 1) dq.pollFirst();
    while (!dq.isEmpty() && nums[dq.peekLast()] <= nums[i]) dq.pollLast();
    dq.offerLast(i);
    if (i >= k - 1) result[i - k + 1] = nums[dq.peekFirst()];
}
```

**Java API Tips:**
- `ArrayDeque` resizes like ArrayList — amortized O(1) for all operations
- No capacity restriction on `ArrayDeque` (unlike `LinkedList`)
- `PriorityQueue` is a heap, not a queue for FIFO
- For thread-safe: `ConcurrentLinkedDeque` or `LinkedBlockingDeque`
- `Collections.asLifoQueue(Deque)` wraps a Deque as a LIFO Queue

**Complexity:** O(n) amortized per operation
**Company Frequency:** Amazon (30%), Google (25%), Meta (20%)
**LeetCode References:** 20 Valid Parentheses, 155 Min Stack, 239 Sliding Window Maximum, 739 Daily Temperatures, 84 Largest Rectangle in Histogram, 853 Car Fleet

---

## Tree Patterns

### Recursion Pattern
```java
// Template: Binary Tree DFS (recursion)
void dfs(TreeNode root) {
    if (root == null) return;
    // pre-order: process root
    dfs(root.left);
    // in-order: process root
    dfs(root.right);
    // post-order: process root
}

// Template: Return value from recursion
int treeMaxDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(treeMaxDepth(root.left), treeMaxDepth(root.right));
}
```

### TreeNode Manipulation
```java
// Template: Invert binary tree
TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    TreeNode left = invertTree(root.left);
    TreeNode right = invertTree(root.right);
    root.left = right;
    root.right = left;
    return root;
}

// Template: Build tree from traversals
TreeNode buildTree(int[] preorder, int[] inorder) {
    // Use HashMap for O(1) inorder index lookup
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < inorder.length; i++) map.put(inorder[i], i);
    return build(preorder, 0, preorder.length - 1, inorder, 0, inorder.length - 1, map);
}
```

### BST Patterns
```java
// Template: BST search
TreeNode searchBST(TreeNode root, int val) {
    while (root != null && root.val != val) {
        root = val < root.val ? root.left : root.right;
    }
    return root;  // O(log n) average, O(n) worst
}

// Template: BST validation
boolean isValidBST(TreeNode root) {
    return validate(root, null, null);
}
boolean validate(TreeNode node, Integer low, Integer high) {
    if (node == null) return true;
    if (low != null && node.val <= low) return false;
    if (high != null && node.val >= high) return false;
    return validate(node.left, low, node.val) && validate(node.right, node.val, high);
}
```

**Java API Tips:**
- Use `Deque<TreeNode>` for iterative traversal (avoid recursion stack overflow)
- `Deque<TreeNode> stack = new ArrayDeque<>()` for iterative DFS
- `Deque<TreeNode> queue = new ArrayDeque<>()` for BFS
- Recursion depth > 1000 will `StackOverflowError` — use iterative for deep trees
- `TreeSet` and `TreeMap` are backed by Red-Black trees (self-balancing BSTs)

**Complexity:** O(n) traversal, O(log n) BST search
**Company Frequency:** Google (30%), Amazon (30%), Meta (25%)
**LeetCode References:** 94 Binary Tree Inorder Traversal, 98 Validate BST, 102 Level Order Traversal, 105 Construct Tree from Preorder/Inorder, 124 Binary Tree Max Path Sum, 236 LCA of Binary Tree, 297 Serialize and Deserialize Binary Tree

---

## Graph Patterns

### Adjacency List Pattern
```java
// Template: Build adjacency list
Map<Integer, List<Integer>> graph = new HashMap<>();
// or: List<List<Integer>> graph = new ArrayList<>(n);
for (int[] edge : edges) {
    graph.computeIfAbsent(edge[0], k -> new ArrayList<>()).add(edge[1]);
    graph.computeIfAbsent(edge[1], k -> new ArrayList<>()).add(edge[0]); // undirected
}
```

### BFS Template
```java
// Template: BFS shortest path (unweighted)
int bfs(Map<Integer, List<Integer>> graph, int start, int target) {
    Deque<Integer> queue = new ArrayDeque<>();
    Set<Integer> visited = new HashSet<>();
    queue.offer(start);
    visited.add(start);
    int steps = 0;
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            int node = queue.poll();
            if (node == target) return steps;
            for (int neighbor : graph.getOrDefault(node, List.of())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        steps++;
    }
    return -1;
}
```

### DFS Template
```java
// Template: DFS with backtracking
void dfs(Map<Integer, List<Integer>> graph, int node, Set<Integer> visited, List<Integer> path) {
    visited.add(node);
    path.add(node);
    for (int neighbor : graph.getOrDefault(node, List.of())) {
        if (!visited.contains(neighbor)) {
            dfs(graph, neighbor, visited, path);
        }
    }
}
```

**Java API Tips:**
- `HashSet<Integer>` for visited tracking — O(1) contains check
- For large graphs: use `BitSet` or `boolean[]` visited instead of Set (faster, less memory)
- `ArrayDeque` as queue for BFS, as stack for DFS
- Use `int[]` arrays instead of `List<Integer>` for adjacency when nodes are 0..n-1
- Dijkstra: `PriorityQueue<int[]>` with Comparator on distance

**Complexity:** O(V + E) time, O(V) space
**Company Frequency:** Google (30%), Amazon (25%), Meta (20%)
**LeetCode References:** 200 Number of Islands, 207 Course Schedule, 210 Course Schedule II, 323 Number of Connected Components, 994 Rotting Oranges, 417 Pacific Atlantic Water Flow, 133 Clone Graph

---

## Heap Patterns

### PriorityQueue with Comparator
```java
// Template: Min-heap (default — smallest element at top)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

// Template: Max-heap
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
// or: new PriorityQueue<>((a, b) -> b - a);  // watch for integer overflow!

// Template: Custom object heap
PriorityQueue<int[]> heap = new PriorityQueue<>(
    (a, b) -> Integer.compare(a[0], b[0])  // min-heap by first element
);
```

### Two-Heap Pattern (Median Finding)
```java
// Template: Median from data stream
class MedianFinder {
    private PriorityQueue<Integer> small;  // max-heap
    private PriorityQueue<Integer> large;  // min-heap

    public MedianFinder() {
        small = new PriorityQueue<>(Comparator.reverseOrder());
        large = new PriorityQueue<>();
    }

    public void addNum(int num) {
        small.offer(num);
        large.offer(small.poll());
        if (small.size() < large.size()) {
            small.offer(large.poll());
        }
    }

    public double findMedian() {
        return small.size() > large.size()
            ? small.peek()
            : (small.peek() + large.peek()) / 2.0;
    }
}
```

**Java API Tips:**
- `PriorityQueue` is unbounded — grows dynamically (can OOM if misused)
- Initial capacity: `new PriorityQueue<>(initialSize)` avoids resizing
- Iterator does NOT guarantee order — must poll to get sorted elements
- `Comparator.naturalOrder()` for ascending, `Comparator.reverseOrder()` for descending
- PriorityQueue is NOT thread-safe — use `PriorityBlockingQueue` for concurrent access
- For delay-scheduled tasks: `DelayQueue`

**Complexity:** O(log n) offer/poll, O(1) peek
**Company Frequency:** Amazon (30%), Google (25%), Facebook (20%)
**LeetCode References:** 215 Kth Largest Element, 295 Find Median from Data Stream, 347 Top K Frequent Elements, 23 Merge k Sorted Lists, 378 Kth Smallest in Sorted Matrix, 973 K Closest to Origin

---

## Dynamic Programming Patterns

### Memoization Pattern (Top-Down)
```java
// Template: DP with memoization using HashMap or array
class Solution {
    private Map<Integer, Integer> memo = new HashMap<>();

    public int fibonacci(int n) {
        return fib(n);
    }

    private int fib(int n) {
        if (n <= 1) return n;
        return memo.computeIfAbsent(n, k -> fib(k - 1) + fib(k - 2));
    }
}

// Template: 2D memoization with int[][] (faster than Map)
private int[][] memo;
// Initialize: memo = new int[m][n];
// Fill: Arrays.stream(memo).forEach(row -> Arrays.fill(row, -1));
```

### Tabulation Pattern (Bottom-Up)
```java
// Template: Tabulation with array (usually faster)
int dp(int n) {
    int[] dp = new int[n + 1];
    dp[0] = 0; dp[1] = 1;
    for (int i = 2; i <= n; i++) {
        dp[i] = dp[i - 1] + dp[i - 2];
    }
    return dp[n];
}

// Template: Space-optimized (O(1) space)
int fibOptimized(int n) {
    int prev2 = 0, prev1 = 1;
    for (int i = 2; i <= n; i++) {
        int curr = prev1 + prev2;
        prev2 = prev1;
        prev1 = curr;
    }
    return prev1;
}
```

**Java API Tips:**
- `int[]` memo is faster than `Map<Integer, Integer>` — prefer primitives
- For 2D DP: `int[][]` or `Integer[][]` (null = uncomputed)
- `Arrays.fill(dp, -1)` to mark uncomputed states
- `Stream.generate(() -> null).limit(n).toArray()` for lazy init
- Long running DP: use `long[]` instead of `int[]` to avoid overflow
- Java's recursion limit (~10,000) may not be enough — use iterative tabulation

**Complexity:** O(n) to O(n²) depending on problem
**Company Frequency:** Amazon (30%), Google (30%), Meta (25%)
**LeetCode References:** 70 Climbing Stairs, 198 House Robber, 300 Longest Increasing Subsequence, 322 Coin Change, 1143 Longest Common Subsequence, 416 Partition Equal Subset Sum, 62 Unique Paths, 72 Edit Distance

---

## Sliding Window Patterns

### int[] Frequency Pattern (Best for limited character sets)
```java
// Template: Fixed alphabet sliding window with int[26]
int slidingWindow(String s) {
    int[] freq = new int[26];
    int l = 0, maxLen = 0;
    for (int r = 0; r < s.length(); r++) {
        freq[s.charAt(r) - 'a']++;
        while (invalidCondition(freq)) {
            freq[s.charAt(l) - 'a']--;
            l++;
        }
        maxLen = Math.max(maxLen, r - l + 1);
    }
    return maxLen;
}
```

### HashMap<T, Integer> Pattern (For general objects)
```java
// Template: Sliding window with HashMap
int slidingWindowMap(int[] nums, int k) {
    Map<Integer, Integer> map = new HashMap<>();
    int l = 0, result = 0;
    for (int r = 0; r < nums.length; r++) {
        map.merge(nums[r], 1, Integer::sum);
        while (map.size() > k) {
            map.merge(nums[l], -1, Integer::sum);
            map.remove(nums[l], 0);  // remove if count == 0
            l++;
        }
        result = Math.max(result, r - l + 1);
    }
    return result;
}
```

**Java API Tips:**
- `int[256]` for ASCII / extended ASCII (faster than HashMap)
- `int[26]` for lowercase letters only — use `s.charAt(i) - 'a'`
- `Map.merge(key, 1, Integer::sum)` to increment; `map.remove(key, 0)` to clean
- `s.chars().collect(HashMap::new, ...)` for stream-based frequency
- Use `cnt[s.charAt(r)]++` when range is small — avoid HashMap overhead
- For counting with negative values: `Map.merge(key, delta, Integer::sum)`

**Complexity:** O(n) time, O(k) space (k = window size or alphabet size)
**Company Frequency:** Amazon (35%), Google (25%), Meta (20%)
**LeetCode References:** 3 Longest Substring Without Repeating, 76 Minimum Window Substring, 424 Longest Repeating Character Replacement, 567 Permutation in String, 904 Fruit Into Baskets, 438 Find All Anagrams in a String

---

## Two Pointer Patterns

### While Loop Pattern
```java
// Template: Standard two-pointer
public void twoPointer(int[] nums) {
    int l = 0, r = nums.length - 1;
    while (l < r) {
        if (condition(nums[l], nums[r])) {
            l++;
        } else {
            r--;
        }
    }
}
```

### Slow/Fast Pointer (Floyd's Algorithm)
```java
// Template: Detect cycle in linked list
boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}

// Template: Find middle of linked list
ListNode findMiddle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    return slow;
}
```

### Three-Pointer / Partitioning
```java
// Template: Dutch National Flag (3-way partition)
void sortColors(int[] nums) {
    int l = 0, m = 0, r = nums.length - 1;
    while (m <= r) {
        if (nums[m] == 0)      swap(nums, l++, m++);
        else if (nums[m] == 1) m++;
        else                    swap(nums, m, r--);
    }
}
```

**Java API Tips:**
- `while (l < r)` is the standard loop — never `l <= r` unless including middle
- Use `l + (r - l) / 2` to avoid overflow (not `(l + r) / 2`)
- `int mid = (l + r) >>> 1` for unsigned shift (faster, Java-specific)
- For linked list two-pointer: `ListNode` dummy head pattern: `ListNode dummy = new ListNode(0); dummy.next = head;`
- `Collections.reverse(list)` vs two-pointer on array — arrays are faster
- For List: `list.get(l)` and `list.set(l, val)` — O(1) for ArrayList, O(n) for LinkedList

**Complexity:** O(n) time, O(1) space
**Company Frequency:** Amazon (35%), Google (25%), Meta (20%)
**LeetCode References:** 11 Container With Most Water, 15 3Sum, 19 Remove Nth from End, 75 Sort Colors, 125 Valid Palindrome, 141 Linked List Cycle, 167 Two Sum II, 283 Move Zeroes

---

## Pattern Selection Decision Tree

```
What's the input?
├── String → Single traversal?
│   ├── Yes → Two-pointer or Sliding window
│   ├── Complexity needed? → StringBuilder + char[]
│   └── Pattern matching? → HashMap for character frequency
├── Array → Need ordering?
│   ├── Yes → Sort first, then two-pointer / binary search
│   ├── Need subarray / subsequence? → Sliding window / DP
│   └── Need top K? → Heap / QuickSelect
├── Tree → Need traversal?
│   ├── All nodes? → BFS (level) / DFS (pre/in/post)
│   ├── BST? → In-order traversal, binary search
│   └── Path problem? → DFS with backtracking
├── Graph → Need shortest path?
│   ├── Unweighted → BFS
│   ├── Weighted → Dijkstra (PriorityQueue)
│   └── All paths → DFS / Backtracking
└── Combinatorial → Need optimal value?
    ├── Yes, with optimal substructure → DP (memo/tab)
    └── Yes, with greedy choice → Greedy + sort/priority queue
```
