# Module 51: WebSockets & Real-Time Communication - Quizzes

---

## Q1: The Initial Handshake
How does a WebSocket connection begin?

A) Using a specialized UDP packet sent to port 21.
B) As a standard HTTP `GET` request containing an `Upgrade: websocket` header.
C) As an HTTPS `POST` request with a JSON payload.
D) Through a direct peer-to-peer TCP connection that bypasses HTTP entirely.

**Answer**: B
**Explanation**: WebSockets reuse the standard web infrastructure (ports 80 and 443). The connection starts as a normal HTTP request. If the server agrees, it responds with an `HTTP 101 Switching Protocols` status, and the connection stays open as a full-duplex WebSocket channel.

---

## Q2: STOMP Protocol
What is the primary benefit of using STOMP over raw WebSockets in a Spring Boot application?

A) STOMP encrypts data automatically.
B) Raw WebSockets only send unformatted text/binary. STOMP adds a messaging structure (headers, destinations, commands) enabling Pub/Sub routing and the use of familiar `@MessageMapping` annotations.
C) STOMP prevents the server from crashing.
D) STOMP forces the connection to use UDP.

**Answer**: B
**Explanation**: WebSockets provide no rules on how messages should be formatted. STOMP (Simple Text Oriented Messaging Protocol) acts like HTTP for WebSockets, giving structure so the server knows exactly where to route a message (e.g., to a specific user or a general chat room).

---

## Q3: Connection Maintenance
Why is a "Heartbeat" (Ping/Pong) mechanism essential for WebSocket architectures?

A) To keep the database connection alive.
B) To prevent the JVM garbage collector from deleting the session object.
C) To detect silent network disconnections and prevent intermediate firewalls or load balancers from dropping the "idle" connection.
D) To measure the user's internet speed.

**Answer**: C
**Explanation**: Firewalls and proxies often close TCP connections that have no traffic for a certain period to save memory. A heartbeat sends tiny, periodic messages to ensure the network equipment knows the connection is still actively being used, and allows both client and server to detect if the cord was physically cut.