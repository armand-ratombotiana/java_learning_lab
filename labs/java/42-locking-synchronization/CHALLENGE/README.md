# Challenge: Lock-Free MPMC Queue

## Problem
Implement a multi-producer, multi-consumer (MPMC) bounded queue using only CAS operations (no locks, no synchronized blocks). This is the algorithm used by `java.util.concurrent.ConcurrentLinkedQueue` and Disruptor.

## Specifications
- `MpmcBoundedQueue<T>` with fixed capacity (power of 2 for bitwise masking)
- Uses `AtomicReferenceArray` for slots
- Producer claim sequence via `AtomicLong` with CAS
- Consumer claim sequence via separate `AtomicLong`
- Each slot holds a `volatile` reference and a sequence number for coordination

## Core Algorithm (Michael-Scott variant)
```
produce(item):
  loop:
    seq = producerClaim.getAndIncrement()
    slot = seq & mask
    while buffer[slot].seq != seq: spin
    buffer[slot].value = item
    buffer[slot].seq = seq + 1

consume():
  loop:
    seq = consumerClaim.getAndIncrement()
    slot = seq & mask
    while buffer[slot].seq != seq + 1: spin
    item = buffer[slot].value
    buffer[slot].seq = seq + capacity
    return item
```

## Advanced Challenges
1. Support `drainTo(Collection)` for batch consumption
2. Compare throughput against `LinkedBlockingQueue` and `ArrayBlockingQueue` under high contention
3. Handle producer/consumer imbalance (more producers than consumers)
4. Add `remainingCapacity()` without locking

## Edge Cases
- Empty queue: consumer must spin or yield
- Full queue: producer must spin or reject
- Queue at capacity bounds with concurrent producers/consumers
- Memory ordering: proper use of volatile and atomic fences

## Hints
- `VarHandle` (Java 9+) provides fine-grained memory ordering control
- Use `Thread.onSpinWait()` for spinning (Java 9+)
- Power-of-2 capacity enables fast modulus via `& (capacity - 1)`
