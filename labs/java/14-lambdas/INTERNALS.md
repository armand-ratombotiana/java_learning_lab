# Internals — Lambdas

## invokedynamic
The `invokedynamic` instruction allows the JVM to determine the call site target at runtime, enabling lambda translation without generating new classes at compile time.

## LambdaMetafactory
```java
LambdaMetafactory.metafactory(
    MethodHandles.Lookup caller,
    String interfaceMethodName,
    MethodType factoryType,
    MethodType interfaceMethodType,
    MethodHandle implementation,
    MethodType dynamicMethodType
)
```

## Captured Arguments
- Non-capturing lambdas (`x -> x+1`) are singleton — one instance shared globally
- Capturing lambdas create a new instance each time they cross a capture boundary
- The JVM may inline and escape-analyse captured lambdas

## Serialization
Lambdas are not serializable by default. To make a lambda serializable, the target functional interface must extend `Serializable` and the capture site must be stable.

## Synthetic Methods
The compiler generates a `private static` (or instance) synthetic method for the lambda body, named `lambda$main$0` etc.
