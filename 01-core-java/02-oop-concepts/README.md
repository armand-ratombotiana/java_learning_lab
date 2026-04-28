# 02 - Object-Oriented Programming (OOP) Concepts

## Overview

This module provides comprehensive demonstrations of all fundamental Object-Oriented Programming concepts in Java. Each concept is illustrated with practical, real-world examples and accompanied by thorough unit tests.

## Table of Contents

1. [Classes and Objects](#classes-and-objects)
2. [Encapsulation](#encapsulation)
3. [Inheritance](#inheritance)
4. [Polymorphism](#polymorphism)
5. [Abstraction](#abstraction)
6. [Interfaces](#interfaces)
7. [Project Structure](#project-structure)
8. [Running Tests](#running-tests)
9. [Building the Project](#building-the-project)

---

## Classes and Objects

**Concept**: Classes are blueprints for creating objects. Objects are instances of classes with specific state and behavior.

**Implementation**: `Person.java`

```java
Person person = new Person("John Doe", 30);
person.displayInfo();
person.celebrateBirthday();
```

**Key Features**:
- Constructors (default, parameterized, and full)
- Instance variables and static variables
- Methods and getters/setters
- toString() override

**Tests**: `PersonTest.java` - 16 comprehensive test cases

---

## Encapsulation

**Concept**: Encapsulation is the bundling of data (variables) and methods (functions) inside a class, with controlled access through public methods. It hides internal implementation details.

**Implementation**: `BankAccount.java`

```java
BankAccount account = new BankAccount("ACC001", 1000.0);
account.deposit(500.0);
account.withdraw(200.0);
System.out.println("Balance: $" + account.getBalance());
```

**Key Features**:
- Private fields (hidden data)
- Public getter/setter methods (controlled access)
- Data validation in setter methods
- Business logic methods (deposit, withdraw, transfer)

**Tests**: `BankAccountTest.java` - 23 comprehensive test cases covering:
- Balance validation
- Deposit/withdrawal validations
- Transfer operations
- Edge cases

---

## Inheritance

**Concept**: Inheritance allows a class (child/subclass) to inherit properties and methods from another class (parent/superclass). It promotes code reuse and establishes IS-A relationships.

**Implementation**: `Animal.java` (parent) → `Dog.java` (child)

```java
public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, int age, String breed) {
        super(name, age); // Call parent constructor
        this.breed = breed;
    }
    
    @Override
    public void makeSound() {
        System.out.println(name + " barks: Woof! Woof!");
    }
}

// Usage
Dog dog = new Dog("Buddy", 5, "Golden Retriever");
dog.makeSound(); // Outputs: Buddy barks: Woof! Woof!
dog.eat();       // Inherited from Animal
```

**Key Features**:
- Parent class: `Animal` with common attributes (name, age)
- Child class: `Dog` extends Animal with additional properties (breed)
- Method overriding: `makeSound()`
- Super keyword: Call parent constructor and methods
- Method inheritance: eat(), sleep()

**Tests**: `AnimalTest.java` - 10 test cases covering:
- Parent-child relationships
- Constructor chaining
- Method overriding
- instanceof checks

---

## Polymorphism

**Concept**: Polymorphism means "many forms." It allows objects of different types to be treated through the same parent interface. There are two types: compile-time (method overloading) and runtime (method overriding).

**Implementation**: `Shape.java` (abstract parent) → `Circle.java`, `Rectangle.java` (children)

```java
Shape circle = new Circle(5.0);
Shape rectangle = new Rectangle(4.0, 6.0);

System.out.println("Circle area: " + circle.calculateArea());
System.out.println("Rectangle area: " + rectangle.calculateArea());
```

**Key Features**:
- Abstract parent class: `Shape` with abstract methods
- Concrete implementations: `Circle` and `Rectangle`
- Different implementations of `calculateArea()` and `calculatePerimeter()`
- Polymorphic behavior: Same interface, different implementations

**Tests**: `ShapeTest.java` - 15 test cases covering:
- Area and perimeter calculations
- Different shape implementations
- Polymorphic behavior
- toString() representations

---

## Abstraction

**Concept**: Abstraction is the process of hiding complex internal implementation details and showing only the essential features. Abstract classes define a contract that subclasses must implement.

**Implementation**: `Vehicle.java` (abstract parent) → `Car.java`, `Motorcycle.java` (children)

```java
public abstract class Vehicle {
    protected String brand;
    
    // Abstract methods - must be implemented by subclasses
    public abstract void start();
    public abstract void stop();
    public abstract int getMaxSpeed();
    
    // Concrete method - shared by all vehicles
    public void displayInfo() {
        System.out.println("Vehicle: " + brand);
    }
}

// Concrete implementations
Car car = new Car("Toyota", "Camry", 2023);
Motorcycle motorcycle = new Motorcycle("Honda", "CBR", 2023);

car.start();           // Car-specific implementation
motorcycle.start();    // Motorcycle-specific implementation
```

**Key Features**:
- Abstract class: Cannot be instantiated directly
- Abstract methods: Must be implemented by subclasses
- Concrete methods: Shared implementation
- Different behaviors: Each vehicle implements start() differently
- Max speeds: Car (200 km/h), Motorcycle (180 km/h)

**Tests**: `VehicleTest.java` - 13 test cases covering:
- Abstract class usage
- Concrete implementations
- Polymorphic array handling
- Specific vehicle features

---

## Interfaces

**Concept**: An interface is a contract that defines a set of methods a class must implement. It supports multiple inheritance of type. All methods in an interface are abstract by default (Java 8+ allows default and static methods).

**Implementation**: `Flyable.java` interface → `Bird.java`, `Airplane.java`

```java
public interface Flyable {
    void fly();
    void land();
    int getMaxAltitude();
    
    // Default method (Java 8+)
    default void displayFlightInfo() {
        System.out.println("Max Altitude: " + getMaxAltitude());
    }
    
    // Static method (Java 8+)
    static void printFlightRules() {
        System.out.println("Follow air traffic control rules");
    }
}

// Multiple implementations
Flyable bird = new Bird("Eagle");
Flyable airplane = new Airplane("Boeing 747");

bird.fly();      // Bird-specific flying behavior
airplane.fly();  // Airplane-specific flying behavior
```

**Key Features**:
- Interface contract: Methods that must be implemented
- Multiple implementations: Different types can implement Flyable
- Default methods: Shared implementation in interface
- Static methods: Utility methods in interface
- Different behaviors: Bird flies differently than Airplane

**Tests**: `FlyableTest.java` - 14 test cases covering:
- Interface implementation
- Multiple implementations of same interface
- Default methods
- Polymorphic behavior with interfaces
- Specific implementation details

---

## Project Structure

```
02-oop-concepts/
├── pom.xml                      # Maven configuration
├── README.md                    # This file
├── src/
│   ├── main/java/com/learning/
│   │   ├── Main.java            # Entry point demonstrating all concepts
│   │   ├── Person.java          # Classes and Objects
│   │   ├── BankAccount.java     # Encapsulation
│   │   ├── Animal.java          # Inheritance (parent)
│   │   ├── Dog.java             # Inheritance (child)
│   │   ├── Shape.java           # Polymorphism (abstract parent)
│   │   ├── Circle.java          # Polymorphism (child)
│   │   ├── Rectangle.java       # Polymorphism (child)
│   │   ├── Vehicle.java         # Abstraction (abstract parent)
│   │   ├── Car.java             # Abstraction (child)
│   │   ├── Motorcycle.java      # Abstraction (child)
│   │   ├── Flyable.java         # Interface
│   │   ├── Bird.java            # Interface implementation
│   │   └── Airplane.java        # Interface implementation
│   └── test/java/com/learning/
│       ├── PersonTest.java      # Tests for Person class
│       ├── BankAccountTest.java # Tests for encapsulation
│       ├── AnimalTest.java      # Tests for inheritance
│       ├── ShapeTest.java       # Tests for polymorphism
│       ├── VehicleTest.java     # Tests for abstraction
│       └── FlyableTest.java     # Tests for interfaces
└── target/                      # Compiled output
```

---

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=PersonTest
mvn test -Dtest=BankAccountTest
mvn test -Dtest=AnimalTest
mvn test -Dtest=ShapeTest
mvn test -Dtest=VehicleTest
mvn test -Dtest=FlyableTest
```

### Run Specific Test Method

```bash
mvn test -Dtest=PersonTest#testDefaultConstructor
```

### View Test Coverage

```bash
mvn test
cd target/site/jacoco
open index.html
```

---

## Building the Project

### Compile

```bash
mvn clean compile
```

### Run Tests

```bash
mvn test
```

### Generate Code Coverage Report

```bash
mvn test jacoco:report
```

### Code Quality Checks

```bash
mvn checkstyle:check  # Code style validation
mvn pmd:check         # Static code analysis
mvn spotbugs:check    # Bug detection
```

### Build JAR

```bash
mvn package
```

---

## Test Results

The project includes **91 comprehensive test cases** covering all OOP concepts:

| Test Class        | Test Count | Coverage |
|------------------|-----------|----------|
| PersonTest       | 16        | ✅ 100%  |
| BankAccountTest  | 23        | ✅ 100%  |
| AnimalTest       | 10        | ✅ 100%  |
| ShapeTest        | 15        | ✅ 100%  |
| VehicleTest      | 13        | ✅ 100%  |
| FlyableTest      | 14        | ✅ 100%  |
| **Total**        | **91**    | ✅ 100%  |

---

## Key Learning Points

### 1. Classes and Objects
- Classes are templates, objects are instances
- Constructors initialize objects
- Methods define behavior
- Variables store state

### 2. Encapsulation
- Hide internal data with private variables
- Provide controlled access via public methods
- Validate data in setters
- Implement business logic

### 3. Inheritance
- Child classes extend parent classes with `extends` keyword
- Use `super` to call parent constructors and methods
- Override methods to provide specific behavior
- Establish IS-A relationships

### 4. Polymorphism
- Same interface, different implementations
- Parent reference can point to child objects
- Runtime method resolution (dynamic binding)
- Override methods in child classes
- Enable flexible and extensible code

### 5. Abstraction
- Use abstract classes to define contracts
- Abstract methods must be implemented by subclasses
- Concrete methods provide shared functionality
- Hide complex implementation details

### 6. Interfaces
- Define contracts without implementation
- Multiple classes can implement same interface
- Support multiple inheritance of type
- Use for flexible design and decoupling

---

## Best Practices Demonstrated

1. **Proper Naming Conventions**: Clear, descriptive class and method names
2. **Javadoc Documentation**: Comprehensive comments for all classes and methods
3. **Unit Testing**: 91 test cases with 100% coverage
4. **Code Organization**: Logical grouping of related classes
5. **Access Modifiers**: Appropriate use of public, private, and protected
6. **Constructor Overloading**: Flexible object creation
7. **Method Overriding**: Polymorphic behavior
8. **Error Handling**: Input validation in methods
9. **Consistent Code Style**: Proper indentation and formatting
10. **SOLID Principles**: Single responsibility, open/closed, Liskov substitution

---

## Further Reading

- [Java Tutorials - OOP Concepts](https://docs.oracle.com/javase/tutorial/java/concepts/)
- [Effective Java - Joshua Bloch](https://www.oreilly.com/library/view/effective-java-3rd/9780134685991/)
- [Design Patterns - Gang of Four](https://en.wikipedia.org/wiki/Design_Patterns)

---

## Author

Java Learning Team

## Version

1.0.0

## Last Updated

March 2026
