# Visual Guide to Heaps

## Min-Heap Array and Tree

```
Array: [3, 5, 8, 17, 19, 36, 42, 25, 100]

         3
       /   \
      5     8
     / \   / \
    17 19 36 42
   / \
  25 100
```

Valid: parent ≤ both children.

## Max-Heap Array and Tree

```
Array: [100, 19, 36, 17, 3, 25, 1, 2, 7]

         100
       /    \
      19     36
     /  \   /  \
    17   3 25   1
   / \
  2   7
```

Valid: parent ≥ both children.

## Insert Operation

```
Insert 1 into min-heap [3, 5, 8, 17, 19, 36, 42, 25, 100]:

Step 1: append to end → index 9
  [3, 5, 8, 17, 19, 36, 42, 25, 100, 1]

Step 2: siftUp index 9
  parent of 9: (9-1)/2 = 4 → heap[4] = 19
  1 < 19 → swap → index 4
  [3, 5, 8, 17, 1, 36, 42, 25, 100, 19]

  parent of 4: (4-1)/2 = 1 → heap[1] = 5
  1 < 5 → swap → index 1
  [3, 1, 8, 17, 5, 36, 42, 25, 100, 19]

  parent of 1: (1-1)/2 = 0 → heap[0] = 3
  1 < 3 → swap → index 0
  [1, 3, 8, 17, 5, 36, 42, 25, 100, 19]

Done — heap property restored.
```

## Extract Min Operation

```
Extract from [1, 3, 8, 17, 5, 36, 42, 25, 100, 19]:

Step 1: Save min = heap[0] = 1
Step 2: heap[0] = heap[last] = 19, size--
  [19, 3, 8, 17, 5, 36, 42, 25, 100]

Step 3: siftDown index 0
  left child = 1 → 3, right child = 2 → 8
  3 < 19 → swap with left child → index 1
  [3, 19, 8, 17, 5, 36, 42, 25, 100]

  left child = 3 → 17, right child = 4 → 5
  5 < 19 → swap with right child → index 4
  [3, 5, 8, 17, 19, 36, 42, 25, 100]

  left child = 9 → out of bounds → stop
Result: extracted 1
```

## Heapify (Build Heap) O(n)

```
Array: [10, 2, 5, 3, 1, 7, 9, 4]
n = 8, last non-leaf = (n/2)-1 = 3

siftDown at 3 (value 3):
  left=7(4), right=8(null) → swap 3 with 4 → done
siftDown at 2 (value 5):
  left=5(7), right=6(9) → 5 < min → no swap
siftDown at 1 (value 2):
  left=3(3), right=4(1) → min=1 at right → swap
  [10, 1, 5, 3, 2, 7, 9, 4]
  left=9(null) → stop
siftDown at 0 (value 10):
  left=1(1), right=2(5) → min=1 at left → swap
  [1, 10, 5, 3, 2, 7, 9, 4]
  left=3(3), right=4(2) → min=2 at right → swap
  [1, 2, 5, 3, 10, 7, 9, 4]
  left=9(null) → stop
Result: [1, 2, 5, 3, 10, 7, 9, 4]
```
