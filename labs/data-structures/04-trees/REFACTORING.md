# Refactoring Trees

## Replace Recursion with Iteration

```java
// Before — recursion (risk of stack overflow)
void inorder(TreeNode node, List<Integer> result) {
    if (node == null) return;
    inorder(node.left, result);
    result.add(node.val);
    inorder(node.right, result);
}

// After — iteration
void inorder(TreeNode root, List<Integer> result) {
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode curr = root;
    while (curr != null || !stack.isEmpty()) {
        while (curr != null) {
            stack.push(curr);
            curr = curr.left;
        }
        curr = stack.pop();
        result.add(curr.val);
        curr = curr.right;
    }
}
```

## Use Functional Style (Java 8+)

```java
// Before — manual traversal
List<Integer> values = new ArrayList<>();
inorder(root, values);

// After — stream-like (with a custom Spliterator)
// Or use libraries like vavr for functional tree operations
```

## Extract Tree Visitor Pattern

```java
// Before — hardcoded operations
void inorder(TreeNode node) {
    if (node == null) return;
    inorder(node.left);
    System.out.println(node.val);  // hardcoded
    inorder(node.right);
}

// After — visitor
interface Visitor<E> {
    void visit(E data);
}

void inorder(TreeNode node, Visitor<Integer> visitor) {
    if (node == null) return;
    inorder(node.left, visitor);
    visitor.visit(node.val);
    inorder(node.right, visitor);
}
```

## Convert Array to Tree and Vice Versa

```java
// Array → Complete Binary Tree
TreeNode arrayToTree(int[] arr, int i) {
    if (i >= arr.length) return null;
    TreeNode node = new TreeNode(arr[i]);
    node.left = arrayToTree(arr, 2 * i + 1);
    node.right = arrayToTree(arr, 2 * i + 2);
    return node;
}

// Tree → Array (level order)
List<Integer> treeToArray(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    while (!queue.isEmpty()) {
        TreeNode node = queue.poll();
        result.add(node != null ? node.val : null);
        if (node != null) {
            queue.offer(node.left);
            queue.offer(node.right);
        }
    }
    return result;
}
```
