# Mock Interview Transcript: Records

## Interviewer: Senior SWE, Google
## Candidate: Mid-level Java developer
## Time: 30 minutes
## Focus: Record semantics, limitations, use cases

---

**Q1: What is a record in Java? What does it provide?**

**Candidate**: A record is a transparent carrier for immutable data. It automatically generates: constructor with all fields, `equals()`, `hashCode()`, `toString()`, accessor methods (not `getX()` — just `x()`), and the `component()` array for reflection. Records are implicitly `final` and their fields are `private final`.

**Interviewer**: Write a Record for a complex number.

**Candidate**: 
```java
record Complex(double real, double imag) {
    public Complex {  // Compact constructor — no field assignment needed
        if (Double.isNaN(real) || Double.isNaN(imag)) {
            throw new IllegalArgumentException("NaN not allowed");
        }
    }
    
    public double abs() { return Math.sqrt(real * real + imag * imag); }
    
    public Complex add(Complex other) {
        return new Complex(real + other.real, imag + other.imag);
    }
}
```

**Interviewer**: What's the difference between a canonical constructor and a compact constructor?

**Candidate**: The canonical constructor takes all components. The compact constructor omits the parameter list — the parameters are implicitly declared as the components. In a compact constructor, you can't assign to fields (they're implicitly assigned at the end). You can only validate or transform parameters.

**Interviewer**: Can records be extended? Can they extend other classes?

**Candidate**: Records can't extend other classes (they implicitly extend `java.lang.Record`). They're implicitly `final`, so they can't be extended. However, records can implement interfaces.

**Interviewer**: How do Records work with serialization?

**Candidate**: Records have special serialization behavior: (1) They use `RecordCodec` instead of standard serialization. (2) They can't customize serialization via `writeObject`/`readObject`. (3) Deserialization always calls the canonical constructor, ensuring invariants. (4) `serialVersionUID` is optional (derived from component types). (5) Records are more secure — no way to bypass constructor validation during deserialization.

**Interviewer**: What constraints apply to record fields?

**Candidate**: (1) All fields must be `private final` (implicit). (2) No instance field initializers (except in compact constructor). (3) No instance initializer blocks. (4) Can have static fields and methods. (5) Can't declare instance methods that modify state. (6) Can't add instance fields in the body beyond the components.

**Interviewer**: When NOT to use a record?

**Candidate**: (1) When you need mutable state (use class). (2) When you need inheritance (records are final). (3) When you need JPA entities (JPA requires no-arg constructor, mutable fields). (4) When you need different equals/hashCode behavior than "all fields". (5) When you need validation that depends on multiple instances or external state. (6) When binary compatibility with existing serialized data is required.

**Interviewer**: How do records interact with Jackson serialization?

**Candidate**: Jackson 2.12+ supports records. It uses the canonical constructor for deserialization and accessor methods (not getters) for serialization. Records work well as DTOs. You need `@JsonCreator` or constructor parameter annotations for property name mapping if using Jackson < 2.12.

---

## Feedback

**Strengths**:
- Complete record semantics explanation
- Correct compact constructor usage with validation
- Understands serialization and security benefits
- Identifies appropriate and inappropriate use cases

**Areas for Improvement**:
- Could discuss Lombok's `@Value` comparison
- Mention `javap` shows the generated methods

**Score**: 4/5 — Ready for record usage in production
