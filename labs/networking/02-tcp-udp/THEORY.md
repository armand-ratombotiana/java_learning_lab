# TCP/UDP - Theory

## TCP (Transmission Control Protocol)
- Connection-oriented, reliable, ordered delivery
- Flow control via sliding window
- Congestion control (slow start, congestion avoidance, fast retransmit)
- Three-way handshake: SYN -> SYN-ACK -> ACK
- Connection termination: FIN -> ACK -> FIN -> ACK

## UDP (User Datagram Protocol)
- Connectionless, unreliable, no ordering guarantees
- No flow control or congestion control
- Lower latency, lower overhead
- Use cases: DNS, VoIP, video streaming, gaming

## Java TCP Socket Example
```java
public class TcpEchoServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("TCP Echo Server on port 5000");
        while (true) {
            try (Socket socket = serverSocket.accept()) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(
                    socket.getOutputStream(), true);
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.println("Echo: " + line);
                }
            }
        }
    }
}
```

## Java UDP Example
```java
public class UdpEchoServer {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(5001);
        System.out.println("UDP Echo Server on port 5001");
        byte[] buf = new byte[1024];
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            DatagramPacket response = new DatagramPacket(
                packet.getData(), packet.getLength(),
                packet.getAddress(), packet.getPort());
            socket.send(response);
        }
    }
}
```

## NIO Non-blocking TCP
```java
public class NioTcpServer {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.bind(new InetSocketAddress(5002));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select();
            for (SelectionKey key : selector.selectedKeys()) {
                if (key.isAcceptable()) {
                    SocketChannel client = channel.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buf = ByteBuffer.allocate(256);
                    client.read(buf);
                    buf.flip();
                    client.write(buf);
                    buf.clear();
                }
            }
            selector.selectedKeys().clear();
        }
    }
}
```
