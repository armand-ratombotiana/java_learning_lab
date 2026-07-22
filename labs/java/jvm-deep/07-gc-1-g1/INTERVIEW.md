# Interview Questions: G1 Garbage Collector

## Company-Specific Focus

### Google
- G1 GC: region-based, generational, incremental, concurrent, parallel
- Heap layout: Eden, Survivor, Old regions (each 1-32MB)
- Mixed GC: collecting both young and old regions

### Microsoft
- G1 GC vs .NET GC: region-based vs segment-based
- Tuning: -XX:MaxGCPauseMillis (default 200ms)

### Amazon
- G1 GC for medium-sized heaps (4-32GB)
- SATB (Snapshot-At-The-Beginning): concurrent marking algorithm
- Remembered Sets: tracking cross-region references

### Meta
- Young GC: STW, Eden -> Survivor/Old promotion
- Concurrent marking: starting with an initial mark, concurrent root region scan
- Evacuation failure: promotion failure triggers Full GC

### Apple
- G1 GC tuning: region size, pause time target, initiating heap occupancy
- Full GC: single-threaded, STW, mark-sweep-compact

### Oracle
- G1 GC: default since JDK 9+
- Garbage-First: collects regions with the most garbage first
- G1 vs G1 (generational): experimental generational mode in JDK 21+
- Tuning flags: -XX:+UseG1GC, -XX:G1HeapRegionSize

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — G1 is a GC implementation) |

## Real Production Scenarios
- **Uber**: G1 GC evacuation failure caused 10s pause — increased heap and adjusted IHOP threshold
- **Netflix**: G1 GC tuning reduced pause times from 200ms to 10ms with -XX:MaxGCPauseMillis=10

## Interview Patterns & Tips
- **Region-based**: heap divided into equal-sized regions
- **G1 cycles**: young GC + concurrent marking + mixed GC
- **SATB**: concurrent marking ensures correctness
- **Humongous**: objects > 50% of region size allocated in humongous regions

## Deep Dive Questions
- **Region layout**: How are regions assigned to Eden/Survivor/Old?
- **SATB**: How does Snapshot-At-The-Beginning work?
- **Remembered sets**: How are cross-region references tracked?
- **Mixed GC**: Which old regions are selected for collection?
- **Humongous allocation**: How are large objects handled?