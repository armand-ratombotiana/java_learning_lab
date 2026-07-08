package com.networking.tcp-udp-deep-dive;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NagleAlgorithmTest {
    private NagleAlgorithm nagle;

    @BeforeEach
    void setUp() {
        nagle = new NagleAlgorithm(1460);
    }

    @Test
    void smallDataIsBufferedWhenOutstandingAck() {
        nagle.onData(new byte[]{'A'});
        assertTrue(nagle.getTotalSent() > 0,
            "Initial write should send (no outstanding ack)");
        int sentAfterFirst = nagle.getTotalSent();
        nagle.onData(new byte[]{'B'});
        assertEquals(sentAfterFirst, nagle.getTotalSent(),
            "Second write should buffer (outstanding ack)");
    }

    @Test
    void largeDataSentImmediately() {
        byte[] largeData = new byte[2000];
        var segments = nagle.onData(largeData);
        assertFalse(segments.isEmpty(), "Data >= MSS should send immediately");
    }

    @Test
    void ackFlushesBuffer() {
        nagle.onData(new byte[]{'A'});
        int beforeAck = nagle.getPacketCount();
        nagle.onAckReceived();
        assertTrue(nagle.getPacketCount() >= beforeAck,
            "ACK should flush buffered data");
    }

    @Test
    void multipleSmallWritesCombine() {
        for (int i = 0; i < 10; i++) {
            nagle.onData(new byte[]{'A'});
        }
        nagle.onAckReceived();
        assertTrue(nagle.getPacketCount() <= 5,
            "Multiple small writes should combine into fewer packets");
    }

    @Test
    void mssBoundarySends() {
        byte[] partial = new byte[1000];
        var seg1 = nagle.onData(partial);
        assertEquals(1, seg1.size(), "First write < MSS should send (no outstanding)");
        nagle.onAckReceived();
        byte[] more = new byte[1000];
        var seg2 = nagle.onData(more);
        assertEquals(1, seg2.size(), "Should send after ACK received");
    }

    @Test
    void sizeJustBelowMssBuffers() {
        byte[] data = new byte[1459];
        nagle.onData(new byte[]{'A'});
        nagle.onData(data);
        assertEquals(1, nagle.getPacketCount(),
            "Should have sent only initial (data < MSS with outstanding ack)");
    }

    @Test
    void noBufferingWhenNoOutstandingAcks() {
        byte[] data = new byte[100];
        nagle.onData(data);
        int packetsAfterFirst = nagle.getPacketCount();
        nagle.onAckReceived();
        nagle.onData(data);
        assertEquals(packetsAfterFirst + 1, nagle.getPacketCount(),
            "Should send immediately when no outstanding ack");
    }

    @Test
    void zeroLengthInputDoesNothing() {
        var segments = nagle.onData(new byte[0]);
        assertTrue(segments.isEmpty(), "Zero-length data should not send");
    }

    @Test
    void ackWithMoreDataFlushes() {
        nagle.onData(new byte[]{'A'});
        nagle.onAckReceived(true);
        assertEquals(2, nagle.getPacketCount(),
            "ACK with more data pending should flush buffer");
    }
}
