package com.prod.solutions.slowquery;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

/**
 * Simulates a database-style deadlock where two transactions
 * acquire locks in different orders on the same resources.
 *
 * This mirrors real database deadlocks where:
 * Transaction 1: UPDATE accounts SET balance=balance-100 WHERE id=1;
 *                UPDATE accounts SET balance=balance+100 WHERE id=2;
 * Transaction 2: UPDATE accounts SET balance=balance-100 WHERE id=2;
 *                UPDATE accounts SET balance=balance+100 WHERE id=1;
 *
 * Result: Deadlock! InnoDB detects and kills one transaction.
 */
public class DeadlockSimulator {

    static class DbRow {
        private final int id;
        private int value;
        private final Lock lock = new ReentrantLock();

        DbRow(int id, int value) { this.id = id; this.value = value; }
        int getId() { return id; }
        int getValue() { return value; }
        void setValue(int value) { this.value = value; }
        Lock getLock() { return lock; }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Database Deadlock Simulator ===");

        DbRow row1 = new DbRow(1, 100);
        DbRow row2 = new DbRow(2, 100);

        Thread txn1 = new Thread(() -> {
            try {
                // Lock row1 then row2
                if (row1.getLock().tryLock(1, TimeUnit.SECONDS)) {
                    System.out.println("Txn1: locked row1");
                    Thread.sleep(100);
                    if (row2.getLock().tryLock(1, TimeUnit.SECONDS)) {
                        System.out.println("Txn1: locked row2");
                        // Transfer
                        row1.setValue(row1.getValue() - 50);
                        row2.setValue(row2.getValue() + 50);
                        System.out.println("Txn1: transfer complete");
                        row2.getLock().unlock();
                    } else {
                        System.out.println("Txn1: FAILED to lock row2");
                    }
                    row1.getLock().unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Transaction-1");

        Thread txn2 = new Thread(() -> {
            try {
                // BUG: Locks row2 then row1 (opposite order!)
                if (row2.getLock().tryLock(1, TimeUnit.SECONDS)) {
                    System.out.println("Txn2: locked row2");
                    Thread.sleep(100);
                    if (row1.getLock().tryLock(1, TimeUnit.SECONDS)) {
                        System.out.println("Txn2: locked row1");
                        row1.setValue(row1.getValue() + 30);
                        row2.setValue(row2.getValue() - 30);
                        System.out.println("Txn2: transfer complete");
                        row1.getLock().unlock();
                    } else {
                        System.out.println("Txn2: FAILED to lock row1 (deadlock!)");
                    }
                    row2.getLock().unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Transaction-2");

        txn1.start();
        txn2.start();

        txn1.join();
        txn2.join();

        System.out.printf("Row1 value: %d (expected: 110)%n", row1.getValue());
        System.out.printf("Row2 value: %d (expected: 90)%n", row2.getValue());

        System.out.println("\nFix: Always lock rows in the same order (e.g., by ID)");
    }
}
