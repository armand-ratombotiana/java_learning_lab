# Theory — Concurrency

## Threads
The smallest unit of execution. Java threads map to OS threads (platform threads) or are managed by the JVM (virtual threads).

```java
// Extending Thread
class Worker extends Thread {
    public void run() { System.out.println("Working..."); }
}

// Implementing Runnable
Thread t = new Thread(() -> System.out.println("Working..."));
```

## Synchronization
Prevents race conditions by ensuring mutual exclusion:
```java
public synchronized void increment() { count++; }
// Or:
public void increment() {
    synchronized(this) { count++; }
}
```

## Locks (java.util.concurrent.locks)
More flexible than synchronized:
```java
Lock lock = new ReentrantLock();
lock.lock();
try { /* critical section */ }
finally { lock.unlock(); }
```

## ExecutorService
Manages thread pools:
```java
ExecutorService executor = Executors.newFixedThreadPool(4);
executor.submit(() -> System.out.println("Task"));
executor.shutdown();
```

## CompletableFuture
Asynchronous, composable futures:
```java
CompletableFuture.supplyAsync(() -> fetchData())
    .thenApply(data -> transform(data))
    .thenAccept(result -> System.out.println(result));
```
