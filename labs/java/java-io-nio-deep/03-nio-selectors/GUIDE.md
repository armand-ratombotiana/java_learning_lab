# Deep Dive: NIO Selectors

## 1. Selector Overview

A `Selector` multiplexes multiple `SelectableChannel`s into a single thread. This enables handling thousands of connections with one thread — the foundation of high-performance networking.

### Reactor Pattern

```java
Selector selector = Selector.open();
ServerSocketChannel ssc = ServerSocketChannel.open();
ssc.configureBlocking(false);
ssc.bind(new InetSocketAddress(8080));
ssc.register(selector, SelectionKey.OP_ACCEPT);

while (true) {
    int readyChannels = selector.select();  // blocks until an event
    
    if (readyChannels == 0) continue;
    
    Set<SelectionKey> selectedKeys = selector.selectedKeys();
    Iterator<SelectionKey> it = selectedKeys.iterator();
    
    while (it.hasNext()) {
        SelectionKey key = it.next();
        it.remove();  // must remove after processing
        
        if (key.isAcceptable()) {
            // Accept new connection
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            // Read from client
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buf = ByteBuffer.allocate(256);
            client.read(buf);
            // Process data...
            key.interestOps(SelectionKey.OP_WRITE);
        } else if (key.isWritable()) {
            // Write to client
            SocketChannel client = (SocketChannel) key.channel();
            // Write response...
            key.interestOps(SelectionKey.OP_READ);
        }
    }
}
```

## 2. SelectionKey Operations

| Method | Description |
|--------|-------------|
| `isAcceptable()` | New connection can be accepted |
| `isConnectable()` | Connection completed |
| `isReadable()` | Data available to read |
| `isWritable()` | Channel ready to write |
| `interestOps()` | Set which operations to monitor |
| `attach(obj)` | Attach user data to key |

## 3. Non-blocking vs Blocking

| Aspect | Blocking I/O | Non-blocking NIO |
|--------|-------------|-------------------|
| Threads per connection | 1 thread per connection | 1 thread handles many |
| Scalability | Thread-limited (C10K problem) | Handles millions (C10M) |
| Complexity | Simple | Higher |
| Latency per request | Lower (dedicated thread) | Higher (shared thread) |
