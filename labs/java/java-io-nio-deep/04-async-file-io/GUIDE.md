# Deep Dive: Asynchronous I/O

## 1. AsynchronousFileChannel

Unlike `FileChannel`, `AsynchronousFileChannel` returns immediately and notifies via `Future` or `CompletionHandler`.

### Future-based API

```java
AsynchronousFileChannel channel = AsynchronousFileChannel.open(
    Path.of("data.bin"), StandardOpenOption.READ);

ByteBuffer buf = ByteBuffer.allocate(1024);
Future<Integer> result = channel.read(buf, 0);  // non-blocking!

while (!result.isDone()) {
    // do other work while I/O completes
}

int bytesRead = result.get();  // blocks only if not done yet
```

### CompletionHandler callback

```java
channel.read(buf, 0, buf, new CompletionHandler<>() {
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] data = new byte[attachment.remaining()];
        attachment.get(data);
        System.out.println("Read " + result + " bytes: " + new String(data));
    }
    
    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        System.err.println("Read failed: " + exc.getMessage());
    }
});
```

## 2. AsynchronousSocketChannel

```java
// Client with Future
AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
Future<Void> connected = client.connect(new InetSocketAddress("localhost", 8080));
connected.get();  // wait for connection

ByteBuffer buf = ByteBuffer.wrap("Hello".getBytes());
Future<Integer> written = client.write(buf);
written.get();

// Server with CompletionHandler
AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
server.bind(new InetSocketAddress(8080));
server.accept(null, new CompletionHandler<>() {
    @Override
    public void completed(AsynchronousSocketChannel client, Object attachment) {
        server.accept(null, this);  // accept next connection
        ByteBuffer buf = ByteBuffer.allocate(256);
        client.read(buf, buf, new ReadHandler(client));
    }
    
    @Override
    public void failed(Throwable exc, Object attachment) {
        System.err.println("Accept failed: " + exc.getMessage());
    }
});
```

## 3. Thread Pool Configuration

`AsynchronousChannelGroup` controls the thread pool:

```java
ExecutorService pool = Executors.newFixedThreadPool(4);
AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(pool);
AsynchronousFileChannel channel = AsynchronousFileChannel.open(
    Path.of("data.bin"), Set.of(StandardOpenOption.READ), group);
```
