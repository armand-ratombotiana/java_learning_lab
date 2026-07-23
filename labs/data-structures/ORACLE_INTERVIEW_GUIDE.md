# Oracle Interview Guide — Data Structures Academy

## Interview Process for DS Roles
- **Rounds**: Recruiter screen → 1-2 technical phone screens (coding + database concepts) → 4-5 on-site rounds (2-3 coding, 1 system design, 1 behavioral + manager round).
- **Timeline**: Phone screen → on-site within 3-4 weeks. Decision in 1-2 weeks.
- **Format**: Live coding in shared IDE. Some teams use HackerRank for coding screens.
- **Focus**: Correctness, robustness, and enterprise-grade code. Oracle values thorough testing and defensive programming.
- **Coding Environment**: Usually VS Code or IntelliJ shared via screen. Some teams use Oracle's internal tools.
- **Culture**: Process-oriented. Heavy emphasis on design documentation and code quality.

## Top Problems by Lab

### Lab 30-b-tree (B-Trees)
#### Problem: Range Sum Query — Immutable (LC 303)
- **Difficulty**: Easy | **Frequency**: High
- **Statement**: Given an integer array, compute sum of elements between indices i and j inclusive.
- **How the interview goes**: Oracle tests prefix sum optimization. Discuss how B-trees handle range queries in databases.
- **Approach 1**: Prefix Sum Array — O(1) query, O(n) precompute, O(n) space. Cumulative sums from index 0.
- **Approach 2**: Segment Tree — O(log n) query, O(n) precompute, O(n) space. Database-style range indexing.
- **Java**:
```java
/**
 * Range sum query using prefix sum array.
 * Time: O(1) query, O(n) init, Space: O(n)
 */
public class NumArray {
    private int[] prefix;
    public NumArray(int[] nums) {
        prefix = new int[nums.length + 1];
        for (int i = 0; i < nums.length; i++) prefix[i + 1] = prefix[i] + nums[i];
    }
    public int sumRange(int left, int right) {
        return prefix[right + 1] - prefix[left];
    }
}
```
- **What Oracle evaluates**: Precomputation technique, off-by-one correctness, understanding of why B-trees are used in DBs for this.
- **Follow-up**: Range sum with updates (LC 307) — use Fenwick/BIT (Lab 20) or Segment Tree (Lab 19).

#### Problem: B-Tree Concept Discussion
- **Difficulty**: Medium (Conceptual) | **Frequency**: High
- **Statement**: Discuss how B-trees work and why Oracle uses them for database indexing.
- **How the interview goes**: Oracle asks you to explain B-tree properties (order, fanout, leaf nodes) and compare to BSTs.
- **Approach 1**: Explain B-Tree Structure — Each node has multiple keys, all leaves at same depth. Fanout = O(log_{t}(n)).
- **Approach 2**: Red-Black Tree comparison — B-trees minimize disk I/O, RB trees work in memory. Oracle DB uses B+ trees.
- **Java** (B-tree search algorithm):
```java
/**
 * B-tree search (conceptual implementation).
 * Time: O(log n), Space: O(1)
 */
class BTreeNode {
    int[] keys;
    BTreeNode[] children;
    int t; // minimum degree
    int n; // current number of keys
    boolean leaf;
}
public boolean search(BTreeNode root, int key) {
    BTreeNode curr = root;
    while (curr != null) {
        int i = 0;
        while (i < curr.n && key > curr.keys[i]) i++;
        if (i < curr.n && key == curr.keys[i]) return true;
        if (curr.leaf) return false;
        curr = curr.children[i];
    }
    return false;
}
```
- **What Oracle evaluates**: Understanding b-tree structure, fanout, disk I/O optimization, comparison with other trees.
- **Follow-up**: B+ tree vs B-tree. Clustered vs non-clustered index. Database page organization.

### Lab 08-hashing
#### Problem: Two Sum (LC 1)
- **Difficulty**: Easy | **Frequency**: Very High
- **Statement**: Find two indices summing to target.
- **How the interview goes**: Oracle's standard warm-up. Expects HashMap solution with discussion of hash collisions.
- **Approach 1**: One-Pass HashMap — O(n) time, O(n) space. Trade memory for speed.
- **Approach 2**: Two-Pointer on Sorted — O(n log n) time, O(1) space. Better when memory is constrained.
- **Java**:
```java
/**
 * Two Sum using hash map.
 * Time: O(n), Space: O(n)
 */
public int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        if (map.containsKey(complement)) return new int[]{map.get(complement), i};
        map.put(nums[i], i);
    }
    throw new IllegalArgumentException("No two sum solution");
}
```
- **What Oracle evaluates**: HashMap usage, collision awareness, input validation, exception handling.
- **Follow-up**: Discuss hash collision resolution (chaining vs open addressing). Security aspects (DoS via hash flooding).

