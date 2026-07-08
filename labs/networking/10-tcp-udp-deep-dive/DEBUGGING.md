# TCP/UDP Deep Dive — Debugging

## 1. Common TCP Issues

### 1.1 Connection Refused
**Symptoms:** SocketException: Connection refused
**Causes:**
- No service listening on the target port
- Firewall blocking the connection
- Target host unreachable

**Debugging:**
```java
public static void diagnoseConnection(String host, int port) {
    try {
        InetAddress address = InetAddress.getByName(host);
        System.out.println("Resolved: " + address.getHostAddress());

        if (!address.isReachable(5000)) {
            System.out.println("Host not reachable (ICMP)");
        }

        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(address, port), 3000);
            System.out.println("Connection successful");
        }
    } catch (UnknownHostException e) {
        System.out.println("DNS resolution failed: " + e.getMessage());
    } catch (SocketTimeoutException e) {
        System.out.println("Connection timeout (host:port may be filtered)");
    } catch (ConnectException e) {
        System.out.println("Connection refused: " + e.getMessage());
    } catch (IOException e) {
        System.out.println("IO Error: " + e.getMessage());
    }
}
```

### 1.2 Connection Reset
**Symptoms:** SocketException: Connection reset
**Causes:**
- RST packet received (e.g., application crashed without closing socket)
- SO_LINGER with timeout 0
- Writing to a closed connection
- Firewall reset

**Debugging:**
```java
// Check if peer has closed connection
public static boolean isConnectionClosed(Socket socket) {
    try {
        InputStream is = socket.getInputStream();
        int data = is.read();
        return data == -1; // EOF means connection closed
    } catch (SocketException e) {
        return true; // Connection reset
    } catch (IOException e) {
        return true;
    }
}
```

### 1.3 Connection Timeout
**Symptoms:** SocketTimeoutException: Read timed out
**Causes:**
- Network congestion
- Server overloaded
- Firewall dropping packets
- Incorrect timeout value

**Diagnostic:**
```java
public static void testNetworkLatency(String host) {
    try {
        InetAddress addr = InetAddress.getByName(host);
        long start = System.nanoTime();
        boolean reachable = addr.isReachable(5000);
        long elapsed = System.nanoTime() - start;
        System.out.println("ICMP RTT: " + (elapsed / 1_000_000) + "ms");

        // TCP RTT
        start = System.nanoTime();
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(addr, 80), 5000);
            elapsed = System.nanoTime() - start;
            System.out.println("TCP RTT: " + (elapsed / 1_000_000) + "ms");
        }
    } catch (IOException e) {
        System.out.println("Network test failed: " + e.getMessage());
    }
}
```

## 2. Packet Capture and Analysis

### tcpdump examples
```bash
# Capture all traffic on port 8080
tcpdump -i eth0 port 8080

# Capture TCP handshake
tcpdump -i eth0 'tcp port 8080 and (tcp[tcpflags] & (tcp-syn|tcp-ack) != 0)'

# Capture retransmissions
tcpdump -i eth0 'tcp[13] & 4 != 0'  # RST
tcpdump -i eth0 'tcp[13] & 8 != 0'  # FIN

# Analyze with Wireshark
# tshark -r capture.pcap -Y "tcp.analysis.retransmission"
```

### Wireshark Filters for TCP Analysis
```
# Show only TCP issues
tcp.analysis.flags

# Show retransmissions
tcp.analysis.retransmission

# Show duplicate ACKs
tcp.analysis.duplicate_ack

# Show zero window
tcp.analysis.zero_window

# Show connections by state
tcp.flags.syn == 1 && tcp.flags.ack == 0
tcp.flags.fin == 1
```

## 3. Debugging Nagle's Algorithm

### Is Nagle causing delays?
```java
public class NagleDebugger {
    public static void testNagleDelay() throws Exception {
        // Test 1: With Nagle
        try (Socket nagleSocket = new Socket("localhost", 8080)) {
            nagleSocket.setTcpNoDelay(false);
            long nagleTime = measureWriteLatency(nagleSocket);
            System.out.println("Nagle latency: " + nagleTime + "ms");
        }

        // Test 2: Without Nagle
        try (Socket noNagleSocket = new Socket("localhost", 8080)) {
            noNagleSocket.setTcpNoDelay(true);
            long noNagleTime = measureWriteLatency(noNagleSocket);
            System.out.println("No Nagle latency: " + noNagleTime + "ms");
        }
    }

    private static long measureWriteLatency(Socket socket) throws IOException {
        var out = socket.getOutputStream();
        long start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            out.write(1); // Write single byte
            out.flush();
        }
        return (System.nanoTime() - start) / 100_000; // Average in ms/10
    }
}
```

