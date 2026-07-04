# Code Deep Dive: Binary Tree Algorithms

## Serialize and Deserialize (LeetCode 297)

```java
public class Codec {

    // Encodes a tree to a single string (preorder with null markers)
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serialize(root, sb);
        return sb.toString();
    }

    private void serialize(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("null,");
            return;
        }
        sb.append(node.val).append(",");
        serialize(node.left, sb);
        serialize(node.right, sb);
    }

    // Decodes encoded string back to tree
    public TreeNode deserialize(String data) {
        Queue<String> values = new LinkedList<>(Arrays.asList(data.split(",")));
        return deserialize(values);
    }

    private TreeNode deserialize(Queue<String> values) {
        String val = values.poll();
        if ("null".equals(val)) return null;
        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left = deserialize(values);
        node.right = deserialize(values);
        return node;
    }
}
```

## Lowest Common Ancestor (LeetCode 236)

```java
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) {
        return root;
    }
    TreeNode left = lowestCommonAncestor(root.left, p, q);
    TreeNode right = lowestCommonAncestor(root.right, p, q);
    if (left != null && right != null) return root;  // p and q in different subtrees
    return left != null ? left : right;
}
```

## Diameter of Binary Tree (LeetCode 543)

```java
private int diameter = 0;

public int diameterOfBinaryTree(TreeNode root) {
    height(root);
    return diameter;
}

private int height(TreeNode node) {
    if (node == null) return 0;
    int leftHeight = height(node.left);
    int rightHeight = height(node.right);
    // Update diameter: longest path through this node
    diameter = Math.max(diameter, leftHeight + rightHeight);
    return 1 + Math.max(leftHeight, rightHeight);
}
```

## Maximum Path Sum (LeetCode 124)

```java
private int maxSum = Integer.MIN_VALUE;

public int maxPathSum(TreeNode root) {
    maxGain(root);
    return maxSum;
}

private int maxGain(TreeNode node) {
    if (node == null) return 0;
    int left = Math.max(maxGain(node.left), 0);
    int right = Math.max(maxGain(node.right), 0);
    int priceNewPath = node.val + left + right;
    maxSum = Math.max(maxSum, priceNewPath);
    return node.val + Math.max(left, right);
}
```
