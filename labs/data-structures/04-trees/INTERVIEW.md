# Interview Questions: Trees

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 94 Binary Tree Inorder Traversal](https://leetcode.com/problems/binary-tree-inorder-traversal/) | Easy | Amazon, Google, Microsoft, Meta, Apple | Recursive/iterative DFS |
| [LC 102 Binary Tree Level Order Traversal](https://leetcode.com/problems/binary-tree-level-order-traversal/) | Medium | Amazon, Meta, Google, Microsoft, Apple | BFS / queue |
| [LC 104 Maximum Depth of Binary Tree](https://leetcode.com/problems/maximum-depth-of-binary-tree/) | Easy | Amazon, Meta, Google, Microsoft, Apple | Recursive/max depth |
| [LC 105 Construct Binary Tree from Preorder and Inorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/) | Medium | Amazon, Meta, Google, Microsoft, Apple | Divide & conquer |
| [LC 98 Validate Binary Search Tree](https://leetcode.com/problems/validate-binary-search-tree/) | Medium | Amazon, Meta, Google, Microsoft, Apple | BST range check |
| [LC 236 Lowest Common Ancestor of a Binary Tree](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/) | Medium | Amazon, Meta, Google, Microsoft, Apple | Recursive LCA |
| [LC 297 Serialize and Deserialize Binary Tree](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/) | Hard | Amazon, Meta, Google, Microsoft | BFS / DFS encoding |
| [LC 124 Binary Tree Maximum Path Sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/) | Hard | Amazon, Meta, Google, Microsoft, Apple | Post-order DFS |
| [LC 100 Same Tree](https://leetcode.com/problems/same-tree/) | Easy | Amazon, Google, Meta, Apple | Recursive comparison |
| [LC 226 Invert Binary Tree](https://leetcode.com/problems/invert-binary-tree/) | Easy | Amazon, Meta, Google, Microsoft, Apple | Recursive swap |
| [LC 101 Symmetric Tree](https://leetcode.com/problems/symmetric-tree/) | Easy | Amazon, Meta, Google, Microsoft, Apple | Recursive mirror check |
| [LC 112 Path Sum](https://leetcode.com/problems/path-sum/) | Easy | Amazon, Meta, Google, Microsoft | Root-to-leaf DFS |
| [LC 199 Binary Tree Right Side View](https://leetcode.com/problems/binary-tree-right-side-view/) | Medium | Amazon, Meta, Microsoft, Apple | BFS rightmost / DFS |
| [LC 230 Kth Smallest Element in a BST](https://leetcode.com/problems/kth-smallest-element-in-a-bst/) | Medium | Amazon, Meta, Google, Microsoft, Oracle | Inorder traversal |
| [LC 662 Maximum Width of Binary Tree](https://leetcode.com/problems/maximum-width-of-binary-tree/) | Medium | Google, Amazon, Meta | BFS with index |
| [LC 114 Flatten Binary Tree to Linked List](https://leetcode.com/problems/flatten-binary-tree-to-linked-list/) | Medium | Amazon, Google, Microsoft, Meta | In-place DFS |
| [LC 99 Recover Binary Search Tree](https://leetcode.com/problems/recover-binary-search-tree/) | Medium | Google, Amazon, Microsoft | Morris / inorder swap detection |

## NeetCode Reference
NeetCode 150: Trees category — 14 problems covering traversals, validation, construction, serialization, and advanced patterns.

## Company-Specific Questions

### Google
- Design a data structure that supports querying for the kth largest element in a BST (augmented tree with subtree sizes)
- Serialize and deserialize a binary tree — compare BFS (level-order) vs DFS (preorder) encoding
- Implement a BST iterator using O(h) space (controlled recursion)
- Construct a binary tree from preorder and inorder traversal — what if the tree has duplicates?

### Microsoft
- Maximum width of a binary tree (how to compute width using heap indices at depth)
- Flatten a binary tree to a linked list in-place (preorder → right-skewed tree)
- Recover BST when two nodes are swapped — detect in inorder traversal

### Meta
- Binary tree right side view — both BFS and DFS approaches
- Path sum III — count paths summing to target, can start anywhere (prefix sum + HashMap)
- Verify if a binary tree is a subtree of another binary tree

### Amazon
- Lowest common ancestor of a binary tree — recursive solution; prove correctness by cases
- Serialize/deserialize an N-ary tree (different from binary — variable children)
- Level order traversal with zig-zag (spiral order traversal)

### Apple
- Binary tree maximum path sum — explain the six cases at each node
- Invert binary tree (Google famously made this a phone screen question)
- Implement a two-pass tree for constraints satisfaction

### Oracle
- How does TreeMap/TreeSet use Red-Black trees internally? What are the invariants?
- Recursive vs iterative tree traversal — stack memory implications for large trees
- How would you implement a persistent (immutable) binary tree in Java?

## Real Production Scenarios

- **Scenario 1: File System Representation** — An operating system represents the directory structure as an N-ary tree. Each node stores file/directory metadata. Operations like `ls`, `find`, and path resolution are tree traversals (preorder for listing, path-following for resolution).

- **Scenario 2: HTML DOM Parser** — A web browser parses HTML into a DOM tree. The render engine uses tree traversal to compute layout: post-order traversal for computing element sizes, pre-order for positioning, and a mix for painting.

- **Scenario 3: Compiler Abstract Syntax Tree (AST)** — A compiler transforms source code into an AST. Semantic analysis uses tree traversals (symbol resolution, type checking). Code generation uses a post-order traversal to emit assembly instructions.

## Interview Tips

- Time: O(n) for most traversals, O(log n) for BST operations, O(h) for path-dependent queries
- Space: O(h) for recursion stack (h = tree height), O(n) worst case for skewed tree, O(log n) average for balanced
- Common edge cases: null root, single node, skewed tree, complete tree, duplicate values
- Recursive tree solutions fit many problems, but iterative (explicit stack) avoids stack overflow on deep trees
- For BST problems, the inorder traversal gives sorted order — exploit this property

## Java-Specific Considerations

- No standard `Tree` class in Java — you must implement from scratch (class Node { int val; Node left; Node right; })
- `TreeMap<K,V>` / `TreeSet<E>` — Red-Black tree backed, sorted, O(log n) operations
- Recursive traversals are natural but may cause StackOverflowError on deep trees (consider iterative with explicit `Deque`)
- `Deque<TreeNode>` with `ArrayDeque` is best for iterative stack/queue implementations
- `HashMap<TreeNode, ...>` for problems needing parent maps or visited tracking
- In-order traversal of BST using Morris traversal achieves O(1) space (no stack) via threaded binary tree
- Java's `Comparator`/`Comparable` defines BST ordering when implementing custom trees
