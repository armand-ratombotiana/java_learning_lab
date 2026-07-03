# Abstraction & Interfaces — Interview Questions

1. **Q: What is the difference between an abstract class and an interface?** A: Abstract class: can have state (fields), constructors, abstract + concrete methods. Interface: only public static final fields, default/static/private methods, abstract methods (no constructors, no instance state).

2. **Q: When would you use an abstract class vs an interface?** A: Abstract class when classes share common state/behavior (IS-A, partial implementation). Interface for contracts/capabilities (CAN-DO, multiple inheritance).

3. **Q: What is a default method and why was it introduced?** A: A method in an interface with a body (Java 8+). Introduced to allow adding methods to existing interfaces without breaking implementations (e.g., adding `stream()` to Collection).

4. **Q: Can an interface have instance variables?** A: No. Interface fields are implicitly `public static final` — constants, not instance state.

5. **Q: What is the diamond problem with default methods?** A: If two interfaces define the same default method, implementing class must override it. Use `InterfaceName.super.method()` to select.

6. **Q: What is a functional interface?** A: Interface with exactly one abstract method. Can be implemented with lambdas. Marked with `@FunctionalInterface`.

7. **Q: What are sealed classes and interfaces?** A: Sealed types (Java 17+) restrict which classes can extend/implement them. `sealed interface Shape permits Circle, Rectangle`. Enables exhaustive pattern matching.

8. **Q: Can an interface extend another interface?** A: Yes. Interface-to-interface inheritance uses `extends`, not `implements`. Adds more methods to the contract.

9. **Q: What is a marker interface?** A: Interface with no methods (e.g., `Serializable`, `Cloneable`). Just marks class as having some capability. Increasingly replaced by annotations.

10. **Q: How does Java's lambda implementation relate to interfaces?** A: Lambda expressions are syntactic sugar for creating instances of functional interfaces. The compiler uses `invokedynamic` for efficient lambda creation.

11. **Q: What is the Adapter pattern?** A: Convert one interface to another. Wrap a class behind the expected interface. Common with third-party libraries.

12. **Q: What is the Template Method pattern?** A: Abstract class defines algorithm skeleton (template method), subclasses implement abstract steps. Controls structure while allowing variation.
