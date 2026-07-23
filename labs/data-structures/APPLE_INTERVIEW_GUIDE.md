# Apple Interview Guide — Data Structures Academy

## Interview Process for DS Roles
- **Rounds**: Recruiter call → 1-2 technical phone screens → 5-6 on-site rounds (4-5 technical + 1 behavioral/Apple culture fit).
- **Timeline**: Phone screen → on-site within 3-5 weeks. Decision 1-2 weeks.
- **Format**: Whiteboard or iPad-based coding for on-site. Remote interviews use shared screen (typically Xcode or VS Code).
- **Focus**: Attention to detail, memory efficiency, and correctness on edge cases. Apple emphasizes elegant solutions.
- **Coding Environment**: Whiteboard for in-person. Some teams use CoderPad or remote Xcode.
- **Culture**: Secrecy is paramount. Apple interviewers rarely share feedback details. Focus on the complete user experience of your code.

## Top Problems by Lab

### Lab 03-linked-lists
#### Problem: Add Two Numbers (LC 2)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Add two numbers represented as linked lists (digits stored in reverse order).
- **How the interview goes**: Apple tests pointer manipulation and carry handling. Expect detailed edge case discussion.
- **Approach 1**: Iterative with Carry — O(max(m,n)) time, O(max(m,n)) space. Dummy node pattern.
- **Approach 2**: Recursive — O(max(m,n)) time, O(max(m,n)) stack. Elegant but more stack overhead.
- **Java**:
```java
/**
 * Add two linked list numbers with carry.
 * Time: O(max(m,n)), Space: O(max(m,n))
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
- **What Apple evaluates**: Clean pointer handling, carry propagation, memory allocation for result.
- **Follow-up**: Forward order digits (LC 445). Multiply two numbers. Represent decimal.

#### Problem: Copy List with Random Pointer (LC 138)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Deep copy a linked list where each node has a random pointer.
- **How the interview goes**: Apple tests three-phase approach. Must handle interleaved random references.
- **Approach 1**: HashMap + Iteration — O(n) time, O(n) space. Map old → new nodes.
- **Approach 2**: Interleaving (Three Pass) — O(n) time, O(1) extra space. Insert cloned nodes between originals.
- **Java**:
```java
/**
 * Deep copy linked list with random pointers using interleaving.
 * Time: O(n), Space: O(1) extra
 */
public Node copyRandomList(Node head) {
    if (head == null) return null;
    Node curr = head;
    while (curr != null) {
        Node clone = new Node(curr.val);
        clone.next = curr.next;
        curr.next = clone;
        curr = clone.next;
    }
    curr = head;
    while (curr != null) {
        if (curr.random != null) curr.next.random = curr.random.next;
        curr = curr.next.next;
    }
    Node dummy = new Node(0), cloneCurr = dummy;
    curr = head;
    while (curr != null) {
        cloneCurr.next = curr.next;
        cloneCurr = cloneCurr.next;
        curr.next = curr.next.next;
        curr = curr.next;
    }
    return dummy.next;
}
```
- **What Apple evaluates**: Deep copy understanding, O(1) space optimization, pointer reassignment order.
- **Follow-up**: Unmodifiable list (defensive copy). Cyclic list detection.

### Lab 04-trees
#### Problem: Validate Binary Search Tree (LC 98)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Determine if a binary tree is a valid binary search tree.
- **How the interview goes**: Apple expects bounds propagation. Discuss common pitfalls (only checking immediate children).
- **Approach 1**: Bounds Propagation — O(n) time, O(h) space. Pass min/max constraints down.
- **Approach 2**: In-order Traversal — O(n) time, O(h) space. Previous element tracking.
- **Java**:
```java
/**
 * Validate BST using in-order traversal.
 * Time: O(n), Space: O(h)
 */
