package com.learning.concurrency;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Comprehensive tests for Thread Basics Demo
 * Tests: Synchronization, thread safety, atomic operations
 */
@Timeout(10)  // All tests must complete in 10 seconds
public class ThreadBasicsTest {
    
    // ==================== THREAD CREATION TESTS ====================
    
    @Test
    public void testThreadCreation_ExtendThread() throws InterruptedException {
        ThreadBasicsDemo.CounterThread thread = new ThreadBasicsDemo.CounterThread("Test");
        thread.start();
        thread.join();
        // Thread should complete without error
        assertFalse(thread.isAlive());
    }
    
    @Test
    public void testThreadCreation_Runnable() throws InterruptedException {
        Thread thread = new Thread(new ThreadBasicsDemo.RunnableCounter("Test"));
        thread.start();
        thread.join();
        assertFalse(thread.isAlive());
    }
    
    // ==================== SYNCHRONIZATION TESTS ====================
    
    @Test
    public void testSynchronizedCounter_ThreadSafe() throws InterruptedException {
        ThreadBasicsDemo.SynchronizedCounter counter = new ThreadBasicsDemo.SynchronizedCounter();
        int threadCount = 10;
        int incrementsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        // Should have exact count despite concurrent access
        assertEquals(threadCount * incrementsPerThread, counter.getValue());
    }
    
    @Test
    public void testBlockSynchronizedCounter_ThreadSafe() throws InterruptedException {
        ThreadBasicsDemo.BlockSynchronizedCounter counter = new ThreadBasicsDemo.BlockSynchronizedCounter();
        int threadCount = 10;
        int incrementsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        assertEquals(threadCount * incrementsPerThread, counter.getValue());
    }
    
    @Test
    public void testUnsafeCounter_NotThreadSafe_Demonstration() throws InterruptedException {
        // This test demonstrates why UnsafeCounter is dangerous
        // Modern JVMs may not always show race condition, but the pattern IS unsafe
        ThreadBasicsDemo.UnsafeCounter counter = new ThreadBasicsDemo.UnsafeCounter();
        
        // Sequential test - should work
        counter.increment();
        counter.increment();
        counter.increment();
        assertEquals(3, counter.getValue(), "Sequential access should work");
        
        // Note: Without synchronization, concurrent access is unpredictable
        // The fact that it sometimes works doesn't make it thread-safe!
        // This is why we need proper synchronization
    }
    
    // ==================== ATOMIC OPERATIONS TESTS ====================
    
    @Test
    public void testAtomicCounter_ThreadSafe() throws InterruptedException {
        ThreadBasicsDemo.AtomicCounter counter = new ThreadBasicsDemo.AtomicCounter();
        int threadCount = 10;
        int incrementsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        assertEquals(threadCount * incrementsPerThread, counter.getValue());
    }
    
    @Test
    public void testAtomicCounter_VaryingThreadCounts() throws InterruptedException {
        // Test with 1 thread
        testAtomicCounterWithThreadCount(1);
        // Test with 5 threads
        testAtomicCounterWithThreadCount(5);
        // Test with 10 threads
        testAtomicCounterWithThreadCount(10);
        // Test with 20 threads
        testAtomicCounterWithThreadCount(20);
    }
    
    private void testAtomicCounterWithThreadCount(int threadCount) throws InterruptedException {
        ThreadBasicsDemo.AtomicCounter counter = new ThreadBasicsDemo.AtomicCounter();
        int incrementsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        assertEquals(threadCount * incrementsPerThread, counter.getValue(),
                     "Atomic counter should be accurate with " + threadCount + " threads");
    }
    
