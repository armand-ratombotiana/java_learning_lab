# Methods — Internal Mechanics

## Method Table (vtable)

Each class has a virtual method table. Instance methods are indexed by position in the table. Invocation uses the index: `invokevirtual #12` means "call method at index 12 in the vtable."

## Invocation Instructions

| Instruction | Target | Resolution |
|-------------|--------|------------|
| `invokevirtual` | Instance method | Runtime (vtable dispatch) |
| `invokespecial` | Constructor, super, private | Compile-time |
| `invokestatic` | Static method | Compile-time |
| `invokeinterface` | Interface method | Runtime (itable dispatch) |
| `invokedynamic` | Lambda, dynamic call | Runtime (bootstrap) |

## JIT Inlining

The JIT identifies "hot" methods (frequently called) and inlines them. Thresholds:
- Default: 10,000 invocations before JIT compilation
- Min inlining size: 35 bytes of bytecode
- Max inlining depth: 9 (default)

## Recursion and Stack

Each recursive call adds a frame. Default stack size: 1MB (OS-dependent). A deep recursion of 10,000 frames × 100 bytes/frame = 1MB → StackOverflowError.

## Method Handle (Java 7+)

`java.lang.invoke.MethodHandle` provides a modern, type-safe method dispatch mechanism. Used internally by invokedynamic for lambda implementation.

## Method Area (Metaspace)

Class-level method metadata (bytecode, constant pool, method descriptors) stored in Metaspace (Java 8+), previously PermGen.
