# TCP/UDP Deep Dive — Architecture

## 1. System Architecture Overview

The TCP/UDP lab demonstrates transport layer concepts through a layered architecture:

```
┌──────────────────────────────────────┐
│         Application Layer            │
│  (File transfer, Chat, Streaming)    │
├──────────────────────────────────────┤
│      Reliable UDP Layer (R-UDP)      │
│  (Seq numbers, ACKs, Retransmit)     │
├──────────────────────────────────────┤
│     Transport Layer Simulation       │
│  (TCP State Machine, Congestion Ctrl)│
├──────────────────────────────────────┤
│    Java Socket API (UDP/TCP)         │
├──────────────────────────────────────┤
│    OS Kernel Network Stack           │
│  (TCP/IP stack, NIC drivers)         │
└──────────────────────────────────────┘
```

## 2. Component Diagram

```
┌──────────────┐  Events   ┌──────────────────┐
│  Application  │──────────>│  TCP Connection  │
│  (User Input) │<──────────│  (State Machine) │
└──────────────┘  Output    └────────┬─────────┘
                                     │
                                     │ Data
                                     v
┌─────────────────────────────────────────────┐
│          Congestion Controller               │
│  ┌─────────┐  ┌──────────┐  ┌───────────┐  │
│  │  Slow   │  │ Cong.    │  │   Fast    │  │
│  │  Start  │─>│ Avoid    │─>│ Recovery  │  │
│  └─────────┘  └──────────┘  └───────────┘  │
│         │            │            │          │
│         v            v            v          │
│  ┌───────────────────────────────────────┐  │
│  │      Window Manager (cwnd, ssthresh)  │  │
│  └───────────────────────────────────────┘  │
└─────────────────────────────────────────────┘
                     │
                     │ Segments
                     v
┌─────────────────────────────────────────────┐
│         Segment Dispatcher                   │
│  ┌────────────────┐  ┌──────────────────┐   │
│  │  TCP Segment   │  │   UDP Datagram   │   │
│  │  (with Nagle)  │  │   (no Nagle)     │   │
│  └────────────────┘  └──────────────────┘   │
└─────────────────────────────────────────────┘
```

## 3. Reliable UDP Architecture

The R-UDP layer converts unreliable datagrams into a reliable stream:

```
┌──────────────┐     ┌─────────────────┐     ┌──────────────┐
│  Sender App  │────>│  Sequence Gen    │────>│   Sender     │
│              │<────│  + Timeout Timer │<────│   Buffer     │
└──────────────┘     └─────────────────┘     └──────┬───────┘
                                                     │
                                                     v
┌──────────────┐     ┌─────────────────┐     ┌──────────────┐
│  Receiver    │<────│  Reorder +      │<────│   Network    │
│  App         │     │  Checksum Verify│     │  (UDP/IP)    │
└──────────────┘     └─────────────────┘     └──────────────┘
```

## 4. Nagle's Algorithm Placement

Nagle operates in the TCP layer, not the application layer:

```
Application writes
       │
       │ 1 byte
       v
┌─────────────────┐
│  TCP Send Buffer │
│  (Small data)    │
└────────┬─────────┘
         │
         ▼
┌─────────────────┐
│  Nagle Check     │
│  if size >= MSS: │──> Send immediately
│  if no unacked:  │──> Send immediately
│  else:           │──> Buffer until ACK
└─────────────────┘
```

## 5. Socket Buffer Architecture

```
┌─────────────────────────────────────┐
│          Application                 │
│  write()              read()        │
└──┬──────────────────────────────▲───┘
   │                              │
┌──▼──────────────────────────────┴───┐
│  SO_SNDBUF            SO_RCVBUF     │
│  (Send Buffer)        (Recv Buffer) │
│  ┌──────────────┐  ┌──────────────┐│
│  │ Pending      │  │ Received     ││
│  │ Data         │  │ Data (waiting││
│  │ (unacked)    │  │ for read())  ││
│  └──────────────┘  └──────────────┘│
└──┬──────────────────────────────▲───┘
   │                              │
┌──▼──────────────────────────────┴───┐
│      Network Layer (IP)              │
└─────────────────────────────────────┘
```

## 6. Multicast Architecture

```
┌──────────────────────────────────────┐
│         Multicast Group              │
│         239.0.0.1:5000              │
└──────────────────────────────────────┘
        ▲              ▲              ▲
        │              │              │
┌───────┴──┐    ┌──────┴───┐   ┌──────┴───┐
│ Sender   │    │ Receiver1│   │ Receiver2│
│ (socket) │    │ (socket) │   │ (socket) │
└──────────┘    └──────────┘   └──────────┘
```

Multicast uses Class D IP addresses (224.0.0.0 to 239.255.255.255). Receivers join the group via IGMP, and the network replicates packets to all subscribers.

## 7. Design Decisions

### Why R-UDP Instead of TCP?
- Full control over reliability semantics
- Can implement custom retransmission strategies
- Useful for real-time applications where some loss is acceptable
- Avoids TCP head-of-line blocking

### Why Multiple Congestion Control Variants?
- Different environments need different algorithms
- Reno: works well for low-BDP networks
- Cubic: optimized for high-BDP, long-RTT
- BBR: model-based, avoids bufferbloat

### Nagle's Impact
- Increases throughput by reducing packet count
- Increases latency for interactive apps
- Should be disabled for real-time applications

## 8. Performance Considerations

- **Buffer sizing**: Set SO_RCVBUF/SO_SNDBUF to at least 2× BDP
- **Nagle + Delayed ACK**: Can cause 500ms delays (Nagling + delayed ACK interaction)
- **MSS clamping**: Path MTU discovery prevents fragmentation
- **Window scaling**: Required for high-throughput connections
- **TCP Fast Open**: Reduces 1 RTT on subsequent connections
