# Quizzes: Sealed Types

Test your knowledge of the `sealed` modifier, `permits` clauses, and exhaustive pattern matching.

## Quiz 1: The Basics of Sealing

**Q1: What is the primary purpose of the `sealed` modifier on a class or interface?**
- A) To prevent the class from being instantiated.
- B) To encrypt the bytecode of the class.
- C) To explicitly restrict which other classes or interfaces are allowed to extend or implement it.
- D) To prevent the class from being serialized.
*Answer: C*

**Q2: If class `A` is a permitted subclass of a `sealed` class `B`, what modifier MUST class `A` declare?**
- A) `public` or `private`
- B) `abstract`
- C) It must choose exactly one of: `final`, `sealed`, or `non-sealed`.
- D) `static`
*Answer: C (This ensures the compiler knows exactly how the inheritance hierarchy continues or terminates).*

## Quiz 2: Rules and Restrictions

**Q1: You create `public sealed class Parent permits Child` in package `com.example`. You create `public final class Child extends Parent` in package `com.example.sub`. You are NOT using the Java Module System (`module-info.java`). What happens?**
- A) It compiles perfectly.
- B) The compiler throws an error because in an unnamed module, permitted subclasses must reside in the exact same package as the sealed parent.
- C) It throws a `SecurityException` at runtime.
- D) It compiles, but throws a `ClassNotFoundException`.
*Answer: B*

**Q2: Which of the following automatically satisfies the requirement for a subclass modifier without needing an explicit `final`, `sealed`, or `non-sealed` keyword?**
- A) An Enum class.
- B) A Java Record.
- C) An Abstract class.
- D) An Interface.
*Answer: B (Java Records are implicitly `final`, so they satisfy the requirement automatically).*

## Quiz 3: Exhaustiveness

**Q1: What is "Exhaustiveness Checking" in the context of a `switch` expression over a sealed interface?**
- A) The compiler checks if the `switch` statement takes too long to execute.
- B) The compiler verifies that every possible permitted subclass of the sealed interface has a corresponding `case` branch. If they are all covered, the compiler does not require a `default` branch.
- C) The compiler checks if all variables are used.
- D) The JVM checks for memory leaks.
*Answer: B*