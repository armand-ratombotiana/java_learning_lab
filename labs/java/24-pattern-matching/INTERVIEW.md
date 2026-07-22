# Interview Questions: Pattern Matching

## Company-Specific Focus

### Google
- Pattern matching for instanceof: automatic variable binding after type check
- Pattern matching for switch: handling objects with type patterns inline
- Nested record patterns: destructuring deeply nested records with one expression

### Microsoft
- Pattern matching: how does it compare to C# / is the C# pattern matching from 7.0 to 9.0?
- Guarded patterns: filtering after matching with 'when' clauses
- Null handling: special null case in pattern matching switch

### Amazon
- Avoiding boilerplate in data processing with pattern matching on shape types
- Using sealed classes and patterns for designing events
- Nested pattern on request objects for routing on several criteria

### Meta
- instanceof and cast elimination: local variable type inference and the elegance of it
- Truth condition: null patterns and the significance of dominance

### Apple
- Exhaustive matching prevents unhandled types
- Clean null handling: null case with pattern matching
- Records and patterns for domain modeling in production code

### Oracle
- JEP 441 (Java 21): Pattern Matching for switch final
- JLS 14.30.1: Pattern declarations and type patterns
- Dominance: patterns must be from specific to generic to be valid
- The record deconstruction pattern in the JVM

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 160 Intersection of Two Linked Lists | Easy | Amazon, Apple, Google | Pattern matching on node types |
| 206 Reverse Linked List | Easy | Amazon, Google | Not directly pattern matching |
| 141 Linked List Cycle | Easy | Amazon, Google, Apple | Pattern matching/two pointers |
| 104 Maximum Depth of Binary Tree | Easy | Google, Amazon | Pattern matching tree nodes |
| 100 Same Tree | Easy | Amazon, Apple, Google | Pattern matching, compare pair of types |

## Real Production Scenarios
- **Uber**: Replace 800 lines of if-instanceof-cast boilerplate with pattern-matching switch in a rule engine
- **Netflix**: Pattern matching in the ranking engine — the old Visitor pattern for expression types was replaced entirely
- **Slack**: A nested record pattern in a single line performed what was previously 15 lines of null checks and casts

## Interview Patterns & Tips
- **Instanceof pattern**: `if (obj instanceof String s)` binds s as a String variable; no separate cast
- **Switch with null**: The default case does not match null but you can use case null
- **Dominance**: Each pattern must be less general than the previous patterns to avoid dead code

## Deep Dive Questions
- **JVM**: How is a pattern-matching switch in a class file compiled? invokedynamic?
- **Performance**: What does the JIT do with pattern-matching on a sealed class?
- **Record pattern**: How does a record pattern nester produce its field access?
- **MatchException**: When does pattern matching throw MatchException? How to handle?
- **Null handling**: Why does the switch statement throw NullPointerException for a null but pattern matching switch does not?