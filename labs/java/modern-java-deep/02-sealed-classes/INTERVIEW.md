# Interview Questions: Sealed Classes Deep Dive

## Company-Specific Focus

### Google
- Sealed classes: precise type hierarchy control with permits clause
- Sealed interfaces: permits clause for interface hierarchies
- Exhaustive pattern matching: sealed enables compile-time completeness checking

### Microsoft
- Sealed classes in Java vs C# sealed: very different — Java sealed controls subtypes, C# sealed prevents overriding
- Sealed interfaces: interface evolution control

### Amazon
- Sealed types for event models: precisely defined set of event types
- Domain modeling: sealed hierarchy prevents invalid states

### Meta
- Switch exhaustiveness: sealed + pattern matching = no default needed
- Library APIs: sealing prevents users from extending internal types

### Apple
- Immutability + sealed: closed type hierarchies ensure known subtypes
- Sealed + records: algebraic data types in Java

### Oracle
- JEP 409: Sealed Classes (Final)
- JLS 8.1.2 and 9.1.4: sealed class and interface descriptions
- Permits clause: must list all permitted subtypes
- Subtype constraints: permitted subtypes must be final, sealed, or non-sealed

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 297 Serialize and Deserialize Binary Tree | Hard | Google, Amazon | Sealed class for node types |
| 428 Serialize and Deserialize N-ary Tree | Hard | Amazon, Google | Sealed for node representations |
| 173 Binary Search Tree Iterator | Medium | Apple, Google | Sealed for iterable option |

## Real Production Scenarios
- **Stripe**: Payment method sealed class hierarchy prevented invalid payment types
- **Google**: Expression tree nodes as sealed classes guaranteed exhaustiveness

## Interview Patterns & Tips
- **Permits clause**: restricts which classes can extend a sealed class
- **Exhaustiveness**: enables complete pattern matching
- **Subtype modifiers**: permitted subtypes must be sealed, final, or non-sealed

## Deep Dive Questions
- **Class file**: How are sealed classes represented in the class file?
- **Validation**: What compile-time and runtime checks verify the permits clause?
- **Reflection**: How can you get the permitted subtypes at runtime?
- **Non-sealed**: What does the non-sealed modifier mean?
- **JIT**: How does sealing help JIT devirtualization?