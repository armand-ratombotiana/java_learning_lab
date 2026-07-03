# OOP Basics — Interview Questions

1. **Q: What are the four pillars of OOP?** A: Encapsulation, Inheritance, Polymorphism, Abstraction.

2. **Q: What is encapsulation?** A: Bundling data and methods together, controlling access with private fields and public methods. Hides internal state.

3. **Q: What is the difference between a class and an object?** A: Class = blueprint (compile-time). Object = instance (runtime). Class defines structure, object has state.

4. **Q: What is a constructor?** A: Special method that initializes objects. Same name as class, no return type. Called with `new`.

5. **Q: What is constructor overloading?** A: Multiple constructors with different parameter lists. Provides different initialization paths.

6. **Q: What is the `this` keyword?** A: Reference to the current object. Used to disambiguate fields from parameters, call other constructors.

7. **Q: What is a static variable?** A: Class-level variable shared across all instances. One copy per class. Accessed via class name.

8. **Q: Can a static method access instance variables?** A: No. Static methods don't have a `this` reference. They can only access static members.

9. **Q: What is the default constructor?** A: No-arg constructor provided by the compiler if no constructors are defined. Initializes fields to defaults.

10. **Q: What is garbage collection?** A: Automatic memory reclamation by JVM. Objects with no references become eligible. System.gc() suggests but doesn't force collection.

11. **Q: What is a fluent interface?** A: API design where methods return `this` to enable chaining: `obj.setX(1).setY(2).doSomething()`.

12. **Q: What is a POJO?** A: Plain Old Java Object — simple class with private fields, getters/setters, no-arg constructor. Used for data transfer.
