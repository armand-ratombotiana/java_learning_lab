# Mental Models for Heaps

## The Tournament Bracket

A min-heap is like a single-elimination tournament. The winner (smallest value) is at the root. Each match determines who advances. To find a new winner after the champion leaves, the runner-up competes against the next contestants (siftDown).

## The Pyramid of Rocks

Rocks are piled with the smallest at the top. Every rock above is smaller than the rocks below it (min-heap). If you remove the top rock, you replace it with the last rock added and let it settle down to its correct position.

## The Hospital Emergency Room

Patients arrive with different urgency levels. The most urgent patient is always treated next (peek/pop). New patients are added and bubble up based on urgency (siftUp). This is a priority queue — heaps are the most efficient implementation.

## The AM Stock Exchange

Orders are placed with limit prices. The order with the best price (highest buy, lowest sell) is always at the top. When filled, the next best order rises to the top.

## The Toy Box (Max-Heap)

The largest toy is always on top. Every toy is larger than the toys below it. When you take the top toy, you grab the last toy added, put it on top, and push it down until it's larger than its children.

## Array Representation Model

```
Logical tree:          Physical array:
        50                  ┌────┐
       /  \                 │ 50 │  [0]
      30   20               │ 30 │  [1]
     / \   / \              │ 20 │  [2]
    15 10 8   5             │ 15 │  [3]
                            │ 10 │  [4]
                            │  8 │  [5]
                            │  5 │  [6]
                            └────┘
```

The tree and array are two views of the same data. The parent/child relationships are computed by index arithmetic, not stored as pointers.
