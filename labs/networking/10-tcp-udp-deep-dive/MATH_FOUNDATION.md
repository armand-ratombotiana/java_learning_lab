# TCP/UDP Deep Dive — Math Foundation

## 1. Sequence Number Arithmetic

TCP sequence numbers are 32-bit unsigned integers, wrapping after 2^32 bytes.

### Sequence Space
- Range: 0 to 2^32 - 1 (4,294,967,295)
- Wrapping: After 2^32 bytes, sequence wraps to 0
- Window size can be up to 2^30 bytes with window scaling

### Sequence Number Comparison
```
seq1 < seq2 if (seq2 - seq1) mod 2^32 < 2^31
```

### Initial Sequence Number (ISN)
- Should be random to avoid spoofing
- RFC 6528 recommends ISN = M + F(local_ip, local_port, remote_ip, remote_port)
- Where M is a 4μs counter

## 2. Throughput and Latency Formulas

### Bandwidth-Delay Product (BDP)
```
BDP = Bandwidth × RTT
```

BDP is the amount of data "in flight" in the network.

### Maximum Throughput (TCP)
```
Throughput ≤ WindowSize / RTT
```

For a 64KB window and 100ms RTT:
```
Throughput = 65536 × 8 / 0.1 = 5.24 Mbps
```

With window scaling (1GB window):
```
Throughput = 10^9 × 8 / 0.1 = 80 Gbps
```

### Ideal Window Size
```
Optimal Window = BDP = Bandwidth × RTT
```

If window < BDP: pipe is underutilized
If window > BDP: bufferbloat, increased latency

## 3. Congestion Control Math

### Slow Start
```
cwnd(t) = MSS × 2^(t/RTT)
```
After n RTTs:
```
cwnd = MSS × 2^n
```

### Congestion Avoidance (Reno)
```
cwnd(t) = MSS + (t/RTT) × MSS
```
Linear increase: adds 1 MSS per RTT.

### TCP Reno Throughput Model
```
Throughput ≈ (MSS × sqrt(3/2)) / (RTT × sqrt(p))
```
Where p is the packet loss rate.

For MSS = 1460, RTT = 100ms, p = 0.001:
```
Throughput ≈ (1460 × 1.225) / (0.1 × 0.0316) ≈ 565,000 bytes/s ≈ 4.5 Mbps
```

### TCP Cubic
```
cwnd = C × (t - K)^3 + Wmax
```
Where:
- t = time since last loss
- Wmax = window size at last loss
- C = scaling constant (typically 0.4)
- K = cbrt(Wmax × β / C), β = 0.3 (decrease factor)

### TCP BBR
BBR uses pacing rate instead of window:
```
PacingRate = pacing_gain × estimated_BW
```

Estimated bandwidth is the maximum delivery rate observed over the last 6-10 RTTs.

## 4. Reliability Math (Reliable UDP)

### Probability of Successful Delivery
With packet loss rate p and n transmissions:
```
P(success) = 1 - p^n
```

For p = 0.01 (1% loss) and 3 retransmissions:
```
P(success) = 1 - 0.01^3 = 0.999999
```

### Expected Number of Transmissions
```
E[transmissions] = 1 / (1 - p)
```

For p = 0.1: E = 1.11 transmissions on average
For p = 0.5: E = 2 transmissions on average

### Optimal Timeout Calculation
Based on RTT estimation:
```
SRTT = α × SRTT_old + (1-α) × RTT_sample  (α typically 0.875)
RTTVAR = β × RTTVAR_old + (1-β) × |SRTT - RTT_sample|  (β typically 0.75)
RTO = SRTT + 4 × RTTVAR
```

Karn's algorithm: ignore RTT samples from retransmitted segments.

## 5. Nagle's Algorithm

### Condition for Sending
```
if (data >= MSS) OR (no_unacked_data):
    send()
else:
    buffer()
```

### Maximum Delay
Nagle's algorithm delays ACK for at most 500ms (RFC 1122) or until window is full.

### TCP_NODELAY Effect
Without Nagle and delayed ACK disabled:
```
Small Packet Rate = min(1 / (RTT/2), application_write_rate)
```

## 6. Queueing Theory

### Little's Law
```
L = λ × W
```
- L = average number of packets in queue
- λ = average arrival rate
- W = average time in queue

### M/M/1 Queue Model
```
Average queue length = ρ / (1 - ρ)
Average waiting time = (ρ / λ) / (1 - ρ) = 1 / (μ - λ)
```
Where ρ = λ/μ (utilization), μ = service rate.

## 7. Jitter and Delay

### One-way Delay (OWD)
```
OWD = t_received - t_sent
```
Requires clock synchronization (NTP).

### Jitter Calculation
```
J(i) = |D(i-1,i)| where D(i-1,i) = (Ri - R(i-1)) - (Si - S(i-1))
```
- Ri = receive time of packet i
- Si = send time of packet i

## Summary
These mathematical foundations underpin transport layer performance. Understanding BDP, congestion control models, and queueing theory enables engineers to tune networking stacks for optimal performance.
