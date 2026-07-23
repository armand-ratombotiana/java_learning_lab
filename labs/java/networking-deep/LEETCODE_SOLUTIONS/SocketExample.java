package networking;

import java.io.*;
import java.net.*;

/**
 * Traditional Java Socket programming (blocking I/O).
 * 
 * Shows: ServerSocket, Socket, BufferedReader/PrintWriter patterns.
 * 
 * Time: O(1) per connection accept
 * Space: O(buffer) per connection
 */
public class SocketExample {

    static class EchoServer {
        private final ServerSocket serverSocket;

        EchoServer(int port) throws IOException {
            serverSocket = new ServerSocket(port);
        }

        void start() throws IOException {
            Socket client = serverSocket.accept();
            System.out.println("Client connected: " + client.getInetAddress());

            try (var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 var out = new PrintWriter(client.getOutputStream(), true)) {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Received: " + line);
                    out.println("Echo: " + line);
                    if ("quit".equalsIgnoreCase(line)) break;
                }
            }
            serverSocket.close();
        }
    }

    static class EchoClient {
        void start(int port) throws IOException {
            try (var socket = new Socket("localhost", port);
                 var out = new PrintWriter(socket.getOutputStream(), true);
                 var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                String[] messages = {"Hello", "World", "quit"};
                for (String msg : messages) {
                    out.println(msg);
                    String response = in.readLine();
                    System.out.println("Response: " + response);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8888;
        var server = new EchoServer(port);
        var client = new EchoClient();

        // Start server in background
        var serverThread = new Thread(() -> {
            try { server.start(); } catch (IOException e) { }
        });
        serverThread.start();

        // Give server time to start
        Thread.sleep(500);

        // Run client
        client.start(port);
        serverThread.join();

        System.out.println("All SocketExample tests passed.");
    }
}