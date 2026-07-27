# Problem Walkthrough: 04-Trees

## Problem 1: Maximum Depth of Binary Tree (LC 104) — Meta

### Interview Scenario
"Meta interviewer: 'Given the root of a binary tree, return its maximum depth.'"

### The Problem
Maximum depth is the number of nodes along the longest path from root to the farthest leaf.

### Step 1: Clarify (30 seconds)
- **Q:** What's a leaf? **A:** A node with no children.
- **Q:** Empty tree? **A:** Return 0.
- **Q:** Single node? **A:** Return 1.
- **Edge cases:** Null root, skewed tree (linked list), balanced tree, only left children.

### Step 2: Brute Force (2 min)
- Level-order traversal (BFS) counting levels.
- **Time:** O(n) — works, but recursion is more elegant.
- **Space:** O(n) for queue.

### Step 3: Optimize (5 min)
- "Recursive DFS: depth of a node = 1 + max(depth(left), depth(right)). Base case: null node has depth 0."
- O(n) time, O(h) space where h is height of the tree (could be O(n) for skewed, O(log n) for balanced).
- **Why Meta likes it:** Recursive tree problems are their bread and butter. They want to see you handle recursion fluently.

### Step 4: Code (10 min)

```java
/**
 * Computes the maximum depth of a binary tree.
 * <p>
 * Time: O(n) | Space: O(h)
 */
public class Solution {
    public int maxDepth(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }
}
```

### Step 5: Test (3 min)
- **Edge:** root = null → 0
- **Edge:** root = [1] → 1
- **Example:** root = [3, 9, 20, null, null, 15, 7] → 3
- **Example:** root = [1, null, 2] → 2 (skewed right)
- Walk through the recursion call stack.

### Step 6: Follow-ups
- "Balanced binary tree?" — Check height difference at each node (LC 110).
- "Minimum depth?" — Need BFS for optimal (first leaf found).
- "Diameter of the tree?" — Longest path between any two nodes (LC 543).
- **What Meta looks for:** Can you modify a simple recursive pattern? Do you understand recursion depth limits?

### Company Evaluation Criteria
- **Meta:** Recursion fluency. Can you reason about the call stack?
- **Google:** Would ask about iterative DFS (stack).
- **Amazon:** Would ask about BFS approach for minimum depth.

---

## Problem 2: Binary Tree Level Order Traversal (LC 102) — Amazon

### Interview Scenario
"Amazon interviewer: 'Given the root of a binary tree, return the level order traversal of its nodes' values (left to right, level by level).'"

### The Problem
Return a list of lists where each inner list contains node values at one depth level.

### Step 1: Clarify (30 seconds)
- **Q:** Empty tree? **A:** Return empty list.
- **Q:** Order within level? **A:** Left to right.
- **Q:** Can I use recursion? **A:** BFS is preferred, but both work.
- **Edge cases:** Skewed tree, single node, large width.

### Step 2: Brute Force (2 min)
- Recursive: track depth, add to list at that depth. Requires pre-allocating list sizes.
- **Time:** O(n) — fine.
- **Space:** O(n) for result + O(h) for call stack.

### Step 3: Optimize (5 min)
- "Use BFS with a queue: process nodes level by level. At each level, record current queue size, process that many nodes, add children to queue."
- O(n) time, O(n) space (queue holds max width). This is the standard Amazon solution.
- **Why Amazon values this:** BFS models how distributed systems process work in layers — relevance to Amazon's microservice architecture.

### Step 4: Code (10 min)

```java
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Performs level-order traversal of a binary tree.
 * <p>
 * Time: O(n) | Space: O(n)
 */
public class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            result.add(level);
        }

        return result;
    }
}
```

### Step 5: Test (3 min)
- **Edge:** root = null → []
- **Example:** root = [3, 9, 20, null, null, 15, 7] → [[3], [9, 20], [15, 7]]
- **Example:** root = [1] → [[1]]
- **Example:** root = [1, 2, 3, 4, 5, 6, 7] → [[1], [2, 3], [4, 5, 6, 7]]
- Show queue state at each level.

### Step 6: Follow-ups
- "Zigzag level order (alternating direction)?" — Use deque or reverse every other level (LC 103).
- "Average of levels?" — Sum level values / size (LC 637).
- "Right side view?" — Track last node at each level (LC 199).
- **What Amazon looks for:** Can you build on the BFS pattern for multiple variants?