public boolean isValidBST(TreeNode root) {
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode prev = null, curr = root;
    while (!stack.isEmpty() || curr != null) {
        while (curr != null) { stack.push(curr); curr = curr.left; }
        curr = stack.pop();
        if (prev != null && prev.val >= curr.val) return false;
        prev = curr;
        curr = curr.right;
    }
    return true;
}
```
- **What Apple evaluates**: BST property understanding, iterative in-order, handling edge cases (duplicates, null).
- **Follow-up**: Recover BST (LC 99). BST iterator (LC 173). Convert sorted array to BST (LC 108).

#### Problem: Same Tree (LC 100)
- **Difficulty**: Easy | **Frequency**: High
- **Statement**: Check if two binary trees are structurally identical.
- **How the interview goes**: Apple tests recursive thinking and null handling. Simple but foundational.
- **Approach 1**: Recursive — O(n) time, O(h) space. Check both subtrees.
- **Approach 2**: Iterative BFS — O(n) time, O(n) space. Use two queues.
- **Java**:
```java
/**
 * Same tree check using recursion.
 * Time: O(n), Space: O(h)
 */
public boolean isSameTree(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null || p.val != q.val) return false;
    return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
}
```
- **What Apple evaluates**: Symmetric thinking, null handling granularity, short-circuit evaluation.
- **Follow-up**: Symmetric tree (LC 101). Subtree of another tree (LC 572).

### Lab 13-dynamic-programming
#### Problem: Maximum Subarray (LC 53)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Find contiguous subarray with the largest sum.
- **How the interview goes**: Apple expects Kadane's algorithm. Must handle all-negative arrays correctly.
- **Approach 1**: Kadane's Algorithm — O(n) time, O(1) space. Track current max and global max.
- **Approach 2**: Divide and Conquer — O(n log n) time, O(log n) space. Split, combine left/right/cross.
- **Java**:
```java
/**
 * Maximum subarray using Kadane's algorithm.
 * Time: O(n), Space: O(1)
 */
public int maxSubArray(int[] nums) {
    int maxEndingHere = nums[0], maxSoFar = nums[0];
    for (int i = 1; i < nums.length; i++) {
        maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
        maxSoFar = Math.max(maxSoFar, maxEndingHere);
    }
    return maxSoFar;
}
```
- **What Apple evaluates**: Iterative DP understanding, handling all-negative input, tracking state correctly.
- **Follow-up**: Return the subarray. Maximum product subarray (LC 152). Circular subarray (LC 918).

#### Problem: House Robber II (LC 213)
- **Difficulty**: Medium | **Frequency**: Medium
- **Statement**: Max sum from non-adjacent elements in a circular array.
- **How the interview goes**: Apple tests breaking circular constraint into two linear subproblems.
- **Approach 1**: Two Runs — O(n) time, O(1) space. Run linear house robber on [0..n-2] and [1..n-1].
- **Approach 2**: DP with state — O(n) time, O(n) space. Track first house robbed state.
- **Java**:
```java
/**
 * House robber II using two linear runs.
 * Time: O(n), Space: O(1)
 */
public int rob(int[] nums) {
    if (nums.length == 1) return nums[0];
    return Math.max(robLinear(nums, 0, nums.length - 2), robLinear(nums, 1, nums.length - 1));
}
private int robLinear(int[] nums, int lo, int hi) {
    int prev = 0, curr = 0;
    for (int i = lo; i <= hi; i++) {
        int temp = curr;
        curr = Math.max(curr, prev + nums[i]);
        prev = temp;
    }
    return curr;
}
```
- **What Apple evaluates**: Reducing circular to linear, function reuse, base case handling.
- **Follow-up**: House robber III (LC 337) tree. Paint house (LC 256).

### Lab 06-stacks
#### Problem: Min Stack (LC 155)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Design stack with push, pop, top, and getMin in O(1).
- **How the interview goes**: Apple tests OOD + satisfaction of O(1) constraint for all operations.
- **Approach 1**: Two Stacks — O(1) per operation, O(n) space. Main stack + min stack.
- **Approach 2**: Single Stack with Pairs — O(1) per operation, O(n) space. Stack of (value, min) pairs.
- **Java**:
```java
/**
 * Min stack using pair storage.
 * Time: O(1) per operation, Space: O(n)
 */
