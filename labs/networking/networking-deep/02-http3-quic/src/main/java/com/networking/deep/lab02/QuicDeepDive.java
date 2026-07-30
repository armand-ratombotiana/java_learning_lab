package com.networking.deep.lab02;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class QuicDeepDive {

    public enum QuicState { INIT, HANDSHAKE, ESTABLISHED, MIGRATED, CLOSED }

    public record QuicConnection(String connectionId, String sourceAddress, String destAddress, QuicState state) {}
    public record QuicStream(int streamId, boolean bidirectional, boolean finished) {}
    public record PacketLossSimulation(double lossRate, Random rand) {}

    public record ZeroRttData(String request, byte[] earlyData, int antiReplayWindow) {
        public boolean isReplayAttack() { return Math.abs(Instant.now().getEpochSecond() % 100000 - antiReplayWindow) < 5; }
    }

    public static class QuicEndpoint {
        private final String address;
        private final AtomicInteger streamCounter = new AtomicInteger(1);
        private final Map<String, QuicConnection> connections = new ConcurrentHashMap<>();
        private final Map<Integer, QuicStream> streams = new ConcurrentHashMap<>();

        public QuicEndpoint(String address) { this.address = address; }

        public QuicConnection connect(String peerAddress) {
            var connId = UUID.randomUUID().toString().substring(0, 8);
            var conn = new QuicConnection(connId, address, peerAddress, QuicState.INIT);
            connections.put(connId, conn);
            return conn;
        }

        public QuicConnection handshake(QuicConnection conn, boolean use0Rtt) {
            var state = conn.state();
            if (state == QuicState.INIT) {
                state = QuicState.HANDSHAKE;
                if (use0Rtt) {
                    System.out.println("0-RTT handshake: " + conn.connectionId());
                } else {
                    System.out.println("1-RTT handshake: " + conn.connectionId());
                }
                state = QuicState.ESTABLISHED;
            }
            var updated = new QuicConnection(conn.connectionId(), conn.sourceAddress(), conn.destAddress(), state);
            connections.put(conn.connectionId(), updated);
            return updated;
        }

        public QuicStream openStream(QuicConnection conn, boolean bidirectional) {
            int id = streamCounter.getAndAdd(2);
            var stream = new QuicStream(id, bidirectional, false);
            streams.put(id, stream);
            return stream;
        }

        public QuicConnection migrateConnection(QuicConnection conn, String newAddress) {
            System.out.println("Connection migration: " + conn.connectionId() + " " + conn.destAddress() + " -> " + newAddress);
            var migrated = new QuicConnection(conn.connectionId(), newAddress, conn.destAddress(), QuicState.MIGRATED);
            connections.put(conn.connectionId(), migrated);
            return migrated;
        }
    }

    public static class Http3OverQuic {
        private final QuicEndpoint client;
        private final QuicEndpoint server;
        private final PacketLossSimulation loss;

        public Http3OverQuic(QuicEndpoint client, QuicEndpoint server, double lossRate) {
            this.client = client;
            this.server = server;
            this.loss = new PacketLossSimulation(lossRate, new Random());
        }

        public long simulateRequest(String request) {
            long start = System.nanoTime();
            var conn = client.connect(server.address);
            var established = client.handshake(conn, true);
            if (loss.rand().nextDouble() < loss.lossRate()) {
                try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            var stream = client.openStream(established, true);
            System.out.println("  HTTP/3 request on stream " + stream.streamId() + ": " + request);
            return (System.nanoTime() - start) / 1_000_000;
        }
    }

    public static class Http2Simulator {
        public long simulateRequest(String request, PacketLossSimulation loss) {
            long start = System.nanoTime();
            if (loss.rand().nextDouble() < loss.lossRate()) {
                try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return (System.nanoTime() - start) / 1_000_000;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== QUIC Connection ===");
        var client = new QuicEndpoint("10.0.0.1:54321");
        var server = new QuicEndpoint("10.0.0.2:443");

        var conn = client.connect(server.address);
        System.out.println("Connection: " + conn);
        var established = client.handshake(conn, false);
        System.out.println("After 1-RTT: " + established);

        var zeroRtt = client.connect(server.address);
        var zeroRttConn = client.handshake(zeroRtt, true);
        System.out.println("After 0-RTT: " + zeroRttConn);

        System.out.println("\n=== Connection Migration ===");
        var migrated = client.migrateConnection(established, "10.0.0.1:54322");
        System.out.println("Migrated: " + migrated);

        System.out.println("\n=== HTTP/3 vs HTTP/2 under packet loss ===");
        var h3 = new Http3OverQuic(client, server, 0.05);
        var h2 = new Http2Simulator();

        long h3total = 0, h2total = 0;
        var lossSim = new PacketLossSimulation(0.05, new Random());
        for (int i = 0; i < 5; i++) {
            h3total += h3.simulateRequest("GET /api/data-" + i);
            h2total += h2.simulateRequest("GET /api/data-" + i, lossSim);
        }
        System.out.printf("HTTP/3 total: %dms (avg %dms)%n", h3total, h3total / 5);
        System.out.printf("HTTP/2 total: %dms (avg %dms)%n", h2total, h2total / 5);
        System.out.printf("Improvement: %.0f%%%n", (1 - (double) h3total / h2total) * 100);
    }
}
