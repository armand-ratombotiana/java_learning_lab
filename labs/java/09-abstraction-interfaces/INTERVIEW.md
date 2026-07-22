# Interview Questions: Abstraction & Interfaces

## Company-Specific Focus

### Google
- Abstract classes vs interfaces: semantic differences and appropriate use cases
- Interface evolution: adding methods with default implementations vs abstract class versioning
- Functional interfaces with @FunctionalInterface annotation

### Microsoft
- Interface with default methods is comparable to C# 8+ default method implementations
- Abstract classes for template patterns, interfaces for cross-cutting concerns
- Marker interfaces (Serializable, Cloneable) philosophy

### Amazon
- Programming to an interface: minimizing coupling for services and repositories
- Sealed interfaces in Java 17+ for controlling permitted subtypes
- Interface with constants (static final fields) — is it an antipattern?

### Meta
- Interfaces vs abstract classes: when to choose one over the other
- @FunctionalInterface compiler checking and lambda compatibility
- Limiting inheritance by designing sealed interfaces

### Apple
- Prefer interfaces as contracts, abstract classes as partial implementations
- Using interfaces for ability grouping and lowering class coupling
- Immutable in an interface context

### Oracle
- JLS 9: Interface declarations and types
- Interface static methods and their JVM binding
- Default methods — purpose, resolution rules, and the C state problem
- Functional interface in the JLS: exactly one abstract method (SAM)

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 208 Implement Trie (Prefix Tree) | Medium | Google, Amazon, Microsoft | Interface design for tree methods |
| 284 Peeking Iterator | Medium | Google, Apple, Facebook | Interface extension with default methods |
| 211 Design Add and Search Words Data Structure | Medium | Google, Amazon, Microsoft | Interface interaction with implementations |
| 642 Design Search Autocomplete System | Hard | Microsoft, Amazon, Google | Abstract class for template behavior |
| 348 Design Tic-Tac-Toe | Medium | Google, Microsoft | Interface for external abstraction |

## Real Production Scenarios
- **Stripe**: Library change to add a new method to an interface broken 200+ integrations; default method allowed a phased rollout
- **LinkedIn**: Abstract class used for DAOs — third class in hierarchy resulted in diamond problem with repeated state; refactored to interfaces
- **Etsy**: Adding a new function to API interface without altering existing implementations was allowed by the default keyword

## Interview Patterns & Tips
- **Abstract class**: Use when classes share a common state and behavior
- **Interface**: Use for defining a contract of behavior (what a class can do)
- **Default method**: Introduced to allow API evolution without breaking implementations
- **Static method in interface**: By JVM's invokestatic and does not have access to object state
- **SAM interface**: Functional interface gives alternative of lambda expression
- **Member declarations**: Interfaces can have public static final fields and public abstract methods by default

## Deep Dive Questions
- **JVM**: How does the itables (interface method table) differ from vtable? How does invokeinterface resolve a particular method?
- **Bytecode**: What is the bytecode for a default method? Is it attached to the interface's class definition?
- **Class loading**: When is an interface's static initializer called? When are default methods initialized?
- **JIT**: How does the JIT handle i-cache for multiple interfaces with the same method signature?
- **Java 8+**: Evolution of interfaces from purely abstract contracts to providing concrete behavior