package com.sd.consistency;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class QuorumSimulation {

    public static class QuorumNode {
        private final String id;
        private volatile String value;
        private volatile long version;

        public QuorumNode(String id) { this.id = id; }

        public synchronized WriteAck write(String v, long ver) {
            if (ver > version) {
                this.value = v;
                this.version = ver;
                return new WriteAck(true, version);
            }
            return new WriteAck(false, version);
        }

        public ReadResult read() {
            return new ReadResult(value, version);
        }

        public String getId() { return id; }
    }

    public static record WriteAck(boolean success, long version) {}
    public static record ReadResult(String value, long version) {}

    public static class QuorumSystem {
        private final List<QuorumNode> nodes;
        private final int writeQuorum;
        private final int readQuorum;
        private final AtomicLong globalVersion = new AtomicLong(0);

        public QuorumSystem(List<QuorumNode> nodes) {
            this.nodes = nodes;
            this.writeQuorum = nodes.size() / 2 + 1;
            this.readQuorum = nodes.size() / 2 + 1;
            System.out.println("N=" + nodes.size() + " W=" + writeQuorum + " R=" + readQuorum);
        }

        public boolean write(String value) {
            long version = globalVersion.incrementAndGet();
            int acks = 0;
            for (QuorumNode node : nodes) {
                WriteAck ack = node.write(value, version);
                if (ack.success()) acks++;
            }
            boolean success = acks >= writeQuorum;
            System.out.println("Write '" + value + "' v" + version + ": " + acks + " acks -> "
                + (success ? "SUCCESS" : "FAILED"));
            return success;
        }

        public String read() {
            Map<Long, List<String>> versionValues = new HashMap<>();
            for (QuorumNode node : nodes) {
                ReadResult r = node.read();
                if (r.value() != null) {
                    versionValues.computeIfAbsent(r.version(), k -> new ArrayList<>()).add(r.value());
                }
            }

            long latestVersion = versionValues.keySet().stream().max(Long::compareTo).orElse(-1L);
            String latestValue = latestVersion >= 0
                ? versionValues.get(latestVersion).get(0) : null;

            System.out.println("Read: latest v" + latestVersion + " = " + latestValue);
            return latestValue;
        }
    }

    public static void main(String[] args) {
        List<QuorumNode> nodes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            nodes.add(new QuorumNode("node-" + i));
        }

        QuorumSystem quorum = new QuorumSystem(nodes);
        quorum.write("Alice");
        quorum.write("Bob");
        quorum.read();
    }
}
