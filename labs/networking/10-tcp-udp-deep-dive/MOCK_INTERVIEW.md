# TCP/UDP Deep Dive — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: Explain TCP's four fundamental mechanisms for reliable data transfer.

**Expected coverage**: Checksum (error detection), Sequence numbers (ordered delivery, duplicate detection), Acknowledgments (ACK for received data), Retransmission (timeout-based on RTO, fast retransmit on 3 dup ACKs). Also: Go-Back-N vs Selective Repeat (TCP uses SACK).

**Q2**: Describe the TCP state transition diagram in detail.

**Expected coverage**: CLOSED → LISTEN (passive), CLOSED → SYN_SENT → ESTABLISHED (active open), ESTABLISHED → FIN_WAIT_1 → FIN_WAIT_2 → TIME_WAIT → CLOSED (active close), ESTABLISHED → CLOSE_WAIT → LAST_ACK → CLOSED (passive close). TIME_WAIT (2*MSL) ensures old segments expire and ACK reaches peer. Simultaneous open/close scenarios.

**Q3**: What is the TCP MSS (Maximum Segment Size)? How is it determined?

**Expected coverage**: MSS = MTU - IP header - TCP header (typically 1460 bytes for Ethernet MTU 1500, 1460 - 20 - 20 = 1460), MSS option exchanged during TCP handshake, path MTU discovery (ICMP fragmentation needed + set DF bit), jumbo frames (9000 MTU → 8960 MSS), MSS clamping at routers for tunnels (GRE, PPPoE add overhead).

## Intermediate (3 questions)

**Q4**: Explain TCP window scaling, PAWS, and the TCP timestamp option.

**Expected coverage**: Window scaling (factor up to 14, max window = 1GB), negotiated during handshake, critical for high-BDP links, PAWS (Protection Against Wrapped Sequences) uses timestamp option to detect old segments from wrapped sequence numbers (high-speed links wrap 32-bit seq in ~1.7s at 10Gbps). Timestamp also provides RTT measurement for RTO calculation.

**Q5**: How does TCP calculate the Retransmission Timeout (RTO)?

**Expected coverage**: SRTT (smoothed RTT) = α * SRTT + (1-α) * RTT_sample, RTTVAR (variance) = β * RTTVAR + (1-β) * |SRTT - RTT_sample|, RTO = SRTT + max(G, 4*RTTVAR), where G is clock granularity. Karn's algorithm: don't update SRTT on retransmitted segments. Exponential backoff of RTO on each retransmission (up to 60s default).

**Q6**: What is TCP's Silly Window Syndrome? How do Nagle and Clark's algorithm solve it?

**Expected coverage**: SWS occurs when small amounts of data are sent/received, overwhelming the network with tiny segments. Nagle's algorithm: delay sending if there's unACKed data AND data is smaller than MSS (coalesce small writes). Clark's solution: receiver doesn't advertise small windows (updates when window >= MSS or half the buffer free). Disable Nagle with TCP_NODELAY for latency-sensitive apps.

## Advanced (2 questions)

**Q7**: Design a TCP optimization strategy for a 10Gbps link with 200ms RTT (trans-Pacific).

**Expected coverage**: BDP = 10Gbps * 0.2s = 250MB, need window scaling (factor 14+), enable SACK for efficient loss recovery, BBR congestion control (not loss-based, avoids bufferbloat), TSO/GRO for hardware segmentation offload, adjust tcp_rmem/tcp_wmem (min/default/max), moderate RX/TX ring buffer sizes, RPS/RFS for multi-queue load distribution, increase backlog and SOMAXCONN for accept queue.

**Q8**: You observe high TCP retransmissions despite low application throughput. Walk through root cause analysis.

**Expected coverage**: tcpdump to capture segments, check for SACK (more efficient recovery), analyze window sizes (sender self-throttling on receive window?), check for spurious retransmissions (TSOPT discrepancy?), check RTT variance (congestion vs hardware buffer drops?), check for packet corruption (checksum errors in NIC offload?), verify NIC driver (rx/tx errors, fifo errors in ethtool -S). BBR vs CUBIC behavior with bufferbloat.
