# Mock Interview: Virtual Threads

**Interviewer:** "You're building an API gateway that calls three downstream services. Model this using virtual threads."

**Candidate:** "I'd use `StructuredTaskScope.ShutdownOnFailure` to subscribe to all three services concurrently:

```java
record AggregatedResponse(UserProfile user, Inventory inventory, Pricing pricing) {}

AggregatedResponse handle(String userId, String sku) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Future<UserProfile> user   = scope.fork(() -> userService.get(userId));
        Future<Inventory> inv     = scope.fork(() -> inventoryService.check(sku));
        Future<Pricing> pricing   = scope.fork(() -> pricingService.get(sku));

        scope.join();
        scope.throwIfFailed();

        return new AggregatedResponse(
            user.resultNow(),
            inv.resultNow(),
            pricing.resultNow()
        );
    }
}
```

If any service fails, the scope shuts down the other two immediately — no wasted work. Each subtask runs on a virtual thread, so I can handle thousands of concurrent requests without a huge thread pool."

**Interviewer:** "How would you pass the authenticated user context?"

**Candidate:** "I'd use a `ScopedValue`:

```java
public static final ScopedValue<User> AUTH_USER = ScopedValue.newInstance();

void onRequest(Request req) {
    User user = authenticate(req);
    ScopedValue.where(AUTH_USER, user).run(() -> {
        var handler = new RequestHandler();
        handler.handle(req);
    });
}
```

Inside `handle()` and any forked task, `AUTH_USER.get()` transparently returns the authenticated user — no parameter threading needed. When the request scope exits, the value is GC'd — no thread-local memory leaks."
