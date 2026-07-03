# Why Abstraction & Interfaces Exist

## The Problem Abstraction Solves

When building large systems, implementation details change frequently. Without abstraction, changing a database library means updating every file that uses it. Abstraction defines stable contracts that implementations fulfill, allowing implementations to change without affecting callers.

## Historical Context

Abstract classes existed in C++ (pure virtual functions). Java made them explicit with the `abstract` keyword. Interfaces, however, were Java's distinctive contribution — a pure contract mechanism with no implementation inheritance.

Key motivations for Java interfaces:
- **Multiple type inheritance**: Java forbids multiple class inheritance but allows multiple interface implementation
- **Callback mechanism**: Interfaces like `Runnable` and `ActionListener` provide type-safe callbacks
- **API contracts**: `Serializable`, `Comparable`, `Cloneable` define capabilities without implementation

The addition of default methods (Java 8) was revolutionary — interfaces could now evolve without breaking existing implementations. This enabled the Stream API by adding stream() to Collection. Static methods in interfaces (also Java 8) replaced the common pattern of companion utility classes (e.g., `Collections` companion to `Collection`).
