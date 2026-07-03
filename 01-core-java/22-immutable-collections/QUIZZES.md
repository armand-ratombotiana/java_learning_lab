# Quizzes: Immutable Collections

Test your knowledge of `Collections.unmodifiableList`, `List.of()`, and defensive copying.

## Quiz 1: Unmodifiable vs Immutable

**Q1: What is the primary difference between `Collections.unmodifiableList(myList)` and `List.copyOf(myList)`?**
- A) `unmodifiableList` is faster to create.
- B) `unmodifiableList` creates a read-only view; if the original `myList` is changed, the view changes too. `List.copyOf` creates a completely independent, truly immutable copy.
- C) `List.copyOf` allows nulls, while `unmodifiableList` does not.
- D) There is no difference; they are aliases for the same method.
*Answer: B*

**Q2: You have `List<String> list = List.of("A", "B")`. What happens if you call `list.add("C")`?**
- A) It returns `false`.
- B) It compiles fine, but throws an `UnsupportedOperationException` at runtime.
- C) It throws a `CompileTimeError`.
- D) It creates a new list with "A", "B", "C" and returns it.
*Answer: B*

## Quiz 2: Nulls and Duplicates

**Q1: What happens if you execute `Set.of("Apple", "Banana", "Apple")`?**
- A) It creates a Set containing ["Apple", "Banana"].
- B) It throws an `IllegalArgumentException` because `Set.of()` strictly forbids duplicate elements during creation.
- C) It creates a List instead of a Set.
- D) It throws a `NullPointerException`.
*Answer: B*

**Q2: You need to create an immutable list that contains a `null` value. Which approach must you use?**
- A) `List.of("A", null, "B")`
- B) `List.copyOf(Arrays.asList("A", null, "B"))`
- C) `Collections.unmodifiableList(Arrays.asList("A", null, "B"))`
- D) None of the above; Java forbids nulls in all collections.
*Answer: C (Java 9+ factory methods and copyOf forbid nulls. You must use the older unmodifiable view approach).*

## Quiz 3: Element Immutability

**Q1: If you have a `List<StringBuilder> list = List.of(new StringBuilder("A"))`, which of the following statements is true?**
- A) You cannot add another `StringBuilder` to the list, but you CAN append text to the existing `StringBuilder` inside the list.
- B) You cannot append text to the `StringBuilder` because the list is immutable.
- C) The compiler will not allow you to put mutable objects inside `List.of()`.
- D) You can add another `StringBuilder` to the list, but you cannot modify existing ones.
*Answer: A (The collection's structure is immutable, but the elements inside it remain mutable).*