# Performance — Abstraction & Interfaces

## Interface Dispatch Cost

Interface method calls use `invokeinterface` — slightly more expensive than `invokevirtual`. JIT uses inline caching:
- First call: resolve interface method, cache result
- Subsequent calls: check receiver class against cache
- Cache miss: full resolution again

## Default Method Performance

Default methods are compiled to regular methods in the interface's class file. Dispatch is through itable — same cost as any interface method.

## Lambda vs Anonymous Class

Lambdas are more efficient:
- No separate .class file per lambda (uses `invokedynamic`)
- Non-capturing lambda = singleton (reused)
- Capturing lambda = instances cached by bootstrap
- Anonymous inner class = always new instance

## Abstract Class vs Interface

- Abstract class methods use `invokevirtual` — slightly faster than `invokeinterface`
- Interface dispatch has ~1-2% overhead over class dispatch
- In practice, negligible — choose based on design, not performance

## Static Interface Methods

Resolved at compile time (`invokestatic`) — zero virtual dispatch cost. Same as static class methods.
