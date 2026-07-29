package com.java.io.nio.lab03;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * NIO Selectors — Main Implementation
 *
 * Demonstrates: non-blocking I/O with Selector, reactor pattern,
 * socket channel multiplexing.
 */
public class MainImplementation {

    /**
     * Simple echo server using a single Selector thread (Reactor pattern).
     * Binds on localhost:0 (random available port).
     */
    public static class EchoReactor implements AutoCloseable {
        private final Selector selector;
        private final ServerSocketChannel serverChannel;
        private volatile boolean running = true;

        public EchoReactor() throws IOException {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress("localhost", 0));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        }

        public int getPort() {
            return serverChannel.socket().getLocalPort();
        }

        public void start() {
            Thread reactor = new Thread(() -> {
                while (running) {
                    try {
                        if (selector.select(100) == 0) continue;
                        Set<SelectionKey> keys = selector.selectedKeys();
                        Iterator<SelectionKey> it = keys.iterator();
                        while (it.hasNext()) {
                            SelectionKey key = it.next();
                            it.remove();
                            if (!key.isValid()) continue;
                            if (key.isAcceptable()) handleAccept(key);
                            else if (key.isReadable()) handleRead(key);
                        }
                    } catch (IOException e) {
                        if (running) e.printStackTrace();
                    }
                }
                try { selector.close(); } catch (IOException e) { /* ignore */ }
            }, "echo-reactor");
            reactor.setDaemon(true);
            reactor.start();
        }

        private void handleAccept(SelectionKey key) throws IOException {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
        }

        private void handleRead(SelectionKey key) throws IOException {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer buf = ByteBuffer.allocate(256);
            int bytesRead = sc.read(buf);
            if (bytesRead == -1) {
                sc.close();
                return;
            }
            buf.flip();
            sc.write(buf);  // echo back
        }

        @Override
        public void close() {
            running = false;
            try { serverChannel.close(); } catch (IOException e) { /* ignore */ }
        }
    }

    /**
     * Non-blocking channel operations: demonstrate connect/read/write.
     */
    public static class NonBlockingClient {
        public String sendAndReceive(String host, int port, String message) throws IOException {
            SocketChannel sc = SocketChannel.open();
            sc.configureBlocking(false);
            sc.connect(new InetSocketAddress(host, port));

            // Wait for connection
            while (!sc.finishConnect()) {
                Thread.yield();
            }

            // Send
            ByteBuffer writeBuf = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
            while (writeBuf.hasRemaining()) {
                sc.write(writeBuf);
            }

            // Read response
            ByteBuffer readBuf = ByteBuffer.allocate(256);
            int totalRead = 0;
            long deadline = System.currentTimeMillis() + 2000;
            while (System.currentTimeMillis() < deadline && totalRead < message.length()) {
                int n = sc.read(readBuf);
                if (n > 0) totalRead += n;
            }
            readBuf.flip();
            String response = StandardCharsets.UTF_8.decode(readBuf).toString();
            sc.close();
            return response.trim();
        }
    }

    public static void main(String[] args) throws Exception {
        // Start echo reactor
        EchoReactor reactor = new EchoReactor();
        reactor.start();
        int port = reactor.getPort();
        Thread.sleep(200);  // give time to bind

        // Test with non-blocking client
        NonBlockingClient client = new NonBlockingClient();
        String response = client.sendAndReceive("localhost", port, "Hello Selector!");
        assert response.equals("Hello Selector!") : "Echo mismatch: '" + response + "'";

        // Selector ops demonstration
        Selector sel = Selector.open();
        assert sel.isOpen();
        assert sel.keys().isEmpty();
        sel.close();

        reactor.close();
        System.out.println("All NIO Selectors tests passed.");
    }
}
