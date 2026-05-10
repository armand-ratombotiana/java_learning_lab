# Design Patterns Solution

This module provides comprehensive reference implementations for the GoF (Gang of Four) design patterns.

## Patterns Covered

### Creational Patterns
- **Singleton**: Thread-safe singleton with double-checked locking
- **Factory Method**: Notification factory with type-based creation
- **Builder**: User builder for immutable object construction

### Structural Patterns
- **Adapter**: Media adapter for legacy system integration
- **Decorator**: Coffee decorator for dynamic feature addition
- **Facade**: Computer facade for simplified interface
- **Proxy**: Image proxy for lazy loading
- **Flyweight**: Tree flyweight for memory optimization

### Behavioral Patterns
- **Observer**: Subject-observer for event handling
- **Strategy**: Payment strategy for algorithm interchange
- **Command**: Remote control for request encapsulation
- **State**: State machine for object behavior change
- **Template Method**: Data miner for algorithm skeleton
- **Chain of Responsibility**: Handler chain for request processing
- **Composite**: File system for tree structures
- **Iterator**: Custom iterator for collection traversal
- **Memento**: Originator-caretaker for state snapshots

## Key Concepts

### Singleton Pattern
```java
Singleton.getInstance("data")
```
- Thread-safe with double-checked locking
- Ensures single instance across application

### Factory Pattern
```java
NotificationFactory.createNotification("email")
```
- Decouples object creation from usage
- Supports polymorphic creation

### Builder Pattern
```java
new User.UserBuilder().name("John").age(30).build()
```
- Fluent API for object construction
- Immutable object creation

### Observer Pattern
```java
subject.attach(observer);
subject.notifyObservers("event");
```
- One-to-many dependency management
- Event-driven communication

### Strategy Pattern
```java
new ShoppingCart(new CreditCardPayment("1234"))
```
- Interchangeable algorithms
- Runtime behavior modification

## Implementation Details

### Thread-Safe Singleton
Uses volatile and synchronized blocks to ensure thread safety while maintaining performance.

### Factory with Switch Expression
Modern Java switch expressions for clean type-based object creation.

### Composite Pattern
Implements tree structure with files and directories, demonstrating recursive composition.

### Flyweight Pattern
Tree factory shares TreeType objects across multiple Tree instances to save memory.

## Test Coverage

45+ test cases covering:
- Pattern instantiation
- Behavior verification
- Integration scenarios
- Edge cases
- State management
- Memory optimization

## Running Tests

```bash
# Compile and run tests
javac -cp junit-5.9.3.jar:. -d out src/**/*.java src/**/Test.java
java -cp junit-5.9.3.jar:hamcrest-core-1.3.jar:out org.junit.platform.console.ConsoleLauncher --scan-classpath
```