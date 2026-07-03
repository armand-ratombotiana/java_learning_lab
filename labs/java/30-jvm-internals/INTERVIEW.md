# Interview Questions: JVM Internals

## Q1: What is the JVM architecture?
The JVM consists of the Class Loader Subsystem (loading, linking, initialization), Runtime Data Areas (heap, stack, method area, PC registers, native stacks), Execution Engine (interpreter, JIT compilers, GC), and Native Interface (JNI).

## Q2: How does GC work in the JVM?
GC identifies unreachable objects and reclaims their memory. The GC roots are thread stacks, static fields, JNI references, and active monitors. Live objects are traced from roots. G1 divides the heap into regions and collects the regions with most garbage first.

## Q3: Explain JIT compilation.
The JVM starts by interpreting bytecode. Frequently executed (hot) methods are compiled to native code. C1 (client compiler) provides quick optimization with profiling. C2 (server compiler) provides aggressive optimization. Tiered compilation starts with interpreter, progresses through C1 tiers, and eventually reaches C2.

## Q4: What is the difference between -Xms and -Xmx?
-Xms sets the initial heap size (allocated at startup). -Xmx sets the maximum heap size (heap can grow up to this). Setting them equal avoids resizing overhead.

## Q5: How does escape analysis work?
The JIT determines if an object escapes the method or thread. Non-escaping objects can be: allocated on the stack, scalar-replaced (fields stored directly in registers/memory), or synchronized eliminated.

## Q6: What is biased locking?
Biased locking optimizes for objects locked by a single thread by biasing the object header toward that thread. After revocation threshold is exceeded, biased locking is disabled (default behavior since Java 15).

## Q7: How does the class loader hierarchy work?
Bootstrap ClassLoader (loads JDK core classes) → Platform/Extension ClassLoader (loads JDK extension classes) → Application/System ClassLoader (loads application classpath). Custom ClassLoaders can define new class loading strategies. Delegation is parent-first by default.

## Q8: What causes a full GC?
Full GC occurs when: concurrent GC fails (promotion failure), metaspace fills up, System.gc() is called, or -XX:+DisallowExplicitGC is absent. Full GC is a stop-the-world, single-threaded mark-sweep-compact operation that pauses all application threads.
