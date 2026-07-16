# AQS Code Deep Dive: Building a Custom Latch

This lab demonstrates how to implement a custom "One-Shot" Latch using the AQS framework. This latch starts closed and, once opened, stays open forever.

## 💻 Pure Java Implementation

```java file="labs/java/concurrency/aqs-framework/SOLUTION/OneShotLatch.java"
package java.concurrency.aqs;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * A custom one-shot synchronizer built using AQS.
 * Similar to a CountDownLatch(1) but specifically for binary open/close.
 */
public class OneShotLatch {

    private final Sync sync = new Sync();

    /**
     * Internal helper class that extends AQS.
     * state 0 = closed, state 1 = open.
     */
    private static class Sync extends AbstractQueuedSynchronizer {
        
        @Override
        protected int tryAcquireShared(int ignored) {
            // Success (1) if open, fail (-1) if closed
            return (getState() == 1) ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int ignored) {
            // Open the latch by setting state to 1
            setState(1);
            return true; // Always succeeds and signals waiting threads
        }
    }

    public void await() throws InterruptedException {
        // AQS method: calls tryAcquireShared and handles queueing/blocking
        sync.acquireSharedInterruptibly(1);
    }

    public void signal() {
        // AQS method: calls tryReleaseShared and unparks waiting threads
        sync.releaseShared(1);
    }

    public static void main(String[] args) throws InterruptedException {
        OneShotLatch latch = new OneShotLatch();

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " waiting...");
                    latch.await();
                    System.out.println(Thread.currentThread().getName() + " proceed!");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        Thread.sleep(2000);
        System.out.println("Main thread signaling...");
        latch.signal();
    }
}
```

## 🔍 Key Takeaways
1. **Delegation**: Notice `OneShotLatch` does not extend AQS directly. It is a best practice to use a private inner class `Sync` to avoid exposing AQS internal methods (like `acquire`) to the public API of your component.
2. **Shared Mode**: We use `Shared` mode because multiple threads should be allowed to pass through the latch once it is signaled. If we used `Exclusive` mode, only one thread would wake up.
3. **State Management**: We only had to override two methods and write 5 lines of logic. AQS handled the complex logic of managing 3 waiting threads and waking them all up simultaneously.