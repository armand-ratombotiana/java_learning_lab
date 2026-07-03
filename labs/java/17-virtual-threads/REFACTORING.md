# Refactoring — Virtual Threads

## Old: Callback Hell (Reactive)
```java
service.fetchUser(id)
    .thenCompose(user -> service.fetchOrders(user.id()))
    .thenAccept(orders -> ui.show(orders))
    .exceptionally(err -> handle(err));
```

## New: Virtual Threads (Synchronous)
```java
try {
    User user = service.fetchUser(id);
    List<Order> orders = service.fetchOrders(user.id());
    ui.show(orders);
} catch (Exception e) {
    handle(e);
}
```

## Old: Thread Pool Pooling
```java
ExecutorService pool = Executors.newFixedThreadPool(100);
pool.submit(task);
```

## New: Virtual Thread Executor
```java
ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
exec.submit(task);
```

## Old: Lock + synchronized (pinning)
```java
synchronized void increment() { count++; }
```

## New: ReentrantLock (no pinning)
```java
private final Lock lock = new ReentrantLock();
void increment() { lock.lock(); try { count++; } finally { lock.unlock(); } }
```

## Old: Async Web Server
```java
HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
server.setExecutor(Executors.newFixedThreadPool(200));
```

## New: Virtual Thread Server
```java
server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
```
