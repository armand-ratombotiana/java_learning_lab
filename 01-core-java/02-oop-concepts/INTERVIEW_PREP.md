# Module 02: OOP Concepts - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What are the four pillars of OOP?
**Answer**:
1. **Encapsulation**: Bundling data (variables) and methods that operate on that data into a single unit (class), restricting direct access via access modifiers (private).
2. **Inheritance**: A mechanism where a new class derives properties and behaviors from an existing class, promoting code reuse.
3. **Polymorphism**: The ability of a single interface or method to represent different underlying forms (overloading = compile-time, overriding = runtime).
4. **Abstraction**: Hiding complex implementation details and exposing only the essential features (via interfaces and abstract classes).

### Q2: Abstract Class vs Interface
**Answer**:
- **State**: Abstract classes can have state (instance variables) and constructors. Interfaces cannot have state (only `public static final` constants).
- **Inheritance**: A class can extend only one abstract class, but it can implement multiple interfaces.
- **Methods**: Abstract classes can have fully implemented methods. (Interfaces can too via `default` methods in Java 8+, but their primary purpose is defining a contract).
- **Use Case**: Use abstract classes when classes share a core "is-a" identity and shared code. Use interfaces to define capabilities or roles ("can-do").

### Q3: What is method overloading vs method overriding?
**Answer**:
- **Overloading (Compile-time Polymorphism)**: Multiple methods in the same class share the same name but have different parameter lists (type, count, or order). Return type alone is not sufficient.
- **Overriding (Runtime Polymorphism)**: A subclass provides a specific implementation of a method that is already defined in its superclass. The method signature must be exactly the same.

---

## 💻 Whiteboarding / System Design Scenarios

### Scenario: Design a Parking Lot
**Problem**: Design an object-oriented system for a parking lot.

**Key Entities to Define**:
- **Vehicle (Abstract)**: Extended by `Car`, `Motorcycle`, `Truck`. Has properties like `licensePlate`, `VehicleSize`.
- **ParkingSpot**: Has a `SpotSize`. Can check `isAvailable()` and `park(Vehicle v)`.
- **Level**: Contains a collection of `ParkingSpot` objects. Can find an available spot for a specific vehicle size.
- **ParkingLot**: Contains multiple `Level` objects. Handles ticketing and payments.

**Interview Focus**:
The interviewer is looking for how you handle polymorphism (e.g., matching a `VehicleSize.LARGE` truck to a `SpotSize.LARGE` spot) and encapsulation (hiding the logic of finding a spot inside the `Level` class rather than exposing the array of spots).