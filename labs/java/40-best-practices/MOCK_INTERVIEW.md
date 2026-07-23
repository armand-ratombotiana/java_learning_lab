# Mock Interview Transcript: Best Practices

## Interviewer: Staff Engineer, Google
## Candidate: Senior Java developer
## Time: 40 minutes
## Focus: Effective Java, code quality, design, patterns

---

**Q1: What is the most impactful item from Effective Java you've applied?**

**Candidate**: Item 17: "Design and document for inheritance or else prohibit it." I've seen deep inheritance hierarchies that were impossible to maintain. Instead, favor composition over inheritance. Use `final` classes by default and only open them for extension when it's been explicitly designed and documented.

**Interviewer**: When should you use `Optional` as a return type?

**Candidate**: When the absence of a result is a valid, expected outcome — not an error. Examples: finding an element in a collection, looking up a user by ID. Don't use Optional for: (1) Return types that are collections (return empty list instead). (2) Method parameters (overloading is better). (3) Fields in classes (null or sentinel is better). (4) Performance-critical paths.

**Interviewer**: Compare builder pattern vs telescoping constructors.

**Candidate**: Telescoping constructors are hard to read and error-prone:
```java
new User("Alice", 30, "NYC", "alice@example.com", true);  // Which is which?
```
Builder pattern is clear and flexible:
```java
User.builder()
    .name("Alice")
    .age(30)
    .city("NYC")
    .email("alice@example.com")
    .isActive(true)
    .build();
```
The builder is essential when a class has 4+ parameters, especially optional ones.

**Interviewer**: What's the proper way to implement `equals()` and `hashCode()`?

**Candidate**: 
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Person)) return false;
    Person p = (Person) o;
    return age == p.age && name.equals(p.name);
}

@Override
public int hashCode() {
    return Objects.hash(name, age);
}
```
Contract: reflexive, symmetric, transitive, consistent, and `equals()` → same `hashCode()`.

**Interviewer**: How do you avoid null pointer exceptions?

**Candidate**: (1) Use `Objects.requireNonNull()` early. (2) Return empty collections, not null. (3) Use `Optional` for optional return values. (4) Annotate with `@Nullable` and `@NonNull`. (5) Enable `-Werror` with null analysis in IDE. (6) Use `Map.getOrDefault()`, `Map.putIfAbsent()`. (7) Use `Stream.ofNullable()` (Java 9+). (8) Use records with compact constructors for validation.

**Interviewer**: When would you use `try-with-resources` vs explicit close?

**Candidate**: Always prefer try-with-resources for anything implementing `AutoCloseable`:
```java
// Correct — auto-closes, even with exceptions
try (var in = new FileInputStream("file.txt"); 
     var out = new FileOutputStream("out.txt")) {
    in.transferTo(out);
}
```
Only use explicit close() when you need to manage resource lifecycle outside a single method (rare).

**Interviewer**: How do you handle thread safety in shared objects?

**Candidate**: Strategies in order of preference: (1) Immutability — use `record`, `final` fields, unmodifiable collections. (2) Thread confinement — use ThreadLocal, single-threaded actor model. (3) Synchronization — `synchronized`, `ReentrantLock`. (4) Atomicity — `AtomicInteger`, `LongAdder`, `ConcurrentHashMap`. (5) Volatile for visibility-only (not atomicity).

**Interviewer**: What is the most misunderstood Java feature you've seen?

**Candidate**: `float` and `double` precision. I regularly see monetary calculations with `double` (use `BigDecimal` or `long` for cents). Also, `==` for `Integer` comparison outside the -128 to 127 cache range. And `HashMap` without proper `hashCode()` leading to O(n) lookup. Another common one: thinking `synchronized` on a method is always better than `synchronized` on a specific lock object.

**Interviewer**: Final: What's your recommended project structure for a Java service?

**Candidate**: Package by feature, not by layer:
```
com.example.order/
    Order.java              (record or domain class)
    OrderRepository.java     (interface)
    OrderRepositoryImpl.java (implementation)
    OrderService.java        (business logic)
    OrderController.java     (REST endpoint)
    OrderMapper.java         (DTO ↔ domain mapping)
    OrderValidator.java      (validation)
```
This is better than package-by-layer (model/, repository/, service/, controller/) because features change together. Internally, each package is cohesive and can be extracted as a module if needed.

---

## Feedback

**Strengths**:
- Strong Effective Java knowledge
- Equals/hashCode implementation
- Null safety strategies
- Thread safety patterns
- Package-by-feature structure

**Areas for Improvement**:
- Could discuss defensive copying vs unmodifiable wrappers
- Mention JSR 305 for null annotations

**Score**: 4.5/5 — Excellent best practices knowledge
