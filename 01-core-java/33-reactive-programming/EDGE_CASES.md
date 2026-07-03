# Edge Cases & Pitfalls: Reactive Programming

Reactive programming requires a complete paradigm shift. Treating a reactive stream like a traditional imperative loop will lead to silent failures, thread blocking, and memory leaks.

## 1. Blocking the Reactive Thread
*   **The Scenario**: You receive an item in `onNext()`, and you need to save it to a database using standard JDBC.
    ```java
    @Override
    public void onNext(String item) {
        jdbcTemplate.save(item); // BLOCKING CALL!
        subscription.request(1);
    }
    ```
*   **The Pitfall**: Reactive frameworks (like Project Reactor or RxJava) use a very small number of event-loop threads (typically one per CPU core). If you make a blocking I/O call inside `onNext()`, you freeze that event-loop thread. If you freeze all event-loop threads, your entire application grinds to a halt.
*   **Mitigation**: **Never block in a reactive stream**. You must use non-blocking, reactive database drivers (like R2DBC) or offload the blocking call to a dedicated, bounded thread pool designed for legacy blocking I/O.

## 2. Breaking the Backpressure Contract
*   **The Scenario**: You write a custom `Publisher`. When a subscriber subscribes, you immediately start pushing 10,000 items to `subscriber.onNext()`.
*   **The Pitfall**: You violated the Reactive Streams specification. A Publisher *must not* call `onNext()` more times than the Subscriber has requested via `subscription.request(n)`. If you push data without being asked, you overwhelm the subscriber, eventually causing an `OutOfMemoryError`.
*   **Mitigation**: The Publisher must maintain an internal counter of requested items and only emit data when the counter is greater than zero.

## 3. Swallowed Exceptions in `onError`
*   **The Scenario**: An exception is thrown during the processing of an item in `onNext()`. The framework catches it and routes it to `onError()`. However, your `onError()` implementation is empty or just prints a stack trace without halting the upstream process or notifying the user appropriately.
*   **The Pitfall**: The stream terminates silently. Because reactive streams are asynchronous, the original calling thread has long since moved on. The exception does not bubble up to a global try-catch block.
*   **Mitigation**: Always implement robust logging and error routing in `onError()`. In frameworks like Reactor, use operators like `onErrorResume` or `onErrorMap` to handle failures gracefully and keep the stream alive if appropriate.

## 4. The `request(Long.MAX_VALUE)` Trap
*   **The Scenario**: You write a `Subscriber` and in `onSubscribe()`, you call `subscription.request(Long.MAX_VALUE)`.
*   **The Pitfall**: You have effectively disabled backpressure. You are telling the Publisher: "Send me everything you have as fast as you can." If the Publisher is a fast network socket and your Subscriber writes to a slow disk, your application will run out of memory buffering the incoming data.
*   **Mitigation**: Only request what you can process. A common pattern is to `request(1)` in `onSubscribe()`, process the item in `onNext()`, and then `request(1)` again at the end of `onNext()`.

## 5. Forgetting to Subscribe
*   **The Scenario**: You use a library like Project Reactor to build a complex data pipeline: `Mono.just("Data").map(String::toUpperCase).doOnNext(System.out::println);`
*   **The Pitfall**: Nothing prints to the console. Why? Because reactive streams are **lazy**. Building the pipeline simply creates a blueprint. No data flows until a terminal operation (a subscription) is invoked.
*   **Mitigation**: You must always call `.subscribe()` at the end of your reactive chain to trigger execution.