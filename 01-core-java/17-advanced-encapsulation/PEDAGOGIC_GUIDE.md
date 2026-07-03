# Pedagogic Guide: Advanced Encapsulation

## 1. Module Overview
This module shifts the learner's perspective from basic syntax (`private`, `public`) to architectural design. It introduces modern Java features (Records, Sealed Classes) not just as syntactic sugar, but as powerful tools for Domain-Driven Design (DDD) and creating robust, secure, and predictable code.

## 2. Learning Paths

### Path A: The Modern Java Developer (Focus: Syntax & Usage)
**Target Audience**: Developers migrating from Java 8/11 to Java 17/21.
*   **Focus**: `DEEP_DIVE.md` (Records and Sealed Classes) and `MINI_PROJECT.md`.
*   **Key Takeaway**: Understanding how to replace boilerplate POJOs with Records and how to use Sealed Classes with pattern matching in `switch` expressions.

### Path B: The Domain Architect (Focus: Security & Immutability)
**Target Audience**: Senior developers focusing on system design, security, and DDD.
*   **Focus**: `EDGE_CASES.md` (Leaky Immutability, Serialization Traps) and `INTERVIEW_PREP.md`.
*   **Key Takeaway**: Mastering defensive copying, understanding the security implications of reflection, and using ADTs (Algebraic Data Types) to model domain states.

## 3. Teaching Strategies

### The "Anemic Domain" Critique
Start by showing a standard Java Bean with 10 private fields and 20 public getters/setters. Ask the learner: *"Is this encapsulated?"* 
Most will say yes because the fields are private. Challenge this by demonstrating how external code can pull out the state, mutate it, and push it back in, completely bypassing any business rules the class should enforce. This sets the stage for "Tell, Don't Ask" and true immutability.

### The "Leaky Bucket" Metaphor
When teaching defensive copying, use a bucket metaphor. If you pass a bucket of water (a `List`) into a class, and you keep a hold of the handle outside the class, you can still dump the water out. To truly encapsulate it, the class must pour the water into its *own* bucket (defensive copy) and give back the handle to the original.

## 4. Common Mental Blocks & Clarifications

### Block 1: "Records are just Kotlin Data Classes"
*   **Clarification**: While they look similar, Java Records are stricter. They are designed specifically as nominal tuples (transparent data carriers). You cannot extend them, and you cannot have hidden state inside them. If you need hidden state, use a standard Class.

### Block 2: "Why use Sealed Classes instead of Enums?"
*   **Clarification**: Enums are great for singletons (e.g., `Color.RED`). However, if you need each state to hold different data (e.g., a `Success` state holds a `Data` object, but an `Error` state holds an `Exception` object), Enums fail. Sealed Classes allow different subclasses to have entirely different shapes and fields, while still maintaining a closed hierarchy.

### Block 3: "Defensive copying is bad for performance"
*   **Clarification**: While creating new objects has a cost, modern JVM garbage collectors (like G1 or ZGC) are incredibly efficient at cleaning up short-lived objects. The cost of a defensive copy is almost always worth the prevention of elusive, multi-threaded state mutation bugs.

## 5. Assessment Strategy
*   **Formative**: Provide a "leaky" immutable class and ask the learner to identify where the state can be mutated from the outside, then have them fix it.
*   **Summative**: The `MINI_PROJECT.md` assesses the ability to combine Records, Sealed Classes, and Pattern Matching into a cohesive, secure domain model.