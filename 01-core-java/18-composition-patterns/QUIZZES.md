# Quizzes: Composition Patterns

Test your knowledge of Decorator, Composite, and Delegation patterns.

## Quiz 1: Composition vs Inheritance

**Q1: What does the principle "Favor Composition over Inheritance" primarily aim to solve?**
- A) It makes the code run faster.
- B) It reduces the memory footprint of the application.
- C) It prevents the "Fragile Base Class" problem and class explosion by allowing behaviors to be assembled dynamically at runtime.
- D) It allows multiple inheritance in Java.
*Answer: C*

**Q2: In Java, how is a "has-a" relationship typically implemented?**
- A) By using the `extends` keyword.
- B) By implementing an interface.
- C) By declaring an instance variable of one class inside another class.
- D) By using the `@Composition` annotation.
*Answer: C*

## Quiz 2: The Decorator Pattern

**Q1: Which of the following is a classic example of the Decorator pattern in the standard Java library?**
- A) `java.util.Collections.sort()`
- B) `java.io.BufferedInputStream(new java.io.FileInputStream(...))`
- C) `java.lang.String`
- D) `java.util.ArrayList`
*Answer: B*

**Q2: Structurally, what is required for a class to be a Decorator?**
- A) It must extend the class it is decorating.
- B) It must implement the same interface as the object it decorates AND hold a reference to an instance of that interface.
- C) It must be marked as `final`.
- D) It must use Reflection to intercept method calls.
*Answer: B*

## Quiz 3: The Composite Pattern

**Q1: What is the primary purpose of the Composite pattern?**
- A) To compose a complex string from multiple smaller strings.
- B) To allow clients to treat individual objects and compositions of objects uniformly (e.g., treating a single File and a Directory of files exactly the same way).
- C) To prevent an object from being modified.
- D) To delay the initialization of an object until it is needed.
*Answer: B*

**Q2: What is a major design trade-off when implementing the Component interface in the Composite pattern?**
- A) Deciding between memory efficiency and CPU speed.
- B) Deciding whether to include child-management methods (like `add()` and `remove()`) in the base interface (Uniformity) or only in the Composite class (Type Safety).
- C) Deciding whether to use XML or JSON for serialization.
- D) Deciding between single or multiple inheritance.
*Answer: B*