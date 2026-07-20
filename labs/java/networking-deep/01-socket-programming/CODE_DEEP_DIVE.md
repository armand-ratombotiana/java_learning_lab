# Socket Programming -- Code Deep Dive
## Main Implementation
Package: com.javalab.01

### EchoServer using ServerSocket
ServerSocket serverSocket = new ServerSocket(port);
while (true) { Socket client = serverSocket.accept(); new Thread(() -> handleClient(client)).start(); }

### Thread Pool Server
ExecutorService pool = Executors.newFixedThreadPool(100);
while (true) { Socket client = serverSocket.accept(); pool.submit(() -> handleClient(client)); }

### NIO SocketChannel
SocketChannel channel = SocketChannel.open();
channel.configureBlocking(false);
channel.connect(new InetSocketAddress(host, port));
while (!channel.finishConnect()) { }

### Round-Trip Test Pattern
Each implementation includes a local loopback test that verifies the server
can accept connections, process messages, and return responses correctly.
