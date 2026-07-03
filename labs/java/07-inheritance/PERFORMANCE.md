# Performance — Inheritance

## Virtual Dispatch Cost

- Each virtual method call looks up the vtable — minimal overhead
- JIT uses type profiling: after N calls, if only one type seen, it devirtualizes and inlines
- Megamorphic call sites (3+ types) — cannot devirtualize, vtable dispatch

## Interface Dispatch (itable)

More expensive than vtable — JIT uses inline caching:
1. Monomorphic: cache single type, fast path
2. Polymorphic: check cached types, fallback to itable
3. Megamorphic: always use itable

## Deep Hierarchies

Deep inheritance trees make JIT optimization harder. The compiler has more types to consider and more methods to resolve.

## Final Methods

- Cannot be overridden — JIT can inline them without guard
- Mark base class methods `final` if they shouldn't be overridden
- Performance benefit is usually negligible — use for design, not optimization

## Abstract Methods

- Same dispatch cost as any virtual method
- Indirect (via interface dispatch) is slightly more expensive
- JIT optimizes concrete implementations that extend abstract classes

## Object.getClass() vs instanceof

- `getClass() == SomeClass.class` is O(1) — pointer comparison
- `instanceof` walks hierarchy — O(depth) in worst case
- Both are fast — don't optimize prematurely
