# Mock Interview Transcript: OOP Basics

## Interviewer: Senior SWE, Microsoft
## Candidate: Junior Java developer
## Time: 35 minutes
## Focus: Encapsulation, constructors, access modifiers, static

---

**Q1: Design an immutable class for a Person.**

**Candidate**: 
```java
public final class Person {
    private final String name;
    private final int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
}
```

**Interviewer**: What if `name` is a mutable object like `List<String>`?

**Candidate**: Then I'd need to make a defensive copy in the constructor and in the getter:
```java
public final class Person {
    private final String name;
    private final List<String> nicknames;
    
    public Person(String name, List<String> nicknames) {
        this.name = name;
        this.nicknames = List.copyOf(nicknames);  // immutable copy
    }
    
    public List<String> getNicknames() { return nicknames; }
}
```
Using `List.copyOf()` creates an unmodifiable list. For the getter, returning the reference is safe because the list is already immutable.

**Interviewer**: Why `final` class? Why `final` fields?

**Candidate**: `final` class prevents subclassing, which could add mutable state or override methods. `final` fields ensure the fields are set exactly once (in constructor). Combined with no setter methods and defensive copies, this guarantees immutability even if the class is extended (since it's final).

**Interviewer**: What's the builder pattern's role in immutability?

**Candidate**: Builders help when an immutable class has many fields. Instead of a telescoping constructor (one for each field combination), the builder collects parameters and calls the constructor once. The builder itself is mutable, but the built object is immutable.

**Interviewer**: Compare `public`, `protected`, package-private, `private`.

**Candidate**: `public` — everyone can see. `protected` — subclasses + same package. Package-private (default, no modifier) — same package only. `private` — same class only. Protected without inheritance is package-private for unrelated classes.

**Interviewer**: What about top-level classes — can they be `private` or `protected`?

**Candidate**: No. Top-level classes can only be `public` or package-private. `private` and `protected` only apply to inner classes.

**Interviewer**: Explain the `static` keyword for methods and fields.

**Candidate**: `static` members belong to the class, not instances. Static fields are initialized when the class is loaded. Static methods can't access instance fields (no `this` reference). Static methods are dispatched statically at compile time (invokestatic), while instance methods use virtual dispatch (invokevirtual).

**Interviewer**: What does this print?

```java
class Parent {
    static String get() { return "parent"; }
    String getInstance() { return "parent instance"; }
}
class Child extends Parent {
    static String get() { return "child"; }
    String getInstance() { return "child instance"; }
}

Parent p = new Child();
System.out.println(p.get());
System.out.println(p.getInstance());
```

**Candidate**: `p.get()` prints `"parent"` — static methods are resolved at compile time based on the reference type (Parent), not the runtime type. `p.getInstance()` prints `"child instance"` — instance methods use virtual dispatch, so the Child's override is called.

**Interviewer**: Exactly. That's the key distinction between static and instance method dispatch.

---

## Feedback

**Strengths**:
- Correct immutable class design with defensive copies
- Understands access modifiers and their scope
- Clear on static dispatch vs virtual dispatch

**Areas for Improvement**:
- Should have mentioned `record` (Java 16+) as a simpler immutable class
- Could discuss `UnmodifiableCollections` vs `ImmutableCollections` (Java 9+)

**Score**: 3.5/5 — Good fundamentals, needs modern Java awareness
