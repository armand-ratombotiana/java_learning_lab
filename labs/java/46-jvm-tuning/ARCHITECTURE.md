# Architecture of JVM Tuning

## Heap Architecture
```
Java Heap
├── Young Generation
│   ├── Eden (TLABs, shared allocations)
│   ├── Survivor From (previous collection survivors)
│   └── Survivor To (current collection destination)
└── Old Generation
    └── Tenured (long-lived objects)
```

## Code Cache Architecture
```
Code Cache
├── Non-nmethod Heap (small allocations)
│   ├── Adapter blobs (method entry adapters)
│   ├── Buffer blobs (deoptimization handlers)
│   └── Runtime stubs (safepoint, exception)
├── Profiled nmethod Heap (C1 code)
│   └── Compiled methods with profiling data
└── Non-profiled nmethod Heap (C2 code)
    └── Fully optimized compiled methods
```

## Metaspace Architecture
```
Metaspace
├── Class Metadata
│   ├── Klass structure
│   ├── Vtable, Itable
│   └── Method data (MDO)
├── Constant Pool Cache
│   └── Resolved references
└── Chunk Free List
    ├── Small chunks (1-4KB)
    ├── Medium chunks (4-64KB)
    ├── Large chunks (64KB-1MB)
    └── Humongous chunks (>1MB)
```

## Tuning Decision Tree
```
High GC overhead?
├── High allocation rate?
│   ├── Yes: Profile allocation, reduce waste
│   └── No: Increase heap size
├── Long pause times?
│   ├── Yes: Switch to concurrent collector
│   └── No: Tune young generation size
└── OutOfMemoryError?
    ├── Yes: Increase heap or fix leaks
    └── No: Threshold-based tuning
```

## JVM Flag Categories
```
Memory: -Xms, -Xmx, -Xmn, -XX:NewRatio, -XX:SurvivorRatio
GC: -XX:+UseG1GC, -XX:MaxGCPauseMillis, -XX:ParallelGCThreads
Code: -XX:ReservedCodeCacheSize, -XX:CICompilerCount
Metaspace: -XX:MaxMetaspaceSize, -XX:MetaspaceSize
OS: -XX:+UseLargePages, -XX:+UseNUMA, -XX:+AlwaysPreTouch
Debug: -XX:+PrintFlagsFinal, -XX:+PrintCompilation, -Xlog:gc*
```
