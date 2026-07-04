# Common Mistakes with Advanced Trees

## AVL: Not Updating Height Correctly

```java
// WRONG — forget to update height after rotation
AVLNode rightRotate(AVLNode y) {
    AVLNode x = y.left;
    AVLNode T2 = x.right;
    x.right = y;
    y.left = T2;
    // forgot: y.height = 1 + max(height(y.left), height(y.right));
    // forgot: x.height = 1 + max(height(x.left), height(x.right));
    return x;
}
```

## AVL: Wrong Balance Factor Direction

```java
// WRONG — reversed
int balance = height(node.right) - height(node.left);  // should be left - right
```

## Red-Black: Null Color Confusion

In Java's TreeMap, `RED = false` and `BLACK = true`. Many implementations use the opposite. Checking `null` parent color must be handled:

```java
// Must handle null parent
private boolean colorOf(Entry p) {
    return (p == null) ? BLACK : p.color;
}
```

## B-Tree: Wrong Split Condition

```java
// WRONG — splitting when keys == 2t-1 (correct for insert)
// But splitting on delete requires mergedown when keys == t-1
```

## Fenwick: 0-Indexed Confusion

```java
// WRONG — fenwick uses 1-indexed internally
FenwickTree ft = new FenwickTree(n);
ft.update(0, 5);  // index 0 causes infinite loop!

// CORRECT
ft.update(1, 5);  // 1-indexed
```

## Segment Tree: Off-by-One in Range Queries

```java
// WRONG — inclusive/exclusive mixup
int query(int node, int l, int r, int ql, int qr) {
    if (ql > r || qr < l) return 0;  // ">" vs ">=" confusion
    if (ql >= l && qr <= r) return tree[node];  // should be >= and <=
}
```
