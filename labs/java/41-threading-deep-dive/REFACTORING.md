# Refactoring Threading Code

## From Raw Threads to ThreadPoolExecutor
**Before:** Creating threads per task:
```java
new Thread(() -> process(request)).start();
```
**After:** Thread pool reuse:
```java
executor.submit(() -> process(request));
```
Benefits: thread reuse, bounded concurrency, graceful shutdown, rejection handling.

## From BlockingQueue + Pool to ThreadPoolExecutor
**Before:** Manual worker loop with BlockingQueue:
```java
while (running) { Runnable task = queue.take(); task.run(); }
```
**After:** Use ThreadPoolExecutor directly.
Benefits: built-in lifecycle management, thread creation policy, rejection handling, monitoring.

## From Nested Futures to CompletableFuture
**Before:** Future.get() chains:
```java
Future<String> f1 = executor.submit(task1);
String r1 = f1.get(); // blocks
Future<String> f2 = executor.submit(() -> process(r1));
```
**After:** CompletableFuture pipeline:
```java
CompletableFuture.supplyAsync(task1)
    .thenApplyAsync(this::process)
    .thenAccept(System.out::println);
```
Benefits: non-blocking, composable, declarative error handling.

## From Unstructured to Structured Concurrency
**Before:** Orphaned subtasks:
```java
Thread t1 = new Thread(task1);
Thread t2 = new Thread(task2);
t1.start(); t2.start();
// if method returns here, t2 keeps running
```
**After:** Structured scope:
```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    scope.fork(task1);
    scope.fork(task2);
    scope.join();
}
```
Benefits: all subtasks complete before returning, no orphaned threads.

## From synchronized to ReentrantLock
**Before:** Synchronized block:
```java
synchronized (lock) { shared++; }
```
**After:** ReentrantLock (when needed):
```java
lock.lock();
try { shared++; } finally { lock.unlock(); }
```
Benefits: tryLock, fair/unfair selection, Condition, lock inspection.
