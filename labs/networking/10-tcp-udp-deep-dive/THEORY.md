# TCP/UDP Deep Dive — Theory

## 1. TCP Fundamentals

### 1.1 The TCP Header
TCP segments contain a 20-byte header (minimum) with the following fields:

- **Source Port (16 bits)**: Identifies the sending application
- **Destination Port (16 bits)**: Identifies the receiving application
- **Sequence Number (32 bits)**: Byte offset of the first data byte in this segment
- **Acknowledgment Number (32 bits)**: Next expected byte sequence number (ACK flag set)
- **Data Offset (4 bits)**: Header length in 32-bit words
- **Flags (9 bits)**: URG, ACK, PSH, RST, SYN, FIN, plus CWR, ECE, NS
- **Window Size (16 bits)**: Number of bytes the receiver is willing to accept
- **Checksum (16 bits)**: Error detection for header and data
- **Urgent Pointer (16 bits)**: Points to urgent data (URG flag set)
- **Options (variable)**: MSS, window scaling, SACK, timestamps, etc.

### 1.2 Connection Establishment (3-Way Handshake)

```
CLIENT                      SERVER
  |                           |
  |---- SYN (seq=x) -------->|  LISTEN
  |                           |  SYN_RCVD
  |<-- SYN+ACK (seq=y,ack=x+1)|
  |                           |
  |---- ACK (seq=x+1,ack=y+1)>|  ESTABLISHED
  |                           |
  |<==== Data Exchange ======>|
```

The three-way handshake establishes initial sequence numbers and negotiates options like MSS and window scaling.

### 1.3 Connection Termination (Four-Way Handshake)

```
CLIENT                      SERVER
  |                           |
  |---- FIN (seq=u) -------->|  CLOSE_WAIT
  |                           |
  |<-- ACK (ack=u+1) --------|  (half-close)
  |                           |
  | (data can still flow server→client)
  |                           |
  |<-- FIN (seq=v) ----------|  LAST_ACK
  |                           |
  |---- ACK (ack=v+1) ------>|  CLOSED
```

### 1.4 TCP State Machine

The TCP state machine has 11 states:
1. **CLOSED** — No connection
2. **LISTEN** — Waiting for incoming connection
3. **SYN_SENT** — Sent SYN, waiting for SYN+ACK
4. **SYN_RCVD** — Received SYN, sent SYN+ACK
5. **ESTABLISHED** — Connection established, data can flow
6. **FIN_WAIT_1** — Sent FIN, waiting for ACK or FIN
7. **FIN_WAIT_2** — Received ACK to FIN, waiting for FIN
8. **CLOSE_WAIT** — Received FIN, sent ACK, waiting for application close
9. **CLOSING** — Received FIN while in FIN_WAIT_1 (simultaneous close)
10. **LAST_ACK** — Sent FIN in CLOSE_WAIT, waiting for ACK
11. **TIME_WAIT** — Received ACK to FIN, waiting 2MSL before CLOSED

## 2. Congestion Control

### 2.1 Slow Start
- Begins with cwnd = 1 MSS (or 10 MSS in modern TCP)
- Exponential growth: cwnd doubles every RTT
- Until ssthresh (slow start threshold) is reached

### 2.2 Congestion Avoidance
- After ssthresh, linear growth: cwnd += 1 MSS per RTT
- AIMD (Additive Increase Multiplicative Decrease) principle

### 2.3 Fast Retransmit and Fast Recovery
- Triple duplicate ACK triggers retransmission
- ssthresh = cwnd / 2
- cwnd = ssthresh (enters congestion avoidance)

### 2.4 TCP Variants

| Variant | Key Feature | Use Case |
|---------|-------------|----------|
| TCP Reno | AIMD, fast recovery | General purpose |
| TCP NewReno | Improved fast recovery | Better loss recovery |
| TCP Cubic | Cubic growth function | High-bandwidth, long-RTT |
| TCP BBR | Model-based (not loss-based) | Modern internet |
| TCP Vegas | Delay-based congestion | Low-latency environments |

### 2.5 Nagle's Algorithm
- Coalesces small packets into larger ones
- Sends only if: data >= MSS **or** no outstanding ACKs
- Problematic for interactive/telnet applications
- Disabled via `TCP_NODELAY` socket option

## 3. UDP Fundamentals

### 3.1 UDP Header (8 bytes)
- **Source Port (16 bits)**
- **Destination Port (16 bits)**
- **Length (16 bits)**: Header + data length
- **Checksum (16 bits)**: Optional in IPv4, mandatory in IPv6

### 3.2 Key Differences from TCP

| Feature | TCP | UDP |
|---------|-----|-----|
| Connection | Connection-oriented | Connectionless |
| Reliability | Guaranteed delivery | Best-effort |
| Ordering | Preserves order | No ordering |
| Flow Control | Sliding window, congestion control | None |
| Header Size | 20-60 bytes | 8 bytes |
| Use Cases | Web, email, file transfer | DNS, VoIP, streaming, gaming |

### 3.3 UDP Multicast
- One-to-many delivery using multicast groups
- IGMP for group membership management
- Applications: streaming media, real-time data feeds

## 4. Socket Internals

### 4.1 Socket Buffer Sizes
- **SO_RCVBUF**: Receive buffer size (default ~64KB, can be tuned)
- **SO_SNDBUF**: Send buffer size (default ~64KB)
- Window scale option enables windows up to 1GB

### 4.2 TCP_NODELAY
- Disables Nagle's algorithm
- Critical for low-latency applications
- Can cause small packet proliferation if misused

### 4.3 SO_LINGER
- Controls behavior on close with pending data
- `SO_LINGER with 0`: Send RST on close
- `SO_LINGER with timeout`: Block until data delivered or timeout

### 4.4 TCP_QUICKACK (Linux)
- Sends ACK immediately instead of delaying
- Reduces latency at cost of more ACK packets

## 5. Reliable UDP

Building reliability on top of UDP requires:
- Sequence numbers for ordering
- Acknowledgments (ACKs) for delivery confirmation
- Timeouts and retransmissions for lost packets
- Flow control to prevent overwhelming receiver
- Checksums for integrity verification

This is essentially implementing TCP at the application layer, giving full control over the reliability mechanism.

## Summary
TCP and UDP represent the two fundamental transport layer approaches. TCP provides reliability at the cost of latency and overhead; UDP provides speed at the cost of reliability. Understanding their internals is essential for building high-performance networked applications.