class MinStack {
    private Deque<int[]> stack = new ArrayDeque<>();
    public void push(int val) {
        int min = stack.isEmpty() ? val : Math.min(val, stack.peek()[1]);
        stack.push(new int[]{val, min});
    }
    public void pop() { stack.pop(); }
    public int top() { return stack.peek()[0]; }
    public int getMin() { return stack.peek()[1]; }
}
```
- **What Apple evaluates**: Constraint satisfaction, space efficiency, elegant design.
- **Follow-up**: Constant space using encoded values. Max stack (LC 716).

#### Problem: Daily Temperatures (LC 739)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: For each day, days until warmer temperature appears.
- **How the interview goes**: Apple tests monotonic stack pattern. Discuss brute force first.
- **Approach 1**: Monotonic Decreasing Stack — O(n) time, O(n) space. Store indices.
- **Approach 2**: Reverse Scan — O(n) time, O(1) extra space. Jump-based optimization.
- **Java**:
```java
/**
 * Daily temperatures using monotonic stack.
 * Time: O(n), Space: O(n)
 */
public int[] dailyTemperatures(int[] temperatures) {
    int n = temperatures.length;
    int[] result = new int[n];
    Deque<Integer> stack = new ArrayDeque<>();
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
            int idx = stack.pop();
            result[idx] = i - idx;
        }
        stack.push(i);
    }
    return result;
}
```
- **What Apple evaluates**: Monotonic stack pattern recognition, index storage, result array construction.
- **Follow-up**: Next greater element I (LC 496). Next greater element II (LC 503) circular.

### Lab 08-hashing
#### Problem: Contains Duplicate II (LC 219)
- **Difficulty**: Easy | **Frequency**: Medium
- **Statement**: Check if any duplicate values exist within distance K in array.
- **How the interview goes**: Apple tests sliding window with hash set. Must manage window boundaries.
- **Approach 1**: Sliding Window + HashSet — O(n) time, O(k) space.
- **Approach 2**: HashMap — O(n) time, O(n) space. Track last seen index.
- **Java**:
```java
/**
 * Contains duplicate II using sliding window hash set.
 * Time: O(n), Space: O(k)
 */
public boolean containsNearbyDuplicate(int[] nums, int k) {
    Set<Integer> window = new HashSet<>();
    for (int i = 0; i < nums.length; i++) {
        if (window.contains(nums[i])) return true;
        window.add(nums[i]);
        if (window.size() > k) window.remove(nums[i - k]);
    }
    return false;
}
```
- **What Apple evaluates**: Sliding window management, space optimization to O(k), edge cases.
- **Follow-up**: Contains duplicate III (LC 220) with value difference ≤ t. Use TreeSet.

#### Problem: Design HashMap (LC 706)
- **Difficulty**: Easy | **Frequency**: Medium
- **Statement**: Design a HashMap without using built-in hash table libraries.
- **How the interview goes**: Apple tests understanding of collision resolution (chaining) and resizing.
- **Approach 1**: Array + Linked List Chaining — O(1) average, O(n) worst case.
- **Approach 2**: Open Addressing — O(1) average. More compact but complex deletion.
- **Java**:
```java
/**
 * HashMap design using chaining.
 * Time: O(1) average, Space: O(n)
 */
