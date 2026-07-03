# Module 48: Reactive Streams & Flow API - Edge Cases & Pitfalls

---

## Pitfall 1: Forgetting to Request Items (Deadlocks)

### ❌ Wrong
Creating a custom `Subscriber` but forgetting to call `subscription.request(n)` inside the `onSubscribe` or `onNext` methods. The Publisher will sit idle forever waiting for permission to emit data, resulting in a silent deadlock.
```java
public void onSubscribe(Flow.Subscription subscription) {
    this.subscription = subscription;
    // ❌ Missing: subscription.request(1);
}
```

### ✅ Correct
Always prime the pump by requesting the first batch of items in `onSubscribe`, and remember to request more inside `onNext` as you finish processing them.

---

## Pitfall 2: Blocking Inside onNext

### ❌ Wrong
Performing heavy blocking I/O (like a database save or `Thread.sleep`) directly inside the `onNext()` method of a Subscriber. This blocks the thread pool shared by the Publisher (e.g., the `ForkJoinPool` backing the `SubmissionPublisher`), severely degrading the performance of the entire application.

### ✅ Correct
If you must do blocking work, dispatch it to a separate, dedicated `ExecutorService` inside your `onNext()` method, and only call `subscription.request()` once that async task completes.

---

## Pitfall 3: Re-using Subscriptions

### ❌ Wrong
Attempting to subscribe the exact same `Subscriber` instance to multiple `Publisher`s. The internal state (like the `Subscription` reference) will get overwritten and corrupted, leading to chaotic behavior and `IllegalStateException`s.

### ✅ Correct
Subscribers should generally be stateful and single-use. Always instantiate a `new MySubscriber()` for every `publisher.subscribe()` call.