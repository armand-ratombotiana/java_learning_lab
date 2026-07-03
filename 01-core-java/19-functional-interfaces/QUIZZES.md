# Quizzes: Functional Interfaces & SAM

Test your knowledge of Java's functional programming foundations.

## Quiz 1: The SAM Concept

**Q1: What defines a Functional Interface in Java?**
- A) An interface annotated with `@FunctionalInterface`.
- B) An interface that contains exactly one abstract method (excluding methods from `java.lang.Object`).
- C) An interface that extends `java.util.function.Function`.
- D) An interface with only `default` methods.
*Answer: B (The annotation is optional but recommended; the single abstract method is the strict requirement).*

**Q2: If an interface has one abstract method and two `default` methods, is it a Functional Interface?**
- A) Yes, because default methods are not abstract.
- B) No, because it has more than one method in total.
- C) Only if it is annotated with `@FunctionalInterface`.
- D) No, default methods break the SAM rule.
*Answer: A*

## Quiz 2: Built-in Interfaces

**Q1: Which functional interface from `java.util.function` takes one argument and returns a `boolean`?**
- A) `Function<T, Boolean>`
- B) `Supplier<Boolean>`
- C) `Predicate<T>`
- D) `Consumer<T>`
*Answer: C*

**Q2: You need to pass a lambda that prints a string to the console but returns nothing. Which interface should be the target type?**
- A) `Supplier<String>`
- B) `Consumer<String>`
- C) `Function<String, Void>`
- D) `Runnable`
*Answer: B*

## Quiz 3: Edge Cases

**Q1: Why does the compiler throw an error if you try to modify an `int` variable declared outside a lambda from within the lambda?**
- A) Because lambdas run in a different thread.
- B) Because local variables captured by a lambda must be `final` or effectively final.
- C) Because you must use the `volatile` keyword.
- D) Because lambdas cannot access external variables at all.
*Answer: B*

**Q2: You have a lambda `() -> Thread.sleep(1000)`. You try to pass it to a method expecting a `Runnable`. Why does this fail to compile?**
- A) `Runnable` expects a return value.
- B) `Thread.sleep()` throws `InterruptedException` (a checked exception), but `Runnable.run()` does not declare any checked exceptions.
- C) `Runnable` is not a functional interface.
- D) You must use a method reference instead.
*Answer: B*