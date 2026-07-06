package com.dsacademy.lab12.segmenttree;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Random;

public class SegmentTreeTest {

    @Test
    void testBuildAndQuery() {
        int[] arr = {1, 3, 5, 7, 9, 11};
        RecursiveSegmentTree st = new RecursiveSegmentTree(arr);
        assertEquals(36, st.rangeSum(0, 5));
        assertEquals(15, st.rangeSum(0, 2));
        assertEquals(27, st.rangeSum(3, 5));
        assertEquals(1, st.rangeSum(0, 0));
    }

    @Test
    void testPointUpdate() {
        int[] arr = {1, 2, 3, 4, 5};
        RecursiveSegmentTree st = new RecursiveSegmentTree(arr);
        st.update(2, 10);
        assertEquals(1 + 2 + 10 + 4 + 5, st.rangeSum(0, 4));
        assertEquals(10, st.rangeSum(2, 2));
    }

    @Test
    void testEmptyArray() {
        RecursiveSegmentTree st = new RecursiveSegmentTree(new int[]{});
        assertEquals(0, st.rangeSum(0, -1));
        assertEquals(0, st.size());
    }

    @Test
    void testSingleElement() {
        RecursiveSegmentTree st = new RecursiveSegmentTree(new int[]{42});
        assertEquals(42, st.rangeSum(0, 0));
        st.update(0, 100);
        assertEquals(100, st.rangeSum(0, 0));
    }

    @Test
    void testLazyRangeAdd() {
        int[] arr = {1, 2, 3, 4, 5};
        LazySegmentTree lst = new LazySegmentTree(arr);
        lst.rangeAdd(1, 3, 10);
        assertEquals(1 + 12 + 13 + 14 + 5, lst.rangeSum(0, 4));
        assertEquals(12, lst.rangeSum(1, 1));
        assertEquals(13, lst.rangeSum(2, 2));
        assertEquals(14, lst.rangeSum(3, 3));
    }

    @Test
    void testLazyMultipleUpdates() {
        int[] arr = {0, 0, 0, 0, 0};
        LazySegmentTree lst = new LazySegmentTree(arr);
        lst.rangeAdd(0, 2, 5);
        lst.rangeAdd(1, 4, 3);
        assertEquals(5, lst.rangeSum(0, 0));
        assertEquals(8, lst.rangeSum(1, 1));
        assertEquals(8, lst.rangeSum(2, 2));
        assertEquals(3, lst.rangeSum(3, 3));
        assertEquals(3, lst.rangeSum(4, 4));
    }

    @Test
    void testLazyPointUpdate() {
        int[] arr = {1, 2, 3, 4, 5};
        LazySegmentTree lst = new LazySegmentTree(arr);
        lst.rangeAdd(0, 4, 1);
        lst.pointUpdate(2, 100);
        assertEquals(100, lst.rangeSum(2, 2));
        assertEquals(2, lst.rangeSum(0, 0));
    }

    @Test
    void testIterativeSegmentTree() {
        int[] arr = {1, 3, 5, 7, 9, 11};
        IterativeSegmentTree ist = new IterativeSegmentTree(arr);
        assertEquals(36, ist.rangeSum(0, 5));
        assertEquals(15, ist.rangeSum(0, 2));
        ist.update(2, 6);
        assertEquals(1 + 3 + 6 + 7 + 9 + 11, ist.rangeSum(0, 5));
    }

    @Test
    void testMinMaxSegmentTree() {
        int[] arr = {5, 2, 8, 1, 9, 3};
        SegmentTreeMinMax mm = new SegmentTreeMinMax(arr);
        assertEquals(1, mm.rangeMin(0, 5));
        assertEquals(9, mm.rangeMax(0, 5));
        assertEquals(2, mm.rangeMin(0, 1));
        assertEquals(8, mm.rangeMax(0, 2));
        mm.update(3, 10);
        assertEquals(2, mm.rangeMin(0, 5));
        assertEquals(10, mm.rangeMax(0, 5));
    }

    @Test
    void testRangeSumWithLargeData() {
        int n = 10000;
        int[] arr = new int[n];
        Random rand = new Random(42);
        for (int i = 0; i < n; i++) arr[i] = rand.nextInt(1000);
        RecursiveSegmentTree st = new RecursiveSegmentTree(arr);
        for (int i = 0; i < 100; i++) {
            int l = rand.nextInt(n);
            int r = rand.nextInt(n - l) + l;
            long expected = 0;
            for (int j = l; j <= r; j++) expected += arr[j];
            assertEquals(expected, st.rangeSum(l, r));
        }
    }

    @Test
    void testInvalidInputs() {
        RecursiveSegmentTree st = new RecursiveSegmentTree(new int[]{1, 2, 3});
        assertThrows(IllegalArgumentException.class, () -> st.rangeSum(2, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> st.rangeSum(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> st.rangeSum(0, 5));
        assertThrows(NullPointerException.class, () -> new RecursiveSegmentTree(null));
    }

    @Test
    void testIterativeSingleElement() {
        IterativeSegmentTree ist = new IterativeSegmentTree(new int[]{7});
        assertEquals(7, ist.rangeSum(0, 0));
        ist.update(0, 3);
        assertEquals(3, ist.rangeSum(0, 0));
    }
}
