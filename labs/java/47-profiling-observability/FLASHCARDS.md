# Profiling & Observability Deep Dive — Flashcards

## Card 1
**Front:** What is profiling and observability?
**Back:** A set of Java platform features for async-profiler (CPU, allocation, wall, lock profiling) that provides improved performance, safety, and developer productivity.

## Card 2
**Front:** Key benefit of structured concurrency
**Back:** Task lifetimes are bounded by the code block, ensuring proper cleanup and error propagation.

## Card 3
**Front:** JFR event streaming
**Back:** Programmatic subscription to JFR events using RecordingStream or EventStream APIs.

## Card 4
**Front:** Foreign Memory API
**Back:** Java 21+ API providing safe, managed off-heap memory access through MemorySegment and Arena.

## Card 5
**Front:** JMX MBean
**Back:** Managed Bean that exposes attributes and operations for monitoring and management via JMX.

## Card 6
**Front:** ScopedValue
**Back:** A bounded thread-local variable that is inherited by child tasks within a structured concurrency scope.

## Card 7
**Front:** TLAB
**Back:** Thread-Local Allocation Buffer: each thread gets a dedicated Eden region to allocate without synchronization.

## Card 8
**Front:** False sharing
**Back:** Performance degradation when threads on different cores access variables on the same cache line.

## Card 9
**Front:** Deadlock
**Back:** Two or more threads each holding a lock that the other needs, causing all to block indefinitely.

## Card 10
**Front:** Boxing overhead
**Back:** Performance cost of converting between primitive types and their wrapper objects, causing allocations.