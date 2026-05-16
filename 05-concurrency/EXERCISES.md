# Concurrency Exercises

## Exercise 1: Thread Creation and Lifecycle

### Task
Create a simple thread pool and execute multiple tasks using it.

### Solution

```java
public class ThreadPoolDemo {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        List<Future<String>> futures = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            Future<String> future = executor.submit(() -> {
                Thread.sleep(500);
                return "Task " + taskId + " completed by " + Thread.currentThread().getName();
            });
            futures.add(future);
        }
        
        for (Future<String> future : futures) {
            System.out.println(future.get());
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}
```

---

## Exercise 2: Producer-Consumer Pattern

### Task
Implement a producer-consumer pattern using BlockingQueue.

### Solution

```java
public class ProducerConsumerDemo {
    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);
        
        // Producer
        Thread producer = new Thread(() -> {
            int value = 0;
            while (true) {
                try {
                    queue.put(value);
                    System.out.println("Produced: " + value);
                    value++;
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Producer");
        
        // Consumer
        Thread consumer = new Thread(() -> {
            while (true) {
                try {
                    Integer value = queue.take();
                    System.out.println("Consumed: " + value);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Consumer");
        
        producer.start();
        consumer.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            producer.interrupt();
            consumer.interrupt();
        }));
    }
}
```

---

## Exercise 3: Thread-Safe Counter with Synchronization

### Task
Create a thread-safe counter using synchronized keyword and ReentrantLock.

### Solution

```java
public class ThreadSafeCounter {
    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock();
    
    // Using synchronized
    public synchronized void incrementSync() {
        count++;
    }
    
    public synchronized int getCountSync() {
        return count;
    }
    
    // Using ReentrantLock
    public void incrementLock() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
    
    public int getCountLock() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ThreadSafeCounter counter = new ThreadSafeCounter();
        ExecutorService executor = Executors.newFixedThreadPool(100);
        
        for (int i = 0; i < 1000; i++) {
            executor.submit(counter::incrementLock);
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        System.out.println("Final count: " + counter.getCountLock());
    }
}
```

---

## Exercise 4: CompletableFuture for Async Operations

### Task
Use CompletableFuture to chain asynchronous operations.

### Solution

```java
public class CompletableFutureDemo {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        CompletableFuture<String> future = CompletableFuture
            .supplyAsync(() -> {
                sleep(100);
                return "Step 1";
            }, executor)
            .thenApply(result -> {
                return result + " -> Step 2";
            })
            .thenCompose(result -> {
                return CompletableFuture.supplyAsync(() -> {
                    sleep(100);
                    return result + " -> Step 3";
                }, executor);
            })
            .thenAccept(result -> System.out.println("Final: " + result));
        
        future.get();
        executor.shutdown();
    }
    
    static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { }
    }
}
```

---

## Exercise 5: Parallel Stream Processing

### Task
Process a large collection in parallel using parallel streams.

### Solution

```java
public class ParallelStreamDemo {
    public static void main(String[] args) {
        List<Integer> numbers = IntStream.rangeClosed(1, 1_000_000)
            .boxed()
            .toList();
        
        // Sequential processing
        long startSeq = System.nanoTime();
        long sumSeq = numbers.stream()
            .mapToLong(Integer::longValue)
            .sum();
        long endSeq = System.nanoTime();
        
        // Parallel processing
        long startPar = System.nanoTime();
        long sumPar = numbers.parallelStream()
            .mapToLong(Integer::longValue)
            .sum();
        long endPar = System.nanoTime();
        
        System.out.println("Sequential sum: " + sumSeq + " time: " + (endSeq - startSeq) / 1_000_000 + "ms");
        System.out.println("Parallel sum: " + sumPar + " time: " + (endPar - startPar) / 1_000_000 + "ms");
        
        // Parallel with reduce
        String result = numbers.parallelStream()
            .filter(n -> n % 2 == 0)
            .limit(10)
            .map(String::valueOf)
            .reduce("", (a, b) -> a + b + ",");
        System.out.println("Result: " + result);
    }
}
```

---

## Exercise 6: Thread-Local Storage

### Task
Use ThreadLocal to maintain per-thread state.

### Solution

```java
public class ThreadLocalDemo {
    private static final ThreadLocal<Date> threadLocalDate = ThreadLocal.withInitial(Date::new);
    private static final ThreadLocal<Integer> threadLocalCounter = ThreadLocal.withInitial(() -> 0);
    
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        for (int i = 0; i < 4; i++) {
            final int taskId = i;
            executor.submit(() -> {
                for (int j = 0; j < 5; j++) {
                    int counter = threadLocalCounter.get();
                    threadLocalCounter.set(counter + 1);
                    System.out.println("Thread: " + Thread.currentThread().getName() + 
                        " Task: " + taskId + " Counter: " + threadLocalCounter.get() + 
                        " Date: " + threadLocalDate.get());
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        System.out.println("Final counter in main: " + threadLocalCounter.get());
    }
}
```

---

## Exercise 7: CountDownLatch for Coordination

### Task
Use CountDownLatch to wait for multiple tasks to complete.

### Solution

```java
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        int numWorkers = 5;
        CountDownLatch latch = new CountDownLatch(numWorkers);
        ExecutorService executor = Executors.newFixedThreadPool(numWorkers);
        
        for (int i = 0; i < numWorkers; i++) {
            final int workerId = i;
            executor.submit(() -> {
                try {
                    System.out.println("Worker " + workerId + " starting...");
                    Thread.sleep(ThreadLocalRandom.current().nextLong(100, 1000));
                    System.out.println("Worker " + workerId + " completed");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        System.out.println("All workers completed");
        
        executor.shutdown();
    }
}
```

---

