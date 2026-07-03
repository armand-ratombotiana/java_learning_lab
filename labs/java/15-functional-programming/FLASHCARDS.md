# Flashcards — Functional Programming

**Q:** What is a pure function?  
**A:** Deterministic, no side effects, only depends on inputs.

**Q:** What is referential transparency?  
**A:** An expression can be replaced by its value without changing behaviour.

**Q:** What is `Optional.map`?  
**A:** Transforms the value if present, returns `Optional.empty()` otherwise.

**Q:** What is `Optional.flatMap`?  
**A:** Like map but the transformer already returns an Optional.

**Q:** What is function composition?  
**A:** Combining two functions into one: `f.andThen(g)`.

**Q:** What is the left identity monad law?  
**A:** `of(x).flatMap(f) ≡ f(x)`

**Q:** What is an immutable object?  
**A:** An object whose state cannot change after creation.

**Q:** What is a combinator?  
**A:** A function that combines other functions.

**Q:** What is memoization?  
**A:** Caching the result of a pure function by input.

**Q:** What is the "functional core, imperative shell" pattern?  
**A:** Pure domain logic wrapped by thin I/O layers.
