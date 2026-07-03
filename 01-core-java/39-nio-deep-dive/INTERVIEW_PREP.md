# Interview Preparation: NIO Deep Dive

This document covers advanced questions related to Java NIO, non-blocking I/O architectures, Buffer mechanics, and the epoll bug.

## Q1: Explain the fundamental architectural difference between traditional `java.io` and `java.nio`.
**Answer:**
*   **`java.io`** is stream-oriented and **blocking**. If a thread calls `read()` on a socket, it blocks until data is available. To handle $N$ concurrent clients, you need $N$ threads. This model does not scale well to thousands of connections due to thread memory overhead and context-switching.
*   **`java.nio`** is buffer-oriented and **non-blocking**. Threads can ask a channel to read data; if no data is available, the method returns immediately with 0 bytes, freeing the thread to do other work. By using a `Selector` (multiplexer), a single thread can monitor thousands of connections for events, achieving massive scalability (the Reactor pattern).

## Q2: What exactly does the `flip()` method do on a `ByteBuffer`, and why is it necessary?
**Answer:**
A `ByteBuffer` tracks its state using three pointers: `capacity`, `limit`, and `position`.
When you write data into a buffer, the `position` advances. If you write 50 bytes, `position` is 50.
If you immediately try to read from the buffer, you will start reading from index 50, which contains garbage data or zeros.
`flip()` prepares the buffer for reading. It sets the `limit` to the current `position` (meaning "don't read past the data I just wrote"), and it resets the `position` to 0 (meaning "start reading from the beginning"). Without `flip()`, you cannot safely transition a buffer from write mode to read mode.

## Q3: What is a "Direct Buffer" (`ByteBuffer.allocateDirect()`), and what are its pros and cons?
**Answer:**
A Direct Buffer allocates memory off-heap, directly in the OS's native memory space, bypassing the JVM heap.
*   **Pros**: It eliminates the "double-copy" overhead during I/O. Normally, the OS reads data from a socket into native memory, and the JVM copies it into the heap. Direct buffers allow the OS to write directly into the buffer the JVM is using, maximizing I/O performance.
*   **Cons**: Allocation and deallocation are much more expensive than heap buffers. Furthermore, because the memory is off-heap, the JVM Garbage Collector struggles to manage it. If you allocate many direct buffers without pooling them, you can easily cause a native OS OutOfMemory crash.

## Q4: In non-blocking NIO, why must you call `channel.write(buffer)` inside a `while(buffer.hasRemaining())` loop?
**Answer:**
In non-blocking mode, a `write()` operation is not guaranteed to write the entire contents of the buffer. It will only write as much data as the underlying OS network socket buffer can currently accept. 
If your buffer has 10KB of data, but the OS buffer only has 2KB of free space, `write()` will write 2KB, update the buffer's position, and return 2048 immediately without blocking. If you do not loop, the remaining 8KB of data is silently lost.

## Q5: What is the "epoll bug" in Java NIO, and how do frameworks like Netty handle it?
**Answer:**
The epoll bug is a notorious issue in the Linux implementation of the NIO `Selector`.
Normally, `selector.select()` should block until an event occurs. However, due to the bug, `select()` can wake up immediately and return 0, even when no channels have events. Because it's in a `while(true)` loop, the thread will call `select()` again, which returns 0 again. The thread goes into an infinite spin-loop, pinning the CPU at 100% utilization and freezing the application.
**Framework Handling**: Frameworks like Netty implement a workaround. They count how many times `select()` returns 0 without any actual events occurring. If this count exceeds a threshold (e.g., 512 times in a row), Netty assumes the epoll bug has occurred. It creates a brand new `Selector`, migrates all registered channels from the corrupted selector to the new one, and destroys the corrupted one.