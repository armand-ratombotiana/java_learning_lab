# Mock Interview Transcript: Polymorphism

## Interviewer: Senior SWE, Google
## Candidate: Mid-level Java developer
## Time: 40 minutes
## Focus: Virtual dispatch, dynamic binding, polymorphism patterns

---

**Q1: Explain how the JVM dispatches a method call like `obj.method()`. Walk through the bytecode and JIT.**

**Candidate**: At the bytecode level, `obj.method()` typically compiles to `invokevirtual` for instance methods. The JVM looks at the runtime class of `obj`, finds the method table (vtable), and dispatches to the appropriate implementation. For interfaces, it uses `invokeinterface` which uses an itable lookup.

**Interviewer**: What's the difference between a vtable and an itable?

**Candidate**: A vtable is an array of method pointers for a class. Each class has one vtable, and subclasses extend it (overridden methods replace entries, new methods are appended). An itable is used for interface dispatch — it's an array of method pointers for interface methods. Interface dispatch is typically O(1) but involves an extra level of indirection compared to vtable dispatch.

**Interviewer**: How does the JIT optimize virtual dispatch?

**Candidate**: The JIT uses several techniques: (1) Class Hierarchy Analysis (CHA) — if only one implementation of an interface exists in the loaded classes, it can devirtualize. (2) Inline caching — the JIT assumes the same class as last time and adds a guard check. (3) Megamorphic dispatch — if a call site has many different receiver types, it falls back to a full vtable/itable lookup.

**Interviewer**: Code question: Write a visitor pattern for a payment system.

**Candidate**: 
```java
interface PaymentVisitor {
    void visit(ApplePay p);
    void visit(CreditCard p);
}

interface Payment {
    void accept(PaymentVisitor v);
}

record ApplePay(String deviceId) implements Payment {
    public void accept(PaymentVisitor v) { v.visit(this); }
}
record CreditCard(String number) implements Payment {
    public void accept(PaymentVisitor v) { v.visit(this); }
}

class FeeCalculator implements PaymentVisitor {
    double fee;
    public void visit(ApplePay p) { fee = 0.01; }
    public void visit(CreditCard p) { fee = 0.03; }
}
```

**Interviewer**: In Java 21, is there an alternative to the visitor pattern?

**Candidate**: Yes! Pattern matching in switch can replace the visitor pattern:
```java
double calculateFee(Payment p) {
    return switch (p) {
        case ApplePay a -> 0.01;
        case CreditCard c -> 0.03;
    };
}
```
This is much more concise. The compiler checks exhaustiveness if Payment is a sealed interface.

**Interviewer**: How does this compile compared to the visitor pattern?

**Candidate**: The switch with type patterns compiles to either `invokedynamic` or a chain of `instanceof` checks. With `invokedynamic`, the bootstrap method caches the type mapping for faster dispatch. The visitor pattern compiles to standard `invokeinterface` calls. The pattern matching approach is typically faster because there's no object allocation per visit.

**Interviewer**: Let's test your understanding. What does this print?

```java
class A { void m() { System.out.println("A"); } }
class B extends A { void m() { System.out.println("B"); } }
class C extends B { void m() { System.out.println("C"); } }

A x = new C();
B y = (B) x;
y.m();
```

**Candidate**: It prints `C`. The cast `(B) x` doesn't change the runtime type — `x` is still a `C` object. The reference type is `B`, but `invokevirtual` dispatches based on the actual class. Since `C` overrides `m()`, `C`'s version is called.

**Interviewer**: What if `C` doesn't override `m()`?

**Candidate**: Then it prints `B`, following the inheritance chain. The JVM looks up the method starting from `C`'s vtable, not finding it, continues to `B`'s vtable where it's defined.

---

## Feedback

**Strengths**:
- Deep understanding of virtual dispatch at JVM level
- Knows vtable, itable, inline caching, CHA
- Implements visitor pattern cleanly
- Understands modern Java alternatives (pattern matching)

**Areas for Improvement**:
- Could discuss `invokedynamic` more for lambda-based polymorphic dispatch
- Might mention that Java uses single dispatch only (no multi-dispatch)

**Score**: 4.5/5 — Excellent polymorphism knowledge
