# Edge Cases & Pitfalls: Advanced Inheritance

---

## Pitfall 1: Interface vs Abstract Class

### ❌ Wrong
```java
// Using abstract class when interface is better
abstract class Animal {
    abstract void eat();
    abstract void sleep();
    void breathe() { }  // Can't override without abstract
}

class Dog extends Animal { }  // Must implement all
```

### ✅ Correct
```java
// Use interface for behavior contracts
interface Animal {
    void eat();
    void sleep();
    default void breathe() { }  // Optional
}

class Dog implements Animal { }
```

---

## Pitfall 2: Forgetting Default Method Conflicts

### ❌ Wrong
```java
interface A { default void log() { System.out.println("A"); } }
interface B { default void log() { System.out.println("B"); } }

class C implements A, B { }  // COMPILATION ERROR!
```

### ✅ Correct
```java
class C implements A, B {
    public void log() {
        A.super.log();  // Must resolve explicitly
    }
}
```

---

## Pitfall 3: Deep Inheritance Hierarchies

### ❌ Wrong
```java
class Animal { }
class Mammal extends Animal { }
class Dog extends Mammal { }
class Bulldog extends Dog { }  // Too deep!
```

### ✅ Correct
```java
// Prefer composition
interface Animal { }
interface Mammal extends Animal { }
interface Dog extends Mammal { }
// Keep hierarchy shallow
```

---

## Pitfall 4: Sealed Class Mishandling

### ❌ Wrong
```java
sealed class Shape permits Circle { }
class Square extends Shape { }  // ERROR: Square not permitted!
```

### ✅ Correct
```java
sealed class Shape permits Circle, Rectangle, Square { }
final class Circle extends Shape { }
final class Rectangle extends Shape { }
final class Square extends Shape { }
```

---

## Checklist

- [ ] Use interfaces for multiple behavior contracts
- [ ] Resolve default method conflicts explicitly
- [ ] Prefer composition over deep inheritance
- [ ] Use sealed classes to control inheritance
- [ ] Avoid implementing interface with conflicting defaults