# Step by Step — Virtual Threads

## Step 1: Platform Thread Baseline
```java
ExecutorService exec = Executors.newFixedThreadPool(100);
for (int i = 0; i < 10_000; i++) {
    exec.submit(() -> blockingTask());
}
```

## Step 2: Switch to Virtual Threads (minimal change)
```java
ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
for (int i = 0; i < 10_000; i++) {
    exec.submit(() -> blockingTask());
}
```
No other code changes needed.

## Step 3: Direct Virtual Thread Creation
```java
Thread vThread = Thread.ofVirtual()
    .name("my-virtual")
    .start(() -> doWork());
vThread.join(); // Wait for completion
```

## Step 4: Structured Concurrency
```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    scope.fork(this::task1);
    scope.fork(this::task2);
    scope.join();               // Wait for all
    scope.throwIfFailed();      // Handle errors
} // Scope closed → all tasks completed or cancelled
```

## Step 5: Monitor Pinning
Add JVM flag: `-Djdk.tracePinnedThreads=full`

Run and check logs for `"Pinned"` warnings indicating pinning points.
