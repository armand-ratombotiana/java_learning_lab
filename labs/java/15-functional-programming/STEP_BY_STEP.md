# Step by Step — Functional Programming

## Refactor Imperative to Functional

### Step 1: Imperative
```java
List<String> result = new ArrayList<>();
for (String s : list) {
    if (s != null && s.length() > 3) {
        result.add(s.toUpperCase());
    }
}
```

### Step 2: Introduce Stream
```java
List<String> result = list.stream()
    .filter(s -> s != null && s.length() > 3)
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

### Step 3: Use Pure Predicates (extract)
```java
Predicate<String> nonNull = Objects::nonNull;
Predicate<String> longerThan3 = s -> s.length() > 3;

List<String> result = list.stream()
    .filter(nonNull.and(longerThan3))
    .map(String::toUpperCase)
    .toList();
```

### Step 4: Handle Optional (instead of null checks)
```java
public Optional<String> findFirstLong(List<String> input) {
    return input.stream()
        .filter(Objects::nonNull)
        .filter(s -> s.length() > 3)
        .map(String::toUpperCase)
        .findFirst();
}
```

## Apply the Functional Pipeline
```java
findFirstLong(names)
    .map(s -> "Found: " + s)
    .ifPresentOrElse(
        System.out::println,
        () -> System.out.println("Not found")
    );
```
