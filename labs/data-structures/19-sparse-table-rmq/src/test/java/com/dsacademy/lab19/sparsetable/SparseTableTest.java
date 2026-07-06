package com.dsacademy.lab19.sparsetable;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Random;

public class SparseTableTest {

    @Test
    void testRangeMin() {
        int[] arr = {5, 2, 8, 1, 9, 3, 7, 4};
        SparseTableMin st = new SparseTableMin(arr);
        assertEquals(1, st.rangeMin(0, 7));
        assertEquals(2, st.rangeMin(0, 1));
        assertEquals(3, st.rangeMin(5, 7));
        assertEquals(5, st.rangeMin(0, 0));
    }

    @Test
    void testRangeMax() {
        int[] arr = {5, 2, 8, 1, 9, 3, 7, 4};
        SparseTableMax st = new SparseTableMax(arr);
        assertEquals(9, st.rangeMax(0, 7));
        assertEquals(8, st.rangeMax(0, 2));
        assertEquals(7, st.rangeMax(4, 6));
    }

    @Test
    void testRangeSum() {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8};
        SparseTableSum st = new SparseTableSum(arr);
        assertEquals(36, st.rangeSum(0, 7));
        assertEquals(6, st.rangeSum(0, 2));
        assertEquals(15, st.rangeSum(3, 5));
    }

    @Test
    void testDisjointSparseTable() {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8};
        DisjointSparseTable dst = new DisjointSparseTable(arr);
        assertEquals(36, dst.rangeSum(0, 7));
        assertEquals(6, dst.rangeSum(0, 2));
        assertEquals(15, dst.rangeSum(3, 5));
    }

    @Test
    void testSingleElement() {
        SparseTableMin st = new SparseTableMin(new int[]{42});
        assertEquals(42, st.rangeMin(0, 0));
    }

    @Test
    void testPowerOfTwo() {
        int n = 16;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = n - i;
        SparseTableMin st = new SparseTableMin(arr);
        assertEquals(1, st.rangeMin(0, n - 1));
        assertEquals(8, st.rangeMin(0, 7));
    }

    @Test
    void testRandomMinQueries() {
        Random rand = new Random(42);
        int n = 1000;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = rand.nextInt(10000);
        SparseTableMin st = new SparseTableMin(arr);
        for (int q = 0; q < 500; q++) {
            int l = rand.nextInt(n);
            int r = rand.nextInt(n - l) + l;
            int expected = Integer.MAX_VALUE;
            for (int i = l; i <= r; i++) expected = Math.min(expected, arr[i]);
            assertEquals(expected, st.rangeMin(l, r));
        }
    }

    @Test
    void testRandomSumQueries() {
        Random rand = new Random(42);
        int n = 500;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = rand.nextInt(100);
        SparseTableSum st = new SparseTableSum(arr);
        DisjointSparseTable dst = new DisjointSparseTable(arr);
        for (int q = 0; q < 500; q++) {
            int l = rand.nextInt(n);
            int r = rand.nextInt(n - l) + l;
            int expected = 0;
            for (int i = l; i <= r; i++) expected += arr[i];
            assertEquals(expected, st.rangeSum(l, r));
            assertEquals(expected, dst.rangeSum(l, r));
        }
    }

    @Test
    void testEmptyTable() {
        assertThrows(IllegalStateException.class, () -> new SparseTableMin(new int[]{}).rangeMin(0, 0));
    }

    @Test
    void testInvalidRange() {
        SparseTableMin st = new SparseTableMin(new int[]{1, 2, 3});
        assertThrows(IllegalArgumentException.class, () -> st.rangeMin(1, 0));
    }
}
