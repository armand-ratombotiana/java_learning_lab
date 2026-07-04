# Common Mistakes with Trees

## Null Root Not Handled

```java
// WRONG — NullPointerException on null root
int height(TreeNode root) {
    return 1 + Math.max(height(root.left), height(root.right));
}

// CORRECT
int height(TreeNode root) {
    if (root == null) return -1;
    return 1 + Math.max(height(root.left), height(root.right));
}
```

## Infinite Recursion

```java
// WRONG — no base case, or cycle in tree
void traverse(TreeNode node) {
    process(node);
    traverse(node.left);   // Does not check for null!
    traverse(node.right);
}
```

## Confusing Pre/In/Post Order

```java
// WRONG — postorder labeled as inorder
void inorder(TreeNode node) {
    process(node.data);      // This is preorder!
    inorder(node.left);
    inorder(node.right);
}
```

## Not Using equals() for Comparison

```java
// WRONG — reference comparison for strings
if (node.data == "target") { ... }

// CORRECT
if ("target".equals(node.data)) { ... }
```

## Mutating Tree During Traversal

```java
// WRONG — deleting nodes during traversal invalidates references
void deleteLeaves(TreeNode node) {
    if (node.left == null && node.right == null) {
        node = null;  // This only changes local reference!
    }
}

// CORRECT — parent must update child reference
TreeNode deleteLeaves(TreeNode node) {
    if (node == null) return null;
    if (node.left == null && node.right == null) return null;
    node.left = deleteLeaves(node.left);
    node.right = deleteLeaves(node.right);
    return node;
}
```

## Stack Overflow from Deep Trees

```java
// Recursive traversal of a degenerate tree (10000 nodes) → StackOverflowError
void traverse(TreeNode node) {
    if (node == null) return;
    traverse(node.left);  // recursion depth = n
}

// Use iterative approach for deep trees
void traverse(TreeNode root) {
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode curr = root;
    while (curr != null || !stack.isEmpty()) {
        while (curr != null) { stack.push(curr); curr = curr.left; }
        curr = stack.pop();
        process(curr);
        curr = curr.right;
    }
}
```
