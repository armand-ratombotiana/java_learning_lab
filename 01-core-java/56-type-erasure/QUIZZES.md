# Quizzes: Type Erasure

Test your knowledge of Type Erasure, Bridge Methods, and Reification.

## Quiz 1: The Mechanics of Erasure

**Q1: Why did the designers of Java choose to implement Generics using Type Erasure?**
- A) To make the code run faster.
- B) To reduce the size of the compiled `.class` files.
- C) To ensure strict backward compatibility with older JVMs that were built before generics existed (Java 1.4 and older).
- D) To prevent developers from using reflection.
*Answer: C*

**Q2: During compilation, what does the compiler replace an unbounded type parameter `<T>` with?**
- A) `Object`
- B) `String`
- C) The exact type provided during instantiation.
- D) It deletes the parameter entirely.
*Answer: A (Bounded types like `<T extends Number>` are replaced with `Number`).*

## Quiz 2: Consequences of Erasure

**Q1: Why is `if (obj instanceof List<String>)` a compile-time error?**
- A) Because `List` is an interface, not a class.
- B) Because at runtime, the generic type `<String>` has been erased. The JVM only knows it is a `List`, so it cannot possibly verify if it is specifically a list of Strings.
- C) Because you must use the `==` operator.
- D) Because `String` is a final class.
*Answer: B*

**Q2: What is "Heap Pollution"?**
- A) When the Garbage Collector fails to clean up memory.
- B) When you mix raw types with generic types, allowing an object of the wrong type (e.g., an `Integer`) to be inserted into a collection with a different generic reference type (e.g., `List<String>`), leading to a `ClassCastException` later.
- C) When you allocate too many objects in a short time.
- D) When a generic array is created.
*Answer: B*

## Quiz 3: Bridge Methods and Tokens

**Q1: What is the purpose of a compiler-generated "Bridge Method"?**
- A) To connect Java code to a database.
- B) To allow a subclass that specifies a generic type parameter (e.g., `class StringNode extends Node<String>`) to correctly override a method from the generic parent class after type erasure has changed the parent's method signature to use `Object`.
- C) To bypass security managers.
- D) To connect two different JVMs.
*Answer: B*