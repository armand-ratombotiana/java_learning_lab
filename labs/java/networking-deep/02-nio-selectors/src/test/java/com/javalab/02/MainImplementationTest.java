package com.javalab.02;

import org.junit.jupiter.api.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {

    @Test
    @DisplayName("NIO echo server should respond to client")
    void testNioEcho() throws Exception {
        MainImplementation.NioEchoServer server = new MainImplementation.NioEchoServer(0);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            try { server.start(); } catch (Exception e) {}
        });
        Thread.sleep(300);
        try {
            SocketChannel client = SocketChannel.open();
            client.connect(new InetSocketAddress("localhost", server.getPort()));
            ByteBuffer buf = ByteBuffer.allocate(256);
            buf.put("Hello".getBytes());
            buf.flip();
            client.write(buf);
            buf.clear();
            client.read(buf);
            buf.flip();
            byte[] data = new byte[buf.remaining()];
            buf.get(data);
            String response = new String(data).trim();
            assertEquals("Hello", response);
            client.close();
        } finally {
            server.stop();
            executor.shutdownNow();
        }
    }
}
