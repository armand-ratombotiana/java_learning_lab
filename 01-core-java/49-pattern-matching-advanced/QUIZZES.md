# Quizzes: Advanced Pattern Matching

Test your knowledge of pattern variables, switch expressions, and sealed class exhaustiveness.

## Quiz 1: Pattern Variables and Scope

**Q1: In the statement `if (obj instanceof String s && s.length() > 5)`, why is it safe to call `s.length()` on the right side of the `&&` operator?**
- A) Because the compiler automatically wraps it in a try-catch block.
- B) Because of "flow scoping". The right side of an `&&` operator is only evaluated if the left side is true. If the left side is true, the compiler guarantees that `s` has been successfully cast and initialized, so it is in scope.
- C) Because `String` is a primitive type.
- D) It is not safe; this will throw a compile error.
*Answer: B*

**Q2: What happens if you try to use `obj instanceof String s || s.length() > 5`?**
- A) It compiles perfectly.
- B) It throws a `NullPointerException`.
- C) It fails to compile because if the left side is false, the right side still executes, but `s` would not be initialized. Therefore, `s` is not in scope on the right side of an `||` operator.
- D) It creates an infinite loop.
*Answer: C*

## Quiz 2: Switch Patterns and Dominance

**Q1: When writing a pattern-matching `switch` statement, what does the compiler error "this case label is dominated by a preceding case label" mean?**
- A) You used a `default` branch incorrectly.
- B) You placed a more general type (like `Object` or a superclass) *before* a more specific type (like `String` or a subclass). The specific case can never be reached because the general case catches everything first.
- C) You forgot to use the `yield` keyword.
- D) You used a variable name that is already taken.
*Answer: B*

**Q2: How does a `switch` expression handle a `null` input in Java 21?**
- A) It falls into the `default` branch.
- B) It evaluates to `null`.
- C) It throws a `NullPointerException` unless you explicitly provide a `case null ->` branch.
- D) It skips the switch entirely.
*Answer: C*

## Quiz 3: Records and Sealed Classes

**Q1: What is the primary benefit of using a `switch` expression over a `sealed` interface instead of a regular interface?**
- A) It makes the code run faster.
- B) It allows you to omit the `default` branch if you have provided a `case` for every permitted subclass (Exhaustiveness). If a new subclass is added later, the compiler will break the switch, preventing silent runtime bugs.
- C) It automatically extracts variables.
- D) It allows multiple inheritance.
*Answer: B*