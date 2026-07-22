# Interview Questions: Volatile Semantics

## Company-Specific Focus

### Google
- volatile: visibility guarantee — writes to volatile variable are visible to all threads
- volatile prevents instruction reordering: memory barrier insertion
- Does NOT provide atomicity: i++ is not atomic even with volatile

### Microsoft
- volatile in Java vs C#: different semantics — C# volatile implies acquire/release, Java is stricter
- volatile on 64-bit primitives: long/double reads/writes are atomic with volatile

### Amazon
- volatile for flags: running flag, shutdown signal patterns
- volatile reference: visibility of the referenced object's state
- double-checked locking: volatile required for correct lazy initialization

### Meta
- Happens-before: volatile write happens-before subsequent volatile read on same variable
- Memory barrier: store-load barrier on x86 for volatile writes
- Performance: volatile reads are cheaper than volatile writes on x86 (no barrier needed for reads)

### Apple
- ARM memory model: weaker ordering, volatile requires DMB barrier
- VarHandle: finer-grained memory ordering with acquire/release modes

### Oracle
- JLS 17.4: volatile fields and the Java Memory Model
- Atomic access: volatile reads/writes are atomic for all types (including long/double)
- Sequential consistency: volatile provides SC for programs with no data races

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1114 Print in Order | Easy | Google, Amazon, Apple | volatile for ready flag |
| 1115 Print FooBar Alternately | Medium | Amazon, Google | volatile for turn control |

## Real Production Scenarios
- **Twitter**: Missing volatile on a running flag caused threads to continue processing after shutdown signal
- **Amazon**: Double-checked locking without volatile caused intermittent NPE in singleton initialization

## Interview Patterns & Tips
- **Flags and signals**: volatile boolean for shutdown, pause, and completion signals
- **Double-checked locking**: volatile required on the lazy-initialized field
- **Thread.interrupted()**: clears interrupt status, uses volatile internally

## Deep Dive Questions
- **Memory barrier**: What memory barriers does volatile insert on x86 vs ARM?
- **Sequential consistency**: What does it mean for volatile to be sequentially consistent?
- **Reordering restrictions**: What reorderings does volatile prevent?
- **Atomicity**: Why is volatile not atomic for compound operations?
- **VarHandle**: How does VarHandle provide acquire/release semantics?