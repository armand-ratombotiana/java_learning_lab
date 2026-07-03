# Step by Step — Concurrency

## Step 1: No Concurrency — Single Thread
```java
public long process(List<Job> jobs) {
    long total = 0;
    for (Job j : jobs) {
        total += j.compute();
    }
    return total;
}
```

## Step 2: Manual Threads
```java
List<Thread> threads = new ArrayList<>();
for (Job j : jobs) {
    Thread t = new Thread(() -> processOne(j));
    threads.add(t);
    t.start();
}
for (Thread t : threads) t.join();
```

## Step 3: ExecutorService
```java
ExecutorService exec = Executors.newFixedThreadPool(4);
List<Future<Long>> futures = new ArrayList<>();
for (Job j : jobs) {
    futures.add(exec.submit(j::compute));
}
long total = 0;
for (Future<Long> f : futures) total += f.get();
exec.shutdown();
```

## Step 4: CompletableFuture
```java
List<CompletableFuture<Long>> futures = jobs.stream()
    .map(j -> CompletableFuture.supplyAsync(j::compute, executor))
    .toList();

long total = futures.stream()
    .map(CompletableFuture::join)
    .mapToLong(Long::longValue)
    .sum();
```

## Step 5: Parallel Stream (simplest)
```java
long total = jobs.parallelStream()
    .mapToLong(Job::compute)
    .sum();
```
