# Pedagogic Guide: Sealed Types

## 1. Module Overview
This module introduces a feature that fundamentally alters how Java developers design domain models. It moves Java away from the "everything is open and extensible" philosophy of the 1990s toward the "strict, predictable, and verifiable" philosophy of modern functional languages like Scala and Rust.

## 2. Learning Paths

### Path A: The Domain Modeler (Focus: ADTs & Exhaustiveness)
**Target Audience**: Developers building complex business logic, state machines, or parsing engines.
*   **Focus**: `DEEP_DIVE.md` (ADTs, Exhaustiveness) and `MINI_PROJECT.md`.
*   **Key Takeaway**: Understanding how to use `sealed` interfaces and `record`s to completely eliminate the need for `default` branches in switch statements, ensuring that the compiler catches missing business logic.

### Path B: The API/Library Author (Focus: Hierarchy Control & Modifiers)
**Target Audience**: Senior developers building internal frameworks or public APIs where backward compatibility is critical.
*   **Focus**: `DEEP_DIVE.md` (The Rules of Sealing) and `EDGE_CASES.md` (Package boundaries, `non-sealed`).
*   **Key Takeaway**: Mastering the exact rules of the `permits` clause, understanding how `non-sealed` punches a hole in the hierarchy, and learning how to protect an API from malicious or accidental subclassing.

## 3. Teaching Strategies

### The "VIP Club" Metaphor (Sealed Hierarchies)
To explain the three subclass modifiers:
The `Shape` interface is a VIP Club. It has a strict guest list (`permits Circle, Rectangle, Square`).
*   **`final` (The Loner)**: `Circle` gets in. The bouncer asks, "Can anyone come in as your guest?" `Circle` says, "No. I'm `final`. I'm here alone."
*   **`sealed` (The VIP with a Guest List)**: `Rectangle` gets in. The bouncer asks, "Can anyone come in as your guest?" `Rectangle` says, "Yes, but only people on *my* specific list." (`permits TransparentRectangle`).
*   **`non-sealed` (The Party Animal)**: `Square` gets in. The bouncer asks, "Can anyone come in as your guest?" `Square` says, "Yes! Open the floodgates! Anyone who knows me can come in!" This perfectly illustrates the danger of `non-sealed`.

### The "Silent Failure" Demonstration (Exhaustiveness)
To prove why exhaustiveness checking is revolutionary:
1.  Write a standard `switch` on an `enum` or standard interface. Include a `default` branch that throws an exception or logs a warning.
2.  Add a new value to the `enum` or a new implementation of the interface.
3.  Compile the code. Show that it compiles perfectly.
4.  Run the code. Show that it crashes or silently falls into the `default` branch.
5.  Now, change it to a `sealed` interface and a `switch` expression *without* a `default` branch.
6.  Add a new permitted subclass.
7.  Compile the code. **BOOM!** The compiler instantly flags the `switch` statement as an error. The bug was caught at compile time instead of production.

## 4. Common Mental Blocks & Clarifications

### Block 1: "Why not just use Enums?"
*   **Clarification**: This is the most common question. Enums are great for singletons without state (e.g., `Color.RED`). But what if you have a `PaymentResult`? A `Success` result needs to hold a `TransactionId` (String). An `Error` result needs to hold an `Exception` object. Enums cannot hold different types of state per instance easily. Sealed classes allow each variant to have entirely different fields and methods while maintaining the closed hierarchy of an Enum.

### Block 2: "Why do I have to write `permits` if the compiler can infer it?"
*   **Clarification**: Explain that the compiler only infers it if all classes are in the *exact same file*. While this is fine for tiny scripts, enterprise Java classes are usually in their own files. Therefore, explicitly writing the `permits` clause is a mandatory habit for real-world development, and it acts as excellent API documentation.

### Block 3: "Why is Mockito crashing when I try to test my sealed class?"
*   **Clarification**: This is a major pain point for developers adopting Java 17+. Explain that Mockito creates "fake" subclasses at runtime. The JVM's security model strictly enforces sealing at runtime as well as compile time. If Mockito tries to create `Shape$MockitoMock$1`, the JVM rejects it because it isn't in the `permits` clause. Guide them toward using real instances (Records are easy to instantiate!) instead of mocking data carriers.

## 5. Assessment Strategy
*   **Formative**: Provide a snippet: `public sealed class A permits B, C {}` and `public class B extends A {}`. Ask the learner why this fails to compile. (Answer: Class B is missing the required `final`, `sealed`, or `non-sealed` modifier).
*   **Summative**: The `MINI_PROJECT.md` requires the learner to build a Payment Processor. By successfully defining an ADT hierarchy and writing an exhaustive `switch` that compiles without a `default` branch, they prove they understand the core value proposition of sealed types.