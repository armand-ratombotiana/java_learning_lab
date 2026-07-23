# Company Interview Process Guide — Data Structures Academy

This guide focuses on the **interview process itself** (rounds, timelines, formats, evaluation criteria) for each major company. For **technical content** (specific DS topics per company), see [ACADEMY_INTERVIEW_GUIDE.md](./ACADEMY_INTERVIEW_GUIDE.md).

---

## Google

### Interview Process

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| Phone Screen | Technical phone (Google Docs) | 45 min | 1-2 medium DS problems, verbal reasoning |
| Onsite Coding 1 | CoderPad (choose language) | 45 min | Problem-solving process, multiple approaches |
| Onsite Coding 2 | CoderPad | 45 min | Follow-ups with changing constraints |
| System Design | Whiteboard (Jamboard) | 45 min | DS choice justification at scale |
| Googleyness & Leadership | Behavioral | 30-45 min | Ambiguity, collaboration, growth mindset |
| Hiring Committee Review | No candidate | N/A | Packet of all feedback reviewed by committee |

### Timeline

1. **Application → Phone screen**: 1-3 weeks
2. **Phone screen → decision**: 3-5 business days
3. **Onsite scheduling**: 2-4 weeks out
4. **Onsite → Hiring Committee**: 1-2 weeks
5. **HC → Offer/Rejection**: 1-2 weeks
6. **Total**: 6-12 weeks from application to decision

### Company-Specific Tips

- **Coding environment**: Google Docs (phone) / CoderPad (onsite). No syntax highlighting. Practice writing code in a plain text editor.
- **Evaluation**: Each interviewer writes a detailed feedback report. HC reviews all reports holistically. One weak round does not automatically disqualify you.
- **Expectation**: Google wants to see your **thought process**, not just the correct answer. Always verbalize trade-offs.
- **Data structures emphasis**: Graphs (topological sort, shortest path), tries (autocomplete), trees (BST, LCA, serialization), hash maps. Labs **04-trees**, **05-graphs**, **08-tries**, **06-hash-tables** are highest priority.
- **Unique to Google**: No behavioral questions in coding rounds; system design rounds focus heavily on DS internals (e.g., "Why use a B-tree here instead of a hash map?").

### Interview Stories

> **SDE2, 2024**: "Phone screen was a graph problem — number of connected components. I started with DFS, then optimized with Union-Find. The interviewer asked me to compare both approaches on time, space, and real-world performance. No feedback during coding, which was unnerving, but I kept talking through each step."

> **L3, 2023**: "Coding round 1 was Alien Dictionary (LC 269). I built the graph and did topological sort. The follow-up was: 'What if the dictionary has 1 million words? How does your solution change?' — they wanted to hear about adjacency list vs matrix trade-offs."

> **L4, 2024**: "System design was Design Google Docs. The interviewer specifically asked about the autocomplete feature — I walked through trie construction, prefix search, and top-K suggestions using a min-heap. They pushed on memory optimization for mobile."

### Recommended LeetCode Sets

- Top 100 Google questions (LeetCode company tag)
- Focus: Graphs (LC 200, 207, 269, 743), Trees (LC 124, 297, 987), Tries (LC 208, 212, 642)
- Labs to complete: **04-trees**, **05-graphs**, **08-tries**, **12-segment-trees**, **13-fenwick-tree**

### Compensation Negotiation for DS-Heavy Roles

- Google offers are formulaic but negotiable. Use competing offers (Meta, Stripe) for leverage.
- DS-heavy roles (Google Brain, Core ML) have higher equity grants — ask for a "front-loaded" vesting schedule.
- **Key data point**: Levels.fyi shows Google L4 total comp at $280K-$380K for DS-focused SWE roles.

---

## Microsoft

### Interview Process

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| Phone Screen | Teams + CoderPad | 45 min | 1-2 problems, fundamentals |
| Onsite (Asks Loop) | 4-5 rounds, separate interviewers | 45 min each | Coding, system design, behavioral |
| Hiring Manager | Teams | 45 min | Team fit, career goals, past projects |
| "Asks" Feedback | Internal | N/A | Interviewers submit structured feedback forms |

