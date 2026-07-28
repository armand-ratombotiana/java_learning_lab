# Lab 02: Sealed Classes Deep Dive

## Overview
Sealed classes and interfaces (Java 17 final) give you fine-grained control over type hierarchies. This lab explores sealed interfaces/classes, the `permits` clause, and exhaustive switch handling.

## Goals
- Define sealed hierarchies with `permits`
- Understand subclass location constraints (same file, same package, or nested)
- Combine sealed types with exhaustive switch expressions
- Use sealed interfaces for algebraic data types (ADTs)
- Write maintainable closed hierarchies

## Prerequisites
- Java 17+
- Understanding of inheritance and interfaces
- Basic pattern matching
