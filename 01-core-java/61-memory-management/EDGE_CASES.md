# Edge Cases & Pitfalls: Memory Management

Memory leaks in Java do not look like memory leaks in C++. In Java, a memory leak is an *unintentional strong reference* that prevents the Garbage Collector from doing its job.

## 1. The `ThreadLocal` Classloader Leak
*   **The Scenario**: You deploy a web application to Tomcat. You use a `ThreadLocal` to store user session data. You undeploy and redeploy the application multiple times without restarting Tomcat.
*   **The Pitfall**: Application servers pool threads. If your application sets a `ThreadLocal` variable but fails to call `remove()` at the end of the HTTP request, the thread (which belongs to Tomcat) holds a strong reference to your application's object forever. Because the object references its Class, and the Class references the application's `ClassLoader`, the *entire application* cannot be garbage collected when undeployed. After a few redeploys, Tomcat crashes with `OutOfMemoryError: Metaspace`.
*   **Mitigation**: Always call `ThreadLocal.remove()` in a `finally` block, or use Java 21 `ScopedValue`s which are automatically cleaned up.

## 2. Lapsed Listeners (The Observer Pattern Leak)
*   **The Scenario**: You have a long-lived `EventBus` or `UIComponent`. Short-lived objects register themselves as listeners: `eventBus.register(this)`.
*   **The Pitfall**: When the short-lived object is no longer needed, developers often forget to call `eventBus.unregister(this)`. Because the `EventBus` maintains a `List<Listener>` (strong references), the short-lived objects are kept alive forever, slowly consuming all heap memory.
*   **Mitigation**: 
    1. Always explicitly unregister listeners.
    2. Implement the EventBus using `WeakReference`s for its listeners, so if the listener is only referenced by the EventBus, it will be automatically garbage collected.

## 3. Premature Promotion
*   **The Scenario**: You have an application that generates a massive number of medium-lived objects (e.g., a large XML payload being parsed over a few seconds).
*   **The Pitfall**: The Young Generation (Eden + Survivors) is sized too small. When a Minor GC occurs, the medium-lived objects are still in use, so they survive. Because the Survivor spaces are too small to hold them, the JVM is forced to promote them directly to the Old Generation (Premature Promotion). The Old Generation fills up quickly with objects that will die a second later, triggering frequent, stop-the-world Major GCs, devastating application throughput.
*   **Mitigation**: Tune the JVM arguments. Increase the size of the Young Generation (`-Xmn`) or adjust the Survivor Ratio (`-XX:SurvivorRatio`) to ensure medium-lived objects have enough time to die in the Young Generation before being promoted.

## 4. Defeating Escape Analysis
*   **The Scenario**: You write clean, locally scoped methods to take advantage of Escape Analysis (Scalar Replacement) to avoid heap allocation.
*   **The Pitfall**: You log the object: `log.debug("Created: " + myLocalObj)`. Or you pass the object to a method that isn't inlined by the JIT compiler.
*   **The Consequence**: The moment the object's reference is passed to an external method (like a logger), the JIT compiler can no longer guarantee that the object won't escape the current thread. Escape Analysis fails, Scalar Replacement is aborted, and the object is allocated on the heap, generating garbage.
*   **Mitigation**: Be extremely careful with logging and method boundaries in ultra-low-latency, zero-allocation code paths.

## 5. The `finalize()` Resurrection Trap
*   **The Scenario**: You override the `finalize()` method to clean up native resources.
*   **The Pitfall**: `finalize()` is fundamentally broken (and deprecated in Java 9). When an object becomes unreachable, the GC places it in a finalization queue. A low-priority background thread runs `finalize()`. Inside `finalize()`, a developer could accidentally assign `this` to a static global variable.
*   **The Consequence**: The object has "resurrected" itself! It is strongly reachable again. The GC must abort the collection. This delays memory reclamation, causes unpredictable pauses, and breaks JVM invariants.
*   **Mitigation**: Never use `finalize()`. Use `try-with-resources` (AutoCloseable) or `java.lang.ref.Cleaner` (Phantom References) for resource cleanup.