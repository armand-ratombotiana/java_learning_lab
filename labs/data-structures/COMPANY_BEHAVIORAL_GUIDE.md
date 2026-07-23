# Company Behavioral Interview Guide — Data Structures Academy

Behavioral questions tailored for data structures engineers — with concrete DS project examples and company-specific frameworks.

---

## Google

### Culture
**Values**: Innovation, "10x thinking", psychological safety, intellectual humility. Google wants engineers who challenge assumptions and relish ambiguity.

### Leadership Principles (unofficial but tested)
- **Googleyness**: Comfort with ambiguity, intellectual humility, collaboration
- **Cognitive ability**: How you approach unfamiliar problems
- **Growth mindset**: Learning from failure, seeking feedback
- **Bias to action**: Making decisions with incomplete information

### Top 10 Behavioral Questions
1. Tell me about a project you led that involved complex data structures.
2. Describe a time you had to choose between two data structures for a problem.
3. How do you approach a problem you have never seen before?
4. Tell me about a time you disagreed with a technical decision.
5. Describe a time you optimized a system for performance.
6. How do you ensure your code is maintainable and readable?
7. Tell me about a failure and what you learned.
8. Describe a time you had to learn a new technology quickly.
9. How do you handle ambiguity in requirements?
10. Tell me about a time you mentored someone on DS concepts.

### STAR Sample Answer: Optimizing a Data Structure for Performance

> **Situation**: Our team was building a real-time analytics dashboard that required computing range sum queries on streaming financial data. The initial implementation used an ArrayList and computed sums by iterating O(n) per query.
> **Task**: Reduce query latency from 50ms to under 1ms to support real-time updates.
> **Action**: I identified that the access pattern was prefix-sum heavy with occasional point updates. I replaced the ArrayList with a Fenwick tree (Binary Indexed Tree), reducing queries to O(log n). I also implemented lazy propagation for batch updates. I benchmarked both implementations — the Fenwick tree was 97x faster for our workload.
> **Result**: Query latency dropped to 0.3ms. The dashboard went from refreshing every 10 seconds to real-time. I documented the trade-off in a team RFC, and the pattern was adopted by two other teams.

### How to Frame DS Experience
- Framing: "I chose X over Y because of [access pattern / memory constraint / concurrency need]"
- Always mention the **alternative** you rejected and why
- Google interviewers love hearing about the **optimization journey**, not just the final result

### Common Pitfalls
- Being too vague: "I optimized a hash map" → instead: "I replaced separate chaining with cuckoo hashing to reduce worst-case lookup time"
- Not discussing trade-offs: Every DS choice has trade-offs — acknowledge them
- Forgetting to mention complexity analysis unprompted
- Over-engineering: Google values simple, correct solutions over complex ones

### Questions to Ask the Interviewer
- "How does your team use segment trees or Fenwick trees in production?"
- "What's the most challenging data structure problem your team has solved recently?"
- "How does Google's infrastructure handle the trade-off between consistency and availability in distributed data structures?"
- "What's the interview process like for the Hiring Committee review?"

---

## Microsoft

### Culture
**Values**: Growth mindset, diversity & inclusion, customer obsession, "One Microsoft". Microsoft values collaborative engineers who write production-quality code.

### Leadership Principles (Microsoft Values)
- **Growth mindset**: Embrace challenges, learn from mistakes
- **Customer obsession**: Understand customer needs deeply
- **Diversity & inclusion**: Bring diverse perspectives
- **One Microsoft**: Collaborate across teams, break silos
- **Making a difference**: Have impact at scale

### Top 10 Behavioral Questions
1. Tell me about a time you wrote production-quality code for a data structure.
2. Describe a project where you had to make a trade-off between performance and readability.
3. How do you handle a situation where you disagree with your manager's technical decision?
4. Tell me about a time you had to learn a new data structure for a project.
5. Describe a time you improved the performance of an existing system.
6. How do you ensure code quality in your projects?
7. Tell me about a time you collaborated with a difficult teammate.
8. Describe a complex technical problem you solved from first principles.
9. How do you stay current with new data structures and algorithms?
10. Tell me about a time you received critical feedback.

