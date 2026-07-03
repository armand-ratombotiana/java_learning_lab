# Methods — Interview Questions

1. **Q: Is Java pass-by-value or pass-by-reference?** A: Pass-by-value. For primitives, value is copied. For objects, reference value is copied — you can modify the object but cannot swap references.

2. **Q: What is method overloading?** A: Multiple methods with same name, different parameter lists. Resolved at compile time based on argument types.

3. **Q: How does varargs work?** A: `void method(String... args)` compiles to `void method(String[] args)`. Each call site creates an array. Must be last parameter, only one allowed.

4. **Q: Can you overload a method by changing return type only?** A: No. Return type is not part of method signature. Must differ in parameter count or types.

5. **Q: What is the difference between overloading and overriding?** A: Overloading = same name, different params (compile-time). Overriding = same signature (runtime). Overriding is for subclasses.

6. **Q: Why does recursion cause StackOverflowError?** A: Each recursive call adds a stack frame. Default stack size is ~1MB. Deep recursion fills the stack.

7. **Q: What is a static method?** A: Method that belongs to the class, not instances. Called on class name: `ClassName.method()`. Cannot access instance variables.

8. **Q: What are the steps in a method call?** A: (1) Push arguments, (2) Create stack frame, (3) Store return address, (4) Execute method body, (5) Return value, (6) Restore caller frame.

9. **Q: What is the `main` method?** A: Entry point of Java application. Signature: `public static void main(String[])`. JVM calls it to start the program.

10. **Q: What is method inlining?** A: JIT optimization where method body replaces the call site. Eliminates call overhead. Applied to small, hot methods.

11. **Q: What is a synthetic method?** A: Compiler-generated method for language features (e.g., bridge methods for generics). Not present in source code.

12. **Q: Can you define a method inside another method?** A: No (traditional methods). Yes for lambdas and local classes (Java 8+).
