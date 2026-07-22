# Interview Questions: Methods

## Company-Specific Focus

### Google
- Method dispatch: static vs virtual, JIT devirtualization and inline caching
- Overload resolution: how the compiler chooses the most specific method
- Varargs method performance: array creation overhead for each invocation

### Microsoft
- Method reference vs lambda: are they equivalent in bytecode?
- Java C# differences: extension methods in C# vs static utility methods in Java
- Overloading with generics: type erasure and bridge methods

### Amazon
- Inline caching and JIT inlining decisions for virtual methods in AWS SDK
- Interface default methods and diamond problem resolution
- Recursive method stack depth vs iterative solutions for server-side reliability

### Meta
- The cost of method calls in tight loops: JIT inlining thresholds
- Default methods in functional interfaces for compatibility in maintained libraries
- Method overloading resolution with method references

### Apple
- Considerations for using private vs package-private methods for encapsulation
- Constructors: chaining with `this()` and `super()`, inheritance ordering
- Method visibility: public API vs internal implementation

### Oracle
- Method signature, erasure, and bridge methods in the JLS
- How are `invokevirtual`, `invokespecial`, `invokeinterface`, `invokestatic` JVM instructions different?
- The `invokedynamic` instruction: how methods are bootstrapped for lambdas
- Method handle API and the bytecode it generates

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 50 Pow(x, n) | Medium | Amazon, Google | Recursive vs iterative with odd/even handling |
| 509 Fibonacci Number | Easy | Facebook, Amazon | Recursive without memo => exponential time; with memo => linear |
| 779 K-th Symbol in Grammar | Medium | Google, Apple | Recursive function with ternary division |
| 1614 Maximum Nesting Depth of Parentheses | Easy | Amazon, Microsoft | Methods for modular decomposition |
| 1823 Find the Winner of the Circular Game | Medium | Google, Apple | Recursive elimination function |

## Real Production Scenarios
- **Uber**: Overloaded methods caused ambiguous call after library version update; explicit casting needed
- **Airbnb**: Varargs method call in hot loop creating 100K array objects per second — refactored to accept an explicit parameter
- **Pinterest**: Deep call stack from recursion caused `StackOverflowError` during batch processing; rewrote as iteration

## Interview Patterns & Tips
- **Overload resolution**: The compiler picks the most specific type at compile-time, not runtime
- **Overriding vs hiding**: Static methods cannot be overridden, only hidden. The method called depends on the compile-time type of the expression
- **Varargs as last parameter**: If there is no argument for a varargs parameter, an empty array is passed, not null
- **Bride methods**: When a subclass overrides a parent method with a narrower return type (covariant return), the compiler generates a bridge method
- **Constructors**: If you don't define any, Java provides a no-arg constructor automatically; if you define a parameterized constructor, you must explicitly define a no-arg one if needed

## Deep Dive Questions
- **JVM**: How do the `invokevirtual` and `invokeinterface` instructions differ in their virtual method lookups? Which has higher overhead?
- **Bytecode**: What does the bytecode for a varargs method call look like? How does the resulting `anewarray` instruction work before invocation?
- **JIT**: How does the JIT compiler decide to inline a method? What are the trade-offs with stack depth and code size?
- **Concurrency**: Is it safe to call a synchronized method in a class that also has non-synchronized methods accessing the same member? What can go wrong?
- **Java 21+**: With the implementation of virtual threads, what are the new recommendations for recursive method depth due to potential stack copying?
