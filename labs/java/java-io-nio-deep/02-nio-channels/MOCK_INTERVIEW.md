# Mock Interview Transcript: NIO Channels

## Interviewer: Senior SWE, Amazon
## Candidate: Mid-level Java developer
## Time: 25 minutes
## Focus: FileChannel, SocketChannel, memory-mapped I/O

---

**Q1: How does FileChannel differ from FileInputStream/FileOutputStream?**

**Candidate**: `FileChannel` is bidirectional — it can read and write from the same channel (unlike streams which are unidirectional). It also supports position-based reads/writes (no need to track cursor), file locking, memory-mapped I/O, and zero-copy transfers via `transferTo/transferFrom`. The channel works with `ByteBuffer` instead of byte arrays.

**Interviewer**: What's the advantage of memory-mapped files?

**Candidate**: A memory-mapped file maps disk data directly into virtual memory. Reads and writes happen at memory speed — the OS pages data in/out transparently. Benefits: (1) no explicit system calls for data access, (2) pages are shared between processes (OS-level caching), (3) can access files larger than available RAM via paging. Drawbacks: (1) mapping cost on open, (2) harder to guarantee data is flushed, (3) limited by address space on 32-bit systems.

**Interviewer**: How does scatter/gather work and why is it useful?

**Candidate**: Scatter read reads data into multiple buffers — for example, headers in one buffer and payload in another. Gather write writes from multiple buffers sequentially. This avoids copying data between buffers: the network card can DMA directly from the buffers. This is especially useful for network protocols with fixed-size headers and variable-length payloads.

**Interviewer**: What's zero-copy I/O?

**Candidate**: Zero-copy eliminates copying data between kernel space and user space. With traditional I/O, data moves: disk → kernel buffer → user buffer → socket buffer → NIC. With `transferTo()`, it's: disk → kernel buffer → NIC (or kernel buffer directly → NIC via DMA). This reduces CPU usage and memory bandwidth. Kafka uses this for high-throughput message delivery.

**Interviewer**: How do you decide between direct and heap ByteBuffers?

**Candidate**: Direct buffers allocate memory outside the heap — they have higher allocation/deallocation cost but better I/O performance because the JVM can pass the memory address directly to native I/O operations without pinning or copying. Use direct buffers for long-lived, large I/O operations. Use heap buffers for small, short-lived operations where allocation speed matters.
