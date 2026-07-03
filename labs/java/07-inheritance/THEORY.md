# Inheritance — Theoretical Foundation

## IS-A Relationship

Inheritance models an IS-A relationship: a `Dog` IS-A `Animal`, a `SavingsAccount` IS-A `BankAccount`. A subclass extends the superclass, inheriting its fields and methods.

```java
public class Animal {
    protected String name;

    public void eat() {
        System.out.println(name + " is eating");
    }
}

public class Dog extends Animal {
    public void bark() {
        System.out.println(name + " is barking");
    }
}
```

Inheritance is transitive: if C extends B extends A, then C IS-A B and C IS-A A.

## The `extends` Keyword

```java
public class SubClass extends SuperClass {
    // Inherits all accessible members
    // Can add new members
    // Can override methods
}
```

Java supports single inheritance for classes — a class can extend only one superclass. This eliminates the complexity and ambiguity of multiple inheritance (like C++'s diamond problem).

## The `super` Keyword

`super` refers to the immediate superclass. Uses:

1. Access superclass field: `super.fieldName`
2. Call superclass method: `super.methodName()`
3. Call superclass constructor: `super(args)`

```java
public class Manager extends Employee {
    private double bonus;

    public Manager(String name, double salary, double bonus) {
        super(name, salary);  // Must be first statement in constructor
        this.bonus = bonus;
    }

    @Override
    public double getSalary() {
        return super.getSalary() + bonus;  // Calls superclass method
    }
}
```

If a constructor does not explicitly call `super(args)`, the compiler inserts `super()` (no-arg superclass constructor). If the superclass has no no-arg constructor, this causes a compilation error.

## Method Overriding

A subclass can provide a specific implementation of a method defined in the superclass.

Rules:
- Same method signature (name and parameter types)
- Return type must be the same or a subtype (covariant return)
- Access modifier cannot be more restrictive
- Cannot override `final` or `static` methods
- Overriding method should throw the same, subtype, or no exception (not broader exceptions)

```java
@Override  // Recommended — compiler checks overriding rules
public String toString() {
    return "MyClass: " + value;
}
```

The `@Override` annotation triggers a compiler error if the method does not actually override a superclass method — catches typos and signature mismatches early.

## The `final` Keyword

| Usage | Effect |
|-------|--------|
| `final class` | Cannot be subclassed |
| `final method` | Cannot be overridden |
| `final variable` | Cannot be reassigned |

`final` classes include `String`, `Integer`, `Math`. `final` methods enable JIT inlining (no dynamic dispatch needed).

## The Object Class

Every class in Java inherits from `java.lang.Object` (directly or indirectly). Key methods:

| Method | Purpose |
|--------|---------|
| `equals(Object)` | Value equality (default: reference equality) |
| `hashCode()` | Hash code for hash-based collections |
| `toString()` | String representation |
| `clone()` | Creates and returns a copy |
| `finalize()` | Called before GC (deprecated in Java 9) |
| `getClass()` | Returns the runtime Class object |
| `notify()`, `wait()` | Thread synchronization |

### equals() and hashCode() Contract

1. If `a.equals(b)` then `a.hashCode() == b.hashCode()` (must)
2. If `a.hashCode() == b.hashCode()` then `a.equals(b)` can be true or false (allowed — hash collision)

Both should be overridden together. Eclipse/IntelliJ can auto-generate correct implementations.

## Constructor Chaining

Constructors are called from the top of the hierarchy down: Object → Superclass → Subclass.

```java
class A { A() { System.out.println("A"); } }
class B extends A { B() { System.out.println("B"); } }
class C extends B { C() { System.out.println("C"); } }

new C();
// Output: A B C
```

## Composition vs Inheritance

Prefer composition (HAS-A) over inheritance (IS-A):

```java
// Composition — more flexible
public class Car {
    private Engine engine;  // Car HAS-A Engine
    private List<Wheel> wheels;
}

// Inheritance — tighter coupling
public class Car extends Vehicle { }  // Car IS-A Vehicle
```

Favor composition when the relationship is not clearly IS-A or when the superclass design is unstable.
