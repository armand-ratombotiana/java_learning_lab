# Interview Questions: Functional Programming

## Basic Questions

### Q1: What is functional programming?
**A**: A programming paradigm that treats computation as evaluation of mathematical functions and avoids changing state and mutable data.

### Q2: What is a pure function?
**A**: A function that produces same output for same input and has no side effects. No dependency on external state.

### Q3: What are the benefits of immutability?
**A**: Thread-safe (no race conditions), easier to reason about, enables safe parallel processing, simpler debugging.

### Q4: Explain function composition
**A**: Combining simple functions to build complex operations. In Java: andThen(), compose() methods on Function interface.

## Intermediate Questions

### Q5: What is a higher-order function?
**A**: A function that takes functions as arguments or returns a function. Examples: filter, map, reduce.

### Q6: What is lazy evaluation in streams?
**A**: Intermediate operations don't execute until a terminal operation is called. This allows optimization and short-circuiting.

### Q7: What is the difference between map and flatMap?
**A**: map transforms each element to one element. flatMap transforms to stream and flattens all streams into one.

### Q8: What is a closure?
**A**: A lambda that captures variables from its surrounding scope. Can access effectively final local variables.

## Advanced Questions

### Q9: How does FP help with concurrency?
**A**: Pure functions don't share state, making them safe to execute in parallel. No synchronization needed.

### Q10: What is a monad in Java?
**A**: A design pattern providing container with chainable operations. Examples: Optional, Stream, CompletableFuture.

### Q11: Compare imperative vs declarative programming
**A**: Imperative: how to do (step-by-step instructions). Declarative: what to do (describe desired result). FP is declarative.

### Q12: What is referential transparency?
**A**: An expression is referentially transparent if it can be replaced with its value without changing program behavior.

### Q13: How would you refactor imperative code to FP?
**A**: 
1. Identify transformations (filter, map)
2. Use streams to chain operations
3. Use pure functions for each step
4. Collect results instead of accumulating in loops

### Q14: What are the trade-offs of FP?
**A**: 
- Pros: Conciseness, testability, concurrency, composability
- Cons: Learning curve, debugging stack traces, performance overhead for some use cases