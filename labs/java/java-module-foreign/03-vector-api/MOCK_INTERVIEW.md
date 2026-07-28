# Mock Interview: Vector API

**Interviewer:** "Implement a dot product of two float arrays using the Vector API."

**Candidate:**

```java
static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

float dotProduct(float[] a, float[] b) {
    float sum = 0;
    int i = 0;

    // Ensure same length
    assert a.length == b.length;

    // Vectorized loop
    for (; i <= a.length - SPECIES.length(); i += SPECIES.length()) {
        FloatVector va = FloatVector.fromArray(SPECIES, a, i);
        FloatVector vb = FloatVector.fromArray(SPECIES, b, i);
        FloatVector vmul = va.mul(vb);
        sum += vmul.reduceLanes(VectorOperators.ADD);
    }

    // Tail loop
    for (; i < a.length; i++) {
        sum += a[i] * b[i];
    }
    return sum;
}
```

**Interviewer:** "How would you handle misaligned data?"

**Candidate:** "I use `fromArray` which handles alignment internally. For maximum performance, I'd ensure arrays are aligned to species boundary using `MemorySegment`:

```java
try (Arena arena = Arena.ofConfined()) {
    MemorySegment seg = arena.allocate(
        SPECIES.length() * (float) Float.BYTES,
        SPECIES.vectorBitSize() / 8  // alignment
    );
    // Access via MemorySegment + Vector API
}
```

But for most cases, the JIT and hardware handle misalignment with minimal penalty."