### Timeline

1. **Application → Recruiter screen**: 1-2 weeks
2. **Phone screen**: 1-2 weeks after screen
3. **Onsite**: 2-4 weeks after phone
4. **Decision**: 1-2 weeks (often fast — 3-5 days)
5. **Total**: 4-8 weeks

### Company-Specific Tips

- **Coding environment**: Microsoft Teams with CoderPad. Interviewers watch your build process.
- **Evaluation**: Microsoft uses the "Asks" framework — each round is scored independently. You need strong passes on most rounds.
- **Expectation**: **Production-quality code** — handle nulls, edge cases, use meaningful variable names, write test cases.
- **Data structures emphasis**: Linked lists (LC 2, 21, 206, 234), trees (LC 98, 236, 297), hash maps. Labs **02-linked-lists**, **04-trees**, **06-hash-tables** are critical.
- **Unique to Microsoft**: Recursion vs iteration discussions are common. They test your depth of understanding of TreeMap/TreeSet internals.

### Interview Stories

> **SDE2, 2024**: "Phone screen was a linked list problem — reverse nodes in k-group. I wrote the iterative solution first, then the interviewer asked for the recursive approach. They wanted me to compare stack depth and memory usage between the two."

> **SWE, 2023**: "Onsite had a design round: 'Design a distributed LRU Cache.' I started with HashMap + DLL, then discussed sharding, consistent hashing, and replication. The interviewer loved that I mentioned Bloom filters for cache miss optimization."

> **Senior SWE, 2024**: "Behavioral was straightforward — 'Tell me about a project where you had conflicting requirements.' I framed my answer around choosing between a B-tree and a skip list for a storage system I had built."

### Recommended LeetCode Sets

- Top 100 Microsoft questions (LeetCode company tag)
- Focus: Linked Lists (LC 19, 21, 141, 206), Trees (LC 98, 102, 236, 297), Strings (LC 8, 468)
- Labs to complete: **02-linked-lists**, **03-stacks-queues**, **04-trees**, **09-advanced-trees**

### Compensation Negotiation

- Microsoft base salary bands are narrower than Google's. Negotiate RSUs and signing bonus instead.
- DS infrastructure roles (Azure, Cosmos DB) command higher levels.
- **Key data point**: L64 total comp for DS specialists is $250K-$350K.

---

## Amazon

### Interview Process

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| Phone Screen | Chime + LiveCode | 45-60 min | 1-2 coding problems + LP questions |
| Onsite Loop | 5 rounds (4 technical + 1 bar raiser) | 45-60 min each | Coding, system design, LPs |
| Bar Raiser | Cross-team senior engineer | 60 min | Leadership Principles, hire bar |
| Hiring Manager | Technical + behavioral | 45 min | Team fit, project experience |

### Timeline

1. **Application → Phone screen**: 1-2 weeks
2. **Phone → Onsite**: 2-4 weeks
3. **Onsite → Decision**: 3-5 business days (Amazon is fast)
4. **Total**: 3-7 weeks

### Company-Specific Tips

- **Coding environment**: Amazon Chime + LiveCode (or similar shared editor). No autocomplete, but syntax highlighting is available.
- **Evaluation**: Every interviewer submits a written report. Bar raiser has veto power. Leadership Principles (LPs) are assessed in EVERY round.
- **Expectation**: Scalability discussions. Always ask "What if this runs on 1000 servers?"
- **Data structures emphasis**: Arrays, hash tables, LRU cache, Bloom filters, concurrent DS. Labs **01-arrays**, **06-hash-tables**, **15-lru-cache**, **10-bloom-filters**, **16-concurrent-data-structures**.
- **Unique to Amazon**: Expect "design a data structure" questions (LRU Cache, LFU Cache, All O(1) Data Structure). Bar raiser round often combines DS with system design.
- **LPs to emphasize**: Customer Obsession, Deliver Results, Dive Deep, Ownership.

