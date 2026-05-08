# Exercises: Web Sockets

---

## Easy Exercises (1-5)

### Exercise 1: Simple Echo Server
Create a WebSocket server that echoes back any message sent by the client.

### Exercise 2: Client Connection
Implement a WebSocket client that connects to a server and sends/receives messages.

### Exercise 3: Broadcast Feature
Add the ability for the server to broadcast messages to all connected clients.

### Exercise 4: Heartbeat/Ping-Pong
Implement ping-pong keep-alive mechanism to detect disconnected clients.

### Exercise 5: Message Types
Handle both text and binary messages differently (JSON vs byte array).

---

## Medium Exercises (6-10)

### Exercise 6: Room-based Messaging
Create multiple chat rooms where clients join specific rooms.

### Exercise 7: Reconnection Logic
Implement exponential backoff reconnection strategy.

### Exercise 8: Connection Lifecycle Events
Track: connect, disconnect, reconnect events with logging.

### Exercise 9: Message Queue
Implement a message queue for offline clients (store and forward).

### Exercise 10: Secure WebSocket (WSS)
Configure SSL/TLS for secure WebSocket connections.

---

## Hard Exercises (11-15)

### Exercise 11: Horizontal Scaling
Use Redis pub/sub to broadcast messages across multiple server instances.

### Exercise 12: Session Management
Track user sessions, online status, and last-seen timestamps.

### Exercise 13: Protocol Upgrade
Manually implement the WebSocket handshake (parse headers, compute accept key).

### Exercise 14: Frame Parsing
Parse WebSocket frames manually (read bytes, decode mask, handle fragmentation).

### Exercise 15: Load Balancer Integration
Configure sticky sessions and health checks for WebSocket endpoints.

---

## Solutions Summary

| # | Exercise | Difficulty | Key Concepts |
|---|----------|------------|---------------|
| 1 | Echo Server | Easy | ServerSocket, echo |
| 2 | Client Connection | Easy | Socket, PrintWriter |
| 3 | Broadcast | Easy | CopyOnWriteArrayList |
| 4 | Ping-Pong | Medium | Thread, heartbeat |
| 5 | Message Types | Easy | text vs binary |
| 6 | Rooms | Medium | Map<String, Set<>> |
| 7 | Reconnection | Medium | backoff, retry |
| 8 | Lifecycle | Medium | events |
| 9 | Message Queue | Medium | Queue, store-forward |
| 10 | WSS | Medium | SSLContext |
| 11 | Scaling | Hard | Redis pub/sub |
| 12 | Sessions | Hard | Map<sessionId, User> |
| 13 | Upgrade | Hard | RFC 6455 handshake |
| 14 | Frame Parsing | Hard | ByteBuffer, bitwise |
| 15 | Load Balancer | Hard | sticky sessions |

---

<div align="center">

[Back to README](./README.md) | [Pedagogic Guide](./PEDAGOGIC_GUIDE.md)

</div>