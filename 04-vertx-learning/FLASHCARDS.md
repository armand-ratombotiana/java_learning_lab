# Vert.x Learning Flashcards

## Card 1: Verticle
- **Q:** What is a Verticle in Vert.x?
- **A:** The basic unit of deployment - a component that can run and be deployed

---

## Card 2: AbstractVerticle
- **Q:** How do you create a basic verticle?
- **A:** Extend AbstractVerticle and override start(Promise) method

---

## Card 3: Event Loop
- **Q:** What is the event loop in Vert.x?
- **A:** Non-blocking event processing loop that handles async operations (one per CPU core by default)

---

## Card 4: setTimer vs setPeriodic
- **Q:** What is the difference between setTimer and setPeriodic?
- **A:** setTimer executes once after delay; setPeriodic executes repeatedly

---

## Card 5: Event Bus
- **Q:** What is the Vert.x event bus?
- **A:** Mechanism for communication between verticles (send, publish, consume)

---

## Card 6: send vs publish
- **Q:** What is the difference between send and publish on event bus?
- **A:** send is point-to-point (one consumer gets reply); publish is pub-sub (all consumers get message)

---

## Card 7: executeBlocking
- **Q:** When should executeBlocking be used?
- **A:** For blocking operations (database calls, file I/O) to avoid blocking event loop

---

## Card 8: Router
- **Q:** What is Router in Vert.x Web?
- **A:** Maps HTTP requests to handlers based on path, method, etc.

---

## Card 9: RoutingContext
- **Q:** What is RoutingContext in Vert.x Web?
- **A:** Provides access to request, response, path params, query params, body, etc.

---

## Card 10: WebClient
- **Q:** What is WebClient used for?
- **A:** Non-blocking HTTP client for making outbound HTTP requests

---

## Card 11: Circuit Breaker
- **Q:** What does Circuit Breaker do?
- **A:** Prevents cascading failures by failing fast when service is unavailable

---

## Card 12: Message Codec
- **Q:** What is a message codec used for?
- **A:** Serializes/deserializes objects sent over the event bus

---

## Card 13: Promise
- **Q:** What is a Promise in Vert.x?
- **A:** Represents a future result that can be succeeded or failed

---

## Card 14: Future
- **Q:** What is a Future in Vert.x?
- **A:** Represents a result that will be available sometime in the future

---

## Card 15: Worker Verticle
- **Q:** What is a worker verticle?
- **A:** A verticle that runs on the worker pool, suitable for blocking operations

---

## Card 16: Context
- **Q:** What is Vert.x Context?
- **A:** Provides access to event loop and allows scheduling code execution

---

## Card 17: Clustered Vert.x
- **Q:** What is clustered Vert.x?
- **A:** Vert.x running on multiple nodes with shared event bus

---

## Card 18: WebSocket
- **Q:** How do you handle WebSocket in Vert.x?
- **A:** Use server.webSocketHandler(ws -> ...) to handle WebSocket connections

---

## Card 19: @VertxGen
- **Q:** What is @VertxGen annotation?
- **A:** Generates async proxy for service interfaces to call across event bus

---

## Card 20: BodyHandler
- **Q:** What is BodyHandler used for?
- **A:** Enables reading of request body (JSON, form data, files)

---

## Quick Reference

| Class/Method | Purpose |
|--------------|---------|
| AbstractVerticle | Base class for verticles |
| Vertx.vertx() | Create Vertx instance |
| vertx.deployVerticle() | Deploy a verticle |
| vertx.setTimer() | Schedule one-time task |
| vertx.setPeriodic() | Schedule repeating task |
| vertx.executeBlocking() | Run blocking code |
| eventBus().send() | Request-reply message |
| eventBus().publish() | Fire-and-forget |
| eventBus().consumer() | Register message handler |
| Router.router() | Create HTTP router |
| router.get/post/put/delete | Add route handlers |
| ctx.pathParam() | Get path parameter |
| ctx.queryParam() | Get query parameter |
| ctx.body() | Get request body |
| ctx.json() | Write JSON response |
| WebClient.create() | Create HTTP client |
| CircuitBreaker | Fault tolerance |
| ServerWebSocket | WebSocket handling |