### Interview Stories

> **SDE2, 2024**: "Phone screen was a medium array problem. The interviewer paused 15 minutes for LP questions. I used the STAR method for every answer and made sure to explicitly name the LP I was demonstrating."

> **SDE1, 2023**: "Bar raiser asked: 'Design a data structure for Amazon product recommendations cache.' I built an LRU cache tier with a Bloom filter to avoid cache breakdown. The bar raiser pushed me on false positive rates and memory trade-offs."

> **SDE3, 2024**: "System design was Design DynamoDB-style key-value store. Every component came back to DS choices — consistent hashing for partitioning, LSM trees for write optimization, Bloom filters for SSTable lookups."

### Recommended LeetCode Sets

- Top 100 Amazon questions (LeetCode company tag)
- Focus: Arrays (LC 1, 11, 42, 238, 560), LRU (LC 146, 460), Trees (LC 102, 199, 236)
- Labs to complete: **06-hash-tables**, **15-lru-cache**, **10-bloom-filters**, **17-spatial-data-structures**

### Compensation Negotiation

- Amazon uses TC (total compensation) targets. Base salary caps at $160K-$175K. Negotiate RSUs aggressively.
- DS-heavy roles (AWS, DynamoDB, ElastiCache) qualify for higher equity.
- **Key data point**: SDE2 total comp ranges $200K-$350K depending on DS specialization.

---

## Meta (Facebook)

### Interview Process

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| Phone Screen | CoderPad (Facebook editor) | 45 min | 2 coding problems (medium) |
| Onsite Coding 1 | CoderPad | 45 min | 2 problems — arrays, strings, hash maps |
| Onsite Coding 2 | CoderPad | 45 min | 2 problems — trees, recursion, graphs |
| Behavioral | Video call | 45 min | Past experience, motivation, culture fit |
| System Design (E5+) | Whiteboard | 45 min | Scale, DS choices, distributed systems |

### Timeline

1. **Application → Recruiter call**: 1-2 weeks
2. **Phone screen**: 1-2 weeks later
3. **Onsite**: 2-3 weeks after phone
4. **Decision**: 1 week (very fast)
5. **Total**: 4-8 weeks

### Company-Specific Tips

- **Coding environment**: Facebook's CoderPad variant. Minimal autocomplete, basic syntax highlighting.
- **Evaluation**: Speed and accuracy are critical — 2 problems per 45-minute round. You must finish both.
- **Expectation**: Meta asks **variations** of the same core problems. Practice deeply on their favorite patterns, not broadly.
- **Data structures emphasis**: Arrays (LC 1, 238, 560), hash maps (LC 49, 347), trees (LC 102, 199, 236), recursion. Labs **01-arrays**, **06-hash-tables**, **04-trees**.
- **Unique to Meta**: Interviewers rarely interrupt — you must talk while coding. Follow-ups are about space optimization (O(n) → O(1)).

### Interview Stories

> **E4, 2024**: "Coding round 1: two problems in 45 minutes. First was an easy array problem (3 min), second was a medium tree problem. The follow-up on the tree was 'Can you do it in O(1) space?' — that was Morris traversal. I had practiced it the night before."

> **E5, 2023**: "Coding round 2 was a hash map problem — Subarray Sum Equals K. After solving it, the follow-up was: 'What if the array has negative numbers?' and then 'What if we want the longest subarray?' Both were variations I had seen on LeetCode."

> **E4, 2024**: "Behavioral was unusual — the interviewer spent 30 minutes on 'Tell me about a time you had a technical disagreement.' I talked about choosing between a hash map and a trie for an autocomplete system. They wanted to hear how I resolved the debate with data."

### Recommended LeetCode Sets

- Top 50 Meta questions (LeetCode company tag) — Meta has a smaller, more focused set
- Focus: Arrays (LC 1, 11, 15, 238, 560), Trees (LC 102, 199, 236, 297), Strings (LC 3, 76, 424)
- Labs to complete: **01-arrays**, **04-trees**, **06-hash-tables**, **03-stacks-queues**

