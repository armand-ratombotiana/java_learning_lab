# Module 51: WebSockets & Real-Time Communication - Edge Cases & Pitfalls

---

## Pitfall 1: Silent Connection Drops

### ❌ Wrong
Assuming that a WebSocket connection will stay open indefinitely. TCP connections can be silently dropped by intermediate routers, firewalls, or load balancers (like AWS ALB) if they are idle for too long, without the server or client ever receiving a disconnect notification.

### ✅ Correct
Implement a Ping/Pong (Heartbeat) mechanism. The client should periodically send a tiny "ping" message, and the server responds with "pong". If the server misses a certain number of pings, it formally closes the dead connection and frees up the memory.

---

## Pitfall 2: Stateful Load Balancing Mishaps

### ❌ Wrong
Deploying a WebSocket application to a cluster with a standard Round-Robin load balancer and no shared state. If the initial HTTP handshake goes to Node A, but subsequent packets or reconnection attempts go to Node B, the connection fails.

### ✅ Correct
Ensure your Load Balancer is configured to support WebSockets (allowing the `Upgrade` header). In some complex authentication setups, you may need to enable "Sticky Sessions" (Session Affinity) so the client is always routed to the node holding their WebSocket state, or better yet, use a stateless pub/sub backbone like Redis to share state across all nodes.

---

## Pitfall 3: Blocking the WebSocket Thread

### ❌ Wrong
Performing heavy database queries or long-running computations directly inside the `handleTextMessage()` method. If the thread is blocked, it cannot process messages from other connected users sharing that thread pool.

### ✅ Correct
Offload heavy or blocking processing to an asynchronous task executor (like `@Async` or a dedicated `ExecutorService`), so the WebSocket networking thread can immediately return to listening for new messages.