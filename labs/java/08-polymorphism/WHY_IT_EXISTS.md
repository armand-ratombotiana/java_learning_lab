# Why Polymorphism Exists

## The Problem Polymorphism Solves

Without polymorphism, code that works with multiple types requires explicit type checking and branching. Every method that accepts animals would need `if (animal instanceof Dog) ... else if (animal instanceof Cat) ...`. Polymorphism eliminates these cascading conditionals — the object itself determines the correct behavior.

## Historical Context

Polymorphism (from "many forms") was a key innovation in OOP. Smalltalk implemented pure dynamic dispatch — every method call goes through the object's class. C++ offered both static (overloaded functions) and dynamic (virtual functions) polymorphism.

Java chose a middle path:
- **All non-static methods are virtual by default**: No `virtual` keyword needed
- **`final` methods**: Opt-out from overriding for performance and correctness
- **Overloading**: Resolved at compile time (static polymorphism)
- **Overriding**: Resolved at runtime (dynamic polymorphism)

The decision to make all methods virtual (unlike C++) simplified the language but had performance implications. The JIT compiler mitigates this by devirtualizing monomorphic call sites — if only one implementation has been loaded, it inlines the call.