### Compensation Negotiation

- Meta offers are aggressive, especially at E5+. Base salary is competitive; RSUs are the primary lever.
- Use Google or Stripe offers as leverage. Meta will often match or exceed.
- **Key data point**: E4 total comp $250K-$350K, E5 total comp $350K-$500K.

---

## Apple

### Interview Process

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| Phone Screen | Interviewer + shared doc | 45 min | 1-2 coding problems, lightweight |
| Onsite | 5-6 rounds (varies by team) | 45 min each | Coding, DS design, behavioral, manager |
| Team Matching | Multiple team conversations | 30 min each | Fit with specific team |
| Final Decision | After team matching | N/A | Manager + hiring committee |

### Timeline

1. **Application → Phone screen**: 1-4 weeks (varies by team)
2. **Onsite**: 2-6 weeks after phone
3. **Team matching**: 1-4 weeks (can be longest phase)
4. **Total**: 6-16 weeks (unpredictable)

### Company-Specific Tips

- **Coding environment**: Varies by team. Often whiteboard (in-person) or Notes app + verbal explanation. No standard tool.
- **Evaluation**: Very team-dependent. Each interview has significant weight. Team matching can reject even after strong onsite.
- **Expectation**: Memory-conscious solutions. Apple products have tight memory budgets.
- **Data structures emphasis**: Bit arrays, circular buffers, ropes, cache-oblivious structures. Labs **18-circular-buffers**, **31-rope**, **30-cache-oblivious**, **24-xor-unrolled-lists**.
- **Unique to Apple**: Visual problem-solving is valued — draw data structures. Memory footprint analysis is as important as time complexity. Recursion depth concerns (watch for stack overflow on mobile).

### Interview Stories

> **SWE, 2023**: "I interviewed for Camera Frameworks. The coding round asked me to implement a circular buffer for a video processing pipeline. They cared deeply about the overwrite policy — blocking vs dropping frames."

> **SWE, 2024**: "Phone screen was a tree problem — invert a binary tree. But the follow-up was all about memory: 'How much stack space does recursion use? How would you do this iteratively?' I showed both approaches and compared their memory footprints."

> **Senior SWE, 2023**: "System design was Design a text editor for iOS. I proposed a rope data structure for the text buffer. The interviewer asked me to draw the tree, explain split/concat operations, and analyze character deletion performance."

### Recommended LeetCode Sets

- Apple-specific problems on LeetCode (company tag)
- Focus: Bit manipulation (LC 191, 338, 461), In-place algorithms (LC 73, 289), Trees (LC 104, 226)
- Labs to complete: **18-circular-buffers**, **31-rope**, **24-xor-unrolled-lists**, **30-cache-oblivious**

### Compensation Negotiation

- Apple is less structured than other FAANG. Negotiate level (ICT3 vs ICT4) as aggressively as comp.
- DS roles in Core OS, Camera, and Silicon teams have higher compensation.
- **Key data point**: ICT3 total comp $200K-$280K, ICT4 $300K-$400K.

---

## Netflix

### Interview Process

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| Technical Screen | Video call + CoderPad | 45 min | 1 coding problem + architecture discussion |
| Onsite | 4-5 rounds | 45 min each | Coding, system design, culture |
| Culture Fit | Multiple interviewers | 30-45 min each | Freedom & Responsibility values |
| Executive Review | Director/VP | 30 min | Final alignment, compensation |

### Timeline

1. **Application → Initial screen**: 1-3 weeks
2. **Onsite**: 2-4 weeks after screen
3. **Decision**: 3-7 days
4. **Total**: 4-8 weeks

### Company-Specific Tips

