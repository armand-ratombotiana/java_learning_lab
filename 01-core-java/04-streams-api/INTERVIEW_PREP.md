# Module 04: Streams API - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: Intermediate vs Terminal Operations
**Answer**: 
- **Intermediate Operations**: (e.g., `filter`, `map`, `sorted`) return a new Stream. They are *lazy*, meaning they do not execute until a terminal operation is called.
- **Terminal Operations**: (e.g., `collect`, `reduce`, `count`, `forEach`) trigger the actual execution of the stream pipeline and return a non-stream result (or `void`).

### Q2: How does Stream lazy evaluation work, and why is it beneficial?
**Answer**: 
Lazy evaluation means that computation on the source data is only performed when the terminal operation is initiated, and source elements are consumed only as needed.
**Benefit**: It enables optimizations like "short-circuiting" (e.g., `findFirst()` or `limit()`), where the stream stops processing elements as soon as the result is found, avoiding unnecessary computations on the entire dataset.

### Q3: What is the difference between `map` and `flatMap`?
**Answer**:
- `map(Function<T, R>)`: Transforms one object into exactly one other object. (e.g., mapping a `String` to its `Integer` length).
- `flatMap(Function<T, Stream<R>>)`: Transforms one object into a *Stream* of other objects, and then "flattens" all those resulting streams into a single, continuous stream. Used for un-nesting collections (e.g., taking a `List<List<String>>` and turning it into a `Stream<String>`).

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Grouping Anagrams
**Problem**: Given an array of strings, write a stream pipeline to group anagrams together. (e.g., `["eat", "tea", "tan", "ate", "nat", "bat"]` -> `[["bat"], ["nat", "tan"], ["ate", "eat", "tea"]]`)

**Solution**:
```java
public Collection<List<String>> groupAnagrams(String[] strs) {
    return Arrays.stream(strs)
        .collect(Collectors.groupingBy(str -> {
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            return new String(chars);
        }))
        .values();
}
```

### Scenario 2: Find the Longest Word
**Problem**: Given a sentence, find the longest word using Streams. Return it wrapped in an `Optional`.

**Solution**:
```java
public Optional<String> findLongestWord(String sentence) {
    return Arrays.stream(sentence.split("\\s+"))
                 .max(Comparator.comparingInt(String::length));
}
```