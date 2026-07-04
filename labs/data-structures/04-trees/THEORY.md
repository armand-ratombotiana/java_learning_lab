# Theory: Trees

## Tree Terminology

A tree is a hierarchical data structure consisting of nodes connected by edges. It is an **acyclic, connected graph**.

- **Root**: the topmost node (no parent)
- **Parent**: a node with children
- **Child**: a node directly connected below another
- **Leaf**: a node with no children
- **Sibling**: nodes sharing the same parent
- **Subtree**: a node and all its descendants
- **Height**: number of edges on the longest path from node to leaf
- **Depth**: number of edges from root to node
- **Level**: set of nodes at the same depth

## Binary Tree

Each node has at most two children: left and right.

```java
class Node<E> {
    E data;
    Node<E> left;
    Node<E> right;
}
```

## Binary Search Tree (BST)

A binary tree where for every node:
- Left subtree contains values **less than** the node
- Right subtree contains values **greater than** the node
- Both subtrees are also BSTs

## Time Complexity

| Operation | Binary Tree | BST (balanced) | BST (degenerate) |
|-----------|------------|----------------|-------------------|
| Search    | O(n)       | O(log n)       | O(n)              |
| Insert    | O(1)*      | O(log n)       | O(n)              |
| Delete    | O(1)*      | O(log n)       | O(n)              |
| Traversal | O(n)       | O(n)           | O(n)              |

\*Insert at available position; search is O(n)

## Tree Traversals

### Depth-First (DFS)

| Traversal | Order | Use Case |
|-----------|-------|----------|
| Preorder  | root → left → right | Copy tree, prefix notation |
| Inorder   | left → root → right | Sorted output (BST) |
| Postorder | left → right → root | Delete tree, postfix notation |

### Breadth-First (BFS)

- **Level-order**: process nodes level by level, left to right
- Uses a queue

## Tree Balance

- **Perfect binary tree**: all internal nodes have 2 children, all leaves at same depth
- **Complete binary tree**: all levels filled except possibly last (filled left to right)
- **Full binary tree**: every node has 0 or 2 children
- **Balanced**: height is O(log n) — AVL, Red-Black trees achieve this
- **Degenerate**: each node has at most one child (effectively a linked list)
