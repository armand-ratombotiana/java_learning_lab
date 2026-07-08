# TCP/UDP Deep Dive — Code Deep Dive

## 1. TCP State Machine Implementation

```java
package com.networking.tcp-udp-deep-dive;

public enum TcpState {
    CLOSED, LISTEN, SYN_SENT, SYN_RCVD, ESTABLISHED,
    FIN_WAIT_1, FIN_WAIT_2, CLOSE_WAIT, CLOSING, LAST_ACK, TIME_WAIT;

    public boolean canSendData() {
        return this == ESTABLISHED || this == CLOSE_WAIT;
    }

    public boolean canReceiveData() {
        return this == ESTABLISHED || this == FIN_WAIT_1 || this == FIN_WAIT_2;
    }
}
```

The TCP state machine transitions based on events:

```java
public class TcpStateMachine {
    private TcpState state = TcpState.CLOSED;

    public void onSynSent() {
        if (state == TcpState.CLOSED) {
            transitionTo(TcpState.SYN_SENT);
        }
    }

    public void onSynReceived() {
        if (state == TcpState.SYN_SENT) {
            transitionTo(TcpState.SYN_RCVD);
        }
    }

    public void onEstablished() {
        if (state == TcpState.SYN_RCVD || state == TcpState.SYN_SENT) {
            transitionTo(TcpState.ESTABLISHED);
        }
    }

    private void transitionTo(TcpState newState) {
        System.out.println("State: " + state + " -> " + newState);
        state = newState;
    }
}
```

## 2. Congestion Control Simulator

The core congestion control algorithm tracks cwnd (congestion window), ssthresh (slow start threshold), and implements AIMD:

```java
public class CongestionControl {
    private int cwnd = 10;        // Initial window (MSS units)
    private int ssthresh = 65535; // Initial threshold
    private int ackedBytes = 0;
    private boolean inFastRecovery = false;

    public void onAckReceived(int acked) {
        ackedBytes += acked;
        if (cwnd < ssthresh) {
            // Slow start: cwnd += MSS per ACK
            cwnd = Math.min(cwnd + acked, ssthresh);
        } else {
            // Congestion avoidance: cwnd += MSS^2 / cwnd per ACK
            cwnd += (acked * acked) / cwnd;
        }
    }

    public void onLossDetected(LossType type) {
        switch (type) {
            case TIMEOUT -> {
                ssthresh = Math.max(cwnd / 2, 2);
                cwnd = 10; // Reset to initial window
                inFastRecovery = false;
            }
            case TRIPLE_DUP_ACK -> {
                ssthresh = Math.max(cwnd / 2, 2);
                cwnd = ssthresh;
                inFastRecovery = true;
            }
        }
    }
}
```

### TCP Cubic Implementation

```java
public class CubicCongestionControl {
    private static final double C = 0.4;
    private static final double BETA = 0.3;
    private double wmax;
    private double k;
    private double lastLossTime;

    public void onLoss() {
        wmax = cwnd;
        k = Math.cbrt(wmax * BETA / C);
        cwnd = wmax * (1 - BETA);
        lastLossTime = System.nanoTime();
    }

    public double getCwnd() {
        double t = (System.nanoTime() - lastLossTime) / 1_000_000_000.0;
        return C * Math.pow(t - k, 3) + wmax;
    }
}
```

## 3. Nagle's Algorithm Implementation

```java
public class NagleAlgorithm {
    private final int mss;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private boolean hasOutstandingAck = false;

    public NagleAlgorithm(int mss) {
        this.mss = mss;
    }

    public List<byte[]> onData(byte[] data) {
        List<byte[]> segments = new ArrayList<>();
        try {
            buffer.write(data);
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }

        if (buffer.size() >= mss) {
            segments.add(extractBuffer(mss));
        } else if (!hasOutstandingAck) {
            segments.add(extractBuffer(buffer.size()));
        }
        // Otherwise, buffer the data (Nagle's delay)
        return segments;
    }

    public void onAckReceived() {
        hasOutstandingAck = false;
        // Now send any buffered data
    }

    private byte[] extractBuffer(int size) {
        byte[] chunk = Arrays.copyOf(buffer.toByteArray(), size);
        buffer.reset();
        if (size < buffer.size()) {
            // Not realistic; just illustration
        }
        return chunk;
    }
}
```

## 4. Reliable UDP (R-UDP) Implementation

