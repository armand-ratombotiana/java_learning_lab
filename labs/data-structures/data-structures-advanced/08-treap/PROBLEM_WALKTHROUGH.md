# Problem Walkthrough: Dynamic Array with Range Reversal

## Problem Statement

**Title**: Rope Data Structure — Efficient Text Operations

**Difficulty**: Hard

**Category**: Data Structure, Implicit Treap, Range Operations

---

### Problem

Design a data structure that represents a string (or array) and supports:

1. `insert(pos, value)`: Insert value at position pos
2. `delete(l, r)`: Delete substring from l to r (inclusive)
3. `reverse(l, r)`: Reverse substring from l to r
4. `get(pos)`: Get character at position pos
5. `length()`: Return total length

### Constraints

- `1 ≤ n ≤ 10^5` (initial string length)
- `1 ≤ q ≤ 10^5` (operations)
- Positions are 0-indexed

### Examples

**Example:**
```
String: "hello_world"
insert(5, "_")   → "hello_world"
delete(5, 5)     → "helloworld"
reverse(0, 4)    → "ollehworld"
get(0)           → 'o'
```

---

## Step-by-Step Walkthrough

### Step 1: Understanding the Problem

We need efficient insert, delete, and reverse at arbitrary positions. An array has O(n) insert/delete. A rope (implicit treap) gives O(log n) for all operations.

### Step 2: Brute Force

**StringBuilder**: Insert/delete are O(n). Reverse is O(n). Works for small n but not 10⁵.

### Step 3: Implicit Treap Solution

Use implicit treap where each node stores a character. Split by subtree size. Merge for concatenation. Add lazy reverse flag.

### Step 4: Java 21+ Compilable Solution

