# Vert.x Learning Quiz

## Section 1: Vert.x Basics

**Question 1:** What is a Verticle in Vert.x?
- A) A database connection
- B) The basic unit of deployment
- C) A web framework
- D) A configuration file

**Answer:** B) Verticle is the basic unit of deployment in Vert.x

---

**Question 2:** How do you deploy a verticle in Vert.x?
- A) new MyVerticle().run()
- B) vertx.deployVerticle(new MyVerticle())
- C) Verticle.deploy(MyVerticle.class)
- D) Vertx.instance().start(new MyVerticle())

**Answer:** B) vertx.deployVerticle(new MyVerticle())

---

**Question 3:** What is the default number of event loops in Vert.x?
- A) 1
- B) 2
- C) Number of CPU cores
- D) 10

**Answer:** C) Number of CPU cores

---

**Question 4:** Which annotation makes a class a verticle in Vert.x 4?
- A) @Verticle
- B) @Component
- C) No annotation needed, extend AbstractVerticle
- D) @Module

**Answer:** C) No annotation needed, extend AbstractVerticle

---

**Question 5:** What is Vert.x?
- A) A servlet container
- B) A reactive, event-driven framework
- C) A database
- D) A testing framework

**Answer:** B) Vert.x is a reactive, event-driven framework

---

## Section 2: Event Loop

**Question 6:** How do you schedule a one-time timer in Vert.x?
- A) vertx.setInterval()
- B) vertx.setTimer()
- C) vertx.schedule()
- D) vertx.timeout()

**Answer:** B) vertx.setTimer()

---

**Question 7:** What does executeBlocking do?
- A) Runs code on the main thread
- B) Runs blocking code on a worker thread
- C) Stops the event loop
- D) Executes immediately

**Answer:** B) Runs blocking code on a worker thread

---

**Question 8:** Which method sends a message on the event bus expecting a reply?
- A) publish()
- B) broadcast()
- C) send()
- D) post()

**Answer:** C) send() (request-response pattern)

---

**Question 9:** What is the event bus used for?
- A) Database connections only
- B) Inter-verticle communication
- C) HTTP routing
- D) File operations

**Answer:** B) Inter-verticle communication

---

**Question 10:** What happens if you block the event loop in Vert.x?
- A) Nothing
- B) Warning is logged
- C) Application hangs
- D) The operation succeeds

**Answer:** B) Warning is logged (blocked thread check)

---

## Section 3: Reactive Patterns

**Question 11:** What is the purpose of Router in Vert.x Web?
- A) Route network traffic
- B) Map HTTP requests to handlers
- C) Connect to databases
- D) Manage threads

**Answer:** B) Map HTTP requests to handlers

---

**Question 12:** Which pattern does Future/Promise implement?
- A) Observer pattern
- B) Callback pattern
- C) Synchronous pattern
- D) Queue pattern

**Answer:** B) Callback pattern (async result handling)

---

**Question 13:** What does Circuit Breaker protect against?
- A) Slow queries
- B) Cascading failures in distributed systems
- C) Memory leaks
- D) Invalid input

**Answer:** B) Cascading failures in distributed systems

---

**Question 14:** What is the purpose of @VertxGen?
- A) Generates random IDs
- B) Generates service proxies for async services
- C) Creates databases
- D) Builds HTTP clients

**Answer:** B) Generates service proxies for async services

---

**Question 15:** Which client is used for HTTP requests in Vert.x Web?
- A) HttpClient
- B) HttpURLConnection
- C) RestClient
- D) WebClient

**Answer:** D) WebClient (non-blocking HTTP client)

---

## Advanced Questions

**Question 16:** What is the difference between publish and send on the event bus?
- A) They are identical
- B) send() expects a reply, publish() is fire-and-forget
- C) publish() is slower
- D) send() is deprecated

**Answer:** B) send() is point-to-point (request-reply), publish() is pub-sub (all consumers)

---

**Question 17:** What is the purpose of a Worker Verticle?
- A) Handle HTTP requests faster
- B) Run blocking code
- C) Manage databases only
- D) Serve static files

**Answer:** B) Run blocking code on worker thread pool

---

**Question 18:** How do you handle body in POST request in Vert.x Web?
- A) ctx.getBody()
- B) ctx.body()
- C) ctx.request().body()
- D) ctx.data()

**Answer:** B) ctx.body()

---

**Question 19:** What is the purpose of BodyHandler?
- A) Compresses response
- B) Handles multipart form data and JSON bodies
- C) Encrypts data
- D) Logs requests

**Answer:** B) Handles multipart form data and JSON bodies

---

**Question 20:** Which method returns the request body as JSON in Vert.x?
- A) ctx.json()
- B) ctx.bodyAsJsonObject()
- C) ctx.getJson()
- D) ctx.getBodyAsJson()

**Answer:** B) ctx.body().asJsonObject() or ctx.bodyAsJsonObject()

---

## Score Interpretation

| Score | Level |
|-------|-------|
| 18-20 | Expert |
| 14-17 | Advanced |
| 10-13 | Intermediate |
| 5-9 | Beginner |
| < 5 | Foundation needed |