# Module 47: Memory Profiling & Analysis - Edge Cases & Pitfalls

---

## Pitfall 1: Truncated Heap Dumps

### ❌ Wrong
Generating a heap dump on a massive 32GB production JVM without ensuring there is enough free disk space. The OS kills the `jmap` process, or the file truncates, making it unreadable by MAT.

### ✅ Correct
Always verify disk space before running `jmap`. If space is an issue, consider compressing the dump on the fly or piping it directly via SSH to a remote analysis machine.

---

## Pitfall 2: Analyzing Dumps on Production Servers

### ❌ Wrong
Running Eclipse MAT or `jhat` directly on the production server. These tools require parsing the entire heap structure, which is extremely CPU and memory-intensive, potentially crashing the server or stalling other applications.

### ✅ Correct
Always transfer the `.hprof` file to an isolated local machine or a dedicated analysis server before opening it with Eclipse MAT or VisualVM.

---

## Pitfall 3: Ignoring ThreadLocal Leaks

### ❌ Wrong
Using `ThreadLocal` variables in a web application (which uses thread pools) to store user contexts, but forgetting to clear them. When the HTTP request finishes, the thread returns to the pool, keeping the `ThreadLocal` data alive forever, causing a massive memory leak.

### ✅ Correct
Always use a `finally` block or a Servlet Filter to `remove()` or `clear()` the `ThreadLocal` at the end of the request lifecycle.