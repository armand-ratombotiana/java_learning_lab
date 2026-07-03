# Interview Preparation: Sealed Types

This document covers advanced questions related to the `sealed` modifier, Algebraic Data Types (ADTs), and the architectural benefits of closed hierarchies.

## Q1: What is the difference between a `final` class and a `sealed` class?
**Answer:**
*   A `final` class is completely closed. It cannot be extended by *any* other class. It represents the absolute end of an inheritance hierarchy.
*   A `sealed` class is partially closed. It *can* be extended, but *only* by a specific, explicitly defined list of subclasses (declared in the `permits` clause). It allows you to create a controlled, finite hierarchy.

## Q2: If a class `A` extends a sealed class `B`, what are the three modifier choices for class `A`, and what do they mean?
**Answer:**
Every permitted subclass must declare exactly one of these modifiers to explicitly state how the hierarchy continues:
1.  `final`: The hierarchy ends here. No one can extend class `A`.
2.  `sealed`: The hierarchy continues, but remains controlled. Class `A` must provide its own `permits` clause to explicitly list its own allowed subclasses.
3.  `non-sealed`: The hierarchy is opened back up. Any class in the application can now extend class `A`, effectively breaking the strict control of the hierarchy from that point downward.

## Q3: Explain what an Algebraic Data Type (ADT) is, and how Java 17+ supports them.
**Answer:**
An Algebraic Data Type is a composite type. Specifically, sealed classes enable "Sum Types" (also known as Tagged Unions). A Sum Type means a value can be exactly *one* of a finite set of variants.
For example, a `Result` type can be *either* a `Success` *or* a `Failure`.
Java supports this by combining `sealed` interfaces with `record` classes. The sealed interface defines the root of the Sum Type, and the records define the specific, immutable variants. This provides a type-safe way to model complex domain states without relying on error-prone Enums or excessive class hierarchies.

## Q4: Why is "Exhaustiveness Checking" considered the most powerful feature of Sealed Classes?
**Answer:**
When you use a `switch` expression over a standard interface, the compiler forces you to include a `default` branch because any unknown class could implement that interface at runtime.
When you `switch` over a `sealed` interface, the compiler knows the exact, finite list of permitted subclasses. If your `switch` contains a `case` for every permitted subclass, the compiler considers it "exhaustive" and does not require a `default` branch.
**The Benefit**: If a developer later adds a new permitted subclass to the sealed interface, the compiler will immediately flag every exhaustive `switch` statement in the codebase as an error, forcing the developer to handle the new case. This completely eliminates a massive category of runtime bugs where new states silently fall into `default` branches and are ignored.

## Q5: Why might you get an error when trying to mock a sealed interface using a framework like Mockito?
**Answer:**
Mocking frameworks like Mockito work by dynamically generating a subclass (or implementing an interface) at runtime using bytecode generation libraries (like ByteBuddy or CGLIB).
Because the interface is `sealed`, the JVM strictly enforces that *only* the classes listed in the `permits` clause can implement it. When Mockito tries to generate a new proxy class at runtime, the JVM rejects it, throwing an `IncompatibleClassChangeError` or `IllegalArgumentException`.
To fix this, you must either use real instances of the permitted subclasses in your tests, or configure the mocking framework to use advanced agent-based techniques that can bypass JVM sealing rules.