#### Problem: Design HashMap (LC 706)
- **Difficulty**: Easy | **Frequency**: High
- **Statement**: Design a HashMap without built-in library.
- **How the interview goes**: Oracle tests custom hash table with chaining. Must handle resizing discussion.
- **Approach 1**: Array + LinkedList Chaining — O(1) average, O(n) worst case.
- **Approach 2**: Open Addressing (Linear Probing) — O(1) average. Memory efficient but deletion is complex.
- **Java**:
```java
/**
 * HashMap design with chaining.
 * Time: O(1) average, Space: O(n)
 */
class MyHashMap {
    private static class Node { int key, val; Node next; Node(int k, int v) { key = k; val = v; } }
    private Node[] buckets;
    private static final int SIZE = 10000;
    public MyHashMap() { buckets = new Node[SIZE]; }
    private int hash(int key) { return key % SIZE; }
    public void put(int key, int value) {
        int idx = hash(key);
        Node head = buckets[idx];
        if (head == null) { buckets[idx] = new Node(key, value); return; }
        Node prev = null, curr = head;
        while (curr != null) {
            if (curr.key == key) { curr.val = value; return; }
            prev = curr; curr = curr.next;
        }
        prev.next = new Node(key, value);
    }
    public int get(int key) {
        Node curr = buckets[hash(key)];
        while (curr != null) { if (curr.key == key) return curr.val; curr = curr.next; }
        return -1;
    }
    public void remove(int key) {
        int idx = hash(key);
        Node curr = buckets[idx], prev = null;
        while (curr != null) {
            if (curr.key == key) {
                if (prev == null) buckets[idx] = curr.next;
                else prev.next = curr.next;
                return;
            }
            prev = curr; curr = curr.next;
        }
    }
}
```
- **What Oracle evaluates**: Hash function design, collision handling, linked list manipulation, encapsulation.
- **Follow-up**: Load factor and resizing. Thread-safe HashMap. HashMap vs ConcurrentHashMap.

### Lab 04-trees
#### Problem: Kth Smallest Element in a BST (LC 230)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Find the Kth smallest element in a BST.
- **How the interview goes**: Oracle tests in-order traversal. Discuss recursion vs iteration.
- **Approach 1**: Iterative In-order — O(h + k) time, O(h) space. Early termination after K elements.
- **Approach 2**: Augmented BST — O(log n) average with size tracking at each node. Good for frequent queries.
- **Java**:
```java
/**
 * Kth smallest in BST using iterative in-order traversal.
 * Time: O(h + k), Space: O(h)
 */
public int kthSmallest(TreeNode root, int k) {
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode curr = root;
    while (!stack.isEmpty() || curr != null) {
        while (curr != null) { stack.push(curr); curr = curr.left; }
        curr = stack.pop();
        if (--k == 0) return curr.val;
        curr = curr.right;
    }
    return -1;
}
```
- **What Oracle evaluates**: In-order traversal, early termination, stack management, handling of null.
- **Follow-up**: Kth largest (reverse in-order). BST from preorder traversal (LC 1008).

#### Problem: Binary Tree Inorder Traversal (LC 94)
- **Difficulty**: Easy | **Frequency**: Very High
- **Statement**: Return in-order traversal of a binary tree.
- **How the interview goes**: Oracle tests both recursive and iterative approaches. Expects both.
- **Approach 1**: Recursive — O(n) time, O(h) space. Simplest form.
- **Approach 2**: Morris Traversal — O(n) time, O(1) space. Threaded binary tree. Advanced but impresses.
- **Java**:
```java
/**
 * Inorder traversal using recursion.
 * Time: O(n), Space: O(h)
 */
public List<Integer> inorderTraversal(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    inorder(root, result);
    return result;
}
private void inorder(TreeNode node, List<Integer> result) {
    if (node == null) return;
    inorder(node.left, result);
    result.add(node.val);
    inorder(node.right, result);
}
```
- **What Oracle evaluates**: Recursion fundamentals, traversal understanding, list construction.
- **Follow-up**: Morris traversal (O(1) space). Iterative with stack. Preorder and postorder.

