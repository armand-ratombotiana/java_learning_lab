# Code Deep Dive — Concurrency

## Bank Account — Thread-Safe with Lock
```java
class BankAccount {
    private final Lock lock = new ReentrantLock();
    private int balance;

    public void transfer(BankAccount to, int amount) {
        lock.lock();
        try {
            this.balance -= amount;
            to.deposit(amount);
        } finally {
            lock.unlock();
        }
    }
}
```

## CompletableFuture Composition
```java
CompletableFuture<User> userFuture = CompletableFuture
    .supplyAsync(() -> fetchUser(id));

CompletableFuture<Double> salaryFuture = userFuture
    .thenCompose(user -> CompletableFuture
        .supplyAsync(() -> computeSalary(user)));

CompletableFuture<String> emailFuture = userFuture
    .thenApply(User::getEmail);

CompletableFuture.allOf(salaryFuture, emailFuture)
    .thenRun(() -> System.out.println("All done"))
    .join();
```

## Producer-Consumer with BlockingQueue
```java
class Producer implements Runnable {
    private final BlockingQueue<String> queue;
    public void run() {
        while (true) {
            queue.put(produce());
        }
    }
}

class Consumer implements Runnable {
    private final BlockingQueue<String> queue;
    public void run() {
        while (true) {
            String item = queue.take();
            consume(item);
        }
    }
}
```
