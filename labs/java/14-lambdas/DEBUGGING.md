# Debugging — Lambdas

## Stepping Through Lambdas
- Most IDEs (IntelliJ, Eclipse) can step into lambda bodies
- Lambda methods appear as `lambda$main$0` in the stack trace

## Printing Inside a Lambda
```java
list.stream()
    .map(s -> {
        System.out.println("Mapping: " + s);
        return s.toUpperCase();
    })
    .forEach(System.out::println);
```

## Watch Variables
Variables captured by the lambda are visible in the IDE debugger's variables panel when inside the lambda body.

## Lambda Stack Traces
```
at Example.lambda$main$0(Example.java:10)
at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)
```
The `lambda$main$0` identifier maps to the synthetic method generated for the lambda at line 10.

## Common Issues
- "Variable must be effectively final" — check scope
- "Target type unknown" — add explicit cast or assign to typed variable
- Stack traces truncated in parallel operations — examine `CompletionException` causes
