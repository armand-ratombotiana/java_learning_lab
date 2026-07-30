# Serialize and Deserialize Binary Tree (LeetCode 297)

**Problem:** Design an algorithm to serialize and deserialize a binary tree. There is no restriction on how your serialization/deserialization algorithm should work. You just need to ensure that a binary tree can be serialized to a string and this string can be deserialized to the original tree structure.

**Serialization** is the process of converting a data structure or object into a sequence of bits so that it can be stored in a file or memory buffer, or transmitted across a network connection link to be reconstructed later.

## Java Solution (BFS Level-Order)

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * Serialization and deserialization of a binary tree using BFS (level-order).
 *
 * <p>Uses a level-order traversal with markers for null nodes. The serialized
 * format is a comma-separated string. For example:
 * {@code "1,2,3,null,null,4,5,null,null,null,null"}</p>
 *
 * <h2>Complexity Analysis</h2>
 * <ul>
 *   <li><b>serialize(root)</b> — O(n) time, O(n) space</li>
 *   <li><b>deserialize(data)</b> — O(n) time, O(n) space</li>
 * </ul>
 *
 * <b>Space:</b> O(n) for both operations
 */
public class Codec {

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TreeNode other)) return false;
            return val == other.val
                && Objects.equals(left, other.left)
                && Objects.equals(right, other.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(val, left, right);
        }
    }

    /**
     * Serializes a binary tree to a comma-separated string using level-order
     * traversal (BFS).
     *
     * @param root the root of the binary tree
     * @return the serialized string
     */
    public String serialize(TreeNode root) {
        if (root == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node == null) {
                sb.append("null,");
            } else {
                sb.append(node.val).append(",");
                queue.offer(node.left);
                queue.offer(node.right);
            }
        }
        // Remove trailing comma
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Deserializes a comma-separated string back into a binary tree using BFS.
     *
     * @param data the serialized string
     * @return the root of the reconstructed binary tree
     */
    public TreeNode deserialize(String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        String[] values = data.split(",");
        TreeNode root = new TreeNode(Integer.parseInt(values[0]));
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        int i = 1;
        while (!queue.isEmpty() && i < values.length) {
            TreeNode parent = queue.poll();

            // Left child
            if (!values[i].equals("null")) {
                TreeNode left = new TreeNode(Integer.parseInt(values[i]));
                parent.left = left;
                queue.offer(left);
            }
            i++;

            // Right child
            if (i < values.length && !values[i].equals("null")) {
                TreeNode right = new TreeNode(Integer.parseInt(values[i]));
                parent.right = right;
                queue.offer(right);
            }
            i++;
        }
        return root;
    }
}
```

## Alternative DFS (Pre-order) Solution

```java
/**
 * Alternative serialization using DFS pre-order traversal.
 *
 * <p>Format: {@code "1,2,null,null,3,4,null,null,5,null,null"}</p>
 *
 * <h2>Complexity</h2>
 * <ul>
 *   <li>O(n) time, O(n) space for both operations</li>
 * </ul>
 */
class CodecDFS {

    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        if (sb.length() > 0) sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("null,");
            return;
        }
        sb.append(node.val).append(",");
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }

    public TreeNode deserialize(String data) {
        if (data == null || data.isEmpty()) return null;
        String[] values = data.split(",");
        int[] index = {0};
        return deserializeHelper(values, index);
    }

    private TreeNode deserializeHelper(String[] values, int[] index) {
        if (index[0] >= values.length || values[index[0]].equals("null")) {
            index[0]++;
            return null;
        }
        TreeNode node = new TreeNode(Integer.parseInt(values[index[0]]));
        index[0]++;
        node.left = deserializeHelper(values, index);
        node.right = deserializeHelper(values, index);
        return node;
    }
}
```

## Test Cases

```java
/**
 * Unit tests for Codec (BFS and DFS serialization).
 */
public class CodecTest {

    private static void testRoundTrip(Codec codec, TreeNode root) {
        String serialized = codec.serialize(root);
        TreeNode deserialized = codec.deserialize(serialized);
        boolean equal = treesEqual(root, deserialized);
        if (!equal) {
            System.out.println("FAIL: serialized=" + serialized);
        }
        assert equal : "round-trip should preserve the tree";
    }

    private static boolean treesEqual(TreeNode a, TreeNode b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.val == b.val
            && treesEqual(a.left, b.left)
            && treesEqual(a.right, b.right);
    }

    public static void main(String[] args) {
        Codec codec = new Codec();

        // --- Test 1: Null tree ---
        testRoundTrip(codec, null);

        // --- Test 2: Single node ---
        testRoundTrip(codec, new TreeNode(1));

        // --- Test 3: Left-skewed ---
        TreeNode leftSkew = new TreeNode(1,
            new TreeNode(2,
                new TreeNode(3), null), null);
        testRoundTrip(codec, leftSkew);

        // --- Test 4: Right-skewed ---
        TreeNode rightSkew = new TreeNode(1, null,
            new TreeNode(2, null,
                new TreeNode(3)));
        testRoundTrip(codec, rightSkew);

        // --- Test 5: Full binary tree ---
        TreeNode full = new TreeNode(1,
            new TreeNode(2, new TreeNode(4), new TreeNode(5)),
            new TreeNode(3, new TreeNode(6), new TreeNode(7)));
        testRoundTrip(codec, full);

        // --- Test 6: Incomplete tree ---
        TreeNode incomplete = new TreeNode(1,
            new TreeNode(2, null, new TreeNode(5)),
            new TreeNode(3));
        testRoundTrip(codec, incomplete);

        // --- Test 7: Large random tree ---
        TreeNode large = buildLargeTree(1000);
        String serialized = codec.serialize(large);
        TreeNode deserialized = codec.deserialize(serialized);
        assert treesEqual(large, deserialized) : "large tree round-trip failed";
        System.out.println("Large tree OK, string length: " + serialized.length());

        // --- Test 8: DFS Codec ---
        CodecDFS dfsCodec = new CodecDFS();
        testRoundTrip(codec, null);
        testRoundTrip(codec, full);
        testRoundTrip(codec, incomplete);

        // --- Test 9: Empty string deserializes to null ---
        assert codec.deserialize("") == null : "empty string -> null";
        assert codec.deserialize(null) == null : "null string -> null";

        System.out.println("All Codec tests passed!");
    }

    // Helper to build a large complete tree
    private static TreeNode buildLargeTree(int n) {
        if (n <= 0) return null;
        TreeNode[] nodes = new TreeNode[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new TreeNode(i);
        }
        for (int i = 1; i <= n; i++) {
            if (2 * i <= n) nodes[i].left = nodes[2 * i];
            if (2 * i + 1 <= n) nodes[i].right = nodes[2 * i + 1];
        }
        return nodes[1];
    }
}
```
