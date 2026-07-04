# HTTP Protocol - Mathematical Foundation

## Latency Calculations

### Total Request Time
T_total = T_dns + T_tcp + T_tls + T_ttfb + T_download

### HTTP/1.1 with N resources (without pipelining)
T_1_1 = (N * RTT) + (N * T_tls) + T_content

### HTTP/2 with multiplexing
T_2 = RTT + T_tls + T_content

### HTTP/3 (QUIC) 0-RTT
T_3 = T_content + (0 or 1 RTT)

## Connection Pool Math

### Optimal Pool Size
PoolSize = Throughput_per_conn * Latency * Connection_overhead

```java
public class ConnectionPoolSize {
    public static int calculateOptimalPoolSize(
            double requestsPerSecond,
            double avgLatencySeconds,
            double connectionOverhead) {
        return (int) Math.ceil(requestsPerSecond *
               avgLatencySeconds * connectionOverhead);
    }
}
```

## TCP Congestion Window Impact

cwnd doubles every RTT during slow start:
- Initial: 10 segments (~14KB)
- After 1 RTT: 20 segments
- After 2 RTT: 40 segments
- After N RTT: 10 * 2^N segments
