# 📌 OOP Concepts - Quick Reference Sheet

Use this sheet for quick lookups while coding or studying.

---

## 🏗️ The Four Pillars of OOP

| Pillar | Description | Key Benefit |
|--------|-------------|-------------|
| **Encapsulation** | Bundling data + methods, restricting access | Data protection, maintainability |
| **Inheritance** | Class inherits properties from parent | Code reuse, hierarchy |
| **Polymorphism** | One interface, many implementations | Flexibility, extensibility |
| **Abstraction** | Hiding complexity, showing essentials | Simplicity, modularity |

---

## 🎯 Class vs Object

```
Class = Blueprint/Template
Object = Instance of a class

class Car {          // Class definition
    String color;    // Field
    void drive() {} // Method
}

Car myCar = new Car();  // Object creation
```

---

## 🔒 Access Modifiers

| Modifier | Class | Package | Subclass | World |
|----------|-------|---------|----------|-------|
| `public` | ✅ | ✅ | ✅ | ✅ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `default` | ✅ | ✅ | ❌ | ❌ |
| `private` | ✅ | ❌ | ❌ | ❌ |

---

## 🧬 Inheritance Types

```java
// Single Inheritance
class A {}
class B extends A {}

// Multilevel Inheritance
class A {}
class B extends A {}
class C extends B {}

// Hierarchical Inheritance
class A {}
class B extends A {}
class C extends A {}

// Multiple Inheritance (via interfaces)
interface A {}
interface B {}
class C implements A, B {}
```

---

## 🔀 Constructors

```java
class Person {
    String name;
    int age;

    // Default constructor
    Person() {}

    // Parameterized constructor
    Person(String n, int a) {
        name = n;
        age = a;
    }

    // Copy constructor
    Person(Person other) {
        this.name = other.name;
        this.age = other.age;
    }
}
```

---

## 🔄 Method Overloading vs Overriding

| Aspect | Overloading | Overriding |
|--------|-------------|------------|
| Location | Same class | Subclass |
| Signature | Different | Same |
| Return | Can differ | Same |
| Binding | Compile-time | Runtime |

```java
// Overloading (same class)
int add(int a, int b) { return a + b; }
double add(double a, double b) { return a + b; }

// Overriding (subclass)
class Parent {
    void display() { System.out.println("Parent"); }
}
class Child extends Parent {
    @Override
    void display() { System.out.println("Child"); }
}
```

---

## 🌟 Key OOP Principles

### Composition over Inheritance
```java
// Better than extending
class Engine {}
class Car {
    private Engine engine; // Has-a relationship
}
```

### Liskov Substitution Principle
```java
// Subclass must be usable as parent
class Bird { void fly() {} }
class Penguin extends Bird {
    @Override void fly() { throw new Error(); } // ❌ Violates LSP
}
```

### Interface Segregation
```java
// Many specific interfaces > one general
interface Printer { void print(); }
interface Scanner { void scan(); }
class AllInOne implements Printer, Scanner {} // ✅
```

---

## 📦 Key OOP Patterns

| Pattern | Use Case |
|---------|----------|
| Singleton | One instance only |
| Factory | Object creation logic |
| Builder | Complex object construction |
| Observer | Event handling |
| Strategy | Interchangeable algorithms |

---

## ⚠️ Common OOP Mistakes

1. **God Class** - Too many responsibilities
2. **Deep Inheritance** - Hard to maintain
3. **Missing Abstraction** - Exposing internal details
4. **Tight Coupling** - Hard to change
5. **Not using interfaces** - Code depends on concrete classes

---

## 🎓 Interview Quick Notes

- **Encapsulation**: Data hiding via getters/setters
- **Inheritance**: `extends` keyword, `super()` call
- **Polymorphism**: Method overloading (compile) vs overriding (runtime)
- **Abstraction**: Abstract classes and interfaces
- **this vs super**: Current instance vs parent
- **final**: Prevents inheritance/overriding

---

**Remember**: "Favor composition over inheritance"