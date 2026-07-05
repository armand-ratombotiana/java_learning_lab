package com.ds01;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

public class ArrayAlgorithmsTest {

    @Test
    void binarySearchFindsElement() {
        int[] arr = {1, 3, 5, 7, 9, 11, 13, 15};
        assertEquals(3, ArrayAlgorithms.binarySearch(arr, 7));
        assertEquals(0, ArrayAlgorithms.binarySearch(arr, 1));
        assertEquals(7, ArrayAlgorithms.binarySearch(arr, 15));
    }

    @Test
    void binarySearchNotFound() {
        int[] arr = {1, 3, 5, 7, 9};
        assertEquals(-1, ArrayAlgorithms.binarySearch(arr, 4));
        assertEquals(-1, ArrayAlgorithms.binarySearch(arr, 0));
        assertEquals(-1, ArrayAlgorithms.binarySearch(arr, 10));
    }

    @Test
    void binarySearchRecursive() {
        int[] arr = {2, 4, 6, 8, 10, 12, 14};
        assertEquals(3, ArrayAlgorithms.binarySearchRecursive(arr, 8));
        assertEquals(-1, ArrayAlgorithms.binarySearchRecursive(arr, 5));
    }

    @Test
    void twoSumSortedFound() {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] result = ArrayAlgorithms.twoSumSorted(arr, 10);
        assertNotEquals(-1, result[0]);
        assertEquals(10, arr[result[0]] + arr[result[1]]);
    }

    @Test
    void twoSumSortedNotFound() {
        int[] arr = {1, 2, 3, 4};
        int[] result = ArrayAlgorithms.twoSumSorted(arr, 100);
        assertEquals(-1, result[0]);
        assertEquals(-1, result[1]);
    }

    @Test
    void reverseArray() {
        int[] arr = {1, 2, 3, 4, 5};
        ArrayAlgorithms.reverse(arr);
        assertArrayEquals(new int[]{5, 4, 3, 2, 1}, arr);
    }

    @Test
    void rotateLeftByZero() {
        int[] arr = {1, 2, 3};
        ArrayAlgorithms.rotateLeft(arr, 0);
        assertArrayEquals(new int[]{1, 2, 3}, arr);
    }

    @Test
    void rotateLeftByPositive() {
        int[] arr = {1, 2, 3, 4, 5};
        ArrayAlgorithms.rotateLeft(arr, 2);
        assertArrayEquals(new int[]{3, 4, 5, 1, 2}, arr);
    }

    @Test
    void rotateRight() {
        int[] arr = {1, 2, 3, 4, 5};
        ArrayAlgorithms.rotateRight(arr, 2);
        assertArrayEquals(new int[]{4, 5, 1, 2, 3}, arr);
    }

    @Test
    void maxSubarraySum() {
        int[] arr = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        assertEquals(6, ArrayAlgorithms.maxSubarraySum(arr));
    }

    @Test
    void maxSubarrayAllNegative() {
        int[] arr = {-5, -3, -8, -2};
        assertEquals(-2, ArrayAlgorithms.maxSubarraySum(arr));
    }

    @Test
    void hasPairWithSum() {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        assertTrue(ArrayAlgorithms.hasPairWithSum(arr, 15));
        assertFalse(ArrayAlgorithms.hasPairWithSum(arr, 200));
    }

    @Test
    void removeDuplicatesSorted() {
        int[] arr = {0, 0, 1, 1, 1, 2, 2, 3, 3, 4};
        int len = ArrayAlgorithms.removeDuplicatesSorted(arr);
        assertEquals(5, len);
        assertEquals(0, arr[0]);
        assertEquals(1, arr[1]);
        assertEquals(2, arr[2]);
        assertEquals(3, arr[3]);
        assertEquals(4, arr[4]);
    }

    @Test
    void removeDuplicatesEmptyArray() {
        int[] arr = {};
        assertEquals(0, ArrayAlgorithms.removeDuplicatesSorted(arr));
    }
}
