# Module 02: OOP Design Patterns - Solution

## Overview
This solution provides comprehensive reference implementations of classic GoF (Gang of Four) design patterns organized by category.

## Package Structure
```
com.learning.lab.module02.solution
```

## Solution Components

### 1. Solution.java
Complete implementations of 13 design patterns across three categories:

#### Creational Patterns (5)
- **Factory Pattern**: Creates objects without specifying exact class
- **Abstract Factory**: Creates families of related objects
- **Singleton**: Ensures single instance with global access
- **Builder**: Constructs complex objects step by step
- **Prototype**: Creates objects by cloning existing ones

#### Structural Patterns (4)
- **Adapter**: Converts interface of a class to another
- **Decorator**: Adds behavior to objects dynamically
- **Composite**: Treats individual and composite objects uniformly
- **Facade**: Provides simplified interface to complex subsystem

#### Behavioral Patterns (4)
- **Observer**: Defines one-to-many dependency between objects
- **Strategy**: Encapsulates interchangeable algorithms
- **Command**: Encapsulates request as an object
- **Template Method**: Defines algorithm skeleton, lets subclasses override steps

### 2. Test.java
Comprehensive test suite covering all patterns with assertions for:
- Pattern creation and behavior
- Object relationships
- Runtime flexibility
- Edge cases

## Running the Solution

```bash
cd 02-oop/SOLUTION
javac -d . Solution.java Test.java
java com.learning.lab.module02.solution.Solution
java com.learning.lab.module02.solution.Test
```

## Key Concepts

### Factory Pattern
- **Use Case**: When object creation logic should be decoupled from usage
- **Example**: DocumentFactory creates PDF/Word/Excel documents

### Abstract Factory Pattern
- **Use Case**: When you need to create families of related objects
- **Example**: UIFactory creates consistent Windows/Mac UI components

### Singleton Pattern
- **Use Case**: When only one instance should exist (database, logger)
- **Implementation**: Private constructor, static getInstance(), thread-safe

### Builder Pattern
- **Use Case**: When object has many optional parameters
- **Example**: Computer builder with CPU, RAM, GPU, storage

### Prototype Pattern
- **Use Case**: When creating objects is expensive (cloning)
- **Implementation**: Clone() method, copy constructor

### Adapter Pattern
- **Use Case**: When integrating incompatible interfaces
- **Example**: Payment adapters for CreditCard, PayPal, Crypto

### Decorator Pattern
- **Use Case**: Adding features dynamically without inheritance
- **Example**: Coffee with Milk, Sugar, whipped cream

### Composite Pattern
- **Use Case**: Tree structures where single items and groups treated same
- **Example**: File system (files and folders)

### Facade Pattern
- **Use Case**: Simplifying complex subsystems
- **Example**: ComputerFacade hiding CPU, Memory, HardDrive complexity

### Observer Pattern
- **Use Case**: Event-driven systems, publish-subscribe
- **Example**: NewsAgency with multiple NewsChannel observers

### Strategy Pattern
- **Use Case**: Interchangeable algorithms at runtime
- **Example**: Payment strategies (CreditCard, PayPal, Crypto)

### Command Pattern
- **Use Case**: Encapsulating operations, undo functionality
- **Example**: Remote control with on/off commands

### Template Method Pattern
- **Use Case**: Shared algorithm with customizable steps
- **Example**: DataMiner for CSV, JSON processing

## Design Principles Applied

1. **Single Responsibility**: Each class has one purpose
2. **Open/Closed**: Open for extension, closed for modification
3. **Liskov Substitution**: Subtypes are substitutable for base types
4. **Dependency Inversion**: Depend on abstractions, not concretions
5. **Interface Segregation**: Many specific interfaces > one general
6. **Composition over Inheritance**: Prefer composition for flexibility

## Best Practices

- **Use patterns judiciously**: Don't over-engineer simple problems
- **Prefer composition**: More flexible than inheritance
- **Know the problem first**: Choose pattern based on problem, not vice versa
- **Start simple**: Implement patterns incrementally
- **Test thoroughly**: Patterns add complexity, need good test coverage

## Common Pitfalls

- Using patterns where simple code suffices
- Over-engineering with too many abstractions
- Ignoring context-specific requirements
- Forgetting about thread safety (Singleton)
- Not considering future changes

## When to Use Each Pattern

| Pattern | When to Use |
|---------|-------------|
| Factory | Object creation varies by type |
| Abstract | Related objects need consistent creation |
| Singleton | One shared resource needed |
| Builder | Complex object with many optional fields |
| Prototype | Cloning expensive objects |
| Adapter | Integrating legacy/incompatible code |
| Decorator | Dynamic feature addition |
| Composite | Hierarchical data structures |
| Facade | Simplifying complex systems |
| Observer | Event handling, notifications |
| Strategy | Algorithm selection at runtime |
| Command | Queuing, undo operations |
| Template | Common algorithms with variation points |