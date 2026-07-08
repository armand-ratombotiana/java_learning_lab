# TCP/UDP Deep Dive — Flashcards

## TCP Basics

**Q: What are the 11 TCP states?**
A: CLOSED, LISTEN, SYN_SENT, SYN_RCVD, ESTABLISHED, FIN_WAIT_1, FIN_WAIT_2, CLOSE_WAIT, CLOSING, LAST_ACK, TIME_WAIT

**Q: What are the flags in the TCP header?**
A: CWR, ECE, URG, ACK, PSH, RST, SYN, FIN (9 flags total)

**Q: What is the minimum TCP header size?**
A: 20 bytes

**Q: What is the maximum TCP header size?**
A: 60 bytes (with options)

**Q: What values are exchanged during the 3-way handshake?**
A: Client sends SYN with ISN (x). Server sends SYN+ACK with ISN (y) and ack = x+1. Client sends ACK with ack = y+1.

**Q: What is the 4-tuple that identifies a TCP connection?**
A: (source IP, source port, destination IP, destination port)

## TCP Flow Control

**Q: How does the receiver communicate its capacity?**
A: Via the Window Size field in every TCP segment

**Q: What is a zero-window condition?**
A: When the receiver's buffer is full and it advertises window = 0. Sender stops and periodically probes.

**Q: What is Silly Window Syndrome (SWS)?**
A: When the receiver advertises tiny window increments, causing the sender to send many small segments

**Q: How is SWS avoided?**
A: Clark's algorithm — receiver should not advertise a new window until it's at least 1 MSS or half the buffer

## Congestion Control

**Q: What are the four phases of TCP congestion control (Reno)?**
A: Slow start, Congestion avoidance, Fast retransmit, Fast recovery

**Q: How does slow start increase cwnd?**
A: Exponentially — cwnd doubles every RTT (cwnd += MSS per ACK received)

**Q: How does congestion avoidance increase cwnd?**
A: Linearly — cwnd += 1 MSS per RTT (cwnd += MSS^2 / cwnd per ACK)

**Q: What is the decrease factor on loss (TCP Reno)?**
A: Multiplicative decrease: cwnd = cwnd / 2 (for triple duplicate ACK), cwnd = 1 MSS (for timeout)

**Q: What triggers fast retransmit?**
A: Three duplicate ACKs (same sequence number acknowledged four times)

**Q: What is the TCP throughput formula?**
A: Throughput = (MSS × sqrt(3/2)) / (RTT × sqrt(loss_rate))

**Q: What is BDP?**
A: Bandwidth-Delay Product = Bandwidth × RTT. The amount of data that can be in flight.

**Q: How does TCP Cubic differ from Reno?**
A: Cubic uses a cubic growth function (W = C(t-K)^3 + Wmax) instead of linear AIMD. Better for high-BDP links.

**Q: How does BBR differ from loss-based algorithms?**
A: BBR is model-based (estimates bandwidth from delivery rate, RTT from timing) rather than loss-based. Avoids bufferbloat.

## Nagle's Algorithm

**Q: What is Nagle's algorithm?**
A: Coalesces small TCP writes: send if (data >= MSS) OR (no outstanding unACKed data). Otherwise buffer.

**Q: How do you disable Nagle's algorithm?**
A: `socket.setTcpNoDelay(true)` in Java, or setsockopt with TCP_NODELAY

**Q: What problem does Nagle's algorithm solve?**
A: The "small packet problem" — sending 1 byte of data in a 41-byte TCP/IP packet (4100% overhead)

**Q: What is the Nagle + Delayed ACK interaction?**
A: Nagle waits for ACK before sending; Delayed ACK waits for data before ACKing. Can cause 500ms delay.

## UDP Basics

**Q: What is the UDP header size?**
A: 8 bytes

**Q: What fields are in the UDP header?**
A: Source Port (16), Destination Port (16), Length (16), Checksum (16)

**Q: What is the maximum UDP payload?**
A: 65535 - 8 (header) - 20 (IPv4 minimum) = 65507 bytes

**Q: Is UDP checksum mandatory?**
A: Optional in IPv4, mandatory in IPv6

**Q: What is a UDP multicast address range?**
A: 224.0.0.0 to 239.255.255.255 (Class D)

**Q: What protocol manages multicast group membership?**
A: IGMP (Internet Group Management Protocol)

## Socket Options

**Q: What does TCP_NODELAY control?**
A: Enables/disables Nagle's algorithm. True = Nagle disabled (low latency)

**Q: What does SO_LINGER control?**
A: Behavior on socket close when there's pending data. 0 = RST on close, timeout = block up to N seconds.

**Q: What does SO_REUSEADDR do?**
A: Allows binding to a port in TIME_WAIT state (useful for server restart)

**Q: What is the default socket timeout in Java?**
A: 0 (infinite) — read() blocks forever

**Q: What does SO_RCVBUF control?**
A: Size of the kernel receive buffer for the socket

**Q: What does SO_SNDBUF control?**
A: Size of the kernel send buffer for the socket

## Performance

**Q: What happens if the socket buffer is smaller than BDP?**
A: The link is underutilized — cannot keep enough data in flight

**Q: What happens if the socket buffer is much larger than BDP?**
A: Bufferbloat — increased latency under load

**Q: What is TCP Fast Open?**
A: Allows data in SYN packet for repeat connections (0-RTT). Client uses TFO cookie from previous connection.

**Q: What is the default initial cwnd in modern TCP?**
A: 10 MSS (RFC 6928)

**Q: What is the maximum TCP window without window scaling?**
A: 65535 bytes

**Q: What is the maximum TCP window with window scaling?**
A: ~1 GB (window scale factor up to 14, so 65535 << 14)

## Reliability Concepts

**Q: What is Karn's algorithm?**
A: Retransmission timeout calculation: ignore RTT samples from retransmitted segments. RTO = smoothed_RTT + 4 × RTT_variance.

**Q: What does SACK allow?**
A: Selective Acknowledgment — receiver can acknowledge non-contiguous data blocks, enabling selective retransmission.

**Q: What is Go-Back-N?**
A: Without SACK, on packet loss, sender retransmits all data from the lost packet onward. Inefficient compared to selective retransmit.

**Q: What does PAWS (Protection Against Wrapped Sequences) do?**
A: Uses TCP timestamps to detect sequence number wraparound on high-speed links (can wrap in < 1 minute at 10 Gbps).

## Security

**Q: What is a SYN flood attack?**
A: Attacker sends many SYN packets without completing the handshake, exhausting server's connection queue.

**Q: What mitigates SYN floods?**
A: SYN cookies (no state stored for half-open connections), increased backlog, reduced SYN_RCVD timeout

**Q: What is a UDP amplification attack?**
A: Attacker sends small UDP queries with spoofed source IP → server sends large response to victim

**Q: What is BCP 38?**
A: Best practice to filter packets with source IPs that don't belong to the originating network (ingress filtering)
