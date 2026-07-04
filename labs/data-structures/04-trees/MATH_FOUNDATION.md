# Math Foundation: Trees

## Tree Properties

For a binary tree with n nodes:
- **Maximum height**: n - 1 (degenerate, linked list)
- **Minimum height**: ⌈log₂(n+1)⌉ - 1 (perfect/balanced)
- **Maximum nodes at height h**: 2^h
- **Maximum nodes in perfect tree of height h**: 2^{h+1} - 1

## Height and Balance

For an AVL tree:
- **Minimum nodes for height h**: N(h) = N(h-1) + N(h-2) + 1 (Fibonacci-like)
- N(0) = 1, N(1) = 2
- N(h) ≈ φ^{h+2}/√5 where φ = (1+√5)/2 ≈ 1.618
- **Maximum height**: h ≤ 1.44 × log₂(n)

For a complete binary tree with n nodes:
- **Number of leaves**: ⌈n/2⌉
- **Number of internal nodes**: ⌊n/2⌋
- **Height**: ⌊log₂(n)⌋

## Traversal Order Statistics

- **Preorder**: root → left → right
- **Inorder**: left → root → right (produces sorted order for BST)
- **Postorder**: left → right → root
- All traversals: O(n)

## Counting Binary Trees

Number of distinct binary trees with n nodes (Catalan number):
```
C_n = (1/(n+1)) × C(2n, n) = (2n)! / ((n+1)! × n!)
```

C_0 = 1, C_1 = 1, C_2 = 2, C_3 = 5, C_4 = 14, C_5 = 42

## Binary Tree Diameter

The diameter (longest path between any two nodes) can be found by:
- At each node, the longest path through that node = height(left) + height(right)
- The diameter is the maximum of these sums
- O(n) time, O(h) space (recursion depth)

## Tree Serialization Space

For an N-ary tree with n nodes and branching factor b:
- Preorder with null markers: 2n + 1 tokens
- Each token: value size + delimiter
- Total: O(n) if value sizes are bounded
