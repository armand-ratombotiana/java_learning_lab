# Protocol Comparison Guide — Interview Edition

> 300+ lines covering TCP vs UDP, HTTP/1.1 vs HTTP/2 vs HTTP/3, REST vs gRPC vs GraphQL, WebSocket vs SSE vs Polling, AMQP vs MQTT vs STOMP.

---

## 1. TCP vs UDP

| Feature | TCP | UDP |
|---------|-----|-----|
| Full Name | Transmission Control Protocol | User Datagram Protocol |
| Connection | Connection-oriented (3-way handshake) | Connectionless |
| Reliability | Reliable (ACK, retransmission) | Unreliable (no ACK) |
| Ordering | In-order delivery (sequence numbers) | No ordering guarantee |
| Flow Control | Sliding window | None |
| Congestion Control | Yes (CUBIC, BBR, Reno) | None (application responsibility) |
| Header Size | 20-60 bytes | 8 bytes |
| Use Cases | Web browsing, email, file transfer, SSH | Streaming, gaming, DNS, VoIP |
| Error Checking | Checksum + ACK | Checksum only |
| Broadcast/Multicast | No (unicast only) | Yes (broadcast, multicast) |
| Speed | Slower (overhead) | Faster (low overhead) |
| State | Stateful (connection state) | Stateless |

### When to Choose TCP

- Data integrity is critical (file transfers, database replication)
- You need ordered delivery (HTTP, email, SSH)
- Network is unreliable (cellular, satellite)
- Application cannot handle retransmission logic

### When to Choose UDP

- Low latency required (VoIP, gaming, live streaming)
- Loss is acceptable (video call glitch > frozen call)
- One-to-many delivery needed (broadcast/multicast)
- Custom reliability on top (QUIC, WebRTC)

### Interview Answer Template
> "I would choose TCP when reliability matters more than speed, and data must arrive intact and in order. I would choose UDP when latency is paramount, or when I'm building a custom protocol that needs features TCP doesn't provide, like multicast or connection migration."

---

## 2. HTTP/1.1 vs HTTP/2 vs HTTP/3

| Feature | HTTP/1.1 (1997) | HTTP/2 (2015) | HTTP/3 (2022) |
|---------|-----------------|---------------|----------------|
| Transport | TCP | TCP (with TLS) | QUIC (UDP) |
| Multiplexing | No (pipelining limited) | Yes (streams over TCP) | Yes (native QUIC streams) |
| Head-of-Line Blocking | Single request blocks queue | TCP-level HOL (packet loss blocks all) | None (loss only affects one stream) |
| Header Compression | None (full headers each request) | HPACK (dictionary-based) | QPACK (out-of-order safe) |
| Server Push | No | Yes (deprecated) | No (proposed replacement: 0-RTT push) |
| Prioritization | No | Yes (stream priority tree) | Yes (extensible priorities) |
| Connection Establishment | 2 RTT | 2 RTT (TCP + TLS 1.3) | 0-1 RTT (QUIC handshake) |
| Encryption | Optional (HTTPS) | Required in practice (TLS) | Required (built-in) |
| Binary? | Text-based | Binary (frames) | Binary (QUIC frames) |
| Backward Compatible | — | Yes (via Upgrade/h2) | Yes (Alt-Svc header) |

### Key Interview Points

**HTTP/2 Problems:**
- TCP head-of-line blocking: packet loss at TCP level blocks ALL streams
- TCP window size limits all multiplexed streams
- Flow control is at connection level (one degraded stream can choke others)

**HTTP/3 Advantages:**
- QUIC runs over UDP — OS doesn't see individual streams
- Independent loss recovery — losing one stream's packet doesn't affect others
- Connection migration — survive IP address changes (Wi-Fi → cellular)
- 0-RTT — send data immediately for returning users

### When to Choose Each

| Protocol | Best For | Avoid When |
|----------|----------|------------|
| HTTP/1.1 | Simple APIs, small payload, low connection count | High concurrency, many subresources |
| HTTP/2 | Modern web, many concurrent requests, server push | High packet loss networks |
| HTTP/3 | High loss, mobile networks, latency sensitive | Legacy clients, firewalls blocking UDP |

