# TCP/UDP Deep Dive — Interview Questions

## Beginner Level

### Q1: What is the difference between TCP and UDP?

**Answer:** TCP is connection-oriented, provides reliable delivery, in-order data delivery, flow control, and congestion control. UDP is connectionless, provides best-effort delivery, no ordering guarantee, and minimal overhead. TCP is used where reliability matters (web, email, file transfer). UDP is used where speed matters (streaming, gaming, DNS).

### Q2: What happens during the TCP three-way handshake?

**Answer:**
1. Client sends SYN with initial sequence number (ISN)
2. Server responds with SYN+ACK, acknowledging client's ISN and sending its own ISN
3. Client sends ACK, acknowledging server's ISN

After this, both sides can exchange data.

### Q3: What is the purpose of port numbers?

**Answer:** Port numbers identify applications on a host. The combination of (source IP, source port, destination IP, destination port) uniquely identifies a TCP or UDP connection. Ports 0-1023 are well-known (system ports), 1024-49151 are registered, and 49152-65535 are dynamic/ephemeral.

### Q4: What is Nagle's algorithm?

**Answer:** Nagle's algorithm improves TCP efficiency by coalescing small writes into larger packets. It delays sending when there's unacknowledged data and the write is smaller than MSS. Disabled via TCP_NODELAY. It trades off latency for throughput.

## Intermediate Level

### Q5: Explain TCP congestion control phases.

**Answer:**
1. **Slow start**: Doubles cwnd every RTT until ssthresh
2. **Congestion avoidance**: Adds 1 MSS per RTT (linear increase)
3. **Fast retransmit**: Triple duplicate ACK triggers retransmit without timeout
4. **Fast recovery**: Halves cwnd, continues in congestion avoidance

### Q6: What is the Bandwidth-Delay Product (BDP)?

**Answer:** BDP = Bandwidth × RTT. It represents the amount of data that can be in flight in the network. If the TCP window is smaller than BDP, the connection cannot fully utilize the available bandwidth. If the window is larger than BDP, it can cause bufferbloat.

### Q7: How does TCP flow control work?

