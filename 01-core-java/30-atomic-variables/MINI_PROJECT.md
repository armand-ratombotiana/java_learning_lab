# Mini Project: Lock-Free Bank Account & High-Contention Counter

## Objective
Build a thread-safe `BankAccount` that uses a custom CAS loop to perform compound atomic operations (withdrawals). Then, compare the performance of `AtomicLong` vs. `LongAdder` under high contention.

## Prerequisites
*   Java 17+

## Part 1: The Lock-Free Bank Account
We need to implement a withdrawal method. It must check if funds are available, and if so, deduct them. This is a compound operation (check-then-act), so `addAndGet` is insufficient. We must write our own CAS spin-loop.

```java
import java.util.concurrent.atomic.AtomicInteger;

public class BankAccount {
    private final AtomicInteger balance;

    public BankAccount(int initialBalance) {
        this.balance = new AtomicInteger(initialBalance);
    }

    public int getBalance() {
        return balance.get();
    }

    // Simple atomic operation
    public void deposit(int amount) {
        balance.addAndGet(amount);
    }

    // Compound atomic operation using a CAS spin-loop
    public boolean withdraw(int amount) {
        int currentBalance;
        int newBalance;
        
        do {
            currentBalance = balance.get();
            
            // 1. Check condition
            if (currentBalance < amount) {
                return false; // Insufficient funds
            }
            
            // 2. Calculate new state
            newBalance = currentBalance - amount;
            
            // 3. Try to apply new state. 
            // If another thread changed the balance between step 1 and 3, 
            // compareAndSet returns false, and the loop repeats.
        } while (!balance.compareAndSet(currentBalance, newBalance));
        
        return true;
    }
}
```

## Part 2: High Contention Profiler (AtomicLong vs LongAdder)
Demonstrate why `LongAdder` was introduced in Java 8 for high-throughput metrics.

```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class ContentionProfiler {
    private static final int THREADS = 64; // High contention
    private static final int ITERATIONS = 1_000_000;

    public static void runProfiler() throws InterruptedException {
        System.out.println("--- Profiling High Contention Counters ---");
        
        // 1. Test AtomicLong
        AtomicLong atomicLong = new AtomicLong();
        long timeAtomic = profile(() -> {
            for (int i = 0; i < ITERATIONS; i++) atomicLong.incrementAndGet();
        });
        System.out.println("AtomicLong Time : " + timeAtomic + " ms (Result: " + atomicLong.get() + ")");

        // 2. Test LongAdder
        LongAdder longAdder = new LongAdder();
        long timeAdder = profile(() -> {
            for (int i = 0; i < ITERATIONS; i++) longAdder.increment();
        });
        System.out.println("LongAdder Time  : " + timeAdder + " ms (Result: " + longAdder.sum() + ")");
    }

    private static long profile(Runnable task) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);

        long start = System.currentTimeMillis();
        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                task.run();
                latch.countDown();
            });
        }
        latch.await();
        long end = System.currentTimeMillis();
        
        executor.shutdown();
        return end - start;
    }
}
```

## Part 3: Test the Implementations
```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Testing Lock-Free Bank Account ---");
        BankAccount account = new BankAccount(100);
        
        // Simulate two threads trying to withdraw 60 simultaneously
        Thread t1 = new Thread(() -> {
            boolean success = account.withdraw(60);
            System.out.println("T1 Withdrawal (60): " + (success ? "SUCCESS" : "FAILED"));
        });
        
        Thread t2 = new Thread(() -> {
            boolean success = account.withdraw(60);
            System.out.println("T2 Withdrawal (60): " + (success ? "SUCCESS" : "FAILED"));
        });

        t1.start(); t2.start();
        t1.join(); t2.join();
        
        System.out.println("Final Balance: " + account.getBalance() + " (Should be 40)\n");

        // Run the profiler
        ContentionProfiler.runProfiler();
    }
}
```

## Expected Output (Times will vary wildly by CPU architecture)
```text
--- Testing Lock-Free Bank Account ---
T1 Withdrawal (60): SUCCESS
T2 Withdrawal (60): FAILED
Final Balance: 40 (Should be 40)

--- Profiling High Contention Counters ---
AtomicLong Time : 850 ms (Result: 64000000)
LongAdder Time  : 45 ms (Result: 64000000)   <-- Massively faster under contention!
```