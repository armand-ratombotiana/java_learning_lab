# Module 47: Memory Profiling & Analysis - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-46 (especially JVM Internals and Performance Optimization)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Understanding the JVM Heap](#heap)
2. [Types of Memory Leaks in Java](#leaks)
3. [Generating Heap Dumps](#heapdumps)
4. [Using Eclipse Memory Analyzer (MAT)](#mat)
5. [Java Flight Recorder (JFR) & JDK Mission Control](#jfr)

---

## 1. Understanding the JVM Heap <a name="heap"></a>
The JVM heap is where all objects are allocated. When an object is no longer referenced by a GC Root (e.g., local variables in the thread stack, static variables), it is eligible for Garbage Collection.

---

## 2. Types of Memory Leaks in Java <a name="leaks"></a>
Even with a Garbage Collector, memory leaks can occur in Java. A leak happens when an application holds unintentional strong references to objects that are no longer needed.
- **Static Collections**: A static `HashMap` or `List` that caches objects but never clears them.
- **Unclosed Resources**: Streams, Connections, or `ThreadLocal` variables that are not properly closed or removed.
- **Inner Classes**: Non-static inner classes hold an implicit reference to their outer class. If the inner class instance is cached, the outer class cannot be collected.

---

## 3. Generating Heap Dumps <a name="heapdumps"></a>
A heap dump is a snapshot of the JVM memory at a given moment in time.
- **On OutOfMemoryError**: Automatically generate a dump when the app crashes using the JVM flag `-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/dumps/heapdump.hprof`.
- **On Demand (jmap)**: Use the JDK utility `jmap` to trigger a dump of a running process: `jmap -dump:live,format=b,file=heapdump.hprof <PID>`.

---

## 4. Using Eclipse Memory Analyzer (MAT) <a name="mat"></a>
Eclipse MAT is the industry-standard tool for parsing `.hprof` files.
- **Dominator Tree**: Shows the biggest objects in the heap and the objects they keep alive (retained size).
- **Leak Suspects Report**: MAT analyzes the dump and highlights the most likely causes of the leak, showing exactly which thread or static variable is holding the memory.
- **Shallow vs Retained Size**: 
  - *Shallow Size*: Memory consumed by the object itself (usually small).
  - *Retained Size*: Memory freed if this object is garbage collected (includes all objects it uniquely references).

---

## 5. Java Flight Recorder (JFR) & JDK Mission Control <a name="jfr"></a>
JFR is a profiling and event collection framework built into the JVM. It has incredibly low overhead (< 1%) and can be left running in production.
- **JFR**: Records events like thread blocking, GC pauses, memory allocations, and network I/O.
- **JMC (JDK Mission Control)**: A desktop application used to visualize the `.jfr` recording files.