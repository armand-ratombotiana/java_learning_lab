# Module 14: Reflection & Introspection - Quizzes

---

## Q1: Getting a Class Object
Which of the following is NOT a valid way to obtain a `Class` object?

A) `String.class`
B) `"Hello".getClass()`
C) `Class.forName("java.lang.String")`
D) `new Class("java.lang.String")`

**Answer**: D
**Explanation**: `Class` objects are constructed automatically by the JVM. You cannot instantiate them directly using the `new` keyword.

---

## Q2: Bypassing Access Modifiers
Which method must be called to modify a `private` field via reflection?

A) `setPublic(true)`
B) `setAccessible(true)`
C) `setModifiable(true)`
D) `overrideAccess(true)`

**Answer**: B
**Explanation**: Calling `field.setAccessible(true)` suppresses Java language access checking.

---

## Q3: Reflection Performance
Why is reflection generally slower than direct code execution?

A) It runs in a separate thread.
B) The JVM cannot perform typical optimizations like inlining.
C) It must communicate over the network.
D) It converts Java bytecodes to C++.

**Answer**: B
**Explanation**: Reflection involves dynamic resolution at runtime, preventing the JVM from performing static analysis and JIT optimizations like method inlining.