### QUIC Handshake (1-RTT)

```
Client: Initial (Client Hello, supported versions)
Server: Initial (Server Hello, TLS cert, transport params)
  └── Both can now send HTTP/3 data
```

### QUIC 0-RTT Handshake

```
Client: Initial + 0-RTT (Client Hello + HTTP data immediately)
Server: Initial (Server Hello + HTTP response)
  └── First request sent immediately if previous connection cached
```

### Security Consideration

- QUIC encrypts packet number, most frame types, and flags
- TCP/TLS encrypts payload but metadata remains visible
- QUIC's encryption is authenticated — prevents spoofing

---

## 3. REST vs gRPC vs GraphQL

| Feature | REST | gRPC | GraphQL |
|---------|------|------|---------|
| Protocol | HTTP/1.1 or HTTP/2 | HTTP/2 | HTTP (usually 1.1) |
| Data Format | JSON, XML, YAML | Protocol Buffers (binary) | JSON |
| Schema | OpenAPI / Swagger | .proto (required) | Schema Definition Language |
| Operations | CRUD via HTTP methods | RPC (unary, streaming, bidirectional) | Query, Mutation, Subscription |
| Caching | Easy (HTTP cache headers) | Complex (no standard) | Limited (per-query) |
| Tooling | curl, browser, Postman | grpcurl, grpc-web, Postman | GraphiQL, Apollo Studio |
| Streaming | SSE, chunked transfer | Native (server, client, bidirectional) | Subscriptions (via WebSocket) |
| Performance | Moderate (text serialization) | High (binary, HTTP/2) | Moderate (JSON) |
| Browser Support | Native | Needs gRPC-web proxy | Native (via GraphQL client) |
| Code Generation | Manual or OpenAPI codegen | Built-in (protoc compiler) | Manual or Apollo/codegen |

### When to Choose

**REST:**
- Simple CRUD APIs
- Public APIs for third-party developers
- When caching is critical (CDNs, reverse proxies)
- Teams familiar with HTTP semantics
- Examples: Stripe API, GitHub API, Twitter API

**gRPC:**
- Internal microservices communication
- High-performance, low-latency requirements
- Streaming data (IoT, financial tickers, chat)
- Polyglot environments (code gen for many languages)
- Examples: Netflix internal APIs, Google Cloud APIs, Uber

**GraphQL:**
- Complex data relationships (social graphs, dashboards)
- Multiple client types (iOS, Android, Web) needing different data shapes
- Rapid prototyping with flexible queries
- Examples: GitHub v4 API, Shopify Storefront API, Hasura

### Interview Questions

1. "Your API is getting slow. Would you switch from REST to gRPC?"
   - Only if profiling shows serialization or HTTP overhead is the bottleneck. GraphQL may also help by reducing over-fetching.

2. "Design an API for a real-time chat application."
   - REST for auth/user profiles, WebSocket for messages, or gRPC bidirectional streaming for efficient message delivery.

3. "When would you use gRPC vs REST for public APIs?"
   - gRPC is harder for browsers, REST is simpler for public consumption. Use gRPC-web or REST for public APIs.

---

## 4. WebSocket vs SSE (Server-Sent Events) vs Polling

| Feature | WebSocket | SSE | Long Polling | Short Polling |
|---------|-----------|-----|--------------|---------------|
| Direction | Bidirectional | Server → Client | Client → Server (simulated push) | Client → Server |
| Protocol | ws:// / wss:// | HTTP/HTTPS | HTTP | HTTP |
| Connection | Persistent, stateful | Persistent | Semi-persistent | Open/close per request |
| Reconnection | Manual | Auto (EventSource) | Manual | Manual |
| Binary Data | Yes (frames) | Text-only | Text | Text |
| Scalability | High (but more complex) | Very high (simple) | Lower (many long-held connections) | Low (high request rate) |
| Use Cases | Chat, gaming, real-time collab | Live feeds, notifications, stocks | Legacy push, fallback | Low-frequency updates |

### When to Choose

