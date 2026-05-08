# Quizzes: Advanced Inheritance Patterns

---

## Q1: Diamond Problem

Which approach solves the diamond problem in Java?

A) Use abstract classes
B) Use interfaces with default methods
C) Use multiple extends
D) Use final classes

---

## Q2: Default Method Conflict

```java
interface A { default void show() { } }
interface B { default void show() { } }
class C implements A, B { }
```

How to resolve the conflict?

A) Compiler error
B) Use `A.show()`
C) Use `A.super.show()`
D) Override in C

---

## Q3: Composition vs Inheritance

When should you prefer composition over inheritance?

A) When you need to extend one class
B) When "has-a" relationship is clearer than "is-a"
C) When you need multiple inheritance
D) When the parent class is final

---

## Q4: Sealed Classes

What does `sealed` mean in Java 17+?

A) Class cannot be instantiated
B) Class can only be inherited by specific classes
C) Class cannot have subclasses
D) Class is abstract

---

## Answers

| Q | Answer |
|---|--------|
| 1 | B |
| 2 | C |
| 3 | B |
| 4 | B |