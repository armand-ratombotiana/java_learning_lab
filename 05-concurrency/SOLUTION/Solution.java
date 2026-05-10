package com.learning.lab.module05.solution;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.stream.IntStream;

public class Solution {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Module 05: Concurrency - Complete Solution ===\n");

        demonstrateThreadCreation();
        demonstrateThreadSynchronization();
        demonstrateThreadCommunication();
        demonstrateExecutorFramework();
        demonstrateThreadPools();
        demonstrateFutureAndCallable();
        demonstrateConcurrentCollections();
        demonstrateAtomicVariables();
        demonstrateLocks();
        demonstrateCompletableFuture();
        demonstrateScheduledExecution();
    }

    private static void demonstrateThreadCreation() {
        System.out.println("=== Thread Creation ===\n");

        extendThread();
        implementRunnable();
        implementCallable();
        lambdaThread();
    }

    private static void extendThread() {
        System.out.println("--- Extending Thread ---");

        MyThread thread = new MyThread("Worker-1");
        thread.start();
        try { thread.join(); } catch (InterruptedException e) {}

        MyThread thread2 = new MyThread("Worker-2");
        thread2.start();
        try { thread2.join(); } catch (InterruptedException e) {}

        System.out.println();
    }

    private static void implementRunnable() {
        System.out.println("--- Implementing Runnable ---");

        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            System.out.println("Running task in: " + name);
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            System.out.println("Task completed in: " + name);
        };

        Thread t1 = new Thread(task, "Runnable-Thread-1");
        Thread t2 = new Thread(task, "Runnable-Thread-2");

        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}

        System.out.println();
    }

    private static void implementCallable() {
        System.out.println("--- Implementing Callable ---");

        Callable<Integer> callable = () -> {
            int sum = 0;
            for (int i = 1; i <= 5; i++) {
                sum += i;
                Thread.sleep(50);
            }
            return sum;
        };

        FutureTask<Integer> futureTask = new FutureTask<>(callable);
        Thread thread = new Thread(futureTask, "Callable-Thread");
        thread.start();

        try {
            Integer result = futureTask.get();
            System.out.println("Callable result: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println();
    }

    private static void lambdaThread() {
        System.out.println("--- Lambda Thread ---");

        Thread t = new Thread(() -> {
            System.out.println("Lambda thread running");
            System.out.println("Thread name: " + Thread.currentThread().getName());
        }, "Lambda-Thread");

        t.start();
        try { t.join(); } catch (InterruptedException e) {}

        System.out.println();
    }

    private static void demonstrateThreadSynchronization() {
        System.out.println("=== Thread Synchronization ===\n");

        demonstrateSynchronizedMethod();
        demonstrateSynchronizedBlock();
        demonstrateStaticSynchronization();
        demonstrateWaitNotify();
    }

    private static void demonstrateSynchronizedMethod() {
        System.out.println("--- Synchronized Method ---");

        BankAccount account = new BankAccount(1000);

        Thread t1 = new Thread(() -> {
            account.deposit(500);
            account.withdraw(200);
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            account.deposit(300);
            account.withdraw(100);
        }, "Thread-2");

        t1.start(); t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}

        System.out.println("Final balance: " + account.getBalance() + "\n");
    }

    private static void demonstrateSynchronizedBlock() {
        System.out.println("--- Synchronized Block ---");

        SharedData data = new SharedData();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                data.update("Thread-1-" + i);
            }
        }, "Update-1");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                data.update("Thread-2-" + i);
            }
        }, "Update-2");

        t1.start(); t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}

        System.out.println();
    }

    private static void demonstrateStaticSynchronization() {
        System.out.println("--- Static Synchronization ---");

        StaticCounter.increment();
        StaticCounter.increment();
        System.out.println("Static counter: " + StaticCounter.getCount() + "\n");
    }

    private static void demonstrateWaitNotify() {
        System.out.println("--- Wait & Notify ---");

        MessageQueue queue = new MessageQueue();

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                queue.produce("Message-" + i);
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            for (int i = 1; i <= 3; i++) {
                queue.consume();
            }
        }, "Consumer");

        producer.start();
        consumer.start();
        try { producer.join(); consumer.join(); } catch (InterruptedException e) {}

        System.out.println();
    }

    private static void demonstrateThreadCommunication() {
        System.out.println("=== Thread Communication ===\n");

        demonstrateProducerConsumer();
        demonstrateThreadJoin();
    }

    private static void demonstrateProducerConsumer() {
        System.out.println("--- Producer-Consumer Pattern ---");

        BlockingQueue<String> queue = new LinkedBlockingQueue<>(2);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    String msg = "Item-" + i;
                    queue.put(msg);
                    System.out.println("Produced: " + msg);
                }
            } catch (InterruptedException e) {}
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    String msg = queue.take();
                    System.out.println("Consumed: " + msg);
                }
            } catch (InterruptedException e) {}
        }, "Consumer");

        producer.start();
        consumer.start();
        try { producer.join(); consumer.join(); } catch (InterruptedException e) {}

        System.out.println();
    }

    private static void demonstrateThreadJoin() {
        System.out.println("--- Thread Join ---");

        Thread t1 = new Thread(() -> {
            System.out.println("Thread-1 started");
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            System.out.println("Thread-1 finished");
        });

        Thread t2 = new Thread(() -> {
            try { t1.join(); } catch (InterruptedException e) {}
            System.out.println("Thread-2 started (after t1)");
            System.out.println("Thread-2 finished");
        });

        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}

        System.out.println();
    }

    private static void demonstrateExecutorFramework() {
        System.out.println("=== Executor Framework ===\n");

        demonstrateFixedThreadPool();
        demonstrateCachedThreadPool();
        demonstrateSingleThreadExecutor();
        demonstrateScheduledThreadPool();
    }

    private static void demonstrateFixedThreadPool() {
        System.out.println("--- Fixed Thread Pool ---");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task-" + taskId + " running in " + Thread.currentThread().getName());
                try { Thread.sleep(50); } catch (InterruptedException e) {}
            });
        }

        executor.shutdown();
        try { executor.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException e) {}
        System.out.println("Fixed pool tasks completed\n");
    }

    private static void demonstrateCachedThreadPool() {
        System.out.println("--- Cached Thread Pool ---");

        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 1; i <= 5; i++) {
            executor.submit(() -> {
                System.out.println("Task " + i + " by " + Thread.currentThread().getName());
            });
        }

        executor.shutdown();
        try { executor.awaitTermination(2, TimeUnit.SECONDS); } catch (InterruptedException e) {}
        System.out.println();
    }

    private static void demonstrateSingleThreadExecutor() {
        System.out.println("--- Single Thread Executor ---");

        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (int i = 1; i <= 3; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task-" + taskId + " executed");
            });
        }

        executor.shutdown();
        try { executor.awaitTermination(2, TimeUnit.SECONDS); } catch (InterruptedException e) {}
        System.out.println();
    }

    private static void demonstrateScheduledThreadPool() {
        System.out.println("--- Scheduled Thread Pool ---");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        Runnable task = () -> System.out.println("Scheduled task executed at " + System.currentTimeMillis());

        scheduler.schedule(task, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(task, 0, 500, TimeUnit.MILLISECONDS);

        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        scheduler.shutdown();
        System.out.println();
    }

    private static void demonstrateThreadPools() {
        System.out.println("=== Thread Pools ===\n");

        demonstrateWorkStealingPool();
    }

    private static void demonstrateWorkStealingPool() {
        System.out.println("--- Work Stealing Pool ---");

        ExecutorService executor = Executors.newWorkStealingPool(4);

        for (int i = 1; i <= 8; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task-" + taskId + " by " + Thread.currentThread().getName());
            });
        }

        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        executor.shutdown();
        try { executor.awaitTermination(2, TimeUnit.SECONDS); } catch (InterruptedException e) {}
        System.out.println();
    }

    private static void demonstrateFutureAndCallable() {
        System.out.println("=== Future & Callable ===\n");

        futureBasic();
        futureWithTimeout();
        futureCancel();
    }

    private static void futureBasic() {
        System.out.println("--- Future Basic ---");

        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<Integer> future = executor.submit(() -> {
            Thread.sleep(100);
            return 42;
        });

        try {
            System.out.println("Result: " + future.get());
            System.out.println("Is done: " + future.isDone());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        System.out.println();
    }

    private static void futureWithTimeout() {
        System.out.println("--- Future with Timeout ---");

        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<String> future = executor.submit(() -> {
            Thread.sleep(200);
            return "completed";
        });

        try {
            String result = future.get(1, TimeUnit.SECONDS);
            System.out.println("Result: " + result);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.out.println("Timeout: " + e.getMessage());
        }

        executor.shutdown();
        System.out.println();
    }

    private static void futureCancel() {
        System.out.println("--- Future Cancel ---");

        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<Long> future = executor.submit(() -> {
            long sum = 0;
            for (int i = 1; i <= 1000000; i++) {
                sum += i;
                if (Thread.interrupted()) {
                    System.out.println("Task interrupted");
                    return sum;
                }
            }
            return sum;
        });

        try { Thread.sleep(10); } catch (InterruptedException e) {}
        future.cancel(true);

        System.out.println("Cancelled: " + future.isCancelled());

        executor.shutdown();
        System.out.println();
    }

    private static void demonstrateConcurrentCollections() {
        System.out.println("=== Concurrent Collections ===\n");

        concurrentHashMap();
        concurrentLinkedQueue();
        blockingQueueTypes();
    }

    private static void concurrentHashMap() {
        System.out.println("--- ConcurrentHashMap ---");

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        map.put("A", 1);
        map.put("B", 2);
        map.putIfAbsent("C", 3);

        System.out.println("Initial: " + map);

        map.computeIfAbsent("D", k -> 4);
        System.out.println("After computeIfAbsent: " + map);

        map.forEach(2, (k, v) -> System.out.println(k + "=" + v));

        System.out.println("Get B: " + map.get("B"));
        System.out.println("Contains C: " + map.containsKey("C"));
        System.out.println();
    }

    private static void concurrentLinkedQueue() {
        System.out.println("--- ConcurrentLinkedQueue ---");

        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

        queue.offer("First");
        queue.offer("Second");
        queue.offer("Third");

        System.out.println("Initial: " + queue);
        System.out.println("Poll: " + queue.poll());
        System.out.println("Peek: " + queue.peek());
        System.out.println("Remaining: " + queue);

        queue.forEach(System.out::println);
        System.out.println();
    }

    private static void blockingQueueTypes() {
        System.out.println("--- Blocking Queue Types ---");

        ArrayBlockingQueue<String> arrayQueue = new ArrayBlockingQueue<>(3);
        LinkedBlockingQueue<String> linkedQueue = new LinkedBlockingQueue<>();
        PriorityBlockingQueue<Integer> priorityQueue = new PriorityBlockingQueue<>();

        try {
            arrayQueue.put("A");
            arrayQueue.put("B");
            arrayQueue.put("C");
            System.out.println("ArrayBlockingQueue full: " + arrayQueue.offer("D"));
            System.out.println("ArrayBlockingQueue: " + arrayQueue);
        } catch (InterruptedException e) {}

        linkedQueue.offer("First");
        linkedQueue.offer("Second");
        System.out.println("LinkedBlockingQueue: " + linkedQueue);

        priorityQueue.offer(3);
        priorityQueue.offer(1);
        priorityQueue.offer(2);
        System.out.println("PriorityBlockingQueue: " + priorityQueue);
        System.out.println("Poll: " + priorityQueue.poll());
        System.out.println();
    }

    private static void demonstrateAtomicVariables() {
        System.out.println("=== Atomic Variables ===\n");

        atomicInteger();
        atomicLong();
        atomicBoolean();
        atomicReference();
        atomicStampedReference();
    }

    private static void atomicInteger() {
        System.out.println("--- AtomicInteger ---");

        AtomicInteger ai = new AtomicInteger(0);

        System.out.println("Initial: " + ai.get());
        System.out.println("incrementAndGet: " + ai.incrementAndGet());
        System.out.println("getAndIncrement: " + ai.getAndIncrement());
        System.out.println("addAndGet: " + ai.addAndGet(10));
        System.out.println("getAndAdd: " + ai.getAndAdd(5));
        System.out.println("final value: " + ai.get());

        System.out.println();
    }

    private static void atomicLong() {
        System.out.println("--- AtomicLong ---");

        AtomicLong al = new AtomicLong(100);
        System.out.println("Initial: " + al.get());
        System.out.println("After decrement: " + al.decrementAndGet());
        System.out.println("Update: " + al.updateAndGet(x -> x * 2));
        System.out.println("Accumulate: " + al.accumulateAndGet(10, (a, b) -> a + b));
        System.out.println();
    }

    private static void atomicBoolean() {
        System.out.println("--- AtomicBoolean ---");

        AtomicBoolean ab = new AtomicBoolean(false);

        System.out.println("Initial: " + ab.get());
        System.out.println("compareAndSet(false, true): " + ab.compareAndSet(false, true));
        System.out.println("After: " + ab.get());

        ab.set(false);
        boolean wasSet = ab.getAndSet(true);
        System.out.println("getAndSet: " + wasSet + ", current: " + ab.get());
        System.out.println();
    }

    private static void atomicReference() {
        System.out.println("--- AtomicReference ---");

        AtomicReference<String> ar = new AtomicReference<>("Initial");

        System.out.println("Initial: " + ar.get());
        System.out.println("compareAndSet: " + ar.compareAndSet("Initial", "Updated"));
        System.out.println("After CAS: " + ar.get());

        ar.set("New Value");
        System.out.println("After set: " + ar.get());

        System.out.println("getAndUpdate: " + ar.getAndUpdate(s -> s.toUpperCase()));
        System.out.println("After update: " + ar.get());
        System.out.println();
    }

    private static void atomicStampedReference() {
        System.out.println("--- AtomicStampedReference ---");

        AtomicStampedReference<String> asr = new AtomicStampedReference<>("Initial", 1);

        int[] stamp = new int[1];
        String value = asr.get(stamp);
        System.out.println("Value: " + value + ", Stamp: " + stamp[0]);

        boolean updated = asr.compareAndSet("Initial", "Updated", 1, 2);
        System.out.println("Updated: " + updated + ", New stamp: " + asr.getStamp());

        asr.get(stamp);
        System.out.println("Current: " + asr.getReference() + ", Stamp: " + stamp[0]);
        System.out.println();
    }

    private static void demonstrateLocks() {
        System.out.println("=== Locks ===\n");

        demonstrateReentrantLock();
        demonstrateReadWriteLock();
        demonstrateStampedLock();
    }

    private static void demonstrateReentrantLock() {
        System.out.println("--- ReentrantLock ---");

        ReentrantLock lock = new ReentrantLock();
        CounterWithLock counter = new CounterWithLock(lock);

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        }, "Lock-T1");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        }, "Lock-T2");

        t1.start(); t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}

        System.out.println("Count: " + counter.getCount());
        System.out.println("Is locked: " + lock.isLocked());
        System.out.println("Held by thread: " + lock.isHeldByCurrentThread() + "\n");
    }

    private static void demonstrateReadWriteLock() {
        System.out.println("--- ReadWriteLock ---");

        ReadWriteCache cache = new ReadWriteCache();

        Thread writer = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                cache.write("key-" + i, "value-" + i);
            }
        }, "Writer");

        Thread reader1 = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                cache.read("key-" + i);
            }
        }, "Reader1");

        Thread reader2 = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                cache.read("key-" + i);
            }
        }, "Reader2");

        writer.start();
        reader1.start();
        reader2.start();

        try { writer.join(); reader1.join(); reader2.join(); } catch (InterruptedException e) {}
        System.out.println();
    }

    private static void demonstrateStampedLock() {
        System.out.println("--- StampedLock ---");

        StampedLockPoint point = new StampedLockPoint(10, 20);

        System.out.println("Initial: x=" + point.getX() + ", y=" + point.getY());

        point.move(5, -5);
        System.out.println("After move: x=" + point.getX() + ", y=" + point.getY());

        double distance = point.distanceFromOrigin();
        System.out.println("Distance from origin: " + distance + "\n");
    }

    private static void demonstrateCompletableFuture() {
        System.out.println("=== CompletableFuture ===\n");

        createCompletableFuture();
        thenApply();
        thenCompose();
        combineFutures();
        exceptionHandling();
    }

    private static void createCompletableFuture() {
        System.out.println("--- Create CompletableFuture ---");

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            return "Hello";
        });

        System.out.println("Result: " + future.join());
        System.out.println();
    }

    private static void thenApply() {
        System.out.println("--- thenApply ---");

        CompletableFuture<Integer> future = CompletableFuture
            .supplyAsync(() -> "123")
            .thenApply(Integer::parseInt)
            .thenApply(n -> n * 2);

        System.out.println("Result: " + future.join());
        System.out.println();
    }

    private static void thenCompose() {
        System.out.println("--- thenCompose ---");

        CompletableFuture<String> future = CompletableFuture
            .supplyAsync(() -> 3)
            .thenCompose(n -> CompletableFuture.supplyAsync(() -> "Number: " + n));

        System.out.println("Result: " + future.join());
        System.out.println();
    }

    private static void combineFutures() {
        System.out.println("--- Combine Futures ---");

        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> 10);
        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> 20);

        CompletableFuture<Integer> combined = f1.thenCombine(f2, Integer::sum);
        System.out.println("thenCombine sum: " + combined.join());

        CompletableFuture<Integer> allOf = CompletableFuture.allOf(
            CompletableFuture.supplyAsync(() -> 1),
            CompletableFuture.supplyAsync(() -> 2),
            CompletableFuture.supplyAsync(() -> 3)
        ).thenApply(v -> 3);

        System.out.println("allOf: all completed");
        System.out.println();
    }

    private static void exceptionHandling() {
        System.out.println("--- Exception Handling ---");

        CompletableFuture<String> success = CompletableFuture
            .supplyAsync(() -> "success")
            .exceptionally(ex -> "fallback");
        System.out.println("Success case: " + success.join());

        CompletableFuture<String> failure = CompletableFuture
            .supplyAsync(() -> { throw new RuntimeException("error"); })
            .exceptionally(ex -> "fallback");
        System.out.println("Failure case: " + failure.join());

        CompletableFuture<String> handle = CompletableFuture
            .supplyAsync(() -> { throw new RuntimeException("error"); })
            .handle((result, ex) -> ex == null ? result : "handled: " + ex.getMessage());
        System.out.println("Handle case: " + handle.join());
        System.out.println();
    }

    private static void demonstrateScheduledExecution() {
        System.out.println("=== Scheduled Execution ===\n");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        Runnable task = () -> System.out.println("Task executed at: " + System.currentTimeMillis());

        System.out.println("Scheduling task with 1s delay...");
        Future<?> future = scheduler.schedule(task, 1, TimeUnit.SECONDS);

        System.out.println("Scheduling fixed rate task (every 200ms, 3 times)...");
        ScheduledFuture<?> rateFuture = scheduler.scheduleAtFixedRate(task, 0, 200, TimeUnit.MILLISECONDS);

        try { Thread.sleep(700); } catch (InterruptedException e) {}
        rateFuture.cancel(true);

        try { Thread.sleep(1500); } catch (InterruptedException e) {}

        scheduler.shutdown();
        System.out.println("Scheduled execution completed\n");
    }
}

