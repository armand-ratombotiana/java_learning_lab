# JIT Compilation Internals

## MethodData Object (MDO)
The MDO stores profiling data per method:
- Counter cell: invocation count (4 bytes)
- Backedge counter: loop iteration count (4 bytes)
- Type profile: for each argument, a profile of actual types (counts per class)
- Branch profile: taken/not-taken counts for each branch

## nmethod
Compiled native code is stored in an `nmethod` (native method):
- Code blob: the compiled instructions
- Metadata: relocation information, OOP maps, scopes
- State: alive, not-entrant, zombie (can be unloaded)
- Dependencies: on classes, methods, and assumptions

## Compiler Threads
- `-XX:CICompilerCount` (default: 2 on 1-2 cores, more on larger systems)
- Compiler threads run at normal priority
- Each thread picks from the compilation queue (priority: warm methods first)
- Compilation is non-blocking — application threads continue

## Deoptimization Types
- **Bailout**: C2 fails to compile (too complex), falls back to C1
- **Non-entrant**: nmethod is no longer valid (class hierarchy changed)
- **Deoptimization point**: specific bytecode index where deopt can occur
- **Frame patching**: when deoptimizing, the stack frame is replaced with interpreter frames

## OSR Entry Points
An OSR nmethod has:
- Entry from the interpreter (normal execution)
- OSR entry from the loop backedge (specially compiled loop body)
- OSR entries are only valid for the specific loop that triggered compilation

## Inline Cache
The JIT uses inline caches for virtual call optimization:
- **Monomorphic**: direct call to the single receiver type
- **Bimorphic**: check two types with a type-test sequence
- **Megamorphic**: fall back to vtable dispatch (no inline caching)

Inline caches are patched at runtime when new types appear, causing deoptimization when a bimorphic cache becomes megamorphic.

## Escape Analysis Implementation
Escape analysis is a flow-sensitive interprocedural analysis that tracks object references:
1. Identify all object allocations
2. Track references through stores, loads, and method calls
3. Classify objects: NoEscape, ArgEscape, GlobalEscape
4. Apply optimizations based on escape state
