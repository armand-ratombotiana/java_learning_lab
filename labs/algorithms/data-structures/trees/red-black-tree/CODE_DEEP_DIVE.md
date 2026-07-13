# Red-Black Tree Code Deep Dive

This lab provides a pure Java implementation of a Red-Black Tree's core mechanics: Node insertion, Rotations, and the complex Fix-Up logic that maintains the 5 mathematical invariants.

## 💻 Pure Java Implementation

```java file="labs/algorithms/data-structures/trees/red-black-tree/SOLUTION/RedBlackTree.java"
package algorithms.datastructures.trees;

/**
 * A fundamental implementation of a Red-Black Tree.
 * Focuses on the Insertion and Fix-Up logic.
 */
public class RedBlackTree {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    class Node {
        int data;
        Node left, right, parent;
        boolean color;

        Node(int data) {
            this.data = data;
            this.color = RED; // Rule: New nodes are ALWAYS inserted as RED
        }
    }

    private Node root;
    // TNULL acts as the sentinel "NIL" leaf node (Rule 3: All leaves are BLACK)
    private final Node TNULL;

    public RedBlackTree() {
        TNULL = new Node(0);
        TNULL.color = BLACK;
        root = TNULL;
    }

    /**
     * Left Rotation: Brings the right child up, pushes the current node down to the left.
     */
    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        
        if (y.left != TNULL) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        
        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    /**
     * Right Rotation: Brings the left child up, pushes the current node down to the right.
     */
    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        
        if (y.right != TNULL) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        
        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    /**
     * Standard BST Insertion, followed by the Red-Black Fix-Up.
     */
    public void insert(int key) {
        Node node = new Node(key);
        node.parent = null;
        node.data = key;
        node.left = TNULL;
        node.right = TNULL;

        Node y = null;
        Node x = this.root;

        // 1. Standard BST traversal to find insertion point
        while (x != TNULL) {
            y = x;
            if (node.data < x.data) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        // 2. Insert the node
        node.parent = y;
        if (y == null) {
            root = node;
        } else if (node.data < y.data) {
            y.left = node;
        } else {
            y.right = node;
        }

        // 3. Fix the Red-Black invariants
        if (node.parent == null) {
            node.color = BLACK; // Rule 2: Root is always BLACK
            return;
        }
        if (node.parent.parent == null) {
            return;
        }

        fixInsert(node);
    }

    /**
     * The core algorithm that restores the 5 Red-Black invariants after an insertion.
     */
    private void fixInsert(Node k) {
        Node u;
        // Loop while there is a violation of Rule 4 (Two RED nodes in a row)
        while (k.parent.color == RED) {
            
            // Case A: Parent is the LEFT child of Grandparent
            if (k.parent == k.parent.parent.left) {
                u = k.parent.parent.right; // 'u' is the UNCLE
                
                // Case 1: Uncle is RED. 
                // Fix: Recoloring only.
                if (u.color == RED) {
                    u.color = BLACK;
                    k.parent.color = BLACK;
                    k.parent.parent.color = RED;
                    k = k.parent.parent; // Move up the tree and check again
                } else {
                    // Case 2: Uncle is BLACK, and 'k' is a RIGHT child (Triangle shape).
                    // Fix: Left rotate on parent to convert to Case 3.
                    if (k == k.parent.right) {
                        k = k.parent;
                        leftRotate(k);
                    }
                    // Case 3: Uncle is BLACK, and 'k' is a LEFT child (Line shape).
                    // Fix: Recolor parent/grandparent and Right rotate on grandparent.
                    k.parent.color = BLACK;
                    k.parent.parent.color = RED;
                    rightRotate(k.parent.parent);
                }
            } 
            // Case B: Parent is the RIGHT child of Grandparent (Symmetric to Case A)
            else {
                u = k.parent.parent.left; // 'u' is the UNCLE

                if (u.color == RED) {
                    u.color = BLACK;
                    k.parent.color = BLACK;
                    k.parent.parent.color = RED;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.left) {
                        k = k.parent;
                        rightRotate(k);
                    }
                    k.parent.color = BLACK;
                    k.parent.parent.color = RED;
                    leftRotate(k.parent.parent);
                }
            }
            if (k == root) {
                break;
            }
        }
        root.color = BLACK; // Rule 2: Root must always be BLACK
    }

    public static void main(String[] args) {
        RedBlackTree bst = new RedBlackTree();
        
        // Insert sorted data (The worst-case scenario for a standard BST)
        System.out.println("Inserting 1, 2, 3, 4, 5 into the Red-Black Tree...");
        bst.insert(1);
        bst.insert(2);
        bst.insert(3);
        bst.insert(4);
        bst.insert(5);
        
        System.out.println("Root is now: " + bst.root.data + " (Color: " + (bst.root.color == BLACK ? "BLACK" : "RED") + ")");
        // If this was a standard BST, the root would be 1, and it would be a linked list.
        // Because it's a Red-Black tree, the rotations will have pulled 2 (or 3) up to the root to balance it!
    }
}
```

## 🔍 Key Takeaways
1. **The Default Color**: Notice that `Node` constructor always sets the color to `RED`. We always insert as RED because inserting a BLACK node immediately violates Rule 5 (Black Depth). Inserting a RED node might violate Rule 4 (Two Reds), which is much easier to fix via rotations.
2. **The Uncle Node**: The entire `fixInsert` logic depends on the color of the "Uncle" node (the sibling of the parent). If the Uncle is RED, we can fix the tree just by flipping colors and pushing the RED violation up the tree. If the Uncle is BLACK, we are forced to perform Rotations to restructure the tree.
3. **The Sentinel `TNULL`**: Notice we don't use `null` for empty leaves. We use a single, shared `TNULL` object that is permanently set to `BLACK`. This elegantly satisfies Rule 3 (All leaves are Black) without requiring complex null-checks throughout the rotation logic.