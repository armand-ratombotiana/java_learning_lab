# Why JVM Tuning Exists

JVM tuning exists because "one size fits all" is false for JVM configuration. A web server serving microsecond-latency requests has different requirements from a batch processing job running for hours. Default JVM parameters are designed for medium-throughput server workloads — they work adequately for many applications but optimally for none.

Heap sizing exists because the optimal heap size depends on the application's live data set and allocation rate. Too small: frequent GC, high overhead. Too large: long GC pauses, wasted memory. The JVM cannot determine the optimal heap size without knowing the workload, so tuning is required.

Code cache sizing exists because JIT-compiled code generation varies by application. A Spring Boot application with thousands of beans generates more compiled code than a simple utility. If the code cache is too small, compilation stops and performance degrades. If too large, memory is wasted.

Metaspace tuning exists because class loading patterns vary dramatically. A web application that dynamically generates classes (JSP, Groovy scripts) needs more Metaspace than a static library. Without limits, Metaspace can grow until it exhausts native memory.

Large pages exist because virtual memory overhead (TLB misses) becomes significant for large heaps. With 4 KB pages, a 16 GB heap requires 4 million TLB entries. With 2 MB huge pages, only 8,000 entries — a 500x reduction. This improves memory access latency for GC-intensive workloads.

NUMA tuning exists because modern servers have multiple memory sockets. Accessing memory on a remote socket is 1.5-2x slower than local socket. NUMA-aware allocation ensures threads allocate memory from their local socket, improving throughput on multi-socket systems.

String dedup exists because strings account for 20-40% of heap memory in many applications. Many strings have identical content (JSON keys, XML tags, database column names). Deduplicating the backing char[] arrays can reduce string memory by 50% with minimal CPU overhead.

These tuning parameters exist to bridge the gap between the JVM's generic defaults and application-specific requirements. Without tuning, most applications run suboptimally.
