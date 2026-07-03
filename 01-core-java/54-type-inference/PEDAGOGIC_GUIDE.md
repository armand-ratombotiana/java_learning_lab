# Pedagogic Guide: Type Inference

## 1. Module Overview
This module is a breather from the heavy architectural patterns of previous modules. It focuses on developer ergonomics and compiler mechanics. The goal is to teach learners how to write code that is clean and readable, while avoiding the trap of abusing new language features just because they exist.

## 2. Learning Paths

### Path A: The Java 8 Migrator (Focus: Syntax & Clean Code)
**Target Audience**: Developers moving to modern Java (11/17/21) who want to modernize their codebase.
*   **Focus**: `DEEP_DIVE.md` (The `var` rules) and `MINI_PROJECT.md` (Data Processor).
*   **Key Takeaway**: Understanding that `var` does not make Java dynamically typed like JavaScript, and learning the official stylistic guidelines for when to use it (when the type is obvious) and when to avoid it.

### Path B: The Compiler Nerd (Focus: Target Typing & Poly Expressions)
**Target Audience**: Senior developers who want to understand exactly how the `javac` compiler works under the hood.
*   **Focus**: `EDGE_CASES.md` (Poly Expressions, Anonymous Classes) and `INTERVIEW_PREP.md`.
*   **Key Takeaway**: Mastering the concept of "Target Typing" and understanding why lambdas and method references cannot be directly assigned to `var` without explicit casting.

## 3. Teaching Strategies

### The "JavaScript vs. Java" Clarification
The first thing every developer asks when they see `var` is: "Did Java just become JavaScript?"
You must immediately squash this.
Write this pseudo-code on the board:
```java
var x = 10;
x = "Hello";
```
Ask what happens. Explain that in JS, it works. In Java, it is a strict compilation error. The compiler simply reads the right side (`10`), mentally replaces `var` with `int`, and locks it in forever.

### The "Readability Test"
To teach best practices, provide two snippets:
**Snippet A:**
```java
var result = service.processData(input);
```
**Snippet B:**
```java
var userList = new ArrayList<User>();
```
Ask the learner which one is better. Guide them to realize that Snippet B is great because the type is explicitly stated on the right. Snippet A is terrible because the reader has no idea what `result` is without navigating to the `processData` method definition. Code is read 10x more than it is written; optimize for reading.

## 4. Common Mental Blocks & Clarifications

### Block 1: "Why does `var list = new ArrayList<>();` give me a warning?"
*   **Clarification**: This is the "Diamond Operator Trap." Explain that the compiler is playing a game of Sudoku. It needs enough numbers on the board to solve the puzzle. If you use `var` on the left, and `<>` on the right, you've erased all the numbers. The compiler gives up and defaults to `Object`. Show them that they must provide the type on at least one side of the assignment.

### Block 2: "Why can't I use `var` for method parameters?"
*   **Clarification**: Explain the concept of Public API Contracts. If a method signature is `public void print(var data)`, the compiler would have to infer the type based on *every single place in the codebase that calls that method*. If Caller A passes a String, and Caller B passes an Integer, what is the type? It breaks the fundamental structure of statically typed APIs. Local variables are safe because their scope is restricted to a few lines of code.

### Block 3: "What is a Poly Expression?"
*   **Clarification**: Use the word "Shape-shifter." A lambda `x -> x + 1` is a shape-shifter. It could be a `Function<Integer, Integer>`, or a `UnaryOperator<Integer>`, or a custom `MathOp` interface. It doesn't know what it is until it is assigned to a specific interface (the Target Type). Because `var` provides no Target Type, the shape-shifter is stuck, and the compiler throws an error.

## 5. Assessment Strategy
*   **Formative**: Provide a list of 5 variable declarations using `var` (some valid, some invalid, some bad practice). Ask the learner to identify which will fail to compile and which violate clean code guidelines.
*   **Summative**: The `MINI_PROJECT.md` requires the learner to refactor a verbose data processing method using `var`, and then explicitly demonstrate the compiler errors that occur when mixing `var` with lambdas and diamond operators.