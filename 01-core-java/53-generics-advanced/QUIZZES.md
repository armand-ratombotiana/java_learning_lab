# Quizzes: Advanced Generics

Test your knowledge of Recursive Bounds, PECS, and the Self-Type Idiom.

## Quiz 1: Wildcards and PECS

**Q1: You have a method `public void process(List<? extends Number> numbers)`. Which of the following operations is valid inside this method?**
- A) `numbers.add(new Integer(5));`
- B) `numbers.add(new Double(5.0));`
- C) `Number n = numbers.get(0);`
- D) `numbers.add(new Object());`
*Answer: C (Because of `? extends`, the compiler guarantees you will get a Number out, but forbids adding anything because the actual list might be a `List<Double>`, which cannot accept an `Integer`).*

**Q2: According to the PECS rule, if a method parameter is a collection that you will ONLY write data into (a consumer of data), how should you declare it?**
- A) `List<? extends T>`
- B) `List<? super T>`
- C) `List<T>`
- D) `List<?>`
*Answer: B (Consumer Super. This allows you to pass a `List<Object>` into a method that writes `String`s).*

## Quiz 2: Bounds and Idioms

**Q1: What is the primary purpose of the Self-Type Idiom (CRTP) in Java?**
- A) To prevent a class from being subclassed.
- B) To allow a method in an abstract base class to return the exact type of the concrete subclass, enabling fluent method chaining (Builder pattern) across inheritance hierarchies.
- C) To bypass type erasure.
- D) To allow multiple inheritance.
*Answer: B*

**Q2: In the generic declaration `<T extends Comparable<T>>`, what does the recursive bound ensure?**
- A) It ensures that `T` can be compared to any other object in the system.
- B) It ensures that `T` implements the `Comparable` interface, and specifically, that instances of `T` can only be compared to other instances of the exact same type `T`.
- C) It ensures that `T` is a primitive type.
- D) It ensures that `T` is an Enum.
*Answer: B*

## Quiz 3: Edge Cases

**Q1: Why does the compiler throw an error if you try to use multiple class bounds (e.g., `<T extends String & Integer>`)?**
- A) Because `String` and `Integer` are final.
- B) Because Java does not support multiple inheritance of classes. A generic type parameter can only extend ONE class (which must be listed first), followed by any number of interfaces.
- C) Because you must use the `|` operator instead of `&`.
- D) It does not throw an error; this is valid syntax.
*Answer: B*