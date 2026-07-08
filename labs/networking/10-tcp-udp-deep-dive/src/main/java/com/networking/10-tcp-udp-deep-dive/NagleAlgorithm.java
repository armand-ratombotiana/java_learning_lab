package com.networking.tcp-udp-deep-dive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NagleAlgorithm {
    private final int mss;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private boolean hasOutstandingUnacked = false;
    private int totalSent = 0;
    private int packetCount = 0;

    public NagleAlgorithm(int mss) {
        this.mss = mss;
    }

    public int getTotalSent() { return totalSent; }
    public int getPacketCount() { return packetCount; }

    public synchronized List<byte[]> onData(byte[] data) {
        List<byte[]> segments = new ArrayList<>();
        try {
            buffer.write(data);
        } catch (IOException e) {
            throw new RuntimeException("Buffer error", e);
        }

        boolean canSend = buffer.size() >= mss || !hasOutstandingUnacked;
        if (canSend && buffer.size() > 0) {
            byte[] segment = extractBuffer(buffer.size());
            segments.add(segment);
            hasOutstandingUnacked = true;
            packetCount++;
            totalSent += segment.length;
        }
        return segments;
    }

    public synchronized void onAckReceived() {
        hasOutstandingUnacked = false;
        if (buffer.size() > 0) {
            byte[] segment = extractBuffer(buffer.size());
            totalSent += segment.length;
            packetCount++;
        }
    }

    public synchronized void onAckReceived(boolean hasMoreData) {
        hasOutstandingUnacked = false;
        if (buffer.size() > 0 && hasMoreData) {
            byte[] segment = extractBuffer(buffer.size());
            totalSent += segment.length;
            packetCount++;
        }
    }

    private byte[] extractBuffer(int size) {
        byte[] data = buffer.toByteArray();
        int actualSize = Math.min(size, data.length);
        byte[] chunk = new byte[actualSize];
        System.arraycopy(data, 0, chunk, 0, actualSize);
        buffer.reset();
        if (actualSize < data.length) {
            try {
                buffer.write(data, actualSize, data.length - actualSize);
            } catch (IOException e) {
                throw new RuntimeException("Buffer error", e);
            }
        }
        return chunk;
    }

    public static void main(String[] args) {
        NagleAlgorithm nagle = new NagleAlgorithm(1460);
        System.out.println("=== Nagle's Algorithm Simulation ===");
        for (int i = 0; i < 10; i++) {
            List<byte[]> segments = nagle.onData(new byte[]{'A'});
            if (segments.isEmpty()) {
                System.out.println("Write 1 byte: buffered (waiting)");
            } else {
                System.out.println("Write 1 byte: sent (" + segments.get(0).length + " bytes)");
            }
        }
        System.out.println("ACK received");
        nagle.onAckReceived();
        System.out.println("Total sent: " + nagle.getTotalSent() + " bytes in " + nagle.getPacketCount() + " packets");
        System.out.println("Without Nagle: 10 bytes in 10 packets");
        System.out.println("With Nagle: " + nagle.getTotalSent() + " bytes in " + nagle.getPacketCount() + " packets");
    }
}
