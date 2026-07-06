# Flashcards: Garbage Collection

**Q: What is the generational hypothesis?**
A: Most objects die young, and those that survive tend to persist.

**Q: List the five main GC collectors in Java.**
A: Serial (single-thread STW), Parallel (multi-thread STW), G1 (regional concurrent), ZGC (colored pointers), Shenandoah (Brooks pointers).

**Q: What is a G1 remembered set?**
A: A per-region data structure tracking references from other regions into this region, enabling incremental collection.

**Q: What is the SATB algorithm?**
A: Snapshot-At-The-Beginning: takes a snapshot of live objects before concurrent marking, treating all snapshot-reachable objects as live.

**Q: What are ZGC's colored pointers?**
A: 4 bits stolen from the 64-bit address storing GC metadata (Marked0, Marked1, Remapped, Finalizable), enabling concurrent relocation without STW.

**Q: What is a TLAB?**
A: Thread-Local Allocation Buffer — thread-private heap region for bump allocation without synchronization.

**Q: What is root scanning in GC?**
A: Finding all live objects by tracing from GC roots (stack, static, JNI references).

**Q: What is the advantage of generational ZGC (Java 21+)?**
A: Adds a young generation to ZGC, improving throughput while maintaining sub-millisecond pause times.
