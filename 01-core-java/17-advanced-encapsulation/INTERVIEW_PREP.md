# Interview Preparation: Advanced Encapsulation

This document covers advanced questions related to immutability, records, sealed classes, and module-level encapsulation.

## Q1: How do you create a truly immutable class in Java? What are the common pitfalls?
**Answer:**
To create a truly immutable class:
1.  Make the class `final` (so subclasses can't override behavior).
2.  Make all fields `private` and `final`.
3.  Provide no mutator methods (setters).
4.  Ensure exclusive access to any mutable object fields. Do this by making **defensive copies** in the constructor (so the caller can't hold a reference to the internal mutable object) and in the getter (so the caller can't modify the returned object).
*Pitfalls*: Forgetting to defensively copy mutable fields like `java.util.Date`, arrays, or `ArrayList`.

## Q2: What is a Java Record, and when should you NOT use it?
**Answer:**
A Record (introduced in Java 14/16) is a transparent data carrier. It automatically generates private final fields, canonical constructors, accessors, `equals`, `hashCode`, and `toString`.
**When NOT to use it:**
1.  When you need to encapsulate *behavior* or hidden state. Records are transparent by design.
2.  When the object's state needs to change over time (Records are inherently shallowly immutable).
3.  When you need to inherit from another class (Records implicitly extend `java.lang.Record` and cannot extend anything else).
4.  When you need JPA/Hibernate entities (JPA requires a no-arg constructor and mutable fields by default, though records can be used for Projections/DTOs).

## Q3: Explain the concept of "Sealed Classes" and how they relate to Domain-Driven Design (DDD).
**Answer:**
Sealed classes (Java 15/17) allow a class or interface to explicitly declare which other classes are permitted to extend/implement it using the `permits` keyword.
**Relation to DDD:** In DDD, you often want to model a closed set of domain events or states (e.g., a `PaymentStatus` can only be `Pending`, `Success`, or `Failed`). Before sealed classes, you had to use Enums (which can't hold instance-specific state easily) or rely on convention. Sealed classes allow you to create Algebraic Data Types (ADTs), ensuring the compiler knows all possible subtypes, which enables exhaustive pattern matching in `switch` statements without needing a default clause.

## Q4: Can you modify a private field of a Record using Reflection?
**Answer:**
No. Unlike standard Java classes where you can use `field.setAccessible(true)` and `field.set(object, newValue)` to bypass encapsulation, the JVM explicitly forbids modifying the fields of a Record via reflection. Attempting to do so will throw an `IllegalAccessException`. This makes Records highly secure data carriers.

## Q5: How does the Java Module System (Jigsaw) improve encapsulation compared to traditional classpath access?
**Answer:**
Before Java 9, `public` meant "public to everyone on the classpath." If you had an internal utility class marked as public, any other jar on the classpath could use it.
With the Module System (`module-info.java`), you can declare a class as `public`, but if you do not `export` the package it resides in, it remains strictly internal to that module. Other modules cannot access it at compile time, and even reflection at runtime will fail (throwing `InaccessibleObjectException`) unless explicitly opened via JVM arguments. This provides true strong encapsulation at the architectural level.