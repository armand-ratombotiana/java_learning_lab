# Pedagogic Guide: Composition Patterns

## 1. Module Overview
This module tackles one of the most frequently quoted (and frequently misunderstood) maxims in software engineering: "Favor Composition over Inheritance." It moves learners away from rigid, deep class hierarchies and towards flexible, modular designs using structural patterns like Decorator and Composite.

## 2. Learning Paths

### Path A: The Application Developer (Focus: Usage & Flexibility)
**Target Audience**: Developers who frequently struggle with rigid class hierarchies or need to extend existing library classes.
*   **Focus**: `DEEP_DIVE.md` (Decorator and Composition vs Inheritance) and `MINI_PROJECT.md`.
*   **Key Takeaway**: Understanding how to use the Decorator pattern to add features to an object at runtime without creating a massive inheritance tree.

### Path B: The System Architect (Focus: Trade-offs & Edge Cases)
**Target Audience**: Senior developers designing APIs, frameworks, or complex domain models.
*   **Focus**: `EDGE_CASES.md` (Object Identity, Composite Trade-offs) and `INTERVIEW_PREP.md`.
*   **Key Takeaway**: Mastering the nuances of the "Self" problem in wrapped objects and making informed decisions between Uniformity and Type Safety in the Composite pattern.

## 3. Teaching Strategies

### The "Class Explosion" Visual
Start by drawing a `Window` class on a whiteboard. Ask the learner to add a `Border`. They will likely suggest `BorderedWindow extends Window`. Then ask them to add a `Scrollbar`. They might suggest `ScrollingWindow extends Window`. 
Now ask: *"What if I want a window with BOTH a border and a scrollbar?"* They will suggest `ScrollingBorderedWindow`.
Finally, introduce a third feature, `Shadow`. Show how the number of required classes explodes combinatorially. This perfectly illustrates the problem that the Decorator pattern solves.

### The "Russian Nesting Doll" Metaphor
When explaining the Decorator pattern, use the metaphor of a Russian nesting doll (Matryoshka). Each doll (Decorator) looks like the one inside it (implements the same interface) and contains the one inside it. When you interact with the outermost doll, it delegates actions down to the innermost core.

## 4. Common Mental Blocks & Clarifications

### Block 1: "Isn't a Decorator just a Proxy?"
*   **Clarification**: Structurally, they are nearly identical (both wrap an object and implement its interface). The difference is *Intent*. A Proxy controls *access* (e.g., checking permissions, lazy loading). A Decorator adds *behavior* (e.g., adding a border, logging output). The client usually knows they are using a Decorator, but is oblivious to a Proxy.

### Block 2: "If composition is better, should I never use inheritance?"
*   **Clarification**: Inheritance is not evil; it is just overused. Inheritance is appropriate when there is a true, immutable "is-a" relationship (a `Square` is a `Shape`) AND you want to reuse the base class's implementation. If you only want to reuse code, or if the relationship might change, composition is better.

### Block 3: "Composite Pattern: Why would a Leaf implement an add() method?"
*   **Clarification**: This is the classic Uniformity vs. Type Safety debate. Explain that treating a single file and a folder identically makes client code very clean (`node.delete()`), but it requires the `File` class to provide dummy implementations (or throw exceptions) for methods like `addChild()`.

## 5. Assessment Strategy
*   **Formative**: Provide a deep inheritance hierarchy (e.g., `Vehicle` -> `Car` -> `ElectricCar` -> `SelfDrivingElectricCar`) and ask the learner to refactor it using composition (e.g., `Car` has an `Engine` and a `DrivingSystem`).
*   **Summative**: The `MINI_PROJECT.md` requires the learner to implement both the Composite and Decorator patterns together, proving they understand how structural patterns can be combined to build complex systems.