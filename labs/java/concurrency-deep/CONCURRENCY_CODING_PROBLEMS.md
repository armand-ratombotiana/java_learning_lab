# Concurrency Coding Problems — 20 Full Solutions

> Complete solutions with analysis, complexity, and company frequency

---

## Problem 1: Thread-Safe Singleton

**Problem**: Implement a thread-safe singleton pattern. Evaluate multiple approaches.

**Solution Approach**: Four canonical approaches with trade-offs.

### Approach A: Bill Pugh — Inner Static Holder

```java
public class Singleton {

    private Singleton() {}

    private static class Holder {
        static final Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance() {
        return Holder.INSTANCE;
    }
}
```

**Thread safety**: Guaranteed by JVM class loading mechanism (class is loaded on first access, and class loading is serialized by the JVM).
**Lazy?**: Yes — `Holder` is loaded only when `getInstance()` is called.
**Performance**: No synchronization after class loading.

### Approach B: Enum (Joshua Bloch)

```java
public enum Singleton {
    INSTANCE;

    private final SomeService service = new SomeService();

    public SomeService getService() {
        return service;
    }
}
```

**Thread safety**: JVM guarantees enum instantiation is serialized.
**Lazy?**: No (eager).
**Serialization**: Free — enum serialization works correctly out of the box.
**Reflection**: Cannot be broken via reflection.

### Approach C: Double-Checked Locking (corrected)

```java
public class Singleton {

    private static volatile Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        Singleton result = instance;
        if (result == null) {
            synchronized (Singleton.class) {
                result = instance;
                if (result == null) {
                    instance = result = new Singleton();
                }
            }
        }
        return result;
    }
}
```

**Thread safety**: Requires `volatile` to prevent reordering of the `new Singleton()` write with assignment to `instance`.
**Local variable**: Using `result` reduces volatile reads by ~50% (Java 8+ optimization pattern).

**Complexity**: O(1) amortized
**Company frequency**: Amazon ★★★★★, Google ★★★★, Microsoft ★★★★, Meta ★★★
**Follow-up**: "Why volatile? Show the exact reordering that breaks it."

---

## Problem 2: Bounded Blocking Queue

**Problem**: Implement a fixed-size blocking queue for producer-consumer.

**Solution Approach**: Circular array + ReentrantLock + two Conditions.

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBlockingQueue<E> {

    private final E[] items;
    private int putIndex;
    private int takeIndex;
    private int count;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    @SuppressWarnings("unchecked")
    public BoundedBlockingQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        items = (E[]) new Object[capacity];
    }

    public void put(E e) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (count == items.length) {
                notFull.await();
            }
            items[putIndex] = e;
            if (++putIndex == items.length) putIndex = 0;
            count++;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public E take() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            E e = items[takeIndex];
            items[takeIndex] = null;
            if (++takeIndex == items.length) takeIndex = 0;
            count--;
            notFull.signal();
            return e;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}
