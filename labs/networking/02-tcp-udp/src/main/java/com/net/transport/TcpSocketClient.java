package com.net.transport;

import java.util.*;
import java.util.concurrent.*;

public class TcpSocketClient {

    public static class TcpClient {
        private final String id;
        private final String serverAddress;
        private final Queue<String> receivedMessages = new ConcurrentLinkedQueue<>();
        private volatile boolean connected;

        public TcpClient(String id, String serverAddress) {
            this.id = id;
            this.serverAddress = serverAddress;
        }

        public boolean connect() {
            connected = true;
            System.out.println("Client " + id + " connected to " + serverAddress);
            return true;
        }

        public boolean send(String message) {
            if (!connected) return false;
            System.out.println("Client " + id + " sent: " + message);
            return true;
        }

        public void receive(String message) {
            receivedMessages.add(message);
            System.out.println("Client " + id + " received: " + message);
        }

        public List<String> getReceivedMessages() {
            return new ArrayList<>(receivedMessages);
        }

        public void disconnect() {
            connected = false;
            System.out.println("Client " + id + " disconnected");
        }
    }

    public static void main(String[] args) {
        TcpClient client = new TcpClient("client-1", "localhost:9000");
        client.connect();
        client.send("GET /api/data HTTP/1.1");
        client.receive("HTTP/1.1 200 OK\n{\"data\": \"value\"}");
        client.send("POST /api/data HTTP/1.1\n{\"key\": \"val\"}");
        client.disconnect();

        System.out.println("\nAll messages received: " + client.getReceivedMessages());
    }
}
