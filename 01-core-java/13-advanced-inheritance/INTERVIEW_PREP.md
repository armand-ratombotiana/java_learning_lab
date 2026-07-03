# Module 13: Advanced Inheritance Patterns - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: How does Java resolve the Diamond Problem using default methods?
**Answer**:
If a class implements two interfaces that provide a default method with the exact same signature, the compiler throws an error. The class *must* override the method. Within the overridden method, the class can explicitly choose which interface's default method to call using the syntax `InterfaceName.super.methodName()`.

### Q2: What are Sealed Classes and why were they introduced?
**Answer**:
Introduced in Java 15 (preview) and standardized in Java 17, `sealed` classes and interfaces restrict which other classes or interfaces may extend or implement them using the `permits` keyword. This allows developers to create closed, known hierarchies, which is highly beneficial for exhaustive Pattern Matching in `switch` expressions, ensuring no unexpected subclasses exist.

### Q3: Why is "Composition over Inheritance" often recommended?
**Answer**:
Inheritance creates a tight coupling between the parent and child class. A change in the parent can break the child (the fragile base class problem). It also locks a class into a single hierarchy, preventing it from reusing code from other sources.
Composition promotes loose coupling by injecting behavior via interfaces and collaborating objects, making the system more modular, testable, and flexible at runtime.

---

## 💻 Whiteboarding Scenarios

### Scenario: Modeling a Vehicle System
**Problem**: Design a system that has Cars, Boats, and AmphibiousVehicles. Do not use deep class inheritance that leads to duplicated code.

**Solution**:
Use Composition via Interfaces with default methods.
```java
interface Drivable {
    default void drive() { System.out.println("Driving on road"); }
}

interface RunnableInWater {
    default void sail() { System.out.println("Sailing on water"); }
}

class Car implements Drivable {}
class Boat implements RunnableInWater {}
class AmphibiousVehicle implements Drivable, RunnableInWater {}
```