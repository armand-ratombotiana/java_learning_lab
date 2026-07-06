# How JVM Tuning Works

## Heap Allocation
When the JVM starts, it commits `-Xms` worth of heap memory from the OS. The heap grows as needed up to `-Xmx`. Growth requires a system call (mmap on Linux, VirtualAlloc on Windows) and page table updates. Fixed heap (`-Xms = -Xmx`) avoids growth overhead but commits all memory upfront.

## Young Generation Collection
When Eden fills, a young collection (minor GC) copies live objects from Eden + SurvivorFrom to SurvivorTo. Objects that survive `MaxTenuringThreshold` collections (default 15) are promoted to old gen. The `-XX:TargetSurvivorRatio` (default 50%) controls how full Survivor spaces can be before promotions increase.

## Code Cache Management
Code cache is divided into three heaps:
- **Non-nmethod**: small stubs and adapters
- **Profiled**: C1 compiled code (tier 1-3)
- **Non-profiled**: C2 compiled code (tier 4)

When the code cache fills by 95%, the JIT:
1. Stops C2 compilation (C2 code is largest)
2. If still filling, stops C1
3. Flushes older nmethods (sweeper thread)
4. If flush doesn't help, stops all compilation

## Metaspace Growth
Metaspace allocates chunks (4KB aligned) from native memory. When a class is loaded, its metadata is stored in the current chunk. If the chunk doesn't have space, a new chunk is allocated. GC collects Metaspace when ClassLoaders become unreachable. The collected chunks are returned to the free list for reuse.

## Large Page Allocation
With `-XX:+UseLargePages`, the JVM uses 2 MB pages (or 1 GB) instead of 4 KB:
- Reduces TLB entries needed (one entry covers 512× more memory)
- Improves CPU cache efficiency
- Pages are pinned in memory (can't be swapped)
- Allocation is slower (requires contiguous physical memory)

## NUMA-Aware Allocation
With `-XX:+UseNUMA`, the JVM:
- Allocates TLABs from the thread's local socket memory
- Structures the heap as multiple NUMA regions
- Attempts to keep GC threads on sockets with their data

## String Dedup Process
During young GC, G1 examines string objects that have survived `StringDeduplicationAgeThreshold` collections:
1. Hash the string's char[] content
2. Look up hash in a dedup table
3. If a matching char[] exists, update the string's reference to the existing array
4. The original char[] becomes eligible for GC
