# Interview Questions: Synchronized Internals

## Company-Specific Focus

### Google
- synchronized method vs synchronized block: bytecode differences (ACC_SYNCHRONIZED flag vs monitorenter/monitorexit)
- Object monitor: each object has a monitor associated with it
- Reentrant synchronization: same thread can acquire the same lock multiple times

### Microsoft
- synchronized vs C# lock statement: similar monitor-based approach
- Synchronized and thread safety: guarantees visibility and atomicity

### Amazon
- Synchronized in high-throughput services: avoid long critical sections
- Performance: biased (pre Java 15), lightweight, heavyweight monitor transitions
- Synchronized block size: keep critical sections as small as possible

### Meta
- Biased locking: optimization for single-thread access (removed in Java 15)
- Lock coarsening: JIT merges adjacent synchronized blocks on same object
- Lock elimination: JIT removes unnecessary locks (escape analysis)

### Apple
- Thread safety with synchronized: simplest mechanism
- Performance impact: monitor acquisition overhead

### Oracle
- synchronized JVM specification: monitorenter/monitorexit bytecodes
- Object header mark word: encodes lock state
- Monitor inflation: biased -> lightweight -> heavyweight

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1114 Print in Order | Easy | Google, Amazon | Synchronized + volatile ordering |
| 1115 FooBar Alternately | Medium | Amazon, Google | wait/notify with synchronized |

## Real Production Scenarios
- **LinkedIn**: Large synchronized block in cache caused 200-thread contention bottleneck
- **Amazon**: Nested synchronized blocks caused deadlock — two locks acquired in different order

## Interview Patterns & Tips
- **Monitor**: synchronized uses object's monitor for mutual exclusion
- **Reentrancy**: same thread can re-acquire a lock it already holds
- **Visibility**: synchronized guarantees happens-before between unlock and subsequent lock

## Deep Dive Questions
- **Object header**: How does the mark word encode lock state?
- **Monitor inflation**: How does biased locking transition to lightweight and then to heavyweight?
- **Contention**: What happens when a thread tries to acquire a locked monitor?
- **WaitSet vs EntrySet**: What are these monitor structures?
- **Biased locking revocation**: Why was biased locking disabled in Java 15?