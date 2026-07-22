# Interview Questions: Advanced Trees (BST, AVL, Red-Black, B-Trees)

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 297 Serialize and Deserialize Binary Tree](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/) | Hard | Amazon, Meta, Google, Microsoft | BFS/DFS encoding |
| [LC 173 Binary Search Tree Iterator](https://leetcode.com/problems/binary-search-tree-iterator/) | Medium | Meta, Amazon, Google, Microsoft, Oracle | Controlled inorder traversal |
| [LC 449 Serialize and Deserialize BST](https://leetcode.com/problems/serialize-and-deserialize-bst/) | Medium | Google, Amazon, Meta | BST-specific encoding |
| [LC 235 Lowest Common Ancestor of a BST](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/) | Medium | Amazon, Google, Microsoft, Meta | BST property navigation |
| [LC 156 Binary Tree Upside Down](https://leetcode.com/problems/binary-tree-upside-down/) | Medium | Google, Meta | Right-skewed tree transform |
| [LC 114 Flatten Binary Tree to Linked List](https://leetcode.com/problems/flatten-binary-tree-to-linked-list/) | Medium | Amazon, Google, Microsoft, Meta | Preorder flatten in-place |
| [LC 98 Validate Binary Search Tree](https://leetcode.com/problems/validate-binary-search-tree/) | Medium | Amazon, Meta, Google, Microsoft, Apple | BST range check |
| [LC 108 Convert Sorted Array to Binary Search Tree](https://leetcode.com/problems/convert-sorted-array-to-binary-search-tree/) | Easy | Amazon, Google, Meta, Microsoft | Divide & conquer balanced BST |
| [LC 230 Kth Smallest Element in a BST](https://leetcode.com/problems/kth-smallest-element-in-a-bst/) | Medium | Amazon, Meta, Google, Microsoft, Oracle | Inorder traversal |
| [LC 450 Delete Node in a BST](https://leetcode.com/problems/delete-node-in-a-bst/) | Medium | Amazon, Meta, Google, Microsoft | BST deletion |
| [LC 701 Insert into a Binary Search Tree](https://leetcode.com/problems/insert-into-a-binary-search-tree/) | Medium | Amazon, Meta, Google, Microsoft | BST insertion |
| [LC 110 Balanced Binary Tree](https://leetcode.com/problems/balanced-binary-tree/) | Easy | Amazon, Meta, Google, Microsoft, Apple | Height-balanced check |
| [LC 1382 Balance a Binary Search Tree](https://leetcode.com/problems/balance-a-binary-search-tree/) | Medium | Google, Amazon, Meta | Inorder → balanced BST |
| [LC 1008 Construct BST from Preorder Traversal](https://leetcode.com/problems/construct-binary-search-tree-from-preorder-traversal/) | Medium | Amazon, Meta, Google | BST construction with bounds |
| [LC 1038 Binary Search Tree to Greater Sum Tree](https://leetcode.com/problems/binary-search-tree-to-greater-sum-tree/) | Medium | Amazon, Google, Meta | Reverse inorder + cumulative sum |

## NeetCode Reference
NeetCode 150: Trees category includes BST problems (Validate BST, Kth Smallest, LCA BST, Construct from traversals).

## Company-Specific Questions

### Google
- Serialize and deserialize a BST using only preorder traversal (no null markers needed — use value bounds)
- Implement an AVL tree with rotations — explain when each rotation type (LL, RR, LR, RL) applies
- Design a dynamic order statistic tree (find kth smallest with support for insert/delete)
- How would you implement a B-tree for a disk-based database index?

### Microsoft
- Compare AVL trees vs Red-Black trees — which is better for read-heavy vs write-heavy workloads?
- Convert a BST to a doubly linked list in-place (inorder traversal + pointer adjustment)
- Design a self-balancing BST that supports order statistics (size-augmented tree)

### Meta
- Implement a BST iterator that supports both forward and backward traversal (bidirectional iterator)
- Balance a BST — collect inorder traversal, then build using divide-and-conquer
- Validate BST — the recursive range approach vs inorder traversal approach

### Amazon
- Design DynamoDB's primary key index using B-tree principles
- How would you implement a range query on a BST? (search for keys between low and high)
- Find two nodes in BST that sum to a target value (HashSet or two-pointer with inorder)

### Apple
- How would you implement a sorted list using a BST (balanced BST as array replacement)?
- Design a thread-safe BST for a shared cache (read-write lock or copy-on-write)
- Implement ceiling and floor queries in a BST

### Oracle
- How does TreeMap implement the Red-Black tree? Walk through the insert fixup algorithm
- What is the difference between `NavigableMap` and `SortedMap`? What methods do they add?
- Explain the JVM's use of balanced trees for class metadata and string interning
- Why does Oracle Database use B+ trees instead of BSTs for indexes?

## Real Production Scenarios

- **Scenario 1: Database Index (B+ Tree)** — An RDBMS uses B+ trees for clustered indexes. The high fan-out (500+ keys per node) means a 4-level tree can index billions of rows. Range scans are efficient because leaf nodes form a linked list. Each node is sized to fit in a disk page (4KB-16KB).

- **Scenario 2: In-Memory Sorted Map** — A caching layer uses `TreeMap` (Red-Black tree) to maintain sorted user sessions by expiration timestamp. The `firstKey()` and `headMap()` methods efficiently find and evict expired sessions. Insertions and deletions are O(log n).

- **Scenario 3: File System Directory** — A file system represents directory entries in a sorted structure (B-tree or balanced BST) for efficient lookup by name. OS file operations (create, delete, rename) update the tree. The tree is stored on disk with caching for recently accessed directories.

## Interview Tips

- Time: O(log n) for balanced tree ops, O(h) for BST (worst O(n) if unbalanced), O(n) for traversal
- Space: O(n) for tree, O(log n) for recursive stack (balanced), O(n) stack in worst case (unbalanced)
- Common edge cases: empty tree, single node, chain (skewed), duplicate values, non-existent key search/deletion
- Balanced tree rotations: always draw the tree before and after rotation; identify which nodes move where
- BST property: left < root < right — strictly maintained? Or left ≤ root < right? Clarify with interviewer

## Java-Specific Considerations

- `TreeMap<K,V>` — Red-Black tree with `NavigableMap` interface (first/last/ceiling/floor/higher/lower/subMap)
- `TreeSet<E>` — wrapper around TreeMap with PRESENT values
- `Collections.binarySearch()` on sorted lists — O(log n) for random-access lists, O(n) for linked lists
- No built-in AVL or B-Tree in standard Java — these are implementation exercises
- `java.util.Comparator<T>` for custom ordering; `Comparable<T>` for natural ordering
- SubMap(headMap/tailMap/subMap) returns views backed by original TreeMap — O(1) for creation, O(log n) for boundaries
- Memory: TreeMap Entry has 5 fields (key, value, left, right, parent, color/boolean) + object overhead
- For rotation implementations: use temporary variables to avoid pointer aliasing bugs
- Balance factor in AVL tree: -1, 0, or +1; rebalance when absolute value > 1
