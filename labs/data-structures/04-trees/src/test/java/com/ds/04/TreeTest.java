package com.ds04;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;

public class TreeTest {

    private BinarySearchTree<Integer> bst;
    private AVLTree<Integer> avl;

    @BeforeEach
    void setUp() {
        bst = new BinarySearchTree<>();
        avl = new AVLTree<>();
    }

    @Test
    void bstInsertAndInorder() {
        bst.insert(5); bst.insert(3); bst.insert(7);
        bst.insert(2); bst.insert(4); bst.insert(6); bst.insert(8);
        var order = bst.inorder();
        assertArrayEquals(new Integer[]{2,3,4,5,6,7,8}, order.toArray());
    }

    @Test
    void bstSearch() {
        bst.insert(10); bst.insert(5); bst.insert(15);
        assertTrue(bst.search(10));
        assertTrue(bst.search(5));
        assertTrue(bst.search(15));
        assertFalse(bst.search(99));
    }

    @Test
    void bstDeleteLeaf() {
        bst.insert(10); bst.insert(5); bst.insert(15);
        bst.delete(5);
        assertFalse(bst.search(5));
    }

    @Test
    void bstDeleteWithOneChild() {
        bst.insert(10); bst.insert(5); bst.insert(3);
        bst.delete(5);
        assertFalse(bst.search(5));
        assertTrue(bst.search(3));
    }

    @Test
    void bstDeleteWithTwoChildren() {
        bst.insert(50); bst.insert(30); bst.insert(70);
        bst.insert(20); bst.insert(40); bst.insert(60); bst.insert(80);
        bst.delete(50);
        assertFalse(bst.search(50));
    }

    @Test
    void bstFindMinMax() {
        bst.insert(50); bst.insert(30); bst.insert(70); bst.insert(20); bst.insert(40);
        assertEquals(20, bst.findMin());
        assertEquals(70, bst.findMax());
    }

    @Test
    void bstEmptyFindMinThrows() {
        assertThrows(NoSuchElementException.class, () -> bst.findMin());
    }

    @Test
    void bstIsBST() {
        bst.insert(10); bst.insert(5); bst.insert(15);
        assertTrue(bst.isBST());
    }

    @Test
    void avlInsertMaintainsBalance() {
        avl.insert(10); avl.insert(20); avl.insert(30);
        avl.insert(40); avl.insert(50); avl.insert(25);
        assertTrue(avl.isAVL());
    }

    @Test
    void avlSearch() {
        avl.insert(10); avl.insert(20); avl.insert(30);
        assertTrue(avl.search(20));
        assertFalse(avl.search(99));
    }

    @Test
    void avlDelete() {
        avl.insert(10); avl.insert(20); avl.insert(30);
        avl.insert(40); avl.insert(50); avl.insert(25);
        avl.delete(40);
        assertFalse(avl.search(40));
        assertTrue(avl.isAVL());
    }

    @Test
    void avlLeftRotation() {
        avl.insert(10); avl.insert(20); avl.insert(30);
        assertEquals(Integer.valueOf(20), avl.getRoot().getData());
        assertTrue(avl.isAVL());
    }

    @Test
    void avlRightRotation() {
        avl.insert(30); avl.insert(20); avl.insert(10);
        assertEquals(Integer.valueOf(20), avl.getRoot().getData());
        assertTrue(avl.isAVL());
    }

    @Test
    void avlLeftRightRotation() {
        avl.insert(30); avl.insert(10); avl.insert(20);
        assertEquals(Integer.valueOf(20), avl.getRoot().getData());
        assertTrue(avl.isAVL());
    }

    @Test
    void avlRightLeftRotation() {
        avl.insert(10); avl.insert(30); avl.insert(20);
        assertEquals(Integer.valueOf(20), avl.getRoot().getData());
        assertTrue(avl.isAVL());
    }

    @Test
    void binaryTreeTraversals() {
        BinaryTree<Integer> bt = new BinaryTree<>(1);
        bt.root.left = new BinaryTree.Node<>(2);
        bt.root.right = new BinaryTree.Node<>(3);
        bt.root.left.left = new BinaryTree.Node<>(4);
        bt.root.left.right = new BinaryTree.Node<>(5);
        assertEquals(5, bt.size());
        assertEquals(2, bt.height());
        assertArrayEquals(new Integer[]{4,2,5,1,3}, bt.inorder().toArray());
        assertArrayEquals(new Integer[]{1,2,4,5,3}, bt.preorder().toArray());
        assertArrayEquals(new Integer[]{4,5,2,3,1}, bt.postorder().toArray());
        assertArrayEquals(new Integer[]{1,2,3,4,5}, bt.levelOrder().toArray());
    }
}
