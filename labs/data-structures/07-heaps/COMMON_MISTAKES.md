# Common Mistakes with Heaps

## Off-by-One in Parent/Child Index

```java
// WRONG — parent formula
int parent = i / 2;  // WRONG: should be (i-1)/2 for 0-indexed

// CORRECT
int parent = (i - 1) / 2;
int left = 2 * i + 1;
int right = 2 * i + 2;
```

## Not Handling Heap Property Violation on Remove

```java
// After replacing root with last element, must siftDown
heap[0] = heap[--size];
// heap[0] may violate heap property
siftDown(0);  // ← CRITICAL
```

## Forgetting Extract Removes from End

```java
// WRONG — just returning root
int extractMin() {
    return heap[0];  // doesn't remove or restore heap!
}

// CORRECT
int extractMin() {
    int min = heap[0];
    heap[0] = heap[--size];
    siftDown(0);
    return min;
}
```

## Heapify Starting from Wrong Index

```java
// WRONG — heapify from leaf nodes (does nothing)
for (int i = size - 1; i >= 0; i--) {
    siftDown(i);  // leaf nodes have no children → no effect, wasted time
}

// CORRECT — start from last non-leaf
for (int i = (size / 2) - 1; i >= 0; i--) {
    siftDown(i);
}
```

## Forgetting Capacity Check

```java
// WRONG — insert without bound check
void insert(int value) {
    heap[size] = value;  // ArrayIndexOutOfBoundsException if full
    siftUp(size++);
}

// CORRECT
void insert(int value) {
    if (size == capacity) resize();
    heap[size] = value;
    siftUp(size++);
}
```

## PriorityQueue with Wrong Comparator

```java
// WRONG — expects max-heap, gets min-heap
PriorityQueue<Integer> maxHeap = new PriorityQueue<>();  // min-heap!

// CORRECT
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
```
