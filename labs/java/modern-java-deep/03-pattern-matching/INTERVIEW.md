# Interview Questions: Pattern Matching Deep Dive

## Company-Specific Focus

### Google
- Pattern matching for instanceof: type check + variable binding in one step
- Pattern matching in switch (Java 21+): switch on type patterns, not just values
- Record patterns: destructuring records directly in pattern matching

### Microsoft
- Java pattern matching vs C# pattern matching: comparing features
- Guarded patterns: when clauses for conditional matching

### Amazon
- Service routing: pattern matching for request type dispatching
- Expression evaluation: pattern matching for expression tree processing

### Meta
- Exhaustiveness: pattern matching switch must cover all possibilities
- Null handling: separate null case in pattern switch
- Dominance: patterns must be ordered from specific to general

### Apple
- Nested patterns: combining record and type patterns
- MatchException: what happens when no pattern matches

### Oracle
- JEP 441: Pattern Matching for switch (Final in JDK 21)
- JEP 440: Record Patterns (Final in JDK 21)
- JEP 443: Unnamed Patterns and Variables (Preview)
- Domiance checking: ensures patterns are ordered correctly

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 293 Flip Game | Easy | Google | Conditional pattern matching |
| 160 Intersection of Two Linked Lists | Easy | Google, Amazon | Pattern match on node type |
| 104 Maximum Depth of Binary Tree | Easy | Amazon, Google | Pattern matching on tree nodes |

## Real Production Scenarios
- **Uber**: 800-line if-instanceof-cast chain replaced with 80-line pattern matching switch
- **Netflix**: Visitor pattern replaced by pattern matching for expression tree evaluation

## Interview Patterns & Tips
- **Instanceof pattern**: `if (x instanceof String s)` binds s as String
- **Switch pattern**: `case Integer i -> process(i)`
- **Guarded pattern**: `case String s && s.length() > 5 -> process(s)`
- **Null**: pattern matching switch can handle null explicitly

## Deep Dive Questions
- **Compilation**: How is pattern matching compiled to bytecode?
- **Exhaustiveness**: How does the compiler check exhaustiveness?
- **Dominance**: How are dominant patterns detected?
- **Record pattern**: How does a record pattern decompose the record?
- **Performance**: What is the performance of pattern matching vs instanceof-cast?