# 02 - TCP and UDP

## Overview

TCP (Transmission Control Protocol) and UDP (User Datagram Protocol) are transport layer protocols. This lab covers sockets, connection lifecycle, flow control, and congestion control with Java implementations.

## Learning Objectives
- Understand TCP vs UDP differences
- Implement TCP and UDP sockets in Java
- Master connection lifecycle (SYN, SYN-ACK, ACK, FIN)
- Understand flow control and congestion control

## Quick Start
```java
// TCP Server
ServerSocket server = new ServerSocket(8080);
while (true) {
    Socket client = server.accept();
    BufferedReader in = new BufferedReader(
        new InputStreamReader(client.getInputStream()));
    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
    out.println("Echo: " + in.readLine());
    client.close();
}
```
