# How Arrays Work

## Memory Layout

A Java `int[]` with 4 elements:

```
Heap:
┌──────────┬──────┬──────┬──────┬──────┐
│  header  │ [0]  │ [1]  │ [2]  │ [3]  │
│ (12-16b) │  4B  │  4B  │  4B  │  4B  │
└──────────┴──────┴──────┴──────┴──────┘
↑
arr reference points here
```

The JVM header stores: class pointer, monitor info, and the array length.

## Access Mechanism

```java
int x = arr[2];
```

Compiles to JVM bytecode:
```
aload_1       // load array reference
iconst_2      // push index 2
iaload        // load int from array
```

`iaload` performs: check `arr != null`, check `0 <= index < arr.length`, compute `base + 4 + index * 4`, read 4 bytes.

## Static Array Creation

```java
int[] arr = new int[4];
```

1. Allocate heap memory: header + 4 × 4 = ~28 bytes (aligned to 32)
2. Zero-initialize all elements (0 for primitives, null for objects)
3. Return reference

## Dynamic Array Resizing (ArrayList simulation)

```java
// Internal method called when size == capacity
void grow(int minCapacity) {
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);  // 1.5x growth
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

`Arrays.copyOf` calls `System.arraycopy` which is a JVM intrinsic using `memmove` for maximum speed.
