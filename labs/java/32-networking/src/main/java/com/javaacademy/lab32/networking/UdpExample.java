package com.javaacademy.lab32.networking;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

public class UdpExample {

    public String sendAndReceive(String host, int port, String message) throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000);
            byte[] sendData = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                InetAddress.getByName(host), port);
            socket.send(sendPacket);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            return new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
        }
    }

    public static class UdpEchoServer implements AutoCloseable {
        private DatagramSocket socket;
        private volatile boolean running = true;

        public void start(int port) throws IOException {
            socket = new DatagramSocket(port);
            new Thread(() -> {
                byte[] buffer = new byte[1024];
                while (running) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String received = new String(packet.getData(), 0, packet.getLength());
                        byte[] response = ("Echo: " + received).getBytes();
                        socket.send(new DatagramPacket(response, response.length, packet.getAddress(), packet.getPort()));
                    } catch (IOException e) {
                        if (running) e.printStackTrace();
                    }
                }
            }, "udp-echo").start();
        }

        public void close() {
            running = false;
            if (socket != null && !socket.isClosed()) socket.close();
        }
    }
}