class MyThread extends Thread {
    private String name;

    public MyThread(String name) {
        this.name = name;
    }

    public void run() {
        System.out.println(name + " is running");
    }
}

class BankAccount {
    private int balance;

    public BankAccount(int initialBalance) {
        this.balance = initialBalance;
    }

    public synchronized void deposit(int amount) {
        balance += amount;
        System.out.println("Deposited: " + amount + ", Balance: " + balance);
    }

    public synchronized void withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
            System.out.println("Withdrew: " + amount + ", Balance: " + balance);
        }
    }

    public synchronized int getBalance() {
        return balance;
    }
}

class SharedData {
    private String data;
    private final Object lock = new Object();

    public void update(String value) {
        synchronized(lock) {
            data = value;
            System.out.println("Updated data: " + data + " by " + Thread.currentThread().getName());
        }
    }

    public String getData() {
        synchronized(lock) {
            return data;
        }
    }
}

class StaticCounter {
    private static int count = 0;

    public static synchronized void increment() {
        count++;
    }

    public static synchronized int getCount() {
        return count;
    }
}

class MessageQueue {
    private Queue<String> queue = new LinkedList<>();

    public synchronized void produce(String message) {
        queue.offer(message);
        System.out.println("Produced: " + message);
        notify();
    }

    public synchronized String consume() {
        while (queue.isEmpty()) {
            try { wait(); } catch (InterruptedException e) {}
        }
        return queue.poll();
    }
}

