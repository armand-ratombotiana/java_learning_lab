# AQS Theory & Intuition

## 💡 The Problem: Writing Synchronizers is Hard
Before AQS (Java 5), writing a custom lock or semaphore was incredibly difficult. You had to manually handle:
1. **Atomicity**: Correctly updating the state using low-level CAS (Compare-And-Swap).
2. **Blocking**: Suspending threads when they can't acquire the lock.
3. **Queueing**: Maintaining a fair or unfair list of waiting threads.
4. **Waking**: Correctly unparking the next thread in line without lost-update bugs.

## 🚀 The Solution: A Common Foundation
Doug Lea created AQS to provide a robust, high-performance foundation for all synchronizers. AQS handles the complex queueing and blocking logic, allowing developers to focus only on the **Acquisition Logic**.

### The Core State
AQS revolves around a single `volatile int state`. 
- For a **Lock**: `0` = free, `1` = locked.
- For a **Semaphore**: `n` = number of available permits.
- For a **CountDownLatch**: `n` = number of remaining counts.

### The Template Method Pattern
AQS uses the Template Method design pattern. You extend AQS and override specific protected methods:
- `tryAcquire(int)`: Try to get an exclusive lock.
- `tryRelease(int)`: Try to release an exclusive lock.
- `tryAcquireShared(int)`: Try to get a shared lock.
- `tryReleaseShared(int)`: Try to release a shared lock.

AQS provides all the "heavy lifting" methods like `acquire()`, `release()`, `acquireShared()`, and `releaseShared()`, which call your `try...` methods and handle the queueing if they return `false`.