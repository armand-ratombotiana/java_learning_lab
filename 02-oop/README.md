# Object-Oriented Programming Module

## Overview

This module explores Object-Oriented Programming (OOP) principles and their implementation in Java. Students will master encapsulation, inheritance, polymorphism, and abstraction while learning design patterns.

## Learning Objectives

- Master the four pillars of OOP: encapsulation, inheritance, polymorphism, abstraction
- Implement interfaces and abstract classes
- Apply common design patterns
- Understand SOLID principles
- Build maintainable, reusable code

## Module Structure

| Document | Purpose |
|----------|---------|
| README.md | Module overview and navigation |
| DEEP_DIVE.md | Comprehensive concept explanations |
| EXERCISES.md | Practice problems with solutions |
| QUIZZES.md | Knowledge assessment tests |
| EDGE_CASES.md | Tricky scenarios and corner cases |
| PEDAGOGIC_GUIDE.md | Teaching methodology |
| PROJECTS.md | Hands-on project implementations |

## Topics Covered

1. **Encapsulation**
   - Data hiding
   - Access modifiers
   - Getters and setters
   - Immutability

2. **Inheritance**
   - Class extension
   - super keyword
   - Method overriding
   - Abstract classes

3. **Polymorphism**
   - Runtime polymorphism
   - Method dispatch
   - Covariant returns
   - instanceof operator

4. **Abstraction**
   - Abstract classes
   - Interfaces
   - Default methods
   - Functional interfaces

5. **Design Patterns**
   - Factory pattern
   - Singleton pattern
   - Strategy pattern
   - Observer pattern
   - Builder pattern

6. **SOLID Principles**
   - Single Responsibility
   - Open/Closed
   - Liskov Substitution
   - Interface Segregation
   - Dependency Inversion

## Prerequisites

- Java basics (Module 1)
- Understanding of classes and objects
- Familiarity with basic data types

## Estimated Duration

- **Total**: 30-35 hours
- **Lessons**: 12-15 hours
- **Exercises**: 10-12 hours
- **Projects**: 8-10 hours

## Quick Example: OOP in Action

```java
// Abstract base class
public abstract class Shape {
    protected String color;
    
    public Shape(String color) {
        this.color = color;
    }
    
    public abstract double calculateArea();
    public abstract double calculatePerimeter();
}

// Interface
public interface Drawable {
    void draw();
    void setPosition(int x, int y);
}

// Concrete implementation
public class Circle extends Shape implements Drawable {
    private double radius;
    private int x, y;
    
    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }
    
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    public double calculatePerimeter() {
        return 2 * Math.PI * radius;
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing circle at (" + x + ", " + y + ")");
    }
    
    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
```

## Design Pattern Examples

### Factory Pattern
```java
public interface Document {
    void open();
    void save();
}

public class PDFDocument implements Document {
    public void open() { /* PDF logic */ }
    public void save() { /* PDF logic */ }
}

public class WordDocument implements Document {
    public void open() { /* Word logic */ }
    public void save() { /* Word logic */ }
}

public class DocumentFactory {
    public static Document createDocument(String type) {
        return switch (type.toLowerCase()) {
            case "pdf" -> new PDFDocument();
            case "word" -> new WordDocument();
            default -> throw new IllegalArgumentException("Unknown type");
        };
    }
}
```

### Singleton Pattern
```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    
    private DatabaseConnection() {
        // Private constructor
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}
```

## Next Steps

After completing this module:
- **Module 3**: Java Collections Framework
- **Module 4**: Stream API
- **Module 5**: Concurrency
- **Module 8**: Generics

## Resources

- [Design Patterns: Elements of Reusable Software](https://www.oreilly.com/library/view/design-patterns-elements/0201633612/)
- [Head First Design Patterns](https://www.oreilly.com/library/view/head-first-design/0596007124/)
- [Refactoring Guru - Design Patterns](https://refactoring.guru/design-patterns)
