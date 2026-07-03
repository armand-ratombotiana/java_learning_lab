# Edge Cases & Pitfalls: Immutable Collections

While immutable collections solve many problems, they introduce specific traps regarding null values, serialization, and the difference between views and true immutability.

## 1. The "Unmodifiable View" Mutation Trap
*   **The Scenario**: You use `Collections.unmodifiableList(myList)` to protect a list before passing it to untrusted code.
*   **The Pitfall**: The untrusted code cannot call `add()` on the view, but if your own code (or another thread) modifies the *original* `myList`, the untrusted code will see the changes. This can lead to `ConcurrentModificationException` if the untrusted code is iterating over the view while you modify the backing list.
*   **Mitigation**: If you need a truly safe snapshot that will never change, use `List.copyOf(myList)` (Java 10+) or create a new collection wrapped in an unmodifiable view: `Collections.unmodifiableList(new ArrayList<>(myList))`.

## 2. Null Elements in Java 9+ Factory Methods
*   **The Scenario**: You are building an immutable list from data that might contain nulls: `List.of("A", null, "C")`.
*   **The Pitfall**: The Java 9 factory methods (`List.of`, `Set.of`, `Map.of`) strictly **forbid null values**. Attempting to pass a null will immediately throw a `NullPointerException`.
*   **Mitigation**: If you absolutely must store nulls in an immutable collection (which is generally bad practice), you must use the older `Collections.unmodifiableList(Arrays.asList("A", null, "C"))`.

## 3. The `Set.of()` Duplicate Trap
*   **The Scenario**: You use `Set.of(a, b, c)` to create a quick set.
*   **The Pitfall**: Standard sets (like `HashSet`) silently ignore duplicate elements. However, `Set.of()` is designed to catch programming errors early. If you pass duplicate elements to `Set.of("A", "B", "A")`, it will throw an `IllegalArgumentException` at runtime.
*   **Mitigation**: If you are generating a set from a source that might contain duplicates, do not use `Set.of()`. Instead, use streams: `source.stream().collect(Collectors.toUnmodifiableSet())`.

## 4. `Map.of()` vs `Map.ofEntries()`
*   **The Scenario**: You want to create an immutable map with 15 key-value pairs.
*   **The Pitfall**: `Map.of()` only accepts up to 10 key-value pairs (20 arguments total). If you try to pass 11 pairs, the code won't compile.
*   **Mitigation**: For maps larger than 10 entries, you must use `Map.ofEntries()`.
    ```java
    import static java.util.Map.entry;
    Map<Integer, String> map = Map.ofEntries(
        entry(1, "A"), entry(2, "B"), /* ... up to N entries ... */ entry(15, "O")
    );
    ```

## 5. Serialization of Java 9+ Collections
*   **The Pitfall**: The objects returned by `List.of()`, `Set.of()`, etc., are highly specialized, private internal classes (e.g., `java.util.ImmutableCollections$List12`). While they are `Serializable`, their exact class names and structures are not part of the public Java API and can change between JDK releases.
*   **Mitigation**: If you serialize these objects in Java 11 and try to deserialize them in Java 21, it might fail. For long-term persistent serialization across different JVM versions, it is safer to serialize standard `ArrayList`s and wrap them in unmodifiable views upon deserialization.