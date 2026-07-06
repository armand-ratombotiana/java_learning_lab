# Mini Project: Custom Thread Pool Implementation

## Objective
Build a custom thread pool from scratch using only core Java concurrency primitives (no ExecutorService). Your thread pool must support a fixed number of worker threads, a work queue, graceful shutdown, and rejection handling.

## Requirements
1. Implement a `CustomThreadPool` class with constructor `CustomThreadPool(int corePoolSize, int maxPoolSize, BlockingQueue<Runnable> queue)`
2. Worker threads should loop polling the queue with a configurable keep-alive timeout
3. Support `execute(Runnable)` and `shutdown()` methods
4. Implement a `RejectionHandler` interface with `void rejectedExecution(Runnable r, CustomThreadPool pool)`
5. Gracefully handle InterruptedException during shutdown

## Stretch Goals
- Add `submit(Callable<T>)` returning a `Future<T>` using a custom `FutureTask`
- Implement `allowCoreThreadTimeOut(boolean)` like ThreadPoolExecutor
- Add `beforeExecute` and `afterExecute` hooks

## Suggested Test Scenarios
1. Submit 100 tasks, verify all complete
2. Submit more tasks than capacity, verify rejection handler fires
3. Verify threads terminate after keep-alive timeout
4. Test reentrant submission (a task submitting another task)

## Deliverables
- `CustomThreadPool.java` with full implementation
- `CustomThreadPoolTest.java` with JUnit 5 tests covering all scenarios
- A short reflective document explaining your design decisions compared to `java.util.concurrent.ThreadPoolExecutor`
