# Streams — Step-by-Step Tutorial

## Step 1: Create a Stream from a Collection

```java
List<String> items = List.of("apple", "banana", "cherry", "date");

// Sequential stream:
Stream<String> stream = items.stream();

// Parallel stream:
Stream<String> parallelStream = items.parallelStream();
```

## Step 2: Filter Elements

```java
List<String> filtered = items.stream()
    .filter(s -> s.startsWith("a") || s.startsWith("c"))
    .toList();
// [apple, cherry]
```

## Step 3: Transform Elements (map)

```java
List<Integer> lengths = items.stream()
    .map(String::length)
    .toList();
// [5, 6, 6, 4]

List<String> uppercased = items.stream()
    .map(String::toUpperCase)
    .toList();
// [APPLE, BANANA, CHERRY, DATE]
```

## Step 4: Chaining Operations

```java
List<String> result = items.stream()
    .filter(s -> s.length() > 4)
    .map(String::toUpperCase)
    .sorted()
    .toList();
// [APPLE, BANANA, CHERRY]
```

## Step 5: Numeric Streams

```java
int[] numbers = {3, 1, 4, 1, 5, 9, 2, 6};

int sum = Arrays.stream(numbers).sum();
int max = Arrays.stream(numbers).max().orElse(0);
double avg = Arrays.stream(numbers).average().orElse(0);

// Range:
IntStream.range(0, 10).forEach(System.out::print);  // 0123456789
IntStream.rangeClosed(1, 5).forEach(System.out::print);  // 12345
```

## Step 6: Collect to Map

```java
List<Person> people = List.of(
    new Person("Alice", 30),
    new Person("Bob", 25),
    new Person("Charlie", 35)
);

// Map by name:
Map<String, Person> byName = people.stream()
    .collect(Collectors.toMap(Person::name, Function.identity()));

// Handle duplicates:
Map<Integer, String> byAge = people.stream()
    .collect(Collectors.toMap(
        Person::age,
        Person::name,
        (existing, replacement) -> existing  // Keep first on conflict
    ));
```

## Step 7: Grouping

```java
Map<Integer, List<Person>> byAge = people.stream()
    .collect(Collectors.groupingBy(Person::age));

Map<Boolean, List<Person>> adults = people.stream()
    .collect(Collectors.partitioningBy(p -> p.age() >= 18));
```

## Step 8: Parallel Stream

```java
long count = items.parallelStream()
    .filter(s -> s.length() > 4)
    .count();
```

**Remember**: Only use parallel streams when:
1. Large dataset (10K+ elements)
2. Independent elements (no shared state)
3. CPU-bound operations (not I/O)
4. Operation dominates parallel overhead

## Step 9: Debug with peek()

```java
List<String> debugged = items.stream()
    .peek(s -> System.out.println("before filter: " + s))
    .filter(s -> s.length() > 4)
    .peek(s -> System.out.println("after filter: " + s))
    .map(String::toUpperCase)
    .peek(s -> System.out.println("after map: " + s))
    .toList();
```
