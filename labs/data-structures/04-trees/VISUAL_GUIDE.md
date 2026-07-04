# Visual Guide to Trees

## Binary Tree

```
         ┌── 1 ──┐
         │        │
      ┌─ 2 ─┐  ┌─ 3 ─┐
      │     │  │     │
      4     5  6     7
```

## BST Property

```
         ┌── 8 ──┐
         │        │
      ┌─ 3 ─┐  ┌─ 10 ─┐
      │     │  │      │
      1     6  9      14
                       │
                      ┌┤
                      13
```

Every node: left < parent < right.

## Degenerate BST (Worst Case)

```
1
 \
  2
   \
    3
     \
      4
       \
        5
```

Inserting sorted data produces a linked list — O(n) search.

## Balanced BST (AVL)

```
         ┌── 4 ──┐
         │        │
      ┌─ 2 ─┐  ┌─ 6 ─┐
      │     │  │     │
      1     3  5     7
```

Height is O(log n).

## Tree Traversal Orders

```
        1
       / \
      2   3
     / \   \
    4   5   6

Preorder:  1, 2, 4, 5, 3, 6   (root, left, right)
Inorder:   4, 2, 5, 1, 3, 6   (left, root, right)
Postorder: 4, 5, 2, 6, 3, 1   (left, right, root)
Level:     1, 2, 3, 4, 5, 6   (queue-based BFS)
```

## Expression Tree

```
        ┌── * ──┐
        │        │
     ┌─ + ─┐  ┌─ 3
     │     │  │
     2     5

Infix:      (2 + 5) * 3
Prefix:     * + 2 5 3
Postfix:    2 5 + 3 *
```
