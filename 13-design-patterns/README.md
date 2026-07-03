# 13 - Design Patterns

Classic Gang of Four design patterns implemented in Java. Covers creational patterns (Singleton, Factory, Builder), structural patterns (Decorator, Adapter), and behavioral patterns (Observer, Strategy, Command). Includes both theory and hands-on implementations.

## Prerequisites

- Java 17+
- Maven 3.x

## Key Concepts

- **Creational**: Singleton (thread-safe, lazy initialization), Factory (object creation delegation), Builder (fluent construction, telescoping constructors)
- **Structural**: Decorator (dynamic behavior composition), Adapter (interface compatibility)
- **Behavioral**: Observer (pub/sub event notification), Strategy (interchangeable algorithms), Command (encapsulated requests, undo support)
- Principles: composition over inheritance, loose coupling, open/closed principle

## Module Structure

- `01-creational/` - Singleton, Factory, Builder patterns
- `src/main/java/com/learning/lab/module13/Lab.java` - All patterns combined lab
- `SOLUTION/` - Pattern solution implementations
- `RESOURCES/` - Pattern reference materials

## Learning Objectives

- Recognize and implement common design patterns
- Apply appropriate patterns to solve design problems
- Understand trade-offs between different patterns

## Estimated Time

- 3-5 hours

## How to Build

```bash
cd 13-design-patterns
mvn clean package
```

Run the lab:

```bash
mvn compile exec:java -Dexec.mainClass="com.learning.lab.module13.Lab"
```
