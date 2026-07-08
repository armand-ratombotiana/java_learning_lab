package com.networking.tcp-udp-deep-dive;

import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

@FunctionalInterface
public interface SocketConfigurator {
    void configure(Socket socket) throws SocketException;

    static SocketConfigurator forThroughput() {
        return s -> {
            s.setReceiveBufferSize(2 * 1024 * 1024);
            s.setSendBufferSize(2 * 1024 * 1024);
            s.setTcpNoDelay(false);
            s.setPerformancePreferences(0, 2, 1);
        };
    }

    static SocketConfigurator forLowLatency() {
        return s -> {
            s.setReceiveBufferSize(65536);
            s.setSendBufferSize(65536);
            s.setTcpNoDelay(true);
            s.setSoTimeout(5000);
            s.setPerformancePreferences(2, 0, 1);
        };
    }

    static SocketConfigurator forHighBdp() {
        return s -> {
            s.setReceiveBufferSize(16 * 1024 * 1024);
            s.setSendBufferSize(16 * 1024 * 1024);
            s.setTcpNoDelay(false);
            s.setPerformancePreferences(0, 1, 2);
        };
    }

    static void configureServer(ServerSocket ss) throws SocketException {
        ss.setReuseAddress(true);
        ss.setReceiveBufferSize(65536);
        ss.setPerformancePreferences(0, 1, 2);
    }

    static void configureDatagram(DatagramSocket ds) throws SocketException {
        ds.setReceiveBufferSize(512 * 1024);
        ds.setSendBufferSize(512 * 1024);
        ds.setReuseAddress(true);
    }

    static SocketConfigurator combined(SocketConfigurator... configurers) {
        return s -> {
            for (var c : configurers) c.configure(s);
        };
    }

    static String inspect(Socket socket) {
        try {
            return String.format("""
                Socket Inspection:
                  Local: %s
                  Remote: %s
                  TCP_NODELAY: %b
                  SO_RCVBUF: %d
                  SO_SNDBUF: %d
                  SO_TIMEOUT: %d
                  SO_LINGER: %s
                  KEEPALIVE: %b
                  TRAFFIC_CLASS: %d
                  REUSE_ADDR: %b
                  OOB_INLINE: %b
                """,
                socket.getLocalSocketAddress(),
                socket.getRemoteSocketAddress(),
                socket.getTcpNoDelay(),
                socket.getReceiveBufferSize(),
                socket.getSendBufferSize(),
                socket.getSoTimeout(),
                socket.getSoLinger() > 0 ? String.valueOf(socket.getSoLinger()) : "disabled",
                socket.getKeepAlive(),
                socket.getTrafficClass(),
                socket.getReuseAddress(),
                socket.getOOBInline());
        } catch (SocketException e) {
            return "Inspection failed: " + e.getMessage();
        }
    }
}
