# 09 - Java Annotations

Annotations and reflection in Java. Covers built-in annotations (`@Deprecated`, `@Override`, `@SuppressWarnings`), custom annotation creation (retention, target policies), meta-annotations (`@Retention`, `@Target`), annotation processing, and runtime reflection with annotations.

## Prerequisites

- Java 17+
- Maven 3.x

## Key Concepts

- Built-in annotations: `@Deprecated`, `@Override`, `@SuppressWarnings`, `@FunctionalInterface`
- Custom annotations: `@interface`, retention policies (`SOURCE`, `CLASS`, `RUNTIME`), target types
- Meta-annotations: `@Retention`, `@Target`, `@Documented`, `@Inherited`, `@Repeatable`
- Runtime annotation processing via reflection (`Class.getAnnotation`, `Method.isAnnotationPresent`)
- Annotation-based framework configuration
- Use cases: serialization, validation, ORM mapping

## Module Structure

- `src/main/java/com/learning/lab/module09/Lab.java` - Main lab source

## Learning Objectives

- Create and use custom annotations with appropriate retention and target
- Process annotations at runtime using reflection
- Understand how frameworks leverage annotations

## Estimated Time

- 1-2 hours

## How to Run

```bash
cd 09-annotations
mvn compile exec:java -Dexec.mainClass="com.learning.lab.module09.Lab"
```
