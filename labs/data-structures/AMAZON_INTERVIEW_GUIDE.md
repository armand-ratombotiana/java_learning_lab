# Amazon Interview Guide — Data Structures Academy

## Interview Process for DS Roles
- **Rounds**: Online Assessment (OA) + 4-5 on-site rounds. 2-3 coding rounds focused on data structures and algorithms.
- **Timeline**: Apply → OA within 1 week → On-site within 2-3 weeks. Decision in 3-5 business days.
- **Format**: Live coding in an IDE (usually shared screen via Chime). Amazon uses their internal IDE-like tool.
- **Focus**: Working code first, optimization second. Amazon prioritizes getting a correct solution over a perfect one.
- **Coding Environment**: LiveShare screen. Interviewers expect you to run and test your code mentally.
- **Leadership Principles**: All answers must tie back to Amazon's 16 Leadership Principles.

## Top Problems by Lab

### Lab 01-arrays (Arrays)
#### Problem: Two Sum (LC 1)
- **Difficulty**: Easy | **Frequency**: Very High
- **Statement**: Find two indices in array that sum to a target value.
- **How the interview goes**: Amazon asks this to test fundamentals. They expect O(n) solution quickly, then discuss edge cases.
- **Approach 1**: Brute Force — O(n²) time, O(1) space. Nested loops, used only as starting point.
- **Approach 2**: HashMap — O(n) time, O(n) space. One-pass, track complements.
- **Java**:
```java
/**
 * Two Sum using one-pass hash map.
 * Time: O(n), Space: O(n)
 */
public int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        if (map.containsKey(complement)) {
            return new int[]{map.get(complement), i};
        }
        map.put(nums[i], i);
    }
    throw new IllegalArgumentException("No solution found");
}
```
- **What Amazon evaluates**: Correctness, handling duplicates, awareness of one-pass vs two-pass.
- **Follow-up**: Design a TwoSum class that supports add/find operations (LC 170).

#### Problem: 3Sum (LC 15)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Find all unique triplets that sum to zero in an array.
- **How the interview goes**: Amazon expects sorting + two-pointer. Discuss duplicate handling carefully.
- **Approach 1**: Sort + Two-Pointer — O(n²) time, O(1) extra space. Sort then fix one element.
- **Approach 2**: HashMap — O(n²) time, O(n) space. Uses hash set for complement lookup.
- **Java**:
```java
/**
 * 3Sum with sorting and two-pointer technique.
 * Time: O(n^2), Space: O(1) excluding output
 */
public List<List<Integer>> threeSum(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    for (int i = 0; i < nums.length - 2; i++) {
        if (i > 0 && nums[i] == nums[i - 1]) continue;
        int lo = i + 1, hi = nums.length - 1, target = -nums[i];
        while (lo < hi) {
            int sum = nums[lo] + nums[hi];
            if (sum == target) {
                result.add(Arrays.asList(nums[i], nums[lo], nums[hi]));
                while (lo < hi && nums[lo] == nums[++lo]);
                while (lo < hi && nums[hi] == nums[--hi]);
            } else if (sum < target) {
                lo++;
            } else {
                hi--;
            }
        }
    }
    return result;
}
```
- **What Amazon evaluates**: Handling duplicates, two-pointer technique, sorting trade-off.
- **Follow-up**: 4Sum (LC 18). Closest 3Sum (LC 16). KSum generalization.

### Lab 03-linked-lists
#### Problem: Merge Two Sorted Lists (LC 21)
- **Difficulty**: Easy | **Frequency**: Very High
- **Statement**: Merge two sorted linked lists into one sorted list.
- **How the interview goes**: Amazon expects iterative solution with dummy node. Tests pointer manipulation.
- **Approach 1**: Iterative with Dummy Node — O(n + m) time, O(1) space. Clean and simple.
- **Approach 2**: Recursive — O(n + m) time, O(n + m) stack space. Elegant but less space-efficient.
- **Java**:
```java
/**
 * Merge two sorted linked lists iteratively.
 * Time: O(n+m), Space: O(1)
 */
public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0), curr = dummy;
    while (l1 != null && l2 != null) {
        if (l1.val < l2.val) { curr.next = l1; l1 = l1.next; }
        else { curr.next = l2; l2 = l2.next; }
        curr = curr.next;
    }
    curr.next = (l1 != null) ? l1 : l2;
    return dummy.next;
}
```
- **What Amazon evaluates**: Pointer manipulation, dummy node pattern, handling uneven lists.
- **Follow-up**: Merge K sorted lists (LC 23) — use divide-and-conquer or heap.

