# LEETCODE_SOLUTION — 1114. Print in Order

## Problem
Three threads must call `first()`, `second()`, `third()` in order.

## Parallel Stream Context
While not a stream problem, it demonstrates coordination primitives for parallel workloads.

```java
class Foo {
    private final CountDownLatch latch12 = new CountDownLatch(1);
    private final CountDownLatch latch23 = new CountDownLatch(1);

    public void first(Runnable printFirst) { printFirst.run(); latch12.countDown(); }
    public void second(Runnable printSecond) throws InterruptedException {
        latch12.await(); printSecond.run(); latch23.countDown();
    }
    public void third(Runnable printThird) throws InterruptedException {
        latch23.await(); printThird.run();
    }
}
```

## Key Insight
Parallel streams internally use fork/join — proper coordination is essential.

## Complexity
- Time: O(1)
- Space: O(1)
