# Mock Interview Transcript: Off-Heap Memory

## Interviewer: Staff Engineer, Apple
## Candidate: Senior Java developer
## Time: 35 minutes
## Focus: DirectByteBuffer, unsafe, memory-mapped files, leak detection

---

**Q1: Compare heap memory vs off-heap memory in Java.**

**Candidate**: Heap memory: managed by GC, allocated via `new`, subject to GC pauses, limit is `-Xmx`. Off-heap memory: allocated natively (outside the heap), not GC'd, limited by system memory (RAM + swap). Benefits of off-heap: (1) No GC overhead — large caches don't cause GC pauses. (2) Efficient I/O — zero-copy transfers to network/disk. (3) Shared memory — multiple JVMs can share via memory-mapped files. (4) Larger than heap limit. Drawbacks: (1) Manual management (must free). (2) Allocation/deallocation is expensive. (3) Serialization/deserialization needed for complex objects.

**Interviewer**: How does `DirectByteBuffer` allocate memory?

**Candidate**: `ByteBuffer.allocateDirect(capacity)` calls `DirectByteBuffer(capacity)`, which calls `Unsafe.allocateMemory(size)`. This makes a system call (`malloc` on Linux, `VirtualAlloc` on Windows). The allocated memory is outside the Java heap. `DirectByteBuffer` holds a reference to a `Cleaner` object, which calls `Unsafe.freeMemory()` when the DirectByteBuffer is GC'd. The `-XX:MaxDirectMemorySize` flag limits total direct memory (default = `-Xmx`).

**Interviewer**: What's the memory leak risk with DirectByteBuffer?

**Candidate**: If you allocate DirectByteBuffers faster than they're GC'd (combined with Cleaner execution), you can run out of off-heap memory. The Cleaner runs on GC, which may not keep up with allocation rate. The JVM flag `-XX:MaxDirectMemorySize` causes an OOM-like error when exceeded. Best practices: (1) Pool DirectByteBuffers (reuse). (2) Track allocation with `-XX:NativeMemoryTracking=summary`. (3) Use Netty's `PooledByteBufAllocator` — it pools direct buffers efficiently.

**Interviewer**: How does `sun.misc.Unsafe` relate to off-heap memory?

**Candidate**: `Unsafe` provides low-level memory operations: `allocateMemory()`, `freeMemory()`, `putLong()`, `getLong()`, `setMemory()`, `copyMemory()`. These allow reading/writing arbitrary memory addresses. `Unsafe` is used internally by `DirectByteBuffer`, `RandomAccessFile`, and many frameworks (Netty, Hazelcast, Ignite). It's dangerous — a wrong address causes a JVM crash (SIGSEGV). Java 9+ restricts access via module system.

**Interviewer**: Write a simple off-heap long array using Unsafe.

**Candidate**: 
```java
class OffHeapLongArray implements AutoCloseable {
    private static final Unsafe UNSAFE;
    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    
    private final long address;
    private final int size;
    
    OffHeapLongArray(int size) {
        this.size = size;
        this.address = UNSAFE.allocateMemory((long) size * Long.BYTES);
        UNSAFE.setMemory(address, (long) size * Long.BYTES, (byte) 0);
    }
    
    void set(int index, long value) {
        UNSAFE.putLong(address + (long) index * Long.BYTES, value);
    }
    
    long get(int index) {
        return UNSAFE.getLong(address + (long) index * Long.BYTES);
    }
    
    @Override
    public void close() { UNSAFE.freeMemory(address); }
}
```

**Interviewer**: How do memory-mapped files work with `MappedByteBuffer`?

**Candidate**: `FileChannel.map(MapMode.READ_WRITE, position, size)` creates a `MappedByteBuffer` backed by a memory-mapped file. The OS manages paging: reads/writes go to virtual memory pages, which are synced to disk. Benefits: (1) No explicit read/write — just access memory. (2) Shared between processes. (3) Huge file handling without loading entirely into heap. (4) OS handles cache coherency. Drawbacks: (1) Can't unmap explicitly before GC (Cleaner). (2) SIGBUS if file shrinks while mapped. (3) 32-bit address space limit.

**Interviewer**: When would you use off-heap for caching vs using the heap?

**Candidate**: Off-heap caching (e.g., Hazelcast, Ignite, MapDB): (1) Cache is very large (>~1/3 of heap). (2) Cache entry lifetime is longer than GC cycle (promoted to old gen). (3) You need to share the cache between JVMs. (4) GC pauses from cache size are unacceptable. Heap caching (Caffeine, Guava): (1) Smaller caches (<1GB). (2) Cache entries have similar lifetime to other objects. (3) You want automatic memory management. (4) Simpler code.

**Interviewer**: Final: How do you detect off-heap memory leaks?

**Candidate**: (1) `-XX:NativeMemoryTracking=summary` — JVM-level tracking. (2) `jcmd <pid> VM.native_memory summary` — shows allocation by category (Java heap, thread, code cache, GC, compiler, internal, direct buffer, mapped). (3) `jcmd <pid> VM.native_memory detail` — per-caller site. (4) `pmap -x <pid>` — OS-level memory map. (5) Enable `-XX:MaxDirectMemorySize` — causes OOM-like error on leak. (6) Use Netty's `LeakDetector` for pooled buffer tracking. (7) `gperftools` heap profiler for native allocations.

---

## Feedback

**Strengths**:
- Complete heap vs off-heap comparison
- DirectByteBuffer and Cleaner mechanism
- Unsafe usage with proper cleanup
- Memory-mapped file knowledge
- Cache strategy decision framework

**Areas for Improvement**:
- Could discuss `Foreign Memory API` (JEP 442, finalized in 22)
- Mention `-XX:+DisableExplicitGC` interaction with DirectByteBuffer

**Score**: 4.5/5 — Expert off-heap knowledge