### Company Evaluation Criteria
- **Amazon:** Pattern reuse — BFS level processing is a template they reward.
- **Google:** Would ask about serialization/deserialization of level order.
- **Meta:** Would ask about printing the tree in level order without lists.

---

## Problem 3: Validate Binary Search Tree (LC 98) — Google

### Interview Scenario
"Google interviewer: 'Given the root of a binary tree, determine if it is a valid BST.'"

### The Problem
A valid BST requires: left subtree values < root value < right subtree values, and both subtrees must also be valid BSTs.

### Step 1: Clarify (30 seconds)
- **Q:** Can there be duplicates? **A:** No, assume distinct values (though LC defines left < root ≤ right — clarify with interviewer).
- **Q:** Empty tree? **A:** A null tree is a valid BST.
- **Q:** Value range? **A:** 32-bit integers.
- **Edge cases:** Single node, skewed tree violating BST, all equal values, min/max integer bounds.

### Step 2: Brute Force (2 min)
- Inorder traversal into a list → check if list is sorted.
- **Time:** O(n) — works, but O(n) extra space.
- **Space:** O(n).

### Step 3: Optimize (5 min)
- "Inorder traversal without storing: keep the previous visited node. At each node, check if current value > previous. Update previous and recurse right."
- Or use recursive range validation: each node must be within (min, max) bounds.
- Both are O(n) time, O(h) space.
- **Why Google loves this:** BST validation tests deep understanding of the BST definition. Many candidates get it wrong by only checking local children.

### Step 4: Code (10 min)

```java
/**
 * Validates whether a binary tree is a BST using range checks.
 * <p>
 * Time: O(n) | Space: O(h)
 */
public class Solution {
    public boolean isValidBST(TreeNode root) {
        return validate(root, null, null);
    }

    private boolean validate(TreeNode node, Integer low, Integer high) {
        if (node == null) return true;

        if (low != null && node.val <= low) return false;
        if (high != null && node.val >= high) return false;

        return validate(node.left, low, node.val)
            && validate(node.right, node.val, high);
    }
}
```

### Step 5: Test (3 min)
- **Edge:** root = null → true
- **Example:** root = [2, 1, 3] → true
- **Example:** root = [5, 1, 4, null, null, 3, 6] → false (4 is in the wrong subtree)
- **Edge:** root = [Integer.MAX_VALUE] → true
- **Tricky:** root = [5, 4, 6, null, null, 3, 7] → false (3 is less than 5 but in the right subtree)

### Step 6: Follow-ups
- "Inorder predecessor and successor in BST?" — Use BST properties to narrow search.
- "Construct BST from preorder/inorder traversal?" — Recurse based on root value.
- "Kth smallest element in BST?" — Inorder traversal, stop at k (LC 230).
- **What Google looks for:** Can you handle the "all ancestors" constraint, not just parent? Do you understand when Integer.MIN_VALUE/MAX_VALUE fails?

### Company Evaluation Criteria
- **Google:** Precision with the BST definition. Range validation is the gold standard.
- **Amazon:** Would ask about BST to doubly linked list conversion.
- **Meta:** Would ask about binary tree to BST conversion.

---

## Study Notes

### Key Patterns
- **Recursive DFS:** Depth, diameter, balanced, symmetric
- **BFS level processing:** Level order, zigzag, right side view, averages
- **BST range validation:** Bounded recursion with (low, high)
- **Inorder traversal of BST is sorted:** Used for validation, kth smallest, sorted order
- **Tree serialization/deserialization:** Preorder + inorder, or level order with markers

### Common Mistakes
- Checking only immediate children for BST (not all ancestors)
- Using mutable instance variables in recursive validation
- Not handling null in binary tree
- Stack overflow on very deep skewed trees (use iterative)
- Off-by-one in depth counting

### Time Complexity Cheat Sheet
| Pattern | Time | Space |
|---|---|---|
| Recursive traversal | O(n) | O(h) |
| BFS level order | O(n) | O(w) max width |
| BST validation | O(n) | O(h) |
| Inorder traversal | O(n) | O(h) |
| Lowest common ancestor | O(n) | O(h) |
