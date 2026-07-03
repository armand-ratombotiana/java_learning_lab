# Quizzes: Lazy Evaluation

Test your knowledge of Eager vs. Lazy evaluation, Thunks, and Infinite Streams.

## Quiz 1: Eager vs Lazy

**Q1: What is the primary difference between `Optional.orElse()` and `Optional.orElseGet()`?**
- A) `orElse()` throws an exception if empty; `orElseGet()` returns a default value.
- B) `orElse()` evaluates its argument eagerly (the method inside the parenthesis is executed immediately, regardless of whether the Optional is empty or not). `orElseGet()` evaluates its argument lazily (the `Supplier` is only executed if the Optional is actually empty).
- C) `orElseGet()` is deprecated in Java 11.
- D) There is no difference; they are aliases.
*Answer: B*

**Q2: How do you simulate a "Thunk" (a delayed computation) in Java?**
- A) By using the `volatile` keyword.
- B) By wrapping the computation inside a `java.util.function.Supplier<T>` and only executing the logic when `get()` is called.
- C) By using a `ReentrantLock`.
- D) By compiling the code with a special flag.
*Answer: B*

## Quiz 2: Streams and Memoization

**Q1: Why is it possible to create an infinite stream in Java (e.g., `Stream.iterate(0, n -> n + 1)`) without crashing the JVM immediately?**
- A) Because the JVM uses a massive amount of virtual memory.
- B) Because Streams are lazy. The `iterate` function does not actually generate all the numbers upfront; it only generates the *next* number when a terminal operation specifically requests it.
- C) Because the compiler automatically limits infinite streams to 10,000 elements.
- D) Because the stream runs on a background thread.
*Answer: B*

**Q2: What does "Memoization" mean in the context of lazy evaluation?**
- A) Writing comments in your code to remember what a function does.
- B) Caching the result of a lazy computation the very first time it is evaluated, so that subsequent requests for the value return the cached result instantly without re-executing the computation.
- C) Converting an object to a byte stream.
- D) Forcing a lazy stream to become eager.
*Answer: B*

## Quiz 3: Edge Cases

**Q1: You have the code: `Supplier<Integer> s = () -> expensiveDatabaseCall();`. You pass `s` to a method that calls `s.get()` three times. How many times does the database call execute?**
- A) 1 time.
- B) 3 times. A standard `Supplier` in Java is just a function; it does not memoize (cache) its result automatically.
- C) 0 times.
- D) It throws a `CompileTimeError`.
*Answer: B*