package com.ds04;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== BinaryTree Demo ===");
        BinaryTree<Integer> bt = new BinaryTree<>(1);
        bt.root.left = new BinaryTree.Node<>(2);
        bt.root.right = new BinaryTree.Node<>(3);
        bt.root.left.left = new BinaryTree.Node<>(4);
        bt.root.left.right = new BinaryTree.Node<>(5);
        bt.root.right.left = new BinaryTree.Node<>(6);
        bt.root.right.right = new BinaryTree.Node<>(7);

        System.out.println("Height: " + bt.height());
        System.out.println("Size: " + bt.size());
        System.out.println("Inorder: " + bt.inorder());
        System.out.println("Preorder: " + bt.preorder());
        System.out.println("Postorder: " + bt.postorder());
        System.out.println("Level-order: " + bt.levelOrder());
        System.out.println("Tree structure:");
        bt.printTree();

        System.out.println("\n=== BinarySearchTree Demo ===");
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        for (int v : new int[]{50, 30, 70, 20, 40, 60, 80, 10, 25}) bst.insert(v);
        System.out.println("Inorder: " + bst.inorder());
        System.out.println("Is BST: " + bst.isBST());
        System.out.println("Search 40: " + bst.search(40));
        System.out.println("Search 99: " + bst.search(99));
        System.out.println("Min: " + bst.findMin() + ", Max: " + bst.findMax());
        bst.delete(20);
        System.out.println("After delete 20: " + bst.inorder());
        bst.delete(50);
        System.out.println("After delete 50: " + bst.inorder());
        System.out.println("Level-order: " + bst.levelOrder());

        System.out.println("\n=== AVLTree Demo ===");
        AVLTree<Integer> avl = new AVLTree<>();
        for (int v : new int[]{10, 20, 30, 40, 50, 25}) avl.insert(v);
        System.out.println("Inorder: " + avl.inorder());
        System.out.println("Level-order: " + avl.levelOrder());
        System.out.println("Is AVL: " + avl.isAVL());
        System.out.println("Search 30: " + avl.search(30));
        avl.delete(40);
        System.out.println("After delete 40: " + avl.inorder());
        System.out.println("Is AVL: " + avl.isAVL());
    }
}
