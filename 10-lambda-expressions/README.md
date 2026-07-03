# 10 - Lambda Expressions

Lambda expressions and functional programming features in Java. Covers lambda syntax, functional interfaces (Predicate, Function, Consumer, Supplier, UnaryOperator, BinaryOperator), method references, closures (variable capture, effectively final), and stream integration with lambdas.

## Prerequisites

- Java 17+
- Maven 3.x

## Key Concepts

- Lambda syntax: `(params) -> expression` / `(params) -> { statements }`
- Functional interfaces: `Predicate<T>`, `Function<T,R>`, `Consumer<T>`, `Supplier<R>`, `UnaryOperator<T>`, `BinaryOperator<T>`
- `@FunctionalInterface` annotation
- Method references: static, instance, constructor (`String::toUpperCase`, `System.out::println`)
- Closures: capturing effectively-final variables from enclosing scope
- Lambda composition with streams (filter, map, reduce)

## Module Structure

- `src/main/java/com/learning/lab/module10/Lab.java` - Main lab source

## Learning Objectives

- Write lambda expressions for concise functional code
- Use built-in functional interfaces effectively
- Apply method references and understand closure semantics

## Estimated Time

- 1-2 hours

## How to Run

```bash
cd 10-lambda-expressions
mvn compile exec:java -Dexec.mainClass="com.learning.lab.module10.Lab"
```
