# TCP/UDP Deep Dive — Internals

## 1. TCP Implementation in the Linux Kernel

### 1.1 TCP Receive Path (Data Flow)

```
NIC Interrupt
    │
    ▼
┌────────────────┐
│  NAPI Polling   │  ──> net_rx_action()
└───────┬────────┘
        │ skb
        ▼
┌────────────────┐
│  ip_local_      │  ──> ip_local_deliver_finish()
│  deliver()      │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  tcp_v4_rcv()   │  ──> Find socket in established hash table
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  tcp_rcv_       │  ──> Process flags, sequence numbers
│  established()  │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  tcp_data_      │  ──> Queue data to receive buffer
│  queue()        │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  tcp_ack()      │  ──> Process ACKs, update cwnd
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  sk_data_ready()│  ──> Wake up user-space recv()
└────────────────┘
```

### 1.2 TCP Send Path

```
Application write()
    │
    ▼
┌────────────────┐
│  tcp_sendmsg()  │  ──> Build skbs from user data
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  tcp_push()     │  ──> Nagle check, push logic
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  tcp_transmit_  │  ──> Build TCP header, calculate checksum
│  skb()          │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  ip_queue_xmit()│  ──> Route lookup, IP header
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  dev_queue_xmit()│  ──> Queue to NIC driver
└────────────────┘
```

### 1.3 TCP Timer Management

The kernel manages five timers per connection:

1. **Retransmit Timer (RTO)**: Triggers retransmission of unacked segments
2. **Delayed ACK Timer**: Sends delayed ACK (up to 500ms)
3. **Keepalive Timer**: Probes idle connections (default 2 hours)
4. **TIME_WAIT Timer**: Cleans up TIME_WAIT state (60s = 2MSL)
5. **Zero-Window Probe Timer**: Probes when window is 0

## 2. TCP Congestion Control Internals

### 2.1 Congestion Window (cwnd) Management

```c
// Linux kernel: tcp_input.c
static void tcp_cong_avoid(struct sock *sk, u32 ack, u32 acked) {
    struct tcp_sock *tp = tcp_sk(sk);

    if (!tcp_is_cwnd_limited(sk))
        return;

    if (tcp_in_slow_start(tp)) {
        // Slow start: exponential growth
        tp->snd_cwnd = min(tp->snd_cwnd + acked, tp->snd_ssthresh);
    } else {
        // Congestion avoidance: AIMD
        if (tcp_congestion_release(sk))
            tp->snd_cwnd_cnt += acked;
        if (tp->snd_cwnd_cnt >= tp->snd_cwnd) {
            tp->snd_cwnd_cnt -= tp->snd_cwnd;
            tp->snd_cwnd++;
        }
    }
}
```

### 2.2 TCP Cubic Growth Function

```c
// Linux kernel: tcp_cubic.c
static inline void bictcp_update(struct bictcp *ca, u32 cwnd) {
    u64 offs;
    int delta;

    // Time since last congestion event
    offs = ca->epoch_start ? tcp_jiffies32 - ca->epoch_start : 0;

    if (ca->epoch_start == 0) {
        ca->epoch_start = tcp_jiffies32;
        ca->ack_cnt = 1;
        ca->tcp_cwnd = cwnd;
        return;
    }

    // Cubic function: W(t) = C*(t-K)^3 + W_max
    delta = (s32)(tcp_jiffies32 - ca->cnt);
    offs = delta * HZ / 1000; // Convert to milliseconds

    if (offs < ca->bic_K) {
        // Concave region
        offs = ca->bic_K - offs;
        ca->bic_target = ca->bic_origin_point -
            (u32)(ca->bic_scale * offs * offs * offs / HZ / HZ / HZ);
    } else {
        // Convex region
        offs = offs - ca->bic_K;
        ca->bic_target = ca->bic_origin_point +
            (u32)(ca->bic_scale * offs * offs * offs / HZ / HZ / HZ);
    }
}
```

## 3. UDP Implementation in the Linux Kernel

### 3.1 UDP Receive Path

```
NIC → IP layer → udp_v4_rcv()
    │
    ▼
┌──────────────────────┐
│  Lookup socket by    │
│  (src_ip, src_port,  │
│   dst_ip, dst_port)  │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│  Queue to socket's   │
│  receive buffer      │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│  Wake up user-space  │
│  recvmsg()           │
└──────────────────────┘
```

