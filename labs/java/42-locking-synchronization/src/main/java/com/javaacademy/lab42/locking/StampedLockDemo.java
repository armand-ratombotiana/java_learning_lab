package com.javaacademy.lab42.locking;

import java.util.concurrent.locks.StampedLock;

/**
 * Demonstrates StampedLock with optimistic reads, and conversion to read/write locks.
 * StampedLock provides three modes: writing, reading, and optimistic reading.
 */
public class StampedLockDemo {

    private int x = 0, y = 0;
    private final StampedLock lock = new StampedLock();

    public void write(int dx, int dy) {
        long stamp = lock.writeLock();
        try {
            x += dx;
            y += dy;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public int readOptimistic() {
        long stamp = lock.tryOptimisticRead();
        int cx = x, cy = y;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                cx = x;
                cy = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return cx + cy;
    }

    public int readLocked() {
        long stamp = lock.readLock();
        try {
            return x + y;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== StampedLock Demo ===\n");
        StampedLockDemo demo = new StampedLockDemo();

        demo.write(10, 20);
        System.out.println("Optimistic read: " + demo.readOptimistic());
        System.out.println("Pessimistic read: " + demo.readLocked());

        // Convert optimistic to write lock
        long stamp = demo.lock.tryOptimisticRead();
        if (demo.lock.validate(stamp)) {
            stamp = demo.lock.tryConvertToWriteLock(stamp);
            if (stamp != 0) {
                System.out.println("Converted optimistic to write lock");
                demo.lock.unlockWrite(stamp);
            }
        }
    }
}
