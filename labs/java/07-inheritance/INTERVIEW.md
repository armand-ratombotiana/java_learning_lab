# Inheritance — Interview Questions

1. **Q: Why does Java not support multiple class inheritance?** A: Avoids diamond problem (C++ style ambiguity). Instead, Java provides multiple interface inheritance.

2. **Q: What is the Object class?** A: Root of class hierarchy. All classes inherit from Object directly or indirectly. Provides toString(), equals(), hashCode(), getClass(), clone().

3. **Q: What is the difference between method overriding and method hiding?** A: Overriding = instance method (runtime). Hiding = static method (compile-time via reference type).

4. **Q: What is the `super` keyword used for?** A: (1) Call superclass constructor: `super(args)`. (2) Call superclass method: `super.method()`. (3) Access superclass field: `super.field`.

5. **Q: What is a covariant return type?** A: Override method can return a subtype of the original return type (Java 5+). Avoids explicit casts.

6. **Q: What is the difference between IS-A and HAS-A?** A: IS-A = inheritance (Dog is-an Animal). HAS-A = composition (Car has-a Engine). Favor HAS-A over IS-A.

7. **Q: What is the fragile base class problem?** A: Changes to a base class can break subclasses unexpectedly. Mitigated by final classes/methods and composition.

8. **Q: What does `final` do on a class, method, and variable?** A: Class: cannot be extended. Method: cannot be overridden. Variable: cannot be reassigned.

9. **Q: How does constructor chaining work?** A: `new Dog()` calls Dog constructor → super() → Animal constructor → super() → Object() → Object body → Animal body → Dog body.

10. **Q: Can you override a private method?** A: No. Private methods are not visible to subclasses. Defining a same-named method in subclass is a new method.

11. **Q: What is the equals/hashCode contract?** A: If `a.equals(b)`, then `a.hashCode() == b.hashCode()`. If hash codes differ, equals must be false. Override both or neither.

12. **Q: What is the Liskov Substitution Principle?** A: Subtypes must be substitutable for their base types. A subclass should not weaken base class guarantees.
