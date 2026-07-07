package com.dsacademy.lab29.vanemboas;

public class VanEmdeBoasTree {

    private VebNode root;
    private final int universeSize;

    public VanEmdeBoasTree(int universeSize) {
        if (universeSize <= 0 || (universeSize & (universeSize - 1)) != 0) {
            throw new IllegalArgumentException("Universe size must be a power of 2");
        }
        this.universeSize = universeSize;
        this.root = new VebNode(universeSize);
    }

    public void insert(int x) {
        validate(x);
        insert(root, x);
    }

    private void insert(VebNode node, int x) {
        if (node.min == -1) {
            node.min = x;
            node.max = x;
            return;
        }
        if (x < node.min) {
            int tmp = x; x = node.min; node.min = tmp;
        }
        if (node.universeSize > 2) {
            int subSize = VebNode.higherSquareRoot(node.universeSize);
            int clusterIdx = VebNode.high(x, subSize);
            int offset = VebNode.low(x, subSize);
            if (node.cluster[clusterIdx] == null) {
                node.cluster[clusterIdx] = new VebNode(subSize);
            }
            if (node.cluster[clusterIdx].min == -1) {
                insert(node.summary, clusterIdx);
            }
            insert(node.cluster[clusterIdx], offset);
        }
        if (x > node.max) {
            node.max = x;
        }
    }

    public boolean contains(int x) {
        validate(x);
        return contains(root, x);
    }

    private boolean contains(VebNode node, int x) {
        if (x == node.min || x == node.max) return true;
        if (node.universeSize == 2) return false;
        int subSize = VebNode.higherSquareRoot(node.universeSize);
        int clusterIdx = VebNode.high(x, subSize);
        int offset = VebNode.low(x, subSize);
        if (node.cluster[clusterIdx] == null) return false;
        return contains(node.cluster[clusterIdx], offset);
    }

    public int predecessor(int x) {
        validate(x);
        return predecessor(root, x);
    }

    private int predecessor(VebNode node, int x) {
        if (node.min == -1) return -1;
        if (x <= node.min) return -1;
        if (node.universeSize == 2) return node.min;
        if (node.max != -1 && x > node.max) return node.max;
        int subSize = VebNode.higherSquareRoot(node.universeSize);
        int clusterIdx = VebNode.high(x, subSize);
        int offset = VebNode.low(x, subSize);
        if (node.cluster[clusterIdx] != null && node.cluster[clusterIdx].min != -1
                && offset > node.cluster[clusterIdx].min) {
            int pred = predecessor(node.cluster[clusterIdx], offset);
            if (pred != -1) {
                return VebNode.index(clusterIdx, pred, subSize);
            }
        }
        int predCluster = predecessor(node.summary, clusterIdx);
        if (predCluster == -1) {
            return (node.min < x) ? node.min : -1;
        }
        VebNode predNode = node.cluster[predCluster];
        return VebNode.index(predCluster, predNode.max, subSize);
    }

    public int successor(int x) {
        validate(x);
        return successor(root, x);
    }

    private int successor(VebNode node, int x) {
        if (node.min == -1) return -1;
        if (x < node.min) return node.min;
        if (node.universeSize == 2) {
            return (x == 0 && node.max == 1) ? 1 : -1;
        }
        int subSize = VebNode.higherSquareRoot(node.universeSize);
        int clusterIdx = VebNode.high(x, subSize);
        int offset = VebNode.low(x, subSize);
        if (node.cluster[clusterIdx] != null && offset < node.cluster[clusterIdx].max) {
            int succ = successor(node.cluster[clusterIdx], offset);
            if (succ != -1) {
                return VebNode.index(clusterIdx, succ, subSize);
            }
        }
        int succCluster = successor(node.summary, clusterIdx);
        if (succCluster == -1) return -1;
        VebNode succNode = node.cluster[succCluster];
        return VebNode.index(succCluster, succNode.min, subSize);
    }

    public int min() { return root.min; }
    public int max() { return root.max; }
    public boolean isEmpty() { return root.min == -1; }

    private void validate(int x) {
        if (x < 0 || x >= universeSize) {
            throw new IndexOutOfBoundsException("Value out of universe range");
        }
    }
}
