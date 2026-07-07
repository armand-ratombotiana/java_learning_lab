package com.dsacademy.lab30.cacheoblivious;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CacheObliviousTest {

    @Test
    void testBTreeInsertAndContains() {
        CacheObliviousBTree tree = new CacheObliviousBTree(4);
        tree.insert(10);
        tree.insert(20);
        tree.insert(5);
        assertTrue(tree.contains(10));
        assertTrue(tree.contains(20));
        assertTrue(tree.contains(5));
        assertFalse(tree.contains(15));
    }

    @Test
    void testBTreeLargeInsert() {
        CacheObliviousBTree tree = new CacheObliviousBTree(8);
        int n = 1000;
        for (int i = 0; i < n; i++) {
            tree.insert(i);
        }
        assertEquals(n, tree.size());
        for (int i = 0; i < n; i++) {
            assertTrue(tree.contains(i));
        }
    }

    @Test
    void testBTreeHeight() {
        CacheObliviousBTree tree = new CacheObliviousBTree(4);
        tree.insert(1);
        tree.insert(2);
        tree.insert(3);
        assertTrue(tree.height() >= 1);
    }

    @Test
    void testMatrixMultiplyCorrectness() {
        int n = 8;
        int[][] a = new int[n][n];
        int[][] b = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = i + j;
                b[i][j] = i - j;
            }
        }
        int[][] c = CacheObliviousMatrixMultiply.multiply(a, b, n);
        int[][] cNaive = CacheObliviousMatrixMultiply.multiplyNaive(a, b, n);
        for (int i = 0; i < n; i++) {
            assertArrayEquals(cNaive[i], c[i]);
        }
    }

    @Test
    void testMatrixMultiplyLarger() {
        int n = 128;
        int[][] a = new int[n][n];
        int[][] b = new int[n][n];
        java.util.Random rnd = new java.util.Random(42);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = rnd.nextInt(10);
                b[i][j] = rnd.nextInt(10);
            }
        }
        int[][] c1 = CacheObliviousMatrixMultiply.multiply(a, b, n);
        int[][] c2 = CacheObliviousMatrixMultiply.multiplyNaive(a, b, n);
        for (int i = 0; i < n; i++) {
            assertArrayEquals(c2[i], c1[i]);
        }
    }

    @Test
    void testArraySum() {
        CacheObliviousArray arr = new CacheObliviousArray(100);
        int expected = 100 * 99 / 2;
        assertEquals(expected, arr.sumRecursive(0, 99));
        assertEquals(expected, arr.sumIterative());
    }

    @Test
    void testArrayMax() {
        CacheObliviousArray arr = new CacheObliviousArray(100);
        assertEquals(99, arr.max());
    }

    @Test
    void testArrayShuffle() {
        CacheObliviousArray arr = new CacheObliviousArray(100);
        arr.shuffle();
        assertTrue(arr.max() == 99);
        assertEquals(100, arr.sumIterative());
    }

    @Test
    void testBTreeEmpty() {
        CacheObliviousBTree tree = new CacheObliviousBTree(4);
        assertEquals(0, tree.size());
        assertFalse(tree.contains(42));
    }
}
