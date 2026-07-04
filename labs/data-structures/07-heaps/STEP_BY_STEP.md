# Step by Step: Heap Operations

## Insert into Min-Heap

```
Initial heap: [4, 10, 8, 17, 25, 16, 20]
        4
       / \
      10  8
     / \  / \
    17 25 16 20

Insert(3):
1. Append: [4, 10, 8, 17, 25, 16, 20, 3]
2. siftUp at index 7:
   parent = (7-1)/2 = 3 → heap[3] = 17
   3 < 17 → swap → index 3
   [4, 10, 8, 3, 25, 16, 20, 17]
   
   parent = (3-1)/2 = 1 → heap[1] = 10
   3 < 10 → swap → index 1
   [4, 3, 8, 10, 25, 16, 20, 17]
   
   parent = (1-1)/2 = 0 → heap[0] = 4
   3 < 4 → swap → index 0
   [3, 4, 8, 10, 25, 16, 20, 17]
Done.
```

## Extract Min

```
Heap: [3, 4, 8, 10, 25, 16, 20, 17]

1. Save min = 3
2. heap[0] = heap[7] = 17, size-- = 7
   [17, 4, 8, 10, 25, 16, 20]
3. siftDown(0):
   left=1(4), right=2(8) → smallest=1, 4 < 17 → swap
   [4, 17, 8, 10, 25, 16, 20]
   left=3(10), right=4(25) → smallest=3, 10 < 17 → swap
   [4, 10, 8, 17, 25, 16, 20]
   left=7(16), right=8(out) → smallest=7, 16 < 17 → swap
   [4, 10, 8, 16, 25, 17, 20]
   left=15,16 (out of bounds) → stop
Result: min=3, heap=[4, 10, 8, 16, 25, 17, 20]
```

## Heap Sort

```
Sort [5, 3, 8, 4, 1, 9, 2, 7, 6]

1. Build max-heap:
   [9, 7, 8, 4, 3, 5, 2, 1, 6]

2. Extract (swap first with last, siftDown, reduce range):
   [8, 7, 6, 4, 3, 5, 2, 1, 9]  (max=9)
   [7, 6, 5, 4, 3, 1, 2, 8, 9]  (max=8)
   [6, 4, 5, 2, 3, 1, 7, 8, 9]  (max=7)
   [5, 4, 1, 2, 3, 6, 7, 8, 9]  (max=6)
   [4, 3, 1, 2, 5, 6, 7, 8, 9]  (max=5)
   [3, 2, 1, 4, 5, 6, 7, 8, 9]  (max=4)
   [2, 1, 3, 4, 5, 6, 7, 8, 9]  (max=3)
   [1, 2, 3, 4, 5, 6, 7, 8, 9]  (max=2)

Result: [1, 2, 3, 4, 5, 6, 7, 8, 9]
```
