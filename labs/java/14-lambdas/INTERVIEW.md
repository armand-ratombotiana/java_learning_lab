# Interview Questions — Lambdas

## Beginner
1. What is a lambda expression and how is it different from an anonymous class?
2. Explain `@FunctionalInterface` and its rules.
3. What are method references? Show three forms.

## Intermediate
4. How does variable capture work? What is "effectively final"?
5. Describe the `java.util.function` package — what are `Predicate`, `Function`, `Consumer`, `Supplier`?
6. What is the difference between `Function.compose` and `Function.andThen`?

## Advanced
7. Explain the bytecode of a lambda — what is `invokedynamic` and how does `LambdaMetafactory` work?
8. How does the JIT compiler optimise lambdas? When are they inlined?
9. Compare lambda performance vs anonymous inner classes (class loading, instantiation, GC).

## Code Review
10. "This code has a compile error — why?"
   ```java
   int x = 1;
   Runnable r = () -> x = 2;
   ```
11. "Convert this to use method references: `list.stream().map(s -> s.trim()).forEach(s -> System.out.println(s))`"
