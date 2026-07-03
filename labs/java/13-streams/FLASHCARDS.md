# Flashcards — Java Streams

**Q:** What is a Stream?  
**A:** A sequence of elements supporting sequential and parallel aggregate operations.

**Q:** What is the difference between `map` and `flatMap`?  
**A:** `map` transforms each element 1:1; `flatMap` transforms 1:many and flattens.

**Q:** Name three terminal operations.  
**A:** `collect`, `forEach`, `reduce`.

**Q:** What is a short-circuit operation?  
**A:** An operation that may produce a result without processing all elements (`findFirst`, `limit`, `anyMatch`).

**Q:** What is the common `ForkJoinPool`?  
**A:** The default thread pool used by parallel streams.

**Q:** What does `Collectors.groupingBy` produce?  
**A:** A `Map<K, List<V>>`.

**Q:** Can a Stream contain null?  
**A:** Yes, but some operations may throw `NullPointerException`.

**Q:** What is the `peek()` method for?  
**A:** Debugging — allows seeing elements as they flow through the pipeline.

**Q:** What is a stateless intermediate operation?  
**A:** An op that does not need to remember previous elements (e.g. `filter`, `map`).

**Q:** What is a stateful intermediate operation?  
**A:** An op that buffers elements (e.g. `sorted`, `distinct`).