### Lab 15-sorting
#### Problem: Sort Colors (LC 75)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Sort array of 0s, 1s, and 2s in place (Dutch national flag problem).
- **How the interview goes**: Oracle tests three-way partitioning. Must do in one pass with O(1) space.
- **Approach 1**: Dutch National Flag — O(n) time, O(1) space. Three pointers.
- **Approach 2**: Counting Sort — O(n) time, O(1) space. Two passes. Less optimal.
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
            swap(nums, lo++, mid++);
        } else if (nums[mid] == 1) {
            mid++;
        } else {
            swap(nums, mid, hi--);
        }
    }
}
private void swap(int[] nums, int i, int j) {
    int temp = nums[i]; nums[i] = nums[j]; nums[j] = temp;
}
```
- **What Oracle evaluates**: Single-pass constraint, invariant maintenance, pointer movement logic.
- **Follow-up**: 4-way sorting. Wiggle sort (LC 324). Partition in quicksort.

#### Problem: Insertion Sort List (LC 147)
- **Difficulty**: Medium | **Frequency**: Medium
- **Statement**: Sort a linked list using insertion sort.
- **How the interview goes**: Oracle tests understanding of insertion sort applied to linked structures.
- **Approach 1**: Dummy Node Insertion — O(n²) time, O(1) space. Maintain sorted portion, insert each element.
- **Approach 2**: Merge Sort on List — O(n log n) time, O(log n) space. Better for large lists.
- **Java**:
```java
/**
 * Insertion sort on linked list.
 * Time: O(n^2), Space: O(1)
 */
public ListNode insertionSortList(ListNode head) {
    ListNode dummy = new ListNode(0), curr = head;
    while (curr != null) {
        ListNode next = curr.next;
        ListNode insert = dummy;
        while (insert.next != null && insert.next.val < curr.val) insert = insert.next;
        curr.next = insert.next;
        insert.next = curr;
        curr = next;
    }
    return dummy.next;
}
```
- **What Oracle evaluates**: Insertion sort mechanics, linked list manipulation, dummy node pattern.
- **Follow-up**: Optimize with merge sort for linked list (LC 148). Sort linked list already sorted by absolute values.

### Lab 16-searching
#### Problem: Binary Search (LC 704)
- **Difficulty**: Easy | **Frequency**: Very High
- **Statement**: Implement binary search on a sorted array.
- **How the interview goes**: Oracle tests binary search fundamentals. Must be flawless.
- **Approach 1**: Iterative Binary Search — O(log n) time, O(1) space. Standard.
- **Approach 2**: Recursive Binary Search — O(log n) time, O(log n) stack space.
- **Java**:
```java
/**
 * Binary search using iteration.
 * Time: O(log n), Space: O(1)
 */
public int search(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) return mid;
        if (nums[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return -1;
}
```
- **What Oracle evaluates**: Off-by-one correctness, overflow-safe mid calculation, termination condition.
- **Follow-up**: Search insert position (LC 35). First/last position (LC 34). Infinite array search.

#### Problem: First Bad Version (LC 278)
- **Difficulty**: Easy | **Frequency**: High
- **Statement**: Find the first bad version in a sequence of versions using isBadVersion() API.
- **How the interview goes**: Oracle tests binary search with boolean predicate. True/false pattern.
- **Approach 1**: Binary Search Lower Bound — O(log n) time, O(1) space. Find first true.
- **Approach 2**: Linear Scan — O(n) time, O(1) space. Only as starting point.
- **Java**:
```java
/**
 * First bad version using binary search (lower bound).
 * Time: O(log n), Space: O(1)
 */
public int firstBadVersion(int n) {
    int lo = 1, hi = n;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (isBadVersion(mid)) hi = mid;
        else lo = mid + 1;
    }
    return lo;
}
// Provided API
private boolean isBadVersion(int version) { return version >= BAD; }
```
- **What Oracle evaluates**: Binary search on boolean predicate, lower bound pattern, left/right convergence.
- **Follow-up**: Find peak element (LC 162). Find minimum in rotated array (LC 153).

### Lab 31-red-black-tree
#### Problem: TreeMap Implementation Concepts
- **Difficulty**: Hard (Conceptual) | **Frequency**: Medium
- **Statement**: Discuss Red-Black Tree properties and implement basic operations.
- **How the interview goes**: Oracle tests understanding of balanced BST. Focus on properties (5 rules) and insertion.
- **Approach 1**: Discuss RB Tree Properties — Every node red/black, root is black, no adjacent reds, same black height.
- **Approach 2**: Compare with AVL Tree — RB trees have fewer rotations, used in Java TreeMap/TreeSet. AVL is stricter.
- **Java** (RB Tree search — same as BST):
```java
/**
 * Red-Black Tree insertion fixup (conceptual).
 * Time: O(log n), Space: O(log n)
 */
enum Color { RED, BLACK }
class RBNode { int key; Color color; RBNode left, right, parent; }

