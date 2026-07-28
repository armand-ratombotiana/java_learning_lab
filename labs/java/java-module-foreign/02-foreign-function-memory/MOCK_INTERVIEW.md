# Mock Interview: FFM API

**Interviewer:** "How would you call the C `getpid()` function from Java?"

**Candidate:** "Using FFM API, it's a few lines:

```java
import java.lang.foreign.*;
import java.lang.invoke.*;

Linker linker = Linker.nativeLinker();
SymbolLookup libc = linker.defaultLookup();

MethodHandle getpid = linker.downcallHandle(
    libc.find("getpid").orElseThrow(),
    FunctionDescriptor.of(ValueLayout.JAVA_INT)
);

int pid = (int) getpid.invokeExact();
System.out.println("PID: " + pid);
```

No JNI headers, no C compilation, no native method declarations. The `Linker` handles all the ABI specifics."

**Interviewer:** "How would you pass a C struct like `struct stat`?"

**Candidate:** "I'd define a `StructLayout`:

```java
StructLayout STAT_LAYOUT = MemoryLayout.structLayout(
    ValueLayout.JAVA_LONG.withName("st_dev"),
    ValueLayout.JAVA_LONG.withName("st_ino"),
    ValueLayout.JAVA_INT.withName("st_mode"),
    // ... remaining fields
);

try (Arena arena = Arena.ofConfined()) {
    MemorySegment statBuf = arena.allocate(STAT_LAYOUT);
    MethodHandle stat = linker.downcallHandle(
        libc.find("stat").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_INT,
            ValueLayout.ADDRESS, ValueLayout.ADDRESS)
    );
    stat.invokeExact(arena.allocateFrom("/etc/hosts"), statBuf);
    long size = statBuf.get(ValueLayout.JAVA_LONG, STAT_LAYOUT.byteOffset(
        MemoryLayout.PathElement.groupElement("st_size")));
}
```

The `MemoryLayout.PathElement` API provides type-safe navigation into nested structs."
