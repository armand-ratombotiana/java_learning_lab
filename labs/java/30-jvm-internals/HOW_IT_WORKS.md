# How the JVM Works

## Class Loading Lifecycle
1. **Loading**: Find the .class file, read binary data, create Class object
2. **Verification**: Ensure bytecode is valid (no stack overflow, type-safe)
3. **Preparation**: Allocate static fields with default values
4. **Resolution**: Resolve symbolic references to direct references (optional, may be deferred)
5. **Initialization**: Execute static initializers and static field assignments

## Runtime Data Areas

### Heap
Shared among all threads. Stores all objects and arrays. Divided into:
- Young Generation (Eden + S0 + S1)
- Old Generation
- Metaspace (class metadata, not technically in heap)

### Stack
Each thread has a private stack. Stack frames contain:
- Local variables array
- Operand stack
- Frame data (constant pool resolution, exception handlers)

### PC Register
Each thread has a PC register pointing to current instruction address.

### Native Method Stack
For native methods written in C/C++.

## Execution
```
Source: MyClass.java
    → javac compiler
Bytecode: MyClass.class
    → ClassLoader loads
    → Bytecode Verifier checks
    → Interpreter runs (initially)
    → JIT compiles hot methods to native code
    → Native code executed directly
```
