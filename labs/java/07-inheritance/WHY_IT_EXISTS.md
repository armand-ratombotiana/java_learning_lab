# Why Inheritance Exists

## The Problem Inheritance Solves

Without inheritance, code reuse requires copying and pasting. If `Cat` and `Dog` both need `eat()` and `sleep()`, those methods must be duplicated or delegated to helper classes. Inheritance establishes an IS-A hierarchy where common behavior is defined once in a superclass and inherited by subclasses.

## Historical Context

Inheritance was a hallmark of early OOP languages. Simula introduced class hierarchies. C++ added multiple inheritance. Java simplified the model:
- **Single implementation inheritance**: One superclass only — no diamond problem
- **Interface inheritance**: Multiple interfaces allowed for type polymorphism
- **`final` control**: Classes and methods can opt out of inheritance

The `super` keyword provides explicit access to the superclass — essential when subclass methods override superclass behavior but still need to invoke it. The `@Override` annotation (Java 5) catches signature mismatches at compile time.

Java's designers saw inheritance as a powerful tool but also a dangerous one. The `final` keyword lets API designers prevent subclassing when it would break invariants (e.g., `String` is final for security and correctness).
