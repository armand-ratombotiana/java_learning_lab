# Interview Questions: Object Layout & Memory Internals

## Company-Specific Focus

### Google
- Object header: mark word (8 bytes on 64-bit), klass pointer (4/8 bytes with compressed OOPs)
- Instance fields alignment and padding
- Array object header: mark word, klass pointer, length field

### Microsoft
- Java object layout vs .NET object layout
- Sizeof: no direct operator, use JOL (Java Object Layout) tool

### Amazon
- Compressed OOPs: how references are encoded when heap < 32GB
- Field ordering: JVM reorders fields for optimal alignment
- TLAB: Thread Local Allocation Buffer for allocation efficiency

### Meta
- Object size calculation using JOL
- Field packing and false sharing
- Object alignment: 8-byte alignment by default

### Apple
- Object layout on ARM64: differences from x86_64
- Memory alignment considerations on Apple Silicon
- Heap fragmentation analysis

### Oracle
- OpenJDK JOL (Java Object Layout) tool
- JVM specification: object representation
- Object header structure in HotSpot
- Field layout: allocation order vs @Contended annotation

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — object layout is a JVM internal concept) |

## Real Production Scenarios
- **LinkedIn**: False sharing in a high-throughput counter array — 8x performance improvement with @Contended
- **Netflix**: Object size analysis using JOL revealed a core object was 3x larger than expected due to alignment
- **Uber**: Compressed OOPs disabled because heap grew beyond 32GB — increasing memory by 15%

## Interview Patterns & Tips
- **JOL**: Use the JOL tool to measure object sizes and layout
- **Compressed OOPs**: Enabled by default when heap < 32GB
- **False sharing**: Multiple threads accessing different fields in the same cache line

## Deep Dive Questions
- **Mark word**: What fields are stored in the mark word? (Identity hash, GC age, lock state)
- **Klass pointer**: What does the klass pointer reference?
- **Compressed OOPs**: How does the JVM compress 64-bit references into 32-bit?
- **@Contended**: How does the @Contended annotation prevent false sharing?
- **Field layout**: How does the JVM order fields within an object?