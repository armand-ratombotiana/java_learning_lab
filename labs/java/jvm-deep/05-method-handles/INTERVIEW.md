# Interview Questions: Method Handles

## Company-Specific Focus

### Google
- MethodHandle: typed, executable reference to a method, field, or constructor
- MethodType: method signature representation (return type + parameter types)
- Lookup: MethodHandles.Lookup for finding methods

### Microsoft
- Method handles vs C# delegates/function pointers
- invoke() vs invokeExact(): type checking at link time vs invocation time

### Amazon
- invokedynamic: uses method handles for dynamic call sites
- Lambda compilation: lambdas are compiled via invokedynamic with method handles
- VarHandle: method handles for atomic field operations

### Meta
- MethodHandle performance: faster than reflection
- bindTo: binding a method handle to a specific receiver
- filterArguments, foldArguments: combining method handles

### Apple
- MethodHandle transformations: compose, filter, fold
- GuardWithTest: conditional method dispatch

### Oracle
- java.lang.invoke package specification
- invokedynamic instruction: bootstrap methods and method handles
- MethodHandles API: collection of useful method handle factories

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — method handles are JVM implementation) |

## Real Production Scenarios
- **LinkedIn**: Method handles for dynamic dispatch in serialization framework — faster than reflection
- **Netflix**: VarHandle for lock-free field updates — replacing AtomicReferenceFieldUpdater

## Interview Patterns & Tips
- **InvokeExact**: requires exact type match at call site
- **Invoke**: performs type conversion if needed
- **Lookup**: controls access based on the creating class's context
- **Performance**: method handles are faster than reflection

## Deep Dive Questions
- **MethodHandle vs Reflection**: What makes method handles faster?
- **invokedynamic**: How is invokedynamic bootstrapped with method handles?
- **VarHandle**: How does VarHandle use method handles for field access?
- **MethodType**: How is the method type represented?
- **Transformations**: How do you compose method handles?