# Performance — Methods

## Calling Overhead

- Small method calls: JIT inlines them (threshold ~35 bytes). Inlined methods have zero call overhead.
- Virtual method dispatch: slightly more expensive than static (vtable lookup). JIT devirtualizes monomorphic calls.
- Interface dispatch: most expensive (itable search). JIT uses inline caching.

## Inlining-Friendly Code

- Keep small methods (under 35 bytes bytecode) for inlining
- Use `final`, `private`, `static` for methods that don't need dynamic dispatch
- The JIT inlines deeply (max depth 9 by default)
- `@ForceInline` (internal JVM annotation) can force inlining

## Recursion Performance

- Each recursive call adds a stack frame (memory + setup)
- No tail-call optimization in Java
- Prefer iteration for deep or performance-critical recursion
- Memoization can transform exponential recursion to linear

## Varargs Cost

- Each call creates an array: `method(1, 2, 3)` → `method(new int[]{1, 2, 3})`
- Avoid varargs in hot paths
- If array is often the same size, overload with fixed parameter method
