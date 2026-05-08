# Exercises: Advanced Inheritance Patterns

Practice exercises for advanced inheritance in Java.

---

## Exercise 1: Interface Composition

Create an interface-based mixin system:

```java
interface Walkable { void walk(); }
interface Swimmable { void swim(); }

// Create a Duck class that implements both
```

---

## Exercise 2: Diamond Problem

Solve the diamond problem using interfaces:

```
      A
     / \
    B   C
     \ /
      D
```

Create interfaces A, B, C and class D that inherits from both B and C.

---

## Exercise 3: Default Methods

Create a hierarchy with default method conflicts:

```java
interface A { default void display() { } }
interface B { default void display() { } }
class C implements A, B { }
```

Resolve the conflict.

---

## Solutions

### Exercise 1: Interface Composition
```java
class Duck implements Walkable, Swimmable {
    public void walk() { System.out.println("Walking"); }
    public void swim() { System.out.println("Swimming"); }
}
```

### Exercise 3: Resolution
```java
class C implements A, B {
    public void display() {
        A.super.display();  // Choose which to use
    }
}
```

---

## Next Steps

- Review [README.md](./README.md) for detailed topic coverage
- Practice implementing inheritance patterns in your own projects