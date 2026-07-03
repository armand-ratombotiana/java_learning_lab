# Module 24: Functional Programming - Edge Cases & Pitfalls

---

## Pitfall 1: Impure Functions and Side Effects

### ❌ Wrong
Writing lambdas or functions that modify external state. This breaks the predictability of functional programming and makes parallelization dangerous.
```java
int total = 0;
List<Integer> numbers = Arrays.asList(1, 2, 3);
numbers.forEach(n -> total += n); // ❌ Modifies external state (not even compiling if total isn't effectively final)
```

### ✅ Correct
Use pure functions and reducers to compute results.
```java
List<Integer> numbers = Arrays.asList(1, 2, 3);
int total = numbers.stream().reduce(0, Integer::sum); // ✅ Pure function
```

---

## Pitfall 2: Overusing Optional

### ❌ Wrong
Using `Optional` as a parameter to methods or constructors, or wrapping collections in `Optional`.
```java
// Anti-pattern
public void printUser(Optional<User> user) {
    user.ifPresent(u -> System.out.println(u.getName()));
}
```

### ✅ Correct
`Optional` should primarily be used as a return type to indicate that a value might be missing. Collections should simply be empty, not Optional.
```java
public void printUser(User user) {
    if (user != null) {
        System.out.println(user.getName());
    }
}
```

---

## Pitfall 3: Not Understanding Lazy Evaluation

### ❌ Wrong
Assuming Stream operations are executed immediately as they are declared.
```java
Stream<Integer> stream = numbers.stream().peek(System.out::println);
// Nothing prints here!
```

### ✅ Correct
Streams are lazy. Intermediate operations are only executed when a terminal operation is invoked.
```java
numbers.stream().peek(System.out::println).count(); // Now it executes
```