# TCP/UDP Deep Dive — Exercises

## Exercise 1: TCP State Machine Visualizer
Implement a Java program that visualizes the TCP state machine. Accept user input for events (SYN sent, SYN received, ACK received, FIN sent, etc.) and display the state transitions.

**Requirements:**
- All 11 TCP states must be handled
- Invalid transitions should raise an error message
- Display a state transition diagram after each step

## Exercise 2: Congestion Control Simulator
Extend the congestion control simulator to plot cwnd over time.

**Requirements:**
- Simulate slow start, congestion avoidance, and fast recovery
- Inject packet loss events at configurable intervals
- Output cwnd values in CSV format for plotting
- Support Reno, Cubic, and BBR algorithms

## Exercise 3: UDP Echo with Reliability
Build a UDP echo client/server that implements reliable delivery.

**Requirements:**
- Client sends messages with sequence numbers
- Server echoes back with ACKs
- Client retransmits on timeout
- Handle out-of-order delivery
- Test with simulated packet loss (drop every Nth packet)

## Exercise 4: Nagle's Algorithm Comparison
Write a benchmark comparing throughput and latency with Nagle enabled vs. disabled.

**Requirements:**
- Send 1000 small messages (1-10 bytes each)
- Measure total time and individual message latency
- Compare TCP_NODELAY=true vs false
- Report results in a table

## Exercise 5: Multicast Chat Application
Build a group chat application using UDP multicast.

**Requirements:**
- Multiple clients can join a multicast group
- Messages are broadcast to all group members
- Each message includes sender ID and timestamp
- Handle graceful leave (departure message)

## Exercise 6: BDP Calculator
Create a tool that recommends optimal socket buffer sizes.

**Requirements:**
- Input: bandwidth, RTT, desired throughput
- Output: recommended SO_RCVBUF, SO_SNDBUF
- Consider full-duplex requirements
- Account for TCP overhead (~3%)

## Exercise 7: TCP Throughput Calculator
Implement the TCP throughput formula:
```
Throughput = (MSS × sqrt(3/2)) / (RTT × sqrt(p))
```

**Requirements:**
- Vary MSS, RTT, and loss rate (p)
- Print throughput in Mbps for a range of values
- Find the loss rate that achieves a target throughput

## Exercise 8: Socket Options Inspector
Create a utility that connects to a remote host and reports all socket option values.

**Requirements:**
- Report SO_RCVBUF, SO_SNDBUF, TCP_NODELAY, SO_TIMEOUT
- Report SO_KEEPALIVE, SO_LINGER, SO_REUSEADDR
- Attempt to set non-default values and verify
- Handle unsupported options gracefully

## Exercise 9: Half-Close Demo
Demonstrate TCP half-close behavior.

**Requirements:**
- Client sends data and calls shutdownOutput()
- Server reads until EOF on input stream
- Server sends response back
- Client reads response
- Document the observed behavior

## Exercise 10: Reliable File Transfer over UDP
Implement a complete file transfer protocol over UDP.

**Requirements:**
- Split large files into MTU-sized chunks
- Each chunk has a sequence number
- Receiver sends selective ACKs (SACK)
- Sender retransmits missing chunks
- Reassemble file in correct order
- Verify file integrity with checksums

## Challenge Exercises

### Challenge 1: TCP BBR Implementation
Implement a simplified TCP BBR (Bottleneck Bandwidth and Round-trip propagation time) model. BBR paces packets based on estimated bandwidth and RTT rather than packet loss. Your implementation should:
- Estimate bandwidth from delivery rate samples
- Estimate min RTT over a sliding window
- Implement pacing_gain cycling (8-phase cycle)
- Handle STARTUP, DRAIN, PROBE_BW, and PROBE_RTT states

### Challenge 2: QUIC-like Reliable UDP
Implement a simplified QUIC-like protocol over UDP with:
- Connection establishment (1-RTT handshake)
- Multiple streams (independent ordered byte streams)
- Stream-level flow control
- Connection migration (change IP/port without reconnection)

## Submission
Organize your solutions in the `SOLUTION/` directory. Include unit tests for each exercise. Use meaningful variable names and JavaDoc comments.
