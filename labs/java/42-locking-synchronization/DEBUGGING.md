# Debugging Locking Issues

## Deadlock Detection
A deadlock occurs when two threads hold locks the other needs. Detect via thread dumps:
- Look for threads in BLOCKED state waiting on locks held by other BLOCKED threads
- `jstack` automatically detects deadlocks and prints "Found one Java-level deadlock"

Prevention strategies:
- Lock ordering: always acquire locks in the same order
- Lock timeout: use `tryLock(timeout)` instead of `lock()`
- Deadlock detection: `ThreadMXBean.findDeadlockedThreads()`

## Livelock Detection
Livelock occurs when threads are not blocked but make no progress (e.g., two threads repeatedly retrying CAS on the same variable). Detect via:
- CPU at 100% but no throughput
- Thread stacks showing the same CAS retry loop repeatedly
- `jstack` shows RUNNABLE threads at the same line

## Contention Analysis
Use `-XX:+PrintSafepointStatistics` and `-XX:+PrintGCApplicationStoppedTime` to measure time spent in contended locks. Use `perf top` (Linux) or `async-profiler` to identify hot locks.

## Thread Dump Analysis
In a thread dump, look for:
- `parking to wait for <...>` (LockSupport.park)
- `waiting for monitor entry` (synchronized BLOCKED)
- `in Object.wait()` (synchronized WAITING)
- `locked <...>` (lock owner)

## Lock Profiling Tools
- `jcmd <pid> Thread.print -l` — thread dump with lock information
- `jconsole` — deadlock detection, thread monitoring
- `Java Flight Recorder (JFR)` — lock contention events
- `async-profiler` — CPU profiling including lock contention

## CAS Debugging
High CAS retry count indicates contention. Measure via:
- `perf stat -e 'lock:cmpxchg'` (Linux) — count CAS instructions
- Custom CAS retry counter in code
- JFR event: `jdk.JavaMonitorEnter` for synchronized contention
