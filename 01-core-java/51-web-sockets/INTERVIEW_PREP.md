# Module 51: WebSockets & Real-Time Communication - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: When should you use WebSockets instead of REST or Server-Sent Events (SSE)?
**Answer**:
- **REST (HTTP)**: Best for standard, client-initiated CRUD operations (request -> response). Poor for real-time because the client has to constantly poll the server to see if there's new data.
- **Server-Sent Events (SSE)**: Uses a single, long-lived HTTP connection where the server pushes data to the client. It is **unidirectional** (Server -> Client). Best for live feeds, stock tickers, or Twitter feeds where the client just listens.
- **WebSockets**: Uses an upgraded TCP connection that is **bidirectional** and **full-duplex**. Both the client and server can send messages to each other independently and simultaneously. Best for highly interactive applications like multiplayer games, collaborative document editing (like Google Docs), or live chat rooms.

### Q2: How do you scale a WebSocket server horizontally?
**Answer**:
Scaling WebSockets is much harder than scaling stateless REST APIs because WebSocket connections are persistent and bound to a specific physical server node.
If a user connects to `Server A`, and another user connects to `Server B`, `Server A` has no idea how to send a chat message to the user on `Server B`.
**To scale horizontally**, you must use a centralized **Message Broker** (like Redis Pub/Sub, RabbitMQ, or Kafka) as a backbone. 
1. The user on `Server A` sends a message.
2. `Server A` publishes that message to a Redis topic.
3. ALL WebSocket servers (`A`, `B`, `C`) are subscribed to that Redis topic.
4. `Server B` receives the event from Redis, realizes the target user is connected to its local JVM, and pushes the message down the WebSocket connection to the user.

### Q3: What is SockJS, and why is it often paired with STOMP in Spring applications?
**Answer**:
WebSockets are widely supported by modern browsers, but corporate firewalls, outdated proxy servers, or older browsers may still block the `Upgrade` header or drop WebSocket traffic.
**SockJS** is a client/server library that provides a WebSocket-like object. It attempts to establish a native WebSocket connection first. If it fails, it seamlessly falls back to alternative HTTP-based transport mechanisms (like XHR streaming or long-polling) without the developer needing to change their application logic. It ensures real-time communication works in restrictive network environments.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Preventing "Connection Leaks" in a Chat App
**Problem**: An interviewer asks: "You built a Java chat server using plain `WebSocketHandler`. You maintain a global `Set<WebSocketSession>` of all connected users to broadcast messages. Over a few days, the server crashes with an OutOfMemory error, even though there are only a few active users. What caused this, and how do you fix it?"

**Solution**:
The problem is a "Connection Leak" or "Zombie Sessions." Users close their laptops or walk into tunnels, dropping their internet connection. Because the TCP drop wasn't clean (no TCP FIN packet sent), the server never triggered the `afterConnectionClosed` callback. Thus, the dead `WebSocketSession` object remains in the global `Set` forever, preventing garbage collection.
**The Fix**:
1. Implement a **Ping/Pong Heartbeat** mechanism to actively check if sessions are alive.
2. If a session fails to respond to Pings, manually remove it from the `Set` and call `session.close()`.
3. Never use a plain `HashSet` for concurrent environments; ensure you are using a `ConcurrentHashMap.newKeySet()` to prevent `ConcurrentModificationException`s when removing dead sessions while broadcasting.