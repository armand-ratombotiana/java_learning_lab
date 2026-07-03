# Quizzes: Reactive Programming & The Flow API

Test your knowledge of Reactive Principles, Backpressure, and the Java 9 Flow interfaces.

## Quiz 1: Reactive Principles

**Q1: What is the primary problem with the traditional "Thread-per-Request" model in high-throughput applications?**
- A) It is too difficult to write code for.
- B) Threads are expensive (consuming memory and context-switching time). If threads block waiting for I/O (like a database query), thousands of threads sit idle, consuming massive amounts of RAM and degrading server performance.
- C) It does not support database transactions.
- D) It violates the Object-Oriented paradigm.
*Answer: B*

**Q2: According to the Reactive Manifesto, a system that stays responsive in the face of failure is called:**
- A) Elastic
- B) Message Driven
- C) Resilient
- D) Responsive
*Answer: C*

## Quiz 2: The Flow API

**Q1: In the Java 9 Flow API, which interface is responsible for controlling Backpressure?**
- A) `Publisher`
- B) `Subscriber`
- C) `Processor`
- D) `Subscription`
*Answer: D (The Subscriber calls `request(n)` on the Subscription object to signal how much data it can handle).*

**Q2: What is the correct lifecycle order of methods called on a `Subscriber`?**
- A) `onNext()`, `onSubscribe()`, `onComplete()`
- B) `onSubscribe()`, `onNext()` (zero or more times), followed by either `onComplete()` or `onError()`
- C) `request()`, `onNext()`, `onComplete()`
- D) `onSubscribe()`, `onError()`, `onNext()`
*Answer: B*

## Quiz 3: Edge Cases

**Q1: What happens if you make a synchronous, blocking database call inside a Subscriber's `onNext()` method in a fully reactive application?**
- A) The database call automatically becomes asynchronous.
- B) The application crashes with a `CompileTimeError`.
- C) You block one of the few event-loop threads. If you do this enough, all event-loop threads freeze, and the entire application stops responding to new requests.
- D) The Publisher automatically slows down.
*Answer: C*

**Q2: What is the danger of calling `subscription.request(Long.MAX_VALUE)`?**
- A) It causes an integer overflow.
- B) It effectively disables backpressure. The Publisher will push data as fast as possible, potentially overwhelming the Subscriber and causing an `OutOfMemoryError`.
- C) It cancels the subscription.
- D) It throws an `IllegalArgumentException`.
*Answer: B*