```java
public class ReliableUdpSocket implements AutoCloseable {
    private final DatagramSocket socket;
    private final InetSocketAddress peer;
    private int nextSeqNum = 0;
    private final Map<Integer, byte[]> sentPackets = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private volatile int expectedSeqNum = 0;

    public ReliableUdpSocket(int port, InetSocketAddress peer) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.peer = peer;
        startReceiver();
        startRetransmitter();
    }

    public void send(byte[] data) {
        int seqNum = nextSeqNum++;
        sentPackets.put(seqNum, data);
        UdpPacket packet = new UdpPacket(seqNum, data, PacketType.DATA);
        sendPacket(packet);
    }

    private void sendPacket(UdpPacket packet) {
        try {
            byte[] buf = packet.serialize();
            DatagramPacket dp = new DatagramPacket(buf, buf.length, peer);
            socket.send(dp);
        } catch (IOException e) {
            System.err.println("Send failed: " + e.getMessage());
        }
    }

    private void startReceiver() {
        scheduler.submit(() -> {
            byte[] buf = new byte[65535];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            while (!socket.isClosed()) {
                try {
                    socket.receive(dp);
                    UdpPacket packet = UdpPacket.deserialize(dp.getData());
                    handlePacket(packet);
                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        System.err.println("Receive error: " + e.getMessage());
                    }
                }
            }
        });
    }

    private void handlePacket(UdpPacket packet) {
        switch (packet.getType()) {
            case DATA -> {
                if (packet.getSeqNum() == expectedSeqNum) {
                    // Deliver to application
                    onDataReceived(packet.getData());
                    expectedSeqNum++;
                }
                // Send ACK regardless (cumulative)
                sendPacket(new UdpPacket(packet.getSeqNum(), null, PacketType.ACK));
                // Remove from sent map when ACKed
                sentPackets.entrySet().removeIf(e -> e.getKey() <= packet.getSeqNum());
            }
            case ACK -> {
                sentPackets.entrySet().removeIf(e -> e.getKey() <= packet.getSeqNum());
            }
        }
    }

    private void startRetransmitter() {
        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            sentPackets.forEach((seq, data) -> {
                // Retransmit if not ACKed within 100ms
                sendPacket(new UdpPacket(seq, data, PacketType.DATA));
            });
        }, 100, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        scheduler.shutdown();
        socket.close();
    }
}
```

## 5. UDP Multicast Sender/Receiver

```java
public class MulticastSender {
    private final MulticastSocket socket;
    private final InetAddress group;

    public MulticastSender(String groupAddress, int port) throws IOException {
        this.group = InetAddress.getByName(groupAddress);
        this.socket = new MulticastSocket(port);
    }

    public void send(String message) throws IOException {
        byte[] buf = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, socket.getLocalPort());
        socket.send(packet);
    }

    public void close() {
        socket.close();
    }
}

public class MulticastReceiver {
    private final MulticastSocket socket;

    public MulticastReceiver(String groupAddress, int port) throws IOException {
        this.socket = new MulticastSocket(port);
        InetAddress group = InetAddress.getByName(groupAddress);
        socket.joinGroup(group);
    }

    public String receive() throws IOException {
        byte[] buf = new byte[65535];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        return new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
    }

    public void close() {
        socket.close();
    }
}
```

## 6. Socket Options Configuration

```java
public class SocketConfigurator {
    public static void configureTcpSocket(Socket socket) throws SocketException {
        // Disable Nagle's algorithm for low latency
        socket.setTcpNoDelay(true);

        // Set send/receive buffer sizes
        socket.setReceiveBufferSize(256 * 1024);    // 256KB
        socket.setSendBufferSize(256 * 1024);       // 256KB

        // Set timeout for reads (milliseconds)
        socket.setSoTimeout(5000);

        // Keepalive probes
        socket.setKeepAlive(true);

        // Linger on close
        socket.setSoLinger(true, 5);

        // Performance preferences (connectTime, latency, bandwidth)
        socket.setPerformancePreferences(1, 2, 0);
    }

    public static void configureUdpSocket(DatagramSocket socket) throws SocketException {
        socket.setReceiveBufferSize(512 * 1024);
        socket.setSendBufferSize(512 * 1024);
        socket.setSoTimeout(3000);
    }
}
```

## 7. Bandwidth-Delay Product Calculation

```java
public class BdpCalculator {
    public static double calculateBdpBytes(double bandwidthMbps, double rttMs) {
        double bandwidthBps = bandwidthMbps * 1_000_000;
        double rttSec = rttMs / 1000.0;
        return (bandwidthBps / 8) * rttSec;
    }

    public static int optimalBufferSizeBytes(double bandwidthMbps, double rttMs) {
        double bdp = calculateBdpBytes(bandwidthMbps, rttMs);
        // Socket buffers should be at least 2x BDP for full duplex
        return (int) Math.ceil(bdp * 2);
    }

    public static void main(String[] args) {
        double bw = 100; // 100 Mbps
        double rtt = 20; // 20 ms
        double bdp = calculateBdpBytes(bw, rtt);
        int buffer = optimalBufferSizeBytes(bw, rtt);
        System.out.printf("BDP: %.0f bytes (%.1f KB)%n", bdp, bdp / 1024);
        System.out.printf("Optimal buffer: %d bytes (%.1f KB)%n", buffer, buffer / 1024.0);
    }
}
```

These implementations demonstrate the key concepts of TCP/UDP internals in practice.
