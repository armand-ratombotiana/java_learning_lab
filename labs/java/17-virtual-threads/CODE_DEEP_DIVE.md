# Code Deep Dive — Virtual Threads

## Simple Virtual Thread Server
```java
void handleRequest(ServerSocket serverSocket) {
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        while (true) {
            Socket socket = serverSocket.accept();
            executor.submit(() -> processRequest(socket));
        }
    }
}

void processRequest(Socket socket) {
    try (socket) {
        String request = new String(socket.getInputStream().readAllBytes());
        String response = handle(request);
        socket.getOutputStream().write(response.getBytes());
    } catch (IOException e) {
        log.error("Request failed", e);
    }
}
```

## StructuredTaskScope Example
```java
public Response handleRequest(Request req) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Future<User> user = scope.fork(() -> db.findUser(req.userId()));
        Future<Config> config = scope.fork(() -> configService.getConfig());

        scope.join();            // Wait for both
        scope.throwIfFailed();   // Propagate exceptions

        return new Response(user.resultNow(), config.resultNow());
    }
}
```

## Avoid Pinning with ReentrantLock
```java
// Instead of:
synchronized (this) { /* critical section */ }

// Use (avoids pinning virtual thread):
private final Lock lock = new ReentrantLock();
lock.lock();
try { /* critical section */ }
finally { lock.unlock(); }
```
