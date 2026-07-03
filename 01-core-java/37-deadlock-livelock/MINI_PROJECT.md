# Mini Project: Bank Transfer Deadlock Resolver

## Objective
Build a banking application that simulates concurrent money transfers between accounts. You will intentionally create a Deadlock, and then resolve it using the **Lock Ordering** strategy (with a tie-breaker for hash collisions) and the **Random Backoff** strategy using `tryLock()`.

## Prerequisites
*   Java 17+

## Step 1: The Account Class
Create a simple `Account` class with an ID and a balance.

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private final int id;
    private int balance;
    public final Lock lock = new ReentrantLock();

    public Account(int id, int balance) {
        this.id = id;
        this.balance = balance;
    }

    public int getId() { return id; }
    public void withdraw(int amount) { balance -= amount; }
    public void deposit(int amount) { balance += amount; }
    public int getBalance() { return balance; }
}
```

## Step 2: The Deadlock Implementation (The Wrong Way)
Write a transfer method that simply locks the `from` account, then locks the `to` account.

```java
public class DeadlockingTransfer {
    public static void transfer(Account from, Account to, int amount) {
        synchronized (from) { // Acquire first lock
            System.out.println(Thread.currentThread().getName() + " locked Account " + from.getId());
            
            try { Thread.sleep(50); } catch (InterruptedException e) {} // Force the race condition
            
            synchronized (to) { // Attempt to acquire second lock
                System.out.println(Thread.currentThread().getName() + " locked Account " + to.getId());
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }
}
```

## Step 3: Resolution 1 - Lock Ordering
Fix the deadlock by ensuring locks are always acquired in the same global order, regardless of which account is `from` and which is `to`. We use a tie-breaker lock in case of hash collisions.

```java
public class OrderedTransfer {
    private static final Object TIE_BREAKER = new Object();

    public static void transfer(Account from, Account to, int amount) {
        int fromHash = System.identityHashCode(from);
        int toHash = System.identityHashCode(to);

        if (fromHash < toHash) {
            synchronized (from) {
                synchronized (to) { executeTransfer(from, to, amount); }
            }
        } else if (fromHash > toHash) {
            synchronized (to) {
                synchronized (from) { executeTransfer(from, to, amount); }
            }
        } else {
            // RARE: Hash collision. Use the global tie-breaker to ensure mutual exclusion
            synchronized (TIE_BREAKER) {
                synchronized (from) {
                    synchronized (to) { executeTransfer(from, to, amount); }
                }
            }
        }
    }

    private static void executeTransfer(Account from, Account to, int amount) {
        from.withdraw(amount);
        to.deposit(amount);
        System.out.println(Thread.currentThread().getName() + " successfully transferred " + amount);
    }
}
```

## Step 4: Resolution 2 - Random Backoff (Livelock Prevention)
Fix the deadlock using `tryLock()`. To prevent Livelock, if a thread fails to get both locks, it releases the first one and sleeps for a random duration before retrying.

```java
import java.util.Random;

public class BackoffTransfer {
    private static final Random random = new Random();

    public static void transfer(Account from, Account to, int amount) {
        while (true) {
            if (from.lock.tryLock()) {
                try {
                    if (to.lock.tryLock()) {
                        try {
                            from.withdraw(amount);
                            to.deposit(amount);
                            System.out.println(Thread.currentThread().getName() + " successfully transferred " + amount);
                            return; // Success! Exit the loop.
                        } finally {
                            to.lock.unlock();
                        }
                    }
                } finally {
                    from.lock.unlock(); // Release the first lock if the second failed
                }
            }
            // Failed to get both locks. Sleep for a random time (1-10ms) to prevent Livelock.
            try {
                Thread.sleep(random.nextInt(10) + 1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
```

## Step 5: Test the Implementations
```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Account acc1 = new Account(1, 1000);
        Account acc2 = new Account(2, 1000);

        // --- UNCOMMENT TO SEE DEADLOCK ---
        // Thread t1 = new Thread(() -> DeadlockingTransfer.transfer(acc1, acc2, 100), "Thread-1");
        // Thread t2 = new Thread(() -> DeadlockingTransfer.transfer(acc2, acc1, 100), "Thread-2");
        
        // --- RUNNING SAFE ORDERED TRANSFER ---
        Thread t1 = new Thread(() -> OrderedTransfer.transfer(acc1, acc2, 100), "Thread-1");
        Thread t2 = new Thread(() -> OrderedTransfer.transfer(acc2, acc1, 100), "Thread-2");

        t1.start(); t2.start();
        t1.join(); t2.join();

        System.out.println("Final Acc1 Balance: " + acc1.getBalance());
        System.out.println("Final Acc2 Balance: " + acc2.getBalance());
    }
}
```

## Expected Output
If you run the `DeadlockingTransfer`, the application will freeze forever.
If you run the `OrderedTransfer` or `BackoffTransfer`, it will complete instantly:
```text
Thread-1 successfully transferred 100
Thread-2 successfully transferred 100
Final Acc1 Balance: 1000
Final Acc2 Balance: 1000
```