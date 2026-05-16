# Performance Flashcards

## Card 1: JVM
- **Q:** What is JVM?
- **A:** Java Virtual Machine - executes Java bytecode

---

## Card 2: Heap
- **Q:** What is the JVM heap?
- **A:** Memory area for objects, divided into Young and Old generations

---

## Card 3: -Xmx
- **Q:** What does -Xmx set?
- **A:** Maximum heap size (e.g., -Xmx2g = 2GB max heap)

---

## Card 4: GC
- **Q:** What does GC do?
- **A:** Garbage Collector - reclaims memory from unused objects

---

## Card 5: G1GC
- **Q:** What is G1GC?
- **A:** Garbage First - modern default GC, region-based

---

## Card 6: Minor GC
- **Q:** What is Minor GC?
- **A:** Collection of Young generation (short-lived objects)

---

## Card 7: Major GC
- **Q:** What is Major GC?
- **A:** Collection of Old generation or full heap

---

## Card 8: JIT
- **Q:** What is JIT compilation?
- **A:** Just-In-Time compiles bytecode to native code for performance

---

## Card 9: Metaspace
- **Q:** What is Metaspace?
- **A:** Stores class metadata (replaced PermGen in Java 8+)

---

## Card 10: JMH
- **Q:** What is JMH?
- **A:** Java Microbenchmark Harness - for measuring code performance

---

## Card 11: Profiler
- **Q:** What is a profiler?
- **A:** Tool that measures performance (CPU, memory) of application

---

## Card 12: Heap Dump
- **Q:** What is a heap dump?
- **A:** Snapshot of heap memory at a point in time

---

## Card 13: jmap
- **Q:** What does jmap do?
- **A:** Prints heap/memory information, creates heap dumps

---

## Card 14: jstack
- **Q:** What does jstack do?
- **A:** Prints thread dumps (stack traces of all threads)

---

## Card 15: jstat
- **Q:** What does jstat do?
- **A:** Prints GC and class loading statistics

---

## Card 16: Memory Leak
- **Q:** What is a memory leak?
- **A:** Objects held in memory that should be garbage collected

---

## Card 17: String Deduplication
- **Q:** What is String deduplication?
- **A:** JVM feature to reduce memory for identical strings

---

## Card 18: ZGC
- **Q:** What is ZGC?
- **A:** Z Garbage Collector - ultra-low latency, scalable

---

## Card 19: Shenandoah
- **Q:** What is Shenandoah?
- **A:** GC with low pause times, concurrent with application

---

## Card 20: Reference Types
- **Q:** What are weak references?
- **A:** Allow GC to collect objects, used for caches

---

## Quick Reference

| Flag/Option | Purpose |
|-------------|----------|
| -Xms | Initial heap size |
| -Xmx | Maximum heap size |
| -Xss | Stack size |
| -XX:+UseG1GC | Use G1 GC |
| -XX:+UseZGC | Use ZGC |
| -XX:MaxGCPauseMillis | Target pause time |
| -XX:+PrintGC | Print GC info |
| -Xlog:gc* | GC logging |
| jmap -heap | Heap info |
| jstat -gc | GC stats |
| jstack | Thread dump |
| jcmd | Diagnostic commands |