# NIO Selectors -- Code Deep Dive
## Main Implementation
Package: com.javalab.02

### Selector-based Event Loop
Selector selector = Selector.open();
ServerSocketChannel ssc = ServerSocketChannel.open();
ssc.configureBlocking(false);
ssc.register(selector, SelectionKey.OP_ACCEPT);
while (true) { selector.select();
  for (SelectionKey key : selector.selectedKeys()) {
    if (key.isAcceptable()) { accept connection }
    if (key.isReadable()) { read data }
    if (key.isWritable()) { write data } } }

### Reactor Pattern
Reactor: event loop using Selector
Acceptor: handles new connections
Handler: processes read/write events

### Round-Trip Test Pattern
Each implementation includes a local loopback test that verifies the server
can accept connections, process messages, and return responses correctly.
