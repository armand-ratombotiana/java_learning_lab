# Reflection — Functional Programming

## Why This Lab Matters
Functional programming principles make code more predictable, testable, and concurrency-friendly.

## What I Learned
- How to write and identify pure functions
- Using Optional as a monad for safe null handling
- Composing functions with combinators
- Immutability as a design choice

## Questions I Still Have
- Will Java ever get an Either monad in the standard library?
- How do persistent (immutable) data structures work under the hood?

## Personal Application
- Refactor null-heavy code to use Optional
- Design domain logic as pure functions
- Use records for immutable data carriers
- Apply the functional core pattern in service layers

## Key Takeaways
1. Pure functions are easier to test, reason about, and parallelise
2. Optional is a monad — use map, flatMap, orElse, not get()
3. Immutability is the default in functional programming
4. Combines with Streams for powerful data pipelines
