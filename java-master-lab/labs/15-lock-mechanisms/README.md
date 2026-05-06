# Lab 15: Lock Mechanisms

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Intermediate-Advanced |
| **Estimated Time** | 4 hours |
| **Real-World Context** | Building a thread-safe data structure |
| **Prerequisites** | Lab 14: Concurrent Collections |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Understand ReentrantLock** and its advantages
2. **Use ReadWriteLock** for read-heavy workloads
3. **Implement StampedLock** for high performance
4. **Work with Condition variables** for thread coordination
5. **Prevent deadlocks** with proper lock ordering
6. **Build a thread-safe data structure** with advanced locks

## 📚 Prerequisites

- Lab 14: Concurrent Collections completed
- Understanding of synchronization
- Knowledge of thread coordination
- Familiarity with concurrent patterns

## 🧠 Concept Theory

### 1. ReentrantLock

Explicit lock with more control than synchronized:

```java
import java.util.concurrent.locks.*;

// ReentrantLock - more flexible than synchronized
Lock lock = new ReentrantLock();

// Lock and unlock
lock.lock();
try {
    // Critical section
    System.out.println("Protected code");
} finally {
    lock.unlock();  // Always unlock in finally
}

// Try lock with timeout
if (lock.tryLock(1, TimeUnit.SECONDS)) {
    try {
        // Critical section
    } finally {
        lock.unlock();
    }
} else {
    System.out.println("Could not acquire lock");
}

// Reentrant - same thread can acquire multiple times
lock.lock();
lock.lock();  // OK - same thread
lock.unlock();
lock.unlock();

// Fair lock - FIFO ordering
Lock fairLock = new ReentrantLock(true);

// Get lock state
int holdCount = ((ReentrantLock) lock).getHoldCount();
boolean isLocked = ((ReentrantLock) lock).isLocked();
boolean isHeldByCurrentThread = ((ReentrantLock) lock).isHeldByCurrentThread();
```

### 2. ReadWriteLock

Separate locks for reading and writing:

```java
// ReadWriteLock - multiple readers, single writer
ReadWriteLock rwLock = new ReentrantReadWriteLock();

// Read lock - multiple threads can hold simultaneously
rwLock.readLock().lock();
try {
    // Read operation
    int value = getValue();
} finally {
    rwLock.readLock().unlock();
}

// Write lock - exclusive access
rwLock.writeLock().lock();
try {
    // Write operation
    setValue(newValue);
} finally {
    rwLock.writeLock().unlock();
}

// Use case: Cache with many readers, few writers
class Cache<K, V> {
    private Map<K, V> data = new HashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public V get(K key) {
        lock.readLock().lock();
        try {
            return data.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            data.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

### 3. StampedLock

High-performance lock with optimistic reading:

```java
// StampedLock - optimistic reading for high performance
StampedLock lock = new StampedLock();

// Optimistic read - no lock acquired
long stamp = lock.tryOptimisticRead();
int value = getValue();
if (!lock.validate(stamp)) {
    // Value may have changed, retry with read lock
    stamp = lock.readLock();
    try {
        value = getValue();
    } finally {
        lock.unlockRead(stamp);
    }
}

// Read lock
stamp = lock.readLock();
try {
    value = getValue();
} finally {
    lock.unlockRead(stamp);
}

// Write lock
stamp = lock.writeLock();
try {
    setValue(newValue);
} finally {
    lock.unlockWrite(stamp);
}

// Convert read lock to write lock
stamp = lock.readLock();
try {
    if (needsWrite()) {
        stamp = lock.tryConvertToWriteLock(stamp);
        if (stamp == 0) {
            lock.unlockRead(stamp);
            stamp = lock.writeLock();
        }
        // Now have write lock
    }
} finally {
    lock.unlock(stamp);
}
```

### 4. Condition Variables

Coordinating threads with locks:

```java
// Condition - wait/notify with explicit locks
Lock lock = new ReentrantLock();
Condition condition = lock.newCondition();

// Producer
new Thread(() -> {
    lock.lock();
    try {
        // Produce data
        data = produceData();
        condition.signalAll();  // Wake up consumers
    } finally {
        lock.unlock();
    }
}).start();

