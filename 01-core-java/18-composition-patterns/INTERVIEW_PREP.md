# Interview Preparation: Composition Patterns

This document covers advanced questions related to the Decorator, Composite, and Delegation patterns, and the principle of "Favor Composition over Inheritance".

## Q1: Why is "Favor Composition over Inheritance" considered a best practice in Object-Oriented Design?
**Answer:**
Inheritance is a static, compile-time relationship that tightly couples the subclass to the superclass (the "Fragile Base Class" problem). If the superclass changes, the subclass might break. Furthermore, inheritance leads to class explosion if you need to combine multiple behaviors.
Composition is a dynamic, runtime relationship. You can assemble objects with different behaviors on the fly by injecting dependencies. It promotes loose coupling, easier testing (via mocking), and greater flexibility.

## Q2: Explain the difference between the Decorator pattern and the Proxy pattern.
**Answer:**
*   **Intent**: Both patterns use composition to wrap an object and delegate calls to it. However, their intents are different.
*   **Decorator**: Adds *behavior* or responsibilities to an object dynamically at runtime. The client explicitly wraps the object (e.g., `new BufferedInputStream(new FileInputStream(...))`).
*   **Proxy**: Controls *access* to the object. It is often created automatically by a framework (e.g., lazy loading in Hibernate, transaction management in Spring). The client usually doesn't know it's interacting with a proxy.

## Q3: What is the primary design trade-off when implementing the Composite pattern?
**Answer:**
The trade-off is between **Type Safety** and **Uniformity**.
The Composite pattern requires a base `Component` interface. 
*   If you put child-management methods (like `add()` and `remove()`) in the base interface, you achieve **Uniformity** (clients treat leaves and composites exactly the same). However, you lose **Type Safety** because a Leaf node cannot have children, so it must throw an `UnsupportedOperationException` if `add()` is called.
*   If you put child-management methods *only* in the Composite class, you achieve **Type Safety** (you can't call `add()` on a Leaf). However, you lose **Uniformity** because the client must use `instanceof` and downcast the Component to a Composite before adding children.

## Q4: How does the Decorator pattern solve the "Class Explosion" problem?
**Answer:**
Imagine a `Window` class. You want to add scrolling and borders. Using inheritance, you would need `ScrollingWindow`, `BorderedWindow`, and `ScrollingBorderedWindow`. If you add a "Shadow" feature, you need 4 more classes. The number of classes grows exponentially ($2^N$).
With the Decorator pattern, you only create 3 decorator classes: `ScrollDecorator`, `BorderDecorator`, and `ShadowDecorator`. You can combine them at runtime in any order (e.g., `new ShadowDecorator(new BorderDecorator(new Window()))`). The number of classes grows linearly ($N$).

## Q5: What is the "Object Identity" (or "Self") problem when using composition/decorators?
**Answer:**
When you wrap an object in a Decorator, there are now two distinct objects in memory. If the inner object passes a reference to itself (`this`) to another component (e.g., registering for an event callback), the callback will hit the inner object directly, completely bypassing the Decorator. The Decorator's added behavior will not be executed. This is a fundamental limitation of object wrapping compared to true inheritance (where `this` always refers to the most derived subclass).