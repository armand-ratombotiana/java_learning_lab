package com.dsacademy.lab34.pst;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class PersistentSegmentTreeTest {

    private PersistentSegmentTree pst;

    @BeforeEach
    void setUp() {
        pst = new PersistentSegmentTree(new int[]{1, 2, 3, 4, 5});
    }

    @Test
    void testInitialQuery() {
        assertEquals(15, pst.query(0, 4, 0));
        assertEquals(6, pst.query(0, 2, 0));
        assertEquals(9, pst.query(2, 4, 0));
    }

    @Test
    void testSingleElementQuery() {
        assertEquals(1, pst.get(0, 0));
        assertEquals(3, pst.get(2, 0));
        assertEquals(5, pst.get(4, 0));
    }

    @Test
    void testVersioning() {
        int v1 = pst.update(2, 10);
        assertEquals(0, pst.getLatestVersion());
        int actualLatest = pst.getLatestVersion();
        assertEquals(v1, actualLatest);
        assertEquals(10, pst.get(2, v1));
        assertEquals(3, pst.get(2, 0));
    }

    @Test
    void testMultipleVersions() {
        int v1 = pst.update(0, 10);
        int v2 = pst.update(1, 20);
        assertEquals(10, pst.get(0, v1));
        assertEquals(1, pst.get(0, 0));
        assertEquals(20, pst.get(1, v2));
        assertEquals(2, pst.get(1, 0));
    }

    @Test
    void testVersionIndependence() {
        int v1 = pst.update(4, 100);
        int v2 = pst.update(4, 200);
        assertEquals(100, pst.get(4, v1));
        assertEquals(200, pst.get(4, v2));
        assertEquals(5, pst.get(4, 0));
    }

    @Test
    void testRangeSumAcrossVersions() {
        int v1 = pst.update(0, 10);
        assertEquals(10 + 2 + 3 + 4 + 5, pst.query(0, 4, v1));
        assertEquals(15, pst.query(0, 4, 0));
    }

    @Test
    void testUpdateInvalidPosition() {
        assertThrows(IndexOutOfBoundsException.class, () -> pst.update(100, 1));
    }

    @Test
    void testQueryInvalidVersion() {
        assertThrows(IllegalArgumentException.class, () -> pst.query(0, 1, 99));
    }

    @Test
    void testEmptyInitial() {
        assertThrows(IllegalArgumentException.class, () -> new PersistentSegmentTree(new int[]{}));
    }

    @Test
    void testLargeArray() {
        int n = 10000;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i;
        PersistentSegmentTree large = new PersistentSegmentTree(arr);
        int sum = n * (n - 1) / 2;
        assertEquals(sum, large.query(0, n - 1, 0));
    }

    @Test
    void testKthSmallest() {
        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};
        KthSmallestRange ksr = new KthSmallestRange(arr);
        assertEquals(1, ksr.query(0, 3, 1));
        assertEquals(3, ksr.query(0, 3, 3));
        assertEquals(2, ksr.query(0, 4, 2));
        assertEquals(5, ksr.query(0, 7, 5));
    }

    @Test
    void testKthSmallestEdgeCases() {
        int[] arr = {5, 5, 5, 5};
        KthSmallestRange ksr = new KthSmallestRange(arr);
        assertEquals(5, ksr.query(0, 3, 1));
        assertEquals(5, ksr.query(0, 3, 4));
    }

    @Test
    void testKthSmallestStaticMethod() {
        int[] arr = {4, 2, 7, 1, 9, 3};
        int[][] queries = {{0, 2, 2}, {1, 4, 3}, {0, 5, 1}};
        int[] results = KthSmallestRange.rangeQuery(arr, queries);
        assertArrayEquals(new int[]{4, 7, 1}, results);
    }

    @Test
    void testKthSmallestInvalidK() {
        int[] arr = {1, 2, 3};
        KthSmallestRange ksr = new KthSmallestRange(arr);
        assertThrows(IllegalArgumentException.class, () -> ksr.query(0, 2, 4));
    }

    @Test
    void testNullInitial() {
        assertThrows(IllegalArgumentException.class, () -> new PersistentSegmentTree(null));
    }
}
