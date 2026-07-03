# Pedagogic Guide: Advanced Polymorphism

## 1. Module Overview
This module bridges the gap between theoretical Object-Oriented design and the practical realities of the Java Virtual Machine (JVM). It moves learners away from the simplistic view of "subclass overrides superclass" and introduces the complexities of compilation, type erasure, and static vs. dynamic binding.

## 2. Learning Paths

### Path A: The API Designer (Focus: Fluency & Safety)
**Target Audience**: Developers building libraries, frameworks, or complex domain models.
*   **Focus**: `DEEP_DIVE.md` (Covariant Returns, Default Methods) and `MINI_PROJECT.md`.
*   **Key Takeaway**: Understanding how to use covariant return types to build fluent APIs (like Builders) that don't require the client to constantly cast objects.

### Path B: The Debugger (Focus: Mechanics & Pitfalls)
**Target Audience**: Senior developers who need to debug obscure runtime errors or prepare for deep-dive technical interviews.
*   **Focus**: `EDGE_CASES.md` (Constructor Traps, Field Hiding) and `INTERVIEW_PREP.md`.
*   **Key Takeaway**: Mastering the distinction between static binding (fields, static methods) and dynamic binding (instance methods), and understanding why `ClassCastExceptions` can occur in compiler-generated bridge methods.

## 3. Teaching Strategies

### The "What does this print?" Challenge
Advanced polymorphism is best taught through code snippets that defy beginner expectations.
Start the lesson with this snippet:
```java
class A { int x = 1; static void print() { System.out.println("A"); } }
class B extends A { int x = 2; static void print() { System.out.println("B"); } }
// Ask: What do these print?
A obj = new B();
System.out.println(obj.x);
obj.print();
```
Most beginners will guess `2` and `B`. When the answer is `1` and `A`, you have their attention. Use this to explain Static vs. Dynamic Binding.

### The "Bridge" Metaphor
When explaining Bridge Methods, use the metaphor of a translator. The JVM only understands `Object` (due to type erasure). Your code only understands `String`. The compiler builds a "bridge" (a synthetic method) that takes the `Object` from the JVM, translates (casts) it to a `String`, and hands it to your code.

## 4. Common Mental Blocks & Clarifications

### Block 1: "Why can't I override static methods?"
*   **Clarification**: Overriding requires the JVM to look at the object in memory at runtime to decide which method to call. Static methods don't belong to objects; they belong to the Class itself. Therefore, the compiler resolves the method call at compile time based purely on the reference type.

### Block 2: "Why is calling an overridable method in a constructor bad?"
*   **Clarification**: This requires understanding the exact sequence of object initialization. Walk the learner through the steps: 1) Memory allocated, 2) Superclass constructor starts, 3) Overridden method called (executing subclass code), 4) Subclass fields initialized. Step 3 happens before Step 4, leading to null pointers or default values.

### Block 3: "Overloading vs Overriding"
*   **Clarification**: Many developers accidentally overload a method when they mean to override it (e.g., changing `Object` to `String` in the parameter list). Stress the importance of the `@Override` annotation as a compiler-enforced safety net.

## 5. Assessment Strategy
*   **Formative**: Provide several small code snippets mixing fields, static methods, and instance methods, and ask the learner to predict the output.
*   **Summative**: The `MINI_PROJECT.md` requires the learner to implement a Builder pattern using covariant return types and resolve a default method conflict, proving they can apply these concepts to write clean, type-safe code.