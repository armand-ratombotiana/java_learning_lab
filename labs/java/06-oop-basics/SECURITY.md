# Security — OOP Basics

## Object Immutability for Security

Immutable objects are inherently thread-safe and prevent tampering:
```java
public final class Credentials {
    private final String username;
    private final String passwordHash;
    // Only getters, no setters
}
```

## Leaking this in Constructor

Never start threads or publish the object reference from a constructor — other threads may see a partially constructed object.

## Defensive Copying

Return copies of mutable internal objects: `return new ArrayList<>(internalList);`

## Serialization Security

Serialization can bypass constructor validation. Implement `readObject()` to validate deserialized state. Use `Serializable` with caution.

## SecurityManager Deprecation

Java 17 deprecated SecurityManager. Rely on OS-level security, module system (`module-info.java`), and input validation instead.
