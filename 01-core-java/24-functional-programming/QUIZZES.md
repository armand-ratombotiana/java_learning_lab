# Module 24: Functional Programming - Quizzes

---

## Q1: Pure Functions
Which of the following describes a "pure function"?

A) A function that uses multiple threads.
B) A function that takes no parameters.
C) A function that produces the same output for the same input and has no side effects.
D) A function that returns void.

**Answer**: C
**Explanation**: Pure functions are deterministic and do not mutate any external state, which makes them inherently thread-safe and predictable.

---

## Q2: Higher-Order Functions
What is a higher-order function?

A) A function that is marked with `@Override`.
B) A function that takes another function as an argument, returns a function, or both.
C) A function that calls itself recursively.
D) A function that belongs to a superclass.

**Answer**: B
**Explanation**: Higher-order functions treat functions as first-class citizens, meaning they can accept functions as inputs or output them as results (e.g., `Stream.map(Function)`).

---

## Q3: Optional Usage
According to standard Java functional programming practices, what is the primary intended use case for `java.util.Optional`?

A) To wrap collections instead of returning empty lists.
B) To be used as a parameter type for optional method arguments.
C) To act as a return type for methods that might not return a value, explicitly preventing NullPointerExceptions.
D) To replace all usage of `null` everywhere in the codebase.

**Answer**: C
**Explanation**: `Optional` was designed primarily as a return type to explicitly signal the possibility of a missing value, forcing the caller to handle the absence safely.