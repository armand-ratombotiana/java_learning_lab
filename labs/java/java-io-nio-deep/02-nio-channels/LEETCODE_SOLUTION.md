# LeetCode 146: LRU Cache (FileChannel-backed)

> **Difficulty**: Medium | **Category**: NIO Channels — Memory-mapped persistence

## Problem

Extend the LRU Cache to support persistence: serialize the cache state to a file using NIO FileChannel.

## Solution

Uses `FileChannel` for writing the cache state, demonstrating channel-based file I/O with `ByteBuffer`.

```java
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

/**
 * LRU Cache with FileChannel-based persistence.
 *
 * Demonstrates: FileChannel, ByteBuffer, scatter/gather writes.
 */
public class PersistentLRUCache {

    private final int capacity;
    private final LinkedHashMap<Integer, Integer> cache;

    public PersistentLRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > PersistentLRUCache.this.capacity;
            }
        };
    }

    public int get(int key) {
        return cache.getOrDefault(key, -1);
    }

    public void put(int key, int value) {
        cache.put(key, value);
    }

    /**
     * Save cache to file using FileChannel.
     * Format: [4 bytes: count] [entries: key(int) + value(int)]...
     */
    public void save(String path) throws IOException {
        try (FileChannel channel = FileChannel.open(
                Path.of(path), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {

            // Write header: number of entries
            ByteBuffer header = ByteBuffer.allocate(4);
            header.putInt(cache.size());
            header.flip();
            channel.write(header);

            // Write each entry
            for (var entry : cache.entrySet()) {
                ByteBuffer buf = ByteBuffer.allocate(8);
                buf.putInt(entry.getKey());
                buf.putInt(entry.getValue());
                buf.flip();
                channel.write(buf);
            }
        }
    }

    /**
     * Load cache from file using FileChannel.
     */
    public void load(String path) throws IOException {
        cache.clear();
        try (FileChannel channel = FileChannel.open(Path.of(path), StandardOpenOption.READ)) {

            ByteBuffer header = ByteBuffer.allocate(4);
            channel.read(header);
            header.flip();
            int count = header.getInt();

            for (int i = 0; i < count; i++) {
                ByteBuffer entry = ByteBuffer.allocate(8);
                channel.read(entry);
                entry.flip();
                int key = entry.getInt();
                int value = entry.getInt();
                cache.put(key, value);
            }
        }
    }

    // ─────────────────────
    // Verification
    // ─────────────────────
    public static void main(String[] args) throws IOException {
        Path tmp = Files.createTempFile("lru-cache", ".bin");
        tmp.toFile().deleteOnExit();

        PersistentLRUCache cache = new PersistentLRUCache(3);
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);
        cache.save(tmp.toString());

        PersistentLRUCache restored = new PersistentLRUCache(3);
        restored.load(tmp.toString());
        assert restored.get(1) == 100;
        assert restored.get(2) == 200;
        assert restored.get(3) == 300;
        assert restored.get(4) == -1;

        System.out.println("All tests passed.");
    }
}
```

## Key Channel Concepts

| Concept | Usage |
|---------|-------|
| FileChannel | Read/write file via ByteBuffer |
| ByteBuffer | Buffer allocation and flipping |
| StandardOpenOption | CREATE, WRITE, TRUNCATE_EXISTING, READ |
