# Mock Interview Transcript: Object Layout & Memory

## Interviewer: Staff Engineer, Apple
## Candidate: Senior Java developer
## Time: 35 minutes
## Focus: Object layout, compressed OOPs, alignment, padding

---

**Q1: How is a Java object laid out in memory?**

**Candidate**: A Java object has three regions: (1) Header: mark word (8 bytes on 64-bit) + klass pointer (4 bytes with compressed OOPs, 8 without). (2) Instance data: fields in declaration order (aligned to their size). (3) Padding: to align object size to 8 bytes (alignment boundary). For arrays: header + 4-byte length + array data + padding.

**Interviewer**: What's the size of an empty Object (on 64-bit with compressed OOPs)?

**Candidate**: Mark word: 8 bytes. Klass pointer: 4 bytes (compressed). Instance data: 0 bytes. Padding: 4 bytes (to align to 8). Total: 16 bytes. Without compressed OOPs: 8 + 8 = 16 bytes (no padding needed, already aligned). Wait — without compressed OOPs: 8 + 8 = 16 (aligned). With compressed OOPs: 8 + 4 = 12, padded to 16.

**Interviewer**: Calculate the size of `new Integer(5)`.

**Candidate**: Object header: 12 bytes (8 mark + 4 klass). Instance field: 4 bytes (int). Padding: 0 bytes (12 + 4 = 16, already aligned). Total: 16 bytes. Compare with primitive `int`: 4 bytes on stack (or in array: 4 + array overhead of 16 bytes header = 20 bytes per element for array of 1).

**Interviewer**: How does `-XX:+UseCompressedOops` affect object layout?

**Candidate**: With compressed OOPs (default for heaps < 32GB): (1) Object references are 4 bytes instead of 8. (2) Klass pointer is 4 bytes. (3) Object headers are 12 bytes (compared to 16 without). (4) Better cache utilization (more references per cache line). (5) Only works for heaps < ~32GB (technically < 32GB + heap base alignment). For heaps > 32GB, OOPs become 64-bit again.

**Interviewer**: Explain field reordering by the JVM.

**Candidate**: The JVM reorders fields to minimize padding. Fields are laid out by size in this order: doubles (8 bytes) → longs (8 bytes) → ints (4 bytes) → shorts (2 bytes) → chars (2 bytes) → booleans (1 byte) → bytes (1 byte) → references (4/8 bytes). Then fields declared in parent classes. This minimizes gaps. Use `-XX:+PrintFieldLayout` to see the actual layout. `@Contended` (JVM internal) adds padding to prevent false sharing.

**Interviewer**: What is false sharing? How do you prevent it?

**Candidate**: False sharing occurs when two threads modify different variables that happen to be on the same CPU cache line (64 bytes on x86). Even though the variables are independent, the cache coherency protocol invalidates the entire cache line for each write. Prevention: (1) Add padding between variables (put 56 bytes of unused space between them). (2) Use `@Contended` annotation (JVM-internal, requires `-XX:-RestrictContended`). (3) Use `VarHandle` with proper alignment. (4) Use ThreadLocal for hot fields.

**Interviewer**: Calculate the size of this class:

```java
class MyClass {
    byte a;       // 1 byte
    long b;       // 8 bytes
    boolean c;    // 1 byte
    int d;        // 4 bytes
}
```

**Candidate**: JVM reorders: long b (8) → int d (4) → byte a (1) → boolean c (1) = 14 bytes. Object header: 12 bytes. Total: 12 + 14 = 26, padded to 32 bytes. Without reordering: byte (1) + 7 padding + long (8) + boolean (1) + 3 padding + int (4) = 24 bytes just for fields + 12 header = 36 bytes. Reordering saves 4 bytes.

**Interviewer**: How does `-XX:ObjectAlignmentInBytes` affect object layout?

**Candidate**: Default alignment is 8 bytes. Objects are sized to a multiple of the alignment. `-XX:ObjectAlignmentInBytes=16` aligns to 16-byte boundaries. This can reduce the effectiveness of compressed OOPs (the base must be aligned to the alignment). Larger alignment may waste memory but can improve cache behavior for large objects.

**Interviewer**: Final: How does the JOL (Java Object Layout) tool help?

**Candidate**: JOL (from OpenJDK) visualizes object layouts:
```java
System.out.println(ClassLayout.parseInstance(new MyClass()).toPrintable());
// Output:
// com.example.MyClass object internals:
// OFF  SZ   TYPE DESCRIPTION               VALUE
//   0   8        (object header: mark)     0x0000005e...
//   8   4        (object header: class)    0xf80025c1
//  12   4    int MyClass.d                 0
//  16   1   byte MyClass.a                 0
//  17   1 boolean MyClass.c                false
//  18   6        (loss due to the next object alignment)
```
JOL also shows: `GraphLayout.parseInstance(obj).totalSize()` for retained size, footprint analysis for collections (HashMap footprint, ArrayList overhead). Essential for memory optimization.

---

## Feedback

**Strengths**:
- Detailed object header breakdown
- Compressed OOPs effect on layout
- Field reordering rules
- False sharing explanation
- Memory size calculations
- JOL tool knowledge

**Areas for Improvement**:
- Could discuss `IdentityHashMap`'s special handling
- Mention primitive wrapper size vs primitives

**Score**: 5/5 — Expert memory layout knowledge
