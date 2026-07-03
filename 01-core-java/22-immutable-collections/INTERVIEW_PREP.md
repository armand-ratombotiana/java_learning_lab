# Interview Preparation: Immutable Collections

This document covers advanced questions related to unmodifiable views, Java 9 factory methods, and deep vs. shallow immutability.

## Q1: Explain the difference between `Collections.unmodifiableList()` and `List.copyOf()`.
**Answer:**
*   `Collections.unmodifiableList(list)` returns a read-only **view** of the original list. If you attempt to call `add()` on the view, it throws an `UnsupportedOperationException`. However, it holds a reference to the original list. If the original list is mutated elsewhere, those changes are immediately visible through the unmodifiable view.
*   `List.copyOf(list)` (introduced in Java 10) creates a **truly immutable copy**. It iterates through the provided list and stores the elements in a new, internal immutable data structure (like `List12` or `ListN`). Changes to the original list do not affect the copy. (Note: If the provided list is already an immutable collection from `List.of()`, `copyOf` is smart enough to just return the reference without copying data).

## Q2: What are the restrictions on elements when using Java 9 factory methods like `List.of()` or `Set.of()`?
**Answer:**
1.  **No Nulls**: They strictly forbid `null` elements. Passing `null` throws a `NullPointerException` immediately. This was a deliberate design choice to prevent null-related bugs later in the application lifecycle.
2.  **No Duplicates (for Sets/Maps)**: `Set.of("A", "A")` or `Map.of("K", "V1", "K", "V2")` will throw an `IllegalArgumentException` at runtime. Standard sets/maps silently ignore or overwrite duplicates, but the factory methods treat duplicates as programming errors.

## Q3: If you have a `List<Employee>` created with `List.of()`, is the data completely safe from modification?
**Answer:**
No. This highlights the difference between **structural immutability** and **element immutability** (deep vs. shallow immutability).
`List.of()` guarantees structural immutability: you cannot add, remove, or replace the `Employee` references in the list. However, if the `Employee` class itself is mutable (e.g., it has a `setSalary()` method), any code that holds a reference to the list can retrieve an `Employee` and modify its internal state. To achieve complete safety, the list must be immutable *and* the elements inside it must be immutable objects (like Java Records or Strings).

## Q4: Why is the iteration order of `Set.of()` and `Map.of()` randomized in Java 9+?
**Answer:**
The iteration order of standard `HashSet` and `HashMap` is undefined but often appears predictable based on object hash codes. Developers sometimes accidentally wrote code that relied on this specific order, which would break if the hashing algorithm changed in a future JDK release.
To prevent this "accidental reliance on undefined behavior," the creators of Java 9 specifically randomized the iteration order of `Set.of()` and `Map.of()` from run to run. This forces developers to write robust code that does not depend on iteration order unless they explicitly use an ordered collection like `TreeSet`.

## Q5: How would you safely expose an internal mutable `List` to a caller via a getter method?
**Answer:**
You should never return the reference to the internal mutable list directly, as the caller could clear or modify it, breaking encapsulation.
*   **Pre-Java 10 approach**: Return `Collections.unmodifiableList(this.internalList)`. This prevents the caller from modifying the list, but if your class modifies the list later, the caller will see those changes (which might be desired or undesired).
*   **Java 10+ approach**: Return `List.copyOf(this.internalList)`. This gives the caller a safe, immutable snapshot of the list at that exact moment in time.