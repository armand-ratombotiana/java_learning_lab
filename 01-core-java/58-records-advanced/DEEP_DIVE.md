# Deep Dive: Advanced Records

## 1. Beyond Simple Data Carriers
Java Records (introduced in Java 14, standard in 16) are often described simply as "boilerplate reducers" or "Java's answer to Lombok." While they do eliminate boilerplate (getters, `equals`, `hashCode`, `toString`), their true purpose is much deeper: they are **transparent carriers for immutable data**.

A Record is a restricted form of a class. When you define a record, you are making a strong semantic commitment: *The state of this object is fully described by its components, and nothing else.*

## 2. Record Components and the Canonical Constructor
The variables declared in the record header are called **components**.
```java
public record User(String username, int age) {}
```
The compiler automatically generates:
1.  `private final` fields for each component.
2.  Public accessor methods (e.g., `username()`, NOT `getUsername()`).
3.  A **Canonical Constructor** that takes all components as arguments and assigns them to the fields.

### The Compact Constructor
If you need to validate data or make defensive copies, you do not need to write out the full canonical constructor. You can use a **Compact Constructor**.

```java
public record User(String username, int age) {
    // Compact constructor: No arguments listed, no 'this.age = age' needed!
    public User {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        // The compiler automatically inserts 'this.username = username' and 'this.age = age' at the end.
    }
}
```
*   **Defensive Copying**: The compact constructor is the exact place where you must perform defensive copying if a component is a mutable object (like a `List` or `Date`).

## 3. Customizing Records
While Records are restricted, they still allow significant customization:

*   **Static Fields and Methods**: Allowed.
*   **Custom Methods**: You can add new instance methods (e.g., `public boolean isAdult() { return age >= 18; }`).
*   **Overriding Accessors**: You can override the generated accessor methods (e.g., to return a defensive copy of a mutable field).
*   **Implementing Interfaces**: Records can implement interfaces (e.g., `public record User(...) implements Serializable, Comparable<User>`).
*   **Multiple Constructors**: You can define additional constructors, but they **must** ultimately delegate to the canonical constructor using `this(...)`.

```java
public record User(String username, int age) {
    // Custom constructor
    public User(String username) {
        this(username, 0); // MUST delegate to the canonical constructor
    }
}
```

## 4. What Records Cannot Do
To maintain their semantic guarantee as transparent data carriers, Records have strict limitations:
1.  **Cannot extend any class**: They implicitly extend `java.lang.Record`. Java does not support multiple inheritance of classes.
2.  **Cannot be extended**: Records are implicitly `final`.
3.  **Cannot declare instance fields**: You cannot add any non-static fields that are not part of the record header. The components *must* define the entire state.

## 5. Serialization Guarantees
Java Serialization is notoriously dangerous because it bypasses constructors (see Module 43). 
**Records fix this.**
The serialization specification was explicitly updated for Records. When a Record is deserialized, the JVM reads the values from the byte stream and **passes them through the canonical constructor**. 

This means that any validation logic or defensive copying you put in your compact constructor is *guaranteed* to execute during deserialization, making Records infinitely safer than standard Serializable classes.

## 6. Record Patterns (Java 21)
As seen in Module 49, Records integrate perfectly with Pattern Matching. Because the compiler knows exactly what components make up a Record, it can automatically deconstruct the Record into local variables.

```java
if (obj instanceof User(String name, int age)) {
    System.out.println(name + " is " + age + " years old.");
}
```