# Profiler Internals

## 🔬 How Profilers Gather Data
There are two primary ways a profiler can see what the JVM is doing:

### 1. Instrumentation (Tracing)
The profiler modifies the bytecode of every method in your application, inserting "start" and "stop" timers at the beginning and end.
- **Pros**: Perfectly accurate call counts.
- **Cons**: Massive overhead (can slow down the app by 10x). It changes the timing of the application so much that the data might be misleading (the "Observer Effect").

### 2. Sampling (The Modern Standard)
The profiler "wakes up" every few milliseconds (e.g., every 10ms) and takes a snapshot of the call stack for all active threads.
- **Pros**: Very low overhead (typically < 2%). Can be run in production.
- **Cons**: Might miss very short-lived methods that run between samples.

## 🛩️ Java Flight Recorder (JFR)
JFR is a high-performance data collection framework built directly into the JVM. It is the gold standard for profiling.
- **Low Overhead**: Because it is built into the JVM, it can access internal data structures that external profilers cannot.
- **Event-Based**: It records "Events" (e.g., GC start, Thread sleep, Class load).
- **Always-On**: In many high-performance systems, JFR is left running 24/7 in production with a circular buffer. If a crash occurs, the last 5 minutes of data are saved for "post-mortem" analysis.

## 🛑 The Safepoint Bias Problem
Many old sampling profilers only take a sample when the JVM reaches a **Safepoint** (a point where all threads are paused for GC or maintenance).
- **The Bias**: Since some methods are "safepoint-friendly" and others are not, the profiler might report that the app is spending 90% of its time in a method that is actually very fast, just because that's where the JVM happened to pause.
- **The Fix**: Use **Async-Profiler** or JFR, which use non-safepoint-biased sampling techniques (like `AsyncGetCallTrace`).