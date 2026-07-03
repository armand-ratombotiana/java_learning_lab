# Flashcards — Virtual Threads

**Q:** What is a virtual thread?  
**A:** A lightweight thread managed by the JVM, not the OS.

**Q:** How many virtual threads can you create?  
**A:** Millions (limited by heap, not OS).

**Q:** What is a carrier thread?  
**A:** A platform thread that executes virtual threads.

**Q:** What is pinning?  
**A:** When a virtual thread cannot yield its carrier (e.g., inside synchronized).

**Q:** What is `StructuredTaskScope`?  
**A:** A structured concurrency API for scoped fork-join parallelism.

**Q:** What is a `Continuation`?  
**A:** The saved stack/frame state of a frozen virtual thread.

**Q:** How do you create a virtual thread?  
**A:** `Thread.ofVirtual().start(runnable)`

**Q:** What is the default carrier pool?  
**A:** `ForkJoinPool` with `maxPoolSize = availableProcessors`.

**Q:** When should you use platform threads over virtual?  
**A:** For CPU-bound tasks.

**Q:** What flag detects pinning?  
**A:** `-Djdk.tracePinnedThreads=full`
