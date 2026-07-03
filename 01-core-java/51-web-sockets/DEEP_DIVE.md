# Module 51: WebSockets & Real-Time Communication - Deep Dive

**Difficulty Level**: Intermediate to Advanced  
**Prerequisites**: Modules 01-50 (especially Spring Boot Basics and Networking)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Introduction to WebSockets](#intro)
2. [The WebSocket Protocol (ws:// and wss://)](#protocol)
3. [Spring Boot WebSocket Integration](#spring-websocket)
4. [STOMP and Message Brokers](#stomp)
5. [Scaling WebSockets](#scaling)

---

## 1. Introduction to WebSockets <a name="intro"></a>
Traditional HTTP is strictly unidirectional (client sends a request, server responds). For real-time applications (chat apps, live sports tickers), clients historically had to use inefficient workarounds like HTTP Long-Polling or regular Polling. 
WebSockets solve this by providing a persistent, full-duplex (bidirectional) communication channel over a single TCP connection. Once the connection is established, both the client and server can send data to each other instantly at any time.

---

## 2. The WebSocket Protocol (ws:// and wss://) <a name="protocol"></a>
A WebSocket connection begins as a standard HTTP `GET` request containing an `Upgrade: websocket` header. If the server supports WebSockets, it responds with an `HTTP 101 Switching Protocols` status code. The protocol then switches from HTTP to the WebSocket protocol (`ws://` for unencrypted, `wss://` for encrypted TLS).

---

## 3. Spring Boot WebSocket Integration <a name="spring-websocket"></a>
Spring provides a low-level WebSocket API to manage sessions manually.

```java
import org.springframework.web.socket.*;

public class MyTextHandler extends TextWebSocketHandler {
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received: " + message.getPayload());
        session.sendMessage(new TextMessage("Echo: " + message.getPayload()));
    }
}
```

---

## 4. STOMP and Message Brokers <a name="stomp"></a>
Raw WebSockets just send unstructured text/binary. **STOMP (Simple Text Oriented Messaging Protocol)** adds semantics on top of WebSockets (like routing, publish/subscribe, and message types). Spring Boot supports STOMP natively, allowing you to use `@MessageMapping` much like `@RequestMapping`.

```java
@Controller
public class ChatController {
    
    // Client sends to /app/chat
    @MessageMapping("/chat")
    // Server broadcasts result to all clients subscribed to /topic/messages
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        return new ChatMessage("Server", "Received: " + message.getContent());
    }
}
```

---

## 5. Scaling WebSockets <a name="scaling"></a>
Scaling WebSocket servers is notoriously difficult because connections are stateful and persistent. If you run 3 instances of a chat server, User A might connect to Server 1, and User B to Server 2. If User A sends a message to User B, Server 1 doesn't have User B's connection!
**Solution**: Use a centralized message broker (like Redis Pub/Sub, RabbitMQ, or Kafka). Server 1 publishes the message to Redis. Server 2 subscribes to Redis, receives the message, and pushes it down the WebSocket to User B.