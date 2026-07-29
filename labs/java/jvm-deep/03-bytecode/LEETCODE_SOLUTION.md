# Serialize and Deserialize N-ary Tree (Byte-Level Encoding)

> **Difficulty**: Hard | **Category**: JVM Deep (Bytecode / Serialization)

## Problem

Design an algorithm to serialize and deserialize an N-ary tree. The serialization must produce a compact byte-level representation that can be stored or transmitted.

## Solution

A recursive byte-level encoding scheme: each node's data is written as a 4-byte integer, followed by its child count, then each child recursively.

```java
import java.util.*;
import java.io.*;

/**
 * Serialize / Deserialize N-ary Tree with byte-level encoding.
 *
 * Format per node: [4 bytes: value] [4 bytes: numChildren] [children...]
 * Total: O(N) bytes where N = number of nodes.
 */
public class NAryTreeCodec {

    public static class Node {
        public int val;
        public List<Node> children;
        public Node() {}
        public Node(int val) { this.val = val; children = new ArrayList<>(); }
        public Node(int val, List<Node> children) { this.val = val; this.children = children; }
    }

    // ─── Serialize ───

    public byte[] serialize(Node root) {
        if (root == null) return new byte[0];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {
            writeNode(root, dos);
            dos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeNode(Node node, DataOutputStream dos) throws IOException {
        dos.writeInt(node.val);
        dos.writeInt(node.children.size());
        for (Node child : node.children) {
            writeNode(child, dos);
        }
    }

    // ─── Deserialize ───

    public Node deserialize(byte[] data) {
        if (data.length == 0) return null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             DataInputStream dis = new DataInputStream(bais)) {
            return readNode(dis);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Node readNode(DataInputStream dis) throws IOException {
        int val = dis.readInt();
        int childCount = dis.readInt();
        List<Node> children = new ArrayList<>(childCount);
        for (int i = 0; i < childCount; i++) {
            children.add(readNode(dis));
        }
        Node node = new Node(val);
        node.children = children;
        return node;
    }

    // ─────────────────────
    // Verification
    // ─────────────────────
    public static void main(String[] args) {
        var codec = new NAryTreeCodec();

        // Build tree:
        //       1
        //    /  |  \
        //   3   2   4
        //  / \
        // 5   6
        Node root = new Node(1, List.of(
            new Node(3, List.of(new Node(5), new Node(6))),
            new Node(2),
            new Node(4)
        ));

        byte[] serialized = codec.serialize(root);
        System.out.println("Serialized size: " + serialized.length + " bytes");

        Node deserialized = codec.deserialize(serialized);
        assert equals(root, deserialized) : "Trees should be equal";

        // Null test
        assert codec.deserialize(codec.serialize(null)) == null;

        // Single node
        Node single = new Node(42);
        Node back = codec.deserialize(codec.serialize(single));
        assert back.val == 42 && back.children.isEmpty();

        System.out.println("All tests passed.");
    }

    static boolean equals(Node a, Node b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.val != b.val) return false;
        if (a.children.size() != b.children.size()) return false;
        for (int i = 0; i < a.children.size(); i++) {
            if (!equals(a.children.get(i), b.children.get(i))) return false;
        }
        return true;
    }
}
```

## Complexity

| Operation       | Time    | Space   |
|-----------------|---------|---------|
| serialize       | O(N)    | O(N)    |
| deserialize     | O(N)    | O(N)    |

**Storage**: 8 bytes per node (4 for value + 4 for child count) + children data.

## Key Insights

1. **Byte-level encoding**: Using `DataOutputStream`/`DataInputStream` for compact binary serialization.
2. **Recursive structure**: The recursive nature mirrors the tree's structure — deserialization is a pre-order walk.
3. **No metadata overhead**: Unlike JSON/XML, binary encoding has minimal framing overhead.
4. **Extensibility**: The format can be extended with type markers, strings, or variable-length encoding (e.g., for large trees or text values).
