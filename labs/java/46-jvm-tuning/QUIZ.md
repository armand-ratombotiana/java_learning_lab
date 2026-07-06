# Quiz: JVM Tuning

1. What does -Xms specify?
   a) Maximum heap size
   b) Initial heap size
   c) Young generation size
   d) Metaspace size

2. What does -XX:NewRatio=3 mean?
   a) Young generation is 3x older generation
   b) Old generation is 3x younger generation
   c) There are 3 survivor spaces
   d) Eden is 3x survivor space

3. What happens when the code cache fills?
   a) The JVM crashes
   b) JIT compilation stops
   c) Methods are migrated to the interpreter
   d) Both b and c

4. What does Metaspace store?
   a) Java object instances
   b) Class metadata and method bytecode
   c) JIT-compiled code
   d) Thread stacks

5. What is the benefit of large pages?
   a) Faster object allocation
   b) Reduced TLB misses for large heaps
   c) Improved GC throughput
   d) Both b and c

6. What does -XX:+UseNUMA do?
   a) Enables non-uniform memory access awareness
   b) Forces all allocation to a single NUMA node
   c) Disables NUMA optimization
   d) Improves garbage collection on single-socket systems

7. What is the default ReservedCodeCacheSize in Java 21?
   a) 64 MB
   b) 128 MB
   c) 240 MB
   d) 512 MB

8. When does string deduplication occur?
   a) At string creation time
   b) During young GC (for objects past a threshold age)
   c) During full GC only
   d) At application shutdown

## Answer Key
1-b, 2-b, 3-d, 4-b, 5-d, 6-a, 7-c, 8-b
