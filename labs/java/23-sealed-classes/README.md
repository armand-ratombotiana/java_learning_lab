# Lab 23: Sealed Classes in Java

## Overview

Sealed classes and interfaces, finalized in Java 17 (JEP 409), allow a class or interface to define which other classes or interfaces may extend or implement it. This provides fine-grained control over type hierarchies, enabling exhaustive pattern matching and more expressive domain models.

### Key Concepts Covered

- Declaring sealed classes and interfaces with the `sealed` keyword
- The `permits` clause to specify permitted subtypes
- Sealed class hierarchy rules
- Pattern matching with sealed types for exhaustive switch
- Migration from unrestricted to sealed hierarchies
- Sealed classes vs. access modifiers

### Prerequisites

- Java 17+ JDK
- Understanding of inheritance, abstract classes, and interfaces
- Familiarity with pattern matching (Lab 24 covers this in detail)

### What You'll Learn

By the end of this lab, you'll understand how sealed classes create controlled type hierarchies, enable exhaustive pattern matching, and improve API design by explicitly documenting permitted implementations.

Let's explore sealed classes in depth.
