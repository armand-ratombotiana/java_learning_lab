# Debugging Arrays

## Common Symptoms and Root Causes

| Symptom | Likely Cause |
|---------|-------------|
| `ArrayIndexOutOfBoundsException` | Index >= length or < 0 |
| `NullPointerException` on array access | Array reference is null, not initialized |
| Unexpected values (0, null) | Array default initialization confused with set values |
| `ConcurrentModificationException` | Modifying ArrayList while iterating |
| Array contains nulls unexpectedly | Forgot to fill after allocation or removed without shifting |

## Debugging Techniques

### Print Array Contents

```java
System.out.println(Arrays.toString(arr));        // 1D
System.out.println(Arrays.deepToString(matrix)); // 2D+
```

### Verify Indices

```java
// Before access, log the index
int index = computeIndex();
System.out.println("index=" + index + ", length=" + arr.length);
if (index < 0 || index >= arr.length) {
    throw new AssertionError("Invalid index: " + index);
}
```

### Use assert for Invariants

```java
private void ensureCapacity() {
    assert size <= elements.length : "Size > capacity: " + size + " > " + elements.length;
}
// Enable with -ea JVM flag
```

### Watch for Overflow in Index Math

```java
int mid = (low + high) / 2;  // overflow risk for large arrays
int mid = low + (high - low) / 2;  // safe
```

## Unit Testing Arrays

```java
@Test
void testArrayRotation() {
    int[] input = {1, 2, 3, 4, 5};
    int[] expected = {3, 4, 5, 1, 2};
    rotate(input, 2);
    assertArrayEquals(expected, input);
}
```
