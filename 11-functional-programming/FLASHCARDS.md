# Functional Programming Flashcards

## Q1: What is a pure function?
**A**: A function that always produces same output for same input and has no side effects.

---

## Q2: What is a side effect?
**A**: Any observable change outside the function: modifying state, I/O operations, throwing exceptions.

---

## Q3: What is a higher-order function?
**A**: A function that takes functions as parameters or returns a function.

---

## Q4: What is function composition?
**A**: Combining two or more functions to create a new function: f(g(x))

---

## Q5: What is immutability?
**A**: Data that cannot be changed after creation. New objects are created instead of modifications.

---

## Q6: What is a closure?
**A**: A function that captures variables from its enclosing scope. The lambda "closes over" captured variables.

---

## Q7: What is a functor?
**A**: A type that implements map, allowing transformation without changing the structure.

---

## Q8: What is a monad?
**A**: A type that wraps a value and provides chainable operations: Optional, Stream, CompletableFuture.

---

## Q9: What is lazy evaluation?
**A**: Expression is not evaluated until its value is needed. Streams use lazy evaluation.

---

## Q10: What's the difference between map and flatMap?
**A**: map transforms each element to one result. flatMap transforms each to a stream and flattens into one.

---

## Q11: What is a pure function's benefit for testing?
**A**: No external state mocking needed. Just call with inputs and verify outputs.

---

## Q12: Why is FP good for concurrency?
**A**: No shared mutable state means no race conditions. Safe to parallelize.

---

## Q13: What is the difference between reduce and collect?
**A**: reduce combines into single value (sum, max). collect groups into collection (toList, groupingBy).

---

## Q14: What is referential transparency?
**A**: An expression is referentially transparent if it can be replaced with its value without changing behavior.

---

## Q15: What is currying?
**A**: Converting a function with multiple arguments into sequence of functions, each taking single argument.