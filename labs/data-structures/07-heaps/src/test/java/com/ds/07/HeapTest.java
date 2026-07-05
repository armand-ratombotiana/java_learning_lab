package com.ds07;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;

public class HeapTest {

    private MinHeap<Integer> minHeap;
    private MaxHeap<Integer> maxHeap;

    @BeforeEach
    void setUp() {
        minHeap = new MinHeap<>();
        maxHeap = new MaxHeap<>();
    }

    @Test
    void minHeapInsertAndGetMin() {
        minHeap.insert(30);
        minHeap.insert(10);
        minHeap.insert(50);
        minHeap.insert(20);
        assertEquals(10, minHeap.getMin());
    }

    @Test
    void minHeapExtractMin() {
        minHeap.insert(30);
        minHeap.insert(10);
        minHeap.insert(50);
        minHeap.insert(20);
        assertEquals(10, minHeap.extractMin());
        assertEquals(20, minHeap.extractMin());
        assertEquals(30, minHeap.extractMin());
        assertEquals(50, minHeap.extractMin());
    }

    @Test
    void minHeapEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> minHeap.getMin());
        assertThrows(NoSuchElementException.class, () -> minHeap.extractMin());
    }

    @Test
    void minHeapFromArray() {
        Integer[] arr = {9, 4, 7, 1, 3, 6, 2, 8, 5};
        MinHeap<Integer> heap = new MinHeap<>(arr);
        assertEquals(1, heap.extractMin());
        assertEquals(2, heap.extractMin());
        assertEquals(3, heap.extractMin());
    }

    @Test
    void maxHeapInsertAndGetMax() {
        maxHeap.insert(10);
        maxHeap.insert(30);
        maxHeap.insert(20);
        maxHeap.insert(50);
        assertEquals(50, maxHeap.getMax());
    }

    @Test
    void maxHeapExtractMax() {
        maxHeap.insert(10);
        maxHeap.insert(30);
        maxHeap.insert(20);
        maxHeap.insert(50);
        assertEquals(50, maxHeap.extractMax());
        assertEquals(30, maxHeap.extractMax());
        assertEquals(20, maxHeap.extractMax());
        assertEquals(10, maxHeap.extractMax());
    }

    @Test
    void maxHeapFromArray() {
        Integer[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        MaxHeap<Integer> heap = new MaxHeap<>(arr);
        assertEquals(9, heap.extractMax());
        assertEquals(8, heap.extractMax());
    }

    @Test
    void heapSort() {
        Integer[] arr = {12, 11, 13, 5, 6, 7, 1, 9, 8, 10};
        HeapSort.sort(arr);
        assertArrayEquals(new Integer[]{1,5,6,7,8,9,10,11,12,13}, arr);
    }

    @Test
    void heapSortPrimitive() {
        int[] arr = {42, 17, 8, 99, 33, 21, 55, 3};
        HeapSort.sort(arr);
        assertArrayEquals(new int[]{3,8,17,21,33,42,55,99}, arr);
    }

    @Test
    void heapSortEmpty() {
        Integer[] arr = {};
        HeapSort.sort(arr);
        assertArrayEquals(new Integer[]{}, arr);
    }

    @Test
    void heapSortSingle() {
        Integer[] arr = {1};
        HeapSort.sort(arr);
        assertArrayEquals(new Integer[]{1}, arr);
    }

    @Test
    void minHeapSize() {
        assertEquals(0, minHeap.size());
        minHeap.insert(1);
        assertEquals(1, minHeap.size());
        minHeap.insert(2);
        assertEquals(2, minHeap.size());
        minHeap.extractMin();
        assertEquals(1, minHeap.size());
    }

    @Test
    void minHeapLargeInsert() {
        for (int i = 100; i >= 1; i--) minHeap.insert(i);
        for (int i = 1; i <= 100; i++) assertEquals(i, minHeap.extractMin());
    }

    @Test
    void maxHeapLargeInsert() {
        for (int i = 1; i <= 100; i++) maxHeap.insert(i);
        for (int i = 100; i >= 1; i--) assertEquals(i, maxHeap.extractMax());
    }
}
