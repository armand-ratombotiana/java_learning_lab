# Deep Dive: NIO Channels

## 1. Channel Overview

Channels are the gateway to I/O operations in NIO. Unlike streams, channels are bidirectional and support scatter/gather.

### FileChannel

```java
// Reading via FileChannel
try (RandomAccessFile file = new RandomAccessFile("data.bin", "r");
     FileChannel channel = file.getChannel()) {
    ByteBuffer buf = ByteBuffer.allocate(1024);
    int bytesRead = channel.read(buf);  // reads into buffer
    buf.flip();  // prepare for reading
    while (buf.hasRemaining()) {
        System.out.print((char) buf.get());
    }
}

// Writing via FileChannel
try (RandomAccessFile file = new RandomAccessFile("data.bin", "rw");
     FileChannel channel = file.getChannel()) {
    ByteBuffer buf = ByteBuffer.wrap("Hello NIO".getBytes());
    channel.write(buf);
}
```

## 2. Scatter / Gather

Scatter: read data into multiple buffers. Gather: write data from multiple buffers.

```java
// Scatter read
ByteBuffer header = ByteBuffer.allocate(128);
ByteBuffer body   = ByteBuffer.allocate(1024);
ByteBuffer[] buffers = {header, body};
channel.read(buffers);  // fills header first, then body

// Gather write
header.flip(); body.flip();
channel.write(buffers);  // writes header then body
```

## 3. Memory-Mapped Files

Maps a file region directly into virtual memory — zero-copy I/O:

```java
try (RandomAccessFile file = new RandomAccessFile("large.bin", "rw");
     FileChannel channel = file.getChannel()) {
    MappedByteBuffer mapped = channel.map(
        FileChannel.MapMode.READ_WRITE, 0, channel.size());
    
    // Read/write directly via the buffer
    int val = mapped.getInt(1024);   // read at offset 1024
    mapped.putInt(2048, 42);         // write at offset 2048
}
```

## 4. SocketChannel / ServerSocketChannel

```java
// Server
try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
    ssc.bind(new InetSocketAddress(8080));
    ssc.configureBlocking(false);  // non-blocking mode
    try (SocketChannel sc = ssc.accept()) {
        ByteBuffer buf = ByteBuffer.allocate(256);
        sc.read(buf);
    }
}

// Client
try (SocketChannel sc = SocketChannel.open()) {
    sc.connect(new InetSocketAddress("localhost", 8080));
    ByteBuffer buf = ByteBuffer.wrap("Hello".getBytes());
    sc.write(buf);
}
```
