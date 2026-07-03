# Polymorphism — Flashcards

1. **Q: Polymorphism definition?** A: Many forms — same interface, different behavior
2. **Q: Dynamic dispatch?** A: Method resolved at runtime by object type
3. **Q: Overloading vs overriding?** A: Overload=compile/different params; Override=runtime/same sig
4. **Q: Covariant return?** A: Override returns subtype (Java 5+)
5. **Q: instanceof + pattern matching?** A: Type check with variable binding (Java 16+)
6. **Q: vtable?** A: Virtual method table for dispatch
7. **Q: itable?** A: Interface method table
8. **Q: invokevirtual vs invokeinterface?** A: invinterface slightly slower
9. **Q: Monomorphic call site?** A: Only one type seen — JIT devirtualizes
10. **Q: Megamorphic call site?** A: 3+ types — stays virtual dispatch
11. **Q: Programming to interface?** A: Use interface type, not concrete class
12. **Q: Strategy pattern?** A: Encapsulate algorithms, make interchangeable
13. **Q: Open/Closed Principle?** A: Open for extension, closed for modification
14. **Q: LSP violation example?** A: Subclass throws new exception
15. **Q: Method reference?** A: Class::method — shorthand for lambda
16. **Q: Polymorphic parameter?** A: Method accepts supertype, works with any subtype
17. **Q: Polymorphic collection?** A: Collection of supertype holds subtypes
18. **Q: Static method overloading?** A: Yes — compile-time polymorphism
19. **Q: Static method overriding?** A: No — hidden, not overridden
20. **Q: Bridge method?** A: Compiler-generated for covariant generics
