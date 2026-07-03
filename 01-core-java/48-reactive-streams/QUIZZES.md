# Module 48: Reactive Streams & Flow API - Quizzes

---

## Q1: The Subscription Interface
In the Java 9 Flow API, what is the primary role of the `Subscription` interface?

A) It handles authentication to a database.
B) It connects the Publisher to the Subscriber and provides the `request(n)` method, acting as the mechanism for backpressure.
C) It transforms data from one type to another.
D) It manages the garbage collection of the stream.

**Answer**: B
**Explanation**: The `Subscription` acts as the control channel. The Subscriber uses it to explicitly pull elements from the Publisher, ensuring the Publisher never overwhelms the Subscriber's memory limits.

---

## Q2: SubmissionPublisher Buffer
What happens by default if a `SubmissionPublisher` emits items much faster than a Subscriber can consume them, and the internal buffer fills up?

A) The publisher immediately throws an `OutOfMemoryError`.
B) The publisher drops the oldest items.
C) The `submit()` method blocks the calling thread until space frees up in the buffer.
D) The publisher creates a new thread for every item.

**Answer**: C
**Explanation**: By default, `SubmissionPublisher.submit()` will block when the subscriber's buffer is full, providing natural backpressure to the producing thread.

---

## Q3: Reactive Processor
If you need to create a class that filters out invalid elements and converts the remaining elements from Strings to Integers, which Flow interface should your class implement?

A) `Flow.Publisher`
B) `Flow.Subscriber`
C) `Flow.Processor`
D) `Flow.Subscription`

**Answer**: C
**Explanation**: A `Processor` acts as both a Subscriber (it receives Strings from an upstream Publisher) and a Publisher (it emits Integers to a downstream Subscriber).