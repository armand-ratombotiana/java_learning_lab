# How Heaps Work

## Min-Heap Implementation

```java
public class MinHeap {
    private int[] heap;
    private int size;
    private int capacity;

    public MinHeap(int capacity) {
        this.capacity = capacity;
        this.heap = new int[capacity];
        this.size = 0;
    }

    public void insert(int value) {
        if (size == capacity) resize();
        heap[size] = value;
        siftUp(size);
        size++;
    }

    public int extractMin() {
        if (isEmpty()) throw new NoSuchElementException();
        int min = heap[0];
        heap[0] = heap[--size];
        siftDown(0);
        return min;
    }

    public int peek() {
        if (isEmpty()) throw new NoSuchElementException();
        return heap[0];
    }

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (heap[i] >= heap[parent]) break;
            swap(i, parent);
            i = parent;
        }
    }

    private void siftDown(int i) {
        while (true) {
            int smallest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < size && heap[left] < heap[smallest])
                smallest = left;
            if (right < size && heap[right] < heap[smallest])
                smallest = right;
            if (smallest == i) break;
            swap(i, smallest);
            i = smallest;
        }
    }

    // Build heap from arbitrary array — O(n)
    public void heapify(int[] arr) {
        this.heap = arr;
        this.size = arr.length;
        this.capacity = arr.length;
        for (int i = (size / 2) - 1; i >= 0; i--) {
            siftDown(i);
        }
    }

    private void swap(int i, int j) {
        int tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }
}
```

## Heap Sort

```java
public void heapSort(int[] arr) {
    // Build max-heap
    for (int i = (arr.length / 2) - 1; i >= 0; i--) {
        siftDown(arr, arr.length, i);
    }
    // Extract elements one by one
    for (int i = arr.length - 1; i > 0; i--) {
        swap(arr, 0, i);
        siftDown(arr, i, 0);
    }
}
```
