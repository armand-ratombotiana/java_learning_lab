# Interview Questions: Functional Programming

## Company-Specific Focus

### Google
- Immutability in the functional style: reducing bugs in complex systems
- Pure functions: testing without test doubles
- Recursion and the disadvantages in the loop versus plain transformations

### Microsoft
- Java functional programming with C#: expressions and function piping
- Ease of typed functional programming in the Java ecosystem
- Handling side effects: user instance scanning in OOP and functional style

### Amazon
- Functional programming for data pipeline: chain execution, avoid mutable operations
- Using the Optional type function for null-safety patterns
- Composition of functions (andThen, compose) in the AWS SDK API design

### Meta
- Functional programming patterns vs. yield-per-request efficiency
- Type-safe domain models using ALG-based representation
- The overhead of functional objects in a GPU-like compute pipeline

### Apple
- Immutable data flow in the reactive stack
- Using the cute record for fixed value objects
- Lazy evaluation patterns in FP heavy coding

### Oracle
- JLS 15.27: Functional expression representation
- JLS 9.8: The SAM interface type requirement for a function
- the overload of method references in a method invocation context
- Built-in functional interface contract

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 102 Binary Tree Level Order Tree Traversal | Medium | Google, Amazon, Meta | mapping in a recursive pattern |
| 78 Subsets | Medium | Microsoft, Amazon, Apple | Fold and flatMap type semantics |
| 150 Evaluate Reverse Polish Notation | Medium | Amazon, Google, Apple | Fold process semantics |
| 46 Permutations | Medium | Google, Amazon, Microsoft | Flatmap recursion |
| 200 Number of Islands | Medium | Amazon, Google, Apple | Function composition: read from matrix |

## Real Production Scenarios
- **Google**: In the presence of a mutable dataset being shared across multiple Promises, state corruption was happening; move to the immutable dataset fixed it.
- **Pinterest**: A multitude of Optional.ifPresent calls in a scheduling pipeline hid a bug for 8 months.
- **Netflix**: Using functional composition instead of nested conditionals decreased the number of uncovered test paths by 40%.

## Interview Patterns & Tips
- **Favor pure functions**: Given the same input, there should be the same output, and no side effects should exist.
- **Avoid null in functional**: Use Optional<T and orElse throw pattern.
- **Immutable return types**: Do not mutate the parameter.
- **Test**: functional composition leads to smaller, verifiable segments of code.

## Deep Dive Questions
- **JVM**: How does the JVM represent functional interfaces and use treat them internally?
- **JIT**: How does the JIT treat a partial function application's additional objects?
- **Memory**: How does an immutable approach affect the GC behavior?
- **JVM Stream**: How does the JIT optimize a composition of intermediate operations (filterMap, MapReduce)?
- **Java Future**: How do the virtual threads and the scoped values extend current functional paradigms?