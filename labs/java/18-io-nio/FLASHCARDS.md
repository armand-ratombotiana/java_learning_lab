# Flashcards — I/O & NIO

**Q:** What is `InputStream`?  
**A:** Abstract class for reading byte streams.

**Q:** What is a `Reader`?  
**A:** Abstract class for reading character streams.

**Q:** What is the purpose of `BufferedReader`?  
**A:** Buffers input and provides `readLine()`.

**Q:** What is `try-with-resources`?  
**A:** Auto-closes resources implementing `AutoCloseable`.

**Q:** What is `Path` vs `File`?  
**A:** `Path` is NIO.2 — immutable, supports symbolic links, better API.

**Q:** What is a `ByteBuffer`?  
**A:** A buffer for reading/writing bytes in NIO channels.

**Q:** What is `FileChannel`?  
**A:** A channel for reading/writing files with zero-copy support.

**Q:** What is a `Selector`?  
**A:** Multiplexer for non-blocking channels.

**Q:** What is `Files.walk()`?  
**A:** Recursive directory traversal returning `Stream<Path>`.

**Q:** What is a `MappedByteBuffer`?  
**A:** A direct byte buffer backed by a memory-mapped file.
