# Debugging Heaps

## Common Issues

| Symptom | Likely Cause |
|---------|-------------|
| Peek returns wrong value | Heap property violated (wrong siftUp/siftDown) |
| Extract returns wrong value | Wrong root selection (max vs min confusion) |
| Infinite loop in siftUp/siftDown | Off-by-one in index bounds |
| Heap sort incorrect order | Wrong heap type (min vs max) |
| PriorityQueue unexpected order | Wrong Comparator direction |

## Debugging Techniques

### Validate Heap Property

```java
boolean isMinHeap(int[] heap, int size) {
    for (int i = 0; i < size; i++) {
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        if (left < size && heap[i] > heap[left]) return false;
        if (right < size && heap[i] > heap[right]) return false;
    }
    return true;
}
```

### Print Heap Structure

```java
void printHeap(int[] heap, int size) {
    int level = 0;
    int count = 0;
    while (count < size) {
        int levelSize = 1 << level;  // 2^level
        for (int i = 0; i < levelSize && count < size; i++) {
            System.out.print(heap[count++] + " ");
        }
        System.out.println();
        level++;
    }
}
```

### Trace Sift Operations

```java
void siftUpDebug(int i) {
    System.out.println("siftUp(" + i + ") value=" + heap[i]);
    while (i > 0) {
        int parent = (i - 1) / 2;
        System.out.println("  compare with parent(" + parent + ")=" + heap[parent]);
        if (heap[i] >= heap[parent]) {
            System.out.println("  → stop, heap property satisfied");
            break;
        }
        System.out.println("  → swap");
        swap(i, parent);
        i = parent;
    }
}
```

### Unit Testing

```java
@Test
void testMinHeap() {
    MinHeap heap = new MinHeap(10);
    heap.insert(5); heap.insert(3); heap.insert(8);
    heap.insert(1); heap.insert(4);
    assertEquals(1, heap.extractMin());
    assertEquals(3, heap.extractMin());
    assertEquals(4, heap.extractMin());
    assertEquals(5, heap.extractMin());
    assertEquals(8, heap.extractMin());
    assertTrue(heap.isEmpty());
}

@Test
void testHeapify() {
    int[] arr = {10, 3, 5, 1, 7, 9, 2};
    MinHeap heap = new MinHeap(10);
    heap.heapify(arr);
    assertTrue(heap.isValidMinHeap());
    assertEquals(1, heap.peek());
}
```
