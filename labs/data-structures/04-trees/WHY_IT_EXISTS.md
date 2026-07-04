# Why Trees Exist

Trees exist to model **hierarchical relationships** that arrays and linked lists cannot represent efficiently.

## Problems with Linear Structures

- Arrays and linked lists are one-dimensional — they cannot represent parent-child relationships
- File systems are hierarchical (directories contain files and subdirectories)
- HTML/XML documents are nested (elements contain elements)
- Decision processes branch (if-then-else structures)
- Sorting requires comparison of pairs

## What Trees Provide

| Need | Tree Solution |
|------|--------------|
| Hierarchical data | Parent-child relationships |
| Fast search (sorted) | BST: O(log n) search |
| Recursive structure | Natural fit for divide-and-conquer |
| Expression representation | Expression trees (operators as internal nodes) |
| Prefix/range queries | Tries, segment trees |
| Balanced storage | AVL, Red-Black, B-trees |

## Tree Variants

- **Binary trees**: foundation of all tree structures
- **BST**: O(log n) search when balanced
- **AVL/Red-Black**: self-balancing BSTs
- **B-tree**: optimized for disk storage (databases)
- **Trie**: string search by prefix
- **Segment/Fenwick tree**: range queries
- **Heap**: priority queue (tree-like)

Trees are the **first hierarchical data structure** — understanding them is essential for databases, file systems, compilers, and networking.
