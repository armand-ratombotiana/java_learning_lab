# Interview Preparation: Concurrent Design Patterns

This document covers advanced questions related to thread coordination, architectural patterns, and backpressure.

## Q1: Why is a Bounded Buffer critical in the Producer-Consumer pattern?
**Answer:**
A bounded buffer (a queue with a fixed maximum capacity) provides **Backpressure**.
If you use an unbounded buffer, a fast Producer will generate data much faster than a slow Consumer can process it. The queue will grow infinitely until the application consumes all available heap memory and crashes with an `OutOfMemoryError`.
A bounded buffer forces the Producer's `put()` operation to block when the buffer is full. This naturally slows down the Producer to match the speed of the Consumer, keeping the system stable under heavy load.

## Q2: Explain the "Poison Pill" pattern and why it is better than calling `Thread.interrupt()`.
**Answer:**
The Poison Pill is a special, unique object placed onto a work queue by a Producer. When a Consumer pulls the Poison Pill off the queue, it recognizes the signal, finishes any remaining cleanup work, and gracefully terminates its own thread.
Calling `Thread.interrupt()` is dangerous because it can happen at any moment. If the Consumer is in the middle of writing a file or updating a database, the interrupt might leave the data corrupted or incomplete. The Poison Pill ensures that the Consumer finishes all previously queued valid work before shutting down.

## Q3: What is the Actor Model, and how does it prevent race conditions?
**Answer:**
The Actor Model is a concurrency pattern that eliminates shared mutable state.
An "Actor" is an object that encapsulates its own state and a "mailbox" (a queue). Threads do not call methods on the Actor directly; instead, they send asynchronous messages to the Actor's mailbox.
A single, dedicated thread processes the messages in the mailbox one by one. Because the Actor's internal state is only ever read or modified by this single thread, race conditions are mathematically impossible, and no explicit locks (`synchronized`) are required.

## Q4: Describe the "Balking" pattern and give a real-world example.
**Answer:**
The Balking pattern is used when an object receives a request to execute an action, but the object is in an inappropriate state to execute it. Instead of waiting for the state to change (like a guarded suspension), the method simply returns immediately (balks) without doing anything.
**Example**: A background auto-save thread calls `document.save()`. If the document has a boolean flag `isDirty == false` (meaning no changes have been made since the last save), the `save()` method balks and returns immediately, saving unnecessary disk I/O.

## Q5: What is "Writer Starvation" in the Reader-Writer pattern, and how can it be mitigated?
**Answer:**
In a `ReentrantReadWriteLock`, multiple threads can hold the Read lock simultaneously. If a Writer thread requests the Write lock, it must wait for all Readers to finish.
"Writer Starvation" occurs in heavily read-dominated systems. If there is a constant, overlapping stream of Readers, the Read lock is never fully released, and the Writer waits forever.
**Mitigation**: Initialize the `ReentrantReadWriteLock` with the `fairness` parameter set to `true`. This forces the lock to grant access in FIFO order. If a Writer is waiting in the queue, newly arriving Readers will be blocked until the Writer gets its turn. Alternatively, use `StampedLock` for optimistic reading.