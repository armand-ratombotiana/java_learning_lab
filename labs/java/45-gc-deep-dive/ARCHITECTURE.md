# Architecture of Garbage Collection

## G1 Architecture
```
G1CollectedHeap
├── Region Table (2048+ regions, 1MB each)
├── Remembered Sets (per-region card tables)
├── SATB Marking State
│   ├── Mark Bitmaps (prev/next)
│   └── SATB Queue (concurrent marking buffer)
├── Collection Set (regions to collect)
├── Young List (Eden + Survivor regions)
└── Free List (available regions)
```

## ZGC Architecture
```
ZCollectedHeap
├── Page Allocator (2MB small pages, 32MB medium, N*2MB large)
├── Marking State
│   ├── Mark Bitmap (relocation information)
│   ├── Color Bits (per pointer)
│   └── Load Barrier Stubs (generated per use site)
├── Relocation Set (pages being relocated)
├── Forwarding Table (object → new address mapping)
└── Statistics (live data, allocation rate)
```

## GC Root Sources
```
GC Roots
├── Thread Roots
│   ├── Stack frames (local variables, operand stack)
│   ├── Continuation frames (virtual threads)
│   └── Thread objects
├── Static Roots
│   ├── Class static fields
│   └── System classes
├── JNI Roots
│   ├── Global references
│   ├── Local references
│   └── Weak references
└── Other
    ├── JVM internals
    └── Native memory
```

## GC Phase Architecture
```
Young Collection:
├── Root Scanning (STW)
├── Object Copying (Evacuation)
├── RSet Scanning (G1)
└── Update References (STW)

Concurrent Marking (G1/ZGC):
├── Initial Mark (STW)
├── Concurrent Mark (async)
├── Remark (STW, G1 only)
└── Cleanup (STW, G1 only)

Concurrent Relocation (ZGC):
├── Select Relocation Set
├── Relocate Objects (async)
└── Fix Up Pointers (async)
```
