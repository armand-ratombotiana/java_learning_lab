# Deep Dive: Advanced Networking (TCP, UDP, Multicast)

## 1. The Transport Layer: TCP vs UDP
When building network applications in Java, the first architectural decision is choosing between TCP (Transmission Control Protocol) and UDP (User Datagram Protocol).

### TCP (Transmission Control Protocol)
*   **Connection-Oriented**: A dedicated handshake (SYN, SYN-ACK, ACK) establishes a connection before data flows.
*   **Reliable**: Guarantees delivery. If a packet is lost, TCP automatically retransmits it.
*   **Ordered**: Guarantees that packets are reassembled in the exact order they were sent.
*   **Heavyweight**: Slower and consumes more overhead due to acknowledgment tracking and flow control.
*   **Java Implementation**: `java.net.Socket` (Client) and `java.net.ServerSocket` (Server).

### UDP (User Datagram Protocol)
*   **Connectionless**: "Fire and forget." No handshake. You just throw packets at an IP address and hope they arrive.
*   **Unreliable**: No guarantee of delivery. Packets can be dropped silently.
*   **Unordered**: Packets can arrive out of order or be duplicated.
*   **Lightweight**: Extremely fast with minimal overhead.
*   **Java Implementation**: `java.net.DatagramSocket` and `java.net.DatagramPacket`.

## 2. Advanced TCP Socket Programming
Beyond simple `read()` and `write()` operations, robust TCP applications require configuring socket options.

### Key Socket Options (`java.net.SocketOptions`)
*   **`SO_TIMEOUT`**: The maximum time a `read()` call will block. If the timeout expires, a `java.net.SocketTimeoutException` is thrown. *Critical for preventing deadlocks if a client connects but never sends data.*
*   **`SO_KEEPALIVE`**: Periodically sends a probe packet to verify the connection is still alive, even if no application data is flowing. Prevents firewalls from dropping "idle" connections.
*   **`TCP_NODELAY` (Nagle's Algorithm)**: By default, TCP delays sending small packets, bundling them together to save bandwidth (Nagle's algorithm). For real-time applications (like games or SSH), you must disable this (`setTcpNoDelay(true)`) to ensure packets are sent instantly, minimizing latency.
*   **`SO_REUSEADDR`**: Allows a socket to bind to a port that is in a `TIME_WAIT` state (recently closed). Crucial for servers that need to restart quickly without waiting for the OS to fully release the port.

## 3. UDP Datagram Programming
UDP is used for applications where speed is more important than perfect accuracy (e.g., live video streaming, VoIP, online gaming, IoT sensor telemetry).

```java
// UDP Sender
DatagramSocket socket = new DatagramSocket();
String message = "Hello UDP";
byte[] buffer = message.getBytes();
InetAddress address = InetAddress.getByName("192.168.1.50");
DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
socket.send(packet);

// UDP Receiver
DatagramSocket socket = new DatagramSocket(4445);
byte[] buffer = new byte[256];
DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
socket.receive(packet); // Blocks until a packet arrives
```

## 4. Multicast (One-to-Many)
Standard TCP and UDP are "Unicast" (one sender, one receiver). 
**Multicast** is a specialized form of UDP that allows one sender to broadcast a single packet to *multiple* receivers simultaneously.

*   **How it works**: Receivers "join" a specific Multicast Group (a special IP address range: `224.0.0.0` to `239.255.255.255`). When a sender sends a packet to that group IP, the network routers duplicate the packet and deliver it to all joined receivers.
*   **Use Case**: Stock ticker feeds, live IPTV broadcasting, service discovery (e.g., finding all printers on a local network).
*   **Java Implementation**: `java.net.MulticastSocket` (Note: In modern Java NIO, `DatagramChannel` is preferred for multicasting).

```java
// Joining a Multicast Group
MulticastSocket socket = new MulticastSocket(4446);
InetAddress group = InetAddress.getByName("230.0.0.0");
socket.joinGroup(group);

byte[] buffer = new byte[256];
DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
socket.receive(packet); // Receives broadcast messages
```