# Mock Interview Transcript: Generics

## Interviewer: Staff Engineer, Google
## Candidate: Senior Java developer
## Time: 45 minutes
## Focus: Type erasure, wildcards, PECS, type inference

---

**Q1: How does Java implement generics? Compare with C++ templates.**

**Candidate**: Java uses type erasure — generic type information is removed at compile time and replaced with casts. `List<String>` and `List<Integer>` both become `List` at runtime. C++ templates are a compile-time mechanism that generates separate code for each type. Java's approach ensures backward compatibility but means you can't use `instanceof`, create arrays of parameterized types, or use primitives directly.

**Interviewer**: What are the implications of erasure for method overloading?

**Candidate**: You can't overload methods that differ only by type parameters because erasure makes them identical at runtime:
```java
void foo(List<String> l) { }
void foo(List<Integer> l) { }  // Compile error: both have same erasure List
```

**Interviewer**: Explain PECS (Producer Extends, Consumer Super).

**Candidate**: PECS stands for "Producer Extends, Consumer Super". If you're reading items from a collection (producer), use `? extends T`. If you're writing items (consumer), use `? super T`. If you're both reading and writing, don't use wildcards.

```java
// Producer: read from source
void copy(Collection<? extends T> source, Collection<? super T> dest) {
    for (T item : source) dest.add(item);
}
```

**Interviewer**: Why can't you add to a `Collection<? extends Number>`?

**Candidate**: Because the actual type parameter could be `Integer`, `Double`, or any subtype of `Number`. The compiler doesn't know which, so it can't guarantee type safety if you add. You can only read from it (getting `Number`). Conversely, you can add to `Collection<? super Number>` because anything you add (Number or subclass) is guaranteed to be a supertype of the wildcard bound.

**Interviewer**: Write a generic method that finds the maximum element in a list.

**Candidate**:
```java
<T extends Comparable<? super T>> T max(List<? extends T> list) {
    if (list.isEmpty()) throw new IllegalArgumentException();
    T max = list.get(0);
    for (T item : list) {
        if (item.compareTo(max) > 0) max = item;
    }
    return max;
}
```

**Interviewer**: Explain the bounds. Why `T extends Comparable<? super T>`?

**Candidate**: `T extends Comparable` ensures T implements Comparable. `? super T` allows T to be compared with supertypes of T. For example, if a class implements `Comparable<Object>`, this bound allows it. The list uses `? extends T` to accept lists of subtypes of T.

**Interviewer**: What does the compiler generate for this generic method? Show the bridge method.

**Candidate**: The method's bytecode uses `Comparable.compareTo()` (raw type) and casts the result. If `MyClass implements Comparable<MyClass>`, the compiler generates a bridge method `compareTo(Object)` that calls `compareTo(MyClass)`. The bridge has a wider signature but delegates to the typed version.

**Interviewer**: Let's discuss `List<String>[]` — can you create arrays of parameterized types?

**Candidate**: No. `new List<String>[10]` doesn't compile because arrays are reified (they know their component type at runtime) but generics are erased. At runtime, you'd have a `List[]` array that could hold any `List`, breaking type safety. The workaround is `List<List<String>>` or using `ArrayList` instead of arrays.

**Interviewer**: How does `@SafeVarargs` work?

**Candidate**: `@SafeVarargs` suppresses heap pollution warnings for varargs with generic types. The compiler creates an array of the generic type for varargs, which is inherently unsafe. The annotation says "trust me, I won't misuse this array". It's used in `Arrays.asList(T...)`, `List.of(T...)`.

---

## Feedback

**Strengths**:
- Deep understanding of type erasure and its implications
- Perfect PECS explanation with examples
- Correct generic bounds for max() method
- Understands array covariance vs generics invariance

**Areas for Improvement**:
- Could mention `var` (Java 10+) for local type inference
- Should discuss `List.of()` vs `Arrays.asList()` differences

**Score**: 5/5 — Excellent generics mastery
