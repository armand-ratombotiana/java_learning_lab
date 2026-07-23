package com.prod.solutions.deadlock;

/**
 * Demonstrates a classic Java deadlock caused by inconsistent lock ordering.
 * Two threads acquire locks in opposite order, creating a circular wait.
 *
 * BUG: withdraw() locks accountA then accountB, while transfer() locks
 * accountB then accountA. This violates the Coffman condition of
 * consistent lock ordering.
 */
public class DeadlockExample {

    static class Account {
        private final String name;
        private int balance;

        Account(String name, int balance) {
            this.name = name;
            this.balance = balance;
        }

        void debit(int amount) { balance -= amount; }
        void credit(int amount) { balance += amount; }
        int getBalance() { return balance; }
        String getName() { return name; }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Deadlock Demo (Inconsistent Lock Ordering) ===");

        Account accountA = new Account("AccountA", 1000);
        Account accountB = new Account("AccountB", 1000);

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                withdraw(accountA, accountB, 1);
            }
        }, "WithdrawThread");

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                transfer(accountB, accountA, 1);
            }
        }, "TransferThread");

        thread1.start();
        thread2.start();

        thread1.join(5000);
        thread2.join(5000);

        System.out.printf("AccountA balance: %d%n", accountA.getBalance());
        System.out.printf("AccountB balance: %d%n", accountB.getBalance());
        System.out.printf("Total: %d (expected: 2000)%n",
                accountA.getBalance() + accountB.getBalance());

        if (thread1.isAlive() || thread2.isAlive()) {
            System.out.println("DEADLOCK DETECTED! Threads are stuck.");
            thread1.interrupt();
            thread2.interrupt();
        } else {
            System.out.println("No deadlock (lucky timing or fix applied).");
        }
    }

    // BUG: Locks accountA then accountB
    static void withdraw(Account from, Account to, int amount) {
        synchronized (from) {
            synchronized (to) {
                if (from.getBalance() >= amount) {
                    from.debit(amount);
                    to.credit(amount);
                }
            }
        }
    }

    // BUG: Locks accountB then accountA (OPPOSITE ORDER!)
    // This creates a circular wait with withdraw().
    static void transfer(Account from, Account to, int amount) {
        synchronized (from) {
            synchronized (to) {
                if (from.getBalance() >= amount) {
                    from.debit(amount);
                    to.credit(amount);
                }
            }
        }
    }
}
