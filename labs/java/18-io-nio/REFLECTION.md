# Reflection — I/O & NIO

## Why This Lab Matters
I/O is the foundation of nearly every Java application — from reading config files to serving web responses.

## What I Learned
- Byte vs character streams and encoding
- Buffering and its impact on performance
- NIO.2 Path/Files API for modern file I/O
- Channels, ByteBuffers, zero-copy, and memory-mapping

## Questions I Still Have
- When should I use a FileChannel vs MappedByteBuffer for large files?
- How does the JDK implement async I/O on Windows (IOCP)?

## Personal Application
- Default to `Files.readAllLines()` and `Files.write()` for simple file ops
- Use `FileChannel.transferTo()` for high-performance file copying
- Use `Files.lines()` with Stream API for log processing

## Key Takeaways
1. Always specify character encoding explicitly
2. Use try-with-resources for all I/O
3. Buffered streams are essential for performance
4. NIO.2 is strictly better than legacy java.io.File
5. Zero-copy through FileChannel is a game-changer for throughput
