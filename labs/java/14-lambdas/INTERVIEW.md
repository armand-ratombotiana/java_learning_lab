# Interview Questions: Lambdas

## Company-Specific Focus

### Google
- Lambda target types (functional interfaces) and type inference
- Variable capture: effectively final rules and variable scope
- Lambda evaluation vs inner anonymous classes — JVM difference and performance implications

### Microsoft
- Lambda vs. C# delegates — the expression execution only difference
- The JVM method reference vs. behavior that uses target
- Lambda variable capture rules and x.method() semantics

### Amazon
- Lambda performance in high throughput service
- Cache penalty due to lambda in a long multi-threaded environment
- Prefer method references e.g. DoubleStream::sum) over standard functions

### Meta
- Keeping a lambda free from long variable lists
- Lambda flash in the microservice middleware
- The distance of local lambda for parallel and accidental barrier storage

### Apple
- Using method references for minimal object creation
- Immutability in functional aggregates
- Clean lambda for callbacks in the UI layer

### Oracle
- JLS 15.27: Lambda and its generic representation
- JVM: How an idea of lambda is compiled (but not to anonymous class): induced dynamic called in sync
- Condition for capturing using free variable
- Method handle and specialized implementations

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 199 Binary Tree Right Side View | Medium | Yahoo, Amazon, Apple | mapping and filter with lambda |
| 406 Queue Reconstruction by Height | Medium | Google, Amazon | lambda under comparator |
| 973 K closest point to origin | Easy | Google, Amazon | Lambda comparator |
| 56 Merge Intervals | Medium | Google, Amazon, Apple | Lambda to sort intervals |
| 252 Meeting Rooms | Easy | Amazon, Google, Apple | lambda comparator and check intervals |

## Real Production Scenarios
- **Stripe**: Accumulated lambdas in the middle layer leading to poor profiling: JIT had no boundaries
- **Amazon**: Parameter naming is unclear due to useless one-character variable in a lambda: readability of code
- **Microsoft**: Large lambda inside a hot path was performing poorly to not being inlined by the JIT

## Interview Patterns & Tips
- **Effectively final rule**: Variables in the lambda are required to be effectively final.
- **this reference**: In a lambda, 'this' refers to the enclosing object, not the lambda.
- **Method reference**: The new keyword for the lambda: Person::new.
- **Performance**: JIT compiles lambda as a static method in the holder class for each target function.

## Deep Dive Questions
- **JVM**: Why doesn't the JVM compile lambdas to inner classes?
- **Bytecode**: How does the JVM use an invokedynamic and the boot strap for lambda to provide the generation?
- **JIT**: Can the JIT inline a lambda? How does it compare with inline of a static method?
- **Heap vs stack**: Where is the lambda object created? Does it escape to the heap?
- **Java 8 vs 21**: Are virtual threads changed to need to be more cautious about lambda capture?