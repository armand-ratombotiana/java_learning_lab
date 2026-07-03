# How It Works — Lambdas

## Compilation
The Java compiler (javac) desugars a lambda into a `invokedynamic` instruction plus a bootstrap method that links to `LambdaMetafactory`.

## At Runtime
1. `invokedynamic` calls the bootstrap method
2. Bootstrap resolves the functional interface and lambda body
3. A synthetic method is generated (or a pre-existing method handle is used)
4. The JVM caches the resulting `Function` / `Predicate` / etc. instance

## Example Desugaring
```java
// Source:
list.forEach(x -> System.out.println(x));

// After compilation (roughly):
list.forEach(
    (Consumer<String>) LambdaMetafactory.metafactory(
        lookup, "accept", MethodType.methodType(Consumer.class),
        MethodType.methodType(void.class, Object.class),
        lookup.findStatic(ClassName.class, "lambda$0", ...),
        MethodType.methodType(void.class, String.class)
    )
);
```

## Performance
- No additional class loading per lambda (unlike anonymous classes)
- Instance creation is cheap (~O(1))
- Capture site is cached after first invocation
