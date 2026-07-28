# Deep Dive: Vector API

## 1. Species and Vectors

A `VectorSpecies` represents a specific SIMD lane configuration:

```java
static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;
// On x86 with AVX2: 256-bit = 8 floats
// On AArch64 NEON: 128-bit = 4 floats
```

Create vectors from arrays:

```java
float[] a = {1, 2, 3, 4, 5, 6, 7, 8};
float[] b = {8, 7, 6, 5, 4, 3, 2, 1};
float[] c = new float[8];

int i = 0;
for (; i < SPECIES.loopBound(a.length); i += SPECIES.length()) {
    FloatVector va = FloatVector.fromArray(SPECIES, a, i);
    FloatVector vb = FloatVector.fromArray(SPECIES, b, i);
    FloatVector vc = va.add(vb);
    vc.intoArray(c, i);
}
// Handle remaining elements with scalar loop
for (; i < a.length; i++) { c[i] = a[i] + b[i]; }
```

## 2. Common Operations

| Operation | Method | Description |
|-----------|--------|-------------|
| Add | `va.add(vb)` | Element-wise addition |
| Multiply | `va.mul(vb)` | Element-wise multiplication |
| Fused multiply-add | `va.fma(vb, vc)` | `a * b + c` |
| Reduce sum | `va.reduceLanes(ADD)` | Sum all lanes |
| Reduce max | `va.reduceLanes(MAX)` | Max of all lanes |
| Compare | `va.compare(GE, vb)` | Lane-wise comparison |
| Blend | `va.blend(vb, mask)` | Select lanes by mask |

## 3. Masked Operations

```java
VectorMask<Float> mask = SPECIES.indexInRange(i, a.length);
FloatVector va = FloatVector.fromArray(SPECIES, a, i, mask);
FloatVector vb = FloatVector.fromArray(SPECIES, b, i, mask);
FloatVector vc = va.add(vb, mask); // only active lanes computed
```

## 4. Shape and Size

| Species Name | Bit Width | Lane Count (float) | ISA |
|-------------|-----------|-------------------|-----|
| SPECIES_PREFERRED | Auto | Auto | Best available |
| SPECIES_64 | 64 | 2 | Scalar |
| SPECIES_128 | 128 | 4 | SSE/NEON |
| SPECIES_256 | 256 | 8 | AVX2 |
| SPECIES_512 | 512 | 16 | AVX-512 |

## 5. Performance Tips

- Use `SPECIES_PREFERRED` for portability
- Ensure array length is a multiple of species length, or handle tail
- Prefer `fromArray`/`intoArray` over individual lane access
- Use `fma` over separate multiply + add for precision and speed
- Warm up the JIT before benchmarking (20k+ iterations)

## 6. Module Configuration

```java
// module-info.java
module com.example.vector {
    requires jdk.incubator.vector;
}
```

Or use `--add-modules jdk.incubator.vector` at compile/runtime.
