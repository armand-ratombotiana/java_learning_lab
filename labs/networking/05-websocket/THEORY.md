# WebSocket - Theory

## WebSocket Protocol (RFC 6455)

WebSocket provides full-duplex communication over a single TCP connection after an HTTP upgrade handshake.

## Handshake
```
Client                          Server
   |                               |
   |--- HTTP Upgrade Request ----->|
   |    GET /chat HTTP/1.1         |
   |    Upgrade: websocket         |
   |    Sec-WebSocket-Key: dGhl... |
   |    Sec-WebSocket-Version: 13  |
   |                               |
   |<-- 101 Switching Protocols ---|
   |    Upgrade: websocket         |
   |    Sec-WebSocket-Accept: ...  |
   |                               |
   |<==== WebSocket Connection ====>|
   |    (full-duplex, frames)      |
```

## Frame Structure
```
 0                   1                   2                   3
 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|F|R|R|R| opcode|M| Payload len |    Extended payload length    |
|I|S|S|S|  (4)  |A|     (7)     |             (16/64)          |
|N|V|V|V|       |S|             |                               |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|          Masking-key (if MASK=1)                              |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                     Payload Data                               |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
```

## Java WebSocket Server
```java
@ServerEndpoint("/chat")
public class ChatEndpoint {
    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        broadcast("User joined (" + sessions.size() + " users)");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        broadcast("User: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        broadcast("User left (" + sessions.size() + " users)");
    }

    private void broadcast(String message) {
        sessions.forEach(s -> {
            try { s.getBasicRemote().sendText(message); }
            catch (IOException e) { e.printStackTrace(); }
        });
    }
}
```

## Spring WebSocket with STOMP
```java
@Controller
public class ChatController {
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage message) {
        return message;
    }
}

// JavaScript client
let stompClient = Stomp.over(new SockJS('/ws'));
stompClient.connect({}, () => {
    stompClient.subscribe('/topic/public', msg => {
        console.log(JSON.parse(msg.body));
    });
});
```