    @Test
    public void testAtomicCounter_Add() throws InterruptedException {
        ThreadBasicsDemo.AtomicCounter counter = new ThreadBasicsDemo.AtomicCounter();
        
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> counter.add(10));
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        assertEquals(50, counter.getValue());
    }
    
    // ==================== LOCK TESTS ====================
    
    @Test
    public void testReentrantLockCounter_ThreadSafe() throws InterruptedException {
        ThreadBasicsDemo.ReentrantLockCounter counter = new ThreadBasicsDemo.ReentrantLockCounter();
        int threadCount = 10;
        int incrementsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        assertEquals(threadCount * incrementsPerThread, counter.getValue());
    }
    
    @Test
    public void testReentrantLockCounter_TryLock() throws InterruptedException {
        ThreadBasicsDemo.ReentrantLockCounter counter = new ThreadBasicsDemo.ReentrantLockCounter();
        
        boolean acquired = counter.tryIncrementWithTimeout();
        assertTrue(acquired, "Should acquire lock when available");
        assertEquals(1, counter.getValue());
    }
    
    @Test
    public void testReadWriteLockCounter_ThreadSafe() throws InterruptedException {
        ThreadBasicsDemo.ReadWriteCounter counter = new ThreadBasicsDemo.ReadWriteCounter();
        int threadCount = 10;
        int incrementsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        assertEquals(threadCount * incrementsPerThread, counter.getValue());
    }
    
    @Test
    public void testReadWriteLockCounter_MultipleReaders() throws InterruptedException {
        ThreadBasicsDemo.ReadWriteCounter counter = new ThreadBasicsDemo.ReadWriteCounter();
        
        // Set initial value
        counter.increment();
        counter.increment();
        counter.increment();
        
        // Multiple threads reading
        ExecutorService executor = Executors.newFixedThreadPool(10);
        AtomicInteger readCount = new AtomicInteger(0);
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                if (counter.getValue() == 3) {
                    readCount.incrementAndGet();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        assertEquals(10, readCount.get(), "All readers should see same value");
    }
    
    // ==================== THREAD COMMUNICATION TESTS ====================
    
    @Test
    public void testProducerConsumer_SyncedValues() throws InterruptedException {
        ThreadBasicsDemo.ProducerConsumer pc = new ThreadBasicsDemo.ProducerConsumer();
        
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    pc.produce(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    int value = pc.consume();
                    assertEquals(i, value);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }
    
    // ==================== VOLATILE TESTS ====================
    
    @Test
    public void testVolatileFlag_Visibility() throws InterruptedException {
        ThreadBasicsDemo.VolatileFlag flag = new ThreadBasicsDemo.VolatileFlag();
        
        Thread t = new Thread(() -> {
            flag.setFlag(true);
        });
        
        t.start();
        t.join();
        
        assertTrue(flag.getFlag());
    }
    
    // ==================== THREAD-LOCAL TESTS ====================
    
    @Test
    public void testThreadLocal_IsolatedValues() throws InterruptedException {
        ThreadBasicsDemo.ThreadLocalExample threadLocal = new ThreadBasicsDemo.ThreadLocalExample();
        
        AtomicInteger value1 = new AtomicInteger();
        AtomicInteger value2 = new AtomicInteger();
        
        Thread t1 = new Thread(() -> {
            threadLocal.setValue(100);
            value1.set(threadLocal.getValue());
            threadLocal.cleanup();
        });
        
        Thread t2 = new Thread(() -> {
            threadLocal.setValue(200);
            value2.set(threadLocal.getValue());
            threadLocal.cleanup();
        });
        
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        
        assertEquals(100, value1.get());
        assertEquals(200, value2.get());
    }
    
    // ==================== THREAD POOL TESTS ====================
    
    @Test
    public void testThreadPool_ExecutesAllTasks() throws InterruptedException {
        ThreadBasicsDemo.ThreadPoolExample pool = new ThreadBasicsDemo.ThreadPoolExample(4);
        AtomicInteger counter = new AtomicInteger(0);
        
        for (int i = 0; i < 20; i++) {
            pool.submitTask(() -> {
                counter.incrementAndGet();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        pool.shutdown();
        
        assertEquals(20, counter.get());
    }
    
    @Test
    public void testFuture_ReturnsValue() throws ExecutionException, InterruptedException, TimeoutException {
        ThreadBasicsDemo.FutureExample future = new ThreadBasicsDemo.FutureExample();
        
        future.getFutureResult();
        future.shutdown();
    }
    
    // ==================== CONCURRENCY STRESS TESTS ====================
    
    @Test
    public void testConcurrentModification_StressSynchronized() throws InterruptedException {
        ThreadBasicsDemo.SynchronizedCounter counter = new ThreadBasicsDemo.SynchronizedCounter();
        int expectedValue = 10000;
        
        ExecutorService executor = Executors.newFixedThreadPool(20);
        for (int i = 0; i < expectedValue; i++) {
            executor.submit(counter::increment);
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        assertEquals(expectedValue, counter.getValue());
    }
    
    @Test
    public void testConcurrentModification_StressAtomic() throws InterruptedException {
        ThreadBasicsDemo.AtomicCounter counter = new ThreadBasicsDemo.AtomicCounter();
        int expectedValue = 10000;
        
        ExecutorService executor = Executors.newFixedThreadPool(20);
        for (int i = 0; i < expectedValue; i++) {
            executor.submit(counter::increment);
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        assertEquals(expectedValue, counter.getValue());
    }
}
