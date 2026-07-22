# Interview Questions: Atomic Classes

## Company-Specific Focus

### Google
- AtomicInteger, AtomicLong, AtomicBoolean, AtomicReference: CAS-based atomic operations
- CAS (Compare-And-Swap): hardware-level atomic compare and exchange
- ABA problem: AtomicStampedReference for ABA prevention

### Microsoft
- Java atomic classes vs Interlocked class in .NET
- CAS operations: underlying hardware instructions (CMPXCHG on x86)

### Amazon
- Atomic counters for high-throughput metrics: AtomicLong vs LongAdder
- Non-blocking algorithms: CAS loops without locking

### Meta
- LongAdder vs AtomicLong: LongAdder uses striped counters, better under contention
- AtomicReference: lock-free updates for object references
- AtomicIntegerArray, AtomicLongArray: atomic operations on array elements

### Apple
- AtomicFieldUpdaters: for atomic operations on volatile fields of a class
- VarHandle: Java 9+ replacement for atomic field operations

### Oracle
- java.util.concurrent.atomic package: CAS-based atomic classes
- Unsafe.compareAndSwap methods: underlying implementation
- VarHandle: Java 9+ standard API for atomic field access

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1114 Print in Order | Easy | Google, Amazon, Microsoft | AtomicInteger counter |
| 1115 Print FooBar Alternately | Medium | Amazon, Google | AtomicBoolean for turn |
| 1116 Print Zero Even Odd | Medium | Google, Apple | AtomicInteger sequencing |

## Real Production Scenarios
- **LinkedIn**: AtomicLong for counting service invocations — replaced with LongAdder when contention became an issue
- **Uber**: CAS loop in a custom non-blocking stack — high performance without locking

## Interview Patterns & Tips
- **CAS loop**: while(!atomic.compareAndSet(old, new)) { old = atomic.get(); new = fn(old); }
- **ABA problem**: Use AtomicStampedReference when reference identity matters
- **LongAdder**: Preferred for highly contended counters
- **VarHandle**: Modern API, replaces Unsafe for atomic field operations

## Deep Dive Questions
- **CAS implementation**: How does Compare-And-Swap work on x86 (CMPXCHG)?
- **ABA problem**: What is the ABA problem and how does AtomicStampedReference solve it?
- **LongAdder**: How does LongAdder use striped counters for high throughput?
- **VarHandle**: How does VarHandle improve upon AtomicReferenceFieldUpdater?
- **Performance**: What is the relative cost of CAS vs lock acquisition?