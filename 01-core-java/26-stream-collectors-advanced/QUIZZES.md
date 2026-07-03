# Quizzes: Advanced Stream Collectors

Test your knowledge of custom collectors, parallel execution, and the `teeing` collector.

## Quiz 1: Custom Collector Mechanics

**Q1: In the `Collector<T, A, R>` interface, what does the `combiner()` function do?**
- A) It combines each element of the stream with the accumulator.
- B) It merges two partial accumulator containers into one, and is only used during parallel stream execution.
- C) It transforms the final accumulator into the final result type `R`.
- D) It combines the stream with another stream.
*Answer: B*

**Q2: If the accumulator type `A` is exactly the same as the final result type `R` (e.g., you are accumulating into a `List` and returning that same `List`), which Characteristic should you apply to the collector?**
- A) `CONCURRENT`
- B) `UNORDERED`
- C) `IDENTITY_FINISH`
- D) `FINAL`
*Answer: C*

## Quiz 2: Parallel Streams and Characteristics

**Q1: You create a custom collector and add the `Characteristics.CONCURRENT` flag. What requirement does this place on your `accumulator()` function?**
- A) It must be written using the `synchronized` keyword.
- B) It must be thread-safe, because multiple threads from a parallel stream will call it concurrently to insert elements into the *exact same* accumulator instance.
- C) It must return a new accumulator instance every time it is called.
- D) It must be stateless.
*Answer: B*

**Q2: What happens if you use `Collectors.toMap(User::getRole, User::getName)` and two users have the same role?**
- A) The second user overwrites the first user in the map.
- B) The map stores a `List` of names for that role.
- C) An `IllegalStateException` is thrown at runtime indicating a duplicate key.
- D) The compiler throws an error.
*Answer: C*

## Quiz 3: Teeing

**Q1: What is the primary use case for `Collectors.teeing()` (introduced in Java 12)?**
- A) To split a stream into two separate streams.
- B) To write stream elements to a file and the console simultaneously.
- C) To pass every element of a stream into two different downstream collectors, and then merge their final results, allowing you to calculate two different metrics in a single pass.
- D) To pause stream execution for debugging.
*Answer: C*