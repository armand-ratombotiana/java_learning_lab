# Deep Dive: Advanced Encapsulation

## 1. Beyond Basic Access Modifiers
Encapsulation is often taught simply as "make fields private and provide public getters/setters." However, true encapsulation is about hiding the *internal state and invariants* of an object, exposing only behavior that maintains those invariants.

### The Limits of Getters and Setters
If a class has private fields but public getters and setters for all of them, it is barely encapsulated. It acts as a dumb data container (an anemic domain model). Advanced encapsulation favors "Tell, Don't Ask" — telling objects what to do rather than asking for their state and manipulating it externally.

## 2. Immutability Patterns
An immutable object's state cannot be modified after it is created. This provides extreme thread safety and predictability.

### Rules for True Immutability in Java:
1.  Make the class `final` so it cannot be subclassed (subclasses could override methods to return mutable state).
2.  Make all fields `private` and `final`.
3.  Do not provide any setter methods.
4.  **Defensive Copying**: If a field is a mutable object (like a `Date` or `ArrayList`), you must make a deep copy of it in the constructor, and return a clone of it in the getter.

```java
public final class Period {
    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        // Defensive copy in constructor
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if (this.start.after(this.end)) throw new IllegalArgumentException();
    }

    public Date getStart() {
        // Defensive copy in getter
        return new Date(start.getTime());
    }
}
```

## 3. Java Records (Data Carriers)
Introduced in Java 14 (preview) and standardized in Java 16, Records provide a concise syntax for declaring immutable data carrier classes.

```java
public record User(String name, int age) {}
```
*   **What you get for free**: Private final fields, a canonical constructor, public accessors (e.g., `name()`, not `getName()`), `equals()`, `hashCode()`, and `toString()`.
*   **Encapsulation in Records**: Records are fundamentally transparent. They are designed to encapsulate *data*, not *behavior*. If you need to hide state or enforce complex business invariants that mutate over time, a Record is the wrong choice.

## 4. Sealed Classes (Controlled Inheritance)
Introduced in Java 15 (preview) and standardized in Java 17, Sealed Classes allow a class or interface to restrict which other classes may extend or implement it.

### Why Seal?
Historically, you could only prevent inheritance by making a class `final` (preventing *all* inheritance) or making constructors package-private. Sealed classes provide fine-grained control, essential for Domain-Driven Design (DDD) and pattern matching.

```java
public abstract sealed class Shape permits Circle, Square, Rectangle {}

public final class Circle extends Shape { /* ... */ }
public non-sealed class Square extends Shape { /* ... */ }
public sealed class Rectangle extends Shape permits TransparentRectangle { /* ... */ }
```

### The Rules of Sealing:
1.  The permitted subclasses must be available at compile time.
2.  Subclasses must directly extend the sealed class.
3.  Every permitted subclass must choose one of three modifiers:
    *   `final`: Cannot be extended further.
    *   `sealed`: Can be extended, but strictly controls its own subclasses.
    *   `non-sealed`: Opens the hierarchy back up to any subclass.

## 5. Module System Encapsulation (Jigsaw)
Since Java 9, encapsulation moved beyond the class level to the *package* and *module* level.
Using `module-info.java`, you can explicitly declare which packages are visible to the outside world (`exports`) and which are strictly internal. Even reflection cannot penetrate a non-exported package without explicit JVM arguments (`--add-opens`).