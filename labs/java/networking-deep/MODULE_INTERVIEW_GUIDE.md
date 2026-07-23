# Networking Deep Dive — Module Interview Guide

## Company-Specific Questions

### Google
- "Compare blocking I/O vs NIO vs async I/O in Java. When would you use each?"
- "How does Netty's event loop work? Explain the Reactor pattern as implemented in Netty."
- "Design a high-performance HTTP server in Java. What threading model would you choose?"

### Amazon
- "How would you implement a load balancer in Java? What data structures handle connection routing?"
- "Compare HTTP/1.1 vs HTTP/2 for microservice communication. How does HTTP/2 multiplexing help?"
- "How does gRPC Java handle streaming? Explain the Netty-based transport."

### Meta
- "How would you implement a WebSocket server in Java? What are the threading considerations?"
- "Compare the Java HttpClient (Java 11+) with Apache HttpClient. When would you use each?"
- "How do you handle backpressure in a network server? Compare Netty's approach with Project Reactor."

### Apple
- "How does NIO's Selector work at the OS level? What's the difference between select(), poll(), epoll()?"
- "How would you minimize network I/O memory footprint on a mobile backend?"

### Oracle
- "Explain the difference between Socket and ServerSocket. How does the TCP stack interact with Java?"
- "How does the JVM implement InterruptibleChannel? What happens to a thread blocked on I/O when interrupted?"
- "Trace a packet from Java application through the OS TCP stack to the network card."

## LeetCode Problems

| Problem | Networking Concept |
|---------|------------------|
| 146 LRU Cache | Network cache design |
| 362 Design Hit Counter | Rate limiting for API |
| 353 Design Snake Game | Client-server game state |
| 379 Design Phone Directory | Connection pool |
| 432 All O(1) Data Structure | Network protocol design |
| 460 LFU Cache | Cache eviction for network |
| 588 Design In-Memory File System | Network file system |
| 622 Design Circular Queue | Ring buffer for network |

## FAANG Interview Stories

**Story 1: Google — NIO Selector Bug**
> *"We had a production issue where a Netty server stopped accepting connections under load. The cause: NIO Selector.select() returned immediately (spurious wakeup) with zero events in a tight loop, consuming CPU and starving other selectors. The fix: check select() returns > 0 before handling events, add timeout."* — SRE, Google

**Story 2: Amazon — gRPC Backpressure**
> *"A gRPC streaming service would OOM when the consumer was slower than the producer. The fix: use FlowControl with Netty's autoRead(false), then resume when the consumer catches up. Lesson: backpressure must be handled at every layer."* — SDE III, Amazon

**Story 3: Netflix — Java HttpClient Timeout**
> *"We migrated from Apache HttpClient to Java 11 HttpClient. The default connect timeout is infinite — a DNS timeout brought down all connections. We had to explicitly set .connectTimeout(Duration.ofSeconds(5)). Lesson: always set timeouts on network calls."* — Senior Engineer, Netflix

## Senior vs Staff Deep Dive

### Senior-Level
- "Explain Netty's ChannelPipeline. How do handlers process inbound and outbound events?"
- "Compare Java NIO vs NIO.2 (AsynchronousChannelGroup). Which is more scalable?"
- "How does epoll differ from kqueue? When does the JVM fall back to select()?"

### Staff-Level
- "Design a network protocol for a distributed database. How do you handle ordering, retransmission, backpressure?"
- "How does Netty's pooled ByteBuf allocator work? Compare with direct ByteBuffer allocation."
- "Design a zero-copy networking path from Java to network card. Where does the JVM enable this?"
- "How would you implement a custom transport protocol on top of UDP in Java? What about reliability?"

## System Design Connections

| System | Networking Tech |
|--------|---------------|
| Web server | Netty/Undertow, HttpServer (Java 11+), WebFlux |
| RPC framework | gRPC, Netty, HTTP/2 |
| Message broker | Netty, NIO selectors, backpressure |
| API gateway | Netty, HTTP/2 multiplexing, WebFlux |
| CDN edge | NIO selectors, direct buffer pooling |
| Game server | Netty, custom protocol over TCP/UDP |

## Code Review Scenarios

**Scenario 1**: Blocking I/O in Netty event loop.
```java
// Bad: blocking call in event loop thread
channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String result = makeDatabaseCall((String) msg);  // Blocks event loop!
        ctx.write(result);
    }
});
// Fix: Use addLast(EventExecutorGroup) for blocking handlers
```

**Scenario 2**: Unbounded Netty memory.
- Issue: No write buffer high watermark set
- Fix: `.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 64 * 1024)`

**Scenario 3**: HTTP connection not closed.
```java
// Old HttpClient — must close response
CloseableHttpResponse resp = httpClient.execute(request);
try {
    // use resp
} finally {
    resp.close();
}
// Java 11+ HttpClient auto-closes when used with try-with-resources
```

## Debugging Scenarios

**Scenario 1**: Connection reset by peer.
- Check: Server closed connection (timeout, error)
- Fix: Set keepalive, check server-side timeout config
- Debug: Enable TCP keepalive: `.option(ChannelOption.SO_KEEPALIVE, true)`

**Scenario 2**: Socket hang indefinitely.
- Check: No timeout set on connect/read
- Fix: `ServerSocket.setSoTimeout(5000)`, `Socket.connect(address, 5000)`
- Detection: Thread dump — thread at `java.net.SocketInputStream.socketRead0`

**Scenario 3**: Buffer bloat in Netty pipeline.
- Symptom: Memory grows unbounded, then OOM
- Detection: Netty's `Channel.writeAndFlush()` returns pending bytes
- Fix: Set high/low water marks, implement channelWritabilityChanged
