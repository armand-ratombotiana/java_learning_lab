# OOP - Edge Cases

## Overview

This document covers tricky scenarios, common pitfalls, and edge cases in Object-Oriented Programming.

---

## 1. Inheritance vs Composition

### Problem
Inheritance can create fragile hierarchies.

```java
// Problem: Tight coupling through inheritance
public class Stack extends ArrayList {  // Bad design!
    public void push(Object item) {
        this.add(item);
    }
    
    public Object pop() {
        if (this.size() == 0) throw new EmptyStackException();
        return this.remove(this.size() - 1);
    }
}
```

### Solution
Prefer composition over inheritance.

```java
// Better: Composition
public class Stack<T> {
    private List<T> items = new ArrayList<>();
    
    public void push(T item) {
        items.add(item);
    }
    
    public T pop() {
        if (items.isEmpty()) throw new EmptyStackException();
        return items.remove(items.size() - 1);
    }
}
```

---

## 2. The Circle-Ellipse Problem (LSP Violation)

### Problem
Square is-a Rectangle, but behavior differs.

```java
public class Rectangle {
    protected int width, height;
    
    public void setWidth(int w) { width = w; }
    public void setHeight(int h) { height = h; }
    public int area() { return width * height; }
}

public class Square extends Rectangle {
    @Override
    public void setWidth(int w) {
        width = w;
        height = w;  // Violates Liskov Substitution!
    }
    
    @Override
    public void setHeight(int h) {
        width = h;
        height = h;  // Breaks expectations!
    }
}

// This fails unexpectedly:
Rectangle r = new Square();
r.setWidth(5);
r.setHeight(3);
System.out.println(r.area());  // Expected: 15, Got: 9
```

### Solution
Model shapes correctly.

```java
public interface Shape {
    double area();
}

public interface Rectangle extends Shape {
    void setWidth(double w);
    void setHeight(double h);
}

public interface Square extends Shape {
    void setSide(double s);
}
```

---

## 3. Multiple Inheritance of State

### Problem
Java doesn't allow multiple inheritance of classes.

```java
// Can't do this:
public class A { int x = 1; }
public class B { int x = 2; }
public class C extends A, B {}  // Error!
```

### Solution
Use interfaces for multiple "inheritance" of behavior.

```java
public interface A { int getX(); }
public interface B { void setX(int x); }

public class C implements A, B {
    private int x = 0;
    
    @Override
    public int getX() { return x; }
    
    @Override
    public void setX(int x) { this.x = x; }
}
```

---

## 4. Default Method Conflicts

### Problem
Multiple interfaces with same default method.

```java
public interface A {
    default void method() { System.out.println("A"); }
}

public interface B {
    default void method() { System.out.println("B"); }
}

public class C implements A, B {
    // Which method to use? Compilation error!
}
```

### Solution
Override the conflicting method.

```java
public class C implements A, B {
    @Override
    public void method() {
        A.super.method();  // Choose one, or combine
        // Or: B.super.method();
        // Or: System.out.println("C");
    }
}
```

---

## 5. Diamond Problem with Default Methods

### Problem
Complex inheritance with defaults.

```java
public interface A { default void m() { System.out.println("A"); } }

public interface B extends A {}  // Inherits A's default

public interface C extends A {}  // Inherits A's default

public class D implements B, C {
    public static void main(String[] args) {
        new D().m();  // Which A's default?
    }
}
```

### Solution
Still works in this case (single A). Complex cases need explicit override.

```java
public class D implements B, C {
    @Override
    public void m() {
        A.super.m();  // Explicitly call A's default
    }
}
```

---

## 6. Static Methods in Interfaces

### Problem
Static methods aren't inherited.

```java
public interface A {
    static void staticMethod() { System.out.println("A's static"); }
}

public interface B extends A {
    static void staticMethod() { System.out.println("B's static"); }
}

public class Main {
    public static void main(String[] args) {
        A.staticMethod();  // Works
        B.staticMethod();  // Works (B's own)
        // A a = new B();
        // a.staticMethod();  // Error! Can't call static via instance
    }
}
```

### Solution
Call static methods via interface name only.

---

## 7. Abstract Class vs Interface

### Problem
Confusion about when to use which.

```java
// When abstract class is better:
public abstract class Animal {
    protected String name;
    
    public Animal(String name) {
        this.name = name;
    }
    
    // Common implementation
    public void sleep() {
        System.out.println(name + " is sleeping");
    }
    
    // To be overridden
    public abstract void makeSound();
}

// Interface for pure abstraction:
public interface Serializable {
    void serialize();
    void deserialize();
}
```

### Decision Tree

```
Is it a type or capability?
├── Type (is-a relationship) → Abstract class
└── Capability (can-do) → Interface

Need to share code/state?
├── Yes → Abstract class
└── No → Interface

Multiple inheritance needed?
├── Yes → Interfaces
└── No → Either
```

---

## 8. Private Methods in Interfaces (Java 9)

### Problem
Default methods might have duplicated code.

