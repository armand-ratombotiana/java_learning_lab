# Performance — OOP Basics

## Object Allocation

- `new` allocates on heap — relatively expensive compared to stack allocation
- Escape Analysis allows JIT to allocate on stack for non-escaped objects
- Object pooling can reduce allocation but adds complexity

## Object Header Overhead

Every object has a header (12-16 bytes on 64-bit). Many small objects waste memory — use primitives or arrays.

## Getter/Setter Inlining

Simple getters/setters are inlined by JIT (no runtime overhead). Don't sacrifice encapsulation for performance.

## Static vs Instance Methods

- Static methods: resolved at compile time, slightly faster dispatch
- Instance methods: virtual dispatch (unless devirtualized)
- Difference is negligible after JIT warmup

## GC Pressure

Creating many short-lived objects increases GC frequency. In hot paths:
- Reuse mutable objects (but be careful with thread safety)
- Use primitives instead of wrappers
- Pool expensive objects (database connections, threads)

## Final Fields

Final fields enable JIT optimizations — they're treated as constants after construction. JMM guarantees safe publication of final fields.
