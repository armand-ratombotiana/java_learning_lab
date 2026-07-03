# Refactoring — Concurrency

## Raw Thread → ExecutorService
```java
// Before
new Thread(() -> doWork()).start();
new Thread(() -> doWork()).start();

// After
ExecutorService exec = Executors.newCachedThreadPool();
exec.submit(() -> doWork());
exec.submit(() -> doWork());
exec.shutdown();
```

## synchronized → ReentrantLock (when needed)
```java
// Before
public synchronized void transfer(BankAccount to, int amount) { ... }

// After (when tryLock, fair lock, or condition needed)
private final Lock lock = new ReentrantLock();
public void transfer(BankAccount to, int amount) {
    lock.lock();
    try { ... }
    finally { lock.unlock(); }
}
```

## Callback → CompletableFuture
```java
// Before
public void fetchData(Callback<String> cb) { ... }

// After
public CompletableFuture<String> fetchData() { ... }
// Usage: fetchData().thenApply(String::toUpperCase).thenAccept(System.out::println);
```

## Manual Thread Pool → Parallel Stream
```java
// Before
ExecutorService exec = Executors.newFixedThreadPool(4);
try { /* manual task distribution */ }
finally { exec.shutdown(); }

// After
list.parallelStream().map(expensiveFunc).toList();
```