- **Coding environment**: CoderPad with interviewer watching. Heavy emphasis on clean, maintainable code.
- **Evaluation**: "Freedom & Responsibility" culture means they hire senior, self-directed engineers. Expect less hand-holding.
- **Expectation**: You should be able to design, code, and defend a complete solution independently.
- **Data structures emphasis**: Hash maps, Bloom filters, LRU caches, consistent hashing. Labs **06-hash-tables**, **10-bloom-filters**, **15-lru-cache**.
- **Unique to Netflix**: System design rounds focus on streaming infrastructure, CDN, recommendation algorithms. They value pragmatic DS choices over theoretical purity.

### Interview Stories

> **Senior SWE, 2024**: "System design was Design Netflix recommendation serving tier. I designed a two-level cache — an LRU for hot recommendations and a Bloom filter for cold-start users. The interviewer pushed me on eviction policies and cache warming strategies."

> **SWE, 2023**: "Coding round was a hard array problem — median of two sorted arrays. The interviewer wanted to see me derive the binary search partition approach from first principles, not just recite the solution."

### Recommended LeetCode Sets

- Focus: System design (not pure coding). Practice designing caching systems, Bloom filters, consistent hashing.
- Labs to complete: **10-bloom-filters**, **15-lru-cache**, **17-spatial-data-structures**, **26-hyperloglog**

### Compensation Negotiation

- Netflix pays top-of-market cash (no RSUs — all cash). Base salary can be $300K-$700K.
- Negotiate base salary directly. There is no equity component.
- **Key data point**: Senior SWE total comp $400K-$700K all cash.

---

## Uber

### Interview Process

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| Phone Screen | Codepad/HackerRank | 45 min | 1-2 medium coding problems |
| Onsite | 4-5 rounds | 45 min each | Coding, system design, behavioral |
| Engineering Manager | Video call | 45 min | Team fit, project experience |
| Behavioral (sometimes separate) | Video call | 45 min | Uber values, conflict resolution |

### Timeline

1. **Application → Phone screen**: 1-2 weeks
2. **Onsite**: 2-4 weeks after phone
3. **Decision**: 5-10 business days
4. **Total**: 5-9 weeks

### Company-Specific Tips

- **Data structures emphasis**: Graphs (rideshare mapping), spatial DS (quadtrees), priority queues (dispatching). Labs **05-graphs**, **17-spatial-data-structures**, **07-heaps**.
- **Unique to Uber**: Fleet dispatching algorithms use priority queues; pricing uses segment trees; maps use spatial structures.
- **Expectation**: Real-time, high-throughput system design knowledge. Uber processes millions of trips per day.

### Interview Stories

> **SWE, 2024**: "System design was 'Design Uber's ETA system.' I used a quadtree for spatial partitioning of drivers, a priority queue for nearest-driver lookup, and a graph with Dijkstra's for route computation."

> **Senior SWE, 2023**: "Coding round was a graph problem — I had to implement a shortest-path algorithm but the edge weights changed dynamically (surge pricing). The follow-up was about using a segment tree to update weights efficiently."

### Recommended LeetCode Sets

- Focus: Graphs (LC 743, 787, 1514), Heaps (LC 23, 295, 973), Spatial problems
- Labs to complete: **05-graphs**, **07-heaps**, **17-spatial-data-structures**, **12-segment-trees**

### Compensation Negotiation

- Uber offers are negotiable. Competing with Lyft or other mobility companies provides leverage.
- DS infrastructure roles (Uber Atlas, mapping) command higher comp.
- **Key data point**: SWE II $200K-$300K, Senior SWE $300K-$450K.

---

## Stripe

### Interview Process

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| Phone Screen | CoderPad | 45 min | 1 coding problem (medium) |
| Onsite Coding | 2 rounds, CoderPad | 45 min each | Hard DS problems, clean code |
| System Design | Whiteboard | 45 min | Distributed payments, DS choices |
| Manager | Video call | 45 min | Career goals, collaboration |
| API Design (sometimes) | Whiteboard | 45 min | Designing intuitive APIs |

### Timeline

1. **Application → Phone screen**: 1-2 weeks
2. **Onsite**: 2-3 weeks after phone
3. **Decision**: 3-5 business days
4. **Total**: 4-7 weeks