### STAR Sample Answer: Choosing a Data Structure Under Constraints

> **Situation**: At my previous company, we needed to add autocomplete to a search bar with 10M+ products and response time under 100ms.
> **Task**: Design and implement a prefix-search system that fit in 500MB of memory.
> **Action**: I evaluated a HashMap of prefixes (fast but high memory), a standard trie (memory-heavy), and a compressed trie (took time to implement but memory-efficient). I built a prototype of each and measured memory usage on a sample of 1M products. The compressed trie used 60% less memory than the standard trie with only 15% slower lookups. I chose the compressed trie, added prefix caching for hot queries, and wrote extensive unit tests.
> **Result**: Autocomplete response time was 45ms at p99, memory usage was 420MB within budget. The feature shipped on schedule and was praised by the product team.

### How to Frame DS Experience
- Microsoft values **production-quality** framing: mention testing, edge case handling, null safety, documentation
- Contrast recursive vs iterative approaches and discuss stack depth concerns
- Reference specific .NET/Java collections framework classes (Dictionary, SortedDictionary, LinkedList)

### Common Pitfalls
- Writing sloppy code in the behavioral round (they look at your approach as much as your answers)
- Not discussing edge case handling when talking about DS implementations
- Being dismissive of alternative approaches — Microsoft values collaborative debate
- Failing to mention cross-team collaboration when discussing impact

### Questions to Ask the Interviewer
- "How does Microsoft's Azure team choose between B-tree and LSM-tree for storage services?"
- "What does 'production-quality' mean on your team specifically?"
- "How does the 'Asks' feedback process work after the interview?"
- "What's the most challenging infrastructure problem your team is solving with data structures?"

---

## Amazon

### Culture
**Values**: Customer obsession, ownership, invent & simplify, deliver results. Every behavioral answer must tie to one or more Leadership Principles (LPs).

### Leadership Principles (16 total — key ones for DS engineers)
| LP | How to Demonstrate It with DS |
|----|-------------------------------|
| Customer Obsession | "I chose a Bloom filter to reduce cache misses, improving latency for end users by 40%" |
| Ownership | "When the LRU cache had a concurrency bug, I owned the fix end-to-end, including rollout and monitoring" |
| Invent & Simplify | "I replaced a complex B-tree implementation with a skip list — simpler code, same performance" |
| Dive Deep | "I analyzed the hash function collision rate and tuned the load factor for our specific data distribution" |
| Deliver Results | "Despite the timeline, I delivered the range-query optimization using a segment tree that met the SLA" |
| Bias for Action | "I prototyped three DS approaches in a day and picked the best one by evening" |

### Top 10 Behavioral Questions
1. Tell me about a time you took ownership of a data structure implementation.
2. Describe a time you had to choose between simplicity and performance.
3. How have you ensured customer needs were met in a technical project?
4. Tell me about a time you invented a simpler solution to a complex problem.
5. Describe a time you dived deep into a technical problem.
6. Tell me about a time you disagreed with a teammate about a technical approach.
7. How have you delivered results under tight deadlines?
8. Describe a time you had to make a decision with incomplete information.
9. Tell me about a time you failed and what you learned.
10. How have you mentored others on data structures?

### STAR Sample Answer: DS Decision with Customer Impact

> **Situation**: Our product recommendation service had a 5% cache miss rate, causing 2-second latency spikes during flash sales that directly impacted customer experience.
> **Task**: Reduce cache miss rate below 1% during traffic spikes.
> **Action**: I analyzed the access pattern and realized that cold keys (new products) were causing most misses. I designed a two-tier cache — a small Bloom filter in front of the LRU cache to prevent cache breakdown, with a second-level distributed cache. I implemented the Bloom filter with optimal k hash functions based on our false-positive tolerance (1%). I A/B tested the solution against the existing LRU-only approach.
> **Result**: Cache miss rate dropped from 5% to 0.3% during peak traffic. P99 latency went from 2s to 50ms. Revenue increased 3% due to faster page loads during flash sales. This was recognized in my team's quarterly review.

