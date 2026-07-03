# Performance — Lambdas

## Lambda vs Anonymous Class
| Aspect | Lambda | Anonymous Class |
|--------|--------|----------------|
| Class loading | None (invokedynamic) | New .class file |
| Instance creation | ~1-2 ns | ~10-20 ns |
| Capture | Avoids object creation for stateless | Always creates new object |

## Non-Capturing vs Capturing
- **Non-capturing:** Stored in `LambdaMetafactory` cache — singleton, effectively free
- **Capturing:** Creates new instance per capture site crossing — allocate on heap, but JIT may inline

## Inlining
The JIT can inline lambda bodies more aggressively than anonymous classes because there's no indirection via a virtual method.

## Boxing
```java
// Bad — autoboxing overhead
IntFunction<Integer> f = x -> x + 1;
// Good — primitive
IntUnaryOperator f = x -> x + 1;
```

## Method References
`String::length` is typically faster than `s -> s.length()` because the JVM resolves method handles efficiently.

## Benchmarking
Always use JMH. Simple `System.nanoTime()` tests are unreliable due to JIT warm-up, on-stack replacement, and garbage collection.
