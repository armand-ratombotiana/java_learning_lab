# Mock Interview Transcript: Sealed Classes

## Interviewer: Staff Engineer, Amazon
## Candidate: Mid-level Java developer
## Time: 30 minutes
## Focus: Sealed class syntax, permits, exhaustiveness

---

**Q1: What problem do sealed classes solve?**

**Candidate**: Sealed classes give you controlled inheritance — you specify exactly which classes can extend/implement a parent. This enables exhaustive pattern matching by the compiler — no `default` case needed. It moves domain invariants from runtime to compile-time checking.

**Interviewer**: Write a sealed interface for a payment method.

**Candidate**: 
```java
public sealed interface Payment permits CreditCard, PayPal, Crypto {}

record CreditCard(String last4, String expiry) implements Payment {}
record PayPal(String email) implements Payment {}
record Crypto(String address, String currency) implements Payment {}
```

**Interviewer**: What are the restrictions on permitted subclasses?

**Candidate**: Permitted subclasses must: (1) Be in the same module or same package (Java 17). (2) Be `final`, `sealed`, or `non-sealed`. (3) Be direct subclasses — no transitive permits. (4) A permitted class can't be extended by non-permitted classes.

**Interviewer**: What does `non-sealed` mean?

**Candidate**: `non-sealed` reopens a sealed hierarchy — the class permits subclasses of its own without restriction. It's like saying "I'm ending the sealed inheritance chain here — anyone can extend from me." For example:
```java
sealed interface Shape permits Circle, Rectangle {}
final class Circle implements Shape {}
non-sealed class Rectangle implements Shape {}  // Anyone can extend Rectangle
```

**Interviewer**: How does the compiler check exhaustiveness for switch on sealed types?

**Candidate**: The compiler analyzes the permits clause to know all possible subclasses. For a `switch` expression, it checks that all permitted types are covered. If you add a new permitted subclass, all `switch` statements that don't cover it won't compile. The `default` case can be omitted when coverage is exhaustive.

**Interviewer**: How does this improve on enums?

**Candidate**: Enums are for fixed sets of values. Sealed classes are for fixed sets of types. Enums can't carry instance-specific state (beyond the constant itself). Sealed subtypes can have arbitrary fields and behavior. Example: a sealed `Expression` interface with `Add`, `Multiply`, `Negate` records — each carries different fields.

**Interviewer**: Write an evaluator for a sealed Expression type.

**Candidate**: 
```java
sealed interface Expr permits Add, Multiply, Negate, Value {}
record Add(Expr left, Expr right) implements Expr {}
record Multiply(Expr left, Expr right) implements Expr {}
record Negate(Expr inner) implements Expr {}
record Value(int val) implements Expr {}

int evaluate(Expr e) {
    return switch (e) {
        case Add(var l, var r) -> evaluate(l) + evaluate(r);
        case Multiply(var l, var r) -> evaluate(l) * evaluate(r);
        case Negate(var inner) -> -evaluate(inner);
        case Value(var v) -> v;
    };
}
```

**Interviewer**: What happens if someone creates a subclass through bytecode manipulation?

**Candidate**: The JVM enforces sealed classes at the class-file level. If a class attempts to extend a sealed class without being listed in the `PermittedSubclasses` attribute, the JVM's class verifier rejects the class with `IncompatibleClassChangeError` or `VerifyError`. This holds even if bytecode manipulation bypasses the Java compiler.

---

## Feedback

**Strengths**:
- Clear explanation of sealed class purpose and benefits
- Correct permits syntax with exhaustive switch
- Understands `non-sealed` reopening
- Knows JVM-level enforcement

**Areas for Improvement**:
- Could discuss sealed classes in `module-info.java` (cross-module permits)
- Might mention `@SuppressWarnings("preview")` for sealed in older versions

**Score**: 4/5 — Strong sealed class understanding
