# 08 - Java Generics

Generic types, methods, and type safety in Java. Covers generic classes (`Box<T>`, `Pair<K,V>`), generic methods, bounded type parameters (`T extends Number`), wildcards (`?`, `? extends T`, `? super T`), generic interfaces, and type erasure concepts.

## Prerequisites

- Java 17+
- Maven 3.x

## Key Concepts

- Generic classes with type parameters
- Generic methods and type inference
- Bounded type parameters for constrained generics
- Wildcards: unbounded (`?`), upper-bounded (`? extends T`), lower-bounded (`? super T`)
- Generic interfaces and implementations
- Type erasure and its implications
- Best practices for generic programming

## Module Structure

- `src/main/java/com/learning/lab/module08/Lab.java` - Main lab source
- `RESOURCES/` - Type erasure documentation

## Learning Objectives

- Define and use generic classes and methods
- Apply bounded type parameters and wildcards effectively
- Understand type erasure and its impact

## Estimated Time

- 1-2 hours

## How to Run

```bash
cd 08-generics
mvn compile exec:java -Dexec.mainClass="com.learning.lab.module08.Lab"
```
