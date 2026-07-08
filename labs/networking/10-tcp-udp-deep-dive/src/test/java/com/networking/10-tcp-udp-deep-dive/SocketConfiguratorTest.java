package com.networking.tcp-udp-deep-dive;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.net.Socket;

class SocketConfiguratorTest {

    @Test
    void throughputConfigSetsLargeBuffers() throws Exception {
        try (Socket s = new Socket()) {
            SocketConfigurator.forThroughput().configure(s);
            assertTrue(s.getReceiveBufferSize() >= 2_000_000,
                "Throughput config should set large receive buffer");
            assertTrue(s.getSendBufferSize() >= 2_000_000,
                "Throughput config should set large send buffer");
        }
    }

    @Test
    void lowLatencyConfigDisablesNagle() throws Exception {
        try (Socket s = new Socket()) {
            SocketConfigurator.forLowLatency().configure(s);
            assertTrue(s.getTcpNoDelay(),
                "Low latency should disable Nagle");
        }
    }

    @Test
    void lowLatencySetsTimeout() throws Exception {
        try (Socket s = new Socket()) {
            SocketConfigurator.forLowLatency().configure(s);
            assertTrue(s.getSoTimeout() > 0,
                "Low latency should set timeout");
        }
    }

    @Test
    void combinedConfigAppliesAll() throws Exception {
        try (Socket s = new Socket()) {
            SocketConfigurator.combined(
                SocketConfigurator.forLowLatency(),
                SocketConfigurator.forThroughput()
            ).configure(s);
            assertTrue(s.getTcpNoDelay());
            assertTrue(s.getReceiveBufferSize() >= 2_000_000);
        }
    }

    @Test
    void inspectContainsKeyInfo() {
        try (Socket s = new Socket()) {
            String info = SocketConfigurator.inspect(s);
            assertTrue(info.contains("TCP_NODELAY"));
            assertTrue(info.contains("SO_RCVBUF"));
            assertTrue(info.contains("SO_SNDBUF"));
            assertTrue(info.contains("KEEPALIVE"));
        } catch (Exception e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    @Test
    void highBdpSetsVeryLargeBuffers() throws Exception {
        try (Socket s = new Socket()) {
            SocketConfigurator.forHighBdp().configure(s);
            assertTrue(s.getReceiveBufferSize() >= 10_000_000,
                "High BDP should set very large receive buffer");
        }
    }
}
