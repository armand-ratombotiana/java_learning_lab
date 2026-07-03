# Internals: JVM Deep Dive

## Object Layout (HotSpot)
A Java object in memory (64-bit, compressed OOPs):
```
[Header]         - 12 bytes (mark word 8 bytes + klass pointer 4 bytes)
[Instance Data]  - Fields in declared order, aligned
[Padding]        - To 8-byte boundary
```
With compressed OOPs (default for heaps < 32GB):
- Object references are 4 bytes instead of 8
- Klass pointer is 4 bytes (requires compressed class pointers)

With -XX:-UseCompressedOops (heaps >= 32GB):
- All references are 8 bytes
- Klass pointer is 8 bytes (uncompressed)

## Mark Word
The mark word (8 bytes) encodes:
- Identity hashcode (calculated lazily, stored after first System.identityHashCode call)
- Lock state (biased locking, thin lock, fat lock, GC forwarding pointer)
- GC state (age, marking status)
- This is why overriding hashCode() can affect performance

## Vtable and Itable
- **vtable**: Virtual method dispatch table per class (pointers to actual method implementations)
- **itable**: Interface method dispatch table (organized by interface ID)
- Both are populated during class linking and used by invokevirtual/invokeinterface

## Polymorphism and Megamorphism
- **Monomorphic**: Only one receiver type seen (inlined and optimized)
- **Bimorphic**: Two receiver types (profile and test)
- **Megamorphic**: Three+ receiver types (vtable lookup, no inlining)
- Inline cache evolution: monomorphic → bimorphic → megamorphic

## TLAB (Thread Local Allocation Buffer)
Each thread gets a small Eden region for allocation without synchronization:
- Typical size: ~100KB-1MB, auto-resized
- Only OOM when TLAB cannot satisfy allocation → allocate directly in Eden (synchronized)

## Escape Analysis
The JIT can determine if an object is thread-local and:
- Allocate on stack instead of heap
- Scalar replace (replace object with its fields)
- Eliminate synchronization on non-escaped objects
