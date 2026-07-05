package com.ds07;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== MinHeap Demo ===");
        MinHeap<Integer> minHeap = new MinHeap<>();
        minHeap.insert(30);
        minHeap.insert(10);
        minHeap.insert(50);
        minHeap.insert(20);
        minHeap.insert(40);
        System.out.println("MinHeap: " + minHeap);
        System.out.println("GetMin: " + minHeap.getMin());
        System.out.println("ExtractMin: " + minHeap.extractMin());
        System.out.println("ExtractMin: " + minHeap.extractMin());
        System.out.println("After extracts: " + minHeap);
        System.out.println("Size: " + minHeap.size());

        System.out.println("\n=== MinHeap from Array (heapify) ===");
        Integer[] arr = {9, 4, 7, 1, 3, 6, 2, 8, 5};
        MinHeap<Integer> heapFromArray = new MinHeap<>(arr);
        System.out.println("Heapified: " + heapFromArray);
        while (!heapFromArray.isEmpty()) {
            System.out.print(heapFromArray.extractMin() + " ");
        }
        System.out.println("\n");

        System.out.println("=== MaxHeap Demo ===");
        MaxHeap<Integer> maxHeap = new MaxHeap<>();
        maxHeap.insert(10);
        maxHeap.insert(30);
        maxHeap.insert(20);
        maxHeap.insert(50);
        maxHeap.insert(40);
        System.out.println("MaxHeap: " + maxHeap);
        System.out.println("GetMax: " + maxHeap.getMax());
        System.out.println("ExtractMax: " + maxHeap.extractMax());
        System.out.println("ExtractMax: " + maxHeap.extractMax());
        System.out.println("After extracts: " + maxHeap);

        System.out.println("\n=== HeapSort Demo ===");
        Integer[] unsorted = {12, 11, 13, 5, 6, 7, 1, 9, 8, 10};
        System.out.println("Before: " + Arrays.toString(unsorted));
        HeapSort.sort(unsorted);
        System.out.println("After:  " + Arrays.toString(unsorted));

        int[] primitiveArr = {42, 17, 8, 99, 33, 21, 55, 3};
        System.out.println("\nPrimitive int[] Before: " + Arrays.toString(primitiveArr));
        HeapSort.sort(primitiveArr);
        System.out.println("Primitive int[] After:  " + Arrays.toString(primitiveArr));
    }
}
