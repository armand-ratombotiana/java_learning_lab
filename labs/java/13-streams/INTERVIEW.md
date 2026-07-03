# Interview Questions — Java Streams

## Beginner
1. What is the Stream API and why was it introduced in Java 8?
2. Explain the difference between `map` and `flatMap` with examples.
3. What are intermediate and terminal operations? Provide examples.

## Intermediate
4. How does `Collectors.groupingBy` work? Show a use case.
5. What is the difference between `findFirst()` and `findAny()`?
6. How do parallel streams achieve parallelism? What is the role of `ForkJoinPool`?
7. Can you reuse a stream after calling a terminal operation? Why?

## Advanced
8. Explain the internal mechanics of a stream pipeline (Spliterator, Sink, PipelineHelper).
9. Design a parallel word-count pipeline using Stream API.
10. When should you use `reduce()` versus `collect()`? What are the constraints on reduce?
11. How would you create a custom `Collector`? Show the implementation.
12. Discuss performance trade-offs: streams vs loops vs for-each.

## Code Review / Problem Solving
13. "Find the first non-repeating character in a string using streams."
14. "Given a list of transactions, compute the sum per day for the top 5 categories."
15. "How would you debug a stream pipeline in production?"
