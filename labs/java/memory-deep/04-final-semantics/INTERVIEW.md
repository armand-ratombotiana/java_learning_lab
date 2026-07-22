# Interview Questions: Final Field Semantics

## Company-Specific Focus

### Google
- Final fields: JMM guarantees safe initialization of final fields
- Constructor safety: final fields are visible to all threads after constructor completes
- JMM rule: freeze action at end of constructor for each final field

### Microsoft
- Final in Java vs readonly in C#: similar initialization guarantees
- Immutability: final fields enable safe publication of immutable objects

### Amazon
- Immutable objects: leveraging final fields for thread-safe value objects
- Deserialization: preserving final field guarantees during deserialization

### Meta
- Escape in constructor: if the object escapes during construction, final field guarantee may not hold
- Final array: the reference is final, but array elements are not

### Apple
- Records: all fields are final — safe publication by design
- Defensive copy: final reference to a mutable object does not make the object immutable

### Oracle
- JLS 17.5: Final Field Semantics
- Freeze action: at end of constructor for final fields
- JMM guarantee: dereferencing the object after construction ensures final fields are initialized
- Constructor visibility: happens-before between constructor freeze and subsequent dereferences

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — final field semantics is a JMM aspect) |

## Real Production Scenarios
- **LinkedIn**: Immutable value objects with final fields solved a race condition in caching
- **Uber**: Final field in service configuration ensured safe publication across threads

## Interview Patterns & Tips
- **Safe publication**: final fields are the easiest way to safely publish objects
- **Constructor escape**: don't leak `this` reference in constructor
- **Immutability**: final field + immutable class = thread safety

## Deep Dive Questions
- **Freeze**: What is the freeze action in the JMM?
- **Constructor escape**: When does the final field guarantee fail?
- **Final array**: Are array elements guaranteed to be visible?
- **Deserialization**: How does deserialization preserve final field semantics?
- **Reflection**: Can reflection change final fields at runtime?