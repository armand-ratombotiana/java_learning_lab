# Interview Questions: Memory Barriers

## Company-Specific Focus

### Google
- Memory barrier: CPU instruction that enforces ordering constraints
- Barrier types: LoadLoad, StoreStore, LoadStore, StoreLoad
- Volatile barrier: how volatile inserts memory barriers

### Microsoft
- Memory barriers in Java vs .NET: Thread.MemoryBarrier()
- x86 vs ARM: different barrier requirements

### Amazon
- StoreLoad barrier: most expensive, required for volatile writes
- LoadLoad barrier: prevents loads from being reordered with earlier loads

### Meta
- JMM mapping to barriers: how volatile read/write maps to barrier instructions
- Barrier elimination: JIT can remove redundant barriers

### Apple
- ARM barriers: DMB (Data Memory Barrier), DSB (Data Synchronization Barrier)
- x86 barriers: MFENCE, LFENCE, SFENCE

### Oracle
- JMM memory models: mapping to hardware memory barriers
- Volatile barrier insertion: compiler inserts barriers before/after volatile operations
- Final field semantics: barrier in constructor to prevent reordering of final field stores

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — barriers are a hardware/JVM concept) |

## Real Production Scenarios
- **Netflix**: Missing barrier on ARM processor caused stale reads in a concurrent counter
- **LinkedIn**: Overuse of volatile caused unnecessary StoreLoad barriers hurting performance

## Interview Patterns & Tips
- **StoreLoad**: most expensive barrier, needed for volatile writes on x86
- **LoadLoad/LoadStore**: cheaper, part of volatile read
- **Barrier coarsening**: JIT merges adjacent barriers

## Deep Dive Questions
- **Barrier types**: Explain LoadLoad, StoreStore, LoadStore, StoreLoad
- **x86 barriers**: What is the default x86 memory ordering model? (TSO)
- **ARM barriers**: What is the ARM memory model?
- **Volatile barrier**: What barriers does volatile read/write insert on x86?
- **Barrier elimination**: When can the JIT remove memory barriers?