# Code Deep Dive — Functional Programming

## Pure Data Processing Pipeline
```java
record Transaction(String id, double amount, boolean flagged) {}

List<String> flaggedIds = transactions.stream()
    .filter(t -> !t.flagged())
    .filter(t -> t.amount() > 10_000)
    .map(Transaction::id)
    .toList(); // Java 16+
```

## Optional as Monad
```java
public Optional<Double> average(List<Integer> numbers) {
    if (numbers.isEmpty()) return Optional.empty();
    double sum = numbers.stream().mapToInt(i -> i).sum();
    return Optional.of(sum / numbers.size());
}

// Safe from NPE:
average(list)
    .filter(a -> a > 50)
    .ifPresentOrElse(
        a -> System.out.println("High average: " + a),
        () -> System.out.println("No data or low average")
    );
```

## Combinator Pattern
```java
@FunctionalInterface
interface Validator<T> {
    boolean validate(T t);
    default Validator<T> and(Validator<T> other) {
        return t -> this.validate(t) && other.validate(t);
    }
}

Validator<String> nonEmpty = s -> !s.isEmpty();
Validator<String> minLength = s -> s.length() >= 3;
Validator<String> combined = nonEmpty.and(minLength);

System.out.println(combined.validate("ab"));  // false
System.out.println(combined.validate("abc")); // true
```
