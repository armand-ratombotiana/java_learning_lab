# How Advanced Trees Work

## AVL Tree Rotation

```java
class AVLNode {
    int key;
    AVLNode left, right;
    int height;

    AVLNode(int key) {
        this.key = key;
        this.height = 1;
    }
}

// Right rotate (LL case)
AVLNode rightRotate(AVLNode y) {
    AVLNode x = y.left;
    AVLNode T2 = x.right;
    x.right = y;
    y.left = T2;
    y.height = 1 + Math.max(height(y.left), height(y.right));
    x.height = 1 + Math.max(height(x.left), height(x.right));
    return x;
}

// Left rotate (RR case)
AVLNode leftRotate(AVLNode x) {
    AVLNode y = x.right;
    AVLNode T2 = y.left;
    y.left = x;
    x.right = T2;
    x.height = 1 + Math.max(height(x.left), height(x.right));
    y.height = 1 + Math.max(height(y.left), height(y.right));
    return y;
}

// Get balance factor
int getBalance(AVLNode node) {
    if (node == null) return 0;
    return height(node.left) - height(node.right);
}

// Insert with rebalancing
AVLNode insert(AVLNode node, int key) {
    if (node == null) return new AVLNode(key);
    if (key < node.key) node.left = insert(node.left, key);
    else if (key > node.key) node.right = insert(node.right, key);
    else return node;  // no duplicates

    node.height = 1 + Math.max(height(node.left), height(node.right));
    int balance = getBalance(node);

    // 4 cases:
    if (balance > 1 && key < node.left.key) return rightRotate(node);           // LL
    if (balance < -1 && key > node.right.key) return leftRotate(node);          // RR
    if (balance > 1 && key > node.left.key) {                                  // LR
        node.left = leftRotate(node.left);
        return rightRotate(node);
    }
    if (balance < -1 && key < node.right.key) {                                // RL
        node.right = rightRotate(node.right);
        return leftRotate(node);
    }
    return node;
}
```

## B-Tree Properties

```java
class BTreeNode {
    int[] keys;          // array of keys
    int t;               // minimum degree (order)
    BTreeNode[] children;
    int n;               // current number of keys
    boolean leaf;

    BTreeNode(int t, boolean leaf) {
        this.t = t;
        this.leaf = leaf;
        this.keys = new int[2 * t - 1];
        this.children = new BTreeNode[2 * t];
        this.n = 0;
    }
}
```

- Every node except root: at least t-1 keys, at most 2t-1 keys
- Every node except root: at least t children, at most 2t children
- All leaves at same depth

## Fenwick Tree (Binary Indexed Tree)

```java
class FenwickTree {
    int[] bit;
    int n;

    FenwickTree(int n) {
        this.n = n;
        this.bit = new int[n + 1];
    }

    void update(int idx, int delta) {
        while (idx <= n) {
            bit[idx] += delta;
            idx += idx & -idx;  // LSB (least significant bit)
        }
    }

    int query(int idx) {
        int sum = 0;
        while (idx > 0) {
            sum += bit[idx];
            idx -= idx & -idx;
        }
        return sum;
    }

    int rangeQuery(int l, int r) {
        return query(r) - query(l - 1);
    }
}
```

## Segment Tree (Range Sum)

```java
class SegmentTree {
    int[] tree;
    int n;

    SegmentTree(int[] arr) {
        n = arr.length;
        tree = new int[4 * n];
        build(arr, 1, 0, n - 1);
    }

    void build(int[] arr, int node, int l, int r) {
        if (l == r) {
            tree[node] = arr[l];
        } else {
            int mid = (l + r) / 2;
            build(arr, 2 * node, l, mid);
            build(arr, 2 * node + 1, mid + 1, r);
            tree[node] = tree[2 * node] + tree[2 * node + 1];
        }
    }

    void update(int pos, int val, int node, int l, int r) {
        if (l == r) tree[node] = val;
        else {
            int mid = (l + r) / 2;
            if (pos <= mid) update(pos, val, 2 * node, l, mid);
            else update(pos, val, 2 * node + 1, mid + 1, r);
            tree[node] = tree[2 * node] + tree[2 * node + 1];
        }
    }

    int query(int ql, int qr, int node, int l, int r) {
        if (ql > r || qr < l) return 0;
        if (ql <= l && r <= qr) return tree[node];
        int mid = (l + r) / 2;
        return query(ql, qr, 2 * node, l, mid)
             + query(ql, qr, 2 * node + 1, mid + 1, r);
    }
}
```
