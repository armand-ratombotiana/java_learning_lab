# 15 - Advanced Java Topics

Advanced Java language features. Covers Java Records (compact data carriers with auto-generated accessors, equals, hashCode, toString), Sealed Classes (restricted inheritance hierarchies, permits clause), and Pattern Matching (instanceof pattern matching, switch pattern matching with type patterns).

## Prerequisites

- Java 21+
- Maven 3.x

## Key Concepts

- Records: `record Person(...)`, compact constructors, validation, custom methods, auto-generated boilerplate
- Sealed classes: `sealed class Shape permits Circle, Rectangle`, exhaustiveness, `final` subclasses
- Pattern matching: `instanceof` with variable binding, nested patterns, `switch` with type patterns
- Use cases for records as DTOs, value objects
- Sealed hierarchies for domain modeling

## Module Structure

- `01-records-sealed/` - Records, sealed classes, and pattern matching lab

## Learning Objectives

- Model immutable data with Java Records
- Design restricted type hierarchies with sealed classes
- Leverage pattern matching for cleaner conditional logic

## Estimated Time

- 1-2 hours

## How to Build

```bash
cd 15-advanced-topics
mvn clean package
```

Run the lab:

```bash
cd 01-records-sealed
mvn compile exec:java -Dexec.mainClass="com.learning.advanced.AdvancedLab"
```
