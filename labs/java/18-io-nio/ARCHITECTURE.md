# Architecture — I/O & NIO

## I/O Layer Cake
```
Application Code
      │
Stream API / NIO.2 (Files, Path)
      │
Buffered Streams (BufferedInputStream, BufferedReader)
      │
Unbuffered Streams (FileInputStream, FileReader)
      │
NIO Channels (FileChannel, SocketChannel)
      │
JVM Native I/O (FileDispatcher, IOUtil)
      │
OS System Calls (read, write, pread, pwrite, sendfile, mmap)
```

## When to Use What
| Use Case | API |
|----------|-----|
| Simple text file read | `Files.readAllLines()` |
| Binary file read | `Files.readAllBytes()` / `FileChannel` |
| Large file, sequential | `Files.lines()` / `BufferedReader` |
| Large file, random access | `SeekableByteChannel` |
| High-performance I/O | `FileChannel` + direct `ByteBuffer` |
| Network I/O | `SocketChannel` + `Selector` |

## Reactor Pattern (NIO Selectors)
```
Single thread
  └── Selector (epoll/kqueue/IOCP)
       ├── Channel 1 (read ready)
       ├── Channel 2 (write ready)
       └── Channel 3 (accept ready)
```
