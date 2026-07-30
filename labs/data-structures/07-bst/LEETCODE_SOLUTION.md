# Design BST with Iterator (In-order Traversal)

**Problem:** Implement a Binary Search Tree (BST) with an iterator that performs **in-order traversal** in **O(1)** average time per `next()` call and **O(h)** space, where h is the height of the tree.

Implement the `BSTIterator` class:

- `BSTIterator(TreeNode root)` — Initializes the iterator. The next pointer points to the smallest element.
- `int next()` — Moves the pointer to the next element and returns it.
- `boolean hasNext()` — Returns true if there is a next element.

## Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Binary Search Tree implementation with an in-order iterator using an
 * explicit stack (simulating recursion).
 *
 * <p>The iterator lazily traverses the tree in O(1) amortized time per
 * {@code next()} call, using O(h) stack space.</p>
 *
 * <h2>Complexity Analysis</h2>
 * <ul>
 *   <li><b>hasNext()</b> — O(1)</li>
 *   <li><b>next()</b> — O(1) amortized, O(h) worst-case (when traversing down left spine)</li>
 *   <li><b>insert(val)</b> — O(h)</li>
 *   <li><b>search(val)</b> — O(h)</li>
 *   <li><b>delete(val)</b> — O(h)</li>
 * </ul>
 *
 * <b>Space:</b> O(n) for the tree, O(h) for the iterator stack
 */
public class BST {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    private TreeNode root;

    /** Constructs an empty BST. */
    public BST() {
        this.root = null;
    }

    /**
     * Inserts a value into the BST. Duplicates are not allowed.
     *
     * @param val the value to insert
     * @return true if inserted, false if the value already exists
     */
    public boolean insert(int val) {
        if (root == null) {
            root = new TreeNode(val);
            return true;
        }
        return insertRec(root, val);
    }

    private boolean insertRec(TreeNode node, int val) {
        if (val < node.val) {
            if (node.left == null) {
                node.left = new TreeNode(val);
                return true;
            }
            return insertRec(node.left, val);
        } else if (val > node.val) {
            if (node.right == null) {
                node.right = new TreeNode(val);
                return true;
            }
            return insertRec(node.right, val);
        }
        return false; // duplicate
    }

    /**
     * Searches for a value in the BST.
     *
     * @param val the value to search for
     * @return true if the value exists
     */
    public boolean search(int val) {
        return searchRec(root, val);
    }

    private boolean searchRec(TreeNode node, int val) {
        if (node == null) return false;
        if (val == node.val) return true;
        return val < node.val
            ? searchRec(node.left, val)
            : searchRec(node.right, val);
    }

    /**
     * Deletes a value from the BST.
     *
     * @param val the value to delete
     * @return true if the value was found and deleted
     */
    public boolean delete(int val) {
        if (!search(val)) return false;
        root = deleteRec(root, val);
        return true;
    }

    private TreeNode deleteRec(TreeNode node, int val) {
        if (node == null) return null;
        if (val < node.val) {
            node.left = deleteRec(node.left, val);
        } else if (val > node.val) {
            node.right = deleteRec(node.right, val);
        } else {
            // Node to delete found
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            // Two children: find inorder successor
            TreeNode successor = findMin(node.right);
            node.val = successor.val;
            node.right = deleteRec(node.right, successor.val);
        }
        return node;
    }

    private TreeNode findMin(TreeNode node) {
        while (node.left != null) node = node.left;
        return node;
    }

    /**
     * Returns an in-order iterator for this BST.
     *
     * @return iterator over values in sorted order
     */
    public Iterator<Integer> iterator() {
        return new BSTIterator(root);
    }

    /**
     * Returns the root node (for testing).
     */
    TreeNode getRoot() {
        return root;
    }
}

/**
 * BST in-order iterator using an explicit stack (simulated recursive call stack).
 *
 * <p>On each {@code next()} call, the left spine is pushed onto the stack as
 * needed, giving O(1) amortized time.</p>
 */
class BSTIterator implements Iterator<Integer> {

    private final Deque<TreeNode> stack;

    /**
     * Constructs an iterator starting at the smallest element.
     *
     * @param root the root of the BST
     */
    BSTIterator(TreeNode root) {
        stack = new ArrayDeque<>();
        pushLeftSpine(root);
    }

