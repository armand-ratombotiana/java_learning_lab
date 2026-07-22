# Interview Questions: Polymorphism

## Company-Specific Focus

### Google
- Dynamic vs static polymorphism: method dispatch differences and JIT impact
- Runtime polymorphism with interfaces: method table and class hierarchy depth cost
- Polymorphism and code bloat: how virtual dispatch affects performance

### Microsoft
- Java is virtual by default; C# needs explicit `virtual` keyword. How does this impact design?
- Overload vs Override: compile-time vs runtime resolution
- Polymorphism in Java generics: bridge methods for type hierarchy

### Amazon
- Polymorphic dispatch overhead in performance-sensitive loops
- Use-case specific polymorphism: visitor pattern in language interpreters
- Double dispatch via pattern matching: Java 21+ making it cleaner

### Meta
- Performance-first: avoiding overload dispatch in hot code paths
- Branch misprediction cost on modern CPUs due to virtual dispatch
- Using enum dispatch as polymorphism light

### Apple
- Using sealed classes to limit inheritance and make virtual calls JIT-friendly
- Polymorphism for extending library without modifying the base
- Proper equals implementation: polymorphic type check using getClass vs instanceof

### Oracle
- Polymorphism and virtual methods in the JVM specification
- Invokevirtual and invokeinterface: underlying mechanisms and differences
- Method resolution rules in successive interfaces (8 and 9)
- Polymorphism with generics: type erasure and heap pollution

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1472 Design Browser History | Medium | Apple, Amazon, Google | Polymorphic actions pattern |
| 1650 Lowest Common Ancestor of a Binary Tree III | Medium | Microsoft, Google | Subtype polymorphism for tree nodes |
| 1628 Design an Expression Tree with Evaluate Function | Medium | Amazon, Apple | Polymorphism for node types |
| 341 Flatten Nested List Iterator | Medium | Apple, Google, Amazon | NestedInteger with double dispatch |
| 430 Flatten a Multilevel Doubly Linked List | Medium | Amazon, Google | Polymorphic fields |

## Real Production Scenarios
- **Shopify**: Instanceof checks in a user-permission model requiring monthly updates — refactored to polymorphic dispatch
- **Uber**: A 1-second CPU time per request attributed to JIT deoptimizing megamorphic call sites in the core workflow
- **Pinterest**: A visitor pattern for content type handling outgrowing its design — replaced with pattern matching switch

## Interview Patterns & Tips
- **Virtual is default in Java**: All non-static, non-final, non-private methods are virtual. Final/private methods are resolved at compile time and may be inlined.
- **instanceof is slow in hot paths**: Consider replacement with polymorphism through interface or switch expressions
- **Covariant returns**: Overriding method can return a subclass of the original return type
- **Polymorphism and constructors**: Calling an overridden method from within a parent constructor can lead to uninitialized state issues
- **Co/Contravariance in generics**: Use `? extends T` for covariance and `? super T` for contravariance

## Deep Dive Questions
- **JVM**: How does inlining cache work in HotSpot JVM? What is the bi-morphic and megamorphic call site threshold?
- **Bytecode**: How do invokevirtual vs invokeinterface compare? What is the byte code for the former and the latter?
- **JIT**: What is the number of unique types (*_) necessary to trigger deoptimization?
- **Memory**: How is the virtual method table (vtable) laid out in memory? Does it include methods from all ancestors?
- **Java 21+**: How can pattern matching and sealed classes reduce the need for the visitor pattern?