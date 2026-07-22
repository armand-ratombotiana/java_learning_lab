# Interview Questions: GC Log Analysis

## Company-Specific Focus

### Google
- GC logging: -Xlog:gc* (JDK 9+ unified logging)
- Key metrics: pause time, frequency, throughput, allocation rate, promotion rate
- GC log tools: GCeasy, GCViewer, GCMV (Eclipse MAT plugin)

### Microsoft
- GC logs vs .NET GC ETW events
- Unified logging: -Xlog format and tags

### Amazon
- Production GC logging: always enabled for troubleshooting
- GC log rotation: -Xlog:gc*:file=gc.log:time,uptime,level,tags:filecount=10,filesize=10M

### Meta
- Young GC analysis: Eden size, survivor usage, promotion rate
- Full GC analysis: heap usage before/after, compaction duration
- Concurrent GC: GC concurrent cycle details (G1, ZGC)

### Apple
- GC pause analysis: identifying long pauses
- Allocation rate: bytes per second, GC pressure indicator

### Oracle
- Unified GC logging: -Xlog:gc* replaced -XX:+PrintGCDetails in JDK 9+
- Log decorators: time, uptime, level, tags, pid, tid
- GC log tags: gc, age, ergo, heap, metaspace, ref, stringtable, etc.

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — GC log analysis is operational) |

## Real Production Scenarios
- **Netflix**: GC log analysis revealed excessive humongous allocations causing G1 Full GCs
- **Uber**: GC log showed promotion failure due to small survivor space - increased SurvivorRatio

## Interview Patterns & Tips
- **Pause time**: look for STW pause duration
- **Allocation rate**: high allocation rate causes frequent GCs
- **Promotion rate**: high promotion rate may indicate insufficient young gen
- **Concurrent mode failure**: GC couldn't complete before old gen filled

## Deep Dive Questions
- **GC log format**: How to read a GC log line?
- **Metrics**: What are the key metrics to look for in GC logs?
- **G1**: How to interpret G1 concurrent cycle logs?
- **ZGC**: How to interpret ZGC logs (colored pointers, remap)?
- **Promotion failure**: How to diagnose promotion failure in G1?