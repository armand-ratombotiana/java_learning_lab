# Polymorphism — Interview Questions

1. **Q: What is polymorphism and why is it useful?** A: The ability of an object to take many forms. Enables programming to interfaces, the Strategy pattern, and the Open/Closed Principle.

2. **Q: How does dynamic method dispatch work?** A: JVM looks up the actual object's class at runtime, finds the method in the vtable, and executes that version. The reference type determines available methods; the object type determines implementation.

3. **Q: What is the difference between compile-time and runtime polymorphism?** A: Compile-time = overloading (method resolution by compiler). Runtime = overriding (method resolution by JVM via vtable).

4. **Q: Can you have polymorphism without inheritance?** A: Yes — interface polymorphism. Different classes implementing the same interface can be used interchangeably.

5. **Q: What is the Open/Closed Principle?** A: Software entities should be open for extension but closed for modification. Polymorphism achieves this — add new subclasses without modifying existing code.

6. **Q: How does the JIT optimize polymorphic calls?** A: Type profiling → if monomorphic (1 type), devirtualize and inline. If bimorphic (2 types), inline both with type check. If megamorphic (3+), keep virtual dispatch.

7. **Q: What is a covariant return type?** A: Java 5+ feature where an overriding method can return a more specific (subtype) return type. Example: `Dog reproduce()` in Dog when Animal's method returns `Animal`.

8. **Q: Why can't you override static methods?** A: Static methods are class-level — they belong to the class, not instances. They're resolved at compile time based on reference type, not runtime object type.

9. **Q: What is the difference between `instanceof` and `getClass()`?** A: `instanceof` checks if an object is an instance of a type OR its subtypes. `getClass() == X.class` checks exact class only (not subtypes).

10. **Q: What is the Strategy pattern?** A: Define a family of algorithms (interfaces), encapsulate each (implementations), make them interchangeable. Client selects strategy at runtime.

11. **Q: How does overload resolution work with null?** A: If multiple methods match (e.g., `method(Object)` and `method(String)`), `method(null)` is ambiguous — compiler error.

12. **Q: What is the difference between polymorphic and non-polymorphic?** A: Polymorphic: method behavior depends on runtime type. Non-polymorphic: method behavior depends on reference type (static methods, overloaded methods).
