# Edge Cases & Pitfalls: Composition Patterns

While composition is generally preferred over inheritance, it introduces its own set of challenges, particularly around object identity, boilerplate code, and deep wrapping.

## 1. The Object Identity Problem (The "Self" Problem)
*   **The Scenario**: You have a `Component` that passes `this` to another object (e.g., registering itself as a listener). You then wrap this `Component` in a `Decorator`.
*   **The Pitfall**: When the inner `Component` passes `this`, it passes a reference to the *inner* object, not the *Decorator*. Any callbacks will hit the inner object directly, bypassing the Decorator's added behavior.
*   **Mitigation**: This is notoriously difficult to solve purely with Decorators. If object identity and callbacks are heavily used, you might need to use a Proxy (which can intercept all calls) or ensure that components do not leak `this` when they are expected to be decorated.

## 2. The Boilerplate Burden (Forwarding Methods)
*   **The Scenario**: You want to decorate a `java.util.List` to log whenever an item is added.
*   **The Pitfall**: To implement the `List` interface, you must implement over 20 methods. If you only care about `add()`, you still have to write 19 boilerplate methods that just forward the call to the delegate: `public int size() { return delegate.size(); }`.
*   **Mitigation**: Use existing forwarding classes (like Guava's `ForwardingList`) or use Lombok's `@Delegate` annotation (though use with caution as it relies on metaprogramming). Alternatively, use Dynamic Proxies if the interface is very large.

## 3. The "Deep Wrapper" Debugging Nightmare
*   **The Scenario**: You wrap an object 10 layers deep (e.g., `new A(new B(new C(new D(...))))`). This is common in Java I/O streams.
*   **The Pitfall**: If an exception is thrown deep inside the stack, the stack trace will show 10 layers of forwarding methods, making it difficult to find the actual source of the error. Furthermore, if you need to access a method specific to layer `C`, you cannot easily downcast layer `A` to `C`.
*   **Mitigation**: Keep decoration chains reasonably short. If you need a specific inner object, do not bury it inside a decorator where its specific type is lost.

## 4. The Composite Pattern: Type Safety vs. Uniformity
*   **The Scenario**: You implement the Composite pattern for a File System. The `FileSystemNode` interface has an `add(FileSystemNode node)` method.
*   **The Pitfall**: A `File` (a Leaf) cannot have children. If `File` implements `FileSystemNode`, what should `file.add(...)` do?
    *   *Option A (Uniformity)*: Throw an `UnsupportedOperationException`. This breaks the Liskov Substitution Principle (LSP) and causes runtime crashes.
    *   *Option B (Type Safety)*: Remove `add()` from the interface and put it only in the `Directory` class. Now the client has to constantly use `instanceof` and downcast, defeating the purpose of the Composite pattern.
*   **Mitigation**: There is no perfect solution; it's a design trade-off. Usually, for simple hierarchies, Option A (Uniformity) is preferred, but it requires careful client code. For robust APIs, Option B (Type Safety) is preferred, perhaps using the Visitor pattern to traverse the tree safely without downcasting.

## 5. Memory Overhead
*   **The Pitfall**: Every wrapper object consumes memory (object header, reference pointer). If you have a million objects and you wrap each one in 3 decorators, you now have 4 million objects.
*   **Mitigation**: If you need to decorate millions of small objects, consider the Flyweight pattern or move the added behavior to a service class rather than wrapping the objects individually.