# Module 36: Advanced Design Patterns - Deep Dive

**Difficulty Level**: Expert  
**Prerequisites**: Modules 01-35 (especially Module 11: Design Patterns)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Concurrency Patterns (Thread Pool, Promise)](#concurrency)
2. [Reactive Patterns (Observer/Pub-Sub)](#reactive)
3. [Enterprise Integration Patterns (EIP)](#enterprise)
4. [Microservices Patterns (Circuit Breaker, Strangler)](#microservices)

---

## 1. Concurrency Patterns <a name="concurrency"></a>
- **Thread Pool Pattern**: Reuses a fixed number of threads to execute tasks, avoiding the overhead of creating and destroying threads repeatedly (e.g., `ExecutorService`).
- **Promise/Future Pattern**: Represents a value that may not be available yet. Allows asynchronous non-blocking execution (e.g., `CompletableFuture`).

```java
CompletableFuture.supplyAsync(() -> performHeavyTask())
    .thenApply(result -> process(result))
    .thenAccept(finalResult -> System.out.println(finalResult));
```

---

## 2. Reactive Patterns (Observer/Pub-Sub) <a name="reactive"></a>
- **Publisher-Subscriber (Pub-Sub)**: An asynchronous messaging pattern where senders (publishers) categorize messages into classes without knowledge of the receivers (subscribers). Used heavily in modern event-driven systems (e.g., Kafka, Project Reactor).

---

## 3. Enterprise Integration Patterns (EIP) <a name="enterprise"></a>
EIPs provide standardized solutions for integrating multiple disparate systems.
- **Message Router**: Consumes a message from one channel and publishes it to one of multiple channels based on a set of conditions.
- **Message Translator**: Modifies the message payload before passing it to the next system (e.g., XML to JSON).
- **Aggregator**: Receives multiple messages and combines them into a single message.

---

## 4. Microservices Patterns <a name="microservices"></a>
- **Circuit Breaker**: Prevents a network or service failure from cascading to other services. If a service fails continuously, the breaker "opens," immediately failing subsequent calls until the service recovers.
- **Strangler Fig Pattern**: Incrementally migrates a legacy monolithic system by gradually replacing specific pieces of functionality with new applications and services, until the old system can be completely removed.