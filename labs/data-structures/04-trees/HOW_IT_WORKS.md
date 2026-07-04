# How Trees Work

## Binary Tree Node

```java
class TreeNode<E> {
    E data;
    TreeNode<E> left;
    TreeNode<E> right;

    TreeNode(E data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}
```

## Depth-First Traversals (Recursive)

```java
// Preorder: root → left → right
void preorder(TreeNode<E> node) {
    if (node == null) return;
    process(node.data);
    preorder(node.left);
    preorder(node.right);
}

// Inorder: left → root → right
void inorder(TreeNode<E> node) {
    if (node == null) return;
    inorder(node.left);
    process(node.data);
    inorder(node.right);
}

// Postorder: left → right → root
void postorder(TreeNode<E> node) {
    if (node == null) return;
    postorder(node.left);
    postorder(node.right);
    process(node.data);
}
```

## Iterative Inorder Traversal (Using Stack)

```java
void inorderIterative(TreeNode<E> root) {
    Deque<TreeNode<E>> stack = new ArrayDeque<>();
    TreeNode<E> current = root;

    while (current != null || !stack.isEmpty()) {
        // Go left as far as possible
        while (current != null) {
            stack.push(current);
            current = current.left;
        }
        // Pop and process
        current = stack.pop();
        process(current.data);
        // Go right
        current = current.right;
    }
}
```

## Breadth-First Traversal (Level-Order)

```java
void levelOrder(TreeNode<E> root) {
    if (root == null) return;
    Queue<TreeNode<E>> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        TreeNode<E> current = queue.poll();
        process(current.data);
        if (current.left != null) queue.offer(current.left);
        if (current.right != null) queue.offer(current.right);
    }
}
```

## Tree Properties

```java
int height(TreeNode<E> node) {
    if (node == null) return -1;  // edge count
    return 1 + Math.max(height(node.left), height(node.right));
}

int size(TreeNode<E> node) {
    if (node == null) return 0;
    return 1 + size(node.left) + size(node.right);
}

int depth(TreeNode<E> root, TreeNode<E> target, int d) {
    if (root == null) return -1;
    if (root == target) return d;
    int left = depth(root.left, target, d + 1);
    if (left != -1) return left;
    return depth(root.right, target, d + 1);
}
```
