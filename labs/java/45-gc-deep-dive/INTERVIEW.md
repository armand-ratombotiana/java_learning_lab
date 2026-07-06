# Interview Questions: Garbage Collection

## Beginner
1. What is garbage collection and why does Java use it?
2. Explain the difference between minor GC and major GC.
3. What is the difference between Serial, Parallel, and G1 GC?
4. What is an OutOfMemoryError and what causes it?

## Intermediate
5. Explain G1's region-based design and how it achieves pause time targets.
6. What are ZGC colored pointers and how do they enable concurrent compaction?
7. What is the role of remembered sets in G1?
8. Describe the G1 marking cycle phases.
9. What is the difference between stop-the-world and concurrent GC?

## Advanced
10. How does ZGC's load barrier work? What is the overhead?
11. Explain how G1's SATB algorithm prevents object loss during concurrent marking.
12. What is the relationship between allocation rate, promotion rate, and GC frequency?
13. How does the JVM implement root scanning for virtual threads (continuations)?
14. Compare ZGC's colored pointers with Shenandoah's Brooks pointers.
15. How do heap size and live set size affect GC pause time for different collectors?

## Expert
16. Design a GC strategy for a real-time trading system with P99 latency < 1ms.
17. How would you implement a custom GC algorithm for a specific workload pattern?
18. Explain the impact of compressed OOPs on ZGC colored pointers.
19. How does GC interact with the JIT compiler's code cache and nmethods?
20. What are the challenges of implementing GC for non-heap memory (off-heap, DirectByteBuffer)?

## Answers
Available in the SOLUTION directory.
