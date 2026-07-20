package com.javalab.01;

import org.junit.jupiter.api.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {

    @Test
    @DisplayName("Echo server should respond to client messages")
    void testEchoRoundTrip() throws Exception {
        MainImplementation.EchoServer server = new MainImplementation.EchoServer(0);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> { try { server.start(); } catch (Exception e) {} });
        Thread.sleep(200);
        try {
            MainImplementation.EchoClient client = new MainImplementation.EchoClient("localhost", server.getPort());
            String response = client.send("Hello");
            assertEquals("Echo: Hello", response);
            client.close();
        } finally {
            server.stop();
            executor.shutdown();
        }
    }

    @Test
    @DisplayName("Multiple messages should echo correctly")
    void testMultipleMessages() throws Exception {
        MainImplementation.EchoServer server = new MainImplementation.EchoServer(0);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> { try { server.start(); } catch (Exception e) {} });
        Thread.sleep(200);
        try {
            MainImplementation.EchoClient client = new MainImplementation.EchoClient("localhost", server.getPort());
            assertEquals("Echo: Hello", client.send("Hello"));
            assertEquals("Echo: World", client.send("World"));
            assertEquals("Echo: Test", client.send("Test"));
            client.close();
        } finally {
            server.stop();
            executor.shutdown();
        }
    }
}
