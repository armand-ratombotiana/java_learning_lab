# Module 15: JVM Internals - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-14  
**Estimated Reading Time**: 60-75 minutes  

---

## 📚 Table of Contents

1. [JVM Architecture Overview](#overview)
2. [Class Loader Subsystem](#classloader)
3. [Runtime Data Areas](#memory)
4. [Execution Engine](#execution)
5. [Garbage Collection](#gc)

---

## 1. JVM Architecture Overview <a name="overview"></a>
The Java Virtual Machine (JVM) acts as a runtime engine to execute Java applications. It converts Java bytecode into machine-specific code.

---

## 2. Class Loader Subsystem <a name="classloader"></a>
Class loaders load classes into memory when required.
- **Bootstrap ClassLoader**: Loads core java APIs (`rt.jar` / `java.base`).
- **Extension/Platform ClassLoader**: Loads classes from the extension directories.
- **Application/System ClassLoader**: Loads classes from the application classpath.

---

## 3. Runtime Data Areas (Memory) <a name="memory"></a>
- **Method Area**: Stores class structures, constant pool, and method data.
- **Heap**: Stores objects and arrays. Managed by the Garbage Collector.
- **Stack**: Stores frames containing local variables and partial results per thread.
- **PC Register**: Contains the address of the currently executing instruction.
- **Native Method Stack**: Stores state for native methods (C/C++).

---

## 4. Execution Engine <a name="execution"></a>
- **Interpreter**: Reads bytecode stream and executes instructions sequentially.
- **JIT Compiler**: Compiles frequently executed "hot" bytecode into native machine code to improve performance.
- **Garbage Collector**: Reclaims unused memory.

---

## 5. Garbage Collection <a name="gc"></a>
Objects are allocated in the heap. The GC cleans up unreferenced objects automatically.
Modern JVMs use collectors like:
- G1 GC (Garbage-First)
- ZGC (Z Garbage Collector)
- Shenandoah GC