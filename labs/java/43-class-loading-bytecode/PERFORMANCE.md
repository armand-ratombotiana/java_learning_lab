# Performance of Class Loading

## Class Loading Cost
Loading a class involves:
- File I/O (or JAR entry lookup): ~50-500 μs (SSD)
- Byte array allocation: ~10-100 ns per KB
- Class verification: ~10-200 μs per class
- Linking: ~1-10 μs per class

Total: ~100-1000 μs per class for first load. Cached classes (once loaded) have zero load cost.

## JAR Loading Performance
- Opening a JAR file: ~1-10 ms (compressed index lookup)
- Reading an entry: ~100-500 μs (decompression + read)
- Package scanning: ~1-10 ms per JAR

Large JARs with many classes (Spring Boot uber-JARs) can take seconds to scan.

## CDS (Class Data Sharing)
CDS archives classes in a shared memory-mapped file:
- Startup improvement: 20-40% faster
- Memory reduction: share class metadata across JVM processes
- Archive creation: ~1-5 seconds
- Supported for Application Classes (JDK 14+)

## invokedynamic Performance
- First invocation: bootstrapping overhead (~1-10 μs)
- Subsequent invocations: as fast as virtual dispatch (~1-5 ns)
- JIT-compiled: can be optimized to direct call (like invokestatic)

## ASM Transformation Overhead
- ClassReader.parse(): ~10-50 μs per class (bytecode parsing)
- ClassVisitor.transform(): varies by transformation complexity
- ClassWriter.toByteArray(): ~10-50 μs per class
- Total transformation: ~50-200 μs per class

## Class Unloading Performance
When a ClassLoader becomes unreachable:
- GC identifies unreachable ClassLoader: during concurrent or STW phase
- Class metadata cleanup: ~100-500 μs per 100 classes
- Full Metaspace reclamation: unpredictable (depends on fragmentation)

## Optimization Tips
- Use CDS for startup-critical applications
- Limit class path size (fewer JARs = less scanning)
- Use `-XX:+AlwaysPreTouch` for predictable class loading
- Profile with `-XX:+TraceClassLoading` to identify bottlenecks
- Consider AppCDS for large Spring Boot applications
