# Interview Questions: Enums

## Company-Specific Focus

### Google
- Enums as more than constants: instance fields, methods, abstract methods per constant
- EnumSet and EnumMap: specialized, compact, performant implementations
- Using enumerated values in switch expressions for exhaustive matching in Java 21

### Microsoft
- Enum vs C# enum: Java enums are objects, C# enums are value types. Why?
- Strategy pattern using enum: clean code for handling each possible value
- Ordinal: why it exists and why it should not be used for persistence

### Amazon
- Enum for status modeling in state machine: internal state transitions in the order lifecycle
- Using enums constants for configuration key constants
- Persistence: use name() or ordinal? Why neither is ideal

### Meta
- Thread safe constants: enum constants are singletons, safe by construction
- Abstract method per enum constant: why this eliminates many if-else chains
- Enum.valueOf vs name/ordinal comparison

### Apple
- Equality guarantee: enums are singletons; == works correctly
- Idea of using an enum for the Optionals pattern
- EnumMap: memory and performance compared to regular HashMap

### Oracle
- JLS 8.9: Enum Types: type safety from the compiler
- Enums extend java.lang.Enum: you cannot extend anything else
- Enum to JVM: each constant is a singleton class in the JVM

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 851 Loud and Rich | Medium | Amazon, Apple | Enum for status modeling |
| 773 Sliding Puzzle | Hard | Google, Amazon | Tile direction as enum |
| 909 Snakes and Ladders | Medium | Amazon, Apple, Google | Direction enum |
| 1162 As Far from Land as Possible | Medium | Google, Amazon | Quad directions |
| 200 Number of Islands | Medium | Amazon, Google, Apple | Direction control |

## Real Production Scenarios
- **Airbnb**: Adding a new enum value caused the service to go down because of a case of a switch without a default branch
- **Spotify**: Using an enum for AudioFormat — persisted to the DB via ordinal. Then the constant order changed making migration difficult
- **Uber**: Using enums for trip state caused a compile-time safety guarantee of all trips being in one of 7 states

## Interview Patterns & Tips
- **Enum values are objects, but** they implement Comparable and Serializable by default.
- **Switch with enum**: The compiler checks that all values are matched if used with pattern matching (Java 21+).
- **Persistence**: Store string (.name()) or a custom code, not the .ordinal(). The ordinal can change if enumeration order changes.

## Deep Dive Questions
- **JVM**: How are enums compiled to class files? Each enum constant is a static final field of the enum type.
- **Memory**: How does the JVM store enum constants? (Object header overhead, fields, and class metadata)
- **EnumMap**: How does an EnumMap outperform a HashMap? It uses the ordinal as an internal array index.
- **Reflection**: How can enums be created via reflection? Are they protected against new instances?
- **Serialization**: How are enums deserialized safely? Through the use of the readResolve() method