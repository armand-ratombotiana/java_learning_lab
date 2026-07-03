# Architecture — Lambdas

## Java Function Library Architecture

```
java.util.function
├── Predicate<T>          — test(T) : boolean
├── Function<T,R>         — apply(T) : R
├── Consumer<T>           — accept(T) : void
├── Supplier<T>           — get() : T
├── UnaryOperator<T>      — T -> T
├── BinaryOperator<T>     — (T,T) -> T
│
├── IntFunction / LongFunction / DoubleFunction
├── IntPredicate / LongPredicate / DoublePredicate
└── IntConsumer / LongConsumer / DoubleConsumer
    (Primitive specializations to avoid boxing)
```

## How Lambdas Integrate with Collections
```
Collections.sort(list, comparator)   // Comparator is @FunctionalInterface
list.forEach(consumer)                // Consumer is @FunctionalInterface
list.removeIf(predicate)              // Predicate is @FunctionalInterface
list.replaceAll(unaryOperator)        // UnaryOperator
map.computeIfAbsent(key, function)    // Function
```

## Language Integration
```
Java Language
  │
  └── Lambda expression
       │
       ├── javac: desugar to invokedynamic
       ├── JVM: link via LambdaMetafactory
       └── Runtime: synthetic method + cached functional interface
```
