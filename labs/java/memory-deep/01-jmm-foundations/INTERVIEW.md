# Interview Questions: JMM Foundations

## Company-Specific Focus

### Google
- Java Memory Model (JSR 133): formal specification of thread safety
- Happens-before: partial ordering of actions in a multithreaded program
- Causality: formal model for determining valid executions

### Microsoft
- JMM vs .NET memory model: release/acquire vs full fences
- Sequential consistency: SC for DRF programs

### Amazon
- Happens-before rules: program order, monitor lock, volatile, thread start/join, transitive
- Data race: conflicting accesses without happens-before ordering

### Meta
- Atomicity: which operations are inherently atomic (int, long with volatile)
- Visibility: when writes become visible to other threads

### Apple
- ARM vs x86: weaker memory ordering on ARM
- Memory barriers: what barriers are needed on different architectures

### Oracle
- JLS 17.4: complete memory model specification
- JSR 133: motivation and design
- Happens-before: definition and formal properties

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — JMM is a theoretical foundation) |

## Real Production Scenarios
- **LinkedIn**: Data race in a counter caused incorrect metrics - fixed with AtomicLong
- **Twitter**: Missing happens-before on a shared flag caused threads to see stale values

## Interview Patterns & Tips
- **Happens-before**: key to reasoning about concurrent programs
- **DRF**: data-race-free programs are sequentially consistent
- **Volatile**: provides happens-before for reads/writes to the same field

## Deep Dive Questions
- **Happens-before**: What are all the sources of happens-before ordering?
- **Sequential consistency**: What does SC mean for DRF programs?
- **Causality**: Why does the JMM need a causality model?
- **Data race**: How does a data race manifest in a running program?
- **DRF guarantee**: What does the guarantee of SC for DRF programs mean?