# Quizzes: Custom Collections Implementation

Test your knowledge of collection internals, Big O notation, and fail-fast mechanics.

## Quiz 1: The Abstract Classes

**Q1: If you want to create a custom, unmodifiable `List` backed by an array, which class is the best to extend, and which methods MUST you implement?**
- A) Extend `AbstractList` and implement `get(int index)` and `size()`.
- B) Extend `AbstractCollection` and implement `iterator()` and `size()`.
- C) Extend `ArrayList` and override `add()`.
- D) Extend `AbstractList` and implement `add(E e)` and `remove(int index)`.
*Answer: A (AbstractList provides the iterator implementation based on your `get` and `size` methods).*

**Q2: What is the primary purpose of extending `AbstractSet` instead of just implementing the `Set` interface directly?**
- A) `AbstractSet` provides a faster `add()` method.
- B) `AbstractSet` provides standard, specification-compliant implementations of `equals()` and `hashCode()` that ensure your custom set behaves correctly when compared to other sets (like `HashSet`).
- C) `AbstractSet` automatically synchronizes the collection for thread safety.
- D) `AbstractSet` allows you to store duplicate elements.
*Answer: B*

## Quiz 2: Iterator Mechanics

**Q1: What is the purpose of the `modCount` variable used in standard Java collections?**
- A) It tracks the number of times the collection is read to optimize caching.
- B) It tracks structural modifications to the collection to support "fail-fast" iterators, throwing a `ConcurrentModificationException` if the collection is modified while an iterator is running.
- C) It counts the number of elements in the collection.
- D) It prevents memory leaks.
*Answer: B*

**Q2: When implementing a custom `remove(int index)` method for an array-backed list, why is it critical to explicitly set the last array element to `null` after shifting elements?**
- A) To prevent a `NullPointerException`.
- B) To satisfy the `List` interface contract.
- C) To prevent a memory leak, ensuring the Garbage Collector can reclaim the object that was removed.
- D) To reset the `modCount`.
*Answer: C*

## Quiz 3: Performance and Generics

**Q1: Why does Java forbid the creation of generic arrays (e.g., `new E[10]`)?**
- A) Because arrays are covariant, but generics are invariant, leading to type safety issues at runtime due to type erasure.
- B) Because generic arrays consume too much memory.
- C) Because it is impossible to garbage collect a generic array.
- D) Because the syntax is too complex for the compiler to parse.
*Answer: A*