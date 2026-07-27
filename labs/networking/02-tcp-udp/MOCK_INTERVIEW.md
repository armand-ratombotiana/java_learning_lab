# TCP/UDP — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: Explain the TCP 3-way handshake. What would happen if it didn't complete?

**Expected coverage**: SYN → SYN-ACK → ACK, ISN generation, half-open connections, SYN flood (and mitigation via SYN cookies), TCP fast open as an optimization to save 1 RTT.

**Q2**: Compare TCP and UDP. When would you choose each?

**Expected coverage**: Connection-oriented vs connectionless, reliability/ACKs vs fire-and-forget, ordered delivery vs unordered, flow/congestion control vs application responsibility, header overhead (20-60 bytes vs 8 bytes), use cases (HTTP/SSH vs DNS/VoIP/gaming/QUIC).

**Q3**: What is a TCP segment? Explain the fields in the TCP header.

**Expected coverage**: Source/Dest port (2 bytes each), Sequence/Ack number (4 bytes each), Data offset (4 bits), Flags (URG/ACK/PSH/RST/SYN/FIN), Window size, Checksum, Urgent pointer, Options (MSS, Window scale, SACK, Timestamps).

## Intermediate (3 questions)

**Q4**: Explain TCP congestion control — slow start, congestion avoidance, fast retransmit, fast recovery.

**Expected coverage**: AIMD (Additive Increase Multiplicative Decrease), cwnd and ssthresh, slow start (exponential growth until ssthresh or loss), congestion avoidance (linear growth), fast retransmit (3 duplicate ACKs), fast recovery, Tahoe vs Reno.

**Q5**: Describe TCP flow control. What happens when the receiver's buffer is full?

**Expected coverage**: Sliding window mechanism, receiver-advertised window (rwnd), zero-window condition, TCP persist timer (probe segments), window scaling option for high-BDP links, Silly Window Syndrome (and Nagle's algorithm / Clark's solution).

**Q6**: What is a TCP socket? Explain bind(), listen(), accept(), and connect().

**Expected coverage**: Socket as endpoint (IP:port), bind() assigns local address, listen() marks as passive, accept() creates new socket per connection, connect() initiates to remote, backlog size, SYN queue vs accept queue, SO_REUSEADDR for TIME_WAIT handling.

## Advanced (2 questions)

**Q7**: Your application has high latency. Walk through debugging TCP performance issues.

**Expected coverage**: iperf for throughput, ss -i for cwnd/rtt, tcpdump for retransmissions, check for window scaling mismatch, Nagle's algorithm interaction with delayed ACK, BBR vs CUBIC tuning, bufferbloat detection.

**Q8**: Design a reliable protocol over UDP (like QUIC). What mechanisms would you need?

**Expected coverage**: Sequence numbers for ordering, ACK + retransmission for reliability, connection ID for state, flow control (credit-based), congestion control, encryption at transport, 0-RTT handshake, connection migration.