#### Problem: Reverse Linked List (LC 206)
- **Difficulty**: Easy | **Frequency**: Very High
- **Statement**: Reverse a singly linked list.
- **How the interview goes**: Amazon's favorite warm-up. Must know both iterative and recursive.
- **Approach 1**: Iterative — O(n) time, O(1) space. Three pointers (prev, curr, next).
- **Approach 2**: Recursive — O(n) time, O(n) stack space. Base case + recursive reversal.
- **Java**:
```java
/**
 * Reverse linked list iteratively.
 * Time: O(n), Space: O(1)
 */
public ListNode reverseList(ListNode head) {
    ListNode prev = null, curr = head;
    while (curr != null) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    return prev;
}
```
- **What Amazon evaluates**: Pointer reassignment order, not losing references, handling null.
- **Follow-up**: Reverse between positions (LC 92). Reverse in K-groups (LC 25).

### Lab 04-trees (Binary Trees)
#### Problem: Binary Tree Level Order Traversal (LC 102)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Return level-by-level traversal of a binary tree.
- **How the interview goes**: Amazon tests BFS implementation. Must track levels explicitly.
- **Approach 1**: BFS with Queue — O(n) time, O(n) space. Process level by level using size.
- **Approach 2**: DFS with Level Tracking — O(n) time, O(h) space. Pass level as parameter.
- **Java**:
```java
/**
 * Level order traversal using BFS queue.
 * Time: O(n), Space: O(n)
 */
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> q = new LinkedList<>();
    q.offer(root);
    while (!q.isEmpty()) {
        int size = q.size();
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TreeNode node = q.poll();
            level.add(node.val);
            if (node.left != null) q.offer(node.left);
            if (node.right != null) q.offer(node.right);
        }
        result.add(level);
    }
    return result;
}
```
- **What Amazon evaluates**: BFS implementation, level boundary tracking, null node handling.
- **Follow-up**: Zigzag traversal (LC 103). Right side view (LC 199). Average of levels (LC 637).

#### Problem: Validate Binary Search Tree (LC 98)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Determine if a binary tree is a valid BST.
- **How the interview goes**: Amazon tests understanding of BST properties. Must use min/max bounds, not just subtree comparisons.
- **Approach 1**: In-order Traversal — O(n) time, O(h) space. BST in-order must be strictly increasing.
- **Approach 2**: Bounds Propagation — O(n) time, O(h) space. Pass min/max to each subtree.
- **Java**:
```java
/**
 * Validate BST using recursive bounds propagation.
 * Time: O(n), Space: O(h)
 */
public boolean isValidBST(TreeNode root) {
    return validate(root, null, null);
}
private boolean validate(TreeNode node, Integer low, Integer high) {
    if (node == null) return true;
    if (low != null && node.val <= low) return false;
    if (high != null && node.val >= high) return false;
    return validate(node.left, low, node.val) && validate(node.right, node.val, high);
}
```
- **What Amazon evaluates**: Understanding BST definition, handling Integer overflow, null checks.
- **Follow-up**: Recover BST (LC 99) with two swapped nodes. BST iterator (LC 173).

### Lab 06-stacks
#### Problem: Valid Parentheses (LC 20)
- **Difficulty**: Easy | **Frequency**: Very High
- **Statement**: Determine if string of brackets is valid (properly closed and nested).
- **How the interview goes**: Amazon's most commonly asked stack problem. Must handle all bracket types.
- **Approach 1**: Stack with HashMap — O(n) time, O(n) space. Map closing to opening brackets.
- **Approach 2**: Stack with Switch — O(n) time, O(n) space. Slightly faster, no map overhead.
- **Java**:
```java
/**
 * Valid parentheses using stack with mapping.
 * Time: O(n), Space: O(n)
 */
public boolean isValid(String s) {
    Map<Character, Character> map = Map.of(')', '(', '}', '{', ']', '[');
    Deque<Character> stack = new ArrayDeque<>();
    for (char c : s.toCharArray()) {
        if (map.containsKey(c)) {
            if (stack.isEmpty() || stack.pop() != map.get(c)) return false;
        } else {
            stack.push(c);
        }
    }
    return stack.isEmpty();
}
```
- **What Amazon evaluates**: Stack usage, mapping strategy, handling edge cases (empty, odd length).
- **Follow-up**: Generate parentheses (LC 22). Minimum remove to make valid (LC 1249). Longest valid parentheses (LC 32).

