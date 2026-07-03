# Polymorphism — Internal Mechanics

## VTable Dispatch (invokevirtual)

`invokevirtual` looks up the method in the receiver's vtable by index. The vtable index is assigned at class loading time and is stable for the class hierarchy. This is why adding methods to a class can require recompilation of subclasses.

## ITable Dispatch (invokeinterface)

`invokeinterface` is more expensive than invokevirtual because it must search the itable for the matching interface method. Modern JVMs use inline caching:
1. First call: resolve interface method in implementing class
2. Cache the result (monomorphic or polymorphic cache)
3. Subsequent calls check if receiver class matches cached class → fast path

## Method Inlining and Devirtualization

The JIT compiler analyzes call sites:
- **Monomorphic**: Only one implementation seen → devirtualize to direct call → inline
- **Bimorphic**: Two implementations seen → inline both with type check
- **Megamorphic**: Three+ implementations → fall back to vtable dispatch

## Overload Selection

The compiler's overload resolution follows a strict priority:
1. Exact match
2. Widening primitive conversion (byte→int→long→float→double)
3. Autoboxing/unboxing
4. Varargs

```java
void m(int x)        // priority 1 for m(5)
void m(long x)       // priority 2 for m(5)
void m(Integer x)    // priority 3 for m(5)
void m(int... x)     // priority 4 for m(5)
```

## Bridge Methods in Generics

When overriding a method that returns a generic type, the compiler generates a bridge method for type erasure compatibility. The bridge calls the actual method.
