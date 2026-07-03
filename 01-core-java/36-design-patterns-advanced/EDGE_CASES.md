 # Module 36: Advanced Design Patterns - Edge Cases & Pitfalls

---

## Pitfall 1: Open Circuit Breakers Ignoring Recovery

### ❌ Wrong
Configuring a Circuit Breaker but failing to set an appropriate "half-open" state timeout. If the circuit opens and stays open indefinitely, the system will never attempt to recover, permanently disabling that feature until manual intervention.

### ✅ Correct
Always configure timeouts and a "Half-Open" threshold. The breaker should periodically let a single test request through to see if the downstream service has recovered.

---

## Pitfall 2: Overusing the Strangler Fig Pattern

### ❌ Wrong
Attempting to strangle a monolith that doesn't actually need to be rewritten. Applying the Strangler Fig pattern adds massive complexity (API Gateways, data syncing between old and new DBs). Doing this for a low-traffic internal tool is a waste of resources.

### ✅ Correct
Only use the Strangler Fig pattern for mission-critical monoliths that are actively stifling development velocity and cannot be retired overnight due to their size.

---

## Pitfall 3: Blocking Inside Futures/Promises

### ❌ Wrong
Using `CompletableFuture.supplyAsync()` but then calling a blocking API (like a synchronous database call) inside the lambda without providing a custom `Executor`. This blocks the common `ForkJoinPool`, starving all other asynchronous tasks in the JVM.

### ✅ Correct
If you must perform blocking I/O inside a Future, always pass a custom `ExecutorService` (like a cached thread pool) dedicated to handling blocking tasks.