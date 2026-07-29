# LeetCode 588: Design In-Memory File System

> **Difficulty**: Hard | **Category**: File Visitor & Watcher — File tree operations

## Problem

Design an in-memory file system that supports:
- `ls(path)`: List files/directories in a path
- `mkdir(path)`: Create a directory
- `addContentToFile(path, content)`: Append content to a file
- `readContentFromFile(path)`: Read file content

## Solution

Uses a tree of `Node` objects (files and directories) with a `FileVisitor`-style recursive traversal for operations like computing total size.

```java
import java.util.*;
import java.util.stream.*;

/**
 * LeetCode 588: Design In-Memory File System
 *
 * Demonstrates tree traversal patterns similar to Files.walkFileTree.
 */
public class InMemoryFileSystem {

    private static class Node {
        String name;
        boolean isDir;
        StringBuilder content = new StringBuilder();
        Map<String, Node> children = new TreeMap<>();  // sorted for ls

        Node(String name, boolean isDir) { this.name = name; this.isDir = isDir; }
    }

    private final Node root;

    public InMemoryFileSystem() {
        root = new Node("", true);
    }

    public List<String> ls(String path) {
        Node node = navigate(path);
        if (node.isDir) {
            return new ArrayList<>(node.children.keySet());
        }
        return List.of(node.name);
    }

    public void mkdir(String path) {
        String[] parts = path.split("/");
        Node cur = root;
        for (String p : parts) {
            if (p.isEmpty()) continue;
            cur.children.putIfAbsent(p, new Node(p, true));
            cur = cur.children.get(p);
        }
    }

    public void addContentToFile(String filePath, String content) {
        Node node = navigateOrCreate(filePath, false);
        node.content.append(content);
    }

    public String readContentFromFile(String filePath) {
        return navigate(filePath).content.toString();
    }

    private Node navigate(String path) {
        String[] parts = path.split("/");
        Node cur = root;
        for (String p : parts) {
            if (p.isEmpty()) continue;
            cur = cur.children.get(p);
            if (cur == null) throw new IllegalArgumentException("Path not found: " + path);
        }
        return cur;
    }

    private Node navigateOrCreate(String path, boolean isDir) {
        String[] parts = path.split("/");
        Node cur = root;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) continue;
            boolean last = (i == parts.length - 1);
            cur.children.putIfAbsent(parts[i], new Node(parts[i], last && isDir));
            cur = cur.children.get(parts[i]);
        }
        return cur;
    }

    /**
     * FileVisitor-style traversal: compute total size of all files.
     */
    public long totalSize() {
        return visit(root, 0);
    }

    private long visit(Node node, long size) {
        if (!node.isDir) return node.content.length();
        long sum = 0;
        for (Node child : node.children.values()) {
            sum += visit(child, size);
        }
        return sum;
    }

    // ─────────────────────
    // Verification
    // ─────────────────────
    public static void main(String[] args) {
        InMemoryFileSystem fs = new InMemoryFileSystem();

        fs.mkdir("/a/b/c");
        fs.addContentToFile("/a/b/c/d.txt", "hello");
        fs.addContentToFile("/a/b/c/e.txt", "world");

        assert fs.ls("/").equals(List.of("a"));
        assert fs.ls("/a/b/c").equals(List.of("d.txt", "e.txt"));
        assert fs.readContentFromFile("/a/b/c/d.txt").equals("hello");

        // total size should be 10 (5 + 5)
        assert fs.totalSize() == 10 : "Expected 10, got " + fs.totalSize();

        // Non-existent path
        try {
            fs.ls("/nonexistent");
            assert false : "Should have thrown";
        } catch (IllegalArgumentException e) {
            // expected
        }

        System.out.println("All tests passed.");
    }
}
```

## Key File Visitor Concepts

| Concept | Usage |
|---------|-------|
| Tree traversal | Recursive node visiting (like FileVisitor) |
| TreeMap | Sorted file listing (like ls order) |
| Path navigation | Split path, walk from root |
| Aggregation | Total size via depth-first traversal |
