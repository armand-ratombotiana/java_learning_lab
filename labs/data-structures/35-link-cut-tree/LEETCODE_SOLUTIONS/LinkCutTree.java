package com.leetcode.linkcut;

/**
 * Custom: Link-Cut Tree
 * Dynamic tree data structure that supports link, cut, and path queries.
 * Uses splay trees for O(log n) amortized operations.
 *
 * This is a simplified single-operation demo (conceptual implementation).
 *
 * Time Complexity: O(log n) amortized per operation
 * Space Complexity: O(n)
 */
public class LinkCutTree {

    public static class Node {
        int val;
        Node left, right, parent;
        boolean reversed;

        Node(int val) { this.val = val; }
    }

    private boolean isRoot(Node x) {
        return x.parent == null || (x.parent.left != x && x.parent.right != x);
    }

    private void push(Node x) {
        if (x == null || !x.reversed) return;
        Node t = x.left; x.left = x.right; x.right = t;
        if (x.left != null) x.left.reversed = !x.left.reversed;
        if (x.right != null) x.right.reversed = !x.right.reversed;
        x.reversed = false;
    }

    private void rotate(Node x) {
        Node p = x.parent;
        if (p == null) return;
        Node g = p.parent;
        if (!isRoot(p)) {
            if (g.left == p) g.left = x;
            else g.right = x;
        }
        if (p.left == x) {
            p.left = x.right;
            if (x.right != null) x.right.parent = p;
            x.right = p;
        } else {
            p.right = x.left;
            if (x.left != null) x.left.parent = p;
            x.left = p;
        }
        p.parent = x;
        x.parent = g;
    }

    private void splay(Node x) {
        while (!isRoot(x)) {
            Node p = x.parent;
            if (!isRoot(p)) {
                push(p.parent);
                push(p);
                push(x);
                if ((p.left == x) == (p.parent.left == p)) rotate(p);
                else rotate(x);
            } else {
                push(p);
                push(x);
            }
            rotate(x);
        }
        push(x);
    }

    public void access(Node x) {
        Node last = null;
        while (x != null) {
            splay(x);
            x.right = last;
            last = x;
            x = x.parent;
        }
    }

    public void makeRoot(Node x) {
        access(x);
        splay(x);
        x.reversed = !x.reversed;
    }

    public void link(Node x, Node y) {
        makeRoot(x);
        x.parent = y;
    }

    public void cut(Node x, Node y) {
        makeRoot(x);
        access(y);
        splay(y);
        if (y.left == x) {
            y.left = null;
            x.parent = null;
        }
    }

    public static void main(String[] args) {
        LinkCutTree lct = new LinkCutTree();
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);

        lct.link(n1, n2);
        lct.link(n2, n3);
        System.out.println("Linked 1-2-3");

        lct.cut(n1, n2);
        System.out.println("Cut 1-2");

        lct.link(n1, n3);
        System.out.println("Linked 1-3");

        // Access test
        lct.access(n1);
        System.out.println("Accessed node 1");

        System.out.println("Link-Cut Tree demo complete");
    }
}
