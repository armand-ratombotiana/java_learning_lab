# GC Internals

## G1 Region Structure
Each G1 region is 1 MB by default. The region's metadata includes:
- Region type (Eden, Survivor, Old, Humongous, Available)
- Live bytes count (used for garbage-first ordering)
- Remembered set (cards with cross-region references)
- Bottom and top addresses in the heap

## G1 Remembered Set Filtering
RSets use three filtering levels:
1. **Cards**: 512-byte aligned chunks containing object references
2. **Bitmaps**: per-card granularity for fine filtering
3. **PRT (Per-Region Table)**: coarse filter (which regions have references)

## ZGC Pointer Format (64-bit)
```
Bit 63-47    Bit 46-44    Bit 43-0
[4-bit color] [reserved]  [42-bit address]
```
- Bit 45 (Finalizable)
- Bit 44 (Remapped)
- Bit 43 (Marked0/Marked1 — alternates)
- Addressable space: 2^42 = 4 TB (without compressed OOPs)

## GC Safepoints
Safepoints are points where all application threads have stopped (for STW GC operations). At a safepoint:
- Thread stacks are scanned (for root references)
- Object headers are updated (for GC state)
- Code is patched (for compiled code migration)

Threads check for safepoint requests at:
- Method exits/entries
- Loop back edges
- Object allocations
- Blocking calls

## GC Log Format (Unified JVM Logging)
With `-Xlog:gc*`:
```
[0.123s][info][gc] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 512M->128M(1024M) 10.5ms
[1.456s][info][gc] GC(1) Concurrent Mark Cycle 50.2ms
```
Format: timestamp, log level, tag, GC ID, event type, before/after/heap, duration.

## Allocation Buffer (TLAB)
Each thread has a Thread-Local Allocation Buffer (TLAB):
- Large heap allocated in TLAB-sized chunks
- Threads bump-allocate within TLAB without synchronization
- TLAB size adjusts based on allocation history
- Large objects (non-TLAB) are allocated directly in Eden
