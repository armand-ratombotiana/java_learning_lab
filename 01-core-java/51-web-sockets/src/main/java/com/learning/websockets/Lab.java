package com.learning.websockets;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Lab {

    static class WebSocketServer {
        private final ServerSocket serverSocket;
        private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
        private volatile boolean running = true;

        WebSocketServer(int port) throws IOException {
            serverSocket = new ServerSocket(port);
        }

        void start() {
            System.out.println("  Server listening on port " + serverSocket.getLocalPort());
            var thread = new Thread(() -> {
                while (running) {
                    try {
                        var client = serverSocket.accept();
                        var handler = new ClientHandler(client, this);
                        clients.add(handler);
                        new Thread(handler).start();
                    } catch (IOException e) {
                        if (running) System.out.println("  Server error: " + e.getMessage());
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }

        void broadcast(String message) {
            clients.forEach(c -> c.send(message));
        }

        void removeClient(ClientHandler client) {
            clients.remove(client);
        }

        void stop() throws IOException {
            running = false;
            serverSocket.close();
        }

        static class ClientHandler implements Runnable {
            private final Socket socket;
            private final WebSocketServer server;
            private final PrintWriter out;
            private final BufferedReader in;
            private final String clientId;

            ClientHandler(Socket socket, WebSocketServer server) throws IOException {
                this.socket = socket;
                this.server = server;
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.clientId = "client-" + System.nanoTime();
            }

            public void run() {
                try {
                    String input;
                    while ((input = in.readLine()) != null) {
                        System.out.println("    [" + clientId + "] RECV: " + input);
                        if ("/quit".equalsIgnoreCase(input)) break;
                        if ("/broadcast".equalsIgnoreCase(input)) {
                            server.broadcast(clientId + " says hello!");
                            continue;
                        }
                        send("Echo: " + input);
                    }
                } catch (IOException e) {
                    System.out.println("    [" + clientId + "] disconnected: " + e.getMessage());
                } finally {
                    close();
                }
            }

            void send(String message) {
                out.println(message);
            }

            void close() {
                try {
                    server.removeClient(this);
                    socket.close();
                } catch (IOException e) {}
            }
        }
    }

    static class WebSocketClient {
        private final Socket socket;
        private final PrintWriter out;
        private final BufferedReader in;

        WebSocketClient(String host, int port) throws IOException {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        String sendAndReceive(String message) throws IOException {
            out.println(message);
            return in.readLine();
        }

        void close() throws IOException {
            socket.close();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Web Sockets Lab ===\n");

        wsProtocol();
        serverAndClient();
        messageFormats();
        connectionLifecycle();
        scalingWebSockets();
    }

    static void wsProtocol() {
        System.out.println("--- WebSocket Protocol (RFC 6455) ---");
        System.out.println("""
  HTTP Upgrade handshake:
    GET /chat HTTP/1.1
    Upgrade: websocket
    Connection: Upgrade
    Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
    Sec-WebSocket-Version: 13

  Response:
    HTTP/1.1 101 Switching Protocols
    Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=

  Frame format:
    1 bit  FIN
    3 bits RSV
    4 bits Opcode (1=text, 2=binary, 8=close, 9=ping, A=pong)
    1 bit  MASK (client->server always masked)
    7/16/64 bits Payload length
    """);
    }

    static void serverAndClient() throws Exception {
        System.out.println("--- Server & Client (TCP simulation) ---");
        var server = new WebSocketServer(0);
        server.start();
        int port = server.serverSocket.getLocalPort();

        var client1 = new WebSocketClient("localhost", port);
        var client2 = new WebSocketClient("localhost", port);

        System.out.println("  Client1 -> Hello");
        System.out.println("  Client1 <- " + client1.sendAndReceive("Hello"));
        System.out.println("  Client2 -> World");
        System.out.println("  Client2 <- " + client2.sendAndReceive("World"));

        client1.close();
        client2.close();
        server.stop();
    }

    static void messageFormats() {
        System.out.println("\n--- Message Formats ---");
        System.out.println("""
  Text frames (opcode 0x01):
    JSON: {"type":"chat","payload":"hello"}
    Plain: raw string messages

  Binary frames (opcode 0x02):
    Protobuf, MessagePack, BSON
    Efficient for large payloads

  Control frames:
    Close (0x08): graceful shutdown with status code
    Ping (0x09): keep-alive heartbeat
    Pong (0x0A): response to ping

  Framing overhead: 2-14 bytes per frame
    """);
    }

    static void connectionLifecycle() {
        System.out.println("\n--- Connection Lifecycle ---");
        System.out.println("""
  1. HTTP Upgrade handshake (101 Switching Protocols)
  2. Open: bidirectional message exchange
     - Messages can arrive in any order
     - No request-response correlation (unlike HTTP)
  3. Ping/Pong keep-alive (application or proxy level)
  4. Close handshake:
     - Either side sends Close frame
     - Other side responds with Close
     - TCP connection terminates

  Reconnection strategy:
  - Exponential backoff (1s, 2s, 4s, 8s, max 30s)
  - On reconnect: resume state / replay missed messages
    """);
    }

    static void scalingWebSockets() {
        System.out.println("--- Scaling WebSockets ---");
        System.out.println("""
  Single server limits:
    ~10k connections (traditional, 1 thread/conn)
    ~100k connections (NIO, event-loop)
    ~1M connections (virtual threads, async)

  Horizontal scaling:
  - Sticky sessions (session affinity via load balancer)
  - External pub/sub (Redis, Kafka) for cross-node broadcast
  - WebSocket gateway (Amazon API Gateway WS, Cloudflare)

  Libraries:
  - Jakarta WebSocket (JSR 356): @ServerEndpoint, @ClientEndpoint
  - Spring WebSocket: WebSocketHandler, STOMP over WebSocket
  - Netty: high-performance NIO-based WebSocket
    """);
    }
}
