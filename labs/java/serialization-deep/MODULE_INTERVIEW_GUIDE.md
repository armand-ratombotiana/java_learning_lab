# Serialization Deep Dive — Module Interview Guide

## Company-Specific Questions

### Google
- "Compare Java serialization with Protocol Buffers. Why would Google choose protobuf over Java serialization?"
- "How does Java serialization handle object graphs with cycles? Show the mechanism."
- "What are the security vulnerabilities of Java deserialization? How do you mitigate them?"

### Amazon
- "Design a serialization strategy for a high-throughput event pipeline. Why would you choose one format over another?"
- "How does Kryo compare to Java serialization for performance? What are the trade-offs?"
- "You have a legacy system using Java serialization. How do you migrate to a modern format?"

### Meta
- "How does Jackson's ObjectMapper handle polymorphism? What annotations are needed?"
- "Compare JSON and Avro for streaming data. When does schema evolution matter?"
- "How would you optimize JSON serialization for a real-time feed with 100K messages/second?"

### Apple
- "What is the memory overhead of serialized vs in-memory objects? How does protobuf help?"
- "Compare XML vs JSON parsing in Java. Which is more memory efficient on mobile backends?"

### Oracle
- "Explain the exact serialization mechanism: writeObject(), readObject(), writeReplace(), readResolve()."
- "How does serialization interact with records in Java 16+?"
- "What is the serialVersionUID? When does deserialization fail vs succeed with mismatched UIDs?"

## LeetCode Problems

| Problem | Serialization Concept |
|---------|---------------------|
| 297 Serialize and Deserialize Binary Tree | Tree encoding, delimiter-based parsing |
| 271 Encode and Decode Strings | Length-prefixed encoding |
| 428 Serialize and Deserialize N-ary Tree | Recursive serialization |
| 449 Serialize and Deserialize BST | Compact encoding using order properties |
| 606 Construct String from Binary Tree | Pre-order serialization |
| 652 Find Duplicate Subtrees | Post-order serialization + HashMap |

## FAANG Interview Stories

**Story 1: Netflix — Java Deserialization Attack**
> *"A service using Java serialization for inter-process communication was compromised via a deserialization gadget chain. We moved to JSON with schema validation. The fix took 2 weeks of 24/7 effort. Lesson: Never use Java serialization with untrusted data."* — Security Engineer, Netflix

**Story 2: Google — Protobuf Migration**
> *"We migrated a logging pipeline from Java serialization to protobuf. Throughput went from 10K to 100K messages/second. The key: no reflection, no transient keyword handling, fixed schema. That said, development velocity dropped because schema changes required code generation."* — SWE, Google

**Story 3: Uber — JSON Performance Tuning**
> *"A real-time tracking service was using Jackson with default settings. We profiled and found 30% of CPU was in serialization. We switched to Afterburner module (bytecode generation) and then to custom binary encoding. CPU dropped to 8%."* — Staff Engineer, Uber

## Senior vs Staff Deep Dive

### Senior-Level
- "Explain writeReplace() and readResolve(). How are they used in serialization proxies?"
- "Compare protobuf vs Avro vs JSON. When would you choose each?"
- "How does Jackson's JsonGenerator differ from ObjectMapper for streaming?"

### Staff-Level
- "Design a serialization format that supports zero-copy deserialization (like FlatBuffers)."
- "How would you implement a custom ObjectStreamClass with lazy initialization?"
- "Design a serialization protocol for a distributed database with schema evolution across 5000 nodes."
- "How does the JVM implement ObjectOutputStream.writeObject()? Walk through the graph traversal."

## System Design Connections

| System | Serialization Choice |
|--------|---------------------|
| Event pipeline | Avro (schema evolution, compact) |
| RPC framework | protobuf (versioning, cross-language) |
| REST API | JSON (readability, tooling) |
| Cache store | Kryo/Java (speed, in-process) |
| Database log | Custom binary (compact, append-only) |
| Message queue | Avro/Thrift (schema registry) |

## Code Review Scenarios

**Scenario 1**: Missing serialVersionUID.
```java
class User implements Serializable {
    String name;  // If serialVersionUID missing, adding field breaks compat
}
// Fix: private static final long serialVersionUID = 1L;
```

**Scenario 2**: Transient password fields.
```java
class Credentials implements Serializable {
    private String username;
    private transient String password;  // Should be transient
    // But: password is null after deserialization — must handle this
}
```

**Scenario 3**: Deserialization without validation.
```java
// Bad: no validation after deserialization
ObjectInputStream ois = new ObjectInputStream(input);
Object obj = ois.readObject();
// Fix: Validate after deserialization, use filter ObjectInputFilter
```

## Debugging Scenarios

**Scenario 1**: `InvalidClassException: local class incompatible`.
- Cause: serialVersionUID mismatch
- Fix: Add explicit serialVersionUID or ensure class changes are compatible

**Scenario 2**: Deserialization taking 30 seconds.
- Cause: Object graph too deep or circular references
- Fix: Use `ObjectOutputStream.reset()` periodically, or modern format

**Scenario 3**: OOM during serialization of large object tree.
- Root cause: ObjectOutputStream holds references to all serialized objects (grows unbounded)
- Fix: Call `reset()` periodically, or use streaming serialization
