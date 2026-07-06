# How Garbage Collection Works

## G1 Region-Based Collection
G1 divides the heap into 1MB regions. Each region is either:
- **Eden**: newly allocated objects
- **Survivor**: objects that survived one or more young collections
- **Old**: long-lived objects
- **Humongous**: objects > 50% of region size
- **Available**: unallocated

G1 collects the region with the most garbage first (garbage-first), yielding the highest free space per pause.

## G1 Marking Cycle
1. **Initial Mark**: STW, marks GC roots (takes ~1ms)
2. **Concurrent Mark**: Concurrent, traces object graph (application runs)
3. **Remark**: STW, finishes marking (takes ~10-100ms)
4. **Cleanup**: STW, accounts live objects, selects regions for evacuation

## G1 Remembered Sets
Each region maintains a remembered set (RS) tracking cards that contain references from other regions. RSets enable G1 to collect a region without scanning the entire heap.

## G1 SATB (Snapshot-At-The-Beginning)
SATB ensures concurrent marking correctness. At the start of marking, a snapshot of the live object graph is taken. During concurrent marking, objects that become garbage (but were live in the snapshot) are treated as live. This prevents premature collection.

## ZGC Colored Pointers
ZGC uses 4 bits in the 64-bit pointer for GC metadata:
- **Finalizable**: reachable via finalizer
- **Remapped**: pointer has been updated after relocation
- **Marked0/Marked1**: used for marking (two-bit marking, alternates between cycles)

Bits are stolen from the high bits of a 64-bit address. ZGC requires a 64-bit JVM and works on Linux, Windows, and macOS (but not 32-bit).

## ZGC Load Barriers
ZGC's load barrier executes on each object reference read. It checks the pointer's color bits:
- If the pointer is "good" (not needing relocation), proceed directly
- If the pointer is "bad" (needs relocation), fix up the pointer and retry

Load barriers are only on reference reads, not writes. This minimizes overhead compared to GC write barriers.

## GC Root Scanning
Root scanning identifies all GC roots. The JVM must walk:
- Thread stacks (active method frames, local variables)
- Static fields (in class metadata)
- JNI global/handles (native code references)
- Native (unmanaged) references

Root scanning is a stop-the-world operation because thread stacks must be consistent.
