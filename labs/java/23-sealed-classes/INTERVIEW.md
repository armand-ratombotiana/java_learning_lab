# Interview Questions: Sealed Classes

## Beginner Questions

### Q1: What are sealed classes and why were they introduced?
Sealed classes are classes or interfaces that restrict which other classes can extend or implement them, specified through a `permits` clause. They were introduced to: (1) enable exhaustive pattern matching — the compiler knows all possible subtypes; (2) provide controlled inheritance — API designers can document and enforce which types are part of a hierarchy; (3) replace the Visitor pattern with more natural pattern matching.

### Q2: How do you declare a sealed interface?
With the `sealed` keyword and a `permits` clause: `sealed interface Shape permits Circle, Rectangle {}`. The permitted subtypes must be marked `final`, `sealed`, or `non-sealed` and must reside in the same module as the sealed interface.

### Q3: What are the three possible modifiers for a permitted subtype?
1. `final` — cannot be extended further; 2. `sealed` — can be extended but only by its own permitted subtypes; 3. `non-sealed` — opens the hierarchy to unrestricted extension.

### Q4: What is the difference between a sealed class and a final class?
A `final` class cannot be extended at all. A `sealed` class can be extended, but only by the classes listed in its `permits` clause. Sealed provides controlled extension; final provides no extension.

## Intermediate Questions

### Q5: How do sealed classes enable exhaustive pattern matching?
The compiler collects all leaf types of a sealed hierarchy (all `final` subtypes, and for `non-sealed` types it falls back to the supertype). When you write a switch expression over the sealed type, the compiler checks that every leaf type has a corresponding case. If a new subtype is added to the permits clause, all existing switch expressions become non-exhaustive until updated.

### Q6: How would you model an algebraic data type using sealed classes and records?
An ADT combines sum types (sealed) with product types (records). For example: `sealed interface Expr permits Constant, Add, Multiply {}` is a sum type (Expr is either Constant OR Add OR Multiply). Each implementation is a record (product type) with its own fields. This enables exhaustive pattern matching over the expression tree.

### Q7: What happens at the JVM level when a class tries to extend a sealed class it's not permitted to?
The JVM class loader verifies the `PermittedSubclasses` attribute. If the extending class is not in the list, the JVM throws a `VerifyError`. This provides runtime protection even if someone tries to bypass the compiler using bytecode manipulation.

### Q8: How would you design a sealed hierarchy that balances control and extensibility for a library?
Use a sealed core type with a mix of `final` subtypes (for built-in implementations) and a `non-sealed` subtype (for user extensions). For example: `sealed interface AuthenticationProvider permits OAuthProvider, LDAPProvider, CustomProvider {}` where `CustomProvider` is `non-sealed`. Users extend `CustomProvider`, not the sealed base type.

## Advanced Questions

### Q9: How does the Java compiler verify exhaustiveness for a sealed type across multiple packages within the same module?
The compiler builds a complete picture of the sealed hierarchy by scanning all permitted subtypes, recursively collecting their permitted subtypes (if they're `sealed`), until it reaches `final` or `non-sealed` types. For packages within the same module, the compiler has full access to all types during compilation. For `non-sealed` subtypes, the compiler cannot know future extensions and reverts to requiring a `default` case or covering the `non-sealed` type itself.

### Q10: Compare Java's sealed classes with Scala's sealed classes and Kotlin's sealed classes.
All three provide sum types with exhaustiveness checking. Key differences: (1) Java requires explicit `permits` clause; Scala infers sealed subtypes from the compilation unit; Kotlin requires subtypes to be nested or in the same file. (2) Java allows `non-sealed` subtypes; Scala and Kotlin require all subtypes to be `final` (or `sealed` in Kotlin's case). (3) Java's sealed integrates with the module system; Scala and Kotlin work at the file/package level.

### Q11: How would you migrate a large codebase to use sealed classes? What are the risks?
Steps: (1) Identify hierarchies that are de facto sealed (documented but not enforced). (2) Check that all subtypes are in the same module. (3) Add the `sealed` keyword and `permits` clause to the base type. (4) Add modifiers to subtypes. (5) Fix exhaustiveness errors in switch expressions. (6) Remove default cases where possible. Risks include: external code that extends the hierarchy outside the module (which won't be caught until runtime), and binary compatibility concerns if the sealed hierarchy is part of a public API.

### Q12: How do sealed classes interact with the module system?
Sealed types and their permitted subtypes must be in the same module. The module's `exports` directive controls visibility of the sealed type. The sealed type can be exported while still restricting extension (external code can use but not extend it). This enables API designers to publish a type hierarchy for consumption while reserving the right to add new subtypes in future versions.
