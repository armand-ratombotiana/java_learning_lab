package com.ds09;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AdvancedTreeTest {

    private RedBlackTree<Integer> rbt;
    private BTree<Integer> btree;
    private SegmentTree segTree;
    private FenwickTree fenwick;

    @BeforeEach
    void setUp() {
        rbt = new RedBlackTree<>();
        btree = new BTree<>(3);
        segTree = new SegmentTree(new int[]{1, 3, 5, 7, 9, 11});
        fenwick = new FenwickTree(new int[]{2, 4, 6, 8, 10, 12, 14, 16});
    }

    @Test
    void rbtInsertAndSearch() {
        rbt.insert(10); rbt.insert(20); rbt.insert(30);
        rbt.insert(40); rbt.insert(50);
        assertTrue(rbt.search(10));
        assertTrue(rbt.search(50));
        assertFalse(rbt.search(99));
    }

    @Test
    void rbtInorderSorted() {
        for (int v : new int[]{50, 30, 70, 20, 40, 60, 80}) rbt.insert(v);
        var order = rbt.inorder();
        assertArrayEquals(new Integer[]{20,30,40,50,60,70,80}, order.toArray());
    }

    @Test
    void rbtDelete() {
        for (int v : new int[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100}) rbt.insert(v);
        rbt.delete(50);
        assertFalse(rbt.search(50));
        rbt.delete(30);
        assertFalse(rbt.search(30));
    }

    @Test
    void rbtRootIsBlack() {
        rbt.insert(10); rbt.insert(20); rbt.insert(30);
        assertNotNull(rbt.getRoot());
        assertFalse(rbt.getRoot().isRed());
    }

    @Test
    void btreeInsertAndSearch() {
        btree.insert(10); btree.insert(20); btree.insert(5);
        btree.insert(6); btree.insert(12); btree.insert(30);
        btree.insert(7); btree.insert(17);
        assertTrue(btree.search(10));
        assertTrue(btree.search(17));
        assertFalse(btree.search(99));
    }

    @Test
    void btreeInorderSorted() {
        for (int v : new int[]{10, 20, 5, 6, 12, 30, 7, 17, 3, 25, 40, 50}) btree.insert(v);
        var order = btree.inorder();
        Integer[] expected = {3, 5, 6, 7, 10, 12, 17, 20, 25, 30, 40, 50};
        assertArrayEquals(expected, order.toArray());
    }

    @Test
    void segmentTreeRangeQuery() {
        assertEquals(24, segTree.query(1, 4));
        assertEquals(36, segTree.query(0, 5));
        assertEquals(12, segTree.query(2, 3));
    }

    @Test
    void segmentTreePointUpdate() {
        segTree.update(2, 10);
        assertEquals(43, segTree.query(0, 5));
    }

    @Test
    void segmentTreeSingleElement() {
        assertEquals(1, segTree.query(0, 0));
    }

    @Test
    void fenwickPrefixSum() {
        assertEquals(20, fenwick.query(3));
        assertEquals(42, fenwick.query(5));
    }

    @Test
    void fenwickRangeSum() {
        assertEquals(24, fenwick.rangeQuery(2, 6));
    }

    @Test
    void fenwickUpdate() {
        fenwick.update(3, 5);
        assertEquals(25, fenwick.query(3));
    }

    @Test
    void fenwickFullRange() {
        assertEquals(72, fenwick.rangeQuery(0, 7));
    }
}
