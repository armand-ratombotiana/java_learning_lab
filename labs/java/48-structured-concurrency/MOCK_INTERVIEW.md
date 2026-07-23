# Mock Interview Transcript: Structured Concurrency

## Interviewer: Staff Engineer, Oracle
## Candidate: Senior Java developer
## Time: 40 minutes
## Focus: StructuredTaskScope, error handling, ScopedValue

---

**Q1: What problem does Structured Concurrency solve?**

**Candidate**: In unstructured concurrency (raw threads, ExecutorService), tasks launched by a parent can outlive the parent. Errors are hard to propagate — a child failure doesn't automatically cancel siblings. Threads/connections can leak if a child is forgotten. Structured concurrency ensures: (1) Tasks have a clear scope — all children complete before the scope closes. (2) Error propagation — if one child fails, others are cancelled. (3) No thread leakage — structured concurrency guarantees cleanup.

**Interviewer**: Write an order processing flow using StructuredTaskScope.

**Candidate**: 
```java
Response processOrder(OrderRequest req) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Future<User> user = scope.fork(() -> userService.getUser(req.userId()));
        Future<Inventory> inv = scope.fork(() -> inventoryService.check(req.sku()));
        
        scope.join();           // Wait for all or fail fast
        scope.throwIfFailed();   // Propagate first exception
        
        // Both succeeded — now depend on both results
        try (var scope2 = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<PaymentResult> payment = scope2.fork(() -> 
                paymentService.charge(user.resultNow(), req.amount()));
            Future<Shipment> shipment = scope2.fork(() -> 
                shipmentService.schedule(inv.resultNow(), req.address()));
            
            scope2.join();
            scope2.throwIfFailed();
            
            return new Response(payment.resultNow(), shipment.resultNow());
        }
    }
}
```

**Interviewer**: What happens if `userService.getUser()` throws an exception?

**Candidate**: Since we use `ShutdownOnFailure`, the scope shuts down immediately: (1) `throwIfFailed()` rethrows the exception (wrapped if needed). (2) The inventory service subtask is cancelled (via `Future.cancel()`). (3) The scope2 and its children are cancelled. (4) The caller gets the original exception.

**Interviewer**: Compare `ShutdownOnFailure` with `ShutdownOnSuccess`.

**Candidate**: `ShutdownOnFailure`: Shutdown when any subtask fails. Use for: all subtasks must succeed (AND-composition). `ShutdownOnSuccess`: Shutdown when any subtask succeeds. Use for: first successful result (OR-composition, like race to first result).

**Interviewer**: How does `ScopedValue` differ from `ThreadLocal` in the context of structured concurrency?

**Candidate**: 
```java
// ThreadLocal — inherited by child threads, mutable, high overhead
private static final ThreadLocal<User> USER = new ThreadLocal<>();
USER.set(user);  // Mutable
executor.submit(() -> doWork(USER.get()));  // Must explicitly pass

// ScopedValue — immutable, inherited by scope forks, lower overhead
private static final ScopedValue<User> USER = ScopedValue.newInstance();
ScopedValue.where(USER, user).run(() -> {
    // USER.get() works in all subtasks within this scope
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        scope.fork(() -> processOrder(USER.get()));  // Inherits USER
        scope.join();
    }
});
```

**Interviewer**: How do you propagate context like request IDs across virtual threads?

**Candidate**: Use ScopedValue:
```java
private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

void handleRequest(HttpExchange exchange) {
    String reqId = exchange.getRequestHeaders().getFirst("X-Request-ID");
    ScopedValue.where(REQUEST_ID, reqId).run(() -> process(exchange));
}

void process(HttpExchange exchange) {
    log.info("Processing request {}", REQUEST_ID.get());  // Works in all subtasks
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        scope.fork(() -> fetchUser());
        scope.fork(() -> fetchOrder());
        scope.join();
    }
}
```

**Interviewer**: Can you create custom shutdown policies in StructuredTaskScope?

**Candidate**: Yes, extend `StructuredTaskScope<T>`:
```java
class ShutdownIfQuorumReached<T> extends StructuredTaskScope<T> {
    private final int quorum;
    private final List<T> results = new ArrayList<>();
    
    ShutdownIfQuorumReached(int quorum) { this.quorum = quorum; }
    
    @Override
    protected void handleComplete(Future<T> future) {
        if (future.state() == Future.State.SUCCESS) {
            synchronized (this) {
                results.add(future.resultNow());
                if (results.size() >= quorum) shutdown();
            }
        }
    }
    
    List<T> results() { return List.copyOf(results); }
}
```

**Interviewer**: What happens if a subtask never completes?

**Candidate**: The `scope.join()` call would block indefinitely. Use `scope.joinUntil(Instant.now().plusSeconds(5))` for a deadline. If the deadline passes, `joinUntil` returns, and you can check which futures are still pending. The scope's `close()` method interrupts any unfinished subtasks.

**Interviewer**: Final: How does structured concurrency improve debugging and error handling?

**Candidate**: (1) Clear stack traces — the relationship between parent and child tasks is maintained. (2) No orphaned tasks — the scope ensures all children complete. (3) Fail-fast — one failure cancels all related work, preventing cascading failures. (4) Resource safety — no thread/connection leaks from forgotten tasks. (5) Deadline propagation — consistent timeout handling for the entire bundle of work.

---

## Feedback

**Strengths**:
- Clear problem statement for structured concurrency
- Correct nested scope usage
- ShutdownOnFailure vs ShutdownOnSuccess
- ScopedValue vs ThreadLocal comparison
- Custom shutdown policy implementation

**Areas for Improvement**:
- Could discuss `scope.joinUntil()` for deadline
- Mention `adopt()` for transferring context

**Score**: 5/5 — Expert structured concurrency knowledge
