# Step by Step: Tree Operations

## BST Insertion

```
Insert 5 into BST [3, 1, 4, 2]:
Start:
    3
   / \
  1   4
   \
    2

Insert 5:
  1. Compare 5 > 3 → go right
  2. Compare 5 > 4 → go right
  3. 4.right is null → insert here

Result:
    3
   / \
  1   4
   \   \
    2   5
```

## BST Deletion (Three Cases)

```
Delete 3 from BST:
    5
   / \
  3   6
 / \   \
2   4   7

Case: node has two children
  1. Find in-order successor: smallest in right subtree = 4
  2. Replace 3 with 4
  3. Delete original 4

Result:
    5
   / \
  4   6
 /     \
2       7
```

## Inorder Traversal (Iterative) Step by Step

```
Tree:    root=1
        / \
       2   3
      / \
     4   5

Stack: []
Current: 1

1. Go left: push(1), push(2), push(4)
   Stack: [1, 2, 4], current = null
2. Pop → 4, process(4), go right(null)
   Stack: [1, 2]
3. Pop → 2, process(2), go right(5)
   Stack: [1]
4. Push(5), go left(null)
   Stack: [1, 5]
5. Pop → 5, process(5), go right(null)
   Stack: [1]
6. Pop → 1, process(1), go right(3)
   Stack: []
7. Push(3), go left(null)
   Stack: [3]
8. Pop → 3, process(3), go right(null)
   Stack: []
Done.

Output: 4, 2, 5, 1, 3
```

## Level-Order Traversal Step by Step

```
Tree:   1
       / \
      2   3
     / \   \
    4   5   6

Queue: [1]
Process 1, add children: queuedd 2, 3 → [2, 3]
Process 2, add children: queuedd 4, 5 → [3, 4, 5]
Process 3, add children: queuedd 6 → [4, 5, 6]
Process 4, no children → [5, 6]
Process 5, no children → [6]
Process 6, no children → []
Done.

Output: 1, 2, 3, 4, 5, 6
```