### Company-Specific Tips

- **Coding environment**: CoderPad, clean code is paramount. Stripe values elegant, readable solutions.
- **Data structures emphasis**: Hash maps, idempotency keys (Bloom filters), consistent hashing, Merkle trees (ledger verification). Labs **06-hash-tables**, **10-bloom-filters**, **22-merkle-tree**.
- **Unique to Stripe**: API design rounds test your ability to design interfaces where data structures are exposed cleanly. Idempotency and consistency are key themes.
- **Expectation**: Stripe interviews are known for being challenging but fair. Interviewers are trained to minimize bias.

### Interview Stories

> **SWE, 2024**: "Coding round was a hard problem involving a hash map with custom eviction. The interviewer wanted me to design the API first, then implement. They cared deeply about naming and method signatures."

> **Senior SWE, 2023**: "System design was 'Design Stripe's ledger system.' I proposed using a Merkle tree for tamper-evident transaction logs. The interviewer asked about reconciliation — how would you detect discrepancies between two Merkle trees?"

### Recommended LeetCode Sets

- Focus: Hash map design problems, concurrency, idempotency
- Labs to complete: **06-hash-tables**, **22-merkle-tree**, **21-cuckoo-robin-hood**, **16-concurrent-data-structures**

### Compensation Negotiation

- Stripe offers are competitive with top-tier. Equity refreshes are generous.
- DS-heavy roles (infrastructure, payments) have higher comp bands.
- **Key data point**: SWE II $250K-$350K, Senior SWE $350K-$500K.

---

## DoorDash

### Interview Process

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| Phone Screen | HackerRank | 45 min | 1-2 medium DS problems |
| Onsite | 4-5 rounds | 45 min each | Coding, system design, behavioral |
| Technical Deep Dive | Whiteboard | 45 min | Past architecture decisions |
| Hiring Manager | Video call | 45 min | Team fit, project experience |

### Timeline

1. **Application → Phone**: 1-2 weeks
2. **Onsite**: 2-4 weeks after phone
3. **Decision**: 5-7 days
4. **Total**: 4-8 weeks

### Company-Specific Tips

- **Data structures emphasis**: Heaps (dispatching), spatial DS (store mapping), LRU cache (menu caching), segment trees (pricing). Labs **07-heaps**, **17-spatial-data-structures**, **15-lru-cache**, **12-segment-trees**.
- **Unique to DoorDash**: Dispatching is a classic "top K nearest" problem using spatial structures. Pricing optimization uses segment trees for range queries over time windows.
- **Expectation**: Real-time optimization focus. How would you dispatch 1000 dashers simultaneously?

### Interview Stories

> **SWE, 2024**: "System design was 'Design DoorDash's dispatch system.' I used a quadtree for spatial indexing of dashers, a priority queue for each dasher's active orders, and a min-heap for ETA computation."

> **Senior SWE, 2023**: "Coding round was a variation of 'Find K closest stores to a location.' I solved with a max-heap of size K. The follow-up discussed what happens when stores are continuously added and removed — enter a quadtree."

### Recommended LeetCode Sets

- Focus: Heaps (LC 23, 295, 347, 973), Arrays (LC 1, 11, 15, 560), Trees (LC 98, 102, 236)
- Labs to complete: **07-heaps**, **17-spatial-data-structures**, **12-segment-trees**, **18-circular-buffers**

### Compensation Negotiation

- DoorDash offers are competitive for senior roles. Use Uber or Amazon offers as leverage.
- DS roles in marketplace and logistics teams have better comp.
- **Key data point**: SWE II $200K-$300K, Senior SWE $300K-$400K.

---

## TikTok (ByteDance)

### Interview Process

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| Phone Screen | Online coding platform | 45 min | 1-2 medium DS problems |
| Technical Rounds | 3-4 rounds | 60 min each | Deep CS fundamentals, algorithms |
| System Design | Whiteboard | 60 min | High-scale design, DS choices |
| Behavioral (HR) | Video call | 30 min | Culture fit, motivation |
| Cross-functional | Video call | 45 min | Collaboration, English fluency |