### How to Frame DS Experience
- Every answer must explicitly name the Leadership Principle you're demonstrating
- Quantify everything — Amazon loves metrics: "40% improvement", "50ms latency", "3% revenue lift"
- Frame DS choices in terms of **customer impact**: "Using a Bloom filter saved users 200ms per request"

### Common Pitfalls
- Not explicitly naming the LP you're demonstrating (interviewers check off LPs)
- Talking about "we" instead of "I" — Amazon wants to hear YOUR specific contribution
- Being too abstract about DS choices — always tie back to customer impact
- Not having a failure story prepared (Amazon asks for one in almost every loop)

### Questions to Ask the Interviewer
- "How does your team apply Amazon Leadership Principles when choosing data structures?"
- "What's the most technically difficult data structure problem your team has solved?"
- "How does the bar raiser evaluate DS-related decisions in the interview?"
- "How does your team think about the trade-off between caching and consistency?"

---

## Meta (Facebook)

### Culture
**Values**: Move fast, be bold, focus on impact, care about people (less formal LP structure). Meta values speed and execution velocity.

### Core Values (tested in behavioral)
- **Move fast with stable infra**: Ship quickly but maintain quality
- **Focus on impact**: Work on what matters most
- **Be open**: Share information freely, give direct feedback
- **Build social value**: Technology that connects people
- **Meta, not me**: Company success over individual ego

### Top 10 Behavioral Questions
1. Describe a time you had to move quickly on a technical decision.
2. Tell me about a time you had a technical disagreement with a teammate.
3. How do you prioritize your work when everything is urgent?
4. Describe a time you simplified a complex system.
5. Tell me about a time you took a calculated risk.
6. How do you handle feedback on your code?
7. Describe a time you solved a problem in a novel way.
8. Tell me about a time you worked on a project with ambiguous requirements.
9. How do you ensure quality when shipping quickly?
10. Describe a time you had a significant impact on a project.

### STAR Sample Answer: Moving Fast with Data Structures

> **Situation**: Our team was building a real-time comment system for a live event feature. The initial design used a balanced BST for storing comments by timestamp, but it was taking too long to implement and test.
> **Task**: Ship the comment system within two weeks without sacrificing reliability.
> **Action**: I proposed replacing the BST with a simpler array-based approach — comments appended to a dynamic array, sorted by insertion order (which was timestamp-aligned). For the "top comments" feature, I used a separate max-heap that was rebuilt every 10 seconds. I argued that the BST's O(log n) operations were unnecessary because our scale (10K concurrent users) could be served with O(n) operations on modern hardware. I prototyped both, the array approach was 3x faster to implement and had zero bugs in the first week.
> **Result**: We shipped on time. The simpler approach handled the event with 15K concurrent users and 0 downtime. The "top comments" heap was later optimized with a more sophisticated ranking algorithm.

### How to Frame DS Experience
- Meta values **speed** — emphasize when you chose a simpler solution to ship faster
- Show that you can balance speed and quality (tests, edge cases, monitoring)
- Mention impact quantified: "Served N users", "Reduced latency by X%", "Shipped in Y weeks"

### Common Pitfalls
- Being too slow or cautious in your answer — Meta likes decisive engineers
- Not having a concrete metric for impact
- Talking about a project that didn't ship (Meta values shipped impact)
- Over-engineering in the story — Meta prefers simple solutions that work

### Questions to Ask the Interviewer
- "How does Meta balance move-fast culture with maintaining reliable data infrastructure?"
- "What's the most interesting data structure problem in the Meta stack?"
- "How does the behavioral round factor into the overall hiring decision?"
- "What does 'impact' look like for a data structures engineer on your team?"

---

