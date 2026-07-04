# Interview Questions: Trees

## Easy

1. **Maximum depth of binary tree** — Find the height (recursive).

2. **Validate BST** — Check if a tree satisfies BST properties (inorder or range check).

3. **Same tree** — Check if two binary trees are identical.

4. **Invert binary tree** — Swap left and right children recursively.

5. **Symmetric tree** — Check if a tree is mirror of itself.

## Medium

6. **Binary tree level-order traversal** — Return level-by-level values (BFS).

7. **Serialize and deserialize binary tree** — Encode/decode tree to string.

8. **Lowest common ancestor** — Find LCA in a binary tree.

9. **Kth smallest element in BST** — Inorder traversal, stop at k.

10. **Binary tree right side view** — Return nodes visible from right side (BFS or DFS).

11. **Construct tree from preorder and inorder** — Reconstruct tree from traversals.

12. **Path sum** — Does a root-to-leaf path sum to target?

## Hard

13. **Binary tree maximum path sum** — Path can start/end anywhere.

14. **Serialize and deserialize N-ary tree** — Encode arbitrary child counts.

15. **Recover BST** — Two elements are swapped; find them without changing structure.

16. **Vertical order traversal** — Column-wise sorted traversal.

## Key Patterns

- **Recursion**: natural fit for tree problems
- **BFS/DFS**: level-order vs depth-first
- **In-order for BST**: sorted output, kth smallest
- **Bottom-up DP**: compute values from leaves upward (height, diameter, path sum)
- **Divide and conquer**: left subtree + right subtree combine to solve for root

## Java-Specific Topics

- `TreeMap` / `TreeSet` for sorted collections
- Recursive vs iterative traversal trade-offs
- `Comparable` vs `Comparator` for BST ordering
- HashMap treeification (Java 8+)