### Timeline

1. **Application → Phone screen**: 1-2 weeks
2. **Technical rounds**: 3-5 weeks (can be fast)
3. **System design + HR**: 1-2 weeks
4. **Decision**: 1-2 weeks
5. **Total**: 6-10 weeks

### Company-Specific Tips

- **Coding environment**: Online coding platform with multiple language support. No autocomplete recommended.
- **Data structures emphasis**: Tries (feed search), Bloom filters (content dedup), LRU/Segment trees (feed ranking), space-filling curves (video transcoding). Labs **08-tries**, **10-bloom-filters**, **15-lru-cache**, **25-space-filling-curves**.
- **Unique to TikTok**: Massive scale (1B+ users). Expect questions about recommendation systems, content deduplication, and real-time feed personalization.
- **Expectation**: Deep CS fundamentals. ByteDance interviews are known for being algorithm-heavy.

### Interview Stories

> **SWE, 2024**: "System design was 'Design TikTok's For You feed.' I discussed Bloom filters for seen-post dedup, LRU for hot content, and a scatter-gather pattern using heaps for merging ranked lists from multiple models."

> **Senior SWE, 2023**: "Coding round was a trie problem — auto-complete for search with multi-language support (Chinese characters). We discussed compressed tries and prefix caching for latency."

### Recommended LeetCode Sets

- Focus: Tries (LC 208, 211, 212, 642), Hash maps (LC 49, 560, 128), Arrays (LC 1, 238, 347)
- Labs to complete: **08-tries**, **10-bloom-filters**, **15-lru-cache**, **25-space-filling-curves**

### Compensation Negotiation

- ByteDance offers are cash-heavy and competitive. US salaries rival FAANG.
- Negotiate RSU/equity component carefully — ByteDance is private, so liquidity matters.
- **Key data point**: SWE II $200K-$350K, Senior SWE $300K-$500K.

---

## Quick Comparison: Interview Rounds

| Company | Phone Screen | Coding Rounds | System Design | Behavioral | Team Fit |
|---------|-------------|---------------|---------------|------------|----------|
| Google | 1 | 2 | 1 | 1 (Googleyness) | HC review |
| Microsoft | 1 | 2-3 | 1 | 1 | HM round |
| Amazon | 1 | 2-3 | 1 | Bar raiser (all rounds) | HM round |
| Meta | 1 | 2 | 1 (E5+) | 1 | None |
| Apple | 1 | 2-3 | 0-1 | 1 | Team matching |
| Netflix | 1 | 2 | 1 | 1 | Executive review |
| Uber | 1 | 2 | 1 | 1 | HM round |
| Stripe | 1 | 2 | 1 | 1 | None |
| DoorDash | 1 | 2 | 1 | 1 | HM round |
| TikTok | 1 | 3-4 | 1 | 1 | Cross-functional |

---

## Key Negotiation Principles for DS Engineers

1. **DS specialization is leverage**: If your role requires expertise in probabilistic data structures, concurrent DS, or spatial indexing, highlight this during negotiation — these skills are scarce.
2. **Use the Academy's breadth as proof**: Mention that you have hands-on experience with 35+ data structures including advanced topics (Merkle trees, Skip lists, Fenwick trees, HyperLogLog). This signals rare depth.
3. **Level vs Comp**: For DS-heavy roles, level is often more important than initial comp. A higher level means larger future refreshes and more equity grants.
4. **Multiple offers**: The strongest negotiating position is a competing FAANG offer. Time your interviews strategically.
5. **Signing bonus**: One-time sign-on bonuses can be $25K-$100K+. Ask for this explicitly if base or RSUs are capped.

For full technical preparation per company, see [ACADEMY_INTERVIEW_GUIDE.md](./ACADEMY_INTERVIEW_GUIDE.md).