**WebSocket:**
- Full-duplex communication needed
- Low latency is critical
- Binary data transfer required
- Examples: Figma collaboration, Slack, trading platforms

**SSE:**
- One-way (server → client) streaming
- Simpler than WebSocket (native EventSource API)
- Automatic reconnection built-in
- Examples: Twitter feed, news ticker, progress bars

**Short/Long Polling:**
- No persistent connection support (some cloud/edge environments)
- Quick implementation with existing HTTP stack
- Low frequency updates acceptable
- Examples: Status checks, simple notifications

### Protocol Overhead

```
WebSocket: 2 bytes per frame (after upgrade)
SSE:      ~1+ bytes per event (text)
Polling:  Full HTTP headers per request (~800 bytes)
```

---

## 5. AMQP vs MQTT vs STOMP

| Feature | AMQP 0-9-1 | AMQP 1.0 | MQTT | STOMP |
|---------|-----------|----------|------|-------|
| Origin | Financial messaging | Enterprise | IoT/M2M | Web/scripting |
| Transport | TCP (reliable) | TCP (reliable) | TCP/TLS | TCP/WebSocket |
| Model | Exchange → Queue → Binding | Node → Link (peer-to-peer) | Pub/Sub with topics | Frame-based text |
| QoS Levels | None (delivery guarantees via exchange config) | Configurable | 0, 1, 2 | None |
| Message Persistence | Yes | Yes | Yes (optional) | No |
| Broker Required | Yes (RabbitMQ) | Yes (ActiveMQ Artemis, Azure SB) | Yes (Mosquitto, HiveMQ) | Yes (RabbitMQ STOMP plugin) |
| Header Size | Moderate | Moderate | 2 bytes (minimum) | Small (text) |
| Best For | Enterprise routing, complex workflows | Multi-platform, cloud | IoT sensors, mobile | Simple messaging, scripting |

### MQTT QoS Levels

| QoS | Description | At Most Once | At Least Once | Exactly Once |
|-----|-------------|-------------|---------------|--------------|
| 0 | Fire and forget | Lowest overhead | No delivery guarantee | Lightweight |
| 1 | At least once | ACK required | Duplicates possible | Balanced |
| 2 | Exactly once | 4-step handshake | Guaranteed delivery | Highest overhead |

### When to Choose

**AMQP (0-9-1 or 1.0):**
- Complex routing (headers, topics, direct, fanout)
- Guaranteed delivery with persistence
- Enterprise integration patterns
- Examples: RabbitMQ, Azure Service Bus, Qpid

**MQTT:**
- IoT with constrained devices (sensors, actuators)
- Unreliable networks (cellular, satellite)
- Minimal bandwidth usage
- Examples: AWS IoT, Azure IoT Hub, Home Assistant

**STOMP:**
- Simple text-based integration
- WebSocket-compatible messaging
- Easy to implement with scripting languages
- Examples: Stock tickers, simple chat, integration testing

### Interview Question

> "You need to build a messaging system for 10,000 IoT sensors sending temperature data every 5 seconds. Which protocol?"

**Answer**: MQTT QoS 0 (fire-and-forget). Low overhead, 2-byte header, designed for constrained devices. Sensors don't need guaranteed delivery for non-critical temperature readings. If occasionally lost, it's fine.

> "Now it's for a financial trading system handling transactions."

**Answer**: AMQP 0-9-1 or 1.0. Needs guaranteed delivery, persistence, complex routing (region, asset class, priority). AMQP supports transactions, dead-letter queues, and durable subscriptions.

---

## Quick Reference: Protocol Selection Matrix

| Requirement | Recommended Protocol |
|-------------|---------------------|
| Real-time bi-di communication | WebSocket |
| Server → client updates | SSE |
| High-performance internal RPC | gRPC |
| Public API, simple CRUD | REST |
| Flexible client-specific queries | GraphQL |
| IoT / constrained devices | MQTT |
| Enterprise messaging | AMQP |
| Simple scripting messaging | STOMP |
| Low-latency lossy transport | QUIC / UDP |
| Reliable ordered transport | TCP |

---

*"The best protocol is the one that makes the right trade-off for your constraints."*
