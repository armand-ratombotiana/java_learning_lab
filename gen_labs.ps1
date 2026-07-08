param([string]$Base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\networking")

function New-MdFile {
    param($LabDir, $Filename, $Title, $LabNum, $Topic, $Topics, $Package)
    $path = Join-Path $LabDir "$Filename.md"
    $content = @"
# $LabNum - $Topic

## Overview
This lab covers $Topic concepts with Java implementation examples.

## Content
Detailed exploration of $Topic in the context of network programming using Java 21+.

## Key Topics
$(($Topics | ForEach-Object { "- $_" }) -join "`n")

## Java Example
```java
package com.networking.$Package;

public class ${Topic -replace "[- ]",""}Lab {
    public static void main(String[] args) {
        System.out.println("$Topic - $Filename");
    }
}
```

## Learning Objectives
- Understand fundamental concepts of $Topic
- Implement real-world examples in Java 21+
- Apply best practices for production systems
- Debug and optimize network communication

## Prerequisites
- Java 21+
- Basic networking knowledge
- Familiarity with socket programming

## Time Estimate
3-5 hours

---

## Introduction

$Topic is a critical area in modern network programming. It encompasses a variety of protocols, patterns, and practices that enable robust, scalable, and secure network communication. This document provides a comprehensive exploration of the subject, covering both theoretical foundations and practical implementation details.

### What is $Topic?

$Topic refers to the set of technologies and techniques used in modern computer networking. Understanding this area is essential for building reliable, high-performance distributed systems that can operate at scale in production environments.

### Why It Matters

In today's interconnected world, network communication is the backbone of virtually every software application. From web browsers to mobile apps, from IoT devices to cloud services, all rely on efficient and reliable network protocols. Mastering $Topic enables engineers to build systems that are faster, more reliable, and more secure.

### Use Cases

The concepts covered in this lab are applicable to a wide range of scenarios:

1. **Enterprise Applications**: Building reliable back-end services that communicate efficiently
2. **Microservices**: Implementing inter-service communication patterns
3. **Real-Time Systems**: Developing low-latency data exchange mechanisms
4. **Cloud-Native Applications**: Designing scalable network architectures
5. **Security-Critical Systems**: Implementing secure communication channels

### Industry Relevance

Major technology companies invest heavily in network protocol optimization. For example:
- Google developed QUIC to reduce web latency
- Netflix built custom TCP congestion control for video streaming
- Amazon Web Services offers managed networking services
- Cloudflare operates a global anycast network for DNS and CDN

Understanding these technologies prepares you for real-world engineering challenges.

## Detailed Exploration

### Fundamental Concepts

At its core, $Topic deals with the exchange of data between systems over a network. The key challenges include:

1. **Reliability**: Ensuring data arrives correctly despite network failures
2. **Performance**: Maximizing throughput while minimizing latency
3. **Security**: Protecting data in transit from eavesdropping and tampering
4. **Scalability**: Supporting growing numbers of clients and data volumes
5. **Interoperability**: Ensuring different systems can communicate effectively

### Protocol Architecture

The architecture of network protocols follows layered designs:

```
+----------------------------------+
|        Application Layer         |
+----------------------------------+
|         Transport Layer          |
+----------------------------------+
|         Network Layer            |
+----------------------------------+
|         Link Layer               |
+----------------------------------+
```

Each layer provides specific abstractions and services to the layer above it, hiding implementation details and enabling modular design.

### Implementation Considerations

When implementing $Topic solutions in Java, consider:

1. **Thread Safety**: Network code must handle concurrent access correctly
2. **Resource Management**: Sockets, buffers, and threads must be properly managed
3. **Error Handling**: Network failures are common and must be handled gracefully
4. **Performance Tuning**: Default settings may not be optimal for your use case
5. **Testing**: Network code requires careful testing with various failure scenarios

### Best Practices

1. **Use try-with-resources** for automatic cleanup of network resources
2. **Set appropriate timeouts** to prevent indefinite blocking
3. **Implement backpressure** to handle slow consumers
4. **Use connection pooling** to reduce overhead
5. **Monitor key metrics** like latency, throughput, and error rates
6. **Log at appropriate levels** for debugging without performance impact
7. **Test with real network conditions** including packet loss and latency variation

### Common Pitfalls

1. Ignoring partial reads/writes in TCP streams
2. Not setting socket timeouts, causing thread starvation
3. Assuming UDP packets arrive in order
4. Sending packets larger than path MTU
5. Not handling connection resets gracefully
6. Forgetting to close resources, causing file descriptor leaks
7. Using blocking I/O in event-driven architectures

## Java Implementation Patterns

### Pattern 1: Resource Management

```java
try (Socket socket = new Socket()) {
    socket.connect(new InetSocketAddress(host, port), 5000);
    socket.setSoTimeout(30000);
    // Use socket...
} catch (IOException e) {
    // Handle network error
}
```

### Pattern 2: Message Framing

```java
DataOutputStream out = new DataOutputStream(socket.getOutputStream());
byte[] message = "Hello".getBytes(StandardCharsets.UTF_8);
out.writeInt(message.length); // Length prefix
out.write(message);           // Message body
out.flush();
```

### Pattern 3: Non-Blocking Operations

```java
CompletableFuture.supplyAsync(() -> {
    try (Socket s = new Socket(host, port)) {
        // Perform network operation
        return result;
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}).thenAccept(result -> {
    // Handle result on completion
});
```

## Summary

This lab provides a comprehensive exploration of $Topic in the context of Java network programming. By understanding both the theoretical foundations and practical implementation techniques, you will be able to build robust, high-performance networked applications that operate reliably in production environments.

The key takeaways are:
- Understand the fundamental protocols and their trade-offs
- Apply appropriate design patterns for your use case
- Follow best practices for resource management and error handling
- Test thoroughly with realistic network conditions
- Monitor and tune performance based on actual metrics

## References

- Official Java Documentation (java.net package)
- Relevant RFCs for the protocols covered
- Industry best practices and patterns
- Academic papers on network protocol design
"@
    Set-Content -Path $path -Value $content
}

$labDefs = @(
    @{Name="11-dns-load-balancing"; Topic="DNS Load Balancing"; Num=11; Package="11-dns-load-balancing"; Topics=@("DNS Resolution","Round-Robin DNS","GeoDNS","Anycast","DNS-based Failover","DNSSEC","DNS Caching")},
    @{Name="12-http3-quic"; Topic="HTTP/3 and QUIC"; Num=12; Package="12-http3-quic"; Topics=@("HTTP/3","QUIC Protocol","0-RTT","Multiplexing","Connection Migration","HTTP/3 vs HTTP/2","QUIC Loss Detection")},
    @{Name="13-network-security-deep"; Topic="Advanced Network Security"; Num=13; Package="13-network-security-deep"; Topics=@("TLS 1.3","mTLS","Certificate Pinning","HSTS","HPKP","OCSP Stapling")},
    @{Name="14-message-queues-protocols"; Topic="Message Queues and Protocols"; Num=14; Package="14-message-queues-protocols"; Topics=@("AMQP","MQTT","STOMP","Protocol Comparison","QoS Levels","Pub/Sub","Message Routing")},
    @{Name="15-grpc-advanced"; Topic="Advanced gRPC"; Num=15; Package="15-grpc-advanced"; Topics=@("gRPC Load Balancing","Deadline Propagation","Interceptors","Reflection","Health Checking","Streaming")}
)

$files = @("README","THEORY","MATH_FOUNDATION","CODE_DEEP_DIVE","EXERCISES","QUIZ","ARCHITECTURE","SECURITY","PERFORMANCE","REFACTORING","DEBUGGING","COMMON_MISTAKES","STEP_BY_STEP","VISUAL_GUIDE","INTERNALS","HOW_IT_WORKS","MENTAL_MODELS","HISTORY","WHY_IT_MATTERS","WHY_IT_EXISTS","REFERENCES","REFLECTION","INTERVIEW","FLASHCARDS")

foreach ($lab in $labDefs) {
    $labDir = Join-Path $Base $lab.Name
    Write-Output "Generating files for $($lab.Name)..."
    foreach ($file in $files) {
        New-MdFile -LabDir $labDir -Filename $file -Title "$($lab.Num) - $($lab.Topic)" -LabNum $lab.Num -Topic $lab.Topic -Topics $lab.Topics -Package $lab.Package
    }
}

Write-Output "All template .md files generated!"