### 3.2 UDP Fragmentation

```c
// For packets exceeding MTU, IP layer fragments
// UDP doesn't reassemble fragments; IP does
static int ip_fragment(struct net *net, struct sock *sk,
                        struct sk_buff *skb, ...) {
    // Split skb into MTU-sized fragments
    // Each fragment gets its own IP header
    // Only first fragment has UDP header
}
```

## 4. Socket Buffer (sk_buff) Structure

```c
// Linux kernel: include/linux/skbuff.h
struct sk_buff {
    /* These two must be first */
    struct sk_buff *next;
    struct sk_buff *prev;

    struct sock *sk;
    struct net_device *dev;

    /* Header pointers */
    char *head;     // Start of allocated buffer
    char *data;     // Start of protocol header
    char *tail;     // End of protocol data
    char *end;      // End of allocated buffer

    unsigned int len;         // Length of actual data
    __u32 priority;
    __be16 protocol;

    /* Timestamps */
   ktime_t tstamp;

    /* Networking headers */
    union {
        struct tcphdr *th;
        struct udphdr *uh;
        struct icmphdr *icmph;
    };
};
```

## 5. TCP Segmentation Offload (TSO)

Modern NICs can offload TCP segmentation:

```
Without TSO:
    TCP: Build 10 x 1460-byte segments → 10 packets
    NIC: Send each packet individually

With TSO:
    TCP: Build 1 x 14600-byte super-packet
    NIC: Split into 10 x 1460-byte segments (in hardware)

Benefit: Fewer CPU interrupts, higher throughput
```

## 6. Large Receive Offload (LRO) / Generic Receive Offload (GRO)

```
Without GRO:
    NIC → 10 x 1460-byte packets → 10 interrupts → 10 TCP handlings

With GRO:
    NIC → 10 x 1460-byte packets → Merge into 1 super-packet
    → 1 interrupt → 1 TCP handling

Benefit: Reduced CPU utilization, higher receive throughput
```

## 7. Nagle's Algorithm in the Kernel

```c
// Linux kernel: tcp_output.c
static bool tcp_nagle_check(bool partial, const struct tcp_sock *tp,
                             int nonagle) {
    // nonagle = 1 when TCP_NODELAY is set
    return partial &&
           ((tp->packets_out == 0 && tp->write_seq - tp->snd_una <= 0) ||
            tp->packets_out > 0) &&
           !nonagle;
}

static bool tcp_may_send_now(struct sock *sk) {
    struct tcp_sock *tp = tcp_sk(sk);
    // Send if:
    // 1. No outstanding data (packets_out == 0), OR
    // 2. TCP_NODELAY is set, OR
    // 3. Window is >= MSS
    return skb_queue_empty(&tp->write_queue) ||
           tp->nonagle & TCP_NAGLE_OFF ||
           tp->packets_out == 0;
}
```

## 8. Delayed ACK in the Kernel

```c
// Linux kernel: tcp_input.c
static void tcp_delack_timer_handler(struct sock *sk) {
    struct tcp_sock *tp = tcp_sk(sk);

    if (tp->rcv_nxt == tp->copied_seq &&
        !tp->delayed_acks)
        return;

    // Send delayed ACK
    tp->compressed_ack--;
    __tcp_send_ack(sk, 0);
}

// Delayed ACK is sent:
// - Immediately if two packets arrive
// - After 500ms timer if only one packet
// - When application reads data (window update)
```

## 9. Socket Buffer Limits

```
/proc/sys/net/ipv4/tcp_rmem = 4096 131072 6291456
                                    │
                            Default receive buffer

/proc/sys/net/ipv4/tcp_wmem = 4096 65536 6291456
                                    │
                            Default send buffer

/proc/sys/net/core/rmem_max = 212992     │
/proc/sys/net/core/wmem_max = 212992     ├── Hard limits
```

## 10. Kernel Bypass Technologies

For ultra-low latency, applications bypass the kernel:

| Technology | Approach | Latency |
|------------|----------|---------|
| DPDK | Userspace NIC driver | < 1μs |
| RDMA | Direct memory access | ~1μs |
| XDP | eBPF at driver level | ~100ns |
| io_uring | Async I/O with sqpoll | ~1μs |

These internals provide a deep understanding of how TCP and UDP actually work at the operating system level.
