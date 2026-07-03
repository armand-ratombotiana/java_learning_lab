# Refactoring — Java Streams

## Before (Loops)
```java
List<String> result = new ArrayList<>();
for (String s : list) {
    if (s.startsWith("A")) {
        result.add(s.toUpperCase());
    }
}
```

## After (Streams)
```java
List<String> result = list.stream()
    .filter(s -> s.startsWith("A"))
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

## Nested Loops → flatMap
```java
// Before
for (List<Integer> inner : matrix) {
    for (int x : inner) { sum += x; }
}
// After
int sum = matrix.stream().flatMapToInt(inner -> inner.stream().mapToInt(i -> i)).sum();
```

## Conditional Accumulation → Reduce
```java
// Before
int max = Integer.MIN_VALUE; for (int x : arr) if (x > max) max = x;
// After
int max = arr.stream().reduce(Integer.MIN_VALUE, Integer::max);
```

## Resource Cleanup → try-with-resources
```java
try (Stream<String> lines = Files.lines(path)) {
    lines.filter(...).forEach(...);
}
```

## Extracting Complex Lambdas
Extract multi-line lambdas into private methods with method references:
```java
.filter(this::isValidRecord)
```
