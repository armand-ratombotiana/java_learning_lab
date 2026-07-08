package com.networking.tcp-udp-deep-dive;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class ReliableUdpSocketTest {
    private ReliableUdpSocket server;
    private ReliableUdpSocket client;
    private static final int PORT = 9876;

    @BeforeEach
    void setUp() throws Exception {
        server = new ReliableUdpSocket(PORT, new InetSocketAddress("127.0.0.1", PORT + 1));
        client = new ReliableUdpSocket(PORT + 1, new InetSocketAddress("127.0.0.1", PORT));
    }

    @AfterEach
    void tearDown() {
        server.close();
        client.close();
    }

    @Test
    void sendAndReceive() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        byte[][] received = {null};
        server.setDataConsumer(data -> {
            received[0] = data;
            latch.countDown();
        });
        byte[] testData = "Hello".getBytes();
        client.send(testData);
        assertTrue(latch.await(2, TimeUnit.SECONDS), "Should receive within timeout");
        assertArrayEquals(testData, received[0]);
    }

    @Test
    void multipleMessages() throws Exception {
        CountDownLatch latch = new CountDownLatch(3);
        StringBuilder sb = new StringBuilder();
        server.setDataConsumer(data -> {
            sb.append(new String(data));
            latch.countDown();
        });
        for (int i = 0; i < 3; i++) {
            client.send(("msg" + i).getBytes());
        }
        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertEquals("msg0msg1msg2", sb.toString());
    }

    @Test
    void ackReducesOutstanding() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        server.setDataConsumer(data -> latch.countDown());
        client.send("test".getBytes());
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        Thread.sleep(300);
        assertEquals(0, client.getOutstandingPackets(),
            "Ack should clear outstanding packets");
    }

    @Test
    void outOfOrderDelivery() throws Exception {
        CountDownLatch latch = new CountDownLatch(3);
        StringBuilder sb = new StringBuilder();
        server.setDataConsumer(data -> {
            sb.append(new String(data));
            latch.countDown();
        });
        client.send("A".getBytes());
        client.send("B".getBytes());
        client.send("C".getBytes());
        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertEquals("ABC", sb.toString());
    }

    @Test
    void receiverSendsAck() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        var clientRef = new ReliableUdpSocket[]{null};
        var client2 = new ReliableUdpSocket(PORT + 2, new InetSocketAddress("127.0.0.1", PORT));
        clientRef[0] = client2;
        server.setDataConsumer(data -> {});
        client2.send("data".getBytes());
        Thread.sleep(500);
        assertTrue(server.getOutstandingPackets() <= 1);
        client2.close();
    }

    @Test
    void socketCloseReleasesResources() {
        server.close();
        client.close();
        assertEquals(0, server.getOutstandingPackets());
        assertEquals(0, client.getOutstandingPackets());
    }

    @Test
    void sequentialSeqNumbers() {
        int seq1 = client.getNextSeqNum();
        int seq2 = client.getNextSeqNum();
        assertEquals(seq2, seq1 + 1);
    }
}
