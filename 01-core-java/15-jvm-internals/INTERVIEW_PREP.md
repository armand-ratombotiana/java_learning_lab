# Module 15: JVM Internals - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What are the main memory areas of the JVM?
**Answer**:
1. **Heap**: Shared memory area where all objects and arrays are allocated. Managed by the Garbage Collector.
2. **Method Area (Metaspace in Java 8+)**: Shared area that stores class structures (runtime constant pool, field/method data, method code).
3. **Stack**: Thread-local memory area that stores frames (local variables, partial results, method calls/returns).
4. **PC Register**: Thread-local area containing the address of the currently executing JVM instruction.
5. **Native Method Stack**: Thread-local area for C/C++ native method calls (JNI).

### Q2: How does the Garbage Collector work in Java?
**Answer**:
The GC automatically frees memory by identifying objects that are no longer reachable (have no strong references from GC Roots like local variables or active threads).
It generally uses a Generational strategy:
- **Young Generation**: Where new objects are created. Collected frequently (Minor GC). Objects that survive multiple cycles move to the Old Generation.
- **Old (Tenured) Generation**: Stores long-lived objects. Collected less frequently (Major/Full GC).
- **GC Algorithms**: G1 (default in Java 9+), ZGC, Shenandoah.

### Q3: What is the difference between a Memory Leak and a Memory Exhaustion (OOM)?
**Answer**:
- **Memory Leak**: Occurs when the application keeps unintentional strong references to objects that are no longer needed (e.g., adding objects to a static `HashMap` and forgetting to remove them). The GC cannot clean them, slowly draining memory over time.
- **Memory Exhaustion (OOM)**: A sudden failure when the application genuinely needs more memory than the JVM heap allows (e.g., trying to load a 2GB file into a 1GB heap), crashing immediately with `OutOfMemoryError`.

---

## 💻 Whiteboarding Scenarios

### Scenario: Diagnosing a Memory Leak
**Problem**: An application running in production crashes every three days with an `OutOfMemoryError`. How do you troubleshoot and fix it?

**Solution**:
1. **Enable Heap Dumps**: Add `-XX:+HeapDumpOnOutOfMemoryError` to the JVM startup flags so a `.hprof` file is generated when the crash occurs.
2. **Analyze the Dump**: Download the dump file and open it in a tool like Eclipse Memory Analyzer (MAT) or VisualVM.
3. **Find the Leak Suspect**: Look at the "Dominator Tree" or "Leak Suspects" report to find which object holds the most retained heap size.
4. **Trace to GC Roots**: Find the path from the leaking objects back to the GC Roots (often a static collection or a ThreadLocal).
5. **Fix Code**: Modify the Java code to ensure objects are properly removed from caches/collections when their lifecycle ends, or use `WeakHashMap` if appropriate.