#### Problem: Min Stack (LC 155)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Design a stack that supports push, pop, top, and retrieving the minimum element in O(1).
- **How the interview goes**: Amazon tests OOD + stack. Discuss storing min pairs vs auxiliary stack.
- **Approach 1**: Auxiliary Stack — O(1) each operation, O(n) space. Maintain separate min stack.
- **Approach 2**: Single Stack with Min Tracking — O(1) each operation, O(n) space. Store diff or min-node pairs.
- **Java**:
```java
/**
 * Min stack using auxiliary stack for tracking minimums.
 * Time: O(1) per operation, Space: O(n)
 */
class MinStack {
    private Deque<Integer> stack = new ArrayDeque<>();
    private Deque<Integer> minStack = new ArrayDeque<>();
    public void push(int val) {
        stack.push(val);
        minStack.push(Math.min(val, minStack.isEmpty() ? val : minStack.peek()));
    }
    public void pop() { stack.pop(); minStack.pop(); }
    public int top() { return stack.peek(); }
    public int getMin() { return minStack.peek(); }
}
```
- **What Amazon evaluates**: OOD design, O(1) constraint handling, space optimization awareness.
- **Follow-up**: Constant space solution using encoded values. Max stack (LC 716).

### Lab 08-hashing
#### Problem: Group Anagrams (LC 49)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Group strings that are anagrams of each other.
- **How the interview goes**: Amazon tests canonical representation thinking. Discuss sorting vs counting.
- **Approach 1**: Frequency Count Key — O(n·k) time, O(n·k) space. Encode int[26] as string key.
- **Approach 2**: Sorted String Key — O(n·k log k) time, O(n·k) space. Simpler but slower.
- **Java**:
```java
/**
 * Group anagrams using frequency count key.
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
- **What Amazon evaluates**: Computing canonical form, trade-off analysis, time vs space.
- **Follow-up**: Large input streaming. Anagram palindrome grouping.

#### Problem: Top K Frequent Elements (LC 347)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Return the K most frequent elements from an array.
- **How the interview goes**: Amazon wants HashMap + Heap approach. Discuss bucket sort for O(n).
- **Approach 1**: HashMap + Min-Heap — O(n log k) time, O(n + k) space.
- **Approach 2**: Bucket Sort — O(n) time, O(n) space. Use frequency array where index = count.
- **Java**:
```java
/**
 * Top K frequent elements using heap.
 * Time: O(n log k), Space: O(n + k)
 */
public int[] topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int n : nums) freq.merge(n, 1, Integer::sum);
    PriorityQueue<Integer> heap = new PriorityQueue<>(Comparator.comparingInt(freq::get));
    for (int key : freq.keySet()) {
        heap.offer(key);
        if (heap.size() > k) heap.poll();
    }
    return heap.stream().mapToInt(i -> i).toArray();
}
```
- **What Amazon evaluates**: HashMap for counting, heap for selection, min-heap vs max-heap choice.
- **Follow-up**: K closest points to origin (LC 973). Top K frequent words (LC 692) with tie-breaking.

### Lab 09-heaps
#### Problem: Kth Largest Element in a Stream (LC 703)
- **Difficulty**: Easy | **Frequency**: Medium
- **Statement**: Design class that finds Kth largest element in a stream of numbers.
- **How the interview goes**: Amazon tests real-time data processing with heaps. Min-heap of size K.
- **Approach 1**: Min-Heap of size K — O(n log k) total, O(k) space. Keep K largest elements.
- **Approach 2**: Order Statistics Tree — O(n log n) total, O(n) space. Overkill for this problem.
- **Java**:
```java
/**
 * Kth largest element in stream using min-heap of size K.
 * Time: O(n log k), Space: O(k)
 */
class KthLargest {
    private final PriorityQueue<Integer> heap = new PriorityQueue<>();
    private final int k;
    public KthLargest(int k, int[] nums) {
        this.k = k;
        for (int n : nums) add(n);
    }
    public int add(int val) {
        heap.offer(val);
        if (heap.size() > k) heap.poll();
        return heap.peek();
    }
}
```
- **What Amazon evaluates**: Understanding heap size management, add/poll interaction, OOD design.
- **Follow-up**: Find median from data stream (LC 295). Sliding window median (LC 480).

#### Problem: Merge K Sorted Lists (LC 23)
- **Difficulty**: Hard | **Frequency**: High
- **Statement**: Merge K sorted linked lists into one sorted list.
- **How the interview goes**: Amazon expects divide-and-conquer or heap approach. Scale discussion matters.
- **Approach 1**: Min-Heap — O(n log k) time, O(k) space. Insert all heads, poll and push.
- **Approach 2**: Divide and Conquer — O(n log k) time, O(1) extra space. Merge pairs recursively.
- **Java**:
```java
/**
 * Merge K sorted lists using min-heap.
 * Time: O(n log k), Space: O(k)
 */
public ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> heap = new PriorityQueue<>(Comparator.comparingInt(a -> a.val));
    for (ListNode node : lists) if (node != null) heap.offer(node);
    ListNode dummy = new ListNode(0), curr = dummy;
    while (!heap.isEmpty()) {
        ListNode node = heap.poll();
        curr.next = node; curr = curr.next;
        if (node.next != null) heap.offer(node.next);
    }
    return dummy.next;
}
```
- **What Amazon evaluates**: Heap customization, null handling, scalability (K can be very large).
- **Follow-up**: External merge sort. Merge K sorted arrays (index-based instead of pointer-based).

### Lab 05-graphs
#### Problem: Course Schedule (LC 207)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Determine if all courses can be completed given prerequisite pairs (cycle detection in directed graph).
- **How the interview goes**: Amazon tests topological sort / cycle detection. Discuss both BFS (Kahn's) and DFS.
- **Approach 1**: BFS (Kahn's Algorithm) — O(V + E) time, O(V + E) space. Indegree tracking.
- **Approach 2**: DFS with States — O(V + E) time, O(V + E) space. WHITE/GRAY/BLACK cycle detection.
- **Java**:
```java
/**
 * Course schedule using BFS topological sort (Kahn's algorithm).
 * Time: O(V+E), Space: O(V+E)
 */
public boolean canFinish(int numCourses, int[][] prerequisites) {
    List<List<Integer>> graph = new ArrayList<>(numCourses);
    for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());
    int[] indegree = new int[numCourses];
    for (int[] pre : prerequisites) {
        graph.get(pre[1]).add(pre[0]);
        indegree[pre[0]]++;
    }
    Queue<Integer> q = new LinkedList<>();
    for (int i = 0; i < numCourses; i++) if (indegree[i] == 0) q.offer(i);
    int count = 0;
    while (!q.isEmpty()) {
        int course = q.poll();
        count++;
        for (int neighbor : graph.get(course)) {
            if (--indegree[neighbor] == 0) q.offer(neighbor);
        }
    }
    return count == numCourses;
}
```
- **What Amazon evaluates**: Graph representation choice, cycle detection, handling empty prerequisites.
- **Follow-up**: Course schedule II (LC 210) — return ordering. Minimum semesters (LC 1136).

## System Design Questions
1. **Design Amazon's Product Recommendation System** — Graph (Lab 05) for user-item relationships, Heaps (Lab 09) for top N recommendations, Hashing (Lab 08) for fast lookups.
2. **Design Amazon Shopping Cart** — LRU Cache (linked list + hash map, combining Lab 03 and Lab 08), Queue (Lab 07) for order processing, Stack (Lab 06) for undo operations.
3. **Design Amazon's Inventory System** — B-Tree (Lab 30) for range queries on stock levels, Hash Maps (Lab 08) for SKU lookups, Sorting (Lab 15) for warehouse optimization.

## Behavioral Questions (STAR)
1. **Situation**: "Tell me about a time you delivered results under tight deadline." — **Task**: Feature with 2-day deadline. **Action**: Prototyped using Arrays (Lab 01) and Hashing (Lab 08) for O(1) lookups. **Result**: Delivered on time with 30% better performance than spec.
2. **Situation**: "Describe a time you invented and simplified." — **Task**: Complex pipeline with O(n²) bottleneck. **Action**: Replaced nested loops with HashMap (Lab 08). **Result**: Reduced from 2 hours to 30 seconds.
3. **Situation**: "How did you handle a disagreement with a teammate?" — **Task**: Chose between BFS and DFS. **Action**: Benchmarked both (Lab 05), showed BFS handled cyclic dependencies better. **Result**: Team agreed on BFS.
4. **Situation**: "Tell me about a time you were deep right." — **Task**: Memory leak from recursive tree traversal. **Action**: Converted to iterative BFS (Lab 04/07). **Result**: Fixed memory issue, reduced stack usage by 90%.
5. **Situation**: "Describe a time you took a calculated risk." — **Task**: Choosing data structure for new feature. **Action**: Chose Trie (Lab 17) over HashMap for prefix search. **Result**: Correct call — 50% faster queries with only 10% more memory.

## Study Plan
1. **Weeks 1-2**: Lab 01-arrays, Lab 03-linked-lists — Core Amazon interview topics
2. **Weeks 3-4**: Lab 04-trees, Lab 08-hashing — High frequency topics
3. **Week 5**: Lab 06-stacks, Lab 09-heaps — Problem-solving with additional DS
4. **Week 6**: Lab 05-graphs — Graph traversal and cycle detection
5. **Weeks 7-8**: Practice Amazon Leadership Principle framing, mock interviews

## Interview Tips
- **Pace**: Get a working solution first, then optimize. Amazon values delivery over perfection.
- **Communication**: Tie your approach to Amazon Leadership Principles (e.g., "I chose this for scalability under 'Think Big'").
- **What impresses**: Running through test cases mentally, discussing trade-offs, showing engineering judgment.
- **Avoid**: Over-optimizing prematurely, ignoring edge cases, not asking clarifying questions.
- **Coding style**: Amazon expects professional, maintainable code. Use meaningful variable names and consistent formatting.
