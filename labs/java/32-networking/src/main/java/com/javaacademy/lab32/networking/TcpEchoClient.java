package com.javaacademy.lab32.networking;

import java.io.*;
import java.net.*;

public class TcpEchoClient implements AutoCloseable {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public TcpEchoClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public String sendMessage(String message) throws IOException {
        out.println(message);
        return in.readLine();
    }

    @Override
    public void close() throws IOException {
        try { socket.close(); } catch (IOException ignored) { }
    }
}
