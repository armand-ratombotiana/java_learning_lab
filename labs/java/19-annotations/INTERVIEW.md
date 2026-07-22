# Interview Questions: Annotations

## Company-Specific Focus

### Google
- Creating custom annotations with the right retention policy for the right scenario
- Annotation processing at compile time vs runtime for boilerplate reduction
- Meta-annotations: @Documented, @Inherited, @Retention, @Target

### Microsoft
- Java annotation vs C# attribute: key differences
- jakarta annotations for input validation in spring
- Custom annotation for code processors

### Amazon
- Runtime annotation scanning for pluggability in the pipelines
- Dependency injection frameworks and annotations for official component scanning
- Overhead of scanning at application startup

### Meta
- Using reflection to process annotations: performance pitfalls and caching
- Retaining for performance/memory usage trade-off
- Annotation on elements: what the target conditions mean

### Apple
- Prefer source retention for standard annotation
- Permalinks to producing POJO with source generated for annotation instead of reflection
- Documenting behavior with Javadoc: an annotation vs the well-known Javadoc approach

### Oracle
- Java annotation: JLS 9.6 and 9.7 for types of annotation
- Repeatable annotation mechanism
- Custom annotation and their location: how are they stored in class files?

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 384 Shuffle an Array | Medium | Apple, Google, Amazon | Design decorator annotation model for basic behavior |
| 146 LRU Cache | Medium | Google, Apple | Design pattern by Category of annotation |
| 380 Insert Delete GetRandom O(1) | Medium | Amazon, Facebook | Similar pattern |
| 535 Encode and Decode TinyURL | Medium | Google, Expedia, Amazon | Not direct but pattern |
| 348 Design Tic-Tac-Toe | Medium | Apple, Google, Microsoft | Design of annotation to the architecture |

## Real Production Scenarios
- **Airbnb**: In one of the earliest design patterns, ROS annihilation occurred due to the heavy annotation so much scanning was done on startup
- **Fitbit**: A calendar type annotation-based permission had a type of scanning frequency on Android that made things extremely difficult
- **Uber**: Using class-level annotation and a reflection-based approach for dependency injection, the binary size grew large

## Interview Patterns & Tips
- **Annotation Target**: The TYPE, the METHOD, and the FIELD, these restricts where it is allowed
- **Retention policy**: At source vs class vs runtime. The performance overhead depends
- **Documented**: only types/annotations that good content appear in the JavaDoc.

## Deep Dive Questions
- **JVM**: How are custom annotations stored in the class file?
- **Memory**: What is the overhead of an annotation at the JVM level in the metaspace?
- **Code generation**: How can annotation processing be used to generate project code?
- **JIT**: What is the effect of large annotations processing with the JIT in the flows usage?
- **Design trade-offs**: What to use a annotation for vs what to keep as a more specific constant?