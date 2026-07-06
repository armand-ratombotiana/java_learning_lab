# Code Deep Dive: JVM Tuning

## HeapSizingDemo.java Analysis
The `allocate()` method allocates N megabytes using `new byte[1024*1024]` and measures elapsed time. The timing varies based on heap size and GC activity. With small `-Xmx`, the allocation triggers GC; with large `-Xmx`, allocation is fast (no GC pressure). The `Runtime.getRuntime()` methods report current heap state.

The demo demonstrates the relationship between heap size and allocation throughput. A 10 MB allocation on a 512 MB max heap is insignificant; a 500 MB allocation on that same heap triggers GC and potentially OOM.

## CodeCacheDemo.java Analysis
Dynamic proxies generate Method objects that, when invoked, trigger JIT compilation. Each of the 1000 Task implementations has a unique `compute` method body. When all 1000 are exercised 10,000 times, the JIT compiles each one. With default code cache (240 MB), this fills a significant portion of the cache.

The `Proxy.newProxyInstance` call creates a class per invocation (cached by Proxy). The generated class has one method implementation that captures the `multiplier` variable. Each class is distinct from the JIT's perspective, requiring separate compilation.

## MetaspaceDemo.java Analysis
The `JavaCompiler` API compiles source strings into .class files in memory. Each generated class has a unique name (`Generated_0` through `Generated_499`). The `Class.forName()` loads the compiled class, storing its metadata in Metaspace. The `MemoryPoolMXBean` shows the Metaspace usage increase.

The 500 classes consume approximately 1-2 MB of Metaspace. With `-XX:MaxMetaspaceSize=64m`, allocating 500 classes is fine, but increasing to 50,000 would reach the limit.

## StringDedupDemo.java Analysis
Creating 100,000 identical strings with `new String(...)` creates 100,000 distinct objects with identical char[] arrays. Each string object is ~24 bytes (header + hash + reference to char[]), plus the char[] copies: 100,000 × ~80 bytes = ~8 MB. With `intern()`, all references point to the single pooled instance: ~1 string object + 1 char[] = negligible.

## JvmFlagReporter.java Analysis
The `RuntimeMXBean.getInputArguments()` returns all JVM arguments, including -XX flags. This is the most reliable way to verify which flags are active. The `MemoryMXBean` provides heap and non-heap memory usage. The `OperatingSystemMXBean` provides CPU and OS info. System properties provide Java version, architecture, and classpath details.
