# Interview Preparation: JVM Internals

This document covers advanced questions related to the JVM architecture, memory areas, ClassLoaders, and the JIT compiler.

## Q1: Explain the Parent-Delegation model in Java ClassLoading. Why is it used?
**Answer:**
When a ClassLoader is asked to load a class (e.g., `com.mycompany.User`), it does not immediately search for it. Instead, it delegates the request to its parent ClassLoader. This continues up the hierarchy until the request reaches the top (the Bootstrap ClassLoader). If the parent cannot find the class, the child ClassLoader then attempts to find and load it.
**Why:**
1.  **Security**: It prevents malicious code from overriding core Java classes. If you write a malicious class named `java.lang.String`, the Application ClassLoader will delegate to the Bootstrap ClassLoader, which will find the real `java.lang.String` first and return it, completely ignoring your malicious class.
2.  **Uniqueness**: It ensures that a class is only loaded once, preventing type conflicts (in Java, a class is identified by its fully qualified name *and* the ClassLoader that loaded it).

## Q2: What is the difference between the Heap and the Metaspace?
**Answer:**
*   **The Heap**: The memory area where all instantiated objects and arrays reside. It is the primary domain of the Garbage Collector. It is shared across all threads.
*   **The Metaspace**: (Replaced PermGen in Java 8). It is an off-heap native memory area that stores class-level metadata. When a `.class` file is loaded, its structure, method bytecode, static variables, and constant pool are stored here. If you dynamically generate too many classes at runtime (e.g., using CGLIB proxies), the Metaspace will fill up and throw an `OutOfMemoryError`.

## Q3: Why does a Java application typically start slower than a C++ application, but often matches or exceeds its performance after running for a while?
**Answer:**
C++ is an Ahead-Of-Time (AOT) compiled language. It is compiled directly to native machine code before execution.
Java uses a hybrid approach. At startup, the JVM uses an **Interpreter** to read bytecode line-by-line. This is very slow.
However, as the application runs, the JVM profiles the code. When a method is called frequently (becomes "hot"), the **Just-In-Time (JIT) Compiler** kicks in. The JIT compiles the hot bytecode into highly optimized native machine code. Because the JIT compiles at runtime, it has access to dynamic profiling data that an AOT compiler doesn't have (e.g., it knows exactly which branch of an `if` statement is taken 99% of the time). It can perform aggressive optimizations based on real-time execution patterns, eventually allowing Java to match or beat C++ performance.

## Q4: What is JIT "Deoptimization"?
**Answer:**
Because the JIT compiler uses real-time profiling, it makes optimistic assumptions to maximize performance.
For example, if it sees an interface `PaymentProcessor` and notices that only `CreditCardProcessor` has ever been loaded, it might optimize the code by removing the dynamic interface dispatch and hardcoding a direct call to the `CreditCardProcessor` method (Method Inlining).
If, an hour later, a new class `PayPalProcessor` is dynamically loaded, the JIT's assumption is broken. The JVM must perform **Deoptimization**: it throws away the highly optimized machine code, falls back to the slow Interpreter, and eventually recompiles the code to handle the new polymorphic state.

## Q5: What is a `StackOverflowError` and what typically causes it?
**Answer:**
The JVM assigns a fixed amount of memory (a Stack) to every thread. The Stack stores local variables and method call frames. Every time a method is called, a new frame is pushed onto the Stack. When the method returns, the frame is popped off.
A `StackOverflowError` occurs when the thread attempts to push a new frame onto the Stack, but the Stack's memory limit has been reached. This is almost exclusively caused by deep, infinite, or uncontrolled **recursion** in the Java code.