    /**
     * Returns true if there are more elements in the in-order traversal.
     *
     * @return true if a next element exists
     */
    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    /**
     * Returns the next element in the in-order traversal.
     *
     * @return the next smallest value
     * @throws NoSuchElementException if the traversal is complete
     */
    @Override
    public Integer next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements");
        }
        TreeNode node = stack.pop();
        int result = node.val;
        // The right child's left spine is the next successor
        pushLeftSpine(node.right);
        return result;
    }

    private void pushLeftSpine(TreeNode node) {
        while (node != null) {
            stack.push(node);
            node = node.left;
        }
    }
}
```

## Test Cases

```java
/**
 * Unit tests for BST and BSTIterator.
 */
public class BSTTest {

    public static void main(String[] args) {
        // --- Test 1: Insert and search ---
        BST bst = new BST();
        assert bst.insert(5) : "insert 5";
        assert bst.insert(3) : "insert 3";
        assert bst.insert(7) : "insert 7";
        assert bst.insert(1) : "insert 1";
        assert bst.insert(9) : "insert 9";
        assert !bst.insert(5) : "duplicate insert should fail";
        assert bst.search(5) : "5 should exist";
        assert bst.search(1) : "1 should exist";
        assert !bst.search(99) : "99 should not exist";

        // --- Test 2: In-order iterator ---
        java.util.Iterator<Integer> it = bst.iterator();
        int prev = Integer.MIN_VALUE;
        while (it.hasNext()) {
            int val = it.next();
            assert val > prev : "in-order should be ascending";
            prev = val;
        }
        // Verify expected order: 1, 3, 5, 7, 9
        assert prev == 9 : "last element should be 9";

        // --- Test 3: Iterator on empty tree ---
        BST empty = new BST();
        Iterator<Integer> emptyIt = empty.iterator();
        assert !emptyIt.hasNext() : "empty iterator should have no next";

        // --- Test 4: Delete leaf ---
        BST bst2 = new BST();
        bst2.insert(5); bst2.insert(3); bst2.insert(7);
        assert bst2.delete(3) : "delete leaf 3";
        assert !bst2.search(3) : "3 should be gone";
        int[] expected = {5, 7};
        checkOrder(bst2, expected);

        // --- Test 5: Delete node with one child ---
        BST bst3 = new BST();
        bst3.insert(5); bst3.insert(3); bst3.insert(4);
        assert bst3.delete(3) : "delete node with right child";
        checkOrder(bst3, new int[]{4, 5});

        // --- Test 6: Delete node with two children ---
        BST bst4 = new BST();
        bst4.insert(5); bst4.insert(3); bst4.insert(7);
        bst4.insert(2); bst4.insert(4); bst4.insert(6); bst4.insert(8);
        assert bst4.delete(5) : "delete root with two children";
        // After deletion, in-order should be 2,3,4,6,7,8
        checkOrder(bst4, new int[]{2, 3, 4, 6, 7, 8});

        // --- Test 7: Delete root (only node) ---
        BST bst5 = new BST();
        bst5.insert(42);
        assert bst5.delete(42) : "delete root";
        assert !bst5.search(42) : "root should be gone";
        assert !bst5.iterator().hasNext() : "tree should be empty";

        // --- Test 8: Iterator after operations ---
        BST bst6 = new BST();
        for (int v : new int[]{8, 3, 10, 1, 6, 14, 4, 7, 13}) {
            bst6.insert(v);
        }
        Object[] vals = {};
        java.util.List<Integer> list = new java.util.ArrayList<>();
        bst6.iterator().forEachRemaining(list::add);
        assert list.size() == 9 : "should have 9 elements";
        assert list.get(0) == 1 : "smallest should be 1";
        assert list.get(8) == 14 : "largest should be 14";

        // --- Test 9: Delete non-existent ---
        assert !bst.delete(999) : "delete non-existent should return false";

        System.out.println("All BST tests passed!");
    }

    private static void checkOrder(BST bst, int[] expected) {
        java.util.List<Integer> list = new java.util.ArrayList<>();
        bst.iterator().forEachRemaining(list::add);
        assert list.size() == expected.length : "size mismatch";
        for (int i = 0; i < expected.length; i++) {
            assert list.get(i) == expected[i] : "mismatch at index " + i;
        }
    }
}
```
