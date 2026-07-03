# Edge Cases & Pitfalls: Advanced Networking

Network programming is fraught with invisible failures. The network is unreliable, latency is not zero, and topology changes constantly.

## 1. The "Half-Open" Connection (TCP)
*   **The Scenario**: A client connects to your server. Your server spawns a thread and calls `inputStream.read()`. The client loses power or its physical network cable is cut.
*   **The Pitfall**: Because the client didn't send a formal TCP FIN packet to close the connection gracefully, the server's OS still thinks the connection is alive. The server thread will block on `read()` forever. This is a "half-open" connection. If this happens often, your server will leak threads until it crashes.
*   **Mitigation**: **Always set `SO_TIMEOUT`**. 
    ```java
    socket.setSoTimeout(60000); // 60 seconds
    ```
    If no data is received within the timeout, a `SocketTimeoutException` is thrown, allowing your thread to catch it, close the dead socket, and free the thread.

## 2. UDP Packet Fragmentation and Truncation
*   **The Scenario**: You send a 10KB JSON payload via a single UDP `DatagramPacket`.
*   **The Pitfall**: The Maximum Transmission Unit (MTU) of most Ethernet networks is 1500 bytes. If you send a 10KB UDP packet, the OS must fragment it into multiple smaller packets. If *even one* of those fragments is lost in transit, the entire 10KB packet is discarded by the receiving OS. UDP has no retransmission mechanism. Furthermore, if your receiving buffer is only 1024 bytes, and a 2048-byte packet arrives, Java will silently truncate the data to fit your buffer without throwing an error.
*   **Mitigation**: Keep UDP payloads small (ideally under 512 bytes) to avoid fragmentation. Always ensure your receiving `byte[]` buffer is large enough to handle the maximum expected packet size.

## 3. The `TIME_WAIT` Port Exhaustion
*   **The Scenario**: You build a high-frequency microservice that opens a TCP connection to a database, makes a quick query, and immediately closes the connection. It does this 5,000 times a second.
*   **The Pitfall**: When a TCP connection is closed, the port it used goes into a `TIME_WAIT` state for typically 60-120 seconds. This is an OS-level safeguard to ensure delayed packets don't interfere with future connections. If you open and close connections too quickly, you will exhaust all 65,535 ephemeral ports on your server. You will get a `java.net.BindException: Address already in use`.
*   **Mitigation**: Use Connection Pooling (e.g., HikariCP). Never rapidly open and close TCP connections. Reuse them.

## 4. Nagle's Algorithm Latency (TCP)
*   **The Scenario**: You are building a real-time multiplayer game. You send player coordinates (20 bytes) 60 times a second over TCP.
*   **The Pitfall**: By default, TCP uses Nagle's Algorithm. To save bandwidth, the OS will deliberately delay sending your tiny 20-byte packets, waiting a few milliseconds to see if you send more data so it can bundle them into one larger packet. This introduces severe, noticeable latency (lag) into your real-time application.
*   **Mitigation**: Disable Nagle's Algorithm for real-time streams.
    ```java
    socket.setTcpNoDelay(true);
    ```

## 5. Multicast Routing and TTL
*   **The Scenario**: You write a Multicast application to broadcast data to servers in a different data center.
*   **The Pitfall**: Multicast packets have a Time-To-Live (TTL) value. By default, this is often 1, meaning the packet will not pass through *any* routers; it is restricted to the local subnet. Furthermore, many cloud providers (like AWS or GCP) completely block multicast traffic on their virtual networks for security and performance reasons.
*   **Mitigation**: Set the TTL appropriately (`multicastSocket.setTimeToLive(32)`). Ensure your network infrastructure (routers, switches, cloud VPCs) explicitly supports and routes IGMP/Multicast traffic.