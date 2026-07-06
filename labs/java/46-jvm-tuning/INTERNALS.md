# JVM Tuning Internals

## Heap Growth Mechanism
The JVM's heap manager commits memory in pages. When `-Xms` < `-Xmx`, the heap grows by:
1. Detecting allocation failure (TLAB allocation fails)
2. Expanding the heap by a growth increment
3. Committing new pages from the OS
4. Resuming allocation

Growth is expensive (page table updates, TLB shootdowns). Fixed heap avoids this.

## Code Cache Internal Structure
The code cache is managed by `CodeCache` (C++ class):
- `get_code_heap()` returns the appropriate heap (non-nmethod, profiled, non-profiled)
- `allocate()` attempts allocation in the non-profiled heap first
- `sweep()` removes zombie nmethods
- `full_count()` checks if the cache is near capacity

## Metaspace Chunk Allocation
Metaspace uses a buddy allocation system:
- Root chunks: 2 MB aligned
- Split into smaller chunks for allocation
- Free chunks stored in a free list
- Maximum chunk size: 4 MB

When a class requires more than the current chunk can provide, a new chunk is allocated from the parent space.

## ELF Symbol Resolution
For `-XX:+PrintAssembly`, the JVM needs the hsdis plugin. The plugin disassembles compiled code by reading the nmethod's instruction bytes and translating them to assembly mnemonics using a CPU-specific decoder (e.g., Capstone for x86).

## String Dedup Table
The dedup table is a `HashMap<StringHash, WeakReference<char[]>>`:
- Key: hash of the char[] content (int)
- Value: weak reference to the char[]
- Size: bounded by `-XX:StringDeduplicationSizeThreshold`
- Cleanup: cleared when char[] is collected

## JVM Flag Storage
JVM flags are stored as `Flag` objects:
- Each flag has: name, type (intx, uintx, double, bool, ccstr), current value, default value, origin
- Origin: DEFAULT, COMMAND_LINE, ENVIRON, ATTACH_ON_DEMAND, MANAGEMENT
- `-XX:+PrintFlagsFinal` prints all flags with their origins
- `-XX:+PrintCommandLineFlags` prints only command-line modified flags
