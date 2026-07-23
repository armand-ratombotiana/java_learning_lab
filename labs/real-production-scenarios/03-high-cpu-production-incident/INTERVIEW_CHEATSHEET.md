# Interview Cheatsheet: High CPU Incident

## Key Diagnostic Commands
- `top -H -p <pid>` — per-thread CPU in Java process
- `prstat -L -p <pid>` (Solaris) or `ps -mp <pid> -o THREAD` (Linux)
- `jstack <pid>` — correlate high-CPU thread IDs to code
- `jcmd <pid> Thread.print` — lighter than jstack
- `perf top -p <pid>` — native CPU profiling
- Async-profiler: `profiler.sh -d 30 -f profile.svg <pid>`
- JMC Flight Recorder: CPU profiling sample

## Common Metrics to Check
- CPU utilization by process/thread
- GC CPU time (% of total CPU)
- Context switch rate
- Thread count
- System load average

## Typical Root Causes
- Regex catastrophic backtracking (ReDoS)
- Infinite loop or runaway recursion
- Excessive GC (GC overhead > 95%)
- Spin loop waiting for condition
- Tight polling loop
- CPU-intensive computation without optimization
- JIT decompilation (rare)

## Interview Question Patterns
- "A service uses 400% CPU but request rate is normal — what's happening?"
- "How do you find which Java method is consuming CPU in production?"
- "Diagnose a regex ReDoS attack"
- "Design a safe regex validation system"

## STAR Story Template
**S**: Search service CPU spiked to 800%, response times went to 60s
**T**: Find and mitigate the CPU spike
**A**: Used top -H to find threads, jstack to identify regex pattern, fixed with backtracking-safe regex
**R**: CPU dropped to 80%, latency back to 50ms, added regex timeout library
