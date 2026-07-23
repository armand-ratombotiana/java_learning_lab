package concurrency;

/**
 * LeetCode 1115 - Print FooBar Alternately
 * 
 * Two threads call foo() and bar() alternately, printing "foobar" n times.
 * 
 * Approaches: Semaphore, ReentrantLock + Condition, CyclicBarrier
 * Time: O(n), Space: O(1)
 */
public class PrintFooBarAlternately {

    // Approach: Semaphore
    static class FooBar {
        private final int n;
        private final java.util.concurrent.Semaphore fooSema = new java.util.concurrent.Semaphore(1);
        private final java.util.concurrent.Semaphore barSema = new java.util.concurrent.Semaphore(0);

        FooBar(int n) { this.n = n; }

        public void foo(Runnable printFoo) {
            for (int i = 0; i < n; i++) {
                try { fooSema.acquire(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                printFoo.run();
                barSema.release();
            }
        }

        public void bar(Runnable printBar) {
            for (int i = 0; i < n; i++) {
                try { barSema.acquire(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                printBar.run();
                fooSema.release();
            }
        }
    }

    // Approach: ReentrantLock + Condition (more flexible)
    static class FooBarLock {
        private final int n;
        private final java.util.concurrent.locks.ReentrantLock lock = new java.util.concurrent.locks.ReentrantLock();
        private final java.util.concurrent.locks.Condition fooTurn = lock.newCondition();
        private boolean isFooTurn = true;

        FooBarLock(int n) { this.n = n; }

        public void foo(Runnable printFoo) {
            for (int i = 0; i < n; i++) {
                lock.lock();
                try {
                    while (!isFooTurn) { fooTurn.await(); }
                    printFoo.run();
                    isFooTurn = false;
                    fooTurn.signal();
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                finally { lock.unlock(); }
            }
        }

        public void bar(Runnable printBar) {
            for (int i = 0; i < n; i++) {
                lock.lock();
                try {
                    while (isFooTurn) { fooTurn.await(); }
                    printBar.run();
                    isFooTurn = true;
                    fooTurn.signal();
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                finally { lock.unlock(); }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        FooBar fb = new FooBar(3);
        StringBuilder sb = new StringBuilder();
        Thread t1 = new Thread(() -> fb.foo(() -> sb.append("foo")));
        Thread t2 = new Thread(() -> fb.bar(() -> sb.append("bar")));
        t1.start(); t2.start();
        t1.join(); t2.join();
        System.out.println("Output: " + sb.toString());
        assert "foobarfoobarfoobar".equals(sb.toString());
        System.out.println("All tests passed.");
    }
}