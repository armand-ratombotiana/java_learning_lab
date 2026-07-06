# Step-by-Step: Locking Internals

## Step 1: Run AqsLock.java
Observe the custom lock in action. The main() method acquires the lock, executes a critical section, and releases it. The AQS-based implementation provides mutual exclusion.

## Step 2: Run ReentrantLockDemo.java
Watch the output for fair vs unfair lock comparison. Both achieve counter=5000, but the order of thread execution differs. In the unfair case, barging can cause later threads to execute earlier than some queued threads.

## Step 3: Run StampedLockDemo.java
The demo writes (10,20), then performs optimistic and pessimistic reads. The optimistic read may succeed without any lock acquisition. The tryConvertToWriteLock section shows upgrading from optimistic to write lock in one atomic operation.

## Step 4: Run LockSupportDemo.java
The park-thread parks itself and is unparked by main. The blocker-thread parks with a Blocker object — use `LockSupport.getBlocker(t2)` to see what it's waiting on. This is how thread dumps show "waiting on ReentrantLock" messages.

## Step 5: Run CasCounter.java
Ten threads each increment the counter 1000 times using CAS. The final value should be exactly 10000. If any CAS operation failed silently, the count would be lower. The retry loop ensures eventual success.

## Step 6: Run LockingTest.java
Execute all JUnit tests. The AqsLock test verifies mutual exclusion across 5 threads × 1000 increments. The ReentrantLock test verifies fairness and hold count. The CasCounter test verifies atomicity.
