# Debugging — I/O & NIO

## Strace / DTrace
```bash
strace -e read,write java Main  # Linux
```

## File Handle Limits
```bash
ulimit -n  # Check max open files
lsof -p <pid> | wc -l  # Count open handles
```

## Common Issues
- `FileNotFoundException` → check path separators and permissions
- `IOException: Too many open files` → resource leaks
- `java.nio.charset.MalformedInputException` → wrong encoding
- `NonReadableChannelException` → channel not opened with READ

## Debugging ByteBuffer
```java
ByteBuffer buf = ByteBuffer.allocate(8);
System.out.printf("pos=%d lim=%d cap=%d%n", buf.position(), buf.limit(), buf.capacity());
```

## Directory Listing Gone Wrong
```java
// This throws if directory doesn't exist
Files.list(path).forEach(System.out::println);
// Use Files.exists() first or catch NoSuchFileException
```

## NIO Selector Debug
Enable selector logging:
```bash
-Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.WindowsSelectorProvider
```
