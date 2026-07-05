package com.net.transport;

import java.util.*;
import java.util.concurrent.*;

public class UdpMulticast {

    public static class MulticastGroup {
        private final String groupAddress;
        private final int port;
        private final List<UdpListener> members = new CopyOnWriteArrayList<>();

        public MulticastGroup(String groupAddress, int port) {
            this.groupAddress = groupAddress;
            this.port = port;
        }

        public void join(UdpListener listener) {
            members.add(listener);
            System.out.println(listener.getId() + " joined group " + groupAddress);
        }

        public void leave(UdpListener listener) {
            members.remove(listener);
        }

        public void send(String message, String senderId) {
            System.out.println("\n[" + senderId + "] broadcasting: " + message);
            for (UdpListener member : members) {
                if (!member.getId().equals(senderId)) {
                    member.receive(message, senderId);
                }
            }
        }
    }

    public static class UdpListener {
        private final String id;
        private final List<String> inbox = new CopyOnWriteArrayList<>();

        public UdpListener(String id) { this.id = id; }

        public void receive(String message, String from) {
            inbox.add(message);
            System.out.println("  [" + id + "] received from " + from + ": " + message);
        }

        public String getId() { return id; }
        public List<String> getInbox() { return inbox; }
    }

    public static void main(String[] args) {
        MulticastGroup group = new MulticastGroup("230.0.0.1", 4446);

        UdpListener node1 = new UdpListener("node-1");
        UdpListener node2 = new UdpListener("node-2");
        UdpListener node3 = new UdpListener("node-3");

        group.join(node1);
        group.join(node2);
        group.join(node3);

        group.send("Hello everyone!", "node-1");
        group.send("Status update", "node-2");

        System.out.println("\nInboxes:");
        System.out.println("  node-1: " + node1.getInbox());
        System.out.println("  node-2: " + node2.getInbox());
        System.out.println("  node-3: " + node3.getInbox());
    }
}
