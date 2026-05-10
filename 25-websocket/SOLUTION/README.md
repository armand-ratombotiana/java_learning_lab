# WebSocket Solution

## Concepts Covered

### WebSocket Server
- Full-duplex communication over TCP
- Session management with connection tracking
- Broadcast and targeted messaging

### STOMP Protocol
- Simple Text Oriented Messaging Protocol over WebSocket
- Frame parsing: CONNECT, SUBSCRIBE, SEND, MESSAGE
- Message headers and body handling

### Features
- Heartbeat for connection maintenance
- Query string-based user identification
- Thread-safe session management

## Dependencies

```xml
<dependency>
    <groupId>jakarta.websocket</groupId>
    <artifactId>jakarta.websocket-api</artifactId>
    <version>2.1.1</version>
</dependency>
<dependency>
    <groupId>jakarta.websocket</groupId>
    <artifactId>jakarta.websocket-client-api</artifactId>
    <version>2.1.1</version>
</dependency>
```

## Running Tests

```bash
mvn test -Dtest=WebSocketSolutionTest
```