// Consumer
new Thread(() -> {
    lock.lock();
    try {
        while (data == null) {
            condition.await();  // Wait for signal
        }
        consumeData(data);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    } finally {
        lock.unlock();
    }
}).start();

// Await with timeout
if (condition.await(1, TimeUnit.SECONDS)) {
    System.out.println("Signaled");
} else {
    System.out.println("Timeout");
}
```

### 5. Deadlock Prevention

Strategies to avoid deadlocks:

```java
// ❌ Potential deadlock - inconsistent lock ordering
class Account {
    private Lock lock = new ReentrantLock();
    private int balance;
    
    public void transfer(Account other, int amount) {
        lock.lock();
        try {
            other.lock.lock();  // May cause deadlock
            try {
                this.balance -= amount;
                other.balance += amount;
            } finally {
                other.lock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }
}

// ✅ Deadlock prevention - consistent lock ordering
class SafeAccount {
    private Lock lock = new ReentrantLock();
    private int balance;
    private int id;
    
    public void transfer(SafeAccount other, int amount) {
        SafeAccount first = this;
        SafeAccount second = other;
        
        // Ensure consistent ordering
        if (first.id > second.id) {
            SafeAccount temp = first;
            first = second;
            second = temp;
        }
        
        first.lock.lock();
        try {
            second.lock.lock();
            try {
                first.balance -= amount;
                second.balance += amount;
            } finally {
                second.lock.unlock();
            }
        } finally {
            first.lock.unlock();
        }
    }
}

// ✅ Deadlock prevention - timeout
public boolean transferWithTimeout(Account other, int amount, long timeout, TimeUnit unit) {
    if (!lock.tryLock(timeout, unit)) {
        return false;
    }
    try {
        if (!other.lock.tryLock(timeout, unit)) {
            return false;
        }
        try {
            this.balance -= amount;
            other.balance += amount;
            return true;
        } finally {
            other.lock.unlock();
        }
    } finally {
        lock.unlock();
    }
}
```

### 6. Lock Performance

Comparing lock mechanisms:

```java
// Performance comparison
long startTime;
int iterations = 1000000;

// Synchronized
Object syncLock = new Object();
startTime = System.nanoTime();
for (int i = 0; i < iterations; i++) {
    synchronized(syncLock) {
        // Critical section
    }
}
long syncTime = System.nanoTime() - startTime;

// ReentrantLock
Lock lock = new ReentrantLock();
startTime = System.nanoTime();
for (int i = 0; i < iterations; i++) {
    lock.lock();
    try {
        // Critical section
    } finally {
        lock.unlock();
    }
}
long lockTime = System.nanoTime() - startTime;

// ReadWriteLock (read-heavy)
ReadWriteLock rwLock = new ReentrantReadWriteLock();
startTime = System.nanoTime();
for (int i = 0; i < iterations; i++) {
    rwLock.readLock().lock();
    try {
        // Read operation
    } finally {
        rwLock.readLock().unlock();
    }
}
long rwTime = System.nanoTime() - startTime;

System.out.println("Synchronized: " + syncTime);
System.out.println("ReentrantLock: " + lockTime);
System.out.println("ReadWriteLock: " + rwTime);
```

### 7. Best Practices

Lock programming guidelines:

```java
// ❌ Bad: Not unlocking in case of exception
Lock lock = new ReentrantLock();
lock.lock();
doSomething();  // May throw exception
lock.unlock();  // May not execute

// ✅ Good: Always unlock in finally
lock.lock();
try {
    doSomething();
} finally {
    lock.unlock();
}

// ❌ Bad: Holding locks too long
lock.lock();
try {
    expensiveOperation();  // Long operation
    criticalSection();
} finally {
    lock.unlock();
}

// ✅ Good: Minimize lock holding time
expensiveOperation();
lock.lock();
try {
    criticalSection();
} finally {
    lock.unlock();
}

// ❌ Bad: Nested locks without ordering
lock1.lock();
lock2.lock();  // May cause deadlock

// ✅ Good: Consistent lock ordering
if (lock1Id < lock2Id) {
    lock1.lock();
    lock2.lock();
} else {
    lock2.lock();
    lock1.lock();
}
```

### 8. Lock Monitoring

Checking lock state:

```java
Lock lock = new ReentrantLock();

// Check if locked
if (((ReentrantLock) lock).isLocked()) {
    System.out.println("Lock is held");
}

// Check if held by current thread
if (((ReentrantLock) lock).isHeldByCurrentThread()) {
    System.out.println("Current thread holds lock");
}

// Get hold count
int holdCount = ((ReentrantLock) lock).getHoldCount();
System.out.println("Hold count: " + holdCount);

// Get queue length
int queueLength = ((ReentrantLock) lock).getQueueLength();
System.out.println("Threads waiting: " + queueLength);

// Check if fair
boolean isFair = ((ReentrantLock) lock).isFair();
System.out.println("Fair: " + isFair);
```

### 9. Choosing the Right Lock

Selection guide:

```java
// Simple synchronization: synchronized
synchronized(lock) {
    // Critical section
}

// Need timeout or fairness: ReentrantLock
Lock lock = new ReentrantLock(true);  // Fair
if (lock.tryLock(1, TimeUnit.SECONDS)) {
    try {
        // Critical section
    } finally {
        lock.unlock();
    }
}

// Read-heavy workload: ReadWriteLock
ReadWriteLock rwLock = new ReentrantReadWriteLock();
rwLock.readLock().lock();  // Multiple readers
rwLock.writeLock().lock();  // Exclusive writer

// High-performance read: StampedLock
StampedLock lock = new StampedLock();
long stamp = lock.tryOptimisticRead();
// Read data
if (!lock.validate(stamp)) {
    // Retry with read lock
}
```

### 10. Best Practices Summary

Lock programming guidelines:

```java
// ✅ Always use try-finally
Lock lock = new ReentrantLock();
lock.lock();
try {
    // Critical section
} finally {
    lock.unlock();
}

// ✅ Minimize lock scope
lock.lock();
try {
    criticalSection();
} finally {
    lock.unlock();
}

// ✅ Use consistent lock ordering
// ✅ Avoid nested locks
// ✅ Use timeouts when appropriate
// ✅ Monitor lock contention
// ✅ Choose appropriate lock type
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Use ReentrantLock

**Objective**: Implement explicit locking with ReentrantLock

**Acceptance Criteria**:
- [ ] ReentrantLock created
- [ ] Lock/unlock properly
- [ ] Try-finally used
- [ ] Timeout handling
- [ ] Code compiles without errors

**Instructions**:
1. Create ReentrantLock
2. Implement critical section
3. Handle timeouts
4. Test thread safety
5. Verify correctness

### Task 2: Implement ReadWriteLock

**Objective**: Use separate locks for reading and writing

**Acceptance Criteria**:
- [ ] ReadWriteLock created
- [ ] Read lock usage
- [ ] Write lock usage
- [ ] Multiple readers work
- [ ] Exclusive writer works

**Instructions**:
1. Create ReadWriteLock
2. Implement read operations
3. Implement write operations
4. Test with multiple threads
5. Verify correctness

### Task 3: Prevent Deadlocks

**Objective**: Design systems that avoid deadlocks

**Acceptance Criteria**:
- [ ] Consistent lock ordering
- [ ] Timeout mechanisms
- [ ] Deadlock detection
- [ ] Prevention strategies
- [ ] Verified safe

**Instructions**:
1. Identify deadlock scenarios
2. Implement prevention
3. Test with multiple threads
4. Verify no deadlocks
5. Document strategies

---

## 🎨 Mini-Project: Thread-Safe Data Structure

### Project Overview

**Description**: Create a thread-safe data structure using advanced lock mechanisms.

**Real-World Application**: Concurrent data structures, thread-safe collections, performance-critical systems.

**Learning Value**: Master lock mechanisms and thread-safe design patterns.

### Project Structure

```
thread-safe-data-structure/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── ThreadSafeList.java
│   │           ├── ThreadSafeMap.java
│   │           ├── LockStats.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               └── ThreadSafeStructureTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create ThreadSafeList Class

```java
package com.learning;

import java.util.*;
import java.util.concurrent.locks.*;

/**
 * Thread-safe list implementation using ReadWriteLock.
 */
public class ThreadSafeList<T> {
    private List<T> list = new ArrayList<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    
    /**
     * Add element.
     */
    public void add(T element) {
        lock.writeLock().lock();
        try {
            list.add(element);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Get element.
     */
    public T get(int index) {
        lock.readLock().lock();
        try {
            return list.get(index);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Remove element.
     */
    public T remove(int index) {
        lock.writeLock().lock();
        try {
            return list.remove(index);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Get size.
     */
    public int size() {
        lock.readLock().lock();
        try {
            return list.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get all elements.
     */
    public List<T> getAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(list);
        } finally {
            lock.readLock().unlock();
        }
    }
}
```

#### Step 2: Create ThreadSafeMap Class

```java
package com.learning;

import java.util.*;
import java.util.concurrent.locks.*;

/**
 * Thread-safe map implementation using ReentrantLock.
 */
public class ThreadSafeMap<K, V> {
    private Map<K, V> map = new HashMap<>();
    private Lock lock = new ReentrantLock();
    
    /**
     * Put value.
     */
    public void put(K key, V value) {
        lock.lock();
        try {
            map.put(key, value);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Get value.
     */
    public V get(K key) {
        lock.lock();
        try {
            return map.get(key);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Remove value.
     */
    public V remove(K key) {
        lock.lock();
        try {
            return map.remove(key);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Get size.
     */
    public int size() {
        lock.lock();
        try {
            return map.size();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Get all entries.
     */
    public Map<K, V> getAll() {
        lock.lock();
        try {
            return new HashMap<>(map);
        } finally {
            lock.unlock();
        }
    }
}
```

#### Step 3: Create LockStats Class

```java
package com.learning;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks lock statistics.
 */
public class LockStats {
    private AtomicLong readOperations = new AtomicLong(0);
    private AtomicLong writeOperations = new AtomicLong(0);
    private AtomicLong contentions = new AtomicLong(0);
    
    /**
     * Record read operation.
     */
    public void recordRead() {
        readOperations.incrementAndGet();
    }
    
    /**
     * Record write operation.
     */
    public void recordWrite() {
        writeOperations.incrementAndGet();
    }
    
    /**
     * Record contention.
     */
    public void recordContention() {
        contentions.incrementAndGet();
    }
    
    /**
     * Get statistics.
     */
    public void displayStats() {
        System.out.println("\n========== LOCK STATS ==========");
        System.out.println("Read Operations: " + readOperations.get());
        System.out.println("Write Operations: " + writeOperations.get());
        System.out.println("Contentions: " + contentions.get());
        System.out.println("================================\n");
    }
}
```

#### Step 4: Create Main Class

```java
package com.learning;

/**
 * Main entry point for Thread-Safe Data Structure.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Create thread-safe structures
        ThreadSafeList<Integer> list = new ThreadSafeList<>();
        ThreadSafeMap<String, Integer> map = new ThreadSafeMap<>();
        LockStats stats = new LockStats();
        
        // Add initial data
        for (int i = 0; i < 10; i++) {
            list.add(i);
            map.put("key" + i, i * 100);
        }
        
        // Create reader threads
        Thread[] readers = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int threadNum = i;
            readers[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    Integer value = list.get(j % list.size());
                    stats.recordRead();
                }
            });
        }
        
        // Create writer threads
        Thread[] writers = new Thread[2];
        for (int i = 0; i < 2; i++) {
            final int threadNum = i;
            writers[i] = new Thread(() -> {
                for (int j = 0; j < 50; j++) {
                    list.add(threadNum * 1000 + j);
                    stats.recordWrite();
                }
            });
        }
        
        // Start all threads
        for (Thread t : readers) t.start();
        for (Thread t : writers) t.start();
        
        // Wait for completion
        for (Thread t : readers) t.join();
        for (Thread t : writers) t.join();
        
        // Display statistics
        stats.displayStats();
        System.out.println("Final list size: " + list.size());
        System.out.println("Final map size: " + map.size());
    }
}
```

#### Step 5: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for thread-safe structures.
 */
public class ThreadSafeStructureTest {
    
    private ThreadSafeList<Integer> list;
    private ThreadSafeMap<String, Integer> map;
    
    @BeforeEach
    void setUp() {
        list = new ThreadSafeList<>();
        map = new ThreadSafeMap<>();
    }
    
    @Test
    void testListAddAndGet() {
        list.add(100);
        assertEquals(100, list.get(0));
    }
    
    @Test
    void testListSize() {
        list.add(1);
        list.add(2);
        assertEquals(2, list.size());
    }
    
    @Test
    void testMapPutAndGet() {
        map.put("key1", 100);
        assertEquals(100, map.get("key1"));
    }
    
    @Test
    void testMapSize() {
        map.put("key1", 100);
        map.put("key2", 200);
        assertEquals(2, map.size());
    }
    
    @Test
    void testConcurrentAccess() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                list.add(i);
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                if (list.size() > 0) {
                    list.get(0);
                }
            }
        });
        
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        
        assertEquals(100, list.size());
    }
}
```

### Running the Project

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Run the application
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

---

## 📝 Exercises

### Exercise 1: Deadlock Detection

**Objective**: Detect and prevent deadlocks

**Task Description**:
Create system that detects and prevents deadlock scenarios

**Acceptance Criteria**:
- [ ] Deadlock detection
- [ ] Prevention strategies
- [ ] Timeout handling
- [ ] Recovery mechanisms
- [ ] Tests pass

### Exercise 2: Lock Performance Comparison

**Objective**: Compare different lock mechanisms

**Task Description**:
Benchmark synchronized vs ReentrantLock vs ReadWriteLock

**Acceptance Criteria**:
- [ ] Performance metrics
- [ ] Comparison analysis
- [ ] Recommendations
- [ ] Documentation
- [ ] Accurate results

### Exercise 3: Condition Variable Coordination

**Objective**: Use condition variables for coordination

**Task Description**:
Create producer-consumer with condition variables

**Acceptance Criteria**:
- [ ] Producer implementation
- [ ] Consumer implementation
- [ ] Proper coordination
- [ ] No deadlocks
- [ ] Tests pass

---

## 🧪 Quiz

### Question 1: What is ReentrantLock?

A) A lock that can be acquired once  
B) A lock that can be acquired multiple times by same thread  
C) A lock for reading only  
D) A lock for writing only  

**Answer**: B) A lock that can be acquired multiple times by same thread

### Question 2: When should you use ReadWriteLock?

A) Always  
B) For write-heavy workloads  
C) For read-heavy workloads  
D) Never  

**Answer**: C) For read-heavy workloads

### Question 3: What does StampedLock provide?

A) Optimistic reading  
B) High performance  
C) Both A and B  
D) Neither  

**Answer**: C) Both A and B

### Question 4: How do you prevent deadlock?

A) Use consistent lock ordering  
B) Use timeouts  
C) Avoid nested locks  
D) All of the above  

**Answer**: D) All of the above

### Question 5: What is a Condition variable?

A) A variable that changes  
B) A mechanism for thread coordination  
C) A type of lock  
D) A synchronization primitive  

**Answer**: B) A mechanism for thread coordination

---

## 🚀 Advanced Challenge

### Challenge: Complete Lock Framework

**Difficulty**: Advanced

**Objective**: Build comprehensive lock framework

**Requirements**:
- [ ] Multiple lock types
- [ ] Deadlock detection
- [ ] Performance monitoring
- [ ] Fairness guarantees
- [ ] Timeout support
- [ ] Statistics tracking

---

## 🏆 Best Practices

### Lock Programming

1. **Always Use Try-Finally**
   - Ensure locks are released
   - Handle exceptions properly
   - Prevent resource leaks

2. **Minimize Lock Scope**
   - Hold locks briefly
   - Avoid long operations
   - Reduce contention

3. **Prevent Deadlocks**
   - Consistent ordering
   - Use timeouts
   - Avoid nesting

---

## 🔗 Next Steps

**Next Lab**: [Lab 16: File I/O](../16-file-io/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built thread-safe data structure
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 15! 🎉**

You've mastered advanced lock mechanisms. Ready for file I/O? Move on to [Lab 16: File I/O](../16-file-io/README.md).