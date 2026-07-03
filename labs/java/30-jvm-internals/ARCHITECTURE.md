# Architecture: JVM Design

## G1 GC Architecture
```
Heap Region Management:
┌──────────────────────────────────────┐
│ Young Collection (STW)               │
│  → Eden regions promoted to Survivor │
│  → Survivor regions aged             │
├──────────────────────────────────────┤
│ Concurrent Marking                   │
│  → Scan roots → Initial mark (STW)   │
│  → Concurrent marking (application)  │
│  → Final marking (STW)               │
│  → Cleanup (partially STW)           │
├──────────────────────────────────────┤
│ Mixed Collections (STW)              │
│  → Evacuate young + selected old     │
│  → Repeat until old gen occupancy ↓  │
├──────────────────────────────────────┤
│ Full GC (STW, fallback)              │
│  → Single-threaded mark-sweep-compact│
│  → Only when concurrent fails        │
└──────────────────────────────────────┘
```

## JIT Compilation Tiers
```
Tier 0: Interpreter (no compilation)
    ↓ (method hot enough)
Tier 1: Simple C1 (no profiling)
    ↓ (more profiling needed)
Tier 2: Limited C1 (light profiling)
    ↓ (even more profiling)
Tier 3: Full C1 (full profiling)
    ↓ (very hot, > CompileThreshold)
Tier 4: C2 (maximally optimized)
```

## Memory Architecture
```
Each Thread:
┌──────────────────────┐
│ TLAB (Eden portion)  │
│ Stack (frames)       │
│ PC Register          │
│ Native Stack         │
└──────────────────────┘

Shared:
┌──────────────────────┐
│ Heap (Eden, S0, S1,  │
│        Old, Humongous)│
│ Metaspace (classes)   │
│ String Table          │
│ Code Cache (JIT)      │
│ Constant Pool Pool    │
└──────────────────────┘
```

## Class Loading Architecture
```
Bootstrap ClassLoader
    └── Extension/Platform ClassLoader
            └── Application/System ClassLoader
                    └── Custom ClassLoaders

Delegation: parent first by default
Custom: can override findClass() to load from networks, databases, etc.
```
