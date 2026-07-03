# Quizzes: NIO Deep Dive

Test your knowledge of Channels, Buffers, and Selectors.

## Quiz 1: Traditional I/O vs. NIO

**Q1: What is the primary scalability problem with traditional `java.io` (e.g., `InputStream`) when building a high-traffic web server?**
- A) It can only read text, not binary data.
- B) The `read()` method blocks the thread until data is available. To handle 10,000 concurrent connections, you need 10,000 threads, which exhausts memory and causes massive context-switching overhead.
- C) It does not support encryption.
- D) It is limited to 1024 bytes per read.
*Answer: B*

**Q2: Which of the following is a characteristic of Java NIO Channels?**
- A) They are strictly unidirectional (read-only or write-only).
- B) They operate directly on `String` objects.
- C) They are bi-directional (you can read and write to the same channel) and can be configured in non-blocking mode.
- D) They automatically parse JSON.
*Answer: C*

## Quiz 2: Buffer Mechanics

**Q1: You write 50 bytes into a newly allocated `ByteBuffer` with a capacity of 100. You now want to read those 50 bytes out of the buffer. What method MUST you call first?**
- A) `buffer.clear()`
- B) `buffer.rewind()`
- C) `buffer.flip()`
- D) `buffer.read()`
*Answer: C (This sets the limit to the current position, and resets the position to 0).*

**Q2: What is the difference between `ByteBuffer.allocate()` and `ByteBuffer.allocateDirect()`?**
- A) `allocateDirect()` is thread-safe.
- B) `allocateDirect()` allocates memory on the JVM heap, while `allocate()` uses the hard drive.
- C) `allocateDirect()` allocates memory off-heap (directly in OS memory), avoiding the overhead of copying data between the JVM and the OS during I/O operations, but making it harder for the Garbage Collector to manage.
- D) There is no difference in modern JVMs.
*Answer: C*

## Quiz 3: Selectors

**Q1: What role does a `Selector` play in Java NIO?**
- A) It selects which thread should run next.
- B) It allows a single thread to monitor multiple non-blocking channels for events (like "data ready to read" or "ready to accept connection"), avoiding the need for a thread-per-connection model.
- C) It selects the fastest network interface card.
- D) It filters out invalid data from a stream.
*Answer: B*

**Q2: When iterating over `selector.selectedKeys()`, why must you explicitly call `iterator.remove()` after processing a key?**
- A) To close the connection.
- B) To free up memory.
- C) Because the Selector does not automatically remove keys from the selected set. If you don't remove it, you will process the same event again on the next loop iteration.
- D) To notify the client that the data was received.
*Answer: C*