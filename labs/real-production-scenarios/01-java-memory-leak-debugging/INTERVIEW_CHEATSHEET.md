# Interview Cheatsheet: Java Memory Leak

## Key Diagnostic Commands
- `jcmd <pid> VM.classloader_stats` — check ClassLoader count
- `jcmd <pid> VM.metaspace` — Metaspace usage
- `jstat -gcutil <pid> 5s` — GC and class unloading
- `jmap -dump:live,format=b,file=heap.hprof <pid>` — heap dump
- `jstack <pid>` — thread dump (look for ThreadLocal references)
- `mat` / Eclipse MAT — analyze heap dump, "Leak Suspects Report"

## Common Metrics to Check
- Metaspace growth rate (MB/hour)
- ClassLoader count over time
- Loaded vs. unloaded class count
- GC pause frequency and duration
- Heap usage after full GC

## Typical Root Causes
- ThreadLocal set without remove() in thread-pooled env
- ClassLoader retained via ThreadLocal reference chain
- Missing finally block in request lifecycle
- Custom ClassLoader not closed/released
- Servlet container retaining TCCL reference

## Interview Question Patterns
- "How does ThreadLocal cause a memory leak in web apps?"
- "How would you diagnose a Metaspace OOM?"
- "Walk through using Eclipse MAT to find a leak suspect"
- "Compare heap dump vs. GC log analysis"
- "How does ScopedValue (JEP 429) solve ThreadLocal leaks?"

## STAR Story Template
**S**: Gateway crashing with OOM every 6 hours during peak traffic
**T**: ID root cause and prevent recurrence
**A**: Added HeapDumpOnOOM, analyzed in MAT, found ThreadLocal→ClassLoader chain, added finally blocks
**R**: Metaspace stabilized, no recurrence in 6 months, added linting rules
