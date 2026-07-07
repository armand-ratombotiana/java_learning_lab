package com.dsacademy.lab34.pst;

public final class PersistentSegmentTree {

    private final int size;
    private PersistentNode[] roots;
    private int versionCount;

    public PersistentSegmentTree(int[] initial) {
        if (initial == null || initial.length == 0) {
            throw new IllegalArgumentException("Initial array cannot be null or empty");
        }
        this.size = initial.length;
        this.roots = new PersistentNode[64];
        this.versionCount = 0;
        this.roots[0] = build(0, size - 1, initial);
    }

    private PersistentNode build(int l, int r, int[] arr) {
        if (l == r) {
            return new PersistentNode(null, null, arr[l]);
        }
        int mid = (l + r) >>> 1;
        PersistentNode leftChild = build(l, mid, arr);
        PersistentNode rightChild = build(mid + 1, r, arr);
        return new PersistentNode(leftChild, rightChild, leftChild.value + rightChild.value);
    }

    public int update(int pos, int newValue) {
        return update(pos, newValue, versionCount);
    }

    public int update(int pos, int newValue, int baseVersion) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException("Position " + pos + " out of range [0, " + size + ")");
        }
        if (baseVersion < 0 || baseVersion > versionCount) {
            throw new IllegalArgumentException("Invalid version " + baseVersion);
        }
        PersistentNode newRoot = update(roots[baseVersion], 0, size - 1, pos, newValue);
        versionCount++;
        if (versionCount >= roots.length) {
            PersistentNode[] newRoots = new PersistentNode[roots.length * 2];
            System.arraycopy(roots, 0, newRoots, 0, roots.length);
            roots = newRoots;
        }
        roots[versionCount] = newRoot;
        return versionCount;
    }

    private PersistentNode update(PersistentNode node, int l, int r, int pos, int newValue) {
        if (l == r) {
            return new PersistentNode(null, null, newValue);
        }
        int mid = (l + r) >>> 1;
        if (pos <= mid) {
            PersistentNode newLeft = update(node.left, l, mid, pos, newValue);
            return new PersistentNode(newLeft, node.right, newLeft.value + node.right.value);
        }
        PersistentNode newRight = update(node.right, mid + 1, r, pos, newValue);
        return new PersistentNode(node.left, newRight, node.left.value + newRight.value);
    }

    public int query(int ql, int qr, int version) {
        if (version < 0 || version > versionCount) {
            throw new IllegalArgumentException("Invalid version " + version);
        }
        return query(roots[version], 0, size - 1, ql, qr);
    }

    private int query(PersistentNode node, int l, int r, int ql, int qr) {
        if (ql > r || qr < l) return 0;
        if (ql <= l && r <= qr) return node.value;
        int mid = (l + r) >>> 1;
        return query(node.left, l, mid, ql, qr) + query(node.right, mid + 1, r, ql, qr);
    }

    public int get(int pos, int version) {
        return query(pos, pos, version);
    }

    public int getLatestVersion() {
        return versionCount;
    }

    public int getSize() {
        return size;
    }

    public PersistentNode getRoot(int version) {
        if (version < 0 || version > versionCount) return null;
        return roots[version];
    }

    public int getVersionCount() {
        return versionCount;
    }
}
