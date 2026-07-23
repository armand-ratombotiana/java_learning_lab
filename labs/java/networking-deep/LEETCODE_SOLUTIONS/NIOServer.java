package networking;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/**
 * Java NIO (Non-blocking I/O) Server using Selector.
 * 
 * NIO allows a single thread to handle multiple connections.
 * Uses: Channel, Buffer, Selector (multiplexed I/O).
 * 
 * Compared to blocking I/O:
 * - One thread manages thousands of connections
 * - No per-connection thread overhead
 * - More complex API
 * 
 * Time: O(1) per select/accept/read
 * Space: O(buffers per connection)
 */
public class NIOServer {

    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private static final int BUFFER_SIZE = 256;

    NIOServer(int port) throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("NIO Server listening on port " + port);
    }

    void start() throws IOException {
        var buffers = new HashMap<SelectionKey, ByteBuffer>();

        while (true) {
            selector.select(); // Block until event

            var keys = selector.selectedKeys();
            var iter = keys.iterator();

            while (iter.hasNext()) {
                var key = iter.next();
                iter.remove();

                if (!key.isValid()) continue;

                if (key.isAcceptable()) {
                    // Accept new connection
                    var client = serverChannel.accept();
                    client.configureBlocking(false);
                    var readKey = client.register(selector, SelectionKey.OP_READ);
                    buffers.put(readKey, ByteBuffer.allocate(BUFFER_SIZE));
                    System.out.println("Accepted: " + client.getRemoteAddress());
                }

                if (key.isReadable()) {
                    var client = (SocketChannel) key.channel();
                    var buffer = buffers.get(key);
                    buffer.clear();

                    int bytesRead = client.read(buffer);
                    if (bytesRead == -1) {
                        // Client disconnected
                        System.out.println("Disconnected: " + client.getRemoteAddress());
                        buffers.remove(key);
                        client.close();
                        key.cancel();
                        continue;
                    }

                    buffer.flip();
                    var response = ByteBuffer.wrap("Echo: ".getBytes());
                    client.write(response);
                    client.write(buffer);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8889;

        // Start server
        var serverThread = new Thread(() -> {
            try {
                new NIOServer(port).start();
            } catch (IOException e) { e.printStackTrace(); }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        Thread.sleep(500);

        // Test client
        try (var socket = new Socket("localhost", port);
             var out = new PrintWriter(socket.getOutputStream(), true);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("Hello NIO");
            String response = in.readLine();
            System.out.println("Response: " + response);
            assert response.contains("Hello NIO");
        }

        System.out.println("All NIOServer tests passed.");
    }
}