# Module 47: Memory Profiling & Analysis - Quizzes

---

## Q1: Heap Dump Analysis
In Eclipse MAT, what is the difference between "Shallow Size" and "Retained Size"?

A) Shallow Size is the size of the object plus all objects it references; Retained Size is the size of the object alone.
B) Shallow Size is the memory consumed by the object itself; Retained Size is the amount of memory that would be freed if this object were garbage collected.
C) Shallow Size is the size of primitive types; Retained Size is the size of complex objects.
D) There is no difference.

**Answer**: B
**Explanation**: Shallow size is usually small (just the headers and primitive fields of the object). Retained size includes the shallow size of the object plus the shallow sizes of all objects that are accessible *only* through this object.

---

## Q2: Generating Heap Dumps
Which JVM flag should be used to guarantee that a heap dump is captured automatically if the application crashes due to memory exhaustion?

A) `-XX:+HeapDumpOnOutOfMemoryError`
B) `-XX:+GenerateHeapDump`
C) `-Xdump:heap`
D) `-XX:+LogOOM`

**Answer**: A
**Explanation**: `-XX:+HeapDumpOnOutOfMemoryError` is a critical production flag. It ensures that when an OOM occurs, the JVM freezes and writes the exact state of memory to disk for later forensic analysis before it terminates.

---

## Q3: Production Profiling
Why is Java Flight Recorder (JFR) generally considered safe to run continuously in a production environment?

A) It only profiles the CPU, never memory.
B) It has extremely low performance overhead (typically < 1%) because it is built deeply into the JVM itself rather than relying on external agent instrumentation.
C) It deletes all logs every minute to save disk space.
D) It pauses the application while recording.

**Answer**: B
**Explanation**: Unlike external profilers that inject bytecode and cause significant latency, JFR hooks directly into the JVM internals, allowing for continuous, low-overhead profiling and event recording.