## Apple

### Culture
**Values**: Secrecy, craftsmanship, simplicity, collaboration. Apple values engineers who obsess over performance and memory efficiency.

### Leadership Values (culturally tested)
- **Craftsmanship**: Obsession with quality and detail
- **Simplicity**: Elegant, minimal solutions
- **Collaboration**: Cross-functional teamwork (HW + SW)
- **Passion**: Genuine excitement for the product
- **Innovation**: Thinking differently, challenging assumptions

### Top 10 Behavioral Questions
1. Tell me about a time you optimized a system for memory usage.
2. Describe a project where craftsmanship was critical.
3. How do you handle the tension between performance and code readability?
4. Tell me about a time you collaborated across teams.
5. Describe a situation where you had to simplify a complex system.
6. How do you debug a performance bottleneck in a data structure?
7. Tell me about a time you advocated for a technical decision.
8. Describe a time you had to learn a new technology quickly.
9. How do you ensure your code is efficient on constrained hardware?
10. Tell me about a time you were passionate about a project outcome.

### STAR Sample Answer: Memory-Constrained DS Optimization

> **Situation**: On an IoT project, we needed to maintain a set of 100K sensor IDs in memory on a device with only 64KB RAM. A standard HashSet would consume ~800KB.
> **Task**: Reduce the memory footprint to under 20KB while maintaining acceptable false-positive rates.
> **Action**: I designed a Bloom filter with a target false-positive rate of 1%. I calculated the optimal bit-array size (m = n * ln(1/p) / ln(2)^2 ≈ 96KB wasn't enough), so I used two optimizations: (1) a counting Bloom filter for deletable keys, (2) partitioned the sensor IDs into 5 groups by type, each with its own smaller filter. I tuned k hash functions using a test harness with real sensor data to minimize false positives.
> **Result**: Memory usage was 18KB (within budget). False positive rate was 0.8% (below 1% target). The solution ran for 6 months on device without a single cache-related issue.

### How to Frame DS Experience
- Apple cares about **memory** above all — always discuss memory footprint, cache lines, and recursion depth
- Mention hardware awareness: cache line size (64 bytes), TLB, page boundaries
- Frame performance choices in terms of **battery life** and **thermal constraints** for mobile devices

### Common Pitfalls
- Not considering memory usage in your answers — Apple expects it
- Ignoring the hardware layer (cache lines, memory alignment)
- Proposing solutions that work on desktop but not on iPhone/watch
- Being vague about optimization — Apple wants specific, measured numbers

### Questions to Ask the Interviewer
- "How does your team approach memory optimization when implementing data structures for iOS/watchOS?"
- "What's the most interesting memory-constrained engineering problem you've solved?"
- "How do you balance performance with battery life in data-heavy features?"
- "What does the team matching process look like after a successful interview?"

---

## Netflix

### Culture
**Values**: "Freedom & Responsibility" — hire great people, give them freedom, expect high performance. No vacation policy, no approval process for decisions.

### Cultural Values (The Netflix Culture Deck)
- **Judgment**: Make smart decisions with incomplete data
- **Communication**: Concise, transparent, direct
- **Impact**: Focus on what moves the business
- **Curiosity**: Learn continuously, explore new ideas
- **Courage**: Speak up, challenge assumptions
- **Passion**: Care deeply about the product and team

### Top 10 Behavioral Questions
1. Tell me about a time you made a high-impact technical decision independently.
2. Describe how you handle ambiguous problems with minimal guidance.
3. How do you communicate complex technical concepts to non-technical stakeholders?
4. Tell me about a time you disagreed with a team decision.
5. Describe a project where you showed strong judgment.
6. How do you prioritize what to work on?
7. Tell me about a time you had to be brutally honest about a technical decision.
8. Describe a time you went above and beyond your role.
9. How do you stay curious about new data structures?
10. Tell me about a time you failed and how you recovered.

### STAR Sample Answer: Independent DS Decision

> **Situation**: I was tasked with designing a real-time recommendation cache for a video streaming service. There was no senior engineer to review my design.
> **Task**: Design and implement a caching layer that could serve 50K QPS with <10ms latency.
> **Action**: I independently evaluated three approaches: a simple LRU cache (HashMap + DLL), a two-tier LRU + Bloom filter, and a consistent hash ring with per-node LRU caches. I built a simulation using 1M request traces. The two-tier approach showed 99.5% hit rate vs 97% for plain LRU, while the consistent hash approach handled node failures better. Based on the data, I chose the two-tier approach for its simplicity and performance. I documented my decision including the rejected alternatives and their metrics.
> **Result**: The cache served 60K QPS at 5ms p99 latency. The design documentation was adopted as a template for other caching projects in the org.

### How to Frame DS Experience
- Emphasize **independent decision-making** and **owning the outcome**
- Show that you evaluate options with data, not gut feeling
- Netflix values engineers who communicate clearly without sugar-coating
- Frame impact in business terms: "reduced streaming start time by 30%"

### Common Pitfalls
- Relying on others to make decisions (Netflix wants independent contributors)
- Being indirect or avoiding hard truths in communication
- Not having strong opinions backed by data
- Focusing on process over impact — Netflix is results-oriented

### Questions to Ask the Interviewer
- "How does Netflix's Freedom & Responsibility culture apply to data structure decisions?"
- "What's the most impactful data structure problem your team has solved recently?"
- "How do you balance technical debt with shipping velocity?"
- "How does the compensation review work for SWEs focused on data infrastructure?"

---

## Uber

### Culture
**Values**: "We make magic with data" — customer obsession, efficiency, reliability at scale. Uber is data-driven with a bias for action.

### Values (Uber's 5 Core Values)
- **Customer obsession**: Build for riders and drivers
- **Make magic**: Innovative solutions to hard problems
- **Efficiency**: Do more with less
- **Reliability at scale**: Systems that work globally
- **Celebrate differences**: Diverse perspectives

### Top 10 Behavioral Questions
1. Tell me about a time you built a system that worked at scale.
2. Describe a time you had to make a trade-off between cost and performance.
3. How do you handle a production incident related to data structure issues?
4. Tell me about a time you optimized a system for reliability.
5. Describe a project where you had to work with real-time data.
6. How do you approach designing a system for global scale?
7. Tell me about a time you had conflicting priorities.
8. Describe a situation where you had to use data to make a difficult decision.
9. How do you ensure your code is efficient when processing millions of events?
10. Tell me about a time you had to pivot your technical approach mid-project.

### STAR Sample Answer: DS for Real-Time Systems

> **Situation**: Our ride-matching service had a bottleneck in the nearest-driver lookup — the quadtree was recomputed from scratch every 10 seconds, causing 1-second search spikes.
> **Task**: Reduce nearest-driver lookup latency to under 100ms.
> **Action**: I redesigned the spatial index to use an incremental quadtree — instead of rebuilding, I inserted and removed drivers as their status changed. I also added a small priority queue per quadtree cell for the top-5 nearest drivers. The incremental approach reduced rebuild frequency by 95%. I tested with 100K concurrent drivers and verified p99 latency stayed below 80ms.
> **Result**: Lookup latency dropped from 1s to 40ms. The change reduced ride-matching time by 15%, directly improving driver utilization and rider wait times.

### How to Frame DS Experience
- Focus on **scale** — Uber handles millions of events per minute
- Discuss **real-time constraints** — "This decision had to be made in under 100ms"
- Mention **geospatial** awareness — quadtrees, geohashing, H3 indexing
- Frame impact in terms of **cost**: "By using a more efficient DS, we reduced server costs by $X/month"

### Common Pitfalls
- Not addressing the real-time nature of Uber's systems
- Ignoring cost implications of DS choices (Uber is efficiency-focused)
- Proposing solutions that don't scale to Uber's global level
- Not having specific metrics about the impact of your work

### Questions to Ask the Interviewer
- "How does Uber use spatial data structures like quadtrees and H3 in production?"
- "What's the most challenging scaling problem your data infrastructure team has solved?"
- "How do you think about the trade-off between accuracy and speed in real-time systems?"
- "What does the career progression look like for a data structures specialist at Uber?"

---

## Stripe

### Culture
**Values**: "Increase the GDP of the internet" — developer experience, economic empowerment, rigorous thinking, direct communication.

### Core Values
- **Users first**: Build for developers who build for businesses
- **Think rigorously**: First-principles thinking, data-driven decisions
- **Move with urgency**: Ship quickly, iterate
- **Direct communication**: Say what you think, no hierarchy games
- **Global optimization**: What's best for the whole company, not just your team

### Top 10 Behavioral Questions
1. Tell me about a time you designed a system that was easy for other developers to use.
2. Describe how you approach API design — how do you make a data structure's interface intuitive?
3. Tell me about a time you had to balance correctness and performance.
4. How do you handle ambiguity in product requirements?
5. Describe a time you gave difficult direct feedback to a teammate.
6. Tell me about a time you made a decision that benefited the whole organization.
7. How do you ensure your code is easy for others to maintain?
8. Describe a time you optimized a system for developer experience.
9. Tell me about a time you had to deeply understand a complex system.
10. How do you stay disciplined about testing when shipping quickly?

### STAR Sample Answer: API Design for a Data Structure

> **Situation**: I was building an in-memory cache library that would be used by 20+ microservice teams.
> **Task**: Design an API for the cache that was intuitive enough that teams could adopt it without documentation.
> **Action**: I studied how Guava Cache and Caffeine exposed their data structure APIs. I chose to use a builder pattern for configuration (eviction policy, TTL, max size) and a Map-like interface for get/put operations. The underlying DS was a ConcurrentHashMap with per-segment LRU eviction. I added JavaDoc with time-complexity annotations for every method. Before releasing, I ran a readability review with 5 teams and incorporated their feedback on method naming and exception handling.
> **Result**: The library was adopted by 15 teams in the first quarter. Zero production bugs in the first 6 months. Developer surveys rated the API 4.8/5 for intuitiveness.

### How to Frame DS Experience
- Stripe values **developer experience** — how easy is your DS API to use correctly?
- Emphasize **correctness** — Stripe handles billions of dollars; data structure errors have real costs
- Discuss **testing methodology** — property-based testing, fuzzing, correctness proofs
- Frame impact in terms of **developer velocity**: "Teams adopted the DS in 2 hours instead of 2 days"

### Common Pitfalls
- Neglecting API design in your answers — Stripe cares about interfaces
- Not discussing correctness guarantees (Stripe is a payments company)
- Being indirect in communication — Stripe values direct, candid feedback
- Focusing on internal implementation without considering the user's perspective

### Questions to Ask the Interviewer
- "How does Stripe think about the trade-off between strong consistency and availability in payment data structures?"
- "What's the most elegant data structure API you've worked on at Stripe?"
- "How do you balance shipping velocity with correctness in a payments context?"
- "What does the interview process look like for the API design round?"

---

## DoorDash

### Culture
**Values**: "We're building the last-mile logistics platform" — merchant obsession, operational excellence, data-driven decisions.

### Values
- **Merchant obsession**: Serve the merchants who serve customers
- **Operational excellence**: Efficient, reliable, scalable systems
- **Winning with data**: Decisions backed by data, not intuition
- **One DoorDash**: Cross-functional collaboration
- **Bias for action**: Move fast, learn, iterate

### Top 10 Behavioral Questions
1. Tell me about a time you optimized a system for real-time dispatching.
2. Describe a project where you used data to drive a technical decision.
3. How do you balance speed and accuracy in real-time systems?
4. Tell me about a time you designed a system to handle peak traffic.
5. Describe a situation where you had to choose between a simple and a complex solution.
6. How do you handle incidents in production systems?
7. Tell me about a time you collaborated with product and operations teams.
8. Describe a time you improved the efficiency of an existing system.
9. How do you think about scaling a data structure from prototype to production?
10. Tell me about a time you mentored a junior engineer on DS concepts.

### STAR Sample Answer: Real-Time Dispatching Optimization

> **Situation**: Our dispatch system was using a simple round-robin algorithm to assign orders to dashers, leading to 20% of orders being delayed because the nearest available dasher wasn't considered.
> **Task**: Redesign the dispatch algorithm to minimize wait times.
> **Action**: I designed a spatial index using a quadtree to track dasher locations in real-time. For each incoming order, the system queried the quadtree for the K nearest available dashers, then used a priority queue to pick the one with the best ETA. The quadtree was updated incrementally as dashers moved. I also added a segment tree for time-window constraints (e.g., dasher availability by hour). I A/B tested against the round-robin baseline.
> **Result**: Average wait time decreased by 32%. Dasher utilization increased 15%. The system handled 2x peak traffic without degradation.

### How to Frame DS Experience
- Focus on **real-time optimization** — DoorDash is a real-time logistics platform
- Mention **spatial awareness** — geohashing, quadtrees, H3 for store/dasher mapping
- Discuss **peak traffic** — DoorDash has massive spikes during lunch/dinner and holidays
- Frame impact in **business metrics**: lower wait times = higher order volume = more revenue

### Common Pitfalls
- Not considering the real-time nature of DoorDash's systems
- Ignoring the marketplace dynamics: supply (dasher availability) and demand (orders)
- Proposing solutions that don't handle peak-hour traffic
- Not having specific metrics about improvements

### Questions to Ask the Interviewer
- "How does DoorDash use spatial data structures for real-time dasher dispatch?"
- "What's the most challenging scaling problem the dispatch team has solved?"
- "How do you think about the trade-off between dispatch speed and optimality?"
- "What does the career path look like for engineers focused on data infrastructure?"

---

## Quick Reference: Behavioral Framing by Company

| Company | Emphasize | Key Framing | Avoid |
|---------|-----------|-------------|-------|
| Google | Optimization journey, trade-offs | "I analyzed three approaches..." | Vague complexity claims |
| Microsoft | Production code, collaboration | "I wrote clean, tested code..." | Ignoring edge cases |
| Amazon | Leadership Principles, metrics | "Customer impact was X%..." | Not naming LPs explicitly |
| Meta | Speed, impact, shipped work | "We shipped in 2 weeks..." | Over-engineering stories |
| Apple | Memory, hardware, efficiency | "Memory usage dropped to Y KB..." | Ignoring hardware constraints |
| Netflix | Independence, data-driven | "I made the call based on data..." | Waiting for approval |
| Uber | Scale, real-time, cost | "System handled 1M req/s..." | Ignoring cost implications |
| Stripe | Developer experience, correctness | "Zero bugs in 6 months..." | Neglecting API design |
| DoorDash | Real-time, logistics, optimization | "Wait time decreased 32%..." | Not measuring impact |

---

## Universal DS Behavioral Template

```
S: The project was [context] and we needed to [goal].
T: I owned [specific responsibility].
A: I evaluated [DS option 1] (pros: ..., cons: ...) vs [DS option 2] (pros: ..., cons: ...).
   I chose [DS] because [specific reason — access pattern, memory, concurrency, scale].
   Implementation: [key details — how you built it, edge cases, testing].
   [Company-specific framing: Google = trade-offs, Amazon = LP, Meta = speed, etc.]
R: The result was [quantified outcome — latency, memory, throughput, revenue].
   I documented the [decision/pattern] and it was reused by [N teams].
```

For company-specific technical preparation, see [COMPANY_INTERVIEW_GUIDE.md](./COMPANY_INTERVIEW_GUIDE.md) and [ACADEMY_INTERVIEW_GUIDE.md](./ACADEMY_INTERVIEW_GUIDE.md).
