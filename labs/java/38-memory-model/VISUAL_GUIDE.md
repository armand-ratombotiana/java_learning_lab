# Visual Guide — Java Memory Model (Lab 38)

## JMM Happens-Before Diagram

The Java Memory Model defines when one thread's actions are visible to another.

```
   Thread A                    Thread B
       │                           │
       │ synchronized(lock) {      │
       │   x = 42;                 │
       │   flag = true;            │
       │ }  ─── unlock ─────────►  │ synchronized(lock) {   // sees x=42, flag=true
       │                           │   assert x == 42;      }
       │                           │
       │ volatile boolean ready;   │
       │ ready = true; ──────────►  │ if (ready) {          // guaranteed to see prior writes
       │                           │   useData();           }
       │                           │
       │ t.start() ──────────────►  │ (first instruction of new thread sees everything
       │                           │  before start())
```

Key happens-before rules:
1. **Program order** — within a single thread, each action happens-before the next.
2. **Monitor lock** — unlock on a monitor happens-before every subsequent lock on that monitor.
3. **Volatile** — write to a volatile field happens-before every subsequent read of that field.
4. **Thread start** — `t.start()` happens-before the first action in the new thread.
5. **Transitivity** — if A happens-before B and B happens-before C, then A happens-before C.

## GC Generations (HotSpot Heap)

```
   ┌─────────────────────────────────────────────────────┐
   │                  Java Heap                            │
   │                                                       │
   │  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │
   │  │    Young      │  │    Old       │  │   Metaspace│ │
   │  │  Generation   │  │  Generation  │  │ (not heap) │ │
   │  │               │  │              │  │            │ │
   │  │ ┌───┬───┬───┐ │  │              │  │ class      │ │
   │  │ │ E │ S0│ S1│ │  │ long-lived   │  │ metadata   │ │
   │  │ │den│   │   │ │  │ objects      │  │ interned   │ │
   │  │ └───┴───┴───┘ │  │ promoted     │  │ strings    │ │
   │  └───────┬───────┘  │ from young   │  └────────────┘ │
   │          │          │              │                 │
   │    Minor GC ──────► │  Major GC /  │                 │
   │    (copy survivors) │  Full GC     │                 │
   └─────────────────────┴──────────────┴─────────────────┘
```

- **Eden**: new objects allocated here. Most die young.
- **Survivor spaces (S0/S1)**: objects that survive a minor GC are copied between survivors, age incremented.
- **Old generation**: objects promoted after reaching a tenure threshold (`-XX:+MaxTenuringThreshold`).
- **Metaspace** (Java 8+): class metadata; grows dynamically (replaces PermGen).
