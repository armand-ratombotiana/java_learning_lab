=======
# Quizzes: Higher-Order Functions

Test your knowledge of closures, currying, and partial application.

## Quiz 1: Definitions and Concepts

**Q1: What defines a "Higher-Order Function"?**
- A) A function that runs with elevated system privileges.
- B) A function that executes faster than standard methods.
- C) A function that either takes one or more functions as arguments, or returns a function as its result.
- D) A function that uses recursion.
*Answer: C*

**Q2: Which GoF (Gang of Four) Design Pattern is most easily replaced by passing a function as a parameter?**
- A) Singleton Pattern
- B) Strategy Pattern (Instead of creating a whole class hierarchy just to swap out one algorithm, you simply pass a lambda representing the algorithm).
- C) Factory Pattern
- D) Facade Pattern
*Answer: B*

## Quiz 2: Currying and Partial Application

**Q1: What is the primary difference between Currying and Partial Application?**
- A) Currying is for primitives; Partial Application is for objects.
- B) Currying transforms a function of `N` arguments into a sequence of `N` functions of 1 argument each. Partial Application takes a function of `N` arguments, fixes `K` arguments, and returns a single function of `N-K` arguments.
- C) Currying is a Java feature; Partial Application is not.
- D) There is no difference.
*Answer: B*

**Q2: Look at this code: `Function<Integer, Function<Integer, Integer>> add = a -> b -> a + b;`. How would you execute this to add 5 and 10?**
- A) `add.apply(5, 10)`
- B) `add(5)(10)`
- C) `add.apply(5).apply(10)`
- D) `add.apply(15)`
*Answer: C*

## Quiz 3: Closures and Edge Cases

**Q1: What is a "Closure" in Java?**
- A) A block of code that automatically closes database connections.
- B) A lambda expression that captures and remembers the state of local variables from its enclosing scope at the time it was created.
- C) A method that cannot be overridden.
- D) A synchronized block.
*Answer: B*

**Q2: Why does the following code fail to compile?**
```java
int multiplier = 2;
Function<Integer, Integer> func = x -> x * multiplier;
multiplier = 3;
```
- A) Because `multiplier` is not a static variable.
- B) Because lambdas cannot access external variables.
- C) Because local variables captured by a lambda must be `final` or effectively final. Since `multiplier` is reassigned to 3, it is not effectively final.
- D) Because the syntax `x * multiplier` is invalid.
*Answer: C*