package com.ds01;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== DynamicArray Demo ===");
        DynamicArray<Integer> da = new DynamicArray<>();
        for (int i = 1; i <= 10; i++) da.add(i);
        System.out.println("After adding 1-10: " + da);
        System.out.println("Size: " + da.size());
        System.out.println("Get index 5: " + da.get(5));
        da.set(0, 100);
        System.out.println("After set(0,100): " + da);
        da.add(5, 55);
        System.out.println("After add(5,55): " + da);
        da.remove(0);
        System.out.println("After remove(0): " + da);
        System.out.println("Contains 55: " + da.contains(55));
        System.out.println("IndexOf 55: " + da.indexOf(55));
        da.rotate(3);
        System.out.println("After rotate(3): " + da);
        da.clear();
        System.out.println("After clear, isEmpty: " + da.isEmpty());

        System.out.println("\n=== Binary Search Demo ===");
        int[] sorted = {2, 5, 8, 12, 16, 23, 38, 56, 72, 91};
        System.out.println("Array: " + Arrays.toString(sorted));
        System.out.println("Search 23: index " + ArrayAlgorithms.binarySearch(sorted, 23));
        System.out.println("Search 99: index " + ArrayAlgorithms.binarySearch(sorted, 99));

        System.out.println("\n=== Two-Pointer Demo ===");
        int[] sorted2 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int target = 12;
        int[] pair = ArrayAlgorithms.twoSumSorted(sorted2, target);
        System.out.println("Two sum for target=" + target + ": indices [" + pair[0] + "," + pair[1] + "] (" + sorted2[pair[0]] + "+" + sorted2[pair[1]] + ")");

        System.out.println("\n=== Array Rotation Demo ===");
        int[] rot = {1, 2, 3, 4, 5, 6, 7};
        System.out.println("Original: " + Arrays.toString(rot));
        ArrayAlgorithms.rotateLeft(rot, 3);
        System.out.println("Rotate left by 3: " + Arrays.toString(rot));

        System.out.println("\n=== Remove Duplicates Demo ===");
        int[] dup = {0, 0, 1, 1, 1, 2, 2, 3, 3, 4};
        int len = ArrayAlgorithms.removeDuplicatesSorted(dup);
        System.out.println("Unique count: " + len + ", array: " + Arrays.toString(Arrays.copyOf(dup, len)));

        System.out.println("\n=== Max Subarray Sum Demo ===");
        int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println("Max subarray sum: " + ArrayAlgorithms.maxSubarraySum(nums));
    }
}
