package com.dsacademy.lab13.fenwicktree;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Random;

public class FenwickTreeTest {

    @Test
    void testAddAndSum() {
        FenwickTree ft = new FenwickTree(10);
        ft.add(0, 5);
        ft.add(1, 3);
        ft.add(2, 7);
        assertEquals(5, ft.sum(0));
        assertEquals(8, ft.sum(1));
        assertEquals(15, ft.sum(2));
        assertEquals(15, ft.sum(9));
    }

    @Test
    void testRangeSum() {
        FenwickTree ft = new FenwickTree(5);
        ft.add(0, 1);
        ft.add(1, 2);
        ft.add(2, 3);
        ft.add(3, 4);
        ft.add(4, 5);
        assertEquals(1 + 2 + 3, ft.rangeSum(0, 2));
        assertEquals(4 + 5, ft.rangeSum(3, 4));
        assertEquals(15, ft.rangeSum(0, 4));
    }

    @Test
    void testFromArray() {
        int[] arr = {2, 4, 6, 8, 10};
        FenwickTree ft = FenwickTree.fromArray(arr);
        assertEquals(2, ft.sum(0));
        assertEquals(6, ft.sum(1));
        assertEquals(20, ft.sum(3));
        assertEquals(30, ft.sum(4));
    }

    @Test
    void testInversionCount() {
        assertEquals(0, InversionCount.countInversions(new int[]{1, 2, 3, 4, 5}));
        assertEquals(10, InversionCount.countInversions(new int[]{5, 4, 3, 2, 1}));
        assertEquals(3, InversionCount.countInversions(new int[]{2, 3, 1}));
        assertEquals(0, InversionCount.countInversions(new int[]{}));
    }

    @Test
    void testInversionCountWithCompression() {
        assertEquals(0, InversionCount.countInversionsWithCompression(new int[]{1, 2, 3}));
        assertEquals(3, InversionCount.countInversionsWithCompression(new int[]{3, 2, 1}));
        assertEquals(5, InversionCount.countInversionsWithCompression(new int[]{100, 50, 75, 25}));
    }

    @Test
    void testLargeInversionCount() {
        int n = 1000;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = n - i;
        long expected = (long) n * (n - 1) / 2;
        assertEquals(expected, InversionCount.countInversions(arr));
    }

    @Test
    void testRangeUpdatePointQuery() {
        FenwickTreeRangeUpdate ft = new FenwickTreeRangeUpdate(10);
        ft.rangeAdd(2, 5, 10);
        ft.rangeAdd(0, 3, 5);
        assertEquals(5, ft.pointQuery(0));
        assertEquals(5, ft.pointQuery(1));
        assertEquals(15, ft.pointQuery(2));
        assertEquals(15, ft.pointQuery(3));
        assertEquals(10, ft.pointQuery(4));
        assertEquals(10, ft.pointQuery(5));
        assertEquals(0, ft.pointQuery(6));
    }

    @Test
    void test2DBIT() {
        FenwickTree2D ft = new FenwickTree2D(4, 4);
        ft.add(0, 0, 1);
        ft.add(1, 1, 2);
        ft.add(2, 2, 3);
        assertEquals(1, ft.rangeSum(0, 0, 0, 0));
        assertEquals(3, ft.rangeSum(0, 0, 1, 1));
        assertEquals(6, ft.rangeSum(0, 0, 2, 2));
    }

    @Test
    void test2DRangeSum() {
        FenwickTree2D ft = new FenwickTree2D(5, 5);
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                ft.add(i, j, 1);
        assertEquals(9, ft.rangeSum(1, 1, 3, 3));
        assertEquals(25, ft.rangeSum(0, 0, 4, 4));
    }

    @Test
    void test2DSet() {
        FenwickTree2D ft = new FenwickTree2D(3, 3);
        ft.set(1, 1, 10);
        assertEquals(10, ft.rangeSum(1, 1, 1, 1));
        ft.set(1, 1, 5);
        assertEquals(5, ft.rangeSum(1, 1, 1, 1));
    }

    @Test
    void testEdgeCases() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            FenwickTree ft = new FenwickTree(5);
            ft.add(-1, 1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            FenwickTree ft = new FenwickTree(5);
            ft.sum(10);
        });
        assertThrows(IllegalArgumentException.class, () -> new FenwickTree(-1));
    }

    @Test
    void testRangeQueryLongValues() {
        FenwickTreeRangeQuery ft = new FenwickTreeRangeQuery(5);
        ft.add(0, 1000000000L);
        ft.add(1, 2000000000L);
        assertEquals(3000000000L, ft.rangeSum(0, 1));
    }

    @Test
    void testRandomOperations() {
        Random rand = new Random(42);
        int n = 100;
        int[] arr = new int[n];
        FenwickTree ft = new FenwickTree(n);
        for (int op = 0; op < 500; op++) {
            int idx = rand.nextInt(n);
            int delta = rand.nextInt(100);
            arr[idx] += delta;
            ft.add(idx, delta);
            int q = rand.nextInt(n);
            int expected = 0;
            for (int i = 0; i <= q; i++) expected += arr[i];
            assertEquals(expected, ft.sum(q));
        }
    }
}
