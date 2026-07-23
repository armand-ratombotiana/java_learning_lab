# Company Interview Guide — Process & Logistics for Java Engineers

> **Interview processes, timelines, coding environments, compensation levels**
> This is a logistics guide — see `ACADEMY_INTERVIEW_GUIDE.md` for technical content.

---

## Table of Contents
1. [Google](#google)
2. [Microsoft](#microsoft)
3. [Amazon](#amazon)
4. [Meta](#meta)
5. [Apple](#apple)
6. [Oracle](#oracle)
7. [Netflix](#netflix)
8. [Uber](#uber)
9. [Stripe](#stripe)
10. [Bloomberg](#bloomberg)
11. [Palantir](#palantir)

---

## Google

**Interview Process:**
| Round | Format | Duration | Java-Specific Notes |
|-------|--------|----------|---------------------|
| Phone Screen | Google Docs (shared doc) | 45 min | Must write compilable-looking Java; no syntax highlighting |
| Coding Round 1 | Google Docs | 45 min | Medium/Hard; choose Java — they expect idiomatic code |
| Coding Round 2 | Google Docs | 45 min | Often concurrency or data-structure heavy |
| System Design | Google Draw + Doc | 45 min | Java stack choices matter |
| Googleyness | Video call | 30 min | Behavioral; Java project stories help |

**Coding Environment:** Google Docs (plain text, no IDE). Practice writing Java without autocomplete. They want to see imports, method signatures, and Javadoc-style comments.

**Java Expectations:**
- You CAN choose Java. ~40% of Google candidates use Java.
- Expect deep questions on `java.util.concurrent`, JVM memory model, GC tuning.
- Google interviews the JVM hard — know `-XX` flags, G1GC/ZGC internals.

**Interview Stories:**
> *"My Google phone screen was in Google Docs. I wrote a BFS solution using `ArrayDeque`. The interviewer asked why I chose `ArrayDeque` over `LinkedList`. I explained cache locality and amortized O(1). He said 'Good, now implement it without using any java.util classes.'"* — L4 SWE
> *"For L5 system design, I proposed Spring Boot. They asked me to justify every annotation. Then they asked how Spring's dependency injection works at the bytecode level."* — L5 Staff SWE

**Compensation (US, Java Engineer):**
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| L3 (SWE II) | $130-160k | 15% | $100-200k | $170-230k |
| L4 (SWE III) | $160-200k | 15% | $200-400k | $230-350k |
| L5 (Senior) | $200-260k | 20% | $400-800k | $350-550k |
| L6 (Staff) | $250-320k | 25% | $800-1.5M | $500-900k |

---

## Microsoft

**Interview Process:**
| Round | Format | Duration | Java-Specific Notes |
|-------|--------|----------|---------------------|
| Phone Screen | Microsoft Teams + CoderPad | 45 min | Java or C#; Azure-heavy teams prefer C# |
| Coding Round 1 | CoderPad | 45 min | Medium difficulty; collections-heavy |
| Coding Round 2 | CoderPad | 45 min | Often async/reactive patterns |
| Design / Azure | Whiteboard or Diagram | 45 min | Java-Azure integration |
| Behavioral (AA) | Video call | 45 min | Leadership principles + culture fit |

**Coding Environment:** CoderPad with syntax highlighting. Supports Java 17+. You can run code. Use this to your advantage — write JUnit-like test cases inline.

**Java Expectations:**
- Language choice is flexible. Many Azure teams use Java.
- Expect Java vs C# comparison questions (even if you only know Java).
- Async patterns: `CompletableFuture` vs `async/await`.

**Interview Stories:**
> *"My Microsoft loop had 5 rounds. The first coding round was straightforward (LRU cache). The second was a reactive programming problem — they wanted Project Reactor. I used `Flux` and `Mono` and they were impressed I knew the backpressure signals."* — SDE II
> *"The hiring manager asked: 'We have a Java microservice that needs to integrate with Azure Service Bus. Walk me through the architecture.' I mentioned JMS. They said 'Why not use the Azure SDK directly?'"* — SDE II

**Compensation (US, Java Engineer):**
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| SDE I | $90-130k | 10-15% | $60-120k | $110-170k |
| SDE II | $130-180k | 15-20% | $120-250k | $180-300k |
| Senior | $180-230k | 20-30% | $250-500k | $300-450k |
| Principal | $220-300k | 30-50% | $500-1.2M | $450-900k |

---

## Amazon

**Interview Process:**
| Round | Format | Duration | Java-Specific Notes |
|-------|--------|----------|---------------------|
| OA (Online Assessment) | HackerRank | 90 min | 2 coding problems; Java 11/17 |
| Phone Screen | Chime + Live Coding | 60 min | 1 coding + LP behavioral |
| Coding Round 1 | On-site (remote) | 45 min | Usually a collection-heavy problem |
| OOD Round | Whiteboard | 45 min | Design in Java (parking lot, etc.) |
| System Design | Whiteboard | 45 min | Scale, cost-optimization with Java |
| LP Behavioral | Video call | 60 min | 3+ Leadership Principle deep-dives |

**Coding Environment:** HackerRank for OA; for on-site, you'll use Amazon's live coding tool (similar to CoderPad). No IDE features beyond basic syntax highlighting.

**Java Expectations:**
- Java is the most common language at Amazon after C++.
- They care deeply about performance — know auto-boxing costs, memory overhead, GC impact.
- Every team uses the Java Leadership Principles (Customer Obsession through optimized code, etc.).
- Know Java on AWS: Lambda cold starts, ECS vs EKS JVM tuning, Graviton ARM64.

**Interview Stories:**
> *"Amazon S3 team: I had to design a distributed rate limiter. The interviewer kept asking 'How would this look in Java? What's the memory footprint?' I mentioned `TokenBucket` using `ScheduledExecutorService`. They asked about the scheduling overhead at 100K QPS."* — SDE II
> *"The LP round: 'Tell me about a time you had to make a trade-off between performance and maintainability.' I described a Java project where I chose `Record`s over POJOs and how that impacted GC pressure. The bar raiser loved the specific Java detail."* — SDE II

**Compensation (US, Java Engineer):**
| Level | Base | Bonus (1st yr) | RSU (5yr vest) | TC Range |
|-------|------|----------------|----------------|----------|
| L4 (SDE I) | $105-140k | $20-40k | $40-120k | $130-180k |
| L5 (SDE II) | $140-200k | $30-60k | $120-300k | $200-340k |
| L6 (Senior) | $180-250k | $50-100k | $300-700k | $340-550k |
| L7 (Principal) | $225-325k | $100-200k | $700-2.5M | $550-1.2M |

---

## Meta

**Interview Process:**
| Round | Format | Duration | Java-Specific Notes |
|-------|--------|----------|---------------------|
| Phone Screen | CoderPad | 45 min | 1-2 Medium problems; Java or Python preferred |
| Coding Round 1 | CoderPad | 45 min | Algorithms + data structures |
| Coding Round 2 | CoderPad | 45 min | Often string/array manipulation |
| System Design | Excalidraw | 45 min | Real-time systems; Java concurrency matters |
| Behavioral | Video call | 45 min | Meta values + Java project experience |

**Coding Environment:** CoderPad with Java 17, supports JUnit. You can run tests. Practice writing `@Test` methods alongside your solution.

**Java Expectations:**
- Language choice is flexible. Java is well-supported.
- Meta asks about Java streams, lambdas, parallel processing.
- They like functional programming patterns in Java.
- Less JVM depth than Google; more focus on clean, correct code.

**Interview Stories:**
> *"Meta coding round: I wrote a solution using streams and `Collectors.groupingBy`. The interviewer asked me to convert it to an imperative loop. We discussed when streams are appropriate vs when they add overhead. Then they asked me to make it parallel."* — E5 SWE
> *"My Meta system design was Instagram stories. I proposed a reactive Java backend. They asked about Project Reactor's backpressure and how it maps to real-world load shedding."* — E6 Staff SWE

**Compensation (US, Java Engineer):**
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| E3 (SWE) | $110-140k | 10% | $60-120k | $130-180k |
| E4 (SWE) | $140-190k | 10% | $120-280k | $200-320k |
| E5 (Senior) | $180-240k | 15% | $280-600k | $320-500k |
| E6 (Staff) | $220-300k | 15% | $600-1.5M | $500-1,000k |

---

## Apple

**Interview Process:**
| Round | Format | Duration | Java-Specific Notes |
|-------|--------|----------|---------------------|
| Phone Screen | Shared doc or CoderPad | 45 min | 1 coding problem; Java if server-side |
| Coding Round 1 | Whiteboard (in-person) | 45 min | Focus on correctness + memory efficiency |
| Coding Round 2 | Whiteboard | 45 min | Often multi-threaded Java problem |
| Systems/Performance | Whiteboard | 45 min | JVM tuning, memory layout, GC |
| Behavioral | Video call | 30 min | Cross-functional collaboration stories |

**Coding Environment:** In-person whiteboarding is common. For remote: they use a simple shared doc. No IDE, no autocomplete. Apple favors handwriting-style coding.

**Java Expectations:**
- Language choice depends on team. Server-side Java is common (Siri, iCloud, services).
- Apple cares deeply about memory efficiency — know object headers, alignment, off-heap.
- ARM64 (Apple Silicon) optimization questions are unique to Apple.
- JNI interop with Swift/Objective-C is a common discussion point.

**Interview Stories:**
> *"Apple Siri team: They asked me to implement a thread-safe event accumulator with time-based flushing. Each time I added a Java util, they asked about its memory overhead. 'You used `ConcurrentLinkedDeque` — what's its memory per node? How does its iterator work under concurrent modification?'"* — SWE
> *"My performance round: Given a 64GB heap running on M2 Ultra, which GC would I choose for a real-time audio processing pipeline? We spent 30 minutes on ZGC vs Shenandoah tradeoffs."* — Senior SWE

**Compensation (US, Java Engineer):**
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| ICT2 | $110-145k | 5-10% | $50-120k | $130-180k |
| ICT3 | $145-190k | 10% | $120-280k | $190-320k |
| ICT4 (Senior) | $180-240k | 10-15% | $280-600k | $300-500k |
| ICT5 (Staff) | $230-300k | 15-20% | $600-1.5M | $500-900k |

---

## Oracle

**Interview Process:**
| Round | Format | Duration | Java-Specific Notes |
|-------|--------|----------|---------------------|
| Recruiter Screen | Phone | 30 min | Java background check + leveling |
| Technical Phone | Zoom + Shared Screen | 60 min | JVM internals + coding in Java |
| Coding Round | Oracle's Live Coding | 45 min | Algorithms + Java language features |
| JVM Deep-Dive | Video call | 60 min | Class loading, bytecode, JIT, GC |
| System Design | Video call | 45 min | Enterprise Java + cloud architecture |
| Hiring Manager | Video call | 45 min | Java community involvement, OSS contributions |

**Coding Environment:** Oracle uses a custom live coding platform. Supports Java with Maven/Gradle. You can compile and run.

**Java Expectations:**
- You WILL be asked about JVM internals in extreme detail (bytecode, class loading, JIT compilation phases).
- Oracle owns Java — they expect you to know JEPs, release cadence, LTS strategy.
- If interviewing for the Java Platform Group: expect bytecode-level questions, JLS parsing, JMM model.
- For Oracle Cloud (OCI): Spring Boot, Helidon, Micronaut — all fair game.

**Interview Stories:**
> *"Oracle Java Platform Group: The entire interview was about `HashMap`. How does `hashCode()` work? What's the murmur hash? How do tree bins work? What's the memory layout of a TreeNode? What happens during resize with concurrent access? 45 minutes on one class."* — Member of Technical Staff
> *"OCI interview: They asked me to design a distributed object storage system. I proposed Java NIO with `MappedByteBuffer`. The interviewer asked about the JVM crash recovery implications of memory-mapped files."* — Senior Cloud Engineer

**Compensation (US, Java Engineer):**
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| IC2 | $100-130k | 5-10% | $30-80k | $110-160k |
| IC3 | $130-170k | 10-15% | $80-200k | $170-280k |
| IC4 (Senior) | $170-220k | 15-20% | $200-500k | $280-450k |
| IC5 (Principal) | $220-300k | 20-30% | $500-1.2M | $450-900k |

---

## Netflix

**Interview Process:**
| Round | Format | Duration | Java-Specific Notes |
|-------|--------|----------|---------------------|
| Recruiter Screen | Phone | 30 min | Culture fit + Java experience assessment |
| Technical Phone | CoderPad | 60 min | 1-2 Java coding problems |
| Coding Round | CoderPad | 45 min | Algorithm + concurrency in Java |
| System Design | Miro / Excalidraw | 60 min | High-scale content delivery; Java reactive |
| Behavioral "Keeper" | Video call | 60 min | Freedom & Responsibility; Java-specific stories |
| Panel | Video call | 45 min | Cross-functional; includes Java architecture |

**Coding Environment:** CoderPad with Java 17/21 support. Code must be production-quality.

**Java Expectations:**
- Netflix is a heavy Java shop (Zuul, Hystrix, Eureka are Java).
- They expect microservice patterns in Java: Spring Boot, Cloud, Resilience4j.
- Reactive programming with Project Reactor/WebFlux is common.
- They value clean, testable, production-ready code even in interviews.

**Interview Stories:**
> *"Netflix system design: Design the recommendation API. I proposed Spring Boot with WebFlux. They asked how I'd handle backpressure from downstream services. I discussed Project Reactor's `onBackpressureDrop` vs `onBackpressureBuffer` and when to use each."* — Senior SWE
> *"The 'Keeper Test' round: Tell me about a Java project where you made a significant performance improvement. I described reducing GC pauses from 500ms to 50ms by switching from G1GC to ZGC on a latency-critical service."* — Senior SWE

**Compensation (US, Java Engineer):**
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| SWE | $140-220k | None (top-of-market) | $200-600k | $350-600k |
| Senior | $200-300k | None | $600-1.5M | $550-1,000k |
| Staff+ | $250-400k | None | $1.5-4M | $1,000-2,500k |

Netflix pays top-of-market (no bonus, all in RSU + base). Compensation is highly negotiable.

---

## Uber

**Interview Process:**
| Round | Format | Duration | Java-Specific Notes |
|-------|--------|----------|---------------------|
| OA (Online) | HackerRank | 60 min | 2 coding problems in Java |
| Technical Screen | CodeSignal | 60 min | Live coding with interviewer |
| Coding Round 1 | CoderPad-like | 45 min | Algorithms + data structures |
| Coding Round 2 | CoderPad-like | 45 min | Usually concurrency or distributed |
| System Design | Whiteboard/Excalidraw | 60 min | Real-time systems; CQRS, event-driven |
| Behavioral | Video call | 45 min | Uber values + Java project depth |

**Coding Environment:** HackerRank for OA; for on-site, Uber uses a custom CoderPad-like tool with Java 17.

**Java Expectations:**
- Uber's backend is primarily Java (Cadence workflow engine, distributed systems).
- Expect questions on distributed Java patterns: leader election, distributed locks (ZooKeeper/Curator).
- Event-driven architecture with Apache Kafka (Java clients) is common.
- Performance and latency matter at Uber scale — know your way around JVM profiling.

**Interview Stories:**
> *"Uber coding round: Implement a distributed rate limiter that works across multiple JVMs. I used Redis + Redisson. They asked about the performance impact of every Redis call and how I'd optimize with local token buckets."* — SWE II
> *"System design: Design Uber Eats order matching. I started with a Java monolith. They said 'Okay, now make it handle 10x load.' Eventually we ended up with an event-driven architecture using Kafka streams + state stores in Java."* — Senior SWE

**Compensation (US, Java Engineer):**
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| SWE I | $120-150k | 10-15% | $60-150k | $150-220k |
| SWE II | $150-200k | 15-20% | $150-350k | $220-380k |
| Senior | $190-260k | 20-25% | $350-750k | $350-600k |
| Staff+ | $240-330k | 25-30% | $750-2M | $600-1,200k |

---

## Stripe

**Interview Process:**
| Round | Format | Duration | Java-Specific Notes |
|-------|--------|----------|---------------------|
| Recruiter Screen | Phone | 30 min | Experience + Java background check |
| Coding Round 1 | Stripe's IDE (CoderPad) | 60 min | 1-2 Medium problems; idiomatic Java expected |
| Coding Round 2 | Stripe's IDE | 60 min | API design + implementation in Java |
| System Design | Excalidraw | 60 min | Payments, idempotency, distributed tx |
| Debugging Round | Stripe's Debugging Env | 60 min | Find and fix bugs in a Java codebase |
| Behavioral | Video call | 45 min | Stripe values + technical leadership |

**Coding Environment:** Stripe uses a modified CoderPad that simulates their API style. You may be asked to implement a Stripe-like API in Java. Debugging round uses a real Java project.

**Java Expectations:**
- Stripe values correctness heavily: immutability, thread safety, error handling.
- Know Java records, sealed classes, pattern matching (Stripe uses modern Java).
- Idempotency keys: discuss how to implement in Java (ConcurrentHashMap, Redis).
- Payment systems: they test handling of edge cases (nulls, errors, timeouts) in Java.

**Interview Stories:**
> *"Stripe debugging round: They gave me a broken Java service that processes payments. There was a race condition in a `ConcurrentHashMap` usage, a thread leak in an `ExecutorService`, and a subtle `int` overflow bug. I had 60 minutes to find and fix all three."* — Backend SWE
> *"Stripe coding: 'Design and implement a rate limiter for our API — but it must be idempotent and exactly-once.' I used Redis + Lua scripting via Jedis. They asked about the Java Redis client tradeoffs (Jedis vs Lettuce vs Redisson)."* — SWE II

**Compensation (US, Java Engineer):**
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| SWE I | $120-150k | 5-10% | $80-180k | $160-250k |
| SWE II | $150-200k | 10-15% | $180-400k | $250-420k |
| Senior | $200-280k | 15-20% | $400-800k | $400-700k |
| Staff+ | $260-360k | 20-30% | $800-2.5M | $700-1,500k |

---

## Bloomberg

**Interview Process:**
| Round | Format | Duration | Java-Specific Notes |
|-------|--------|----------|---------------------|
| Phone Screen | HackerRank CodePair | 60 min | 2 coding problems in Java |
| On-site Coding | Whiteboard | 45 min each (x2) | Data structures + algorithms |
| On-site Design | Whiteboard | 60 min | Low-level + high-level design |
| On-site Data Structures | Whiteboard | 45 min | Build a data structure from scratch |
| Technical Chat | Whiteboard | 45 min | Java experience, past projects |
| Hiring Manager | In-person | 30 min | Culture fit + terminal/Python expectations |

**Coding Environment:** HackerRank CodePair for phone; whiteboard for on-site. Bloomberg is old-school — expect paper/writing on a board.

**Java Expectations:**
- Bloomberg uses Java extensively for backend services.
- They ask about data structure implementations from scratch (HashMap, ArrayList, TreeMap).
- Financial domain knowledge helps but isn't required.
- Concurrency is BIG: they have multi-threaded systems that process market data.
- They like to see you write code that could pass a Bloomberg code review.

**Interview Stories:**
> *"Bloomberg on-site: I had to implement a thread-safe order book in Java. They wanted me to handle concurrent adds/cancels/executions. I used `ConcurrentSkipListMap` for price levels. They asked why not `TreeMap` with `synchronized`. I explained the lock contention trade-offs."* — Senior SWE
> *"Bloomberg data structures round: 'Implement a hash table from scratch in Java. Handle collisions with both chaining and open addressing. Then make it thread-safe.' 45 minutes on one whiteboard."* — SWE

**Compensation (US, Java Engineer):**
| Level | Base | Bonus | TC Range |
|-------|------|-------|----------|
| SWE | $110-150k | $10-40k | $130-190k |
| Senior | $150-220k | $30-80k | $190-320k |
| Team Lead | $200-280k | $50-150k | $280-450k |

Bloomberg comp is lower than FAANG but has strong stability, great WLB, and no RSUs (cash bonus instead).

---

## Palantir

**Interview Process:**
| Round | Format | Duration | Java-Specific Notes |
|-------|--------|----------|---------------------|
| OA (Online) | CodeSignal | 75 min | 4 problems; Java acceptable |
| Phone Screen | Shared IDE | 60 min | 1-2 coding problems in Java |
| On-site Coding | Whiteboard | 45 min each (x2) | Heavy algorithmic + design |
| On-site Decomposition | Whiteboard | 60 min | Break down a complex problem |
| On-site Go / Tell Me | Whiteboard | 45 min | System design + project deep-dive |
| Behavioral | Whiteboard | 30 min | Palantir values + mission alignment |

**Coding Environment:** CodeSignal for OA (Java 17 supported). On-site is whiteboard with a heavy emphasis on correctness.

**Java Expectations:**
- Palantir's backend is Java-heavy (Ontology API, data pipelines).
- They ask about serialization (Protobuf, Avro, Kryo) — Palantir deals with massive data.
- Distributed systems in Java: ZooKeeper, Cassandra, Hadoop ecosystem.
- They care about clean, modular, well-tested Java code.
- Know how to write Java that handles failure gracefully.

**Interview Stories:**
> *"Palantir decomposition round: 'Here's a Java microservice that ingests geolocation data from military vehicles. It's crashing in production. Find the bug.' The issue was a `HashMap` being used without synchronization in a multi-threaded context, causing an infinite loop during resize."* — Forward Deployed SWE
> *"Palantir system design: Design a data pipeline that processes 10TB of CSV data daily in Java. I proposed using `FileChannel` + `MappedByteBuffer` for I/O, `ForkJoinPool` for parallel processing, and Protobuf for serialization."* — SWE

**Compensation (US, Java Engineer):**
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| SWE | $110-150k | $10-30k | $100-250k | $150-280k |
| Senior | $160-240k | $20-60k | $250-600k | $300-550k |
| Staff+ | $220-320k | $30-100k | $600-2M | $550-1,500k |

---

## Appendix: Company Comparison Matrix

| Attribute | Google | Microsoft | Amazon | Meta | Apple | Oracle | Netflix | Uber | Stripe | Bloomberg | Palantir |
|-----------|--------|-----------|--------|------|-------|--------|---------|------|--------|-----------|----------|
| Allows Java | Yes | Yes (or C#) | Yes | Yes | Yes | Yes | Yes | Yes | Yes | Yes | Yes |
| JVM Depth | Very Deep | Moderate | Deep | Low | Deep | Extreme | Moderate | Moderate | Moderate | Deep | Deep |
| Coding Env | Google Docs | CoderPad | HackerRank | CoderPad | Whiteboard | Live IDE | CoderPad | CodeSignal | Stripe IDE | CodePair | CodeSignal |
| System Design | Heavy | Moderate | Heavy | Heavy | Moderate | Moderate | Heavy | Heavy | Heavy | Moderate | Heavy |
| Concurrency | Very Heavy | Heavy | Heavy | Moderate | Heavy | Heavy | Heavy | Very Heavy | Moderate | Very Heavy | Heavy |
| Avg Timeline | 4-6 wks | 3-5 wks | 5-8 wks | 3-5 wks | 4-8 wks | 4-6 wks | 3-5 wks | 4-6 wks | 4-6 wks | 3-5 wks | 4-8 wks |
| Std. Relocation | Yes | Yes | Yes | Yes | Yes | Yes | Yes | Yes | Yes | Yes | Yes |

---

## Java Preparation Priority by Company

```
Google:     JVM Internals > Concurrency > Collections > Streams > Algorithms
Oracle:     JVM Internals > Bytecode > GC > JMM > Java Language Spec
Amazon:     Collections > Concurrency > Performance > AWS Java > OOD
Netflix:    Reactive Java > Microservices > Performance > GC Tuning
Apple:      Memory Efficiency > Multi-threading > Off-heap > ARM64 JVM
Meta:       Algorithms > Streams > Functional Java > Clean Code
Uber:       Distributed Java > Kafka > Concurrency > CQRS/Event Sourcing
Stripe:     Correctness > API Design > Thread Safety > Modern Java Features
Microsoft:  Async Java > Azure SDK > Collections > Java vs C# Semantics
Bloomberg:  Data Structures > Concurrency > Low-latency Java > Collections
Palantir:   Distributed Java > Serialization > Fault Tolerance > Data Pipelines
```

---

## Timeline Expectations by Seniority

| Level | Prep Time | Coding Focus | System Design | Java Depth |
|-------|-----------|--------------|---------------|------------|
| Junior (0-2 yr) | 4-8 weeks | LeetCode Easy/Medium | Not expected | Core Java, Collections |
| Mid (2-5 yr) | 8-12 weeks | LeetCode Medium | Basic | + Concurrency, Streams |
| Senior (5-8 yr) | 12-16 weeks | LeetCode Medium/Hard | Strong | + JVM, GC, Reactive |
| Staff (8-12 yr) | 16-24 weeks | LeetCode Hard | Expert | + Architecture, JIT |
| Principal (12+ yr) | 24+ weeks | Architecture-focused | Deep expertise | + Distributed Systems |

---

## Logistics Checklist Before Each Interview

- [ ] Confirm coding environment (Docs vs CoderPad vs whiteboard)
- [ ] Know which Java version is available (most use 17 or 21 now)
- [ ] Practice writing Java without autocomplete (especially Google Docs)
- [ ] Have your JUnit/assert snippets memorized for runnable environments
- [ ] Verify timezone for every round
- [ ] Ask about code collaboration — is it shared screen or a link?
- [ ] For system design: prepare to draw Java-specific architecture diagrams
- [ ] For behavioral: prepare 3-5 Java-specific STAR stories (memory leak, concurrency bug, GC tuning, framework migration, performance optimization)