class CounterWithLock {
    private int count = 0;
    private final ReentrantLock lock;

    public CounterWithLock(ReentrantLock lock) {
        this.lock = lock;
    }

    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}

class ReadWriteCache {
    private final Map<String, String> cache = new HashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void write(String key, String value) {
        rwLock.writeLock().lock();
        try {
            cache.put(key, value);
            System.out.println("Wrote: " + key + "=" + value + " by " + Thread.currentThread().getName());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public String read(String key) {
        rwLock.readLock().lock();
        try {
            String value = cache.get(key);
            System.out.println("Read: " + key + "=" + value + " by " + Thread.currentThread().getName());
            return value;
        } finally {
            rwLock.readLock().unlock();
        }
    }
}

class StampedLockPoint {
    private long x, y;
    private final StampedLock lock = new StampedLock();

    public StampedLockPoint(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public void move(long dx, long dy) {
        long stamp = lock.writeLock();
        try {
            x += dx;
            y += dy;
            System.out.println("Moved by (" + dx + ", " + dy + ")");
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public long getX() {
        long stamp = lock.readLock();
        try {
            return x;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public long getY() {
        long stamp = lock.readLock();
        try {
            return y;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public double distanceFromOrigin() {
        long stamp = lock.tryOptimisticRead();
        long currentX = x;
        long currentY = y;

        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }

        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}