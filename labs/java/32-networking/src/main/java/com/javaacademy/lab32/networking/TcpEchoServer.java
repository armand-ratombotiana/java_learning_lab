package com.javaacademy.lab32.networking;

import java.io.*;
import java.net.*;

public class TcpEchoServer implements AutoCloseable {
    private ServerSocket serverSocket;
    private volatile boolean running = true;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        new Thread(() -> {
            while (running) {
                try (Socket client = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                     PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        out.println("Echo: " + line);
                    }
                } catch (IOException e) {
                    if (running) e.printStackTrace();
                }
            }
        }, "echo-server").start();
    }

    public int getPort() { return serverSocket.getLocalPort(); }

    @Override
    public void close() {
        running = false;
        try { if (serverSocket != null) serverSocket.close(); }
        catch (IOException ignored) { }
    }
}
