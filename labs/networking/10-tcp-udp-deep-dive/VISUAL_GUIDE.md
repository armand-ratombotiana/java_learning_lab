# TCP/UDP Deep Dive — Visual Guide

## 1. TCP 3-Way Handshake

```
CLIENT (Port 54321)                  SERVER (Port 80)
        │                                  │
        │          1. SYN (seq=x)          │
        │ ────────────────────────────────> │
        │                                  │  State: LISTEN → SYN_RCVD
        │     2. SYN+ACK (seq=y, ack=x+1)  │
        │ <──────────────────────────────── │
        │                                  │
        │   3. ACK (seq=x+1, ack=y+1)      │
        │ ────────────────────────────────> │  State: SYN_RCVD → ESTABLISHED
        │                                  │
        │   <== Data Exchange (HTTP) ==>   │
        │                                  │
```

## 2. TCP Connection Termination

```
ACTIVE CLOSER                     PASSIVE CLOSER
        │                                  │
        │  1. FIN (seq=u)                  │
        │ ────────────────────────────────> │  State: ESTABLISHED → CLOSE_WAIT
        │                                  │
        │  2. ACK (ack=u+1)                │
        │ <──────────────────────────────── │  State: FIN_WAIT_1 → FIN_WAIT_2
        │                                  │
        │   (Half-close: server can still send data)
        │                                  │
        │  3. FIN (seq=v)                  │
        │ <──────────────────────────────── │  State: CLOSE_WAIT → LAST_ACK
        │                                  │
        │  4. ACK (ack=v+1)                │
        │ ────────────────────────────────> │  State: LAST_ACK → CLOSED
        │                                  │
        │  State: FIN_WAIT_2 → TIME_WAIT (2MSL)
        │  State: TIME_WAIT → CLOSED       │
```

## 3. TCP Congestion Control Phases

```
cwnd
  ^
  |     Slow Start           Congestion        Loss
  |   (exponential)          Avoidance         Event
  |                        (linear/AIMD)
  |  cwnd *= 2 per RTT    cwnd += 1 per RTT
  |
  |   ┌───┐
  |   │   │───┐                    ssthresh ──────►
  |   │   │   │───┐                    │
  |   │   │   │   │───┐    ┌───┐       │
  |   │   │   │   │   │────│   │───────│────────   Fast Recovery
  |   │   │   │   │   │    │   │       │    │   │  cwnd = ssthresh
  |   │   │   │   │   │    │   │       │    │   │
  +───┴───┴───┴───┴───┴────┴───┴───────┴────┴───┴───► RTT
     1   2   3   4   5   6   7   8   9  10  11  12
```

## 4. Nagle's Algorithm Flow

```
Application sends 1 byte
         │
         ▼
┌─────────────────────┐
│ Is data >= MSS?     │──── YES ──► Send immediately
└─────────┬───────────┘
          │ NO
          ▼
┌─────────────────────┐
│ Any unacknowledged  │
│ data outstanding?   │──── NO ──► Send immediately
└─────────┬───────────┘
          │ YES
          ▼
┌─────────────────────┐
│ Buffer the data     │
│ (wait for ACK       │
│  or more data)      │
└─────────────────────┘
         │
         │ ACK received OR
         │ buffer >= MSS
         ▼
    Send buffered data
```

## 5. UDP vs TCP Comparison

```
TCP:                                     UDP:
┌─────────────────┐                     ┌─────────────────┐
│  Connection     │                     │  Connectionless  │
│  Orienté        │                     │                 │
├─────────────────┤                     ├─────────────────┤
│  Seq=1000       │                     │  No sequence    │
│  Ack=2000       │                     │  numbers        │
│  Window=65535   │                     │                 │
├─────────────────┤                     ├─────────────────┤
│  Reliable       │                     │  Best-effort    │
│  Retransmits    │                     │  No ACKs        │
│  on loss        │                     │                 │
├─────────────────┤                     ├─────────────────┤
│  Ordered        │                     │  No ordering    │
│  (in-order del) │                     │  guarantee      │
├─────────────────┤                     ├─────────────────┤
│  Header: 20-60B │                     │  Header: 8B     │
├─────────────────┤                     ├─────────────────┤
│  Flow control   │                     │  No flow ctrl   │
│  (sliding win)  │                     │                 │
├─────────────────┤                     ├─────────────────┤
│  Use cases:     │                     │  Use cases:     │
│  • HTTP/HTTPS   │                     │  • DNS          │
│  • Email (SMTP) │                     │  • VoIP (RTP)   │
│  • File (FTP)   │                     │  • Streaming    │
│  • SSH          │                     │  • Gaming       │
└─────────────────┘                     └─────────────────┘
```