**Answer:** The receiver advertises a window size in each ACK, indicating how many bytes it can accept. The sender must not exceed this window (receiver's available buffer space). If the window is 0, the sender stops and periodically probes (zero-window probe) until the receiver advertises a non-zero window.

### Q8: What is the difference between TCP and UDP checksum?

**Answer:** TCP checksum is mandatory. UDP checksum is optional in IPv4 (but strongly recommended) and mandatory in IPv6. Both cover the header, pseudo-header (including src/dest IPs), and data. The pseudo-header provides protection against misrouted packets.

### Q9: What causes TIME_WAIT state and why does it exist?

**Answer:** TIME_WAIT occurs after the active closer sends the final ACK in connection termination. It lasts 2×MSL (typically 60 seconds). It exists because:
1. The final ACK might be lost and needs to be retransmitted
2. Ensures old segments from this connection don't interfere with a new connection using the same 4-tuple

### Q10: What is the difference between TCP Reno and TCP Cubic?

**Answer:** Reno uses linear increase (AIMD), which works well for low-BDP networks but is too conservative for high-BDP networks (takes too long to recover after loss). Cubic uses a cubic growth function that is flat near the previous loss point (cautious) and aggressively grows away from it (exploring). Cubic is better for high-speed, long-distance networks.

## Advanced Level

### Q11: How does TCP BBR differ from loss-based congestion control?

**Answer:** Loss-based algorithms (Reno, Cubic) use packet loss as a congestion signal, which causes bufferbloat (they fill buffers until loss occurs). BBR is model-based: it estimates bottleneck bandwidth and round-trip propagation time, then paces at the estimated rate. BBR:
- Doesn't rely on loss (works on lossy links too)
- Avoids bufferbloat (keeps queues small)
- Achieves higher throughput on paths with shallow buffers
- Has 4 phases: STARTUP, DRAIN, PROBE_BW, PROBE_RTT

### Q12: How would you implement reliable delivery over UDP?

**Answer:** Key components:
1. **Sequence numbers**: Each datagram has a monotonically increasing ID
2. **ACKs**: Receiver acknowledges received sequence numbers (cumulative or selective)
3. **Retransmission timer**: Sender retransmits unacknowledged data after timeout
4. **Reorder buffer**: Receiver stores out-of-order packets until gaps fill
5. **Checksums**: Application-level integrity check
6. **Duplicate detection**: Discard packets with already-processed sequence numbers

Optional: window-based flow control, congestion control, selective ACKs.

### Q13: Explain the interaction between Nagle's algorithm and delayed ACKs.

**Answer:** This creates a potential deadlock:
1. Nagle holds small data waiting for an ACK
2. Delayed ACK holds the ACK waiting for more data
3. Neither can proceed until a timeout fires

The default delayed ACK timer is 500ms (RFC 1122), so this interaction can cause up to 500ms delays on small writes. The fix is to disable Nagle on the sender (TCP_NODELAY) when application sends small messages.

### Q14: How does TCP handle packet loss?

**Answer:** Two mechanisms:
1. **RTO (Retransmission Timeout)**: If ACK doesn't arrive within SRTT + 4×RTTVAR, retransmit and reset cwnd to initial window
2. **Fast retransmit**: Three duplicate ACKs trigger retransmission without waiting for timeout, preserving throughput

SACK (Selective ACK) allows the receiver to specify exactly which segments are missing, avoiding unnecessary retransmissions.

### Q15: Design a protocol for reliable multicast over UDP.

**Answer:** Approaches:
1. **NACK-based (SRM)**: Receivers send negative ACKs for missing packets. Sender retransmits on NACK. Scalable (one NACK per loss, not per received packet).
2. **FEC-based**: Add error correction codes so loss can be recovered without retransmission. Pro: no feedback channel needed. Con: overhead.
3. **Hierarchical**: Organize receivers in a tree. Local retransmission at each level reduces latency and avoids NACK implosion.
4. **PGM (Pragmatic General Multicast)**: Standard protocol with source-based retransmission and NACK suppression.

### Q16: How would you tune TCP for a data center network?

**Answer:** Data centers have unique characteristics: low RTT (< 1ms), high bandwidth, shallow buffers, incast patterns.

Recommended tuning:
- DCTCP (Data Center TCP) or similar algorithm
- Small initial windows
- ECN for early congestion notification
- Reduced retransmission timers
- Jumbo frames (9000 MTU) to reduce overhead
- Large socket buffers for throughput
- RFS/RPS to distribute packets across cores

### Q17: What are the limitations of TCP for real-time communication?

**Answer:**
1. **Head-of-line blocking**: A lost segment blocks delivery of all subsequent data
2. **Slow start**: Initial throughput is artificially limited
3. **Retransmission delay**: Lost data causes playback stutter
4. **Connection setup**: 1 RTT minimum before data flows (even with Fast Open)
5. **Nagle + Delayed ACK**: Up to 500ms additional latency
6. **Kernel dependency**: Hard to optimize; changes require OS updates

These limitations motivated QUIC (HTTP/3) which runs over UDP with TCP-like reliability implemented in userspace.

### Q18: Explain how TCP Fast Open (TFO) works.

**Answer:** TFO allows data in the SYN packet for repeat connections:
1. First connection: standard 3-way handshake, server sends TFO cookie
2. Subsequent connections: client includes TFO cookie and data in first SYN
3. If cookie is valid, server processes data immediately (0-RTT)
4. If cookie is invalid (e.g., after server restart), falls back to normal handshake

Benefit: Eliminates 1 RTT for repeat connections to a server.

### Q19: What is the difference between SO_REUSEADDR and SO_REUSEPORT?

**Answer:**
- **SO_REUSEADDR**: Allows binding to a port in TIME_WAIT state. Useful for immediate server restart. Also allows multiple processes to bind to the same port if they use different IPs.
- **SO_REUSEPORT** (Linux 3.9+): Allows multiple processes/threads to bind to the exact same IP:port for load balancing. The kernel distributes incoming connections among the listeners.

SO_REUSEPORT enables effective per-CPU accept() loops for high-throughput servers.

### Q20: How would you identify TCP issues in a production system?

**Answer:**
1. **Check connection states**: `ss -tan` shows SYN_SENT (outbound connection not established), TIME_WAIT (many indicates short-lived connections)
2. **Check retransmits**: `netstat -s -t | grep -i retrans` — if > 0.1% of segments are retransmitted, there's a problem
3. **Check buffer drops**: `netstat -s | grep -i "buffer full"` indicates socket buffer overflow
4. **Check congestion window**: `ss -i dst <server>` shows cwnd — if cwnd is consistently low, there's congestion
5. **Capture packets**: tcpdump / Wireshark for detailed analysis
6. **Application metrics**: Monitor connection pool utilization, thread pool health, and request latency distribution
