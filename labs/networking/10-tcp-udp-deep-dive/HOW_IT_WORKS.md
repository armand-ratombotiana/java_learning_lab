# TCP/UDP Deep Dive — How It Works

## 1. How TCP Works

### 1.1 The Core Idea
TCP provides a reliable, ordered, error-checked stream of bytes between applications running on hosts connected via an IP network. It achieves this through sequence numbers, acknowledgments, retransmission, and flow control.

### 1.2 Connection Establishment

When application A wants to connect to application B:

1. **Client sends SYN**: A chooses a random initial sequence number (ISN) and sends a TCP segment with the SYN flag set.
2. **Server responds with SYN+ACK**: B chooses its own ISN, acknowledges A's ISN by setting ack = A_ISN + 1, and sends its SYN.
3. **Client ACKs**: A acknowledges B's ISN by sending ack = B_ISN + 1.

After these three messages, both sides know each other's sequence numbers and the connection is ESTABLISHED.

### 1.3 Data Transfer

Once established, data flows as a continuous stream:

```
Sender writes: [A][B][C][D][E][F][G][H]
TCP segments:  [seq=1000, A][seq=1001, B]...[seq=1007, H]
Receiver sees: [A][B][C][D][E][F][G][H]
```

The sender assigns each byte a sequence number. The receiver acknowledges the highest in-order byte received. If a segment is lost, the sender retransmits.

### 1.4 Flow Control

The receiver advertises a **window size** in every segment, indicating how many bytes it's willing to receive. The sender must not exceed this window. This prevents a fast sender from overwhelming a slow receiver.

### 1.5 Congestion Control

The sender maintains a **congestion window (cwnd)** that limits how much data can be in flight. Unlike the receiver window (which reflects receiver capacity), cwnd reflects network capacity.

- **Slow start**: Begin with small cwnd, double each RTT until loss or threshold
- **Congestion avoidance**: After threshold, increase cwnd linearly
- **Fast recovery**: On triple duplicate ACK, halve cwnd and retransmit

## 2. How UDP Works

### 2.1 The Core Idea
UDP provides a minimal, unreliable datagram service. It sends messages (datagrams) from one application to another with no guarantees about delivery, ordering, or duplicate protection.

### 2.2 Sending a Datagram

```
Application writes: "Hello" (5 bytes)
UDP encapsulates:  [UDP Header (8B)][Payload (5B)]
IP encapsulates:   [IP Header (20B)][UDP Header (8B)][Payload (5B)]
On the wire:       Total: 33 bytes
```

The UDP header contains:
- Source port (2 bytes)
- Destination port (2 bytes)
- Length (2 bytes): header + payload = 13
- Checksum (2 bytes): optional for IPv4

### 2.3 Receiving a Datagram

When a UDP packet arrives:
1. IP layer strips IP header
2. UDP layer checks destination port
3. UDP verifies checksum (if present)
4. If a socket is bound to that port, data is queued
5. If no socket, ICMP Port Unreachable is sent

### 2.4 No Connection State

Unlike TCP, a UDP server doesn't maintain per-client state. Each incoming datagram is self-contained. This makes UDP inherently stateless — ideal for request-response protocols like DNS.

## 3. How Nagle's Algorithm Works

Nagle's algorithm solves the "small packet problem" — applications that send data 1 byte at a time create a huge overhead (41:1 ratio for TCP/IP headers vs data on a 1-byte write).

The algorithm works by delaying small writes:

```
Time  Event                          Action
────  ─────────────────────────────  ─────────────────────
T1    App writes 1 byte             Buffer it (has outstanding data)
T2    App writes 1 byte             Buffer it
T3    App writes 1460 bytes         Send all 1462 bytes (>= MSS)
T4    ACK for previous data arrives Send any remaining buffered data
```

The key insight: if there's already unacknowledged data in flight, coalesce new writes with existing buffered data before sending.

## 4. How Socket Internals Work

### 4.1 The Socket File Descriptor

In Unix/Linux, a socket is a file descriptor (an integer) that references an in-kernel data structure:

```
File Descriptor Table (per process)
┌─────┬────────────────────────────┐
│  0  │ stdin                      │
│  1  │ stdout                     │
│  2  │ stderr                     │
│  3  │ ──► Socket structure       │
└─────┴────────────────────────────┘
           │
           ▼
    Socket Structure (kernel)
    ┌──────────────────────┐
    │  State: ESTABLISHED  │
    │  Type: SOCK_STREAM   │
    │  Protocol: IPPROTO_TCP│
    │  Send Buffer          │
    │  Receive Buffer       │
    │  Congestion state     │
    │  Ops (send, recv...) │
    └──────────────────────┘
```

### 4.2 Socket Buffers

Each TCP socket has two kernel-managed buffers:

**Send Buffer (SO_SNDBUF)**:
- Contains data written but not yet acknowledged
- Kernel copies from user space to kernel space on write()
- Data stays until ACKed by receiver
- Default: typically 16KB-128KB, tunable up to several MB

**Receive Buffer (SO_RCVBUF)**:
- Contains data received but not yet read by the application
- Kernel copies from network to kernel space on receive
- Data stays until application calls read()
- Advertised window = available space in receive buffer

### 4.3 Blocking vs Non-blocking

**Blocking mode (default)**:
- read() blocks until data is available
- write() blocks if buffer is full
- Simple programming model

**Non-blocking mode**:
- read() returns immediately (-1 / EAGAIN if no data)
- write() returns immediately (partial write if buffer full)
- Used with select/poll/epoll for multiplexing

## 5. How TCP Congestion Control Works

### 5.1 The Problem
Multiple TCP connections share network links. Without coordination, they could overload routers, causing packet loss and congestion collapse.

### 5.2 The Solution
Each sender independently probes the network for available bandwidth and adjusts its rate.

**Reno-style AIMD**:
```
Increase: cwnd += MSS  (per RTT, additive)
Decrease: cwnd /= 2   (on loss, multiplicative)
```

This converges to fairness: n connections with same RTT get equal share.

### 5.3 BBR: A Different Approach

Instead of using loss as a congestion signal, BBR:
1. Estimates bottleneck bandwidth (delivery rate)
2. Estimates minimum RTT (propagation delay)
3. Sets pacing rate = bandwidth × gain factor
4. Cycles through probe phases to find optimal rate

BBR doesn't fill buffers (no bufferbloat) because it paces at exactly the bottleneck rate.

## 6. How Reliable UDP Works

Since UDP provides no reliability, we must add it ourselves:

1. **Sequence numbers**: Each datagram carries a monotonically increasing number
2. **ACK packets**: Receiver acknowledges each (or ranges of) sequence numbers
3. **Timeout + Retransmit**: Sender maintains a timer; if no ACK arrives, resend
4. **Reorder buffer**: Receiver buffers out-of-order packets until gaps fill
5. **Duplicate detection**: Receiver drops packets with already-processed seq numbers

This is essentially reimplementing TCP's reliability in userspace, with the advantage that we can customize the behavior (e.g., different retransmission strategies for real-time vs bulk data).

## Summary

TCP provides a reliable pipe through sequence/ACK semantics, flow control via sliding windows, and congestion control via AIMD. UDP provides a simple datagram service with minimal overhead. Understanding how these protocols work internally enables engineers to make informed design choices and debug complex network issues.
