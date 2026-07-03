# Collections — Practice Exercises

## Exercise 1: Word Frequency Counter

Write a method that takes a `List<String>` and returns a `Map<String, Long>` counting occurrences of each word.

```java
Map<String, Long> freq = countWords(List.of("a", "b", "a", "c", "b", "a"));
// {a=3, b=2, c=1}
```

## Exercise 2: Bounded Cache

Implement a thread-safe LRU cache using `LinkedHashMap` with a maximum size.

## Exercise 3: Set Operations

Implement `union`, `intersection`, and `difference` methods for two sets:

```java
Set<String> union(Set<String> a, Set<String> b)
Set<String> intersection(Set<String> a, Set<String> b)
Set<String> difference(Set<String> a, Set<String> b)
```

## Exercise 4: Priority Task Queue

Create a task scheduler using `PriorityQueue` where tasks have priority and scheduled time.

## Exercise 5: Collection Performance Benchmark

Write a benchmark that compares `ArrayList` vs `LinkedList` for:
- `add()` at the end (100K elements)
- `add(0, element)` at the beginning (10K elements)
- Iteration time

## Exercise 6: Group By

Given a `List<Person>`, create a `Map<Integer, List<Person>>` grouping by age.

```java
// Input: [Person("Alice",30), Person("Bob",30), Person("Charlie",25)]
// Output: {30=[Alice, Bob], 25=[Charlie]}
```

## Exercise 7: Most Frequent Element

Find the most frequently occurring element in a `List<T>`.

## Solutions

### Exercise 1

```java
public Map<String, Long> countWords(List<String> words) {
    Map<String, Long> freq = new HashMap<>();
    for (String word : words) {
        freq.merge(word, 1L, Long::sum);
    }
    return freq;
}

// Alternative with streams:
public Map<String, Long> countWords(List<String> words) {
    return words.stream()
        .collect(Collectors.groupingBy(
            Function.identity(), Collectors.counting()
        ));
}
```

### Exercise 3

```java
public Set<String> union(Set<String> a, Set<String> b) {
    Set<String> result = new HashSet<>(a);
    result.addAll(b);
    return result;
}

public Set<String> intersection(Set<String> a, Set<String> b) {
    Set<String> result = new HashSet<>(a);
    result.retainAll(b);
    return result;
}

public Set<String> difference(Set<String> a, Set<String> b) {
    Set<String> result = new HashSet<>(a);
    result.removeAll(b);
    return result;
}
```

### Exercise 6

```java
public Map<Integer, List<Person>> groupByAge(List<Person> people) {
    Map<Integer, List<Person>> grouped = new HashMap<>();
    for (Person p : people) {
        grouped.computeIfAbsent(p.age(), k -> new ArrayList<>()).add(p);
    }
    return grouped;
}

// With streams:
public Map<Integer, List<Person>> groupByAge(List<Person> people) {
    return people.stream()
        .collect(Collectors.groupingBy(Person::age));
}
```
