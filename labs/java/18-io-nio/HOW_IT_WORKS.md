# How It Works — I/O & NIO

## Stream Decorator Pattern
```java
BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("f.txt")));
```
Each layer adds functionality: bytes → chars → buffering → line reading.

## FileChannel Zero-Copy
`FileChannel.transferTo()` instructs the OS to copy data directly from file to socket, bypassing Java heap entirely:
```
File → kernel buffer → NIC (no Java byte[] copy)
```

## ByteBuffer Lifecycle
```
allocate → put data → flip() → get data → clear()/compact()
                  │           │
            Write mode    Read mode
```

## Selector Loop (Reactor Pattern)
```java
while (true) {
    selector.select(); // block until event
    for (SelectionKey key : selector.selectedKeys()) {
        if (key.isReadable()) { /* read */ }
        if (key.isWritable()) { /* write */ }
    }
}
```

## Files.lines() Implementation
Returns a `Stream<String>` backed by a `BufferedReader`. The reader is closed when the stream is closed.