```java
public interface Calculator {
    default int complexOperation(int x, int y) {
        int a = preprocess(x);
        int b = preprocess(y);
        return doSomething(a, b);
    }
    
    // Can I extract common code? Yes with private methods!
    private int preprocess(int value) {
        // Common preprocessing
        return value * 2;
    }
    
    private int doSomething(int a, int b) {
        return a + b;
    }
}
```

### Solution
Use private methods (Java 9+).

```java
public interface Calculator {
    default int complexOperation(int x, int y) {
        int a = preprocess(x);
        int b = preprocess(y);
        return calculate(a, b);
    }
    
    private int preprocess(int value) {
        return value * 2;
    }
    
    private int calculate(int a, int b) {
        return a + b;
    }
}
```

---

## 9. Covariant Arrays

### Problem
Arrays are covariant and can cause runtime errors.

```java
Integer[] ints = new Integer[5];
Number[] numbers = ints;  // Allowed (arrays covariant)

numbers[0] = 3.14;  // Compiles, but ArrayStoreException at runtime!
```

### Solution
Be careful with arrays of parameterized types.

```java
// Use List instead
List<Integer> intList = new ArrayList<>();
List<Number> numberList = intList;  // Compile error!
```

---

## 10. Final Methods and Inheritance

### Problem
Private methods aren't overridden.

```java
public class Parent {
    private void method() { System.out.println("Parent private"); }
    
    public void callMethod() {
        method();  // Calls Parent's method
    }
}

public class Child extends Parent {
    private void method() { System.out.println("Child private"); }
    
    // This is NOT overriding - it's a new method!
}

// Usage
Child c = new Child();
c.callMethod();  // Prints "Parent private"
```

### Solution
Use @Override to catch mistakes.

```java
public class Child extends Parent {
    @Override  // Would cause compile error if truly overriding
    private void method() {  // This is a new method
        System.out.println("Child private");
    }
}
```

---

## 11. Static vs Instance Members

### Problem
Static methods can't be overridden.

```java
public class Parent {
    public static void staticMethod() {
        System.out.println("Parent static");
    }
}

public class Child extends Parent {
    public static void staticMethod() {
        System.out.println("Child static");
    }
}

public class Main {
    public static void main(String[] args) {
        Parent p = new Child();
        p.staticMethod();  // Prints "Parent static"!
    }
}
```

### Solution
Static methods are hidden, not overridden. Use instance methods for polymorphism.

```java
public class Parent {
    public void instanceMethod() {
        System.out.println("Parent instance");
    }
}

public class Child extends Parent {
    @Override
    public void instanceMethod() {
        System.out.println("Child instance");
    }
}
```

---

## 12. equals() and hashCode() Contract

### Problem
Breaking the contract between equals and hashCode.

```java
public class Person {
    private String name;
    private int age;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person p)) return false;
        return age == p.age && Objects.equals(name, p.name);
    }
    
    // Problem: Not overriding hashCode!
    // This breaks HashMap, HashSet, etc.
}
```

### Solution
Always override both or neither.

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Person p)) return false;
    return age == p.age && Objects.equals(name, p.name);
}

@Override
public int hashCode() {
    return Objects.hash(name, age);
}
```

---

## 13. Cloning Issues

### Problem
Default clone() has issues.

```java
public class Student implements Cloneable {
    private String name;
    private List<String> courses;
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();  // Shallow copy - problems!
    }
}

Student s1 = new Student();
Student s2 = (Student) s1.clone();
// s2.courses points to same list as s1.courses!
```

### Solution
Implement deep clone or use copy constructor/factory.

```java
@Override
public Student clone() {
    try {
        Student cloned = (Student) super.clone();
        cloned.courses = new ArrayList<>(this.courses);  // Deep copy
        return cloned;
    } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
    }
}
```

---

## 14. Serialization and Singleton

### Problem
Deserialization can create new instances.

```java
public class Singleton implements Serializable {
    private static final Singleton INSTANCE = new Singleton();
    
    private Singleton() {}
    
    public static Singleton getInstance() {
        return INSTANCE;
    }
}

// After deserialization:
Singleton s1 = Singleton.getInstance();
Singleton s2 = (Singleton) deserialize(s1);
System.out.println(s1 == s2);  // false!
```

### Solution
Implement readResolve.

```java
public class Singleton implements Serializable {
    private static final Singleton INSTANCE = new Singleton();
    
    private Singleton() {}
    
    public static Singleton getInstance() {
        return INSTANCE;
    }
    
    protected Object readResolve() {
        return INSTANCE;  // Return singleton
    }
}
```

---

## Summary Table

| Edge Case | Problem | Solution |
|-----------|---------|----------|
| Inheritance vs Composition | Tight coupling | Prefer composition |
| Circle-Ellipse | LSP violation | Model correctly |
| Default method conflict | Ambiguous | Override explicitly |
| Static methods | Not inherited | Use interface reference |
| Arrays covariance | Runtime errors | Use generics |
| Private methods | Not overridden | Use different names |
| Static members | Not polymorphic | Use instance methods |
| equals/hashCode | Broken contract | Override both |
| Clone | Shallow copy | Implement deep clone |
| Serialization | New singleton | Implement readResolve |
