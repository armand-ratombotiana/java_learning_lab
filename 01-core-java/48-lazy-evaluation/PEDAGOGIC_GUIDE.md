# Pedagogic Guide: Lazy Evaluation

## 1. Module Overview
This module introduces a subtle but powerful optimization technique. It challenges the imperative mindset where code executes exactly where it is written. By teaching learners to defer execution, this module enables them to write highly efficient code that avoids unnecessary database queries, API calls, and memory allocations.

## 2. Learning Paths

### Path A: The Application Developer (Focus: Optional & Streams)
**Target Audience**: Developers writing standard business logic, working with databases, or building REST APIs.
*   **Focus**: `EDGE_CASES.md` (`orElse` vs `orElseGet`) and `DEEP_DIVE.md` (Short-circuiting).
*   **Key Takeaway**: Understanding the severe performance penalty of using `orElse()` with a method call, and learning how to use short-circuiting stream operations to process large datasets efficiently.

### Path B: The Framework Architect (Focus: Thunks & Memoization)
**Target Audience**: Senior developers building caching layers, lazy-loaded singletons, or complex data pipelines.
*   **Focus**: `MINI_PROJECT.md` (The Memoizer) and `INTERVIEW_PREP.md`.
*   **Key Takeaway**: Mastering the difference between a raw Thunk (which re-evaluates every time) and a Memoized Thunk, and understanding how to implement thread-safe lazy initialization.

## 3. Teaching Strategies

### The "Restaurant Menu" Metaphor (Eager vs Lazy)
*   **Eager (Java Default)**: You sit down at a restaurant. The waiter immediately brings you every single item on the menu, cooks it all, and charges you for it. You only eat the burger and throw the rest away. (Massive waste of resources).
*   **Lazy (Supplier/Thunk)**: You sit down. The waiter hands you a menu (a `Supplier`). Nothing is cooked. The kitchen does zero work. You look at the menu, decide you want a burger, and say "I'll take this one" (`get()`). The kitchen only cooks exactly what you asked for.

### The "Infinite Conveyor Belt" Metaphor (Streams)
To explain how infinite streams avoid `OutOfMemoryError`s:
Imagine an infinite conveyor belt producing boxes.
If you say "Put all the boxes in a warehouse" (`collect(toList)`), the warehouse will eventually explode.
If you say "Stand at the end of the belt. Take the first 10 boxes, then turn off the conveyor belt" (`limit(10)`), the factory only ever produces 10 boxes. The infinite potential is never realized because you short-circuited the process.

## 4. Common Mental Blocks & Clarifications

### Block 1: "Why did my `orElse()` method execute even though the Optional had a value?"
*   **Clarification**: This is the most common bug in modern Java. Show this code: `print(calculateValue())`. Ask the learner: "Does `calculateValue()` run before or after `print()`?" They will answer "Before." 
Explain that `Optional.orElse(calculateValue())` works exactly the same way. Java must evaluate the argument *before* it passes it to the `orElse` method. The `orElse` method doesn't know what the argument is until it's already been calculated. `orElseGet(() -> calculateValue())` passes a *menu* (a function), not the cooked meal.

### Block 2: "If I pass a lambda, does it run in a different thread?"
*   **Clarification**: Beginners often confuse "asynchronous" with "lazy." Explain that a `Supplier` does not spawn a thread. When you call `supplier.get()`, it executes on the exact same thread that called `get()`. It is simply *delayed* execution, not *concurrent* execution.

### Block 3: "Why is my stack trace so weird when a lambda throws an exception?"
*   **Clarification**: Explain the "Deferred Exception" edge case. Because the code isn't executed where it's written, the stack trace won't point to the line where the lambda was defined. It will point to the line where `.get()` or `.collect()` was called. This requires learners to trace the data flow backwards to find the origin of the bad logic.

## 5. Assessment Strategy
*   **Formative**: Provide the code `Optional.of("User").orElse(db.saveNewUser())`. Ask the learner to explain the exact state of the database after this line executes. (Answer: A new user was saved to the database, even though it wasn't needed).
*   **Summative**: The `MINI_PROJECT.md` requires the learner to implement a thread-safe `Memoizer`. By proving they can wrap a `Supplier`, check if it has been evaluated, and cache the result safely across multiple threads, they demonstrate a complete understanding of lazy evaluation mechanics.