class MyHashMap {
    class Node { int key, val; Node next; Node(int k, int v) { key = k; val = v; } }
    private Node[] buckets;
    private static final int SIZE = 10000;
    public MyHashMap() { buckets = new Node[SIZE]; }
    public void put(int key, int value) {
        int idx = key % SIZE;
        if (buckets[idx] == null) { buckets[idx] = new Node(-1, -1); }
        Node prev = buckets[idx];
        while (prev.next != null) {
            if (prev.next.key == key) { prev.next.val = value; return; }
            prev = prev.next;
        }
        prev.next = new Node(key, value);
    }
    public int get(int key) {
        int idx = key % SIZE;
        Node curr = buckets[idx];
        while (curr != null) {
            if (curr.key == key) return curr.val;
            curr = curr.next;
        }
        return -1;
    }
    public void remove(int key) {
        int idx = key % SIZE;
        Node prev = buckets[idx];
        while (prev != null && prev.next != null) {
            if (prev.next.key == key) { prev.next = prev.next.next; return; }
            prev = prev.next;
        }
    }
}
```
- **What Apple evaluates**: Hash function design, collision strategy, dummy node pattern.
- **Follow-up**: Thread-safe version. Load factor and resizing. Hash collisions in real systems.

### Lab 16-searching
#### Problem: Search in Rotated Sorted Array (LC 33)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Find target in rotated sorted array in O(log n).
- **How the interview goes**: Apple tests modified binary search. Must handle pivot logic correctly.
- **Approach 1**: Single-Pass Modified Binary Search — O(log n) time, O(1) space. Check which side is sorted.
- **Approach 2**: Find Pivot + Binary Search — O(log n) time, O(1) space. Two passes.
- **Java**:
```java
/**
 * Search in rotated sorted array using modified binary search.
 * Time: O(log n), Space: O(1)
 */
public int search(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) return mid;
        if (nums[lo] <= nums[mid]) {
            if (target >= nums[lo] && target < nums[mid]) hi = mid - 1;
            else lo = mid + 1;
        } else {
            if (target > nums[mid] && target <= nums[hi]) lo = mid + 1;
            else hi = mid - 1;
        }
    }
    return -1;
}
```
- **What Apple evaluates**: Binary search modification, boundary conditions, handling duplicates (LC 81).
- **Follow-up**: Find minimum in rotated sorted array (LC 153). Search with duplicates (LC 81).

#### Problem: Find Peak Element (LC 162)
- **Difficulty**: Medium | **Frequency**: Medium
- **Statement**: Find any peak element in array where neighbor comparison defines peak.
- **How the interview goes**: Apple tests binary search on non-sorted array (local comparison).
- **Approach 1**: Binary Search — O(log n) time, O(1) space. Compare mid with mid+1.
- **Approach 2**: Linear Scan — O(n) time, O(1) space. Simple but suboptimal.
- **Java**:
```java
/**
 * Find peak element using binary search.
 * Time: O(log n), Space: O(1)
 */
public int findPeakElement(int[] nums) {
    int lo = 0, hi = nums.length - 1;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] > nums[mid + 1]) hi = mid;
        else lo = mid + 1;
    }
    return lo;
}
```
- **What Apple evaluates**: Binary search on unsorted arrays, local comparison, convergence logic.
- **Follow-up**: Find peak in 2D matrix. Mountain array peak (LC 852).

### Lab 23-bit-manipulation
#### Problem: Single Number (LC 136)
- **Difficulty**: Easy | **Frequency**: Very High
- **Statement**: Find element that appears once when all others appear twice.
- **How the interview goes**: Apple tests XOR property. Must explain x^x=0 and x^0=x.
- **Approach 1**: XOR — O(n) time, O(1) space. Linear XOR accumulation.
- **Approach 2**: HashMap — O(n) time, O(n) space. Overkill but works.
- **Java**:
```java
/**
 * Single number using XOR.
 * Time: O(n), Space: O(1)
 */
