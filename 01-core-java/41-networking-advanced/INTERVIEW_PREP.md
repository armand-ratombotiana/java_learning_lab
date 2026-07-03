# Interview Preparation: Advanced Networking

This document covers advanced questions related to TCP vs UDP, Socket Options, and Multicast architecture.

## Q1: Compare and contrast TCP and UDP. When would you choose one over the other?
**Answer:**
*   **TCP (Transmission Control Protocol)** is connection-oriented, reliable, and ordered. It guarantees that packets arrive and are assembled in the correct order, retransmitting lost packets automatically. It is heavy and has higher latency due to handshakes and acknowledgments. Use it for HTTP, file transfers, or any scenario where data integrity is paramount.
*   **UDP (User Datagram Protocol)** is connectionless, unreliable, and unordered. It simply fires packets at a destination. Packets can be dropped or arrive out of order. It is extremely fast and lightweight. Use it for live video/audio streaming, online gaming, or high-frequency sensor telemetry where speed is critical and dropping a single frame of data doesn't matter (because a newer frame will arrive a millisecond later).

## Q2: What is a "Half-Open" TCP connection, and how do you prevent it from crashing your server?
**Answer:**
A half-open connection occurs when a client connects to a server, but then the client's machine crashes or loses network connectivity without sending a formal TCP FIN (finish) packet. The server's OS still considers the connection open. If the server thread calls `read()`, it will block forever waiting for data that will never arrive, causing a thread leak.
**Prevention**: You must explicitly configure a read timeout on the server socket using `socket.setSoTimeout(milliseconds)`. If no data is received within that window, the `read()` method throws a `SocketTimeoutException`, allowing the server to catch it, close the dead socket, and free the thread.

## Q3: What is Nagle's Algorithm, and why do game developers disable it using `TCP_NODELAY`?
**Answer:**
Nagle's Algorithm is an OS-level optimization for TCP. If an application tries to send a very small amount of data (e.g., 10 bytes), the OS will delay sending the packet for a few milliseconds, hoping the application will send more data so it can bundle them together into one larger packet. This saves network bandwidth.
However, in real-time applications like multiplayer games or SSH terminals, this artificial delay is perceived as "lag." Game developers must call `socket.setTcpNoDelay(true)` to disable Nagle's Algorithm, forcing the OS to transmit packets immediately, regardless of their size, to minimize latency.

## Q4: Explain how UDP Multicast works and why it is more efficient than Unicast for broadcasting data.
**Answer:**
If you want to send a stock ticker update to 1,000 connected clients using standard Unicast (TCP or UDP), your server must iterate through a list of 1,000 IP addresses and send the exact same packet 1,000 times, consuming massive server CPU and outbound bandwidth.
With Multicast, the clients "join" a specific Multicast Group IP address (e.g., `230.0.0.1`). The server sends the packet **exactly once** to that group IP. The network infrastructure (switches and routers) takes over the work; the hardware duplicates the packet at the network level and routes it to all clients that joined the group. This drops the server's outbound load from $O(N)$ to $O(1)$.

## Q5: What is the `TIME_WAIT` state in TCP, and how can it cause `BindException`s?
**Answer:**
When a TCP connection is closed, the port it used enters a `TIME_WAIT` state for a period of time (typically 60-120 seconds). The OS does this to ensure that any delayed, wandering packets from the old connection don't accidentally get delivered to a new connection that happens to reuse the same port.
If an application rapidly opens and closes thousands of connections, it will quickly put all available ephemeral ports into the `TIME_WAIT` state. When it tries to open a new connection, the OS will throw a `BindException: Address already in use` because it has run out of available ports. This is why Connection Pooling is mandatory for high-throughput systems.