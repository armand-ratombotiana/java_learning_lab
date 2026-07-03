# Common Mistakes with JVM Internals

## Mistake 1: Ignoring Object Header Size
Every object has 12-16 bytes of header. Thousands of small objects (like Integer or Point) waste enormous memory.

## Mistake 2: Thinking GC Collects Null References
GC ignores null references. Setting variables to null doesn't help GC — scope-based optimization is handled by the JIT.

## Mistake 3: System.gc() Calls
System.gc() triggers a full GC, which pauses all threads. The JVM can ignore it (-XX:+DisableExplicitGC), but it's still best avoided.

## Mistake 4: Assuming Finalizers Run
finalize() is deprecated and may never run. Use try-with-resources or Cleaner instead.

## Mistake 5: Misunderstanding the Memory Model
Without proper happens-before (volatile, synchronized, locks), threads see stale data. The CPU and compiler can reorder instructions without synchronization.

## Mistake 6: Overriding hashCode() with Random
If hashCode() returns different values each call, objects cannot be found in HashMap. The identity hashcode is stored in the mark word after first call, but custom hashCode is computed every time.

## Mistake 7: Not Tuning for Application
Default JVM settings are for general-purpose use. Production applications should tune heap sizes, GC algorithm, thread stack sizes, and metaspace.

## Mistake 8: Ignoring Compressed OOPs
Heaps up to 32GB use compressed OOPs (4-byte references). Going beyond 32GB doubles reference size, increasing memory usage by ~30-40%.
