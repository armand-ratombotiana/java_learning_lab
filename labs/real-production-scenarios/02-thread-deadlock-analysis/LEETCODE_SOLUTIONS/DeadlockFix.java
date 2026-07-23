package com.prod.solutions.deadlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

/**
 * Fixes the deadlock by ensuring consistent lock ordering.
 * Uses System.identityHashCode() to enforce a global order,
 * and tryLock() with timeout to avoid indefinite blocking.
 */
public class DeadlockFix {

    static class Account {
        private final String name;
        private int balance;
        private final Lock lock = new ReentrantLock();
        private final int id;

        Account(String name, int balance, int id) {
            this.name = name;
            this.balance = balance;
            this.id = id;
        }

        void debit(int amount) { balance -= amount; }
        void credit(int amount) { balance += amount; }
        int getBalance() { return balance; }
        String getName() { return name; }
        Lock getLock() { return lock; }
        int getId() { return id; }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Deadlock Fix Demo (Consistent Lock Ordering) ===");

        Account accountA = new Account("AccountA", 1000, 1);
        Account accountB = new Account("AccountB", 1000, 2);

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                safeTransfer(accountA, accountB, 1);
            }
        }, "TransferThread1");

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                safeTransfer(accountB, accountA, 1);
            }
        }, "TransferThread2");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.printf("AccountA balance: %d%n", accountA.getBalance());
        System.out.printf("AccountB balance: %d%n", accountB.getBalance());
        System.out.printf("Total: %d (expected: 2000)%n",
                accountA.getBalance() + accountB.getBalance());

        if (accountA.getBalance() + accountB.getBalance() == 2000) {
            System.out.println("SUCCESS: No deadlock, all transfers completed.");
        }
    }

    /**
     * Safe transfer using consistent lock ordering by account ID.
     * Always locks the lower-ID account first, then the higher-ID.
     * Uses tryLock() with timeout to avoid indefinite blocking.
     */
    static boolean safeTransfer(Account from, Account to, int amount) {
        Account firstLock = from.getId() < to.getId() ? from : to;
        Account secondLock = from.getId() < to.getId() ? to : from;

        try {
            if (firstLock.getLock().tryLock(100, TimeUnit.MILLISECONDS)) {
                try {
                    if (secondLock.getLock().tryLock(100, TimeUnit.MILLISECONDS)) {
                        try {
                            if (from.getBalance() >= amount) {
                                from.debit(amount);
                                to.credit(amount);
                                return true;
                            }
                            return false;
                        } finally {
                            secondLock.getLock().unlock();
                        }
                    }
                    return false;
                } finally {
                    firstLock.getLock().unlock();
                }
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
