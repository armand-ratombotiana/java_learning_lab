# Architecture: Arrays in System Design

## Layered Architecture Role

```
┌─────────────────────────────────┐
│  Application Layer              │
│  ArrayList, POJO arrays         │
├─────────────────────────────────┤
│  Collection Layer               │
│  java.util.Arrays, Collections  │
├─────────────────────────────────┤
│  JVM Runtime                    │
│  array objects, bounds check    │
├─────────────────────────────────┤
│  OS / Memory Manager            │
│  contiguous pages, TLB          │
├─────────────────────────────────┤
│  Hardware                       │
│  cache lines, DRAM              │
└─────────────────────────────────┘
```

## Design Patterns

### Iterator Pattern
```java
public interface Iterator<E> {
    boolean hasNext();
    E next();
}
```

ArrayList's `iterator()` returns an `Itr` inner class that tracks cursor position and validates `modCount`.

### Factory Pattern
```java
int[] arr = new int[n];                 // language-level
List<String> list = Arrays.asList(a);    // factory view
List<String> copy = List.copyOf(source); // immutable copy (Java 10+)
```

### Strategy Pattern
```java
Arrays.sort(arr);          // natural order
Arrays.sort(arr, comp);    // custom Comparator
```

## Concurrency Architecture

- Arrays are **not thread-safe** — concurrent reads/writes produce race conditions
- `Collections.synchronizedList(new ArrayList<>())` wraps with synchronized blocks
- `CopyOnWriteArrayList` uses immutable array snapshots for read-optimized concurrency

## Native Interop

Java arrays can be passed to native code via JNI:

```java
// JNI: GetIntArrayElements pins the array (may copy)
JNIEXPORT void JNICALL Java_nativeMethod(JNIEnv* env, jobject obj, jintArray arr) {
    jint* elements = (*env)->GetIntArrayElements(env, arr, NULL);
    // ... use elements as C array ...
    (*env)->ReleaseIntArrayElements(env, arr, elements, 0);
}
```

## Memory Architecture

- **Compressed OOPs** (default): Object references are 32-bit shifted, enabling heaps up to ~32GB with 4-byte references
- **Card marking**: JVM tracks which array regions reference old-gen objects for GC
- **TLAB (Thread-Local Allocation Buffer)**: Small arrays may be allocated in thread-local regions for lock-free allocation
