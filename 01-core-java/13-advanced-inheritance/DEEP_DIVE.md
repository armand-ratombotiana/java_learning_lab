# Deep Dive: Advanced Inheritance Patterns

---

## 1. Multiple Inheritance in Java

### Why No Multiple Inheritance?

Java avoids multiple inheritance of classes due to the **diamond problem**:

```java
class A { void show() { } }
class B extends A { }
class C extends A { }
class D extends B, C { }  // Ambiguous! Which show()?
```

### Solutions in Java

1. **Interface composition** (preferred)
2. **Default methods in interfaces** (Java 8+)
3. **Composition over inheritance**

---

## 2. Interface Default Methods

### Basic Default Method
```java
interface Printable {
    default void print() {
        System.out.println("Printing...");
    }
}
```

### Multiple Interface Conflict
```java
interface A { default void show() { System.out.println("A"); } }
interface B { default void show() { System.out.println("B"); } }

class C implements A, B {
    public void show() {
        A.super.show();  // Resolve: call A's version
    }
}
```

---

## 3. Mixin Patterns

### Real Mixin via Interface + Helper
```java
// Mixin interface
interface Walkable {
    default void walk() { System.out.println("Walking"); }
}

interface Swimmable {
    default void swim() { System.out.println("Swimming"); }
}

// Compose into a class
class Duck implements Walkable, Swimmable {
    // Can override if needed
    @Override
    public void swim() { System.out.println("Duck swimming"); }
}
```

---

## 4. The Diamond Problem

### With Interfaces (No Problem)
```java
interface A { default void display() { System.out.println("A"); } }
interface B extends A { }
interface C extends A { }
class D implements B, C {  // Works fine!
    // Inherits A's default
}
```

### With Classes (Problematic)
```java
abstract class A { abstract void show(); }
class B extends A { void show() { } }
class C extends A { void show() { } }
// class D extends B, C { }  // ERROR: ambiguous
```

---

## 5. Composition over Inheritance

### Problem with Inheritance
```java
class Bird extends Animal { }  // What about FlyingBird?
class FlyingBird extends Bird { }  // Deep hierarchy!
```

### Solution: Composition
```java
interface Flyable { void fly(); }
interface Swimmable { void swim(); }

class Duck implements Flyable, Swimmable {
    // Compose behavior, don't extend
}
```

---

## 6. Sealed Classes (Java 17)

### Restrict Inheritance
```java
sealed class Shape permits Circle, Rectangle, Triangle { }

final class Circle extends Shape { }      // Complete implementation
non-sealed class Rectangle extends Shape { }  // Can be further extended
sealed class Triangle extends Shape permits Equilateral { }
```

---

## Key Takeaways

1. Use **interfaces** for multiple "is-a" relationships
2. Use **composition** for "has-a" relationships
3. Use **sealed classes** to control inheritance
4. Use **default methods** for optional behavior
5. Resolve conflicts explicitly with `Interface.super.method()`

---

## Code Example

```java
public class InheritanceDemo {
    public static void main(String[] args) {
        Duck duck = new Duck();
        duck.walk();
        duck.swim();
        duck.fly();
    }
}

interface Flyable {
    default void fly() { System.out.println("Flying"); }
}

interface Walkable {
    default void walk() { System.out.println("Walking"); }
}

interface Swimmable {
    default void swim() { System.out.println("Swimming"); }
}

class Duck implements Walkable, Swimmable, Flyable { }
```