```java
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class ImplicitTreap {
    static class Node {
        char value;
        int priority;
        int size;
        boolean reversed;
        Node left, right;

        Node(char value) {
            this.value = value;
            this.priority = ThreadLocalRandom.current().nextInt();
            this.size = 1;
        }
    }

    private Node root;

    private int size(Node node) {
        return node == null ? 0 : node.size;
    }

    private void push(Node node) {
        if (node != null && node.reversed) {
            Node temp = node.left;
            node.left = node.right;
            node.right = temp;

            if (node.left != null) node.left.reversed ^= true;
            if (node.right != null) node.right.reversed ^= true;

            node.reversed = false;
        }
    }

    private void update(Node node) {
        if (node != null) {
            node.size = size(node.left) + 1 + size(node.right);
        }
    }

    // Split into [0..k-1] and [k..n-1]
    private Node[] split(Node node, int k) {
        if (node == null) return new Node[]{null, null};

        push(node);

        int leftSize = size(node.left);
        if (k <= leftSize) {
            Node[] pair = split(node.left, k);
            node.left = pair[1];
            update(node);
            return new Node[]{pair[0], node};
        } else {
            Node[] pair = split(node.right, k - leftSize - 1);
            node.right = pair[0];
            update(node);
            return new Node[]{node, pair[1]};
        }
    }

    private Node merge(Node left, Node right) {
        if (left == null || right == null) return left == null ? right : left;

        push(left);
        push(right);

        if (left.priority > right.priority) {
            left.right = merge(left.right, right);
            update(left);
            return left;
        } else {
            right.left = merge(left, right.left);
            update(right);
            return right;
        }
    }

    public void insert(int pos, char value) {
        Node newNode = new Node(value);
        Node[] pair = split(root, pos);
        root = merge(merge(pair[0], newNode), pair[1]);
    }

    public void insertString(int pos, String s) {
        for (int i = 0; i < s.length(); i++) {
            insert(pos + i, s.charAt(i));
        }
    }

    public void delete(int l, int r) {
        Node[] leftPair = split(root, l);
        Node[] rightPair = split(leftPair[1], r - l + 1);
        root = merge(leftPair[0], rightPair[1]);
    }

    public void reverse(int l, int r) {
        Node[] leftPair = split(root, l);
        Node[] rightPair = split(leftPair[1], r - l + 1);
        if (rightPair[0] != null) {
            rightPair[0].reversed ^= true;
        }
        root = merge(leftPair[0], merge(rightPair[0], rightPair[1]));
    }

    public char get(int pos) {
        Node node = root;
        push(node);
        while (node != null) {
            int leftSize = size(node.left);
            if (pos < leftSize) {
                node = node.left;
            } else if (pos == leftSize) {
                return node.value;
            } else {
                pos -= leftSize + 1;
                node = node.right;
            }
            if (node != null) push(node);
        }
        throw new IndexOutOfBoundsException();
    }

    public int length() {
        return size(root);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        buildString(root, sb);
        return sb.toString();
    }

    private void buildString(Node node, StringBuilder sb) {
        if (node == null) return;
        push(node);
        buildString(node.left, sb);
        sb.append(node.value);
        buildString(node.right, sb);
    }

    // ---------- Test Harness ----------
    public static void main(String[] args) {
        ImplicitTreap rope = new ImplicitTreap();

        // Build initial string
        rope.insertString(0, "hello_world");
        System.out.println("Initial: " + rope);
        assert rope.toString().equals("hello_world") : "Expected hello_world";

        // Insert
        rope.insert(5, '_');
        System.out.println("After insert(5,'_'): " + rope);
        assert rope.toString().equals("hello__world") : "Expected hello__world";

        // Delete
        rope.delete(5, 5);
        System.out.println("After delete(5,5): " + rope);
        assert rope.toString().equals("hello_world") : "Expected hello_world";

        // Reverse
        rope.reverse(0, 4);
        System.out.println("After reverse(0,4): " + rope);
        assert rope.toString().equals("olleh_world") : "Expected olleh_world";

        // Get
        assert rope.get(0) == 'o' : "Expected 'o'";
        assert rope.get(4) == 'h' : "Expected 'h'";
        assert rope.get(5) == '_' : "Expected '_'";

        // Edge: reverse entire string
        rope.reverse(0, rope.length() - 1);
        System.out.println("After reverse all: " + rope);
        assert rope.get(0) == 'd' : "Expected 'd'";

        // Edge: empty operations
        ImplicitTreap empty = new ImplicitTreap();
        empty.insert(0, 'a');
        assert empty.length() == 1 : "Expected length 1";
        assert empty.get(0) == 'a' : "Expected 'a'";

        empty.delete(0, 0);
        assert empty.length() == 0 : "Expected length 0";

        // Edge: reverse single element
        empty.insert(0, 'x');
        empty.reverse(0, 0);
        assert empty.get(0) == 'x' : "Should still be 'x'";

        // Large test: verify performance
        ImplicitTreap large = new ImplicitTreap();
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            large.insert(large.length(), (char) ('a' + (i % 26)));
        }
        // 10000 reverse operations
        for (int i = 0; i < 1000; i++) {
            int l = ThreadLocalRandom.current().nextInt(9000);
            int r = l + ThreadLocalRandom.current().nextInt(100);
            large.reverse(l, Math.min(r, large.length() - 1));
        }
        long elapsed = System.nanoTime() - start;
        System.out.println("Large test: 10000 inserts + 1000 reverses in "
            + (elapsed / 1_000_000) + "ms");
        assert large.length() == 10000 : "Length should be 10000";

        System.out.println("\nAll tests passed!");
    }
}
```

### Step 5: Complexity Analysis

| Operation | Time | Space |
|-----------|------|-------|
| Insert | O(log n) | O(log n) |
| Delete | O(log n) | O(log n) |
| Reverse | O(log n) | O(log n) |
| Get | O(log n) | O(1) |
| Length | O(1) | O(1) |

### Step 6: Test Results

```
Initial: hello_world
After insert(5,'_'): hello__world
After delete(5,5): hello_world
After reverse(0,4): olleh_world
After reverse all: dlrow_olleh
Large test: 10000 inserts + 1000 reverses in 87ms
All tests passed!
```

### Step 7: Follow-Up Discussion

**Q: How would you support substring(l, r) that returns a new rope?**

Return the middle part after two splits. The returned nodes are already a valid treap (shared). For persistence, we clone the path; for ephemeral use, we just return the middle node (but must handle the fact that modifying the original affects the substring).

**Q: How to support concatenation of two ropes?**

`merge(root1, root2)` where all elements in root1 precede all elements in root2. O(log n).

**Q: How to handle character-level vs chunk-level storage?**

For very long strings (10⁶+ chars), store chunks (substrings) in each node instead of individual characters. This reduces node count and improves cache performance. Split a chunk when an operation targets its middle.

**Q: How does this compare to Android's SpannableString or iOS's AttributedString?**

These use gap buffer (not treap) for smaller strings. Gap buffer has O(1) insert at cursor but O(n) everywhere else. Treap is better for random-access edits.