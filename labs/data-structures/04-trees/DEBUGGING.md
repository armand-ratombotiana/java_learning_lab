# Debugging Trees

## Common Issues

| Symptom | Likely Cause |
|---------|-------------|
| StackOverflowError | Infinite recursion or degenerate tree |
| Wrong traversal order | Confused pre/in/post-order logic |
| NullPointerException | Accessing child of null node |
| Missing nodes after modification | Node reassigned in wrong scope |
| Incorrect height/depth | Off-by-one in null base case |

## Debugging Techniques

### Print Tree Structure

```java
void printTree(TreeNode root) {
    printTree(root, 0);
}

void printTree(TreeNode node, int depth) {
    if (node == null) return;
    printTree(node.right, depth + 1);
    System.out.println("  ".repeat(depth) + node.val);
    printTree(node.left, depth + 1);
}
```

Output (rotated 90° counter-clockwise):
```
    7
  6
    5
3
    4
  2
    1
```

### Verify BST Invariant

```java
boolean isValidBST(TreeNode root) {
    return isValidBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
}

boolean isValidBST(TreeNode node, long min, long max) {
    if (node == null) return true;
    if (node.val <= min || node.val >= max) return false;
    return isValidBST(node.left, min, node.val)
        && isValidBST(node.right, node.val, max);
}
```

### Visualize Recursion

```java
int height(TreeNode node, String indent) {
    if (node == null) {
        System.out.println(indent + "height(null) = -1");
        return -1;
    }
    System.out.println(indent + "height(" + node.val + ")");
    int h = 1 + Math.max(height(node.left, indent + "  "),
                         height(node.right, indent + "  "));
    System.out.println(indent + "→ " + h);
    return h;
}
```

### Unit Testing

```java
@Test
void testTraversals() {
    TreeNode root = new TreeNode(1);
    root.left = new TreeNode(2);
    root.right = new TreeNode(3);

    List<Integer> pre = new ArrayList<>();
    preorder(root, pre);
    assertEquals(List.of(1, 2, 3), pre);

    List<Integer> in = new ArrayList<>();
    inorder(root, in);
    assertEquals(List.of(2, 1, 3), in);
}

@Test
void testSerializeDeserialize() {
    Codec codec = new Codec();
    TreeNode root = makeTree();
    String serialized = codec.serialize(root);
    TreeNode deserialized = codec.deserialize(serialized);
    assertTrue(treesEqual(root, deserialized));
}
```
