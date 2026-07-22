# Interview Questions: Reordering

## Company-Specific Focus

### Google
- Compiler reordering: compiler optimization that rearranges instructions
- JIT reordering: JIT compiler may reorder instructions for performance
- CPU reordering: out-of-order execution at the processor level

### Microsoft
- Reordering in Java vs C#: similar memory model constraints
- Volatile barrier: prevents reordering of volatile accesses

### Amazon
- As-if-serial semantics: single-threaded programs are never affected by reordering
- Data race hazards: reordering causes visibility issues in unsynchronized programs

### Meta
- Write buffer: CPU write buffer can cause reordering
- StoreLoad reordering: the most common form on x86

### Apple
- ARM reordering: more aggressive reordering than x86
- Memory barrier instructions: DMB, DSB on ARM

### Oracle
- JMM reordering rules: restrictions on which reorderings are allowed
- Volatile rules: volatile prevents certain reorderings
- Final field rules: prevents escape of partially constructed objects

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — reordering is a memory model concept) |

## Real Production Scenarios
- **LinkedIn**: Compiler reordering caused incorrect initialization in a singleton without volatile
- **Amazon**: CPU reordering caused a consumer thread to see partial object state

## Interview Patterns & Tips
- **As-if-serial**: single-threaded behavior preserved
- **Volatile prevents reordering**: volatile read/write acts as a memory barrier
- **Final fields**: JMM guarantees safe initialization of final fields

## Deep Dive Questions
- **Compiler reordering**: How does the compiler reorder instructions?
- **CPU reordering**: What reorderings does x86 allow? What about ARM?
- **Store buffer**: How does the store buffer cause reordering?
- **Memory barrier**: What barriers are needed to prevent reordering?
- **Acquire/Release semantics**: How do acquire/release semantics control reordering?