# Deep Dive: Foreign Function & Memory API

## 1. Memory Segments and Arenas

`MemorySegment` models a contiguous region of off-heap memory. `Arena` controls its lifecycle.

```java
// Confined arena — single-thread access
try (Arena arena = Arena.ofConfined()) {
    MemorySegment segment = arena.allocate(100);
    // use segment ...
} // segment automatically freed

// Shared arena — multi-thread access
Arena shared = Arena.ofShared();
```

## 2. Memory Layouts

Describe the layout of C structs:

```java
record Point(int x, int y) {}

StructLayout POINT_LAYOUT = MemoryLayout.structLayout(
    ValueLayout.JAVA_INT.withName("x"),
    ValueLayout.JAVA_INT.withName("y")
);

// Write a Point into memory
MemorySegment segment = arena.allocate(POINT_LAYOUT);
segment.set(ValueLayout.JAVA_INT, 0, 10);   // x
segment.set(ValueLayout.JAVA_INT, 4, 20);   // y
```

## 3. Calling Foreign Functions

```java
Linker linker = Linker.nativeLinker();
SymbolLookup libc = linker.defaultLookup();

// Find & link: int clock_gettime(clockid_t, struct timespec*)
MethodHandle clock_gettime = linker.downcallHandle(
    libc.find("clock_gettime").orElseThrow(),
    FunctionDescriptor.of(ValueLayout.JAVA_INT,
        ValueLayout.JAVA_INT,
        ValueLayout.ADDRESS)
);
```

## 4. Struct Layout Example (struct timespec)

```java
StructLayout TIMESPEC_LAYOUT = MemoryLayout.structLayout(
    ValueLayout.JAVA_LONG.withName("tv_sec"),
    ValueLayout.JAVA_LONG.withName("tv_nsec")
);

try (Arena arena = Arena.ofConfined()) {
    MemorySegment ts = arena.allocate(TIMESPEC_LAYOUT);
    clock_gettime.invoke(0, ts);  // CLOCK_REALTIME = 0
    long sec  = ts.get(ValueLayout.JAVA_LONG, 0);
    long nsec = ts.get(ValueLayout.JAVA_LONG, 8);
}
```

## 5. Upcall (Java → Native → Java)

```java
// Java function to call from native
static void callback(String msg) {
    System.out.println("Callback: " + msg);
}

MethodHandle handle = MethodHandles.lookup()
    .findStatic(ForeignLab.class, "callback",
        MethodType.methodType(void.class, String.class));

MemorySegment upcallStub = linker.upcallStub(
    handle,
    FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
    arena
);
```

## 6. FFM vs JNI vs JNA

| Aspect | FFM API | JNI | JNA |
|--------|---------|-----|-----|
| Boilerplate | Low | High | Medium |
| Performance | Near-native | Native | Medium |
| Type safety | Static layouts | Manual | Dynamic |
| GC interaction | None (Arena) | None | None |
| Module support | Built-in | Manual | Manual |

## 7. Module Configuration

```java
// module-info.java
module com.example.ffm {
    requires jdk.unsupported;  // not needed — use java.lang.foreign
}
```

## 8. Best Practices

- Always use `try-with-resources` for `Arena` scopes
- Use `ValueLayout.ADDRESS_UNALIGNED` for packed structs
- Prefer `Linker.nativeLinker().defaultLookup()` over manual `dlopen`
- Validate native function descriptors at startup
- Never access a segment after its arena is closed
