# Deep Dive: NIO (New I/O)

## 1. The Limitations of Traditional I/O (`java.io`)
Traditional Java I/O is stream-oriented and **blocking**. 
When a thread calls `read()` on an `InputStream` (e.g., from a network socket), that thread is completely blocked by the OS until data arrives. If you are building a web server expecting 10,000 concurrent connections, you need 10,000 threads. As discussed in previous concurrency modules, 10,000 OS threads will consume massive amounts of RAM and cause severe context-switching overhead, eventually crashing the server.

## 2. The Solution: Java NIO (`java.nio`)
Introduced in Java 1.4, NIO (New I/O or Non-blocking I/O) provides an alternative approach. It is block-oriented (buffer-oriented) rather than stream-oriented, and it supports **non-blocking** operations and **multiplexing**.

The three core components of NIO are Channels, Buffers, and Selectors.

## 3. Channels and Buffers
Instead of reading from an `InputStream` directly into a byte array, you read from a `Channel` into a `Buffer`.

### Buffers
A `Buffer` is essentially a block of memory (an array) wrapped with an API that tracks the state of the data inside it. The most common is `ByteBuffer`.
A Buffer has three critical properties:
1.  **Capacity**: The maximum number of elements it can hold (fixed at creation).
2.  **Position**: The index of the next element to be read or written.
3.  **Limit**: The index of the first element that should *not* be read or written.

**The `flip()` Method**: This is the most crucial (and confusing) part of NIO buffers.
When you write data into a buffer, the `position` advances. When you are ready to read that data out of the buffer, you must call `flip()`. This sets the `limit` to the current `position`, and resets the `position` back to 0. Now the buffer is in "read mode".

### Channels
A `Channel` represents an open connection to an I/O entity (a file, a network socket). Unlike streams, Channels are bi-directional (you can read and write to the same channel).
*   `FileChannel`: For reading/writing files (cannot be non-blocking).
*   `SocketChannel` / `ServerSocketChannel`: For TCP network I/O (can be non-blocking).

## 4. Non-Blocking Mode
You can configure a network channel to be non-blocking:
```java
SocketChannel socketChannel = SocketChannel.open();
socketChannel.configureBlocking(false);
socketChannel.connect(new InetSocketAddress("example.com", 80));
```
In non-blocking mode, a call to `read()` will return immediately. If there is data, it returns the number of bytes read into the buffer. If there is no data, it returns `0` instead of blocking the thread.

## 5. Selectors (Multiplexing)
If `read()` returns 0, you don't want to write a `while(true)` loop that constantly checks `read()` (busy-waiting), as this burns 100% of the CPU.

This is where the **Selector** comes in. A Selector acts as a multiplexer. You can register multiple non-blocking Channels with a single Selector. You tell the Selector what "events" you are interested in for each channel (e.g., `OP_READ`, `OP_WRITE`, `OP_ACCEPT`).

A single thread can then call `selector.select()`. This call *does* block, but it blocks waiting for an event on *any* of the registered channels.

### The Multiplexing Loop
1.  Register 10,000 `SocketChannel`s with 1 `Selector`.
2.  Call `selector.select()`. The thread sleeps.
3.  When data arrives on Channel #402, the OS wakes up the thread.
4.  `select()` returns. You iterate over the `SelectionKey`s to find which channels are ready.
5.  You read the data from Channel #402, process it, and loop back to `select()`.

This architecture (the Reactor pattern) allows a **single thread** to handle tens of thousands of concurrent connections efficiently. This is the underlying technology that powers high-performance servers like Netty, Node.js, and Nginx.