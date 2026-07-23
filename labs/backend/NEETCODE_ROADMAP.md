# Backend Academy — NeetCode Roadmap

<div align="center">

![NeetCode](https://img.shields.io/badge/NeetCode_150-FF6F00?style=for-the-badge)
![Blind 75](https://img.shields.io/badge/Blind_75-4285F4?style=for-the-badge)
![Grind 75](https://img.shields.io/badge/Grind_75-6DB33F?style=for-the-badge)

**Map backend-relevant content from NeetCode 150, Blind 75, and Grind 75 to academy labs**

</div>

---

## Table of Contents

1. [Problem-to-Lab Mapping](#1-problem-to-lab-mapping)
2. [Java-Specific Backend APIs in LeetCode](#2-java-specific-backend-apis-in-leetcode)
3. [Backend Engineer's LeetCode Study Plan](#3-backend-engineers-leetcode-study-plan)
4. [Coding Problems vs Backend Design Problems](#4-coding-problems-vs-backend-design-problems)
5. [Weekly Study Plan (12 Weeks)](#5-weekly-study-plan-12-weeks)

---

## 1. Problem-to-Lab Mapping

### Concurrency & Multi-threading → Lab 15 (WebFlux/Reactive)

| Problem | Source | Type | Backend Connection |
|---------|--------|------|-------------------|
| Print in Order (1114) | NeetCode 150 | Concurrency | Thread coordination in async processing |
| Print FooBar Alternately (1115) | NeetCode 150 | Concurrency | Thread scheduling for batch jobs |
| Print Zero Even Odd (1116) | NeetCode 150 | Concurrency | Coordinated multi-stage pipelines |
| Fizz Buzz Multithreaded (1195) | NeetCode 150 | Concurrency | Thread pool task distribution |
| Dining Philosophers (1226) | NeetCode 150 | Concurrency | Deadlock prevention in distributed locks |
| Design Bounded Blocking Queue (1188) | NeetCode 150 | Concurrency | Message queue, Kafka consumer groups |
| Web Crawler Multithreaded (1242) | NeetCode 150 | Concurrency | Parallel API requests, async fetching |

### Caching & In-Memory Data Stores → Lab 13 (Caching)

| Problem | Source | Type | Backend Connection |
|---------|--------|------|-------------------|
| LRU Cache (146) | NeetCode 150, Blind 75 | Design | Redis eviction policy, HTTP cache |
| LFU Cache (460) | NeetCode 150 | Design | Cache frequency-based eviction |
| Insert Delete GetRandom O(1) (380) | NeetCode 150 | Design | In-memory data store (Redis) |
| Time Based Key-Value Store (981) | NeetCode 150 | Design | Time-series DB, versioned cache |
| Design HashMap (706) | NeetCode 150, Blind 75 | Design | Database hash index |

### API & Data Model Design → Lab 02 (REST APIs), Lab 04 (Spring Data JPA)

| Problem | Source | Type | Backend Connection |
|---------|--------|------|-------------------|
| Design Twitter (355) | NeetCode 150 | Design | Timeline API, fanout, social graph |
| Design Parking System (1603) | NeetCode 150 | Design | Resource allocation API |
| Range Sum Query 2D (304) | NeetCode 150 | Prefix Sum | Precomputed aggregation (reporting) |
| Encode and Decode Strings (271) | NeetCode 150 | Design | Serialization (Protobuf, Avro) |

### Data Processing & Streams → Lab 18 (File/Batch Processing), Lab 24 (Backend Performance)

| Problem | Source | Type | Backend Connection |
|---------|--------|------|-------------------|
| Find Median from Data Stream (295) | NeetCode 150, Blind 75 | Heap | Real-time analytics, sliding window |
| Sliding Window Maximum (239) | NeetCode 150, Blind 75 | Queue | Stream processing, Kafka windowing |
| Top K Frequent Elements (347) | NeetCode 150, Blind 75 | Heap | Trending topics, log analysis |
| Top K Frequent Words (692) | NeetCode 150 | Heap | Search autocomplete, popularity ranking |
| Merge Intervals (56) | NeetCode 150, Blind 75 | Intervals | Batch scheduling, job merging |
| Meeting Rooms II (253) | NeetCode 150 | Intervals | Resource allocation, connection pooling |

### Graph & Distributed Systems → Lab 16 (Spring Cloud), Lab 23 (CQRS/Axon)

| Problem | Source | Type | Backend Connection |
|---------|--------|------|-------------------|
| Number of Connected Components (323) | NeetCode 150 | Graph | Service discovery, cluster membership |
| Course Schedule (207) | NeetCode 150, Blind 75 | Graph | Dependency resolution, build pipelines |
| Alien Dictionary (269) | NeetCode 150 | Graph | Schema evolution, version compatibility |
| Clone Graph (133) | NeetCode 150, Blind 75 | Graph | Service replication, proxy pattern |

### Trie & Autocomplete → Lab 22 (GraphQL DGS)

| Problem | Source | Type | Backend Connection |
|---------|--------|------|-------------------|
| Implement Trie (208) | NeetCode 150, Blind 75 | Trie | Search autocomplete, routing tables |
| Design Search Autocomplete (642) | NeetCode 150 | Trie | GraphQL suggestion, API auto-complete |
| Word Search II (212) | NeetCode 150, Blind 75 | Trie | Pattern matching in stream processing |

### SQL & Database → Lab 04 (Spring Data JPA)

| Problem | Source | Type | Backend Connection |
|---------|--------|------|-------------------|
| Combine Two Tables (175) | LeetCode | SQL | JOIN relationships in JPA |
| Second Highest Salary (176) | LeetCode | SQL | Window functions, ranking |
| Department Top 3 Salaries (185) | LeetCode | SQL | Complex window functions |
| Consecutive Numbers (180) | LeetCode | SQL | Event sequence detection |
| Game Play Analysis III (534) | LeetCode | SQL | Cumulative window aggregation |

---

## 2. Java-Specific Backend APIs in LeetCode

### Spring Boot Patterns Represented in Java LeetCode Solutions

| LeetCode Problem | Java API Used | Spring Boot Equivalent |
|-----------------|--------------|----------------------|
| 146 (LRU Cache) | `LinkedHashMap` with `removeEldestEntry()` | `@Cacheable`, Redis Cache Manager |
| 355 (Design Twitter) | `HashMap` + `PriorityQueue` | Timeline service, feed aggregation |
| 622 (Design Circular Queue) | Array + atomic indices | Kafka ring buffer, Reactor `Sinks` |
| 706 (Design HashMap) | `LinkedList[]` buckets | Hibernate `@CollectionTable` |
| 1188 (Bounded Blocking Queue) | `ReentrantLock`, `Condition` | Spring Integration `QueueChannel` |
| 1242 (Web Crawler) | `CompletableFuture`, `ExecutorService` | Spring `@Async`, WebClient |
| 460 (LFU Cache) | `TreeMap` + `HashMap` | Redis LFU, `@Cacheable` with eviction |
| 895 (Maximum Frequency Stack) | `HashMap` + `Stack[]` | Cache frequency tracking |
| 981 (Time Map) | `TreeMap` per key | Time-series DB, audit tables |
| 1603 (Parking System) | Atomic counters | Connection pool management |
| 380 (Insert Delete GetRandom) | `ArrayList` + `HashMap` | Session management, resource pool |
| 642 (Search Autocomplete) | Trie + `PriorityQueue` | GraphQL search suggestions |

### Concurrency APIs Used in Backend Context

```java
// Problem 1188: Bounded Blocking Queue → Spring Integration Channel
class BoundedBlockingQueue {
    private final Deque<Integer> queue = new ArrayDeque<>();
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();
    // ... (see LEETCODE_PATTERN_CHEATSHEET.md for full implementation)
}
// ⬇ maps to ⬇
// Lab 07: @Bean QueueChannel(new PriorityChannel(capacity))
```

### Stream API in Backend Context

```java
// Problem 347: Top K Frequent Elements → Real-time trending
Map<Integer, Long> frequency = numbers.stream()
    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
// ⬇ maps to ⬇
// Lab 24: Kafka Streams windowed aggregation
```

---

## 3. Backend Engineer's LeetCode Study Plan

### Phase 1: Core Data Structures (Weeks 1-4)
*Target: Build solid foundation for all backend coding interviews*

| Week | Topics | Problems | Lab Link |
|------|--------|----------|----------|
| 1 | Arrays & Hashing | 1, 49, 217, 242, 347, 238 | Lab 02 (REST APIs — data structures) |
| 2 | Two Pointers & Sliding Window | 3, 11, 15, 121, 424, 239 | Lab 24 (Backend Performance — optimization) |
| 3 | Stack & Queue | 20, 155, 739, 622, 1188 | Lab 07 (Messaging — queue patterns) |
| 4 | Linked Lists & Trees | 206, 141, 102, 230, 235 | Lab 01 (Spring Boot — data structures) |

### Phase 2: Backend-Relevant Specialized Topics (Weeks 5-8)

| Week | Topics | Priority Problems | Backend Reason |
|------|--------|-------------------|----------------|
| 5 | Concurrency (ALL must-solve) | 1114, 1115, 1188, 1226, 1242 | Service async, thread pools, locks |
| 6 | Heap & Intervals | 56, 253, 295, 347, 692 | Stream processing, scheduling |
| 7 | Design (ALL must-solve) | 146, 355, 380, 460, 981, 706 | Caching, API design, data stores |
| 8 | Graphs (Backend focus) | 133, 207, 269, 323, 399 | Service dependency, discovery |

### Phase 3: SQL & Database (Weeks 9-10)

| Week | Topics | Problems | Lab Link |
|------|--------|----------|----------|
| 9 | Basic SQL | 175, 176, 181, 182, 183 | Lab 04 (Spring Data JPA — queries) |
| 10 | Advanced SQL | 180, 185, 534, 618, 185 | Lab 04 (Spring Data JPA — complex queries) |

### Phase 4: System Design + Mock Interviews (Weeks 11-12)

| Week | Focus | Activities |
|------|-------|------------|
| 11 | System Design | 2 design problems/day (see SYSTEM_DESIGN_CHEATSHEET.md) |
| 12 | Mock Interviews | 1 full mock interview/day + review missed problems |

---

## 4. Coding Problems vs Backend Design Problems

### Coding Problems (LeetCode Format)

| Characteristic | What to Expect | Preparation |
|----------------|----------------|-------------|
| **Format** | Single function implementation | LeetCode practice |
| **Language** | Any (Java preferred for Spring Boot roles) | Java syntax, Stream API, Collections |
| **Time** | 20-45 minutes | Timed practice (NeetCode) |
| **Focus** | Algorithm correctness + efficiency | Big O analysis |
| **Backend twist** | "Implement a rate limiter" | Frame solution with backend context |

### Backend Design Problems (Whiteboard Format)

| Characteristic | What to Expect | Preparation |
|----------------|----------------|-------------|
| **Format** | System architecture discussion | Draw.io, whiteboard practice |
| **Scope** | End-to-end system (API → DB → Cache → Deployment) | System design cheatsheets |
| **Time** | 45-60 minutes | Mock interviews |
| **Focus** | Trade-offs, scaling, reliability | Decision-making |
| **Expectation** | "Design TikTok's recommendation API" | 5-7 services, data flow, scaling |

### Hybrid Problems (Increasingly Common)

Some companies now combine both in a single round:

```markdown
**Example Hybrid Problem (Stripe, Uber):**
"Design a payment webhook delivery system AND implement the idempotency mechanism."

Phase 1 (Design - 30 min):
- Architecture: webhook table → Kafka → delivery workers
- Idempotency: Redis key with TTL, idempotency key in header
- Retries: exponential backoff with jitter, dead-letter queue

Phase 2 (Coding - 15 min):
- Implement the idempotency check in Java
- Implement the retry schedule calculator
- Implement the webhook payload signature HMAC
```

---

## 5. Weekly Study Plan (12 Weeks)

### Week 1-2: Arrays, Hashmaps, Strings

| Day | Problems | Backend Context |
|-----|----------|-----------------|
| Mon | 1, 217, 242 | API parameter validation |
| Tue | 49, 128, 347 | Data aggregation, trending |
| Wed | 238, 271 | Request/response serialization |
| Thu | 3, 424 | Rate limiting window |
| Fri | Review + implement from scratch | — |
| Sat | Mock coding + one system design | — |
| Sun | Rest + behavioral story prep | — |

### Week 3-4: Trees, Graphs

| Day | Problems | Backend Context |
|-----|----------|-----------------|
| Mon | 102, 199, 230 | JSON tree traversal |
| Tue | 105, 208, 211 | API routing (Trie) |
| Wed | 133, 207, 210 | Service dependency graph |
| Thu | 261, 323, 269 | Service discovery, config |
| Fri | Review + implement from scratch | — |
| Sat | Mock coding + one system design | — |
| Sun | Per-lab INTERVIEW.md read + take notes | — |

### Week 5-6: Concurrency (Backend Focus)

| Day | Problems | Backend Context |
|-----|----------|-----------------|
| Mon | 1114, 1115 | Async processing order |
| Tue | 1188 | Message queue (Kafka topic) |
| Wed | 1226 | Distributed locks (ZooKeeper) |
| Thu | 1242 | Parallel API fetching |
| Fri | Implement custom thread pool | — |
| Sat | Mock coding (concurrency focus) + system design | — |
| Sun | Lab 15 (WebFlux/Reactive) review | — |

### Week 7-8: Design Problems (Backend Focus)

| Day | Problems | Backend Context |
|-----|----------|-----------------|
| Mon | 146, 460 | Redis caching (Lab 13) |
| Tue | 355 | Feed API + fanout (Lab 07) |
| Wed | 380 | In-memory data store |
| Thu | 622, 641 | Message queue structure (Lab 07) |
| Fri | 981, 635 | Versioned data store (Lab 17) |
| Sat | Mock system design + review labs | — |
| Sun | Lab 13, 07, 17 deep review | — |

### Week 9-10: SQL + Spring Data JPA

| Day | Problems | Backend Context |
|-----|----------|-----------------|
| Mon | 175, 176, 181 | Basic JPA JOINs |
| Tue | 180, 183, 184 | Window functions (JPA @Query) |
| Wed | 185, 534, 618 | Complex window aggregations |
| Thu | 512, 569, 571 | Advanced SQL patterns |
| Fri | Translate SQL to Spring Data JPA | — |
| Sat | Lab 04 (Spring Data JPA) full review | — |
| Sun | Mock coding + system design | — |

### Week 11-12: Full Mock Interviews

| Day | Focus | Activities |
|-----|-------|------------|
| Mon | Coding mock (1hr) + review | Pick 2 NeetCode 150 problems, timed |
| Tue | System design (1hr) | Draw architecture for a new problem |
| Wed | Behavioral mock (1hr) | STAR stories, company-specific questions |
| Thu | Company research | Read per-company section in COMPANY_INTERVIEW_GUIDE.md |
| Fri | Weak areas review | Re-do all P0 problems (see LEETCODE_PATTERN_CHEATSHEET.md) |
| Sat | Full day: 4-hour simulation | 2 coding + 1 system design + 1 behavioral |
| Sun | Rest + confidence building | Review success stories, compensation negotiation |

---

## Priority Matrix for Backend Roles

| Problem Category | Google | Amazon | Meta | Microsoft | Netflix | Uber | Stripe | DoorDash |
|-----------------|--------|--------|------|-----------|---------|------|--------|----------|
| Concurrency | P0 | P1 | P1 | P1 | P0 | P0 | P1 | P1 |
| Caching/Design | P0 | P0 | P0 | P0 | P0 | P0 | P0 | P0 |
| SQL/DB | P1 | P1 | P2 | P1 | P2 | P1 | P0 | P1 |
| Graphs | P0 | P1 | P0 | P1 | P1 | P0 | P2 | P1 |
| Arrays/Hashmaps | P0 | P0 | P0 | P0 | P1 | P0 | P0 | P0 |
| Stream Processing | P1 | P2 | P1 | P2 | P0 | P0 | P1 | P0 |

*P0 = Must solve before interview / P1 = Strongly recommended / P2 = Nice to have*

---

## Key Resources by Lab

| Lab | Topic | Relevant LeetCode Problems | Key LeetCode |
|-----|-------|---------------------------|-------------|
| 01 | Spring Boot Basics | (refactoring, OOD patterns) | 380, 706 |
| 02 | REST APIs | API design problems | 355, 146 |
| 03 | Spring MVC | (MVC patterns in OOD) | 1472 |
| 04 | Spring Data JPA | All SQL problems | 175-185 |
| 05 | Transaction Mgmt | Concurrency + ACID patterns | 1188, 1226 |
| 06 | Security | (no direct LeetCode, but concepts) | — |
| 07 | Messaging | Producer-Consumer, Queue design | 622, 1188 |
| 13 | Caching | LRU, LFU, Time Map | 146, 460, 981 |
| 15 | WebFlux/Reactive | Concurrency, async | 1114, 1115, 1242 |
| 17 | API Versioning | Time-based KV, versioning | 981 |
| 18 | File/Batch | Intervals, merging | 56, 57, 986 |
| 22 | GraphQL DGS | Trie, autocomplete | 208, 642 |
| 23 | CQRS/Axon | Event-driven state | (conceptual) |
| 24 | Backend Performance | Stream processing, sliding window | 239, 295, 692 |

---

<div align="center">

**The best backend engineers don't just solve problems — they understand why the problem matters in production.**

</div>
