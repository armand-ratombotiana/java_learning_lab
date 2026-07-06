# Interview Questions: JVM Tuning

## Beginner
1. What is the difference between -Xms and -Xmx?
2. How does young generation size affect GC behavior?
3. What is JVM code cache and why is it important?
4. What is Metaspace and how is it different from the heap?

## Intermediate
5. Explain how to choose between G1, ZGC, and Parallel GC for different workloads.
6. What is the impact of -XX:CompileThreshold on performance?
7. How do large pages work and when should they be used?
8. What is NUMA-aware allocation and why does it matter?
9. How does string deduplication reduce memory usage?

## Advanced
10. How would you tune a 32 GB heap for a latency-sensitive web application?
11. What is the relationship between allocation rate, survivor spaces, and promotion?
12. Explain how code cache management interacts with JIT compilation tiers.
13. How does Metaspace chunk allocation work (buddy system)?
14. What is the impact of compressed OOPs on heap sizing?
15. How would you diagnose and fix a Metaspace memory leak?

## Expert
16. Design a JVM tuning strategy for a serverless Java function (cold start optimization).
17. How does CDS (Class Data Sharing) improve startup time and how would you implement it?
18. Explain the interaction between JVM tuning flags and container memory limits (cgroups).
19. How would you tune the JVM for a data-intensive Spark job processing 200 GB of data?
20. What are the tradeoffs between tiered compilation disabled vs enabled for different deployment scenarios?

## Answers
Available in the SOLUTION directory.
