# Solutions: JVM Tuning & Optimization

## HeapSizingDemo.java
Uses `Runtime.getRuntime()` memory methods to report max, total, and free heap. Allocates from 10 to 500 MB, measuring allocation time. The ratio of allocated size to max heap determines GC pressure. With `-Xmx512m`, a 200 MB allocation is significant; with `-Xmx4g`, it's negligible. Run with different `-Xmx` values to see the impact on allocation throughput.

## CodeCacheDemo.java
Generates 1000 `Task` implementations via `Proxy.newProxyInstance`, each with a unique `multiplier` field. The proxy `invoke` handler closes over the multiplier, making each Task instance unique from the JIT's perspective. All 1000 methods are called 10,000 times, generating significant compiled code. With `-XX:+PrintCodeCache`, you can observe the code cache filling and potentially flushing.

## MetaspaceDemo.java
Uses `JavaCompiler` API to compile 500 synthetic classes (`Generated_0` through `Generated_499`). Each class has a single `value()` method returning its index. `MemoryPoolMXBean` tracks metaspace usage before and after. Metaspace stores class metadata (not instances), so the growth is proportional to the number of unique classes loaded. With `-XX:MaxMetaspaceSize=64m`, the tight limit forces more frequent class unloading.

## StringDedupDemo.java
Creates 100,000 identical `new String("This is a repeated string...")` objects. Each call to `new String` creates a distinct heap object (even with identical content). Memory usage is `100,000 × (string header + char array + hash)`. With `String.intern()`, all references point to the same interned instance, reducing memory to ~1/100,000 of the original (plus the single char array).

## JvmFlagReporter.java
Reads `RuntimeMXBean.getInputArguments()` for JVM flags, `MemoryMXBean` for heap/non-heap usage, and `OperatingSystemMXBean` for OS info. System properties provide build-specific details. This tool is useful for verifying that intended tuning flags are actually active in the current JVM process.