public RBNode insert(RBNode root, int key) {
    // BST insertion first, then fix violations
    RBNode node = new RBNode();
    node.key = key; node.color = Color.RED;
    root = bstInsert(root, node);
    fixViolations(root, node);
    root.color = Color.BLACK;
    return root;
}
// Fixing violations involves rotations and recoloring
private void fixViolations(RBNode root, RBNode node) {
    while (node.parent != null && node.parent.color == Color.RED) {
        RBNode uncle = getUncle(node);
        if (uncle != null && uncle.color == Color.RED) {
            node.parent.color = Color.BLACK;
            uncle.color = Color.BLACK;
            node.parent.parent.color = Color.RED;
            node = node.parent.parent;
        } else {
            // Rotations based on node/parent/grandparent orientation
            // Left/right rotations + recoloring
            break; // simplified for brevity
        }
    }
    root.color = Color.BLACK;
}
```
- **What Oracle evaluates**: Understanding of balanced tree properties, insertion cases, rotation concepts.
- **Follow-up**: Java TreeMap internal implementation. Deletion in RB trees. Compare with AVL and B-tree.

### Lab 32-avl-tree
#### Problem: Balanced BST Check
- **Difficulty**: Medium | **Frequency**: Medium
- **Statement**: Check if a binary tree is height-balanced (AVL property).
- **How the interview goes**: Oracle tests understanding of balance factor and height computation.
- **Approach 1**: Bottom-Up Recursive — O(n) time, O(h) space. Compute heights, check balance at each node.
- **Approach 2**: Top-Down — O(n log n) time, O(h) space. Check each subtree individually. Less efficient.
- **Java**:
```java
/**
 * Check if tree is height-balanced (AVL property).
 * Time: O(n), Space: O(h)
 */
public boolean isBalanced(TreeNode root) {
    return checkHeight(root) != -1;
}
private int checkHeight(TreeNode node) {
    if (node == null) return 0;
    int left = checkHeight(node.left);
    if (left == -1) return -1;
    int right = checkHeight(node.right);
    if (right == -1) return -1;
    if (Math.abs(left - right) > 1) return -1;
    return 1 + Math.max(left, right);
}
```
- **What Oracle evaluates**: Height computation, balance factor, early termination with sentinel values.
- **Follow-up**: Convert sorted array to BST (LC 108). Balance a BST (LC 1382). AVL tree insertion (rotations).

## System Design Questions
1. **Design Oracle Database Indexing System** — B+ Tree (Lab 30) for primary indexing, Hash (Lab 08) for hash indexes, Sorting (Lab 15) for merge-join.
2. **Design Oracle Cloud Storage** — RAFT consensus, Tree (Lab 04) for file hierarchy, Hashing (Lab 08) for content addressing, Queue (Lab 07) for replication.
3. **Design Enterprise Transaction Processing System** — B-Tree (Lab 30) for ACID-compliant storage, Stack (Lab 06) for undo logs, Queue (Lab 07) for transaction ordering.

## Behavioral Questions (STAR)
1. **Situation**: "Tell me about a time you ensured code quality." — **Task**: Data structure library review. **Action**: Wrote test suite covering all edge cases for BST (Lab 04). **Result**: Caught 5 bugs before release.
2. **Situation**: "Describe a time you handled a production incident." — **Task**: Out-of-memory crash in search. **Action**: Replaced recursive DFS (Lab 05) with iterative BFS. **Result**: Memory usage dropped 80%.
3. **Situation**: "How do you approach learning a new technology?" — **Task**: Needed to learn B-trees (Lab 30) for a project. **Action**: Built a prototype from scratch and benchmarked. **Result**: Indexed query time improved 100×.
4. **Situation**: "Tell me about a time you improved a process." — **Task**: Manual testing of sorting algorithms. **Action**: Automated benchmarking framework (Lab 15). **Result**: Reduced test cycle from 2 days to 2 hours.
5. **Situation**: "Describe a time you had to simplify a complex system." — **Task**: Overly complex priority system. **Action**: Unified with a single priority queue using heaps (Lab 09). **Result**: Codebase reduced by 300 lines, 0 regressions.

## Study Plan
1. **Weeks 1-2**: Lab 30-b-tree, Lab 08-hashing — Oracle's most unique high-frequency labs
2. **Weeks 3-4**: Lab 04-trees, Lab 15-sorting — Foundational algorithm knowledge
3. **Week 5**: Lab 16-searching, Lab 31-red-black-tree — Advanced topics
4. **Week 6**: Lab 32-avl-tree — Additional balanced tree knowledge
5. **Weeks 7-8**: Focus on system design with database indexing, mock interviews with enterprise-scale thinking

## Interview Tips
- **Pace**: Thorough and deliberate. Oracle values correct, well-tested code over fast solutions.
- **Communication**: Always discuss error handling and edge cases. Oracle's enterprise products demand robustness.
- **What impresses**: Understanding of underlying data structure implementations, discussing production considerations, writing defensive code.
- **Avoid**: Oversimplifying solutions, ignoring failure modes, not considering concurrency.
- **Coding style**: Enterprise-grade Java. Use proper encapsulation, defensive null checks, meaningful exception messages. Code should be ready for code review.
