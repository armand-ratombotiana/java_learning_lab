# Interview Questions: Sealed Classes

## Company-Specific Focus

### Google
- Sealed classes as a precise modeling tool for domain hierarchies
- Exhaustive pattern matching: sealed classes enable compile-time completeness checking
- Permits clause: controlling which subtypes may extend the sealed class

### Microsoft
- Sealed classes vs C# sealed methods/keywords: not the same concept
- Sealed interfaces: multi-level sealed hierarchy with precise control
- Package level and module sealed classifications

### Amazon
- Sealed interfaces for service contract modeling: limited to known subtypes only
- Sealed classes as expression tree modeling in the rule engine
- Works with JSON schema to domain model mapping: preventing unknown subtypes

### Meta
- Final subtype of sealed classes: what are the common patterns to use
- Sealed vs enums: when to use one over the other for ordered sets
- Refactoring an unsealed hierarchy to sealed — migration steps

### Apple
- Exhaustive type analysis: using all places in code to handle each alternative.
- Immutability and sealed: immutable structure of the data model
- Nested sealed hierarchy with records

### Oracle
- JEP 409 (Java 17): sealed classes and sealed interfaces for sealed types
- JLS 8.1.2 and 9.1.4: sealed class description
- The permits clause must contain all extending classes
- JVM and sealed: permit annotations in the class file

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 297 Serialize and Deserialize Binary Tree | Hard | Google, Amazon | Sealed class for tree node type wrapper |
| 428 Serialize and Deserialize N-ary Tree | Hard | Amazon, Google | Sealed class for serialized node representations |
| 173 Binary Search Tree Iterator | Medium | Apple, Google | Sealed for tree node subtype |
| 235 Lowest Common Ancestor of BST | Easy | Microsoft, Amazon, Apple | Sealed class for tree node |
| 449 Serialize and Deserialize BST | Medium | Amazon, Google | Sealed for tree node representations |

## Real Production Scenarios
- **Google**: A sealed class hierarchy for the evaluation nodes in a dynamic formula evaluator allowed the compiler to check for any missing cases
- **Stripe**: Payment method modeling using sealed types made sure no third party could create a false payment method type
- **Uber**: TripStatus became a sealed class with a process method for each state, and any missing case was a compile error

## Interview Patterns & Tips
- **Exhaustiveness**: Sealed classes allow switch to be exhaustive without default.
- **No reflection attack**: At runtime, only permitted subtypes can subclass; any attempt to create a new subclass fails
- **Prefer sealed interface for contract definition**: allows multiple interface implementations

## Deep Dive Questions
- **JVM**: How does the class file format of a sealed class store permit list?
- **Validation**: What does the compiler and JVM check for sealed permit consistency?
- **Reflection**: Given a sealed class, can you find part of the permits with reflection?
- **JIT**: In relation to devirtualization, what value does sealed create for the JIT?
- **Pattern matching with sealed**: What is the main effect of sealed + pattern matching for the purpose of switch?