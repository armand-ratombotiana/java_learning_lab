package com.dsacademy.lab32.dlx;

public final class DancingLinks {

    private ColumnNode root;
    private int[] solution;
    private int solutionSize;
    private int solutionCount;

    public DancingLinks(boolean[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException("Matrix cannot be null or empty");
        }
        build(matrix);
    }

    private static final class ColumnNode {
        ColumnNode left, right, up, down;
        String name;
        int size;

        ColumnNode(String name) {
            this.name = name;
            this.left = this;
            this.right = this;
            this.up = this;
            this.down = this;
            this.size = 0;
        }
    }

    private static final class DataNode {
        DataNode left, right, up, down;
        ColumnNode column;
        int rowId;

        DataNode(ColumnNode column, int rowId) {
            this.column = column;
            this.rowId = rowId;
            this.left = this;
            this.right = this;
            this.up = this;
            this.down = this;
        }
    }

    private void build(boolean[][] matrix) {
        int cols = matrix[0].length;
        root = new ColumnNode("root");
        ColumnNode[] columns = new ColumnNode[cols];
        for (int j = 0; j < cols; j++) {
            columns[j] = new ColumnNode("col" + j);
            columns[j].left = root;
            columns[j].right = root;
            root.left.right = columns[j];
            root.left = columns[j];
        }
        for (int i = 0; i < matrix.length; i++) {
            DataNode prev = null;
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j]) {
                    DataNode node = new DataNode(columns[j], i);
                    if (prev == null) {
                        prev = node;
                    } else {
                        node.left = prev;
                        node.right = prev.right;
                        prev.right.left = node;
                        prev.right = node;
                    }
                    node.up = columns[j].up;
                    node.down = columns[j];
                    columns[j].up.down = node;
                    columns[j].up = node;
                    columns[j].size++;
                }
            }
        }
        solution = new int[matrix.length];
        solutionSize = 0;
        solutionCount = 0;
    }

    public boolean solveOne() {
        solutionCount = 0;
        solutionSize = 0;
        return search();
    }

    public int[][] solveAll() {
        solutionCount = 0;
        solutionSize = 0;
        java.util.List<int[]> solutions = new java.util.ArrayList<>();
        searchAll(solutions);
        return solutions.toArray(new int[0][]);
    }

    private ColumnNode chooseColumn() {
        int minSize = Integer.MAX_VALUE;
        ColumnNode choice = null;
        for (ColumnNode c = (ColumnNode) root.right; c != root; c = (ColumnNode) c.right) {
            if (c.size < minSize) {
                minSize = c.size;
                choice = c;
                if (minSize == 0) break;
            }
        }
        return choice;
    }

    private void cover(ColumnNode col) {
        col.right.left = col.left;
        col.left.right = col.right;
        for (DataNode row = (DataNode) col.down; row != col; row = (DataNode) row.down) {
            for (DataNode node = (DataNode) row.right; node != row; node = (DataNode) node.right) {
                node.down.up = node.up;
                node.up.down = node.down;
                node.column.size--;
            }
        }
    }

    private void uncover(ColumnNode col) {
        for (DataNode row = (DataNode) col.up; row != col; row = (DataNode) row.up) {
            for (DataNode node = (DataNode) row.left; node != row; node = (DataNode) node.left) {
                node.column.size++;
                node.down.up = node;
                node.up.down = node;
            }
        }
        col.right.left = col;
        col.left.right = col;
    }

    private boolean search() {
        if (root.right == root) return true;
        ColumnNode col = chooseColumn();
        if (col == null) return false;
        cover(col);
        for (DataNode row = (DataNode) col.down; row != col; row = (DataNode) row.down) {
            solution[solutionSize++] = row.rowId;
            for (DataNode node = (DataNode) row.right; node != row; node = (DataNode) node.right) {
                cover(node.column);
            }
            if (search()) return true;
            for (DataNode node = (DataNode) row.left; node != row; node = (DataNode) node.left) {
                uncover(node.column);
            }
            solutionSize--;
        }
        uncover(col);
        return false;
    }

    private void searchAll(java.util.List<int[]> solutions) {
        if (root.right == root) {
            solutions.add(java.util.Arrays.copyOf(solution, solutionSize));
            return;
        }
        ColumnNode col = chooseColumn();
        if (col == null) return;
        cover(col);
        for (DataNode row = (DataNode) col.down; row != col; row = (DataNode) row.down) {
            solution[solutionSize++] = row.rowId;
            for (DataNode node = (DataNode) row.right; node != row; node = (DataNode) node.right) {
                cover(node.column);
            }
            searchAll(solutions);
            for (DataNode node = (DataNode) row.left; node != row; node = (DataNode) node.left) {
                uncover(node.column);
            }
            solutionSize--;
        }
        uncover(col);
    }

    public int[] getSolution() {
        return java.util.Arrays.copyOf(solution, solutionSize);
    }

    public int getSolutionCount() {
        return solutionCount;
    }

    public boolean hasSolution() {
        return solutionSize > 0;
    }
}