## 4. Debugging UDP Issues

### 4.1 Packet Loss Detection
```java
public class UdpLossDetector {
    private final Set<Integer> receivedSeqs = ConcurrentHashMap.newKeySet();
    private int expectedSeq = 0;

    public void onPacketReceived(int seqNum) {
        if (!receivedSeqs.add(seqNum)) {
            System.out.println("Duplicate packet: " + seqNum);
        }
        if (seqNum > expectedSeq + 1) {
            for (int lost = expectedSeq + 1; lost < seqNum; lost++) {
                System.out.println("Lost packet detected: " + lost);
            }
        }
        expectedSeq = Math.max(expectedSeq, seqNum);
    }
}
```

### 4.2 Buffer Overflow Detection
```java
// Check if receive buffer is being overwhelmed
public class BufferMonitor {
    private final DatagramSocket socket;
    private long lastWarningTime = 0;

    public BufferMonitor(DatagramSocket socket) {
        this.socket = socket;
    }

    public void checkBufferStatus() throws SocketException {
        int recvBufSize = socket.getReceiveBufferSize();
        long now = System.currentTimeMillis();

        // If we receive packets faster than we process them
        // the OS buffer may overflow
        if (now - lastWarningTime > 5000) {
            System.out.println("Receive buffer: " + recvBufSize + " bytes");
            lastWarningTime = now;
        }
    }
}
```

## 5. Debugging Tools

### 5.1 Java Built-in Debugging

```java
public class NetworkDebugUtil {
    // Enable Java networking debugging
    // Run with: -Djavax.net.debug=all

    public static void enableSslDebug() {
        System.setProperty("javax.net.debug", "ssl:handshake");
    }

    public static void printSocketInfo(Socket socket) throws SocketException {
        System.out.println("Socket info:");
        System.out.println("  Local: " + socket.getLocalSocketAddress());
        System.out.println("  Remote: " + socket.getRemoteSocketAddress());
        System.out.println("  TCP_NODELAY: " + socket.getTcpNoDelay());
        System.out.println("  SO_RCVBUF: " + socket.getReceiveBufferSize());
        System.out.println("  SO_SNDBUF: " + socket.getSendBufferSize());
        System.out.println("  SO_TIMEOUT: " + socket.getSoTimeout());
        System.out.println("  SO_LINGER: " + socket.getSoLinger());
        System.out.println("  KEEPALIVE: " + socket.getKeepAlive());
        System.out.println("  TRAFFIC_CLASS: " + socket.getTrafficClass());
    }
}
```

### 5.2 External Tools
- **Wireshark**: Full packet analysis
- **tcpdump**: CLI packet capture
- **netstat / ss**: Connection state monitoring
- **iperf3**: Throughput testing
- **mtr**: Combined traceroute/ping
- **nmap**: Port scanning and service detection

## 6. Debugging Workflow

```
Problem: Application is slow or unresponsive
   │
   ├─> 1. Is the connection established?
   │     netstat -an | grep :PORT
   │     Check for ESTABLISHED or SYN_SENT
   │
   ├─> 2. Is there packet loss?
   │     tcpdump -i any port PORT
   │     Look for retransmissions, duplicate ACKs
   │
   ├─> 3. Is Nagle causing delay?
   │     Check TCP_NODELAY setting
   │     Measure write latency with/without
   │
   ├─> 4. Is buffer size adequate?
   │     Check SO_RCVBUF, SO_SNDBUF
   │     Calculate BDP
   │
   ├─> 5. Is there congestion?
   │     Check cwnd, ssthresh, RTT
   │     Look for window saturation
   │
   └─> 6. Application-level issues?
       Check thread pool, blocking I/O
       Verify socket close/cleanup
```

## 7. Logging Best Practices

```java
// Structured logging for network events
public class NetworkEventLogger {
    private static final Logger logger = Logger.getLogger("Network");

    public static void logConnectionEvent(String event, Socket socket) {
        logger.info(String.format("%s | local=%s remote=%s",
            event,
            socket.getLocalSocketAddress(),
            socket.getRemoteSocketAddress()));
    }

    public static void logTcpState(TcpState state, String event) {
        logger.fine(String.format("TCP_STATE | state=%s event=%s", state, event));
    }

    public static void logCongestionEvent(String algo, int cwnd, int ssthresh) {
        logger.fine(String.format("CONGESTION | algo=%s cwnd=%d ssthresh=%d",
            algo, cwnd, ssthresh));
    }
}
```
