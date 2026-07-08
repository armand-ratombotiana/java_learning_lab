package com.networking.tcp-udp-deep-dive;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.concurrent.CompletableFuture;

public class MulticastMessenger implements AutoCloseable {
    private final MulticastSocket socket;
    private final InetAddress group;
    private final int port;
    private volatile boolean running = true;

    public MulticastMessenger(String groupAddress, int port) throws IOException {
        this.group = InetAddress.getByName(groupAddress);
        this.port = port;
        this.socket = new MulticastSocket(port);
        socket.setTimeToLive(1);
        socket.setReuseAddress(true);
        socket.joinGroup(group);
    }

    public void send(String message) throws IOException {
        byte[] buf = (System.currentTimeMillis() + "|" + message).getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);
        socket.send(packet);
    }

    public void startReceiver(java.util.function.Consumer<String> callback) {
        CompletableFuture.runAsync(() -> {
            byte[] buf = new byte[65535];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            while (running && !socket.isClosed()) {
                try {
                    socket.receive(packet);
                    String msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    if (!packet.getAddress().equals(InetAddress.getLocalHost())) {
                        callback.accept(msg);
                    } else {
                        callback.accept(msg);
                    }
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Receive error: " + e.getMessage());
                    }
                }
            }
        });
    }

    public void close() {
        running = false;
        try {
            socket.leaveGroup(group);
        } catch (IOException e) {
            // ignore during close
        }
        socket.close();
    }

    public static void main(String[] args) throws Exception {
        String group = "239.0.0.1";
        int port = 5000;
        System.out.println("=== Multicast Messenger ===");
        try (MulticastMessenger msgr = new MulticastMessenger(group, port)) {
            msgr.startReceiver(msg -> System.out.println("Received: " + msg));
            for (int i = 0; i < 5; i++) {
                msgr.send("Message " + i);
                Thread.sleep(1000);
            }
        }
    }
}
