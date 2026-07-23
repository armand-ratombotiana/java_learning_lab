package concurrency;

/**
 * LeetCode 1114 - Print in Order
 * 
 * Suppose we have a class:
 * public class Foo { public void first() { print("first"); }
 *                     public void second() { print("second"); }
 *                     public void third() { print("third"); } }
 * Three threads will call these methods. Ensure first() runs before second() which runs before third().
 * 
 * Approaches: CountDownLatch, volatile + spin, Semaphore
 * Time: O(1) per call, Space: O(1)
 */
public class PrintInOrder {

    // Approach 1: CountDownLatch (most intuitive)
    static class FooLatch {
        private final java.util.concurrent.CountDownLatch latch1 = new java.util.concurrent.CountDownLatch(1);
        private final java.util.concurrent.CountDownLatch latch2 = new java.util.concurrent.CountDownLatch(1);

        public void first(Runnable printFirst) {
            printFirst.run();
            latch1.countDown();
        }

        public void second(Runnable printSecond) {
            try { latch1.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            printSecond.run();
            latch2.countDown();
        }

        public void third(Runnable printThird) {
            try { latch2.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            printThird.run();
        }
    }

    // Approach 2: Semaphore
    static class FooSemaphore {
        private final java.util.concurrent.Semaphore s12 = new java.util.concurrent.Semaphore(0);
        private final java.util.concurrent.Semaphore s23 = new java.util.concurrent.Semaphore(0);

        public void first(Runnable printFirst) {
            printFirst.run();
            s12.release();
        }

        public void second(Runnable printSecond) {
            try { s12.acquire(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            printSecond.run();
            s23.release();
        }

        public void third(Runnable printThird) {
            try { s23.acquire(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            printThird.run();
        }
    }

    // Approach 3: volatile visibility (busy-wait, not ideal)
    static class FooVolatile {
        private volatile int stage = 1;

        public void first(Runnable printFirst) {
            printFirst.run();
            stage = 2;
        }

        public void second(Runnable printSecond) {
            while (stage != 2) { Thread.onSpinWait(); }
            printSecond.run();
            stage = 3;
        }

        public void third(Runnable printThird) {
            while (stage != 3) { Thread.onSpinWait(); }
            printThird.run();
        }
    }

    public static void main(String[] args) throws Exception {
        // Test with Semaphore approach
        FooSemaphore foo = new FooSemaphore();
        StringBuilder sb = new StringBuilder();
        Runnable p1 = () -> sb.append("first");
        Runnable p2 = () -> sb.append("second");
        Runnable p3 = () -> sb.append("third");

        Thread t3 = new Thread(() -> foo.third(p3));
        Thread t2 = new Thread(() -> foo.second(p2));
        Thread t1 = new Thread(() -> foo.first(p1));

        t3.start(); t2.start(); t1.start();
        t1.join(); t2.join(); t3.join();

        System.out.println("Output: " + sb.toString());
        assert "firstsecondthird".equals(sb.toString()) : "Order violated";
        System.out.println("All tests passed.");
    }
}