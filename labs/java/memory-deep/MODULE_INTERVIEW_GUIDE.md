# Memory Model Deep Dive — Module Interview Guide

## Company-Specific Questions

### Google
- "Explain the full happens-before relationship. Give a non-trivial example involving volatile + synchronized."
- "What is a memory barrier? Where does the JVM insert barriers on x86 vs ARM?"
- "Explain the final field semantics. How does the JVM guarantee that final fields are seen as constructed?"

### Amazon
- "How does the JMM affect distributed system design? What happens if your cache doesn't use volatile?"
- "You have a cluster of Java services with inconsistent state. Could the JMM be responsible?"
- "Explain the Double-Checked Locking pattern. Why does it need volatile in Java 5+?"

### Meta
- "What reorderings are allowed on x86? On ARM? How does the JVM compensate?"
- "If I have a class with only final fields, is it automatically thread-safe? Why or why not?"
- "Explain the Memory Barrier Soup problem. How does VarHandle help?"

### Apple
- "How does the JMM guarantee visibility on ARM processors? What's the impact on performance?"
- "How would you design a lock-free data structure that works correctly on both x86 and ARM?"

### Oracle
- "Cite specific JLS §17.4 sections. What is a happens-before edge? What is not?"
- "How does the JMM define 'causality'? What are causality test cases?"
- "Explain the IRIW (Independent Read of Independent Write) pattern. Is it correctly observed?"

## LeetCode Problems

| Problem | JMM Concept |
|---------|-------------|
| 1114 Print in Order | Happens-before with CountDownLatch |
| 1195 Fizz Buzz Multithreaded | Volatile visibility + atomicity |
| 1242 Web Crawler Multithreaded | ConcurrentHashMap (JMM guarantees) |
| 1279 Traffic Light Controller | Volatile flag + happens-before |
| 1188 Design Bounded Blocking Queue | Memory model for wait/notify |

## FAANG Interview Stories

**Story 1: Google — Volatile Misunderstanding**
> *"I said 'volatile makes writes visible to all threads immediately.' The interviewer asked: 'Immediately? What does immediately mean? At the CPU cycle level?' I had to explain the MESI protocol and store buffers. Then they asked about the difference between x86's TSO and ARM's relaxed model."* — L5 SWE, Google

**Story 2: Amazon — Final Field Semantics**
> *"We had a bug where a supposedly immutable object was published to another thread, and the other thread saw null for a field. The root cause: the 'this' reference escaped the constructor via a lambda. The fix: don't publish 'this' during construction. This is a classic JMM issue."* — Principal Engineer, Amazon

**Story 3: Apple — ARM JMM Bug**
> *"Migrating a service from x86 to ARM revealed a data race that had been hidden by x86's strong memory model. On x86, loads are not reordered with loads. On ARM, they are. The fix required adding a volatile read where none seemed needed."* — Software Engineer, Apple

## Senior vs Staff Deep Dive

### Senior-Level
- "Explain the volatile read/write semantics with memory barriers. What barriers are needed for each?"
- "Compare happens-before of synchronized vs volatile vs final. How do they interact?"
- "What is the Dekker's algorithm pattern? How does the JMM treat it?"

### Staff-Level
- "Design a concurrent data structure that works correctly across all JVM platforms (x86, ARM, RISC-V)."
- "How does the JVM implement VarHandle.{getAcquire, setRelease} on ARM? What are the assembly instructions?"
- "Explain the 'causality' requirements in JMM. What is a valid execution? What are the legal and illegal outcomes?"
- "How would you write a litmus test to verify JMM compliance in a new JVM port?"

## System Design Connections

| System | JMM Consideration |
|--------|------------------|
| Distributed cache | JMM guarantees for local caches, then consistency protocols for distributed |
| Leader election | Volatile for leader flag, happens-before for log writes |
| Event sourcing | Final semantics for immutable events, volatile for event store pointer |
| Rate limiter | Atomic counters, CAS, memory ordering |
| State machine | Synchronized for state transitions, volatile for state read |

## Code Review Scenarios

**Scenario 1**: Race condition in counter.
```java
// Broken: volatile doesn't provide atomicity
volatile int count;
void increment() { count++; }  // Not atomic!
// Fix: AtomicInteger or LongAdder
```

**Scenario 2**: Unsafe publication.
```java
// Broken: 'holder' might be seen before Holder's constructor completes
class Holder { int value; Holder(int v) { value = v; } }
Holder holder;
void init() { holder = new Holder(42); }
// Fix: volatile Holder holder; or final Holder holder in constructor
```

**Scenario 3**: Double-checked locking without volatile.
```java
// Broken in Java 4 and earlier
private static Singleton instance;
public static Singleton get() {
    if (instance == null) {
        synchronized (Singleton.class) {
            if (instance == null) instance = new Singleton();
        }
    }
    return instance;
}
// JMM allows reordering: write to instance before constructor completes
// Fix: volatile Singleton instance;
```

## Debugging Scenarios

**Scenario 1**: Intermittent data race only reproducible in production.
- Tool: ThreadSanitizer (JVM feature), JFR thread contention events
- Technique: Use `-XX:+AlwaysPreTouch` to remove OS page delays from signal
- Analysis: Add logging around shared state, use happens-before analysis

**Scenario 2**: Performance degradation after ARM migration.
- Cause: ARM has weaker memory ordering — JVM inserts more barriers
- Detect: Compare JIT assembly between x86 and ARM (`-XX:+PrintAssembly`)
- Fix: Use `VarHandle` with minimal necessary barriers

**Scenario 3**: AtomicLong vs LongAdder confusion.
- Profile: async-profiler shows CAS contention in `AtomicLong.incrementAndGet()`
- Fix: Replace with `LongAdder` (striped counter) for high-contention counters
- Trade-off: LongAdder has higher memory usage but much lower contention
