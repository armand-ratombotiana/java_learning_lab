# TCP/UDP Deep Dive — Performance

## 1. Key Performance Metrics

### 1.1 Throughput
The rate at which data is successfully delivered.

**TCP Throughput Formula:**
```
Throughput = min(WindowSize / RTT, Bandwidth)
```

**Maximum theoretical throughput for different configurations:**

| Window Size | RTT | Max Throughput |
|-------------|-----|----------------|
| 64 KB | 10 ms | 51.2 Mbps |
| 64 KB | 100 ms | 5.12 Mbps |
| 256 KB | 10 ms | 204.8 Mbps |
| 1 MB | 100 ms | 80 Mbps |
| 16 MB | 10 ms | 12.8 Gbps |

### 1.2 Latency Components
```
Total Latency = Processing + Queuing + Transmission + Propagation
```

- **Processing delay**: Time to process packet (typically microseconds)
- **Queuing delay**: Time waiting in buffers (highly variable)
- **Transmission delay**: Packet size / link bandwidth
- **Propagation delay**: Distance / speed of light in medium

### 1.3 Jitter
The variation in latency. Critical for real-time applications.

Classification:
- Low jitter: < 5ms (good for VoIP)
- Moderate: 5-20ms (acceptable for streaming)
- High: > 20ms (noticeable degradation)

### 1.4 Packet Loss
Percentage of packets that don't reach destination.

| Loss Rate | TCP Impact | UDP Impact |
|-----------|------------|------------|
| 0-0.1% | Minimal | No impact (application dependent) |
| 0.1-1% | Noticeable throughput reduction | Acceptable for most apps |
| 1-5% | Severe throughput reduction | Noticeable quality loss |
| > 5% | Connection may stall | Service degradation |

## 2. TCP Performance Tuning

### 2.1 Kernel Parameters (Linux)
```
# Maximum receive buffer size
net.core.rmem_max = 16777216

# Maximum send buffer size
net.core.wmem_max = 16777216

# TCP receive buffer (min, default, max)
net.ipv4.tcp_rmem = 4096 131072 16777216

# TCP send buffer (min, default, max)
net.ipv4.tcp_wmem = 4096 65536 16777216

# Enable window scaling
net.ipv4.tcp_window_scaling = 1

# Enable TCP Fast Open
net.ipv4.tcp_fastopen = 3

# Enable BBR congestion control
net.ipv4.tcp_congestion_control = bbr

# Backlog for SYN queue
net.ipv4.tcp_max_syn_backlog = 4096

# Enable TCP timestamps
net.ipv4.tcp_timestamps = 1
```

### 2.2 Java Socket Tuning

```java
public class TcpTuner {
    public static void tuneForThroughput(Socket socket) throws SocketException {
        // Large buffers for high BDP
        socket.setReceiveBufferSize(2 * 1024 * 1024);  // 2MB
        socket.setSendBufferSize(2 * 1024 * 1024);     // 2MB

        // Enable Nagle for throughput (coalesces small writes)
        socket.setTcpNoDelay(false);

        // Prefer low latency over bandwidth for interactive apps
        socket.setPerformancePreferences(0, 2, 1);
    }

    public static void tuneForLowLatency(Socket socket) throws SocketException {
        // Small buffers to keep data moving
        socket.setReceiveBufferSize(65536);
        socket.setSendBufferSize(65536);

        // Disable Nagle for immediate sends
        socket.setTcpNoDelay(true);

        // Prefer low latency over bandwidth
        socket.setPerformancePreferences(2, 0, 1);
    }
}
```

## 3. Benchmarks

### 3.1 TCP vs UDP Throughput

```java
public class ThroughputBenchmark {
    public static void main(String[] args) throws Exception {
        int dataSize = 100_000_000; // 100 MB
        int packetSize = 1400;

        // TCP benchmark
        long tcpTime = measureTcpThroughput(dataSize, packetSize);
        double tcpMbps = (dataSize * 8.0) / (tcpTime / 1_000_000_000.0) / 1_000_000;
        System.out.printf("TCP throughput: %.2f Mbps%n", tcpMbps);

        // UDP benchmark
        long udpTime = measureUdpThroughput(dataSize, packetSize);
        double udpMbps = (dataSize * 8.0) / (udpTime / 1_000_000_000.0) / 1_000_000;
        System.out.printf("UDP throughput: %.2f Mbps%n", udpMbps);
    }
}
```

### 3.2 Nagle vs No-Nagle Latency

| Scenario | Nagle Enabled | Nagle Disabled |
|----------|---------------|----------------|
| 1-byte writes, 1ms RTT | 500μs avg | 50μs avg |
| 1KB writes, 10ms RTT | 10ms | 10ms |
| 1-byte writes, 100ms RTT | 50ms avg | 100μs avg |
| 64KB bulk transfer | Identical | Identical |

### 3.3 Congestion Control Algorithm Comparison

| Algorithm | Throughput (100ms RTT, 1% loss) | Fairness | Bufferbloat Resistance |
|-----------|---------------------------------|----------|----------------------|
| Reno | 2.1 Mbps | Good | Poor |
| Cubic | 8.5 Mbps | Good | Poor |
| BBR | 12.3 Mbps | Fair | Excellent |
| Vegas | 9.7 Mbps | Excellent | Excellent |

## 4. Performance Anti-Patterns

### 4.1 The Nagle + Delayed ACK Deadlock
When both Nagle (waits for ACK) and Delayed ACK (waits for more data) are active, the connection can stall for up to 500ms.

**Symptom**: Interactive applications feel sluggish
**Fix**: `socket.setTcpNoDelay(true)` on client

### 4.2 TinyGram Syndrome
Sending many small UDP packets instead of batching.

**Impact**: High packet rate overwhelms NIC interrupts
**Fix**: Batch small messages into larger datagrams

### 4.3 Bufferbloat
Overly large buffers in network devices cause high latency under load.

**Detection**: 
```
Base RTT (idle) vs Loaded RTT >> 100ms suggests bufferbloat
```

**Fix**: 
- Use BBR or CoDel for AQM
- Limit socket buffer to 2× BDP
- Enable fq_codel qdisc on Linux

### 4.4 Connection Reuse
Creating a new TCP connection for every request adds RTT overhead.

**Fix**: 
- Connection pooling (Keep-Alive)
- HTTP/2 multiplexing
- TCP Fast Open

## 5. Monitoring Commands

```bash
# Check TCP connection states
netstat -s -t

# Monitor congestion control events
ss -i

# Check buffer sizes
ss -m

# Monitor retransmissions
netstat -s | grep -i retrans

# Check window scaling negotiation
tcpdump -i eth0 'tcp flags & (tcp-syn) != 0'

# Monitor RTT
ping -c 100 <host>

# Path MTU discovery
traceroute --mtu <host>
```

## 6. Performance Checklist

- [ ] Socket buffers sized for BDP (at least 2×)
- [ ] TCP_NODELAY set for latency-sensitive apps
- [ ] Window scaling enabled (default on modern kernels)
- [ ] Appropriate congestion control algorithm selected
- [ ] Nagle + Delayed ACK interaction considered
- [ ] Connection pooling in use
- [ ] TCP Fast Open enabled
- [ ] Bufferbloat assessed and mitigated
- [ ] Jitter measured for real-time applications
- [ ] MTU path discovery working (no fragmentation)
