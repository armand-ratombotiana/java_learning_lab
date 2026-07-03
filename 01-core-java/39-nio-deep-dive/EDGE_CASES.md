# Edge Cases & Pitfalls: NIO Deep Dive

NIO is incredibly powerful but notoriously difficult to use correctly. The API is low-level and unforgiving, leading to data corruption, memory leaks, and CPU spikes if mishandled.

## 1. The Missing `flip()` Trap
*   **The Scenario**: You read data from a `SocketChannel` into a `ByteBuffer`. You then pass that buffer to a method that prints the data to the console.
*   **The Pitfall**: When you read data into the buffer, the `position` pointer moves forward. If you try to read from the buffer immediately, you will start reading from the current `position` (which is at the end of the newly written data) up to the `capacity`, returning nothing but empty bytes (zeros) or garbage data.
*   **Mitigation**: You MUST call `buffer.flip()` after writing to the buffer and before reading from it. This resets the `position` to 0 and sets the `limit` to the amount of data written. Similarly, you must call `buffer.clear()` or `buffer.compact()` before writing new data into it again.

## 2. Partial Reads and Writes
*   **The Scenario**: You are sending a 10KB JSON payload over a non-blocking `SocketChannel`. You call `channel.write(buffer)`.
*   **The Pitfall**: In non-blocking mode, `write()` is not guaranteed to write the entire buffer. The OS network buffer might only have 2KB of free space. The `write()` method will write 2KB, update the buffer's position, and return `2048`. If you assume the whole 10KB was written and discard the buffer, you have corrupted the data stream.
*   **Mitigation**: You must always call `write()` in a loop until `buffer.hasRemaining()` returns false.
    ```java
    while (buffer.hasRemaining()) {
        channel.write(buffer);
    }
    ```

## 3. The 100% CPU Spin (Selector Bug)
*   **The Scenario**: You have a multiplexing loop: `while(true) { selector.select(); processKeys(); }`.
*   **The Pitfall**: Due to bugs in certain operating systems (famously, older versions of Linux epoll), the `select()` call might wake up immediately and return 0, even though no channels have events and no timeout was reached. Because it doesn't block, the `while(true)` loop spins millions of times a second, pinning the CPU at 100%.
*   **Mitigation**: This is the infamous "epoll bug". Frameworks like Netty implement complex workarounds (e.g., counting the number of times `select()` returns 0 in a row; if it happens 512 times, they rebuild the entire Selector from scratch). If writing raw NIO, you must implement a similar threshold-based rebuild strategy.

## 4. Direct vs. Heap Buffers (Memory Leaks)
*   **The Scenario**: You use `ByteBuffer.allocateDirect(1024)` instead of `ByteBuffer.allocate(1024)` for maximum performance.
*   **The Pitfall**: A Direct Buffer allocates memory directly in the OS (off-heap), bypassing the JVM heap. This avoids the overhead of copying data from the JVM heap to OS memory during I/O. However, because this memory is off-heap, the Garbage Collector has a very hard time managing it. Direct buffers rely on the GC cleaning up a tiny proxy object on the heap, which then triggers a native memory free. If you allocate many direct buffers quickly, you will exhaust native RAM before the JVM runs a GC cycle, crashing the OS.
*   **Mitigation**: Direct buffers should be pooled and reused. Do not allocate them per-request.

## 5. Forgetting to Remove Selection Keys
*   **The Scenario**: `selector.select()` wakes up. You iterate through `selector.selectedKeys()`, process each key, and loop back to `select()`.
*   **The Pitfall**: The Selector does *not* automatically remove keys from the selected set once you process them. If you don't remove them manually, the next time `select()` wakes up, it will process the old, already-handled keys again, leading to duplicate processing or exceptions.
*   **Mitigation**: You must explicitly call `iterator.remove()` after processing a key.
    ```java
    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
    while (iter.hasNext()) {
        SelectionKey key = iter.next();
        iter.remove(); // CRITICAL!
        process(key);
    }
    ```