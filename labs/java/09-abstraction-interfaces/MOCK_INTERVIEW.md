# Mock Interview Transcript: Abstraction & Interfaces

## Interviewer: Staff Engineer, Amazon
## Candidate: Junior Java developer
## Time: 35 minutes
## Focus: Abstract classes vs interfaces, default methods, functional interfaces

---

**Q1: When do you use an abstract class vs an interface?**

**Candidate**: Abstract classes are for sharing state and behavior with a common ancestor. Interfaces are for defining capabilities. Specifically: abstract classes can have state (fields), constructors, and non-public methods. Interfaces (pre-Java 8) had only public abstract methods.

**Interviewer**: Since Java 8, interfaces have default and static methods. Does that change the choice?

**Candidate**: Yes. Default methods let interfaces provide behavior, but they can't hold state (non-static fields). So the rule of thumb is: if you need shared state, use an abstract class. If you're defining a contract/capability, use an interface. Also, classes can implement multiple interfaces but extend only one class.

**Interviewer**: What if you need a base class for a collection of related classes with shared state AND you want to define a contract?

**Candidate**: Use an abstract class for the state and base behavior, and implement interfaces for capabilities. For example, `AbstractList` provides shared state and common behavior, while also implementing `List`, `Collection`, and `Iterable` interfaces.

**Interviewer**: Design a functional interface for a predicate that checks if a string matches a pattern.

**Candidate**: 
```java
@FunctionalInterface
interface StringMatcher {
    boolean matches(String s);
}

// Usage:
StringMatcher startsWithA = s -> s.startsWith("A");
StringMatcher lengthGT5 = s -> s.length() > 5;
```

**Interviewer**: What does `@FunctionalInterface` guarantee?

**Candidate**: It guarantees the interface has exactly one abstract method. If a second abstract method is added, the compiler produces an error. It also implies the interface can be used as a lambda target type.

**Interviewer**: Can a functional interface have default methods?

**Candidate**: Yes. Default methods don't count toward the single abstract method requirement. So `Comparator` is a `@FunctionalInterface` with one abstract method (`compare`) and many default methods (`thenComparing`, `reversed`, etc.).

**Interviewer**: Let's design a plugin system using abstraction. How would you structure it?

**Candidate**: 
```java
public interface Plugin {
    void initialize(PluginContext ctx);
    void execute();
    void shutdown();
}
```
Each plugin implements this interface. The plugin loader discovers implementations (via SPI, classpath scanning, or custom ClassLoader). The host application calls `initialize`, `execute`, `shutdown` without knowing the implementation details.

**Interviewer**: What about providing sensible defaults?

**Candidate**: 
```java
public interface Plugin {
    default void initialize(PluginContext ctx) { }
    void execute();
    default void shutdown() { }
}
```
Now plugin authors only need to implement `execute()`, reducing boilerplate.

**Interviewer**: Good. Final question: compare SPI (Service Provider Interface) with direct implementation.

**Candidate**: SPI allows runtime discovery of implementations — useful for plugin systems. `ServiceLoader` in Java loads implementations declared in `META-INF/services/`. Direct implementation is simpler and compile-time checked but lacks extensibility. SPI is used by JDBC, JAXP, and most Java extension frameworks.

---

## Feedback

**Strengths**:
- Clear on abstract class vs interface trade-offs
- Correctly uses `@FunctionalInterface`
- Good plugin system design with sensible defaults
- Knows SPI mechanism

**Areas for Improvement**:
- Should mention sealed interfaces (Java 17+) for restricted abstraction
- Could discuss `default` method resolution with multiple inheritance

**Score**: 3.5/5 — Good abstraction understanding, needs modern Java features
