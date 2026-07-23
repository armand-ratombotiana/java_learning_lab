package com.leetcode.dlx;

/**
 * Custom: Dancing Links (Algorithm X) for Exact Cover
 * Solve the exact cover problem using dancing links technique.
 *
 * Time Complexity: O(2^n) worst case
 * Space Complexity: O(n * m)
 */
public class DancingLinks {

    public static class Node {
        Node left, right, up, down, col;
        int row, size;
        Node() {
            left = right = up = down = col = this;
        }
    }

    private final Node header;
    private final int cols;

    public DancingLinks(int cols) {
        this.cols = cols;
        header = new Node();
        Node prev = header;
        for (int i = 0; i < cols; i++) {
            Node col = new Node();
            col.size = 0;
            col.col = col;
            prev.right = col;
            col.left = prev;
            prev = col;
        }
        prev.right = header;
        header.left = prev;
    }

    public void addRow(int row, int[] columns) {
        Node first = null;
        for (int c : columns) {
            Node col = header.right;
            for (int i = 0; i < c; i++) col = col.right;
            Node node = new Node();
            node.row = row;
            node.col = col;
            col.size++;

            node.down = col;
            node.up = col.up;
            col.up.down = node;
            col.up = node;

            if (first == null) {
                first = node;
                node.left = node;
                node.right = node;
            } else {
                node.left = first.left;
                node.right = first;
                first.left.right = node;
                first.left = node;
            }
        }
    }

    private void cover(Node col) {
        col.right.left = col.left;
        col.left.right = col.right;
        for (Node row = col.down; row != col; row = row.down) {
            for (Node node = row.right; node != row; node = node.right) {
                node.down.up = node.up;
                node.up.down = node.down;
                node.col.size--;
            }
        }
    }

    private void uncover(Node col) {
        for (Node row = col.up; row != col; row = row.up) {
            for (Node node = row.left; node != row; node = node.left) {
                node.col.size++;
                node.down.up = node;
                node.up.down = node;
            }
        }
        col.right.left = col;
        col.left.right = col;
    }

    public int search(int[] solution, int k) {
        if (header.right == header) return k;
        Node col = header.right;
        for (Node c = col.right; c != header; c = c.right) {
            if (c.size < col.size) col = c;
        }
        if (col.size == 0) return -1;

        cover(col);
        for (Node row = col.down; row != col; row = row.down) {
            solution[k] = row.row;
            for (Node node = row.right; node != row; node = node.right) cover(node.col);
            int result = search(solution, k + 1);
            if (result >= 0) return result;
            for (Node node = row.left; node != row; node = node.left) uncover(node.col);
        }
        uncover(col);
        return -1;
    }

    public static void main(String[] args) {
        // Simple exact cover test
        DancingLinks dlx = new DancingLinks(3);
        dlx.addRow(0, new int[]{0, 1});
        dlx.addRow(1, new int[]{1, 2});
        dlx.addRow(2, new int[]{0, 2});

        int[] solution = new int[10];
        int len = dlx.search(solution, 0);
        System.out.print("Solution exists: " + (len >= 0) + " (expected: true)");
        if (len >= 0) {
            System.out.print(", rows: ");
            for (int i = 0; i < len; i++) System.out.print(solution[i] + " ");
        }
        System.out.println();
    }
}