public int singleNumber(int[] nums) {
    int result = 0;
    for (int n : nums) result ^= n;
    return result;
}
```
- **What Apple evaluates**: Bitwise XOR understanding, O(1) space solution, explaining why XOR works.
- **Follow-up**: Single number II (LC 137) — others appear 3 times. Single number III (LC 260) — two singles.

#### Problem: Number of 1 Bits (LC 191)
- **Difficulty**: Easy | **Frequency**: High
- **Statement**: Count the number of set bits in an integer.
- **How the interview goes**: Apple tests bit masking. Discuss signed vs unsigned.
- **Approach 1**: Brian Kernighan's — O(number of set bits) time, O(1) space. n & (n-1) trick.
- **Approach 2**: Mask and Shift — O(32) time, O(1) space. Check each bit.
- **Java**:
```java
/**
 * Count 1 bits using Brian Kernighan's algorithm.
 * Time: O(set bits), Space: O(1)
 */
public int hammingWeight(int n) {
    int count = 0;
    while (n != 0) {
        n &= (n - 1);
        count++;
    }
    return count;
}
```
- **What Apple evaluates**: Bit manipulation fluency, understanding n & (n-1) trick, signed integer handling.
- **Follow-up**: Reverse bits (LC 190). Power of two (LC 231). Counting bits for 0..n (LC 338).

## System Design Questions
1. **Design Apple iCloud Sync** — Tree (Lab 04) for file hierarchy, Hashing (Lab 08) for content-addressable storage, Queue (Lab 07) for sync operations.
2. **Design Apple Music Recommendation** — Graph (Lab 05) for user-artist relationships, Heap (Lab 09) for top K, Trie (Lab 17) for search.
3. **Design Apple Contacts** — Trie (Lab 17) for autocomplete, Sorting (Lab 15) for display order, Hashing (Lab 08) for fast lookup.

## Behavioral Questions (STAR)
1. **Situation**: "Tell me about a time you paid attention to detail." — **Task**: Off-by-one bug in binary search (Lab 16). **Action**: Wrote comprehensive test suite covering edge cases. **Result**: Caught 3 related bugs pre-deployment.
2. **Situation**: "Describe a complex problem you simplified." — **Task**: Complex tree traversal needed. **Action**: Reduced to simple recursive DFS (Lab 04). **Result**: Code halved in size, performance improved 2×.
3. **Situation**: "How did you handle a situation where you disagreed with a manager?" — **Task**: Manager wanted quick HashMap (Lab 08); I argued for Trie (Lab 17). **Action**: Built both prototypes. **Result**: Trie was 40% better for prefix patterns.
4. **Situation**: "Tell me about a time you improved a process." — **Task**: Slow sorting pipeline. **Action**: Replaced bubble sort with quicksort (Lab 15). **Result**: Process went from 5 minutes to 10 seconds.
5. **Situation**: "Describe a project you're proud of." — **Task**: Built an autocomplete system. **Action**: Designed Trie (Lab 17) + Heap (Lab 09) for top K suggestions. **Result**: Shipped to production, used by millions.

## Study Plan
1. **Weeks 1-2**: Lab 03-linked-lists, Lab 04-trees — Apple's foundational DS focus
2. **Weeks 3-4**: Lab 13-dynamic-programming, Lab 06-stacks — Core algorithmic topics
3. **Week 5**: Lab 08-hashing, Lab 16-searching — Practical DS manipulation
4. **Week 6**: Lab 23-bit-manipulation — Apple's unique niche emphasis
5. **Weeks 7-8**: Mock interviews with emphasis on edge cases and memory efficiency

## Interview Tips
- **Pace**: Deliberate and careful. Apple interviewers appreciate thoroughness over speed.
- **Communication**: Focus on the "why" behind your choices. Apple values design philosophy.
- **What impresses**: Spotting edge cases before being asked, writing elegant minimal code, considering memory usage.
- **Avoid**: Messy code, ignoring constraints, not testing your solution mentally.
- **Coding style**: Elegant, minimal, readable. Code should feel like Apple's products — simple but powerful.
