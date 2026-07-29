# LeetCode 1188: Bounded Blocking Queue (Async I/O variant)

> **Difficulty**: Medium | **Category**: Async I/O — AsynchronousFileChannel

## Problem

Implement a bounded blocking queue that persists enqueued items to a file using `AsynchronousFileChannel`.

## Solution

Demonstrates `AsynchronousFileChannel` with a `CompletionHandler` for non-blocking file writes.

```java
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.concurrent.*;

/**
 * Persisted Bounded Blocking Queue using AsynchronousFileChannel.
 *
 * Enqueued items are appended to a file asynchronously.
 */
public class AsyncPersistedQueue {

    private final int capacity;
    private final Path filePath;
    private final AsynchronousFileChannel channel;
    private final BlockingQueue<Integer> buffer;
    private long writePosition = 0;

    public AsyncPersistedQueue(int capacity, Path filePath) throws IOException {
        this.capacity = capacity;
        this.filePath = filePath;
        this.buffer = new ArrayBlockingQueue<>(capacity);
        this.channel = AsynchronousFileChannel.open(
            filePath,
            StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
    }

    public void enqueue(int value) throws InterruptedException, IOException {
        buffer.put(value);  // blocks if full

        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(value);
        buf.flip();

        long position = writePosition;
        writePosition += 4;

        channel.write(buf, position, buf, new CompletionHandler<>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                // write completed
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.err.println("Async write failed: " + exc.getMessage());
            }
        });
    }

    public int dequeue() throws InterruptedException {
        return buffer.take();
    }

    public int size() {
        return buffer.size();
    }

    public void close() throws IOException {
        channel.close();
    }

    // ─────────────────────
    // Verification
    // ─────────────────────
    public static void main(String[] args) throws Exception {
        Path tmp = Files.createTempFile("async-queue", ".bin");
        tmp.toFile().deleteOnExit();

        AsyncPersistedQueue q = new AsyncPersistedQueue(5, tmp);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);

        assert q.dequeue() == 10;
        assert q.dequeue() == 20;
        assert q.dequeue() == 30;
        assert q.size() == 0;

        // Verify data was written to file
        try (FileChannel verify = FileChannel.open(tmp, StandardOpenOption.READ)) {
            ByteBuffer buf = ByteBuffer.allocate(12);
            verify.read(buf);
            buf.flip();
            assert buf.getInt() == 10;
            assert buf.getInt() == 20;
            assert buf.getInt() == 30;
        }

        q.close();
        System.out.println("All tests passed.");
    }
}
```

## Key Async I/O Concepts

| Concept | Usage |
|---------|-------|
| AsynchronousFileChannel | Non-blocking file writes |
| CompletionHandler | Callback for async operation |
| ByteBuffer | Buffer management for data |
| BlockingQueue | In-memory buffer with backpressure |
