# Mock Interview: Pattern Matching

**Interviewer:** "Write a function that deeply flattens a nested list structure. The input can be `Integer` or `List<?>` containing more integers or lists."

**Candidate:** "I'll model the input as a sealed interface and use pattern matching:

```java
sealed interface Nested permits IntValue, NestedList {}
record IntValue(int value) implements Nested {}
record NestedList(List<Nested> items) implements Nested {}

List<Integer> flatten(Nested nested) {
    return switch (nested) {
        case IntValue(int v) -> List.of(v);
        case NestedList(var items) ->
            items.stream()
                .map(this::flatten)
                .flatMap(List::stream)
                .toList();
    };
}
```

If I can't change the input types, I can still use patterns on raw `Object`:

```java
List<Integer> flatten(Object obj) {
    return switch (obj) {
        case Integer i -> List.of(i);
        case List<?> list -> list.stream()
                .map(this::flatten)
                .flatMap(List::stream)
                .toList();
        default -> throw new IllegalArgumentException();
    };
}
```

The guarded pattern variant handles edge cases like empty lists elegantly:

```java
case List<?> list when list.isEmpty() -> List.of();
```

Pattern matching makes the code match the problem domain directly."
