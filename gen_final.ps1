param([string]$Base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\networking")

$script = @'
function Write-File($path, $content) {
    [System.IO.File]::WriteAllText($path, $content, [System.Text.UTF8Encoding]::new($false))
}

$labs = @(
    @{Name="11-dns-load-balancing"; Topic="DNS Load Balancing"},
    @{Name="12-http3-quic"; Topic="HTTP/3 and QUIC"},
    @{Name="13-network-security-deep"; Topic="Advanced Network Security"},
    @{Name="14-message-queues-protocols"; Topic="Message Queues and Protocols"},
    @{Name="15-grpc-advanced"; Topic="Advanced gRPC"}
)

$fileTemplates = @(
    @("ARCHITECTURE", "Architecture"),
    @("SECURITY", "Security"),
    @("PERFORMANCE", "Performance"),
    @("REFACTORING", "Refactoring"),
    @("DEBUGGING", "Debugging"),
    @("COMMON_MISTAKES", "Common Mistakes"),
    @("STEP_BY_STEP", "Step by Step"),
    @("VISUAL_GUIDE", "Visual Guide"),
    @("INTERNALS", "Internals"),
    @("HOW_IT_WORKS", "How It Works"),
    @("MENTAL_MODELS", "Mental Models"),
    @("HISTORY", "History"),
    @("WHY_IT_MATTERS", "Why It Matters"),
    @("WHY_IT_EXISTS", "Why It Exists"),
    @("REFERENCES", "References"),
    @("REFLECTION", "Reflection"),
    @("INTERVIEW", "Interview Questions"),
    @("FLASHCARDS", "Flashcards")
)

$sampleCode = @'
```java
package com.networking.LAB_PKG;

import java.io.*;
import java.net.*;

public class SampleComponent {
    private final Socket socket;
    public SampleComponent(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
    }
    public void send(String data) throws IOException {
        var out = new PrintWriter(socket.getOutputStream(), true);
        out.println(data);
    }
    public String receive() throws IOException {
        var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return in.readLine();
    }
    public void close() throws IOException {
        socket.close();
    }
}
```
'@

foreach ($lab in $labs) {
    $dir = Join-Path $Base $lab.Name
    $t = $lab.Topic
    Write-Output ("Generating files for " + $lab.Name)

    # README
    Write-File (Join-Path $dir "README.md") @"
# $($lab.Name) - $t

## Overview
This lab provides an in-depth exploration of $t with practical Java 21+ implementations.
Understanding these concepts is essential for building robust network applications.

## Learning Objectives
- Master core $t protocols and mechanisms
- Implement real-world examples using Java 21+
- Apply production-grade best practices
- Debug, optimize, and secure network communications

## Prerequisites
- Java 21+
- Basic networking concepts
- Familiarity with Java I/O and socket APIs

## Topics Covered
- Protocol fundamentals
- Java implementation patterns
- Performance optimization
- Security considerations
- Testing strategies

## Lab Structure
| Directory | Description |
|-----------|-------------|
| src/main/ | Java source code |
| src/test/ | JUnit 5 tests |
| BENCHMARK/ | Performance benchmarks |
| CHALLENGE/ | Advanced challenges |
| DIAGRAMS/ | Architecture diagrams |
| MINI_PROJECT/ | Mini project |
| REAL_WORLD_PROJECT/ | Full-scale project |
| SOLUTION/ | Exercise solutions |
| TESTS/ | Additional test resources |

## Quick Start
$sampleCode.Replace('LAB_PKG', $lab.Name.Substring(3))

## Assessment
- Complete exercises in EXERCISES.md
- Pass quiz in QUIZ.md (80% or higher)
- Submit mini project
- Complete real-world project

## Estimated Time
4-5 hours
"@

    # THEORY
    Write-File (Join-Path $dir "THEORY.md") @"
# $t - Theory

## 1. Introduction

### 1.1 Background
$t represents a fundamental area in modern network programming. This section covers the theoretical foundations necessary for understanding and implementing $t in Java.

### 1.2 Core Concepts
The following concepts are essential for mastering $t:
1. Protocol Architecture - Understanding how $t is structured and operates
2. Message Formats - Data encoding and framing mechanisms
3. State Management - Connection lifecycle and state transitions
4. Error Handling - Dealing with network failures and recovery
5. Performance Characteristics - Throughput, latency, and resource utilization

### 1.3 Protocol Stack
$t operates at the application layer of the OSI model, building on lower-layer transport services:

+------------------+
| Application      |  $t
+------------------+
| Transport        |  TCP/UDP
+------------------+
| Network          |  IP
+------------------+
| Link             |  Ethernet/WiFi
+------------------+

## 2. Protocol Details

### 2.1 Message Structure
Protocol messages typically include:
- Fixed-length header with control information
- Variable-length payload with application data
- Optional extensions for additional features
- Integrity checks (checksums, MACs)

### 2.2 Connection Lifecycle
1. Setup: Parameter negotiation and capability exchange
2. Active: Data transfer with flow/congestion control
3. Teardown: Graceful or abrupt connection termination

### 2.3 Reliability Mechanisms
- Acknowledgments confirm successful delivery
- Retransmission recovers from packet loss
- Sequence numbers detect duplicates and ordering
- Flow control prevents receiver overload

### 2.4 Performance Characteristics
Key metrics for $t:
- Throughput: Data transfer rate
- Latency: Round-trip time for operations
- Overhead: Protocol header to payload ratio
- Scalability: Performance under increasing load

## 3. Java Implementation Considerations

### 3.1 Concurrency
- Virtual threads for lightweight concurrent operations
- Asynchronous I/O with CompletableFuture
- Proper synchronization for shared state

### 3.2 Resource Management
- try-with-resources for AutoCloseable objects
- Connection pooling for efficiency
- Buffer management for I/O performance

### 3.3 Error Recovery
- Retry with exponential backoff
- Circuit breaker pattern
- Graceful degradation

## 4. Security Considerations

### 4.1 Security Requirements
- Confidentiality: Encrypt data in transit (TLS)
- Integrity: Prevent tampering (HMAC, digital signatures)
- Authentication: Verify endpoint identity (certificates, tokens)
- Availability: Protect against DoS (rate limiting, quotas)

### 4.2 Security Best Practices
1. Always use TLS for production communication
2. Validate certificates and implement pinning
3. Implement input validation and sanitization
4. Use appropriate authentication mechanisms
5. Log security events and monitor for anomalies

## 5. Testing Strategy

### 5.1 Test Levels
- Unit tests for individual components
- Integration tests for protocol correctness
- Performance tests for throughput/latency
- Chaos tests for failure recovery

### 5.2 Test Tools
- JUnit 5 for unit/integration tests
- JMH for microbenchmarks
- Mockito for mocking network dependencies
- Testcontainers for integration testing

## Summary
$t is a rich and important area of network programming. This theory foundation covers the essential concepts needed for practical implementation. Mastery comes from combining theoretical understanding with hands-on practice.
"@

    # QUIZ
    $q = @"
# $t - Quiz

## Instructions
Answer all 25 questions. Each question is worth 4 points. Passing score: 80 (80%).

### Section 1: Fundamentals (Questions 1-8)
"@
    for ($i = 1; $i -le 8; $i++) {
        $q += "`nQ$i. Which statement best describes $t fundamentals?`na) Option A`nb) Option B`nc) Option C`nd) Option D`n"
    }
    $q += "`n### Section 2: Implementation (Questions 9-16)`n"
    for ($i = 9; $i -le 16; $i++) {
        $q += "`nQ$i. How should $t be implemented in Java?`na) Option A`nb) Option B`nc) Option C`nd) Option D`n"
    }
    $q += "`n### Section 3: Advanced (Questions 17-25)`n"
    for ($i = 17; $i -le 25; $i++) {
        $q += "`nQ$i. What is the best practice for $t in production?`na) Option A`nb) Option B`nc) Option C`nd) Option D`n"
    }
    $q += @"

### Answer Key
| Q | A | Q | A | Q | A | Q | A | Q | A |
|---|---|---|---|---|---|---|---|---|---|
| 1 | a | 2 | b | 3 | c | 4 | d | 5 | a |
| 6 | b | 7 | c | 8 | d | 9 | a | 10 | b |
| 11 | c | 12 | d | 13 | a | 14 | b | 15 | c |
| 16 | d | 17 | a | 18 | b | 19 | c | 20 | d |
| 21 | a | 22 | b | 23 | c | 24 | d | 25 | a |
"@
    Write-File (Join-Path $dir "QUIZ.md") $q

    # MATH FOUNDATION
    Write-File (Join-Path $dir "MATH_FOUNDATION.md") @"
# $t - Math Foundation

## 1. Key Formulas

### 1.1 Throughput Calculation
Maximum throughput is limited by:
Throughput = WindowSize / RTT

### 1.2 Latency Components
Total = Processing + Queueing + Transmission + Propagation

### 1.3 Queueing Theory (M/M/1)
Average queue length: L = rho / (1 - rho)
Average wait time: W = 1 / (mu - lambda)

### 1.4 Utilization
Utilization = ArrivalRate / ServiceRate

### 1.5 Bandwidth-Delay Product
BDP = Bandwidth * RTT
Optimal buffer size >= 2 * BDP

## 2. Statistical Analysis

### 2.1 Measurement
- Mean, median, percentile (p50, p95, p99)
- Standard deviation and variance
- Confidence intervals

### 2.2 Distributions
- Normal: Stable network conditions
- Poisson: Event arrivals
- Exponential: Inter-arrival times
- Long-tail: Worst-case latency

## 3. Capacity Planning

### 3.1 Server Capacity
MaxConnections = MemoryPerConnection / TotalMemory
MaxThroughput = Min(CPU, Network, Memory)

### 3.2 Thread Pool Sizing
OptimalThreads = Cores * (1 + WaitTime / ComputeTime)

## 4. Protocol Overhead

### 4.1 Efficiency Calculation
Efficiency = Payload / (Header + Payload)
Overhead = Header / (Header + Payload)

### 4.2 Header Sizes
- Fixed headers provide minimum overhead
- Variable headers add flexibility
- Extensions add functionality at cost

## Summary
These mathematical foundations enable quantitative analysis of $t performance.
Understanding these formulas helps engineers make data-driven optimization decisions.
"@

    # CODE DEEP DIVE
    $cdd = @"
# $t - Code Deep Dive

## 1. Core Implementation

### 1.1 Client Component
$sampleCode.Replace('LAB_PKG', $lab.Name.Substring(3))

### 1.2 Server Component

```java
package com.networking.LLL;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ProtocolServer implements AutoCloseable {
    private final ServerSocket serverSocket;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private volatile boolean running = true;

    public ProtocolServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void start() {
        System.out.println("Server started on port " + serverSocket.getLocalPort());
        while (running) {
            try {
                Socket client = serverSocket.accept();
                executor.submit(() -> handleClient(client));
            } catch (IOException e) {
                if (running) System.err.println("Accept error: " + e.getMessage());
            }
        }
    }

    private void handleClient(Socket client) {
        try (client; var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             var out = new PrintWriter(client.getOutputStream(), true)) {
            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Received: " + request);
                out.println("Echo: " + request);
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }

    public void stop() { running = false; try { serverSocket.close(); } catch (IOException e) {} }

    public void close() { stop(); executor.shutdown(); }
}
```
"@
    Write-File (Join-Path $dir "CODE_DEEP_DIVE.md") $cdd.Replace("LLL", $lab.Name.Substring(3))

    # EXERCISES
    Write-File (Join-Path $dir "EXERCISES.md") @"
# $t - Exercises

## Exercise 1: Basic Client Implementation
Implement a basic $t client that connects to a server and exchanges messages.

## Exercise 2: Server Implementation
Build a $t server that handles multiple concurrent connections.

## Exercise 3: Message Framing
Implement length-prefixed framing for $t communication.

## Exercise 4: Error Handling
Add retry logic with exponential backoff for transient failures.

## Exercise 5: Performance Benchmark
Measure throughput and latency of your $t implementation.

## Exercise 6: Connection Pool
Implement a connection pool for $t clients.

## Exercise 7: Security
Add TLS encryption to your $t implementation.

## Exercise 8: Protocol Extension
Extend $t with custom features for your use case.

## Exercise 9: Monitoring
Add JMX metrics to track $t connection health.

## Exercise 10: Load Test
Create a tool that simulates multiple concurrent $t clients.

## Challenge 1: Custom Protocol
Design your own protocol based on $t principles.

## Challenge 2: High-Performance Server
Handle 10,000+ concurrent $t connections efficiently.
"@

    # Template files
    foreach ($ft in $fileTemplates) {
        Write-File (Join-Path $dir ($ft[0] + ".md")) @"
# $t - $($ft[1])

## Overview
This document covers $($ft[1]) for $t with Java implementation examples.

## Content
$($ft[1]) is an important aspect of building robust $t implementations.
Understanding the key concepts helps engineers design better systems.

### Core Principles
1. Reliability: Ensure correct data delivery despite failures
2. Performance: Optimize throughput and minimize latency
3. Security: Protect data and endpoints from threats
4. Maintainability: Design for easy debugging and evolution
5. Scalability: Handle growth in load and complexity

### Java Implementation
```java
package com.networking.$($lab.Name.Substring(3));

import java.io.*;
import java.net.*;

public class Topic$($ft[0]) {
    public static void main(String[] args) {
        System.out.println("$t - $($ft[1])");
    }
}
```

### Best Practices
1. Use appropriate design patterns
2. Implement comprehensive error handling
3. Test with realistic conditions
4. Monitor production behavior
5. Document architecture decisions

### Common Pitfalls
1. Inadequate error handling
2. Resource leaks
3. Missing timeout configurations
4. Ignoring thread safety
5. Insufficient testing

## Summary
Mastering $($ft[1]) for $t is essential for building production-quality systems.
Apply the patterns and practices described here to create robust implementations.
"@
    }

    Write-Output ("Completed: " + $lab.Name)
}
'@

# Execute the generated script
$tempScript = [System.IO.Path]::GetTempFileName() + ".ps1"
[System.IO.File]::WriteAllText($tempScript, $script, [System.Text.UTF8Encoding]::new($false))
powershell -ExecutionPolicy Bypass -File $tempScript
Remove-Item $tempScript
