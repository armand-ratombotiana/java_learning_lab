# Visual Guide to Advanced Trees

## AVL Rotations

### LL (Right Rotation)
```
     z (=3)          y (=2)
    / \             / \
   y   4   →       1   z
  / \                 / \
 1   R               R   4
```

### RR (Left Rotation)
```
  z (=1)               y (=2)
 / \                  / \
2   y    →           z   3
   / \             / \
  L   3           1   L
```

### LR (Left-Right)
```
    z (=3)           z              x
   / \             / \            / \
  y   4    →      x   4    →     y   z
 / \             / \            / \ / \
1   x           y   R          1  L R  4
   / \         / \
  L   R       1   L
```

### RL (Right-Left)
```
  z (=1)            z                 x
 / \              / \               / \
2   y      →     2   x       →     z   y
   / \              / \          / \ / \
  x   4            L   y        2  L R  4
 / \                  / \
L   R                R   4
```

## B-Tree (Order 5, t=3)

```
Root:
┌─────┬─────┬─────┬─────┐
│ 10  │ 30  │ 50  │     │
└──┬──┴──┬──┴──┬──┴──┬──┘
   │     │     │     │
   ▼     ▼     ▼     ▼
┌─────┐ ┌─────────┐ ┌─────────┐
│1 5 7│ │15 20 25 │ │35 40 45 │
└─────┘ └─────────┘ └─────────┘
```

Each node (except root) has 2-4 keys (t-1 to 2t-1).
Each node has 3-5 children.

## Red-Black Tree Example

```
       11 (B)
      /    \
    2 (R)   14 (B)
   /   \    /   \
 1(B)  7(B) 15(R) 16(R)
       / \
      5(R) 8(R)
```

Invariants checked:
- Root is black ✓
- No consecutive reds ✓
- Black height = 2 for all leaf paths ✓

## Fenwick Tree Array

```
Index:  1   2   3   4   5   6   7   8
Array: [1,  3,  1,  7,  2,  5,  1,  10]

Fenwick tree:
bit[1] = arr[1]                          = 1
bit[2] = arr[1] + arr[2]                = 4
bit[3] = arr[3]                          = 1
bit[4] = arr[1] + arr[2] + arr[3] + arr[4] = 12
bit[5] = arr[5]                          = 2
bit[6] = arr[5] + arr[6]                = 7
bit[7] = arr[7]                          = 1
bit[8] = sum of all                     = 22
```

## Segment Tree (Range Sum)

```
Array: [1, 3, 5, 7, 9, 11]
                          [0-5:36]
                       /           \
                 [0-2:9]           [3-5:27]
                /       \          /       \
            [0-1:4]   [2:5]   [3-4:16]   [5:11]
            /    \             /     \
        [0:1]   [1:3]      [3:7]   [4:9]
```
