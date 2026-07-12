# JVM Memory Model Theory

## 💡 The Architecture of JVM Memory
When you run a Java application, the JVM requests a chunk of memory from the Operating System. It divides this memory into several distinct areas, each with a specific purpose.

### 1. The Heap (Shared Memory)
- **Purpose**: Stores all objects and arrays created at runtime (e.g., `new Object()`).
- **Scope**: Shared across all threads in the application.
- **Lifecycle**: Managed by the Garbage Collector (GC). Objects live here until they are no longer reachable.
- **Errors**: `java.lang.OutOfMemoryError: Java heap space`.

### 2. The Stack (Thread-Local Memory)
- **Purpose**: Stores local variables and method call frames.
- **Scope**: Every thread has its own private Stack. Threads cannot access each other's stacks.
- **Lifecycle**: When a method is called, a new "Frame" is pushed onto the stack. It contains the method's local primitive variables (int, double) and *references* (pointers) to objects on the Heap. When the method returns, the frame is popped off and destroyed instantly.
- **Errors**: `java.lang.StackOverflowError` (usually caused by infinite recursion).

### 3. Metaspace (formerly PermGen)
- **Purpose**: Stores class metadata, method bytecode, static variables, and the constant pool.
- **Scope**: Shared across all threads.
- **Location**: In Java 8+, Metaspace is allocated out of native OS memory, not the JVM Heap.
- **Errors**: `java.lang.OutOfMemoryError: Metaspace`.

### 4. PC (Program Counter) Register
- **Purpose**: Keeps track of the exact JVM instruction currently being executed by a thread. Every thread has its own PC Register.

## 🔄 Pass-by-Value in Java
Java is strictly **Pass-by-Value**. It is never Pass-by-Reference.
However, this confuses many developers when dealing with objects.

- If you pass a primitive (`int x = 5`) to a method, a copy of the value `5` is pushed onto the new stack frame.
- If you pass an object (`Person p = new Person()`) to a method, the object itself lives on the Heap. The variable `p` on the Stack is just a *reference* (a memory address) pointing to the Heap. When you pass `p` to a method, Java passes a *copy of the reference value*. 
Therefore, the new method can modify the object's fields on the Heap, but it cannot make the original reference point to a new object.