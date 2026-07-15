# Garbage Collection Tuning Internals

## ⚙️ Heap Layouts by Algorithm

### 1. G1 GC (Garbage First)
G1 ignores the physical contiguous generations (Eden, Survivor, Tenured). Instead, it divides the heap into roughly 2,048 **Regions**.
- **Eden Regions**: Where new objects go.
- **Survivor Regions**: Where surviving objects go.
- **Old Regions**: Where tenured objects live.
- **Humongous Regions**: A special region for objects that are larger than 50% of a standard region size. These are expensive and bypass the normal collection cycle.

### 2. ZGC (Z Garbage Collector)
ZGC uses a technique called **Colored Pointers**. It uses the 64-bit address pointer itself to store metadata about the object's state (e.g., has the object been moved?).
- This allows ZGC to perform **Concurrent Compaction**. While the application is running, ZGC moves objects to new locations to eliminate fragmentation. If an application thread tries to access an old address, a "Load Barrier" intercepts it and redirects it to the new address instantly.

## 📊 Key Tuning Metrics
1. **Allocation Rate**: How many MB/second your app is creating. If this is higher than the GC cleanup rate, you will get an OOM.
2. **Promotion Rate**: How much data is moving from Young to Old generation. High promotion rates often indicate that the Young generation is too small (causing "Premature Promotion").
3. **Pause Time**: The maximum duration of a Stop-The-World event.

## 🛠️ Critical JVM Flags
- `-Xms` / `-Xmx`: Initial and Maximum heap size.
- `-XX:MaxGCPauseMillis`: The target for G1GC to aim for.
- `-XX:G1HeapRegionSize`: Adjusting this can help with Humongous objects.
- `-XX:+UseStringDeduplication`: (G1 only) Can save 10-20% memory by sharing the underlying char arrays of identical strings.
- `-Xlog:gc*`: The modern way to enable detailed GC logging.