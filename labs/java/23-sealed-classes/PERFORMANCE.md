# Performance Analysis of Sealed Classes

## Compiler Optimizations

### Virtual Call Optimization
The JIT compiler can optimize virtual dispatch for sealed hierarchies more aggressively than for open hierarchies.

**Monomorphic dispatch** (single subclass):
If at runtime only one permitted subtype is ever used at a call site, the JIT can devirtualize and inline the call, eliminating dispatch overhead entirely.

**Bimorphic dispatch** (two subclasses):
For sealed types with exactly two subtypes, the JIT can generate efficient inline caches with a type check and direct call.

**Megamorphic dispatch** (three or more subclasses):
For sealed types with a small, finite number of subtypes (known at compile time), the JIT can generate a dense type switch that is faster than a vtable lookup.

### Switch Expression Optimization
Pattern matching on sealed types can be optimized by the compiler:

```java
double area(Shape s) {
    return switch (s) {
        case Circle c -> Math.PI * c.r() * c.r();
        case Rectangle r -> r.w() * r.h();
    };
}
```

The compiler can generate:
1. A `tableswitch` or `lookupswitch` bytecode instruction using the sealed type's internal type ID (similar to enum switches)
2. Type checks are O(1) rather than O(n) for a chain of instanceof checks

### Escape Analysis
The closed set of subtypes enables better escape analysis:
- The JIT knows all possible types an object variable could hold
- This enables more precise object lifetime analysis
- Leads to better scalar replacement and stack allocation decisions

## Memory Overhead

### Sealed Class Metadata
The sealed class metadata (PermittedSubclasses attribute) is stored in the class file's constant pool and loaded into the metaspace. For a typical sealed hierarchy with 3-5 subtypes, this adds approximately 50-100 bytes to the class metadata.

### No Instance Overhead
Sealed classes add no instance-level memory overhead. The instance layout of a sealed class is identical to a non-sealed class with the same fields.

## Runtime Type Checking

### instanceof Checks
The JVM's `instanceof` implementation can be optimized for sealed types:
- If the sealed type has few subtypes, the JVM can use a bit test on the class's internal type ID
- Traditional instanceof requires checking all parent classes in the hierarchy chain
- Sealed instanceof can use a range check: `typeId >= minSealedType && typeId <= maxSealedType`

### Pattern Matching at Scale
For deeply nested sealed hierarchies (e.g., expression trees), the compiler can generate efficient dispatch:

```java
Expr expr = new Add(new Constant(5), new Multiply(new Constant(3), new Constant(4)));
int result = evaluate(expr);  // Multiple dispatch points

// Each switch in the evaluation chain is compiled to an O(1) type switch
```

## Benchmarking Considerations

### Microbenchmarking Caveats
When benchmarking sealed classes:
1. **Warm-up is critical**: JIT optimization of sealed type dispatch requires sufficient iterations
2. **Type distribution matters**: Test with uniform distribution of subtypes (worst case for type checks)
3. **Compare with baseline**: Compare sealed switch against equivalent instanceof-chain or Visitor pattern
4. **Profile allocation**: Sealed types are often used with records (product types), which may increase allocation rates

### Expected Performance Characteristics
| Operation | Sealed Class | Open Hierarchy | Improvement |
|-----------|-------------|----------------|-------------|
| instanceof check | O(1) type switch | O(h) hierarchy walk | 2-10x for deep hierarchies |
| Virtual method call (monomorphic) | Devirtualized + inlined | Devirtualized + inlined | Same |
| Virtual method call (megamorphic 3) | Inline cache | vtable lookup | ~2x |
| Switch on type | tableswitch | if-else chain | O(1) vs O(n) |
| Exhaustiveness check | Compile time | N/A | Enables optimizations |

## Summary

Sealed classes provide:
- **Zero instance overhead** (no additional fields or metadata at instance level)
- **Better JIT optimization** through closed-world type assumption
- **More efficient type checking** through type-range checks
- **Compiler-verified exhaustiveness** (not a runtime optimization but prevents runtime errors)

The performance gains are modest for most applications but can be significant in performance-critical code paths with polymorphic dispatch (e.g., interpreter loops, expression evaluators, event processors).
