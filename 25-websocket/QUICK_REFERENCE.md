# Quick Reference: WebSocket

<div align="center">

![Module](https://img.shields.io/badge/Module-25-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-WebSocket-green?style=for-the-badge)

**Quick lookup guide for WebSocket with Spring**

</div>

---

## 📋 Core Concepts

| Concept | Description |
|---------|-------------|
| **WebSocket** | Full-duplex communication protocol |
| **STOMP** | Simple Text Oriented Messaging Protocol |
| **SockJS** | WebSocket fallback for older browsers |
| **Message Broker** | Routes messages to subscribed clients |

---

## 🔑 Key Configurations

### WebSocket Config
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(Registry registry) {
        registry.addEndpoint("/ws")
            .setAllowedOrigins("*")
            .withSockJS();
    }
}
```

### STOMP Controller
```java
@Controller
public class ChatController {
    
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message handleMessage(Message msg) {
        return msg;
    }
    
    @MessageMapping("/private/{user}")
    public void privateMessage(@DestinationVariable String user, Message msg) {
        messagingTemplate.convertAndSendToUser(user, "/queue/msg", msg);
    }
}
```

---

## 💻 Client Connection

### JavaScript STOMP
```javascript
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, (frame) => {
    stompClient.subscribe('/topic/messages', (msg) => {
        console.log(JSON.parse(msg.body));
    });
    stompClient.send('/app/chat', {}, JSON.stringify({text: 'Hello'}));
});
```

---

## 📊 Message Patterns

### Pub/Sub
```java
@MessageMapping("/broadcast")
@SendTo("/topic/updates")
public BroadcastMessage broadcast(Message msg) {
    return new BroadcastMessage(msg.getContent(), Instant.now());
}
```

### Point-to-Point
```java
@MessageMapping("/direct")
public void sendToUser(Message msg) {
    template.convertAndSendToUser(
        msg.getRecipient(), 
        "/queue/messages", 
        msg
    );
}
```

---

## 🔒 Security

```java
@Configuration
public class WebSocketSecurityConfig implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor.getCommand() == StompCommand.CONNECT) {
            // Validate token from accessor.getFirstNativeHeader("Authorization");
        }
        return message;
    }
}
```

---

## ✅ Best Practices

- Use STOMP over raw WebSocket for messaging semantics
- Implement heartbeat/keep-alive for connection monitoring
- Handle reconnection logic on client side
- Use topic for broadcast, queue for private messages

### ❌ DON'T
- Don't use WebSocket for request/response patterns
- Don't forget to handle connection failures

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>