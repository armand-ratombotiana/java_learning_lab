# Mock Interview Transcript: Lambdas

## Interviewer: Staff Engineer, Oracle
## Candidate: Mid-level Java developer
## Time: 40 minutes
## Focus: Lambda compilation, functional interfaces, method references

---

**Q1: How does the JVM implement a lambda expression? Show the compilation process.**

**Candidate**: When the compiler encounters a lambda, it doesn't compile it as an anonymous inner class. Instead: (1) It generates an `invokedynamic` instruction. (2) The bootstrap method (`LambdaMetafactory.metafactory()`) is called once at runtime. (3) The bootstrap returns a `CallSite` with a `MethodHandle` that points to a synthetic method. (4) Subsequent calls go directly to the synthetic method, not through the bootstrap.

The synthetic method is a `private static` method in the enclosing class with the lambda's body. The JIT can inline it like any other method.

**Interviewer**: How does this compare with anonymous inner classes?

**Candidate**: Anonymous inner classes create a new class file (+ a new .class file loaded by the class loader). Each instance creates a new object. Lambdas: (1) No new .class file — no class loading overhead. (2) If the lambda doesn't capture values, the `CallSite` returns the same instance every time (singleton). (3) If it captures values, a new instance is created per capture but it's a lightweight object. (4) JIT can inline lambdas much better.

**Interviewer**: What is a Captured Lambda? Give an example.

**Candidate**: 
```java
int offset = 10;
Function<Integer, Integer> addOffset = x -> x + offset;  // captures 'offset'
```
The lambda captures `offset` from the enclosing scope. The synthetic method takes `offset` as an extra parameter: `private static Integer lambda$main$0(int offset, int x) { return x + offset; }`. The runtime creates an object holding `offset` and the method handle.

**Interviewer**: What is the `this` reference inside a lambda? How does it differ from an anonymous class?

**Candidate**: Inside a lambda, `this` refers to the enclosing instance (where the lambda is defined). Inside an anonymous class, `this` refers to the anonymous class instance. This is because lambdas don't introduce a new scope — they're essentially methods of the enclosing class.

```java
class Outer {
    String field = "outer";
    void test() {
        Runnable r1 = () -> System.out.println(this.field);  // "outer"
        Runnable r2 = new Runnable() {
            String field = "inner";
            public void run() { System.out.println(this.field); }  // "inner"
        };
    }
}
```

**Interviewer**: Good. When would you use a method reference instead of a lambda?

**Candidate**: Method references are more concise when the lambda just calls an existing method. The forms: (1) `String::length` (static method reference), (2) `obj::method` (instance method of specific object), (3) `String::compareTo` (instance method of arbitrary object of that type — first argument becomes the receiver), (4) `String::new` (constructor reference).

Use method reference when: the lambda body is a single method call, and the signature matches. Use lambda when: you need to combine operations, access local variables, or the method reference might reduce readability.

**Interviewer**: Write the functional interface types in `java.util.function`.

**Candidate**: `Function<T,R>` — takes T, returns R. `Consumer<T>` — takes T, returns void. `Supplier<T>` — takes nothing, returns T. `Predicate<T>` — takes T, returns boolean. `UnaryOperator<T>` — Function where T=R. `BinaryOperator<T>` — BiFunction where both args and return are T. `BiFunction<T,U,R>` — takes T and U, returns R. `BiConsumer<T,U>` — takes T and U, returns void. `BiPredicate<T,U>` — takes T and U, returns boolean. The primitive versions: `IntFunction`, `IntToLongFunction`, `IntConsumer`, `ObjIntConsumer`, etc.

**Interviewer**: What's the `@FunctionalInterface` annotation's actual effect at the JVM level?

**Candidate**: Actually, it has no effect at the JVM level. It's purely a compile-time annotation. The compiler treats any interface with exactly one abstract method as a functional interface regardless of the annotation. `@FunctionalInterface` adds a compile-time check: if the interface has more than one abstract method, it produces an error. It also serves as documentation.

---

## Feedback

**Strengths**:
- Deep understanding of invokedynamic and lambda compilation
- Clear on captured vs non-captured lambda differences
- Correct `this` semantics comparison
- Comprehensive knowledge of functional interface types

**Areas for Improvement**:
- Could discuss JIT inlining of lambda synthetic methods
- Might mention serialization concerns with lambdas

**Score**: 4.5/5 — Excellent lambda internals knowledge
