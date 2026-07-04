# Debugging Advanced Trees

## Common Issues

| Symptom | Likely Cause |
|---------|-------------|
| Stack overflow (AVL) | Balance factor not updated correctly → infinite rotation |
| TreeMap returns null for existing key | Comparator inconsistent with equals |
| B-tree returns wrong results | Incorrect split/merge during insert/delete |
| Fenwick wrong prefix sums | Off-by-one (0 vs 1-indexed) |
| Segment tree wrong range query | Wrong merge logic or lazy propagation |

## Debugging Techniques

### Validate BST

```java
boolean isValidBST(TreeNode root, long min, long max) {
    if (root == null) return true;
    if (root.val <= min || root.val >= max) return false;
    return isValidBST(root.left, min, root.val)
        && isValidBST(root.right, root.val, max);
}
```

### Print AVL Tree with Balance Factors

```java
void printAVL(AVLNode node) {
    if (node == null) return;
    System.out.println("Key=" + node.key + " h=" + node.height
        + " bf=" + getBalance(node));
    printAVL(node.left);
    printAVL(node.right);
}
```

### Unit Testing

```java
@Test
void testAVLBalanced() {
    AVLNode root = null;
    for (int i = 1; i <= 100; i++) {
        root = insert(root, i);
    }
    assertTrue(isAVLBalanced(root));
    assertEquals(1 + (int)(1.44 * (Math.log(100) / Math.log(2))),
        height(root), 1);  // within 1.44 log n
}

@Test
void testFenwick() {
    FenwickTree ft = new FenwickTree(10);
    ft.update(1, 5);
    ft.update(2, 3);
    ft.update(3, 7);
    assertEquals(5, ft.query(1));
    assertEquals(8, ft.query(2));
    assertEquals(15, ft.query(3));
}

@Test
void testSegmentTree() {
    int[] arr = {1, 3, 5, 7, 9};
    SegmentTree st = new SegmentTree(arr);
    assertEquals(9, st.query(1, 3, 1, 0, 4));  // 3+5+7=15
    assertEquals(25, st.query(0, 4, 1, 0, 4)); // sum all
}
```
