# AVL Tree Code Deep Dive

This lab provides a pure Java implementation of an AVL Tree, focusing on the recursive insertion and the logic for the four rotation cases.

## 💻 Pure Java Implementation

```java file="labs/algorithms/data-structures/trees/avl-tree/SOLUTION/AVLTree.java"
package algorithms.datastructures.trees;

/**
 * A fundamental implementation of an AVL (Self-Balancing) Tree.
 */
public class AVLTree {

    class Node {
        int key, height;
        Node left, right;

        Node(int d) {
            key = d;
            height = 1;
        }
    }

    private Node root;

    // Helper to get the height of a node
    private int height(Node n) {
        return (n == null) ? 0 : n.height;
    }

    // Helper to get the balance factor
    private int getBalance(Node n) {
        return (n == null) ? 0 : height(n.left) - height(n.right);
    }

    /**
     * Right Rotation (Fixes Left-Left imbalance)
     */
    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x; // Return new root
    }

    /**
     * Left Rotation (Fixes Right-Right imbalance)
     */
    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y; // Return new root
    }

    public void insert(int key) {
        root = insertRecursive(root, key);
    }

    private Node insertRecursive(Node node, int key) {
        // 1. Standard BST insertion
        if (node == null) return new Node(key);

        if (key < node.key)
            node.left = insertRecursive(node.left, key);
        else if (key > node.key)
            node.right = insertRecursive(node.right, key);
        else
            return node; // Duplicate keys not allowed

        // 2. Update height of this ancestor node
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // 3. Get the balance factor to check for imbalance
        int balance = getBalance(node);

        // --- 4. Handle the 4 Unbalanced Cases ---

        // Left Left Case
        if (balance > 1 && key < node.left.key)
            return rightRotate(node);

        // Right Right Case
        if (balance < -1 && key > node.right.key)
            return leftRotate(node);

        // Left Right Case
        if (balance > 1 && key > node.left.key) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && key < node.right.key) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public void printRoot() {
        if (root != null) {
            System.out.println("Root: " + root.key + " (Height: " + root.height + ")");
        }
    }

    public static void main(String[] args) {
        AVLTree tree = new AVLTree();

        // Insert sorted data which would make a normal BST a linked list
        System.out.println("Inserting 10, 20, 30, 40, 50 into AVL Tree...");
        tree.insert(10);
        tree.insert(20);
        tree.insert(30); // Triggers RR rotation
        tree.insert(40);
        tree.insert(50); // Triggers RR rotation again

        tree.printRoot();
        // Expected: Root should be 20 or 30, not 10.
    }
}
```

## 🔍 Key Takeaways
1. **Post-Order Update**: Notice that the height update and balance checks happen *after* the recursive call returns. This means we re-balance the tree from the bottom up, starting from the point of insertion back to the root.
2. **Double Rotations**: The LR and RL cases require two rotations. For example, in the LR case, we first perform a left rotation on the child to transform it into an LL case, and then a right rotation on the current node to fix it.
3. **Strict Balance**: Unlike Red-Black trees, which allow some imbalance to save on rotation costs, the AVL tree is strictly balanced. This makes it faster for lookups but slightly slower for heavy insertion/deletion workloads.