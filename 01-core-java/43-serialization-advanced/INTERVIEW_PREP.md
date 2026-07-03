# Interview Preparation: Advanced Serialization

This document covers advanced questions related to serialization security, the proxy pattern, and version control.

## Q1: Why is Java Serialization considered a major security vulnerability?
**Answer:**
Java serialization allows arbitrary byte streams to be converted into live Java objects. When `ObjectInputStream.readObject()` is called, the JVM reads the class name from the stream, loads the class, and instantiates it.
If an attacker sends a malicious byte stream containing a class that exists on the server's classpath (a "Gadget"), the JVM will instantiate it. If that class executes code during its deserialization process (e.g., inside `readObject()` or `finalize()`), the attacker can execute arbitrary commands on the server (Remote Code Execution - RCE).
This is why modern applications prefer data-only formats like JSON or Protocol Buffers, which do not instantiate executable code.

## Q2: How does `ObjectInputFilter` (introduced in Java 9) mitigate deserialization vulnerabilities?
**Answer:**
`ObjectInputFilter` allows developers to define strict allow-lists (whitelists) or block-lists (blacklists) for classes before they are deserialized.
When `readObject()` reads the class name from the stream, it passes the name to the filter *before* instantiating the object. If the filter rejects the class, the JVM throws an `InvalidClassException`, preventing the malicious gadget from ever entering memory or executing code.
**Best Practice**: Always use an allow-list (e.g., "Only allow `com.mycompany.User`") rather than a block-list, as attackers constantly discover new gadget classes.

## Q3: Explain the Serialization Proxy Pattern. Why is it safer than overriding `readObject()`?
**Answer:**
Standard deserialization bypasses the class's constructor, meaning any validation logic (e.g., ensuring a date is not null, or an age is > 0) is skipped. An attacker can craft a byte stream that creates an object with an invalid, corrupted state.
The **Serialization Proxy Pattern** solves this. The main class implements `writeReplace()` to return a nested, static `Proxy` object instead of itself. The `Proxy` object is serialized.
Upon deserialization, the JVM reads the `Proxy` object. The `Proxy` object implements `readResolve()`, which extracts the data and calls the *actual constructor* of the main class. This forces the deserialized data to pass through the canonical constructor, ensuring all invariants and validation checks are executed.

## Q4: What is the purpose of `serialVersionUID`, and what happens if you don't define it?
**Answer:**
`serialVersionUID` is a version control number for a serialized class. When an object is serialized, this ID is written into the byte stream. When deserialized, the JVM compares the ID in the stream with the ID of the class currently loaded in the JVM. If they don't match, it throws an `InvalidClassException`.
If you do not explicitly define `private static final long serialVersionUID = 1L;`, the JVM calculates one automatically based on the class's structure (fields, methods, interfaces). If you change *anything* about the class (e.g., add a method, change a field name), the calculated ID changes, breaking compatibility with all previously serialized data. Defining it manually ensures backward compatibility.

## Q5: How do you serialize a Singleton class without breaking the Singleton contract?
**Answer:**
Deserialization uses reflection to create a new instance of an object, completely bypassing private constructors. If you serialize a Singleton and deserialize it, you will have two instances in memory.
To fix this, the Singleton class must implement the `readResolve()` method.
```java
private Object readResolve() throws ObjectStreamException {
    return INSTANCE;
}
```
The JVM calls `readResolve()` immediately after deserializing the object. Whatever this method returns replaces the newly deserialized object. By returning the existing `INSTANCE`, the deserialized copy is discarded (garbage collected), and the Singleton contract is preserved.