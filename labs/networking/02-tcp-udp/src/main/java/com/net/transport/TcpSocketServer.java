package com.net.transport;

import java.util.*;
import java.util.concurrent.*;
import java.net.*;

public class TcpSocketServer {

    public static class TcpServer {
        private final int port;
        private final Map<String, Queue<String>> messageQueues = new ConcurrentHashMap<>();
        private volatile boolean running;

        public TcpServer(int port) {
            this.port = port;
            this.running = true;
        }

        public void start() {
            System.out.println("TCP Server listening on port " + port);
            Executors.newSingleThreadExecutor().submit(() -> {
                int clientId = 0;
                while (running) {
                    try {
                        Thread.sleep(200);
                        String client = "client-" + (++clientId);
                        messageQueues.put(client, new ConcurrentLinkedQueue<>());
                        System.out.println("Accepted connection from " + client);

                        String msg = "Hello from server, " + client;
                        messageQueues.get(client).add(msg);
                        System.out.println("Sent to " + client + ": " + msg);

                        if (clientId >= 3) running = false;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        public List<String> readMessages(String clientId) {
            Queue<String> queue = messageQueues.get(clientId);
            if (queue == null) return List.of();
            List<String> msgs = new ArrayList<>();
            String msg;
            while ((msg = queue.poll()) != null) {
                msgs.add(msg);
            }
            return msgs;
        }

        public void stop() { running = false; }
    }

    public static void main(String[] args) throws Exception {
        TcpServer server = new TcpServer(9000);
        server.start();

        Thread.sleep(500);

        System.out.println("\nClient reads:");
        for (int i = 1; i <= 3; i++) {
            String clientId = "client-" + i;
            List<String> msgs = server.readMessages(clientId);
            System.out.println("  " + clientId + " received: " + msgs);
        }

        server.stop();
    }
}
