# Performance — Polymorphism

## Virtual Dispatch Overhead

Modern JVMs make polymorphic dispatch fast:
- Monomorphic: inlined, zero overhead (most common case)
- Bimorphic: inlined with type check, small overhead
- Megamorphic: vtable/itable lookup, ~5-10% overhead

## Inlining and Devirtualization

The JIT tracks actual types seen at each call site. After profiling:
- Single type: devirtualize to direct call, inline if small
- Two types: inline both, add type check
- Three+ types: keep virtual dispatch

## Method Overloading

Overload resolution is at compile time — zero runtime overhead. Overloaded methods are independent methods.

## Cast Cost

- `(String) obj` with exact type check: cheap (compare klass pointers)
- `instanceof` check: cheap (linear scan of hierarchy)
- Pattern matching `if (obj instanceof String s)`: same cost as instanceof

## Lambda Overhead

- First call: `invokedynamic` bootstrap resolves lambda (cost similar to class loading)
- Subsequent calls: direct method call (same as regular method)
- Lambda that captures no variables: singleton (reused)
- Lambda that captures variables: new instance each time (captures are fields)
