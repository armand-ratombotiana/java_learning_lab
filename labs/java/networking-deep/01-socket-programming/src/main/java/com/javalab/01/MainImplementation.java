package com.javalab.01;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MainImplementation {

    public static class EchoServer implements AutoCloseable {
        private final ServerSocket serverSocket;
        private final ExecutorService pool;
        private volatile boolean running;

        public EchoServer(int port) throws IOException {
            this.serverSocket = new ServerSocket(port);
            this.pool = Executors.newCachedThreadPool();
            this.running = true;
        }

        public void start() {
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    pool.submit(() -> handleClient(client));
                } catch (IOException e) {
                    if (running) e.printStackTrace();
                }
            }
        }

        private void handleClient(Socket client) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                String line;
                while ((line = in.readLine()) != null) {
                    out.println("Echo: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void stop() { running = false; try { serverSocket.close(); } catch (IOException e) {} pool.shutdown(); }
        public void close() { stop(); }
        public int getPort() { return serverSocket.getLocalPort(); }
    }

    public static class EchoClient implements AutoCloseable {
        private final Socket socket;
        private final BufferedReader in;
        private final PrintWriter out;

        public EchoClient(String host, int port) throws IOException {
            this.socket = new Socket(host, port);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        }

        public String send(String message) throws IOException {
            out.println(message);
            return in.readLine();
        }

        public void close() throws IOException {
            socket.close();
        }
    }
}
