# Visual Guide — Java Networking (Lab 32)

## TCP 3-Way Handshake

```
Client (Socket)                  Server (ServerSocket)
      |                               |
      |  1. SYN (SEQ=x)               |
      | ─────────────────────────────> |
      |                               |  LISTEN
      |                               |
      |  2. SYN+ACK (SEQ=y, ACK=x+1)  |
      | <───────────────────────────── |
      |                               |
      |  3. ACK (SEQ=x+1, ACK=y+1)    |
      | ─────────────────────────────> |
      |                               |
      └───────── Connection ESTABLISHED
```

- **SYN**: Client sends initial sequence number `x`.
- **SYN-ACK**: Server responds with its own sequence `y` and acknowledges `x+1`.
- **ACK**: Client acknowledges `y+1`. Connection is now open.
- Data transfer follows; connection ends with a 4-way FIN handshake.

## HTTP Request / Response Flow

```
   ┌─────────┐                                  ┌─────────┐
   │ Browser  │                                  │  Server  │
   │ / Client │                                  │  (HTTP)  │
   └────┬─────┘                                  └────┬────┘
        │                                             │
        │  GET /api/users HTTP/1.1                    │
        │  Host: example.com                          │
        │  Accept: application/json                   │
        │  Authorization: Bearer <token>              │
        │ ───────────────────────────────────────────>│
        │                                             │
        │                  HTTP/1.1 200 OK             │
        │                  Content-Type: application/json
        │                  Content-Length: 142         │
        │                  {"users": [...]}            │
        │ <───────────────────────────────────────────│
        │                                             │
```

Key moving parts:
- **URL → DNS → IP**: Hostname resolved via DNS before TCP connect.
- **Socket**: `new Socket(host, port)` in Java opens the TCP channel.
- **ServerSocket**: `accept()` blocks until a client connects.
- **HTTP is stateless**: Each request is independent; session state is managed via cookies or tokens.
- **Java APIs**: `java.net.Socket`, `java.net.ServerSocket`, `HttpURLConnection`, and since Java 11 `java.net.http.HttpClient`.
