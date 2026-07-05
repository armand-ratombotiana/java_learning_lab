package com.ds09;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== RedBlackTree Demo ===");
        RedBlackTree<Integer> rbt = new RedBlackTree<>();
        for (int v : new int[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100}) rbt.insert(v);
        System.out.println("Inorder: " + rbt.inorder());
        System.out.println("Search 50: " + rbt.search(50));
        System.out.println("Search 55: " + rbt.search(55));
        rbt.delete(50);
        System.out.println("After delete 50: " + rbt.inorder());
        System.out.println("Search 50: " + rbt.search(50));
        System.out.println("Level-order: " + rbt.levelOrder());

        System.out.println("\n=== BTree Demo ===");
        BTree<Integer> bt = new BTree<>(3);
        for (int v : new int[]{10, 20, 5, 6, 12, 30, 7, 17, 3, 25, 40, 50}) bt.insert(v);
        System.out.println("Inorder: " + bt.inorder());
        System.out.println("Search 12: " + bt.search(12));
        System.out.println("Search 99: " + bt.search(99));
        System.out.println("Level-order: " + bt.levelOrder());

        System.out.println("\n=== SegmentTree Demo ===");
        int[] segArr = {1, 3, 5, 7, 9, 11};
        SegmentTree segTree = new SegmentTree(segArr);
        System.out.println("Array: " + Arrays.toString(segArr));
        System.out.println("Range sum [1,4]: " + segTree.query(1, 4));
        System.out.println("Range sum [0,5]: " + segTree.query(0, 5));
        System.out.println("Range sum [2,3]: " + segTree.query(2, 3));
        segTree.update(2, 10);
        System.out.println("After update index 2 to 10");
        System.out.println("Range sum [0,5]: " + segTree.query(0, 5));
        System.out.println("Range sum [2,3]: " + segTree.query(2, 3));

        System.out.println("\n=== FenwickTree Demo ===");
        int[] fenArr = {2, 4, 6, 8, 10, 12, 14, 16};
        FenwickTree ft = new FenwickTree(fenArr);
        System.out.println("Array: " + Arrays.toString(fenArr));
        System.out.println("Prefix sum [0,3]: " + ft.query(3));
        System.out.println("Prefix sum [0,5]: " + ft.query(5));
        System.out.println("Range sum [2,6]: " + ft.rangeQuery(2, 6));
        ft.update(3, 5);
        System.out.println("After update index 3 by +5");
        System.out.println("Prefix sum [0,3]: " + ft.query(3));
        System.out.println("Range sum [2,6]: " + ft.rangeQuery(2, 6));
    }
}
