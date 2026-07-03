# Module 23: Data Structures & Algorithms - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the Time and Space Complexity of Merge Sort vs Quick Sort?
**Answer**:
- **Merge Sort**: 
  - Time Complexity: Always `O(n log n)` in the Best, Average, and Worst cases because it consistently halves the array and merges them.
  - Space Complexity: `O(n)` because it requires allocating temporary arrays during the merge phase. It is not an "in-place" sorting algorithm.
- **Quick Sort**:
  - Time Complexity: Average `O(n log n)`. Worst case `O(n^2)` if the pivot chosen is consistently the smallest or largest element (e.g., sorting an already sorted array with the first element as pivot).
  - Space Complexity: `O(log n)` due to recursive stack calls. It is an "in-place" sort, requiring very little memory. Quick sort is often preferred in practice due to excellent cache locality.

### Q2: What is the difference between a Binary Tree and a Binary Search Tree (BST)?
**Answer**:
- A **Binary Tree** is a hierarchical data structure where each node has at most two children (left and right). There are no rules about how data is ordered.
- A **Binary Search Tree (BST)** is a specific type of Binary Tree that enforces a strict rule: For any given node, all values in its left subtree must be strictly *less than* the node's value, and all values in its right subtree must be strictly *greater than* the node's value. This ordered property allows for extremely fast searches (O(log n) time) compared to searching an unordered tree (O(n) time).

### Q3: How do you detect a cycle (infinite loop) in a Linked List?
**Answer**:
The most optimal way to detect a cycle is **Floyd’s Cycle-Finding Algorithm** (also known as the "Tortoise and the Hare" algorithm).
You use two pointers that traverse the list at different speeds. The "slow" pointer moves one node at a time, while the "fast" pointer moves two nodes at a time. If the list has no cycle, the fast pointer will reach `null`. If there is a cycle, the fast pointer will eventually "lap" the slow pointer and they will point to the exact same node. This algorithm runs in `O(n)` time and requires only `O(1)` auxiliary space.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Two Sum
**Problem**: Given an array of integers `nums` and an integer `target`, return the indices of the two numbers such that they add up to `target`. You may not use the same element twice. Provide an O(n) solution.

**Solution**:
Use a HashMap to store the numbers you have seen so far and their indices. For each number, calculate its "complement" (target - current number) and check if it's already in the map.

```java
public int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> map = new HashMap<>();
    
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        
        if (map.containsKey(complement)) {
            return new int[] { map.get(complement), i };
        }
        
        map.put(nums[i], i);
    }
    
    throw new IllegalArgumentException("No two sum solution");
}
```

### Scenario 2: Reverse a Linked List
**Problem**: Given the `head` of a singly linked list, reverse the list, and return the reversed list.

**Solution**:
Maintain three pointers: `prev`, `current`, and `next`. Traverse the list, flip the `current.next` pointer backward to point to `prev`, and shift all pointers forward.

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

public ListNode reverseList(ListNode head) {
    ListNode prev = null;
    ListNode current = head;
    
    while (current != null) {
        ListNode nextTemp = current.next; // Save the next node
        current.next = prev;              // Reverse the pointer
        
        prev = current;                   // Move prev forward
        current = nextTemp;               // Move current forward
    }
    
    return prev; // prev is the new head
}
```