# Exercises — Virtual Threads

## Beginner
1. Create 10,000 virtual threads that each print "Hello from VT" and join them.
2. Compare creation time: 10,000 platform threads vs 10,000 virtual threads.
3. Use `Executors.newVirtualThreadPerTaskExecutor()` to run 5 tasks.

## Intermediate
4. Write a simple HTTP server that uses virtual threads (one per connection).
5. Use `StructuredTaskScope` to fetch two API responses in parallel and combine them.
6. Add `-Djdk.tracePinnedThreads=full` — identify pinning in a synchronized block.

## Advanced
7. Refactor a reactive (`CompletableFuture`) pipeline to virtual threads.
8. Compare throughput: fixed thread pool vs virtual thread pool for 1000 IO-bound tasks.
9. Use `ReentrantLock` to eliminate pinning in a virtual thread.

## Debugging
10. Enable JFR and record virtual thread events during a load test.
11. Reproduce pinning: call `synchronized` with `Thread.sleep()` inside a virtual thread.

## Reflection
12. How would you port a legacy thread-per-connection server to virtual threads?
