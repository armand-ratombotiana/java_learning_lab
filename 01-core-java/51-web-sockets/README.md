# 51 - Web Sockets

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Status](https://img.shields.io/badge/Status-Complete-success?style=for-the-badge)

**Real-time bidirectional communication for Java applications**

</div>

---

## Overview

This module covers WebSocket protocol implementation in Java for real-time, bidirectional communication between clients and servers.

### Learning Objectives

- Understand WebSocket handshake and lifecycle
- Implement server and client communication
- Handle message framing, ping/pong, and close frames
- Scale WebSocket applications horizontally

---

## Topics Covered

### WebSocket Protocol (RFC 6455)
- HTTP Upgrade handshake (101 Switching Protocols)
- Frame format: FIN, Opcode, MASK, Payload length
- Text (0x01), Binary (0x02), Close (0x08), Ping (0x09), Pong (0x0A)

### Server Implementation
- `ServerSocket` for accepting connections
- Client handler thread per connection
- Broadcasting messages to all connected clients

### Message Patterns
- Text: JSON, plain strings
- Binary: Protobuf, MessagePack
- Control frames: graceful shutdown, keep-alive

### Scaling Considerations
- Single server: ~10K-100K connections (NIO)
- Horizontal: sticky sessions, Redis pub/sub
- Libraries: Jakarta WebSocket, Spring WebSocket, Netty

---

## Running the Module

```bash
cd 01-core-java/51-web-sockets
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.websockets.Lab"
```

---

## Key Concepts

| Concept | Description |
|---------|-------------|
| Upgrade Handshake | HTTP -> WebSocket upgrade via headers |
| Frame Format | 2-14 bytes overhead per message |
| Bidirectional | Server can push without client request |
| Reconnection | Exponential backoff strategy |

---

## Related Modules

- **22-Microservices**: Service communication patterns
- **31-GraphQL**: Real-time subscriptions
- **39-Message Queues**: Async messaging

---

## Resources

- [WebSocket API (MDN)](https://developer.mozilla.org/en-US/docs/Web/API/WebSocket)
- [RFC 6455 Specification](https://datatracker.ietf.org/doc/html/rfc6455)
- [Jakarta WebSocket](https://jakarta.ee/specifications/websocket/)

---

<div align="center">

[Exercises](./EXERCISES.md) | [Pedagogic Guide](./PEDAGOGIC_GUIDE.md)

</div>