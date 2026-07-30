# GUIDE — Network Observability

## Step 1: Flow Log Model
```java
public record FlowLogEntry(String srcIp, String dstIp, int srcPort, int dstPort, String protocol, long packets, long bytes, Instant start, Instant end, String action) {}
```

## Step 2: Flow Log Aggregator
- Aggregate by 5-tuple
- Calculate throughput and packet rate
- Detect anomalies in flow patterns

## Step 3: Packet Capture Engine
```java
public record PacketData(byte[] raw, int length, long timestamp, String srcMac, String dstMac) {}
PacketCapture capture = new PacketCapture();
capture.sniff(interfaceName, filter);
```

## Step 4: eBPF-Based Monitoring
```java
public record EbpfMap(byte[] key, byte[] value) {}
EbpfProgram program = new EbpfProgram("tc_ingress");
program.attach(interfaceName);
```

## Step 5: Network Metrics Pipeline
- Latency: RTT measurement via ICMP/TCP
- Throughput: bytes per second
- Packet loss: retransmission detection
- Jitter: inter-packet delay variation

## Step 6: Exercises
1. Implement a flow log analytics pipeline with anomaly detection
2. Build a protocol parser for HTTP/TCP from captured packets
3. Create a distributed network tracing tool using ICMP probes