## Exercise 8: Concurrent Map Operations

### Task
Use ConcurrentHashMap for thread-safe map operations.

### Solution

```java
public class ConcurrentMapDemo {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        // Simulate word counting
        List<String> words = List.of("apple", "banana", "apple", "cherry", "banana", "apple");
        
        for (String word : words) {
            executor.submit(() -> {
                map.computeIfAbsent(word, k -> new AtomicInteger(0)).incrementAndGet();
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        System.out.println("Word counts: " + map);
        
        // Using computeIfPresent
        map.computeIfPresent("apple", (k, v) -> new AtomicInteger(v.get() + 10));
        System.out.println("After update: " + map);
        
        // Using merge
        map.merge("apple", new AtomicInteger(5), (existing, newVal) -> 
            new AtomicInteger(existing.get() + newVal.get()));
        System.out.println("After merge: " + map);
    }
}
```

---

## Exercise 9: Thread Pool Configuration

### Task
Configure a custom thread pool with specific parameters.

### Solution

```java
public class CustomThreadPoolDemo {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            4,                      // core pool size
            8,                      // maximum pool size
            60L, TimeUnit.SECONDS,  // keep-alive time
            new LinkedBlockingQueue<>(100),  // work queue
            new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("CustomPool-" + counter.getAndIncrement());
                    t.setDaemon(false);
                    return t;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()  // rejection policy
        );
        
        // Submit tasks
        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " running on " + Thread.currentThread().getName());
                try { Thread.sleep(100); } catch (InterruptedException e) { }
            });
        }
        
        executor.shutdown();
        try { executor.awaitTermination(30, TimeUnit.SECONDS); } catch (InterruptedException e) { }
        
        System.out.println("Pool stats: " + executor.getCompletedTaskCount() + " tasks completed");
    }
}
```

---

## Exercise 10: Phaser for Multi-Phase Tasks

### Task
Use Phaser to coordinate multi-phase tasks.

### Solution

```java
public class PhaserDemo {
    public static void main(String[] args) throws InterruptedException {
        Phaser phaser = new Phaser(3);  // 3 parties
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        for (int i = 0; i < 3; i++) {
            final int partyId = i;
            executor.submit(() -> {
                // Phase 1: Preparation
                System.out.println("Party " + partyId + " preparing...");
                sleep(100);
                System.out.println("Party " + partyId + " ready");
                phaser.arriveAndAwaitAdvance();
                
                // Phase 2: Execution
                System.out.println("Party " + partyId + " executing...");
                sleep(100);
                System.out.println("Party " + partyId + " done");
                phaser.arriveAndAwaitAdvance();
                
                // Phase 3: Cleanup
                System.out.println("Party " + partyId + " cleaning up...");
                sleep(50);
                System.out.println("Party " + partyId + " finished");
                phaser.arriveAndAwaitAdvance();
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
    
    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { }
    }
}
```

---

## Exercise 11: Read-Write Lock

### Task
Implement a read-write lock pattern for a shared resource.

### Solution

```java
public class ReadWriteLockDemo {
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Map<String, String> data = new HashMap<>();
    
    public void write(String key, String value) {
        rwLock.writeLock().lock();
        try {
            System.out.println("Writing " + key + "=" + value + " by " + Thread.currentThread().getName());
            Thread.sleep(100);
            data.put(key, value);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    public String read(String key) {
        rwLock.readLock().lock();
        try {
            System.out.println("Reading " + key + " by " + Thread.currentThread().getName());
            Thread.sleep(50);
            return data.get(key);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ReadWriteLockDemo demo = new ReadWriteLockDemo();
        
        // Write some data
        demo.write("name", "John");
        demo.write("age", "30");
        
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // Multiple readers
        for (int i = 0; i < 3; i++) {
            executor.submit(() -> System.out.println("Read: " + demo.read("name")));
        }
        
        // Writer
        executor.submit(() -> demo.write("name", "Jane"));
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}
```

---

## Exercise 12: Deadlock Prevention

### Task
Implement a deadlock-free transfer operation between accounts.

### Solution

```java
public class DeadlockFreeBank {
    private final Map<String, Account> accounts = new HashMap<>();
    
    public void addAccount(String name, int balance) {
        accounts.put(name, new Account(name, balance));
    }
    
    public boolean transfer(String from, String to, int amount) {
        Account fromAccount = accounts.get(from);
        Account toAccount = accounts.get(to);
        
        // Always acquire locks in consistent order (alphabetically)
        Account first = fromAccount.id.compareTo(toAccount.id) < 0 ? fromAccount : toAccount;
        Account second = fromAccount.id.compareTo(toAccount.id) < 0 ? toAccount : fromAccount;
        
        first.lock.lock();
        try {
            second.lock.lock();
            try {
                if (fromAccount.balance >= amount) {
                    fromAccount.balance -= amount;
                    toAccount.balance += amount;
                    System.out.println("Transferred " + amount + " from " + from + " to " + to);
                    return true;
                }
                return false;
            } finally {
                second.lock.unlock();
            }
        } finally {
            first.lock.unlock();
        }
    }
    
    static class Account {
        final String id;
        int balance;
        final ReentrantLock lock = new ReentrantLock();
        
        Account(String id, int balance) {
            this.id = id;
            this.balance = balance;
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        DeadlockFreeBank bank = new DeadlockFreeBank();
        bank.addAccount("A", 1000);
        bank.addAccount("B", 1000);
        
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        // Multiple concurrent transfers
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> bank.transfer("A", "B", 50));
            executor.submit(() -> bank.transfer("B", "A", 50));
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("Final balance A: " + bank.accounts.get("A").balance);
        System.out.println("Final balance B: " + bank.accounts.get("B").balance);
    }
}
```