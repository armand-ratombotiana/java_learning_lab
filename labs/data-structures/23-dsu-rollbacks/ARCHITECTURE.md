# Architecture: Disjoint Set Union with Rollbacks

## System Design

The Disjoint Set Union with Rollbacks implementation follows a modular architecture designed for clarity, maintainability, and performance.

## Component Architecture

### Core Module

The core module contains the primary data structure implementation. It is self-contained with minimal dependencies.

### Utility Module

Utility classes provide supporting functionality such as hash functions, comparators, and conversion utilities.

### Test Module

The test module contains comprehensive JUnit 5 tests covering unit tests, integration tests, and performance benchmarks.

## Design Patterns

### Factory Pattern

Used for creating instances with different configurations.

### Strategy Pattern

Allows pluggable hash functions and collision resolution strategies.

### Template Method Pattern

Defines the skeleton of an algorithm in an abstract class, letting subclasses override specific steps.

## Package Structure

The code is organized into the com.dsacademy package hierarchy, separating main implementation from test code.

## Dependencies

The implementation has minimal external dependencies, relying primarily on the Java standard library.

## Extension Points

The architecture supports extension through:

- Custom hash functions
- Alternative collision resolution strategies
- Different memory allocation strategies
- Concurrent access patterns
