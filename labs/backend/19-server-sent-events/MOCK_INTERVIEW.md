# Mock Interview: Server-Sent Events (Lab 19)

**Role:** Backend Engineer (Mid-Level)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are Server-Sent Events (SSE) and how do they differ from WebSockets?

**Candidate:** SSE is a standard that allows a server to push data to the browser over a single HTTP connection. Key differences from WebSockets:

| Aspect | SSE | WebSocket |
|--------|-----|-----------|
| Direction | Server → Client only | Bidirectional |
| Protocol | HTTP (text/event-stream) | ws:// protocol |
| Auto-reconnect | Built-in (EventSource API) | Must implement manually |
| Firewall friendly | Yes (uses standard HTTP) | May be blocked |
| Complexity | Simple | Complex |
| Binary | Text only (events as text) | Text and binary |
| Use case | Notifications, feeds, streaming | Real-time collaboration, gaming |

Choose SSE when server needs to push data one-directionally (stock tickers, notifications, activity feeds). Choose WebSocket for bidirectional communication (chat, collaborative editing).

**Interviewer:** How do you implement SSE in Spring Boot?

**Candidate:** Spring supports SSE via:
1. **`SseEmitter`** in Spring MVC:
```java
@GetMapping("/stream/notifications")
public SseEmitter streamNotifications() {
    SseEmitter emitter = new SseEmitter(30_000L); // 30s timeout
    notificationService.subscribe(emitter);
    return emitter;
}
```
2. **`Flux<ServerSentEvent>`** in WebFlux:
```java
@GetMapping(value = "/stream/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<Notification>> streamEvents() {
    return notificationService.getNotificationStream()
        .map(data -> ServerSentEvent.builder(data)
            .event("notification")
            .build());
}
```

WebFlux is preferred for SSE because it's inherently non-blocking and handles many concurrent connections efficiently.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you handle client disconnection in SSE?

**Candidate:** Client disconnection must be handled to prevent memory leaks:

```java
@GetMapping("/stream/updates")
public SseEmitter streamUpdates() {
    SseEmitter emitter = new SseEmitter();
    
    // Register completion and timeout callbacks
    emitter.onCompletion(() -> updateService.unsubscribe(emitter));
    emitter.onTimeout(() -> updateService.unsubscribe(emitter));
    emitter.onError(e -> updateService.unsubscribe(emitter));
    
    updateService.subscribe(emitter);
    return emitter;
}
```

Spring Boot also provides `SseEmitter.ExceptionHandler` for graceful error handling. For WebFlux SSE, the `Flux` completes when the client disconnects, automatically cleaning up resources.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a real-time notification system for a SaaS platform that delivers SSE to 1M concurrent users.

**Candidate:** 

**Architecture:**
```
User Action → HTTP → API Gateway → Notification Service
                                         │
                                   ┌─────┴─────┐
                                   │   Redis    │
                                   │  pub/sub   │
                                   └─────┬─────┘
                                         │
                     ┌───────────────────┼───────────────────┐
                     ▼                   ▼                   ▼
               SSE Server 1         SSE Server 2        SSE Server 3
                     │                   │                   │
                [SseEmitter]        [SseEmitter]         [SseEmitter]
                     │                   │                   │
                     ▼                   ▼                   ▼
                 Users (10K)         Users (10K)          Users (10K)
```

**Implementation details:**
1. **Horizontal scaling:** Each SSE server handles ~10K concurrent SSE connections. Use Redis pub/sub to broadcast events to all servers.
2. **Connection pooling:** Netty/WebFlux for non-blocking SSE. Each connection costs ~1KB memory.
3. **Sticky sessions OR stateless reconnection:** Use session affinity at the load balancer OR implement reconnection logic with a unique user channel ID.
4. **Heartbeats:** Send periodic comments (empty event) every 30s to keep connection alive and detect broken connections.
5. **Backpressure:** Use Redis streams with consumer groups to ensure no event loss.

**Redis pub/sub:**
```java
@Service
public class NotificationService {
    private final RedisTemplate<String, Object> redis;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void subscribe() {
        redis.listenTo(RedisTopic.of("notifications"), new NotificationListener());
    }
    
    public void registerEmitter(String userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
    }
    
    @EventListener
    public void onNotification(NotificationEvent event) {
        redis.convertAndSend("notifications", event);
    }
}

class NotificationListener implements MessageListener {
    void onMessage(NotificationEvent event) {
        SseEmitter emitter = emitters.get(event.getUserId());
        if (emitter != null) {
            emitter.send(event.getData());
        }
    }
}
```

**Scaling to 1M users:**
- Use 100 SSE servers, each handling 10K connections
- Redis cluster with 10 shards for pub/sub
- CDN-like edge SSE nodes for global distribution
- Graceful degrade: fall back to polling if SSE connection fails

---

## Interviewer Feedback

**Strengths:** Good SSE fundamentals, practical scaling design, clean WebFlux implementation  
**Areas to Improve:** Could discuss handling SSE across multiple browser tabs for the same user  
**Verdict:** Hire

---

*Lab 19 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
