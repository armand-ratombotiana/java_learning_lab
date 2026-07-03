# OOP Basics — Evolution Across Java Versions

## Java 1.0 (1996)

Core OOP features: classes, objects, constructors, `this`, `static`, instance/class members, access modifiers (`public`, `protected`, `private`), inner classes (including anonymous inner classes). Garbage collection automatically reclaims unreferenced objects.

## Java 5 (2004)

- **Enhanced for-loop**: Works with objects
- **Autoboxing**: Primitive-to-wrapper conversion in object contexts
- **Annotations**: Metadata for classes, fields, methods — `@Override`, `@SuppressWarnings`
- **Enums**: Type-safe enumeration — `enum Status { ACTIVE, INACTIVE }` — more powerful than `public static final` constants

## Java 8 (2014)

- **Default methods**: Interfaces can provide method implementations — affects how classes extend behavior
- **Static methods in interfaces**: Factory methods in interfaces
- **Lambdas**: Functional programming paradigm alongside OOP

## Java 14 (2020)

- **Records (preview)**: `record Point(int x, int y)` — compact carrier classes with auto-generated constructor, accessors, equals, hashCode, toString

## Java 15 (2020)

- **Sealed classes** (preview): `sealed class Shape permits Circle, Rectangle` — controlled inheritance
- **Text blocks**: Multi-line strings for Javadoc

## Java 16 (2021)

- **Records (standard)**: First-class immutable data carriers
- **Pattern matching for instanceof (standard)**: `if (obj instanceof String s)` — avoids explicit cast

## Java 17 (2021) — LTS

- **Sealed classes (standard)**: Precise control over class hierarchies
- **Pattern matching for switch (preview)**: Destructuring in switch

## Java 21 (2023)

- **Record patterns (standard)**: `if (obj instanceof Point(int x, int y))`
- **Pattern matching for switch (standard)**: `case Point(int x, int y) when x > 0 -> ...`
- **Unnamed patterns and variables (preview)**: `int _ = method()` for ignored results

## Core Stability

Despite these additions, the fundamental OOP model (classes, objects, constructors, `this`, `static`) has not changed since Java 1.0. New features are additive — old code still compiles and runs.
