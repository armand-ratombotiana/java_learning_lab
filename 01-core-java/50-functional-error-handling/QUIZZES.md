# Quizzes: Functional Error Handling

Test your knowledge of the Either pattern, the Try monad, and functional error recovery.

## Quiz 1: The Problem with Exceptions

**Q1: Why are traditional Java Exceptions considered problematic in a functional programming paradigm?**
- A) They consume too much memory.
- B) They act as a `GOTO` statement, breaking the linear, declarative flow of a functional pipeline and introducing hidden control flow.
- C) They cannot be logged.
- D) They are not thread-safe.
*Answer: B*

**Q2: When should you use the `Either` monad instead of `Optional`?**
- A) When you are working with primitive data types.
- B) When you only care if data is present or absent.
- C) When an operation can fail, and you need to preserve specific information about *why* it failed (e.g., an error message or an Exception object), rather than just returning "empty".
- D) When you want to execute code concurrently.
*Answer: C*

## Quiz 2: Either and Try Mechanics

**Q1: In the standard convention for the `Either` monad, which side represents the successful result?**
- A) Left
- B) Right
- C) Both
- D) Neither
*Answer: B ("Right is right").*

**Q2: What happens if you call `.map(String::toUpperCase)` on a `Try` object that currently holds a `Failure` (an Exception)?**
- A) The `map` function throws a `NullPointerException`.
- B) The `map` function executes, but returns an empty string.
- C) The `map` function is completely ignored. The `Try` remains a `Failure` holding the original Exception, passing it down the pipeline.
- D) The application crashes.
*Answer: C*

## Quiz 3: Error Recovery

**Q1: You have a `Try<String>` that might contain a network timeout error. You want to provide a default string "Offline" if the network fails. Which method is best?**
- A) `get()`
- B) `map(e -> "Offline")`
- C) `getOrElse("Offline")` (or `recover(e -> "Offline")`)
- D) `flatMap()`
*Answer: C*