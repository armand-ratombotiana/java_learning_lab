# Lab 10: TCP/UDP Deep Dive

## Overview
This lab explores the internals of TCP and UDP protocols — congestion control algorithms, socket internals, Nagle's algorithm, and the trade-offs between connection-oriented and connectionless transport. You will implement TCP state machines, congestion control simulations, and UDP-based reliable delivery mechanisms in Java 21+.

## Learning Objectives
- Understand TCP congestion control algorithms (Reno, Cubic, BBR)
- Implement Nagle's algorithm and understand its impact on latency
- Compare UDP vs TCP for different application scenarios
- Inspect socket internals using Java's Socket and ServerSocket APIs
- Build a reliable data transfer protocol on top of UDP
- Analyze packet loss, RTT, and throughput using simulations

## Prerequisites
- Java 21+
- Basic understanding of networking layers (OSI model)
- Familiarity with socket programming

## Lab Structure

| Directory/File | Description |
|----------------|-------------|
| `src/main/` | Java implementations of TCP/UDP concepts |
| `src/test/` | JUnit 5 unit tests |
| `BENCHMARK/` | Throughput and latency benchmark scripts |
| `CHALLENGE/` | Extended exercises for mastery |
| `DIAGRAMS/` | Architecture and flow diagrams |
| `MINI_PROJECT/` | Mini project: Reliable UDP file transfer |
| `REAL_WORLD_PROJECT/` | Full project: TCP congestion control simulator |
| `SOLUTION/` | Solutions to exercises |
| `TESTS/` | Additional test resources |

## Quick Start

```java
// Simple TCP client example
import java.net.*;
import java.io.*;

public class TcpClient {
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("Hello TCP Server");
            System.out.println("Server response: " + in.readLine());
        }
    }
}
```

## Topics Covered
1. TCP 3-way handshake and connection termination
2. Congestion control: slow start, congestion avoidance, fast recovery
3. Nagle's algorithm and TCP_NODELAY
4. UDP datagram sockets and multicast
5. Socket options: SO_RCVBUF, SO_SNDBUF, TCP_NODELAY
6. Reliable data transfer over UDP
7. TCP state machine (SYN_SENT, ESTABLISHED, CLOSE_WAIT, etc.)

## Assessment
- Complete the coding exercises in `EXERCISES.md`
- Pass the quiz in `QUIZ.md`
- Submit the mini project
- Complete the real-world project

## Estimated Time
4-5 hours

## References
- RFC 793 - Transmission Control Protocol
- RFC 5681 - TCP Congestion Control
- RFC 896 - Nagle's Algorithm
- Stevens, TCP/IP Illustrated, Vol. 1
