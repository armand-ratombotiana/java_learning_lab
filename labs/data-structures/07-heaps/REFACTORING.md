# Refactoring Heaps

## Use PriorityQueue Instead of Manual Heap

```java
// Before — manual heap implementation
class TaskQueue {
    private Task[] heap = new Task[100];
    private int size = 0;
    // ... siftUp, siftDown, etc.
}

// After
PriorityQueue<Task> taskQueue = new PriorityQueue<>();
```

## Use Comparator for Custom Ordering

```java
// Before — custom heap with hardcoded comparison
class MaxHeap { // ... }

// After
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(
    Comparator.reverseOrder()
);
```

## Extract Heap Operations into Reusable Methods

```java
// Before — inline sift logic
void add(int v) {
    heap[size] = v;
    int i = size++;
    while (i > 0 && heap[i] < heap[(i-1)/2]) {
        swap(i, (i-1)/2);
        i = (i-1)/2;
    }
}

// After
void add(int v) {
    heap[size] = v;
    siftUp(size++);
}
```

## Use Stream with PriorityQueue

```java
// Before — manual extraction
List<Integer> sorted = new ArrayList<>();
while (!pq.isEmpty()) {
    sorted.add(pq.poll());
}

// After
List<Integer> sorted = new ArrayList<>();
pq.stream().sorted().toList();  // but PQ's iterator is unordered
// Better: use poll() in a loop
```

## Replace Manual Min/Max Choice with Comparator

```java
// Before — different classes for min/max
class MinHeap { ... }
class MaxHeap { ... }

// After — same class, different comparator
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
```

## Use offer/poll Instead of add/remove

```java
// Before
pq.add(element);     // throws if capacity-limited
pq.remove();         // throws if empty

// After
pq.offer(element);   // returns false if full
pq.poll();           // returns null if empty
```
