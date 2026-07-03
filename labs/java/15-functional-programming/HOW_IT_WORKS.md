# How It Works — Functional Programming

## Pure Function Evaluation
```java
int result = add(3, multiply(2, 4));
// Equivalent (referential transparency):
int result = add(3, 8);  // multiply(2,4) → 8
int result = 11;          // add(3,8) → 11
```

## Lazy Evaluation with Supplier
```java
Supplier<Double> pi = () -> {
    System.out.println("Computing π...");
    return 3.14159;
};
// Nothing computed yet — Supplier is lazy
double value = pi.get(); // Now computed
```

## Optional Monad Flow
```java
Optional.of("42")
    .map(Integer::parseInt)       // Optional<Integer>
    .filter(n -> n > 0)          // Optional<Integer>
    .flatMap(n -> safeDivide(100, n)) // Optional<Integer>
    .orElse(0);                   // Integer
```

## Function Composition
```java
Function<Integer, Integer> square = x -> x * x;
Function<Integer, String> display = x -> "Result: " + x;
Function<Integer, String> pipeline = square.andThen(display);
System.out.println(pipeline.apply(5)); // Result: 25
```
