package com.networking.tcp-udp-deep-dive;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ReliableUdpSocket implements AutoCloseable {
    private final DatagramSocket socket;
    private final InetSocketAddress peer;
    private final AtomicInteger nextSeqNum = new AtomicInteger(0);
    private final Map<Integer, SentPacket> sentPackets = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final NavigableMap<Integer, byte[]> reorderBuffer = new TreeMap<>();
    private int expectedSeqNum = 0;
    private volatile boolean running = true;
    private final int timeoutMs;
    private Consumer<byte[]> dataConsumer = data -> {};

    private static class SentPacket {
        final byte[] data;
        final long sentTime;
        final int retransmitCount;

        SentPacket(byte[] data) {
            this.data = data;
            this.sentTime = System.currentTimeMillis();
            this.retransmitCount = 0;
        }
    }

    public ReliableUdpSocket(int port, InetSocketAddress peer, int timeoutMs) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.socket.setSoTimeout(0);
        this.peer = peer;
        this.timeoutMs = timeoutMs;
        startReceiver();
        startRetransmitter();
    }

    public ReliableUdpSocket(int port, InetSocketAddress peer) throws SocketException {
        this(port, peer, 100);
    }

    public int getNextSeqNum() { return nextSeqNum.get(); }

    public void setDataConsumer(Consumer<byte[]> consumer) { this.dataConsumer = consumer; }

    public void send(byte[] data) {
        int seqNum = nextSeqNum.getAndIncrement();
        byte[] packetData = serializePacket(seqNum, data, PacketType.DATA);
        sentPackets.put(seqNum, new SentPacket(data));
        sendRawPacket(packetData);
    }

    private void sendRawPacket(byte[] data) {
        try {
            DatagramPacket dp = new DatagramPacket(data, data.length, peer);
            socket.send(dp);
        } catch (IOException e) {
            System.err.println("Send failed: " + e.getMessage());
        }
    }

    public void sendAck(int seqNum) {
        byte[] ackData = serializePacket(seqNum, new byte[0], PacketType.ACK);
        sendRawPacket(ackData);
    }

    private byte[] serializePacket(int seqNum, byte[] data, PacketType type) {
        ByteBuffer buf = ByteBuffer.allocate(10 + data.length);
        buf.putInt(seqNum);
        buf.putInt(data.length);
        buf.put((byte) type.ordinal());
        int checksum = Arrays.hashCode(data) ^ seqNum;
        buf.putInt(checksum);
        buf.put(data);
        return buf.array();
    }

    private void startReceiver() {
        scheduler.submit(() -> {
            byte[] buf = new byte[65535];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            while (running && !socket.isClosed()) {
                try {
                    socket.receive(dp);
                    byte[] received = Arrays.copyOf(dp.getData(), dp.getLength());
                    scheduler.submit(() -> handlePacket(received));
                } catch (IOException e) {
                    if (running && !socket.isClosed()) {
                        System.err.println("Receive error: " + e.getMessage());
                    }
                }
            }
        });
    }

    private void handlePacket(byte[] raw) {
        ByteBuffer buf = ByteBuffer.wrap(raw);
        int seqNum = buf.getInt();
        int dataLen = buf.getInt();
        PacketType type = PacketType.values()[buf.get()];
        int checksum = buf.getInt();
        byte[] data = new byte[dataLen];
        buf.get(data);

        int expectedChecksum = Arrays.hashCode(data) ^ seqNum;
        if (checksum != expectedChecksum) {
            System.err.println("Checksum mismatch for seq " + seqNum);
            return;
        }

        switch (type) {
            case DATA -> handleDataPacket(seqNum, data);
            case ACK -> handleAckPacket(seqNum);
        }
    }

    private void handleDataPacket(int seqNum, byte[] data) {
        if (seqNum == expectedSeqNum) {
            dataConsumer.accept(data);
            expectedSeqNum++;
            while (reorderBuffer.containsKey(expectedSeqNum)) {
                dataConsumer.accept(reorderBuffer.remove(expectedSeqNum));
                expectedSeqNum++;
            }
        } else if (seqNum > expectedSeqNum) {
            reorderBuffer.putIfAbsent(seqNum, data);
        }
        sendAck(seqNum);
    }

    private void handleAckPacket(int seqNum) {
        sentPackets.entrySet().removeIf(e -> e.getKey() <= seqNum);
    }

    private void startRetransmitter() {
        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            sentPackets.forEach((seqNum, sentPacket) -> {
                if (now - sentPacket.sentTime > timeoutMs) {
                    byte[] packetData = serializePacket(seqNum, sentPacket.data, PacketType.DATA);
                    sendRawPacket(packetData);
                }
            });
        }, timeoutMs, timeoutMs, TimeUnit.MILLISECONDS);
    }

    public int getOutstandingPackets() { return sentPackets.size(); }

    public int getReorderBufferSize() { return reorderBuffer.size(); }

    @Override
    public void close() {
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        socket.close();
    }

    enum PacketType { DATA, ACK }
}
