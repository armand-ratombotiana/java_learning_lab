# Java Object Layout & Memory Internals — Reflection Prompts

## Section 1: Knowledge Check

1. Without looking at any reference, list the key Java object layout and memory internals mechanisms available in Java 21. How many did you remember?

2. Explain the difference between structured and unstructured concurrency to a fellow developer. What are the key distinctions?

3. Describe the Java object header structure from memory. What fields does it contain and what is their purpose?

4. List the tools you would use to diagnose a performance issue in production. Which tool is appropriate for which scenario?

## Section 2: Deep Understanding

5. Why does the Java platform provide multiple APIs for similar purposes? When would you choose one over another?

6. How does escape analysis affect object allocation in the JVM? What are the limitations of this optimization?

7. What is the relationship between TLABs and GC pause times? How does TLAB sizing affect performance?

8. Consider the trade-offs between DirectByteBuffer, Unsafe, and MemorySegment. When is each appropriate?

## Section 3: Practical Application

9. Look at a concurrent application you have written. Where could structured concurrency improve correctness or readability?

10. Profile an application and identify performance bottlenecks. What Java object layout and memory internals techniques could address them?

11. Review your application's thread pool configuration. Are the settings appropriate for your workload?

12. Examine your usage of ThreadLocal. Are there any potential leak patterns?

## Section 4: Self-Assessment

13. Rate your understanding of each Java object layout and memory internals area from 1 (struggling) to 5 (expert):
    - Profiling and observability
    - Structured concurrency
    - Off-heap memory
    - Object layout and memory internals
    - Advanced concurrency patterns
    - Performance antipatterns

14. What aspect of Java object layout and memory internals do you find most challenging? Why? What would help clarify it?

15. Have you encountered a production issue related to Java object layout and memory internals? What was the root cause and how was it resolved?