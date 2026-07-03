# Internals — I/O & NIO

## File I/O in the JVM
```java
FileInputStream.read()
  → native FileDispatcher.read0()
    → OS read() syscall (Linux: pread, Windows: ReadFile)
```

## Buffered Stream Internals
`BufferedInputStream` wraps an 8 KB internal buffer:
```java
public synchronized int read() throws IOException {
    if (pos >= count) fill(); // fill buffer from underlying stream
    return buf[pos++] & 0xff;
}
```

## Direct vs Non-Direct ByteBuffer
- **Non-direct (heap):** byte[] on heap, copied to native memory for I/O
- **Direct:** native memory outside GC heap, zero-copy for I/O

## FileChannel Impl
```java
FileChannelImpl.read(ByteBuffer[] dsts, ...)
  → IOUtil.read() → NativeDispatcher.pread()
```

## Windows vs Linux
- Linux: `epoll` for selectors, `sendfile` for zero-copy
- Windows: `IOCP` for selectors, `TransmitFile` for zero-copy
- Mac: `kqueue` for selectors

## Memory-Mapped File
`FileChannel.map()` calls `mmap()` on POSIX, `CreateFileMapping` + `MapViewOfFile` on Windows.
