# Edge Cases & Pitfalls: Sealed Types

Sealed types introduce strict compiler rules. Violating these rules, or misunderstanding how they interact with generics and modules, can lead to frustrating compilation errors.

## 1. The Package/Module Boundary Trap
*   **The Scenario**: You are building an API. You define `public sealed interface Event permits LoginEvent, LogoutEvent` in the package `com.api.events`. You decide to put `LoginEvent` in `com.api.events.auth`.
*   **The Pitfall**: If you are not explicitly using the Java Module System (`module-info.java`), the compiler will throw an error: `class is not allowed to extend sealed class (not in the same package)`. In an unnamed module, permitted subclasses MUST reside in the exact same package as the sealed parent.
*   **Mitigation**: Either move all permitted subclasses into the same package, or modularize your application. In a named module, permitted subclasses can be in different packages as long as they belong to the same module.

## 2. Breaking Exhaustiveness with `non-sealed`
*   **The Scenario**: You have a `sealed` interface `Shape` that permits `Circle` and `Square`. You make `Square` a `non-sealed` class so clients can extend it. You write a `switch` statement covering `Circle` and `Square`.
*   **The Pitfall**: The switch is exhaustive, and the code compiles. Later, a client creates `class WeirdSquare extends Square`. When `WeirdSquare` is passed into your `switch`, what happens? It matches the `case Square` branch! Because `WeirdSquare` is a subclass of `Square`, the `case Square` acts as a catch-all for the entire unsealed sub-hierarchy.
*   **Mitigation**: Be extremely careful when using `non-sealed`. It permanently punches a hole in your strictly controlled hierarchy. If you need exhaustiveness to guarantee specific behavior for every single leaf node, you must stick to `final` or `sealed` modifiers on subclasses.

## 3. The Implicit `permits` Clause
*   **The Scenario**: You define a sealed class and its subclasses in the exact same `.java` file.
    ```java
    sealed class Node {}
    final class Leaf extends Node {}
    ```
*   **The Pitfall**: You forgot the `permits Leaf` clause on `Node`. However, the code compiles perfectly!
*   **Mitigation**: This is a feature, not a bug, but it can be confusing. If all permitted subclasses are defined in the *same compilation unit* (the same `.java` file) as the sealed class, the compiler automatically infers the `permits` clause. While convenient, explicitly writing the `permits` clause is recommended for readability and API documentation.

## 4. Generic Type Erasure vs Exhaustiveness
*   **The Scenario**: You have a sealed generic interface: `sealed interface Response<T> permits Success, Error`. You try to switch on it.
    ```java
    Response<String> res = ...;
    switch (res) {
        case Success<String> s -> ...
        case Error<String> e -> ...
    }
    ```
*   **The Pitfall**: Depending on how the generic types are structured, the compiler might complain that the switch is not exhaustive, even though you covered both `Success` and `Error`. Because generics are erased at runtime, the compiler sometimes cannot mathematically prove that a `Success<Integer>` won't somehow slip into the switch.
*   **Mitigation**: You may have to use raw types in the pattern (`case Success s`) or add an explicit `case null, default -> throw new IllegalStateException()` branch, which unfortunately defeats the primary benefit of compiler-enforced exhaustiveness.

## 5. Mocking Sealed Classes in Tests
*   **The Scenario**: You are writing Unit Tests using Mockito. You try to mock a sealed interface: `Shape mockShape = mock(Shape.class);`.
*   **The Pitfall**: Mockito works by dynamically generating subclasses at runtime using CGLIB or ByteBuddy. Because `Shape` is sealed, the JVM strictly forbids creating new subclasses at runtime that are not listed in the `permits` clause. The test will crash with an `IllegalArgumentException` or `IncompatibleClassChangeError`.
*   **Mitigation**: You cannot mock sealed classes or interfaces using standard dynamic proxy techniques. You must either use real instances of the permitted subclasses in your tests, or use advanced mocking frameworks (like modern Mockito with specific inline-mock-maker configurations) that hack the JVM at the agent level to bypass sealing rules.