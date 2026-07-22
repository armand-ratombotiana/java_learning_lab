# Interview Questions: Generics

## Company-Specific Focus

### Google
- Generics during collection hierarchy: reification and type safety at the parameter level
- Type erasure: how the compiler removes generic info and what remains at runtime
- Heap pollution: what it is, when it occurs, and how to minimize its risk

### Microsoft
- Java's type erasure vs .NET's reified generics — design trade-offs
- Wildcard bounds (upper/lower) in Java - does .NET have equivalence?
- Use cases and constraints for Java generics Goetz-style

### Amazon
- Creating type-safe containers for many different data types in microservices
- Type token pattern: capturing generic type at runtime using Super Type Token
- Wildcards in method parameter to increase flexibility

### Meta
- PECS rule: Producer Extends, Consumer Super
- Reified generic question: how to maintain generic constraints across serialization
- How generic's method overloading works at compile versus runtime

### Apple
- Using final class to simulate union types along with generic constraints
- Generifying utility classes while still staying backward compatible
- Covariance of arrays (Object[] -> String[]) and non-covariance of generics List

### Oracle
- JLS 4.5: Parameterized Types, Bounded Type Parameters, API
- Type erasure details in JLS
- Bridge methods for overriding methods with generic type parameters
- The `@SuppressWarnings("unchecked")` annotation's role for making checked warnings go away

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 380 Insert Delete GetRandom O(1) | Medium | Amazon, Google, Apple | Generic class design |
| 146 LRU Cache | Medium | Apple, Google, Microsoft | A generic approach for capacities |
| 208 Implement Trie | Medium | Amazon, Google | Generic nodes |
| 297 Serialize and Deserialize Binary Tree | Hard | Google, Amazon | Generic data transformation with generics |
| 758 Bold Words in String | Easy | Apple, Google | For generic search query |

## Real Production Scenarios
- **LinkedIn**: A generic Mapper class meant to take only List profiles threw ClassCastException because the Producer (Supplier) was returning List of wrong data Type.
- **Pinterest**: The type token patterns for translating objects to and from a database resulted in several hidden ClassCastException failures.
- **Spotify**: The generics in a data pipeline threw a casting exception (cast to String) because of unchecked warnings that had been ignored.

## Interview Patterns & Tips
- **Generics are invariant**: The List is not a subtype of List even if Dog is a subtype of Animal.
- **Wildcards**: ? extends T gives covariance (read-only), ? super T gives contravariance (write-only).
- **Unbounded wildcard**: Use Set when you don't care about the generic type.
- **Cannot actually instantiate generic type directly**: Not permitted by type system.
- **Cannot create arrays of Type Parameters**: But can create new instance using Array.newInstance.
- **Correct type check for a generic list by reified type only with the help of an actual argument**.

## Deep Dive Questions
- **JVM**: At runtime, does the JVM know about generic types? What does the class file contain regarding generics (Signature attribute)?
- **Compilation**: How does the compiler implement type erasure? How it generates synthetic bridge methods? What are the overs of covariant return?
- **JIT**: How can the lack of reified generics prevent certain JIT optimizations (like specialized code for the element type)?
- **Memory Model**: Does type erasure have any impact on garbage collection or object header size?
- **Advanced JVM**: How does the JVM handle compare of a List with an array type for the ELement type? (Many raw types considered)

