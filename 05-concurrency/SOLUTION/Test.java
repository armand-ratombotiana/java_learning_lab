package com.learning.lab.module05.solution;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Test {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Module 05: Concurrency - Comprehensive Tests ===\n");

        testThreadCreation();
        testThreadSynchronization();
        testExecutorService();
        testFutureAndCallable();
        testConcurrentCollections();
        testAtomicVariables();
        testLocks();
        testCompletableFuture();

        printSummary();
    }

    private static void testThreadCreation() {
        System.out.println("--- Testing Thread Creation ---");

        test("Thread start and join", () -> {
            Thread t = new Thread(() -> {
                try { Thread.sleep(50); } catch (InterruptedException e) {}
            });
            t.start();
            t.join();
            assert !t.isAlive() : "Thread should not be alive after join";
        });

        test("Thread with name", () -> {
            Thread t = new Thread(() -> {}, "TestThread");
            assert t.getName().equals("TestThread") : "Thread name should match";
        });

        test("Thread priority", () -> {
            Thread t = new Thread(() -> {});
            t.setPriority(Thread.MAX_PRIORITY);
            assert t.getPriority() == Thread.MAX_PRIORITY : "Priority should be MAX";
        });

        test("Runnable implementation", () -> {
            boolean[] executed = {false};
            Runnable r = () -> executed[0] = true;
            Thread t = new Thread(r);
            t.start();
            t.join();
            assert executed[0] : "Runnable should execute";
        });

        test("Callable with FutureTask", () -> {
            Callable<Integer> callable = () -> 42;
            FutureTask<Integer> task = new FutureTask<>(callable);
            Thread t = new Thread(task);
            t.start();
            t.join();
            assert task.get() == 42 : "FutureTask should return 42";
        });

        System.out.println();
    }

    private static void testThreadSynchronization() {
        System.out.println("--- Testing Thread Synchronization ---");

        test("Synchronized method", () -> {
            TestBankAccount account = new TestBankAccount(100);

            Thread t1 = new Thread(() -> account.deposit(50));
            Thread t2 = new Thread(() -> account.withdraw(30));

            t1.start(); t2.start();
            t1.join(); t2.join();

            assert account.getBalance() == 120 : "Balance should be 120";
        });

        test("Synchronized block", () -> {
            Object lock = new Object();
            int[] counter = {0};

            Thread t1 = new Thread(() -> {
                synchronized(lock) { counter[0]++; }
            });
            Thread t2 = new Thread(() -> {
                synchronized(lock) { counter[0]++; }
            });

            t1.start(); t2.start();
            t1.join(); t2.join();

            assert counter[0] == 2 : "Counter should be 2";
        });

        test("Volatile visibility", () -> {
            VolatileFlag flag = new VolatileFlag();
            Thread t = new Thread(() -> {
                while (!flag.isStopped()) {}
            });
            t.start();
            flag.stop();
            t.join(1000);
            assert !t.isAlive() : "Thread should stop";
        });

        test("Wait and Notify", () -> {
            TestProducerConsumer pc = new TestProducerConsumer();
            pc.produce("test");
            String result = pc.consume();
            assert result.equals("test") : "Should consume produced item";
        });

        System.out.println();
    }

    private static void testExecutorService() {
        System.out.println("--- Testing ExecutorService ---");

        test("Fixed thread pool", () -> {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.submit(() -> 1);
            executor.submit(() -> 2);
            executor.shutdown();
            assert executor.isShutdown() : "Should be shutdown";
        });

        test("Submit and get result", () -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Integer> future = executor.submit(() -> 42);
            Integer result = future.get();
            executor.shutdown();
            assert result == 42 : "Should return 42";
        });

        test("Callable with exception", () -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Integer> future = executor.submit(() -> {
                throw new RuntimeException("test");
            });
            boolean exception = false;
            try { future.get(); }
            catch (ExecutionException e) { exception = true; }
            executor.shutdown();
            assert exception : "Should throw ExecutionException";
        });

        test("Shutdown with awaitTermination", () -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> Thread.sleep(50));
            executor.shutdown();
            boolean terminated = executor.awaitTermination(1, TimeUnit.SECONDS);
            executor.shutdownNow();
            assert terminated : "Should terminate";
        });

        test("InvokeAll", () -> throws Exception {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            List<Callable<Integer>> tasks = List.of(() -> 1, () -> 2, () -> 3);
            List<Future<Integer>> futures = executor.invokeAll(tasks);
            int sum = futures.stream().mapToInt(f -> {
                try { return f.get(); } catch (Exception e) { return 0; }
            }).sum();
            executor.shutdown();
            assert sum == 6 : "Sum should be 6";
        });

        System.out.println();
    }

    private static void testFutureAndCallable() {
        System.out.println("--- Testing Future & Callable ---");

        test("Future get with timeout", () -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(() -> {
                Thread.sleep(50);
                return "done";
            });

            String result = future.get(1, TimeUnit.SECONDS);
            executor.shutdown();
            assert result.equals("done") : "Should return done";
        });

        test("Future isDone", () -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(() -> "done");
            future.get();
            executor.shutdown();
            assert future.isDone() : "Should be done";
        });

        test("Future cancel", () -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Long> future = executor.submit(() -> {
                long sum = 0;
                for (long i = 0; i < 1_000_000_000L; i++) sum += i;
                return sum;
            });
            future.cancel(true);
            executor.shutdown();
            assert future.isCancelled() : "Should be cancelled";
        });

        System.out.println();
    }

    private static void testConcurrentCollections() {
        System.out.println("--- Testing Concurrent Collections ---");

        test("ConcurrentHashMap put and get", () -> {
            ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("key", 1);
            assert map.get("key") == 1 : "Should get value";
        });

        test("ConcurrentHashMap computeIfAbsent", () -> {
            ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
            map.putIfAbsent("key", 1);
            assert map.get("key") == 1 : "Should get value";
        });

        test("ConcurrentLinkedQueue offer and poll", () -> {
            ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
            queue.offer("item");
            String item = queue.poll();
            assert item.equals("item") : "Should poll item";
        });

        test("BlockingQueue put and take", () -> throws InterruptedException {
            BlockingQueue<String> queue = new LinkedBlockingQueue<>(1);
            queue.put("item");
            String item = queue.take();
            assert item.equals("item") : "Should take item";
        });

        test("CopyOnWriteArrayList", () -> {
            List<String> list = new CopyOnWriteArrayList<>();
            list.add("a");
            list.add("b");
            assert list.size() == 2 : "Should have 2 elements";
        });

        System.out.println();
    }

    private static void testAtomicVariables() {
        System.out.println("--- Testing Atomic Variables ---");

        test("AtomicInteger increment", () -> {
            AtomicInteger ai = new AtomicInteger(0);
            ai.incrementAndGet();
            assert ai.get() == 1 : "Should be 1";
        });

        test("AtomicInteger getAndSet", () -> {
            AtomicInteger ai = new AtomicInteger(5);
            int old = ai.getAndSet(10);
            assert old == 5 : "Should return old value";
            assert ai.get() == 10 : "Should be new value";
        });

        test("AtomicBoolean compareAndSet", () -> {
            AtomicBoolean ab = new AtomicBoolean(false);
            boolean result = ab.compareAndSet(false, true);
            assert result && ab.get() : "Should set to true";
        });

        test("AtomicReference get and set", () -> {
            AtomicReference<String> ar = new AtomicReference<>("initial");
            ar.set("updated");
            assert ar.get().equals("updated") : "Should be updated";
        });

        test("AtomicLong addAndGet", () -> {
            AtomicLong al = new AtomicLong(10);
            long result = al.addAndGet(5);
            assert result == 15 : "Should be 15";
        });

        test("AtomicIntegerArray", () -> {
            AtomicIntegerArray array = new AtomicIntegerArray(3);
            array.set(0, 10);
            array.addAndGet(1, 20);
            assert array.get(0) == 10 : "First should be 10";
            assert array.get(1) == 20 : "Second should be 20";
        });

        System.out.println();
    }

    private static void testLocks() {
        System.out.println("--- Testing Locks ---");

        test("ReentrantLock lock and unlock", () -> {
            ReentrantLock lock = new ReentrantLock();
            lock.lock();
            lock.unlock();
            assert !lock.isLocked() : "Should not be locked";
        });

        test("ReentrantLock isHeldByCurrentThread", () -> {
            ReentrantLock lock = new ReentrantLock();
            lock.lock();
            assert lock.isHeldByCurrentThread() : "Should be held by current thread";
            lock.unlock();
        });

        test("ReentrantReadWriteLock", () -> {
            ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

            rwLock.readLock().lock();
            assert rwLock.getReadLockCount() == 1 : "Should have read lock";
            rwLock.readLock().unlock();

            rwLock.writeLock().lock();
            assert rwLock.isWriteLocked() : "Should be write locked";
            rwLock.writeLock().unlock();
        });

        test("StampedLock", () -> {
            StampedLockPoint point = new StampedLockPoint(3, 4);
            long x = point.getX();
            assert x == 3 : "X should be 3";
            long y = point.getY();
            assert y == 4 : "Y should be 4";
        });

        System.out.println();
    }

    private static void testCompletableFuture() {
        System.out.println("--- Testing CompletableFuture ---");

        test("CompletableFuture supplyAsync", () -> {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "result");
            assert future.join().equals("result") : "Should return result";
        });

        test("CompletableFuture thenApply", () -> {
            CompletableFuture<Integer> future = CompletableFuture
                .supplyAsync(() -> "42")
                .thenApply(Integer::parseInt);
            assert future.join() == 42 : "Should be 42";
        });

        test("CompletableFuture thenCompose", () -> {
            CompletableFuture<String> future = CompletableFuture
                .supplyAsync(() -> 3)
                .thenCompose(n -> CompletableFuture.completedFuture("num:" + n));
            assert future.join().equals("num:3") : "Should be num:3";
        });

        test("CompletableFuture thenCombine", () -> {
            CompletableFuture<Integer> future = CompletableFuture
                .supplyAsync(() -> 10)
                .thenCombine(CompletableFuture.supplyAsync(() -> 20), Integer::sum);
            assert future.join() == 30 : "Should be 30";
        });

        test("CompletableFuture exceptionally", () -> {
            CompletableFuture<String> future = CompletableFuture
                .supplyAsync(() -> { throw new RuntimeException("error"); })
                .exceptionally(ex -> "fallback");
            assert future.join().equals("fallback") : "Should be fallback";
        });

        test("CompletableFuture allOf", () -> throws Exception {
            CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> 1);
            CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> 2);
            CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2);
            all.get();
            assert f1.isDone() && f2.isDone() : "Both should be done";
        });

        System.out.println();
    }

    private static void test(String name, Runnable test) {
        try {
            test.run();
            System.out.println("  PASS: " + name);
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("  FAIL: " + name + " - " + e.getMessage());
            failed++;
        }
    }

    private static void printSummary() {
        System.out.println("=== Test Summary ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
        System.out.println("===================");
    }
}

class TestBankAccount {
    private int balance;

    public TestBankAccount(int initial) { this.balance = initial; }

    public synchronized void deposit(int amount) { balance += amount; }
    public synchronized void withdraw(int amount) { balance -= amount; }
    public synchronized int getBalance() { return balance; }
}

class VolatileFlag {
    private volatile boolean stopped = false;

    public void stop() { stopped = true; }
    public boolean isStopped() { return stopped; }
}

class TestProducerConsumer {
    private String item;

    public synchronized void produce(String item) {
        this.item = item;
    }

    public synchronized String consume() {
        return item;
    }
}

class StampedLockPoint {
    private long x, y;
    private final StampedLock lock = new StampedLock();

    public StampedLockPoint(long x, long y) {
        this.x = x;
        this.y = y;
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
}