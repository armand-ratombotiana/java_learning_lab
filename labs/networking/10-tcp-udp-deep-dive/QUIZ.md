# TCP/UDP Deep Dive — Quiz

## Instructions
Answer all 25 questions. Each question is worth 4 points. Passing score: 80 (80%).

---

### Section 1: TCP Fundamentals (Questions 1-6)

**Q1.** What is the minimum size of a TCP header?
a) 8 bytes
b) 16 bytes
c) 20 bytes
d) 32 bytes

**Q2.** During the TCP 3-way handshake, what does the client send first?
a) SYN+ACK
b) SYN
c) ACK
d) FIN

**Q3.** How does the receiver know it is safe to close the connection?
a) After receiving FIN
b) After sending FIN
c) After the TIME_WAIT timer expires
d) After all data has been acknowledged

**Q4.** Which TCP state does a server enter after receiving a SYN?
a) SYN_SENT
b) SYN_RCVD
c) ESTABLISHED
d) LISTEN

**Q5.** What is the purpose of the TCP window size field?
a) To indicate the number of packets in flight
b) To indicate the number of bytes the receiver can accept
c) To indicate the sender's buffer size
d) To indicate the MSS

**Q6.** What flag combination is used for connection reset?
a) SYN+FIN
b) RST
c) PSH+ACK
d) URG

### Section 2: Congestion Control (Questions 7-12)

**Q7.** In TCP slow start, how does cwnd grow each RTT?
a) Linearly (+1 MSS)
b) Exponentially (doubles)
c) Quadratically
d) Remains constant

**Q8.** What is the default initial cwnd in modern TCP implementations?
a) 1 MSS
b) 10 MSS
c) 100 MSS
d) 65535 bytes

**Q9.** In TCP Reno, what happens when three duplicate ACKs are received?
a) cwnd reset to 1 MSS
b) cwnd halved, fast recovery
c) cwnd unchanged, fast retransmit
d) Connection is reset

**Q10.** What distinguishes TCP Cubic from TCP Reno?
a) Cubic uses a cubic growth function
b) Cubic is Windows-only
c) Cubic has no slow start
d) Cubic uses only delay-based signaling

**Q11.** What does BBR use to control the sending rate?
a) Packet loss events
b) Estimated bandwidth and RTT
c) Explicit congestion notifications
d) Queue occupancy

**Q12.** In TCP's AIMD, the decrease factor is typically:
a) 0.5
b) 0.3
c) 0.7
d) 0.1

### Section 3: UDP (Questions 13-17)

**Q13.** What is the size of a UDP header?
a) 8 bytes
b) 12 bytes
c) 20 bytes
d) 4 bytes

**Q14.** Which statement about UDP is FALSE?
a) UDP preserves message boundaries
b) UDP supports multicast
c) UDP guarantees delivery
d) UDP has lower overhead than TCP

**Q15.** What protocol manages multicast group membership?
a) DNS
b) IGMP
c) ARP
d) DHCP

**Q16.** How does a UDP server handle multiple clients?
a) Thread per connection
b) Select/poll multiplexing
c) No connection state per client
d) Fork per client

**Q17.** What is the maximum payload of a UDP datagram?
a) 65535 bytes
b) 65507 bytes
c) 1500 bytes
d) 65527 bytes

### Section 4: Socket Internals (Questions 18-21)

**Q18.** What socket option disables Nagle's algorithm?
a) SO_NODELAY
b) TCP_NODELAY
c) SO_FASTSEND
d) TCP_QUICKACK

**Q19.** What does SO_LINGER with a timeout of 0 do?
a) Waits for pending data to be delivered
b) Sends RST on close
c) Blocks indefinitely on close
d) Has no effect

**Q20.** The Bandwidth-Delay Product (BDP) represents:
a) The maximum throughput of a link
b) The amount of data in flight in the network
c) The minimum buffer size on a router
d) The maximum packet size allowed

**Q21.** If the socket buffer is smaller than the BDP, what happens?
a) Throughput increases
b) The pipe is underutilized
c) Latency decreases
d) Packet loss increases

### Section 5: Advanced Concepts (Questions 22-25)

**Q22.** Karn's algorithm is used for:
a) Calculating retransmission timeout
b) Detecting duplicate ACKs
c) Computing MSS
d) Selecting a congestion algorithm

**Q23.** What does TCP_QUICKACK do on Linux?
a) Disables delayed ACKs
b) Enables Nagle's algorithm
c) Increases buffer size
d) Enables keepalive

**Q24.** In reliable UDP, selective ACKs (SACK) allow:
a) Acknowledging multiple packets at once
b) Acknowledging non-contiguous blocks
c) Reducing header overhead
d) Enabling multicast

**Q25.** What is the TIME_WAIT duration?
a) 1 second
b) 2 × MSL (Maximum Segment Lifetime)
c) 3 × RTT
d) 10 seconds

---

### Answer Key

| Q | A | Q | A | Q | A | Q | A | Q | A |
|---|---|---|---|---|---|---|---|---|---|
| 1 | c | 2 | b | 3 | c | 4 | b | 5 | b |
| 6 | b | 7 | b | 8 | b | 9 | b | 10 | a |
| 11 | b | 12 | a | 13 | a | 14 | c | 15 | b |
| 16 | c | 17 | b | 18 | b | 19 | b | 20 | b |
| 21 | b | 22 | a | 23 | a | 24 | b | 25 | b |