## 6. Socket Option Effects

```
SO_RCVBUF ↑  ┌─────────────┐
             │ Larger buffer│──► Higher throughput, more memory
             └─────────────┘

SO_SNDBUF ↑  ┌─────────────┐
             │ Larger buffer│──► Can absorb write bursts
             └─────────────┘

TCP_NODELAY  ┌─────────────┐
= true       │ Each write  │──► Lower latency, more packets
             │ sent immedi │
             └─────────────┘

SO_LINGER    ┌─────────────┐
= timeout    │ Wait up to  │──► Ensures data delivery on close
             │ timeout sec │
             └─────────────┘
```

## 7. Bandwidth-Delay Product Visualization

```
BDP = Bandwidth × RTT

                     ◄──── BDP ────►
┌──────────────────────────────────────────┐
│                                          │
│     Data "in flight" in the network      │
│                                          │
└──────────────────────────────────────────┘
         ▲                        ▲
         │                        │
    Sender                    Receiver

If window < BDP:  ░░░░░░░░░░░░░░░░░░░░  Underutilized
If window = BDP:  ████████████████████  Optimal
If window > BDP:  ██████████████████████  Bufferbloat
```

## 8. Reliable UDP Architecture

```
Sender                           Receiver
  │                                │
  │── DATA (seq=1) ──────────────>│
  │                                │ (seq 1 received)
  │<────────── ACK (1) ───────────│
  │── DATA (seq=2) ───┐          │
  │ (packet LOST!)    │           │
  │── DATA (seq=3) ──────────────>│
  │                                │ (seq 3 received, reorder buffer)
  │<────────── ACK (3) ───────────│
  │                                │
  │ (timeout → retransmit)        │
  │── DATA (seq=2) ──────────────>│
  │                                │ (seq 2 received, deliver 2,3)
  │<────────── ACK (3) ───────────│ (cumulative ACK up to 3)
  │                                │
```

## 9. TCP State Machine Diagram

```
         ┌─────────────────────────────────────────────────┐
         │                   CLOSED                         │
         └────┬────────────────────────────────┬───────────┘
              │ PASSIVE OPEN                   │ ACTIVE OPEN
              ▼                                ▼
         ┌────────┐                      ┌──────────┐
         │ LISTEN │                      │ SYN_SENT │
         └───┬────┘                      └────┬─────┘
             │ RECV SYN                      │ RECV SYN+ACK
             ▼                                ▼
         ┌──────────┐                  ┌──────────┐
         │ SYN_RCVD │◄───── SYN ──────│ SYN_SENT │
         └────┬─────┘                  └──────────┘
              │ RECV ACK
              ▼
         ┌─────────────┐
         │ ESTABLISHED │
         └──┬──────┬───┘
            │      │
     CLOSE  │      │  RECV FIN
            ▼      ▼
     ┌──────────┐ ┌───────────┐
     │FIN_WAIT_1│ │CLOSE_WAIT │
     └──┬───────┘ └─────┬─────┘
        │ RECV ACK      │ CLOSE
        ▼               ▼
     ┌──────────┐ ┌──────────┐
     │FIN_WAIT_2│ │ LAST_ACK │
     └──┬───────┘ └─────┬────┘
        │ RECV FIN      │ RECV ACK
        ▼               ▼
     ┌──────────┐  ┌────────┐
     │TIME_WAIT │  │ CLOSED │
     └────┬─────┘  └────────┘
          │ 2MSL expires
          ▼
       ┌────────┐
       │ CLOSED │
       └────────┘
```

This visual guide provides a quick reference for the key concepts in TCP/UDP networking.
