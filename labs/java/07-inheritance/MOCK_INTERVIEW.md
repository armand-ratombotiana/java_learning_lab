# Mock Interview Transcript: Inheritance

## Interviewer: Staff Engineer, Oracle
## Candidate: Mid-level Java developer
## Time: 40 minutes
## Focus: Inheritance mechanics, super, constructor chaining, diamond problem

---

**Q1: Walk through the constructor chain for `new Child()` given:**

```java
class GrandParent { GrandParent() { System.out.println("GP"); } }
class Parent extends GrandParent { Parent() { System.out.println("P"); } }
class Child extends Parent { Child() { System.out.println("C"); } }
```

**Candidate**: The output would be: `GP`, `P`, `C`. When constructing a Child, the JVM calls Child's constructor, which first calls `super()` (Parent's constructor), which calls `super()` (GrandParent's constructor). After GrandParent completes, Parent completes, then Child completes.

**Interviewer**: What if Parent doesn't have an explicit no-arg constructor but has a one-arg constructor?

**Candidate**: Then `Child() { super(); }` would fail to compile because Parent doesn't have a no-arg constructor. Child must explicitly call `super(value)` in its constructor.

**Interviewer**: Right. Now, what does this print?

```java
class Base {
    int x = 10;
    Base() { print(); }
    void print() { System.out.println("Base: " + x); }
}
class Derived extends Base {
    int x = 20;
    Derived() { print(); }
    void print() { System.out.println("Derived: " + x); }
}
new Derived();
```

**Candidate**: This prints `Derived: 0` then `Derived: 20`. During Base's constructor, `print()` is overridden to Derived's version (virtual dispatch works even during construction). But `x` hasn't been initialized in Derived yet — it's still 0 (default). After Base's constructor returns, `x = 20` runs, then `print()` prints `Derived: 20`.

**Interviewer**: Why `0`? Why not `10`?

**Candidate**: Because `x` in Derived shadows `x` in Base. The `print()` in Derived accesses `this.x` which refers to Derived's field, not Base's. Since Derived's field initialization hasn't happened yet when Base's constructor runs, it's 0.

**Interviewer**: Good. That's a classic Java gotcha. How do you avoid this?

**Candidate**: Don't call overridable methods from constructors. If you must, make the method `final` or `private`. Or use the Template Method pattern with a helper method that's documented not to be overridden.

**Interviewer**: What about interfaces with default methods? How does the diamond problem work in Java?

**Candidate**: 
```java
interface A { default void foo() { print("A"); } }
interface B { default void foo() { print("B"); } }
class C implements A, B { }  // Compile error: must override foo()
class C implements A, B {
    void foo() { A.super.foo(); }  // Explicitly choose A's version
}
```

**Interviewer**: What if one interface has `default` and another has `static`?

**Candidate**: No conflict — static interface methods aren't inherited. A class can implement both without conflict.

**Interviewer**: How about a class extending a class and implementing an interface with the same default method?

**Candidate**: Class wins over interface. If a superclass has a concrete method (even if not from an interface default), that takes priority over any interface default.

**Interviewer**: Final question: Why does Java not support multiple inheritance of classes?

**Candidate**: Multiple inheritance of classes introduces the diamond problem where the same ancestor class is inherited through two paths. C++ handles this with virtual inheritance, but it adds complexity. Java's single inheritance + multiple interfaces gives most of the benefits without the ambiguity of state inheritance from multiple classes. Interfaces only carry behavior (default methods) not state (fields), so the diamond problem for defaults is resolved by explicit override rules.

---

## Feedback

**Strengths**:
- Deep understanding of constructor chaining order
- Knows the virtual dispatch during construction pitfall
- Clear on diamond problem and interface default resolution

**Areas for Improvement**:
- Could discuss `@Override` annotation's role in catching inheritance errors
- Might mention Records and sealed classes as alternatives to deep hierarchies

**Score**: 4/5 — Solid inheritance understanding
