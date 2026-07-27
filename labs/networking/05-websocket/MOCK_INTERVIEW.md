# WebSocket — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: What is WebSocket? How does it differ from HTTP?

**Expected coverage**: Full-duplex communication over single TCP connection, ws://wss:// scheme, HTTP upgrade handshake (101 Switching Protocols), persistent connection (no per-message HTTP overhead), message-based (not request-response), standardized by RFC 6455.

**Q2**: Walk through the WebSocket handshake process.

**Expected coverage**: Client sends HTTP Upgrade request (Upgrade: websocket, Connection: Upgrade, Sec-WebSocket-Key: random base64, Sec-WebSocket-Version: 13), server responds with 101 (Sec-WebSocket-Accept: SHA-1 hash of key + GUID), connection transitions from HTTP to WebSocket, bidirectional frames follow. WSS adds TLS layer before upgrade.

**Q3**: Explain WebSocket frame structure.

**Expected coverage**: FIN (1 bit: final fragment), RSV (3 bits: for extensions), Opcode (4 bits: 0=continuation, 1=text, 2=binary, 8=close, 9=ping, A=pong), MASK (1 bit: client-to-server masked), Payload length (7/7+16/7+64 bits), Masking key (4 bytes if MASK=1), Payload data. Max frame size 2^63 bytes.

## Intermediate (3 questions)

**Q4**: How does WebSocket handle connection recovery and reconnection?

**Expected coverage**: No built-in reconnection — application implements reconnection logic, exponential backoff (1s, 2s, 4s, 8s... max 30s), jitter to avoid thundering herd, heartbeat/ping-pong for liveness detection, resume with session ID if server persists state, close frame reason codes for diagnostics.

**Q5**: Compare WebSocket to Server-Sent Events (SSE) and long polling.

**Expected coverage**: WebSocket (full-duplex, binary, stateful connection), SSE (server→client only, text, auto-reconnect via EventSource, simpler, HTTP), Long polling (compatible everywhere, high latency, wasteful). WebSocket for bi-di needs (chat, gaming), SSE for server notifications (feeds, alerts), polling for legacy or simple updates.

**Q6**: How do you scale WebSocket connections across multiple servers?

**Expected coverage**: Sticky sessions (cookie-based load balancing to route client to same server), pub/sub backend (Redis pub/sub, Kafka, or message broker to broadcast across WebSocket servers), shared session state (user→server mapping), horizontal scaling behind L4 or L7 load balancer with WebSocket support, graceful shutdown with draining.

## Advanced (2 questions)

**Q7**: You have a WebSocket server handling 100K concurrent connections. Walk through optimization strategies.

**Expected coverage**: Event-driven architecture (epoll/kqueue, Node.js/vert.x/Netty), thread-per-core (no thread-per-connection), zero-copy buffer management, connection limits (ulimit -n, /etc/security/limits.conf), memory per connection (~10-20KB), heartbeat timer management (timing wheel), backpressure handling, monitoring (connections/sec, messages/sec, memory, GC).

**Q8**: Design a real-time collaborative document editor (like Google Docs) using WebSocket.

**Expected coverage**: WebSocket for real-time sync, CRDT or OT for conflict resolution, operation sequencing (lamport clock/hybrid logical clock), version vectors for state reconciliation, heartbeat for liveness, reconnection with state recovery, batching operations, compression (per-message deflate extension), operational transformation for cursor positions.