```

**Complexity**: O(1) per operation
**Company frequency**: Amazon ★★★★★, Uber ★★★★, DoorDash ★★★★
**Follow-up**: "Make it fair (FIFO ordering for waiting threads)", "Multiple conditions vs single condition"

---

## Problem 3: Producer-Consumer with Multiple Producers/Consumers

**Problem**: Implement multi-producer, multi-consumer using a blocking queue with shutdown support.

```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProducerConsumer {

    private static final int POISON_PILL = -1;
    private final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(100);
    private final AtomicBoolean running = new AtomicBoolean(true);

    public class Producer implements Runnable {
        private final int id;
        public Producer(int id) { this.id = id; }

        public void run() {
            try {
                while (running.get()) {
                    int task = produceTask();
                    queue.put(task);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private int produceTask() { return (int) (Math.random() * 100); }
    }

    public class Consumer implements Runnable {
        private final int id;
        public Consumer(int id) { this.id = id; }

        public void run() {
            try {
                while (true) {
                    Integer task = queue.take();
                    if (task == POISON_PILL) {
                        queue.put(POISON_PILL);
                        break;
                    }
                    consume(task);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void consume(int task) { System.out.println("Consumer " + id + " processed " + task); }
    }

    public void shutdown() {
        running.set(false);
        try { queue.put(POISON_PILL); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
```

**Complexity**: O(1) enqueue/dequeue amortized
**Company frequency**: Amazon ★★★★★, Google ★★★, Microsoft ★★★★
**Follow-up**: "Poison pill vs interrupt-based shutdown", "Backpressure"

---

## Problem 4: Concurrent LRU Cache

**Problem**: Design a thread-safe LRU cache with O(1) get and put.

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentLRUCache<K, V> {

    private final int capacity;
    private final ConcurrentHashMap<K, V> map = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<K> deque = new ConcurrentLinkedDeque<>();
    private final ReentrantLock lock = new ReentrantLock();

    public ConcurrentLRUCache(int capacity) {
        this.capacity = capacity;
    }

    public V get(K key) {
        V value = map.get(key);
        if (value != null) {
            lock.lock();
            try {
                deque.remove(key);
                deque.addFirst(key);
            } finally {
                lock.unlock();
            }
        }
        return value;
    }

    public void put(K key, V value) {
        lock.lock();
        try {
            if (map.containsKey(key)) {
                deque.remove(key);
            } else if (map.size() >= capacity) {
                K evictKey = deque.removeLast();
                map.remove(evictKey);
            }
            deque.addFirst(key);
        } finally {
            lock.unlock();
        }
        map.put(key, value);
    }
}
```

**Complexity**: O(1) amortized (ConcurrentLinkedDeque remove is O(n) — improved version with linked nodes)
**Company frequency**: Google ★★★★★, Amazon ★★★★, Meta ★★★★
**Follow-up**: "Remove the global lock — use striped locking", "Implement true O(1) remove with node reference"

---

## Problem 5: Thread Pool from Scratch

**Problem**: Implement a simple but correct thread pool.

```java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleThreadPool {

    private final List<Worker> workers = new ArrayList<>();
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean isStopped = new AtomicBoolean(false);

    public SimpleThreadPool(int numThreads) {
        for (int i = 0; i < numThreads; i++) {
            Worker w = new Worker(taskQueue, isStopped);
            workers.add(w);
            new Thread(w, "pool-worker-" + i).start();
        }
    }

    public void execute(Runnable task) {
        if (isStopped.get()) throw new IllegalStateException("Pool is shutdown");
        taskQueue.offer(task);
    }

    public void shutdown() {
        isStopped.set(true);
    }

    private static class Worker implements Runnable {
        private final BlockingQueue<Runnable> queue;
        private final AtomicBoolean stopped;

        Worker(BlockingQueue<Runnable> queue, AtomicBoolean stopped) {
            this.queue = queue;
            this.stopped = stopped;
        }

        public void run() {
            while (!stopped.get() || !queue.isEmpty()) {
                try {
                    Runnable task = queue.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS);
                    if (task != null) {
                        task.run();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
```

**Complexity**: O(1) task submission
**Company frequency**: Oracle ★★★★★, Goldman Sachs ★★★★, Amazon ★★★
**Follow-up**: "Add core/max pool size", "Add keepAliveTime", "How do you handle rejected tasks?"

---

## Problem 6: Deadlock Detection

**Problem**: Detect deadlocks using ThreadMXBean.

```java
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;

public class DeadlockDetector {

    private final ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();

    public long[] findDeadlockedThreads() {
        return threadMxBean.findDeadlockedThreads();
    }

    public void printDeadlockInfo() {
        long[] deadlockedIds = findDeadlockedThreads();
        if (deadlockedIds == null) {
            System.out.println("No deadlocks detected");
            return;
        }

        ThreadInfo[] infos = threadMxBean.getThreadInfo(deadlockedIds, true, true);
        for (ThreadInfo info : infos) {
            System.out.printf("Thread: %s (%d)%n", info.getThreadName(), info.getThreadId());
            System.out.printf("Blocked on: %s owned by %s%n",
                info.getLockName(), info.getLockOwnerName());
            System.out.printf("Stack trace: %s%n", Arrays.toString(info.getStackTrace()));
        }
    }

    // Example deadlock scenario
    public static void createDeadlock() {
        final Object lock1 = new Object();
        final Object lock2 = new Object();

        Thread t1 = new Thread(() -> {
            while (true) {
                synchronized (lock1) {
                    try { Thread.sleep(50); } catch (InterruptedException e) {}
                    synchronized (lock2) { System.out.println("T1 acquired both"); }
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                synchronized (lock2) {
                    try { Thread.sleep(50); } catch (InterruptedException e) {}
                    synchronized (lock1) { System.out.println("T2 acquired both"); }
                }
            }
        });

        t1.start(); t2.start();
    }
}
```

**Complexity**: O(N) where N = number of threads
**Company frequency**: Oracle ★★★★★, Amazon ★★★★, Microsoft ★★★★
**Follow-up**: "How would you prevent deadlocks?", "Lock ordering strategy"

---

## Problem 7: Rate Limiter (Token Bucket)

**Problem**: Implement a thread-safe token bucket rate limiter.

```java
import java.util.concurrent.atomic.AtomicLong;

public class TokenBucketRateLimiter {

    private final long capacity;
    private final long refillTokens;
    private final long refillPeriodNanos;
    private final AtomicLong tokens;
    private volatile long lastRefillNanos;

    public TokenBucketRateLimiter(long capacity, long refillTokens, long refillPeriodNanos) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriodNanos = refillPeriodNanos;
        this.tokens = new AtomicLong(capacity);
        this.lastRefillNanos = System.nanoTime();
    }

    public boolean tryAcquire() {
        refill();
        while (true) {
            long current = tokens.get();
            if (current <= 0) return false;
            if (tokens.compareAndSet(current, current - 1)) return true;
        }
    }

    private void refill() {
        long now = System.nanoTime();
        long elapsed = now - lastRefillNanos;
        if (elapsed >= refillPeriodNanos) {
            long newTokens = (elapsed / refillPeriodNanos) * refillTokens;
            if (newTokens > 0) {
                tokens.updateAndGet(t -> Math.min(capacity, t + newTokens));
                lastRefillNanos = now;
            }
        }
    }
}
```

**Complexity**: O(1) amortized
**Company frequency**: Google ★★★★★, Amazon ★★★★, Twitter ★★★★
**Follow-up**: "Sliding window vs token bucket", "Distributed rate limiting with Redis"

---

## Problem 8: Readers-Writers Preference

**Problem**: Implement readers-writers with reader preference.

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReadersWriters {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition okToWrite = lock.newCondition();
    private int readers = 0;
    private boolean writing = false;

    public void startRead() throws InterruptedException {
        lock.lock();
        try {
            while (writing) {
                okToWrite.await();
            }
            readers++;
        } finally {
            lock.unlock();
        }
    }

    public void endRead() {
        lock.lock();
        try {
            if (--readers == 0) {
                okToWrite.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public void startWrite() throws InterruptedException {
        lock.lock();
        try {
            while (readers > 0 || writing) {
                okToWrite.await();
            }
            writing = true;
        } finally {
            lock.unlock();
        }
    }

    public void endWrite() {
        lock.lock();
        try {
            writing = false;
            okToWrite.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
```

**Complexity**: O(1) per operation
**Company frequency**: Oracle ★★★★★, Microsoft ★★★★
**Follow-up**: "Writer preference variation", "Fair readers-writers"

---

## Problem 9: Dining Philosophers (No Deadlock)

**Problem**: Implement dining philosophers guaranteeing no deadlock.

```java
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {

    private static class Philosopher extends Thread {
        private final ReentrantLock leftFork;
        private final ReentrantLock rightFork;
        private final int id;

        Philosopher(int id, ReentrantLock left, ReentrantLock right) {
            this.id = id;
            this.leftFork = left;
            this.rightFork = right;
        }

        public void run() {
            try {
                while (true) {
                    think();
                    eat();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void think() throws InterruptedException {
            System.out.println("Philosopher " + id + " thinking");
            Thread.sleep((long) (Math.random() * 100));
        }

        private void eat() throws InterruptedException {
            // Lock forks in fixed order to avoid deadlock
            ReentrantLock first = leftFork;
            ReentrantLock second = rightFork;
            if (id == 4) { // Last philosopher picks up right first
                first = rightFork;
                second = leftFork;
            }

            first.lock();
            try {
                second.lock();
                try {
                    System.out.println("Philosopher " + id + " eating");
                    Thread.sleep((long) (Math.random() * 100));
                } finally {
                    second.unlock();
                }
            } finally {
                first.unlock();
            }
        }
    }

    public static void main(String[] args) {
        int n = 5;
        ReentrantLock[] forks = new ReentrantLock[n];
        for (int i = 0; i < n; i++) forks[i] = new ReentrantLock();

        for (int i = 0; i < n; i++) {
            new Philosopher(i, forks[i], forks[(i + 1) % n]).start();
        }
    }
}
```

**Complexity**: O(1) per eat cycle
**Company frequency**: Google ★★★★, Oracle ★★★★, Amazon ★★★
**Follow-up**: "Using tryLock with timeout", "What about starvation?"

---

## Problem 10: CountDownLatch from Scratch

**Problem**: Implement a reusable CountDownLatch.

```java
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class MyCountDownLatch {

    private static final class Sync extends AbstractQueuedSynchronizer {
        Sync(int count) { setState(count); }

        int getCount() { return getState(); }

        protected int tryAcquireShared(int acquires) {
            return getState() == 0 ? 1 : -1;
        }

        protected boolean tryReleaseShared(int releases) {
            for (;;) {
                int c = getState();
                if (c == 0) return false;
                int nextc = c - 1;
                if (compareAndSetState(c, nextc)) {
                    return nextc == 0;
                }
            }
        }
    }

    private final Sync sync;

    public MyCountDownLatch(int count) {
        if (count < 0) throw new IllegalArgumentException();
        this.sync = new Sync(count);
    }

    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    public boolean await(long timeout, java.util.concurrent.TimeUnit unit)
            throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    public void countDown() {
        sync.releaseShared(1);
    }

    public long getCount() {
        return sync.getCount();
    }
}
```

**Complexity**: O(1) amortized
**Company frequency**: Oracle ★★★★★, Goldman Sachs ★★★★
**Follow-up**: "Use AQS vs ReentrantLock + Conditions", "How does AQS work?"

---

## Problem 11: Lock-Free Stack (Treiber)

**Problem**: Implement a lock-free stack using CAS.

```java
import java.util.concurrent.atomic.AtomicReference;

public class TreiberStack<E> {

    private final AtomicReference<Node<E>> top = new AtomicReference<>();

    private static class Node<E> {
        final E value;
        Node<E> next;
        Node(E value) { this.value = value; }
    }

    public void push(E value) {
        Node<E> newHead = new Node<>(value);
        while (true) {
            Node<E> oldHead = top.get();
            newHead.next = oldHead;
            if (top.compareAndSet(oldHead, newHead)) return;
        }
    }

    public E pop() {
        while (true) {
            Node<E> oldHead = top.get();
            if (oldHead == null) return null;
            Node<E> newHead = oldHead.next;
            if (top.compareAndSet(oldHead, newHead)) {
                return oldHead.value;
            }
        }
    }

    public boolean isEmpty() {
        return top.get() == null;
    }
}
```

**Complexity**: O(1) per operation (spin-wait on CAS)
**Company frequency**: Meta ★★★★★, Oracle ★★★★★, Google ★★★★
**Follow-up**: "ABA problem — how to fix?", "Elimination backoff", "Double-width CAS"

---

## Problem 12: Simple ConcurrentHashMap

**Problem**: Implement a simplified concurrent hash map using striped locking.

```java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleConcurrentHashMap<K, V> {

    private static class Segment<K, V> {
        private final ReentrantLock lock = new ReentrantLock();
        private volatile Entry<K, V>[] table;
        private int size;

        Segment(int capacity) {
            table = new Entry[capacity];
        }

        V get(K key, int hash) {
            int idx = hash & (table.length - 1);
            for (Entry<K, V> e = table[idx]; e != null; e = e.next) {
                if (e.hash == hash && (e.key == key || key.equals(e.key))) {
                    return e.value;
                }
            }
            return null;
        }

        V put(K key, int hash, V value) {
            lock.lock();
            try {
                int idx = hash & (table.length - 1);
                for (Entry<K, V> e = table[idx]; e != null; e = e.next) {
                    if (e.hash == hash && (e.key == key || key.equals(e.key))) {
                        V old = e.value;
                        e.value = value;
                        return old;
                    }
                }
                table[idx] = new Entry<>(hash, key, value, table[idx]);
                size++;
                return null;
            } finally {
                lock.unlock();
            }
        }

        static class Entry<K, V> {
            final int hash;
            final K key;
            volatile V value;
            Entry<K, V> next;
            Entry(int hash, K key, V value, Entry<K, V> next) {
                this.hash = hash; this.key = key; this.value = value; this.next = next;
            }
        }
    }

    private final Segment<K, V>[] segments;

    @SuppressWarnings("unchecked")
    public SimpleConcurrentHashMap(int concurrencyLevel) {
        segments = new Segment[concurrencyLevel];
        for (int i = 0; i < concurrencyLevel; i++) {
            segments[i] = new Segment<>(16);
        }
    }

    private Segment<K, V> segmentFor(Object key) {
        int hash = key.hashCode();
        return segments[(hash ^ (hash >>> 16)) & (segments.length - 1)];
    }

    public V get(K key) {
        return segmentFor(key).get(key, key.hashCode());
    }

    public V put(K key, V value) {
        return segmentFor(key).put(key, key.hashCode(), value);
    }
}
```

**Complexity**: O(1) average per operation
**Company frequency**: Google ★★★★★, Oracle ★★★★
**Follow-up**: "Resizing strategy", "Java 8 vs Java 7 approach"

---

## Problem 13: Barrier (CyclicBarrier Imitation)

```java
public class SimpleCyclicBarrier {

    private final int parties;
    private int waiting;
    private final Object lock = new Object();

    public SimpleCyclicBarrier(int parties) {
        this.parties = parties;
    }

    public void await() throws InterruptedException {
        synchronized (lock) {
            waiting++;
            if (waiting == parties) {
                lock.notifyAll();
                waiting = 0;
            } else {
                lock.wait();
            }
        }
    }

    public int getParties() { return parties; }
}
```

**Complexity**: O(1) per thread
**Company frequency**: Microsoft ★★★, Oracle ★★★
**Follow-up**: "Broken barrier detection", "Runnable barrier action"

---

## Problem 14: Non-Blocking Counter (AtomicReference + CAS)

```java
import java.util.concurrent.atomic.AtomicReference;

public class NonBlockingCounter {

    private static class CounterState {
        final int count;
        final long version;
        CounterState(int count, long version) {
            this.count = count;
            this.version = version;
        }
    }

    private final AtomicReference<CounterState> state =
        new AtomicReference<>(new CounterState(0, 0));

    public int increment() {
        while (true) {
            CounterState current = state.get();
            CounterState next = new CounterState(current.count + 1, current.version + 1);
            if (state.compareAndSet(current, next)) return next.count;
        }
    }

    public int get() {
        return state.get().count;
    }
}
```

**Complexity**: O(1) amortized
**Company frequency**: Meta ★★★★, Oracle ★★★★
**Follow-up**: "AtomicInteger vs this approach", "ABA problem"

---

## Problem 15: Work-Stealing Queue

```java
import java.util.concurrent.ConcurrentLinkedDeque;

public class WorkStealingQueue<T> {

    private final ConcurrentLinkedDeque<T> deque = new ConcurrentLinkedDeque<>();

    public void push(T task) {
        deque.addFirst(task);
    }

    public T pop() {
        return deque.pollFirst();
    }

    public T steal() {
        return deque.pollLast();
    }

    public boolean isEmpty() {
        return deque.isEmpty();
    }

    public int size() {
        return deque.size();
    }
}
```

**Complexity**: O(1) per operation
**Company frequency**: Google ★★★★, Oracle ★★★★
**Follow-up**: "Explain ForkJoinPool work-stealing algorithm"

---

## Problem 16: Future from Scratch

```java
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class SimpleFuture<T> implements Future<T> {

    private volatile T result;
    private volatile ExecutionException exception;
    private volatile boolean done;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition doneCondition = lock.newCondition();

    void complete(T value) {
        lock.lock();
        try {
            if (done) return;
            this.result = value;
            this.done = true;
            doneCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    void completeExceptionally(Throwable t) {
        lock.lock();
        try {
            if (done) return;
            this.exception = new ExecutionException(t);
            this.done = true;
            doneCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T get() throws InterruptedException, ExecutionException {
        lock.lock();
        try {
            while (!done) {
                doneCondition.await();
            }
            if (exception != null) throw exception;
            return result;
        } finally {
            lock.unlock();
        }
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        long nanos = unit.toNanos(timeout);
        lock.lock();
        try {
            while (!done) {
                if (nanos <= 0) throw new TimeoutException();
                nanos = doneCondition.awaitNanos(nanos);
            }
            if (exception != null) throw exception;
            return result;
        } finally {
            lock.unlock();
        }
    }

    public boolean isDone() { return done; }
    public boolean cancel(boolean mayInterrupt) { return false; }
    public boolean isCancelled() { return false; }
}
```

**Complexity**: O(1) per operation
**Company frequency**: Oracle ★★★★, Goldman Sachs ★★★★
**Follow-up**: "Cancellation support", "thenApply chain"

---

## Problem 17: Exchanger from Scratch

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.*;

public class SimpleExchanger<T> {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition otherArrived = lock.newCondition();
    private volatile T slot;
    private volatile boolean hasItem;

    public T exchange(T item) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            if (hasItem) {
                T result = slot;
                slot = item;
                otherArrived.signal();
                return result;
            } else {
                slot = item;
                hasItem = true;
                otherArrived.await();
                T result = slot;
                hasItem = false;
                return result;
            }
        } finally {
            lock.unlock();
        }
    }

    public T exchange(T item, long timeout, TimeUnit unit)
            throws InterruptedException, TimeoutException {
        long nanos = unit.toNanos(timeout);
        lock.lockInterruptibly();
        try {
            if (hasItem) {
                T result = slot;
                slot = item;
                otherArrived.signal();
                return result;
            } else {
                slot = item;
                hasItem = true;
                nanos = otherArrived.awaitNanos(nanos);
                if (nanos <= 0) {
                    hasItem = false;
                    slot = null;
                    throw new TimeoutException();
                }
                T result = slot;
                hasItem = false;
                return result;
            }
        } finally {
            lock.unlock();
        }
    }
}
```

**Complexity**: O(1) per exchange
**Company frequency**: Oracle ★★★, Microsoft ★★★
**Follow-up**: "Multiple pairs", "Fairness"

---

## Problem 18: Scoped Value Pattern (Java 21+)

```java
public class ScopedValueExample {

    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();
    private static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

    public static void handleRequest(String requestId, String userId) {
        User user = loadUser(userId);
        ScopedValue.where(REQUEST_ID, requestId)
                    .where(CURRENT_USER, user)
                    .run(() -> {
                        process();
                    });
    }

    private static void process() {
        String requestId = REQUEST_ID.get();
        User user = CURRENT_USER.get();
        System.out.println("Processing request " + requestId + " for " + user);
        audit();
    }

    private static void audit() {
        // ScopedValue is inherited within the scope
        System.out.println("Audit: " + REQUEST_ID.get());
    }

    private static User loadUser(String userId) { return new User(userId); }
    record User(String id) {}
}
```

**Complexity**: O(1) per scope operation
**Company frequency**: Oracle ★★★★ (emerging)
**Follow-up**: "ScopedValue vs ThreadLocal memory comparison"

---

## Problem 19: Structured Concurrency Example

```java
import java.util.concurrent.*;
import java.util.concurrent.StructuredTaskScope.*;

public class StructuredConcurrencyExample {

    record Order(long id, String customerName, double total) {}

    static class OrderService {
        static Order createOrder(String customerName, double amount) {
            // simulated work
            return new Order(ThreadLocalRandom.current().nextLong(), customerName, amount);
        }
    }

    static class PaymentService {
        static String processPayment(long orderId) {
            return "PAY-ORDER-" + orderId;
        }
    }

    static class NotificationService {
        static String sendConfirmation(String paymentRef) {
            return "EMAIL-SENT-" + paymentRef;
        }
    }

    public static void main(String[] args) throws Exception {
        String result = processOrder("Alice", 150.00);
        System.out.println("Result: " + result);
    }

    static String processOrder(String customer, double amount)
            throws InterruptedException, ExecutionException {

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            Future<Order> orderFuture = scope.fork(() -> OrderService.createOrder(customer, amount));
            Future<String> paymentFuture = scope.fork(() -> {
                Order order = orderFuture.resultNow();
                return PaymentService.processPayment(order.id());
            });
            Future<String> notificationFuture = scope.fork(() -> {
                String paymentRef = paymentFuture.resultNow();
                return NotificationService.sendConfirmation(paymentRef);
            });

            scope.join();
            scope.throwIfFailed();

            return "Order created: " + orderFuture.resultNow() +
                   ", Payment: " + paymentFuture.resultNow() +
                   ", Notification: " + notificationFuture.resultNow();
        }
    }
}
```

**Complexity**: O(1) per subtask creation
**Company frequency**: Oracle ★★★★★ (new hot topic)
**Follow-up**: "ShutdownOnSuccess vs ShutdownOnFailure", "Error handling"

---

## Problem 20: Virtual Thread Schedule Simulation

**Problem**: Demonstrate how virtual threads are scheduled on carriers.

```java
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class VirtualThreadDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Platform Thread Performance ===");
        long start = System.currentTimeMillis();
        try (var executor = Executors.newFixedThreadPool(100)) {
            IntStream.range(0, 10_000).forEach(i ->
                executor.submit(() -> {
                    try { Thread.sleep(10); } catch (InterruptedException e) {}
                    return i;
                })
            );
        }
        long platformTime = System.currentTimeMillis() - start;
        System.out.println("Platform thread pool: " + platformTime + "ms");

        System.out.println("=== Virtual Thread Performance ===");
        start = System.currentTimeMillis();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 10_000).forEach(i ->
                executor.submit(() -> {
                    try { Thread.sleep(10); } catch (InterruptedException e) {}
                    return i;
                })
            );
        }
        long vtTime = System.currentTimeMillis() - start;
        System.out.println("Virtual threads: " + vtTime + "ms");

        System.out.println("Speedup: " + (platformTime / (double) vtTime) + "x");

        // Demonstrate pinning with synchronized
        System.out.println("\n=== Pinning Demo ===");
        Object lock = new Object();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            start = System.currentTimeMillis();
            IntStream.range(0, 100).forEach(i ->
                executor.submit(() -> {
                    synchronized (lock) {
                        try { Thread.sleep(100); } catch (InterruptedException e) {}
                    }
                })
            );
        }
        System.out.println("Synchronized pinning: " + (System.currentTimeMillis() - start) + "ms");
    }
}
```

**Complexity**: N/A — benchmark demo
**Company frequency**: All companies with Java 21+
**Follow-up**: "Pinning causes", "Carrier pool sizing", "When NOT to use virtual threads"

---

## Evaluation Criteria (General)

| Criterion | Weight | What Interviewers Look For |
|-----------|--------|---------------------------|
| Correctness | 40% | Thread safety, no race conditions, proper synchronization |
| Completeness | 20% | Edge cases, interrupt handling, cleanup |
| Performance | 20% | Lock granularity, contention minimization, algorithm choice |
| Communication | 10% | Explaining trade-offs clearly |
| Testing | 10% | Test cases for concurrent correctness |

---

*Practice each problem on a whiteboard first, then implement in Java. Time yourself — you have 15-25 minutes per problem in interviews.*
