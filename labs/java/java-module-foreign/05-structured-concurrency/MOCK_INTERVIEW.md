# Mock Interview: Structured Concurrency

**Interviewer:** "Design an API gateway that calls three downstream services with a 2-second timeout. Any failure should abort all calls."

**Candidate:** "I'd use `StructuredTaskScope.ShutdownOnFailure` with a deadline:

```java
record GatewayResponse(UserProfile user, Inventory inv, Pricing price) {}

GatewayResponse getAggregated(String userId, String sku) throws Exception {
    Instant deadline = Instant.now().plusSeconds(2);

    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Future<UserProfile> user    = scope.fork(() ->
                userService.get(userId));
        Future<Inventory> inv       = scope.fork(() ->
                inventory.check(sku));
        Future<Pricing> price       = scope.fork(() ->
                pricing.get(sku));

        scope.joinUntil(deadline);
        scope.throwIfFailed();      // throws if any task failed

        if (!user.isDone() || !inv.isDone() || !price.isDone()) {
            scope.shutdown();       // cancel remaining
            throw new TimeoutException("Service timeout");
        }

        return new GatewayResponse(
                user.resultNow(),
                inv.resultNow(),
                price.resultNow()
        );
    }
}
```

**Interviewer:** "How would you handle partial results — return what we have after timeout?"

**Candidate:** "I'd extend `StructuredTaskScope`:

```java
class PartialResultScope<T> extends StructuredTaskScope<T> {
    private volatile Instant deadline;
    private final List<T> results = new CopyOnWriteArrayList<>();

    PartialResultScope(Instant deadline) { this.deadline = deadline; }

    @Override
    protected void handleComplete(Future<T> future) {
        if (Instant.now().isAfter(deadline)) {
            shutdown();
            return;
        }
        if (future.state() == Future.State.SUCCESS) {
            results.add(future.resultNow());
        }
    }

    List<T> partialResults() { return List.copyOf(results); }
}
```

This gives the caller a best-effort result set — useful for dashboards or analytics where partial data is acceptable."
