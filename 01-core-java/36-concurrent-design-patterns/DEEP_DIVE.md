# Deep Dive: Concurrent Design Patterns

## 1. The Need for Patterns in Concurrency
Writing multi-threaded code from scratch using primitive locks and threads is notoriously error-prone. Concurrent design patterns provide proven, reusable solutions to common synchronization and coordination problems, separating the business logic from the complex concurrency mechanics.

## 2. Producer-Consumer Pattern
The Producer-Consumer pattern decouples the threads that generate data (Producers) from the threads that process data (Consumers). They communicate via a shared buffer (queue).

### Key Components:
*   **Producer**: Generates work and places it in the buffer.
*   **Consumer**: Takes work from the buffer and processes it.
*   **Buffer**: A thread-safe queue (usually a `BlockingQueue` in Java).

### Why use it?
*   **Asynchrony**: Producers don't have to wait for consumers to finish processing.
*   **Load Leveling**: If producers suddenly generate a massive spike of work, the buffer absorbs the shock, allowing consumers to process it at a steady pace.
*   **Backpressure**: If the buffer is bounded (fixed size), producers are forced to block when the buffer is full, preventing OutOfMemory errors.

## 3. Reader-Writer Pattern
In many systems, data is read far more frequently than it is modified. A standard exclusive lock forces readers to wait for other readers, which is highly inefficient. The Reader-Writer pattern solves this.

### The Rules:
1.  Multiple threads can read the data simultaneously.
2.  Only one thread can write the data at a time.
3.  If a thread is writing, no other thread can read or write.

*   **Java Implementation**: `java.util.concurrent.locks.ReentrantReadWriteLock`.

## 4. Thread Pool (Worker Pool) Pattern
Creating and destroying OS threads is expensive. The Thread Pool pattern maintains a pool of pre-instantiated worker threads that wait for tasks to be assigned to them.

### Why use it?
*   **Resource Management**: Limits the maximum number of concurrent threads, preventing CPU and memory exhaustion.
*   **Performance**: Eliminates the overhead of thread creation for short-lived tasks.
*   **Java Implementation**: `java.util.concurrent.ExecutorService`.

## 5. Active Object (Actor) Pattern
The Actor model is a higher-level concurrency pattern that completely eliminates shared mutable state. Instead of threads sharing memory and using locks to coordinate, Actors encapsulate their own state and communicate exclusively via asynchronous message passing.

### How it works:
1.  An Actor receives a message and places it in its internal "mailbox" (a queue).
2.  A single thread processes the messages in the mailbox sequentially.
3.  Because the Actor's state is only ever modified by its own single thread, no locks are required.

*   **Java Implementation**: Java does not have built-in support for the Actor model. It is typically implemented using third-party frameworks like **Akka**. However, you can conceptually build a simple Active Object using an `ExecutorService` with a single thread.

```java
// Conceptual Active Object
public class ActiveObject {
    private int internalState = 0; // No volatile or locks needed!
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public Future<Integer> processMessageAsync(int value) {
        return executor.submit(() -> {
            // Only this single thread ever touches internalState
            internalState += value;
            return internalState;
        });
    }
}
```

## 6. The Balking Pattern
The Balking pattern is used to prevent an object from executing a certain code if it is in an incomplete or inappropriate state.

### How it works:
If a thread calls a method, but the object's state doesn't allow the method to run, the method immediately returns (balks) without doing anything.

### Use Case:
A background thread periodically saves a document. If the document hasn't been modified since the last save, the `save()` method simply balks (returns immediately) to save CPU and disk I/O.