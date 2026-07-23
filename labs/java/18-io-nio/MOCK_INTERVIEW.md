# Mock Interview Transcript: I/O & NIO

## Interviewer: Staff Engineer, Google
## Candidate: Mid-level Java developer
## Time: 40 minutes
## Focus: NIO, selectors, channels, buffers, memory-mapped files

---

**Q1: Compare Java IO (InputStream/OutputStream) with NIO (Channels/Buffers).**

**Candidate**: IO is stream-oriented (one byte at a time or with buffers), blocking. NIO is buffer-oriented (read/write to Buffer objects), supports non-blocking and multiplexed I/O via Selectors. NIO also supports memory-mapped files (MappedByteBuffer), scatter/gather, and file locking.

**Interviewer**: Explain how a Selector works.

**Candidate**: A Selector can monitor multiple Channels for events (connect, accept, read, write). You register channels with the Selector, specifying interest set. `select()` blocks until one or more channels are ready. Then you process the selected keys. The underlying OS uses `epoll` (Linux), `kqueue` (macOS), or `select` (Windows).

**Interviewer**: Write a non-blocking echo server using NIO.

**Candidate**:
```java
class EchoServer {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        
        while (true) {
            selector.select();  // Block until event
            for (var key : selector.selectedKeys()) {
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    sc.read(buf);
                    buf.flip();
                    sc.write(buf);
                    buf.clear();
                }
                selector.selectedKeys().remove(key);
            }
        }
    }
}
```

**Interviewer**: What's the problem with `select()` in terms of scalability?

**Candidate**: `select()` is O(n) — it scans all file descriptors. `epoll` (Linux) is O(1) for the registration and provides edge-triggered notifications. Java NIO uses the best available OS mechanism, but the API is the same. For very high connections (100K+), Netty's epoll transport provides better performance via JNI.

**Interviewer**: How does `MappedByteBuffer` work?

**Candidate**: `MappedByteBuffer` memory-maps a file into virtual memory. The OS handles paging: reads and writes go directly to/from memory pages, which are flushed to disk lazily. This is zero-copy — no buffer allocation or explicit read/write needed. It's ideal for large files that need random access.

**Interviewer**: What about performance and safety of MappedByteBuffer?

**Candidate**: Performance is excellent — direct OS paging, no syscall per access. However: (1) Can't unmap explicitly until GC (the `Cleaner` handles it). (2) If the file is truncated while mapped, you get SIGBUS. (3) Memory usage is outside the heap (native memory). (4) For large files, mapping can exhaust virtual address space (especially on 32-bit).

**Interviewer**: Compare `DirectByteBuffer` and `HeapByteBuffer`.

**Candidate**: `HeapByteBuffer` allocates on the Java heap. `DirectByteBuffer` allocates native (off-heap) memory. Direct is: (1) Faster for I/O operations (no copying between Java heap and native buffers). (2) More expensive to allocate (system call). (3) Not subject to GC pauses (but counted in resident memory). (4) Used by NIO channels for zero-copy I/O. Use direct for long-lived, large buffers used in I/O. Use heap for small, short-lived buffers.

**Interviewer**: Final question: Compare `FileChannel.transferTo()` with traditional read-write.

**Candidate**: `transferTo()` (and `transferFrom()`) use zero-copy at the OS level. On Linux, it uses `sendfile()` — data goes from disk to socket directly, bypassing user-space entirely. Traditional read-write involves: disk → kernel buffer → user buffer (heap) → kernel buffer → socket. Zero-copy eliminates two copies and context switches.

---

## Feedback

**Strengths**:
- Correct NIO echo server implementation
- Understands selector and OS-level multiplexing
- Deep knowledge of MappedByteBuffer trade-offs
- Clear on zero-copy and transferTo benefits

**Areas for Improvement**:
- Should mention AsynchronousFileChannel (NIO.2) for async I/O
- Could discuss memory-mapped file limitations on different OS

**Score**: 4/5 — Solid NIO knowledge
