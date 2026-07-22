# Interview Questions: Off-Heap Memory & Direct Buffers

## Company-Specific Focus

### Google
- DirectByteBuffer vs HeapByteBuffer: allocation, performance, use cases
- Off-heap memory management: sun.misc.Unsafe, VarHandle
- MappedByteBuffer: memory-mapped files for large data processing

### Microsoft
- Direct memory vs .NET managed/unmanaged memory
- Memory-mapped files in Java vs C# MemoryMappedFile
- Unsafe API and its usage for off-heap memory

### Amazon
- Off-heap caching: using direct memory to reduce GC pressure
- Memory-mapped files for large dataset processing
- Direct buffers for high-performance network I/O
- Netty's Buffer API: PooledByteBufAllocator

### Meta
- GC and off-heap: why off-heap memory does not cause GC pauses
- Unsafe.allocateMemory() and deallocateMemory() patterns
- Off-heap memory leak detection and prevention

### Apple
- Memory-mapped files for resource-constrained devices
- Direct buffer sizing and fragmentation on macOS

### Oracle
- java.nio.Buffer: heap vs direct buffer internals
- Unsafe API: sun.misc.unsafe for off-heap operations
- Direct memory: -XX:MaxDirectMemorySize
- Cleaner API: phantom references for native buffer cleanup

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — off-heap is a memory management technique) |
| 146 LRU Cache | Medium | Amazon, Google | Off-heap cache design |

## Real Production Scenarios
- **Netflix**: Direct memory leak in Netty caused OOM for 32GB direct memory — not bounded by MaxDirectMemorySize
- **Uber**: Memory-mapped file for geospatial index reduced query latency by 80%
- **LinkedIn**: Using Unsafe for off-heap cache avoided GC pauses and tripled throughput

## Interview Patterns & Tips
- **Direct vs Heap**: Direct buffers allocate outside the heap, avoid GC but require manual management
- **MaxDirectMemorySize**: Default equals -Xmx, needs tuning for direct buffer usage
- **Phantom references**: Used by DirectByteBuffer for cleaner to free native memory
- **Unsafe**: Not supported in future JDK versions; use VarHandle or ByteBuffer instead

## Deep Dive Questions
- **DirectByteBuffer**: How does DirectByteBuffer allocate native memory? The cleaner mechanism.
- **MaxDirectMemorySize**: How is direct memory accounted and limited?
- **Unsafe**: How does Unsafe.allocateMemory work?
- **MappedByteBuffer**: How does MappedByteBuffer map files to virtual memory?
- **GC and off-heap**: How does off-heap memory avoid GC but still need native memory management?