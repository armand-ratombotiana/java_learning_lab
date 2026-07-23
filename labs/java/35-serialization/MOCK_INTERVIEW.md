# Mock Interview Transcript: Serialization

## Interviewer: Staff Engineer, Google
## Candidate: Senior Java developer
## Time: 35 minutes
## Focus: Java serialization internals, protobuf, JSON, benchmarks

---

**Q1: Walk through the Java serialization process for a simple object.**

**Candidate**: When `ObjectOutputStream.writeObject(obj)` is called: (1) Check if object is `null` → write null. (2) Check if object is already written → write handle for existing reference. (3) Check if class implements `Serializable` → if not, throw `NotSerializableException`. (4) Write class descriptor (class name, serialVersionUID, field count/types). (5) Check for `writeReplace()` → if present, serialize the replacement. (6) Write field values (primitives directly, objects recursively). (7) Check for `writeObject()` method → if present, call it for custom serialization.

**Interviewer**: What is `serialVersionUID` and what happens if it changes?

**Candidate**: `serialVersionUID` is a hash used to verify class compatibility during deserialization. If the sender and receiver have different UIDs, deserialization throws `InvalidClassException`. The default UID is derived from the class structure (methods, fields, interfaces). Explicit declaration `private static final long serialVersionUID = 1L` makes the UID stable across compatible changes.

**Interviewer**: What are compatible and incompatible changes?

**Candidate**: Compatible changes: (1) Adding fields (default value used). (2) Adding writeObject/readObject (custom handling). (3) Removing fields marked `transient`. (4) Changing access modifiers. Incompatible changes: (1) Removing non-transient fields. (2) Changing class hierarchy. (3) Changing type of existing field. (4) Changing serialVersionUID.

**Interviewer**: Compare Java serialization with Protocol Buffers.

**Candidate**: Protobuf advantages: (1) Language-neutral — works across Java, Go, Python, etc. (2) Schema evolution — adding fields doesn't break old readers. (3) Much smaller (binary, no class metadata). (4) Faster (no reflection, no graph traversal). (5) No security vulnerabilities (no gadget chain attacks). (6) Generated code is type-safe. Java serialization advantages: (1) No code generation needed. (2) Handles object graphs with cycles. (3) Works with any Serializable class automatically.

**Interviewer**: How does `readResolve()` work? When is it used?

**Candidate**: `readResolve()` is called after deserialization. It can return a substitute object. Used for: (1) Singleton pattern — return the existing singleton instance instead of the deserialized copy. (2) Enum-like types — ensure identity is preserved. (3) Object pooling — return a cached object. It's the reverse of `writeReplace()`.

**Interviewer**: Implementation of serialization proxy pattern.

**Candidate**: 
```java
class Person implements Serializable {
    private String name;
    private int age;
    
    private Object writeReplace() {
        return new PersonProxy(this);
    }
    
    private static class PersonProxy implements Serializable {
        private String name;
        private int age;
        PersonProxy(Person p) { this.name = p.name; this.age = p.age; }
        private Object readResolve() {
            return new Person(name, age);  // Use constructor with validation
        }
    }
}
```
This prevents attackers from creating invalid Person objects via custom serialization streams. The proxy's `readResolve()` uses the constructor, which validates parameters.

**Interviewer**: How does JSON serialization (Jackson) handle polymorphism?

**Candidate**: Jackson uses `@JsonTypeInfo` and `@JsonSubTypes`:
```java
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Circle.class, name = "circle"),
    @JsonSubTypes.Type(value = Rectangle.class, name = "rectangle")
})
interface Shape { }
```
Without annotations, Jackson needs a `@class` property in JSON, which is fragile and a security risk.

**Interviewer**: Final: What's the most secure serialization approach for a new system?

**Candidate**: Don't use Java serialization for new systems. Use protocol buffers (cross-platform, schema evolution, fast) or JSON (readable, universal). If you must use Java serialization: use `ObjectInputFilter`, limit class whitelists, never deserialize untrusted data. For configuration, use YAML or JSON. For RPC, use gRPC (protobuf). For caching, use Kryo or protobuf.

---

## Feedback

**Strengths**:
- Complete serialization mechanism walkthrough
- Understands UID, compatible changes, writeReplace/readResolve
- Serialization proxy pattern correctly implemented
- Strong serialization security advice

**Areas for Improvement**:
- Could discuss `Externalizable` interface
- Mention `lookup.defineClass()` for dynamic deserialization

**Score**: 4.5/5 — Excellent serialization knowledge
