# Theory: JVM Internals

## JVM Architecture
The JVM is a specification implemented by HotSpot (Oracle), OpenJ9 (Eclipse), GraalVM, etc. It consists of:

- **Class Loader Subsystem**: Loads, links, and initializes classes
- **Runtime Data Areas**: Heap, Stack, Method Area, PC Registers, Native Method Stacks
- **Execution Engine**: Interpreter, JIT compilers (C1, C2), garbage collector
- **Native Interface**: JNI, native method libraries

## Bytecode Execution
Java source code is compiled to bytecode (.class files). The execution engine:
1. Interprets bytecode initially (fast startup)
2. Profiles hot methods (method invocation count, loop backedge count)
3. Compiles hot methods to native code (C1 for quick optimization, C2 for aggressive optimization)
4. May deoptimize (revert to interpreted) if assumptions change

## Memory Model (JSR 133)
The Java Memory Model defines:
- Happens-before relationships
- Volatile semantics
- Synchronized blocks
- Final field guarantees
- Thread safety visibility rules

## Garbage Collection
GC reclaims memory by identifying live objects (roots: thread stacks, static fields, JNI references). Mark-and-sweep, copying, and concurrent algorithms are used.
