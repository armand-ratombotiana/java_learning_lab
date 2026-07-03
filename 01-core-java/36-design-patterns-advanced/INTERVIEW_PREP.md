# Module 36: Advanced Design Patterns - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the Circuit Breaker Pattern, and what problem does it solve?
**Answer**:
The Circuit Breaker pattern is a fault-tolerance mechanism used in distributed systems. When a microservice calls a downstream service that is unresponsive (e.g., timing out), continuously retrying that call will exhaust thread pools and resources, causing the caller service to crash as well (Cascading Failure).
The Circuit Breaker acts as an electrical circuit breaker. When the failure rate exceeds a threshold, the breaker "Opens", immediately rejecting all subsequent requests with a fallback response, preventing resource exhaustion. It occasionally allows a single request through (Half-Open) to check if the downstream service has recovered. If it has, the circuit "Closes" again.

### Q2: Explain the Promise/Future pattern. How does `CompletableFuture` improve upon the original Java `Future`?
**Answer**:
The Promise/Future pattern acts as a placeholder for a result that is initially unknown because its computation is not yet complete. 
The original Java `Future` (Java 5) was limited because the only way to get its result was to call `.get()`, which *blocked* the thread until the result was ready, defeating the purpose of asynchronous programming.
`CompletableFuture` (Java 8) allows for non-blocking, declarative, functional chaining. You can attach callbacks like `.thenApply()`, `.thenAccept()`, or `.exceptionally()` that trigger automatically when the task finishes, keeping the calling thread completely free.

### Q3: What is the Strangler Fig Pattern?
**Answer**:
The Strangler Fig pattern is a popular architectural pattern for migrating legacy monolithic applications to microservices. Instead of attempting a massive, risky "Big Bang" rewrite, a proxy/API gateway is placed in front of the monolith. New features are written as separate microservices. Over time, existing features are migrated one by one to new microservices, and the API gateway routes traffic accordingly. Eventually, the new services "strangle" the monolith, replacing all its functionality until the monolith can safely be deleted.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Composing Asynchronous Operations
**Problem**: You have two independent APIs: `fetchUser(String id)` and `fetchUserOrders(String id)`. Both are extremely slow and return a `CompletableFuture`. Write a method that calls both simultaneously, waits for both to complete, and combines their results into a `UserProfile` object.

**Solution**:
Do not call `.get()` on the first future before starting the second, as that runs them sequentially. Use `CompletableFuture.allOf()` or `.thenCombine()`.

```java
public CompletableFuture<UserProfile> getCombinedProfile(String userId) {
    CompletableFuture<User> userFuture = fetchUser(userId);
    CompletableFuture<List<Order>> ordersFuture = fetchUserOrders(userId);

    // thenCombine waits for both futures to complete and then applies the bi-function
    return userFuture.thenCombine(ordersFuture, (user, orders) -> {
        return new UserProfile(user.getName(), orders);
    });
}
```