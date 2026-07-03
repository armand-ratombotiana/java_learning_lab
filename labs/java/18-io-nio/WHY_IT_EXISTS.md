# Why I/O & NIO Exist

## Abstracting OS Differences
Every OS handles files, sockets, and devices differently. Java I/O provides a uniform API.

## Character Encoding
`Reader`/`Writer` handle character encoding (UTF-8, UTF-16, etc.) transparently, unlike `InputStream`/`OutputStream`.

## Performance
- `BufferedInputStream` reduces syscalls
- `FileChannel` uses OS-native transfer mechanisms
- Memory-mapped files avoid user-space copying

## Non-Blocking I/O (NIO)
Traditional blocking I/O wastes threads. NIO channels with Selectors enable single-threaded handling of thousands of connections.

## NIO.2 (Java 7)
Original NIO had limitations. NIO.2 adds:
- `Path` (better than `File`)
- Symbolic link support
- File tree walking
- File change notifications (`WatchService`)
