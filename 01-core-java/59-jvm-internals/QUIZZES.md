# Quizzes: JVM Internals

Test your knowledge of ClassLoaders, JVM Memory, and JIT Compilation.

## Quiz 1: Class Loaders and Memory

**Q1: What is the "Parent-Delegation" model in Java ClassLoading?**
- A) A ClassLoader will always try to load the class itself first, and only ask its parent if it fails.
- B) A ClassLoader will always ask its parent ClassLoader to load the class first. It only attempts to load the class itself if the parent (and all ancestors) fail to find it. This prevents core Java classes (like `java.lang.String`) from being maliciously overridden by application code.
- C) A ClassLoader delegates the loading process to a background thread.
- D) A ClassLoader delegates the loading to the Garbage Collector.
*Answer: B*

**Q2: Which JVM memory area is shared across ALL threads and is used to store class structures, method definitions, and static variables?**
- A) The Stack
- B) The PC Register
- C) The Heap
- D) The Method Area (Metaspace)
*Answer: D*

## Quiz 2: The Execution Engine

**Q1: Why are Java applications often slow when they first start up, but become incredibly fast after running for a while?**
- A) Because the Garbage Collector takes time to initialize.
- B) Because the JVM initially executes bytecode using the slow Interpreter. Only after a method is called many times (becomes "hot") does the JIT Compiler kick in, compiling the bytecode into highly optimized native machine code.
- C) Because the OS limits CPU usage for new processes.
- D) Because class loading is asynchronous.
*Answer: B*

**Q2: What is "Deoptimization" in the context of the JIT compiler?**
- A) When the JIT compiler makes the code run slower intentionally to save battery.
- B) When the JIT compiler deletes machine code because the hard drive is full.
- C) When an aggressive assumption made by the JIT compiler (e.g., "this method is never overridden") is invalidated by a runtime event (e.g., a new subclass is loaded). The JVM throws away the optimized machine code and falls back to the Interpreter.
- D) When the Garbage Collector halts the application.
*Answer: C*

## Quiz 3: Edge Cases

**Q1: If you heavily use a library that generates thousands of new, unique proxy classes at runtime, which JVM error are you most likely to encounter?**
- A) `StackOverflowError`
- B) `OutOfMemoryError: Java heap space`
- C) `OutOfMemoryError: Metaspace`
- D) `ClassCastException`
*Answer: C (Class definitions and metadata are stored in the Metaspace, not the standard Heap).*