# TCP/UDP Deep Dive — Step by Step

## Step 1: Set Up Your Environment

1. Ensure Java 21+ is installed:
   ```bash
   java --version
   # Should show "java 21" or later
   ```

2. Ensure Maven or Gradle is available:
   ```bash
   mvn --version
   # or
   gradle --version
   ```

3. Clone or navigate to the lab directory:
   ```bash
   cd labs/networking/10-tcp-udp-deep-dive
   ```

## Step 2: Understand the TCP Basics

1. Read `THEORY.md` sections 1-3 on TCP fundamentals
2. Study the TCP header format (source/dest port, seq/ack numbers, flags, window)
3. Understand the 3-way handshake: SYN → SYN+ACK → ACK
4. Run the TCP state machine demo:
   ```bash
   javac src/main/java/com/networking/tcp-udp-deep-dive/TcpStateMachine.java
   java com.networking.tcp-udp-deep-dive.TcpStateMachine
   ```

## Step 3: Implement a TCP Echo Server

1. Create a basic TCP echo server:
   ```java
   try (ServerSocket server = new ServerSocket(8080)) {
       System.out.println("Server listening on 8080");
       try (Socket client = server.accept()) {
           var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
           var out = new PrintWriter(client.getOutputStream(), true);
           String line;
           while ((line = in.readLine()) != null) {
               out.println("Echo: " + line);
           }
       }
   }
   ```

2. Create a TCP client that connects and sends messages
3. Test the echo server by running both and observing the exchange

## Step 4: Explore Socket Options

1. Add socket option inspection to your echo client
2. Try different combinations of:
   - `setTcpNoDelay(true/false)` — Nagle's algorithm
   - `setSoTimeout(millis)` — Read timeout
   - `setReceiveBufferSize/setSendBufferSize` — Buffer tuning
3. Measure the difference in latency for small writes

## Step 5: Understand Congestion Control

1. Read `THEORY.md` section 2 on congestion control
2. Study the CongestionControl class in `CODE_DEEP_DIVE.md`
3. Run the congestion control simulator:
   ```bash
   java com.networking.tcp-udp-deep-dive.CongestionControlSimulator
   ```
4. Vary parameters: initial window, ssthresh, loss rate
5. Observe the cwnd growth pattern (slow start → congestion avoidance)

## Step 6: Build a UDP Echo Server

1. Create a basic UDP echo server:
   ```java
   try (DatagramSocket socket = new DatagramSocket(9090)) {
       byte[] buf = new byte[65535];
       DatagramPacket packet = new DatagramPacket(buf, buf.length);
       while (true) {
           socket.receive(packet);
           String message = new String(packet.getData(), 0, packet.getLength());
           byte[] response = ("Echo: " + message).getBytes();
           socket.send(new DatagramPacket(response, response.length, packet.getAddress(), packet.getPort()));
       }
   }
   ```

2. Create a UDP client
3. Observe the stateless nature of UDP (no handshake)

## Step 7: Implement Reliable UDP

1. Add sequence numbers to UDP packets
2. Implement ACK sending on the receiver
3. Add retransmission on timeout
4. Implement the reorder buffer on the receiver
5. Test with simulated packet loss (drop every Nth packet)

## Step 8: Benchmark and Compare

1. Run the throughput benchmark:
   ```bash
   java com.networking.tcp-udp-deep-dive.ThroughputBenchmark
   ```

2. Compare TCP vs UDP for different:
   - Message sizes (1 byte, 1KB, 64KB, 1MB)
   - Network conditions (local, LAN, WAN)
   - Congestion algorithms (Reno, Cubic, BBR)

3. Document your findings in a report

## Step 9: Complete the Exercises

1. Work through `EXERCISES.md` from Exercise 1 to Exercise 10
2. Verify each solution with the provided tests
3. Pay special attention to:
   - Exercise 3: UDP Echo with Reliability (core concept)
   - Exercise 8: Socket Options Inspector (practical tool)
   - Exercise 10: Reliable File Transfer (capstone)

## Step 10: Take the Quiz

1. Review `QUIZ.md` to test your knowledge
2. Aim for 80% or higher
3. Review any questions you get wrong

## Step 11: Mini Project — Reliable UDP File Transfer

Implement the Reliable UDP File Transfer described in `MINI_PROJECT/`:
1. Create a file sender that chunks files and sends over R-UDP
2. Create a receiver that reassembles chunks and verifies integrity
3. Test with packet loss rates of 0.1%, 1%, and 5%
4. Measure throughput and compare with TCP

## Step 12: Real-World Project — TCP Congestion Control Simulator

Build the full simulation environment described in `REAL_WORLD_PROJECT/`:
1. Support Reno, Cubic, and BBR algorithms
2. Allow configurable network parameters (bandwidth, RTT, loss rate)
3. Output results in CSV/JSON format
4. Generate comparison charts

## Step 13: Review and Reflection

1. Complete `REFLECTION.md` with your learnings
2. Review all the architectures in `ARCHITECTURE.md`
3. Understand the security implications in `SECURITY.md`
4. Practice interview questions from `INTERVIEW.md`

## Learning Path Summary

```
Theory → TCP Echo → Socket Options → Congestion Control → UDP Echo
→ Reliable UDP → Benchmarks → Exercises → Quiz → Mini Project → Final Project
```

Estimated time: 4-5 hours
