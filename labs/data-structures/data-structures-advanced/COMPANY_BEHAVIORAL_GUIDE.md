# Company Behavioral Interview Guide

## Why Behavioral Questions with Data Structures?

FAANG interviewers often weave data structure questions into behavioral prompts:
- "Tell me about a time you optimised a slow system" → you should mention Bloom filter, trie, or segment tree
- "Describe a project where you handled scale" → you should mention Merkle tree for integrity, skip list for concurrency
- "How did you solve a complex problem?" → you should demonstrate DS decision-making process

## The STAR Framework with DS Context

### Situation
Set the context. What was the system? What data structure challenge existed?

**Example:** "Our search autocomplete was using a SQL LIKE query on 10M products. Latency was 200ms at the 99th percentile."

### Task
What was your responsibility? What were the constraints?

**Example:** "I needed to reduce latency to under 10ms while maintaining memory under 500MB."

### Action
What did you do? BE SPECIFIC about data structures.

**Example:** "I implemented a trie in memory with compressed nodes. For non-existent prefixes, we added a Bloom filter to short-circuit before trie traversal. The trie was built off the critical path during deployment."

### Result
Quantify the outcome. Use numbers.

**Example:** "P99 latency dropped from 200ms to 3ms. Memory usage was 120MB. We saved $50K/month in database costs."

## Company-Specific Behavioral Questions

### Google: "Googleyness" & General Cognitive Ability

| Question | Structure Tie-in |
|----------|-----------------|
| "Tell me about a time you had to make a trade-off" | Choosing between HashSet and Bloom filter |
| "Describe a complex system you built" | System design with Merkle tree + segment tree |
| "How do you handle ambiguous requirements?" | Clarifying constraints before picking DS |
| "Tell me about a time you failed" | Implementing lazy segment tree with wrong propagation |
| "How would you improve an existing system?" | Adding trie to replace sequential string matching |

**Sample STAR response:**

> **S:** Our team maintained a URL shortener handling 1B redirects/day. The existing system checked URL uniqueness with a Redis SET, costing $15K/month in memory.
> **T:** I was tasked with reducing infrastructure cost while maintaining correctness guarantees.
> **A:** I proposed a Bloom filter as a pre-check. For 1B URLs, a Bloom filter with 1% FP rate needs ~1.5GB. We built a weekly-updated Bloom filter in a 1GB RAM instance ($10/month). Every incoming URL first hits the Bloom filter. If "not in set" (guaranteed correct), we skip the Redis check entirely, saving 70% of SET lookups. For the 30% "probably in set" case, we fall through to Redis for confirmation.
> **R:** Monthly Redis cost dropped from $15K to $4.5K. Write latency improved 40% due to reduced Redis load. We maintained zero false negatives.

### Amazon: Leadership Principles (14 total)

| LP | Question | DS Application |
|----|----------|---------------|
| **Customer Obsession** | "Tell me about a time you prioritised customer experience over cost" | Choosing lower FP rate Bloom filter at higher memory cost |
| **Bias for Action** | "When did you make a quick decision without full data?" | Using probabilistic DS when exact count not needed |
| **Dive Deep** | "Describe a time you got to the root cause" | Debugging RB tree rotation bug causing O(n) insertion |
| **Are Right, A Lot** | "When were you wrong about a technical decision?" | Using segment tree where BIT would suffice |
| **Deliver Results** | "Describe your most impactful technical achievement" | Merkle tree sync reducing replication time 10x |
| **Think Big** | "How did you solve a problem at scale?" | Tiered trie + Bloom filter for multi-tenant autocomplete |
| **Learn and Be Curious** | "What's something you taught yourself?" | Implementing implicit treap for collaborative editing |

**Sample Amazon STAR:**

> **Situation:** During the Prime Day sale, our recommendation engine cache kept stampeding — 1000s of concurrent cache misses for the same hot products caused DB overload.
> **T:** I needed to implement a cache stampede prevention mechanism in 48 hours before the next sale.
> **A:** I added a request-level Bloom filter per cache key. When a request misses the cache, before querying the DB, it checks the per-key Bloom filter. If the Bloom filter says the product data "probably exists" and the cache is empty, the request knows another request is already fetching it, so it waits (short spin-loop). This prevents 99% of concurrent cache misses.
> **R:** DB load during Prime Day dropped 85%. P99 latency fell from 800ms to 45ms. The solution handled 500K QPS with zero data inconsistency.

### Meta (Facebook): Leadership & Culture

| Question | DS Context |
|----------|------------|
| "Tell me about a time you had impact at scale" | Trie serving 100M autocomplete QPS |
| "Describe a conflict about technical approach" | RB tree vs skip list vs treap debate |
| "How do you handle competing priorities?" | Time allocation for learning advanced DS |
| "Tell me about a time you built something from scratch" | Implementing segment tree for internal analytics |
| "When did you have to simplify a complex system?" | Replacing suffix tree with suffix array for DNA search |

**Sample Meta STAR:**

> **Situation:** The search autocomplete feature for Facebook Groups was using PostgreSQL ILIKE queries. As we grew to 500M groups, latency exceeded 500ms with frequent timeouts.
> **T:** I needed to build a low-latency suggestion system that could handle 200K QPS with <20ms response time.
> **A:** I led a team to implement a distributed trie system. Each trie node stored character → child mapping and top 10 suggestions by frequency. We sharded tries by locale (en, es, ar, etc.) across 50 machines. Bloom filters at the load balancer level filtered out non-existent prefixes before hitting the trie tier. The trie was rebuilt every 5 minutes from the group metadata stream.
> **R:** P50 latency dropped to 3ms, P99 to 15ms. We reduced the search cluster from 200 to 50 machines, saving $3M/year. The feature supported 10M suggestions served per day.

### Microsoft: Growth Mindset & Collaboration

| Question | DS Context |
|----------|------------|
| "Tell me about a time you learned something new" | Learning Fenwick tree for binary indexed operations |
| "How do you collaborate with others?" | Code review of a segment tree implementation |
| "Describe a time you received critical feedback" | Optimising overly complex treap to simpler BIT |
| "Tell me about a time you had to convince someone" | Proving Bloom filter reliability vs team's concern about FP |
| "How do you handle ambiguous situations?" | Deciding between segment tree and Fenwick tree for a problem |

**Sample Microsoft STAR:**

> **Situation:** Our Azure Cosmos DB team was investigating high RU (request unit) consumption for range queries on time-series IoT data.
> **T:** I needed to find a way to reduce query cost without changing the data model.
> **A:** I ran experiments comparing segment tree with lazy propagation vs multiple point queries. I built a prototype segment tree in the Azure Functions compute layer. The segment tree pre-aggregated hourly, daily, and weekly ranges. Queries resolved against the segment tree rather than scanning all records. I presented the results with a cost-benefit analysis showing 14x RU reduction.
> **R:** The segment tree approach was adopted across 5 teams. Average RU cost for range queries dropped from 1200 to 85. Customer billing impact was significant — top 10 customers saw 30-50% cost reduction.

### Apple: Passion & Product Quality

| Question | DS Context |
|----------|------------|
| "Tell me about a time you focused on quality" | Ensuring Merkle tree hash collisions don't occur |
| "Describe your proudest achievement" | Building on-device trie for Spotlight search |
| "How do you approach privacy in your work?" | On-device Bloom filter vs cloud lookup |
| "Tell me about a time you improved performance" | Replacing string compare with suffix array in diff tool |
| "How do you work with cross-functional teams?" | Teaching non-software teams about Merkle tree for firmware integrity |

**Sample Apple STAR:**

> **Situation:** The iOS keyboard autocorrect feature had high latency on older devices, causing visible stutter during typing. The trie used for word lookup was rebuilt each keystroke.
> **T:** I needed to reduce trie rebuild latency from 8ms to <1ms for a 60fps typing experience.
> **A:** I implemented a persistent (immutable) trie using path copying. The trie was built on a background serial queue. Each new word addition created a new root reference atomically swapped with old root. This made rebuild O(1) from the keyboard's perspective — the trie snapshot was always consistent. I also added a Bloom filter for common 5000 English words to skip the trie entirely for 80% of lookups.
> **R:** Keyboard latency dropped from 8ms to 0.3ms on iPhone 6S. The change shipped in iOS 14. We measured 1M+ keystrokes before any autocorrect delay reported.

## General Behavioral Template

```
S — [Project/System name] was experiencing [problem].
    It served [scale] with [specific metric] constraint.
T — I was responsible for [role]. The challenge was [specific difficulty].
A — I chose [DS] because [reason].
    Implementation steps:
    1. [First: measure/baseline]
    2. [Second: implement DS]
    3. [Third: test at scale]
    4. [Fourth: deploy and monitor]
    The trade-off was [specific trade-off], which we monitored via [metric].
R — [Quantitative result] improvement in [metric].
    [Business impact, if applicable].
```

## Common DS Behavioral Scenarios

| Scenario | Best DS to Mention |
|----------|-------------------|
| "Optimised slow search" | Trie / Suffix Array |
| "Reduced memory footprint" | Bloom Filter |
| "Improved data integrity" | Merkle Tree |
| "Handled concurrent access" | Skip List / Lock-free structure |
| "Reduced query complexity" | Fenwick Tree / Segment Tree |
| "Built a recommendation system" | Treap (order statistics) |
| "Designed a social graph feature" | Union-Find |
| "Built an autocomplete system" | Trie + Bloom Filter |
| "Designed file sync system" | Merkle Tree |
| "Built a fraud detection system" | Bloom Filter + Trie |

## Red Flags to Avoid

1. **Not quantifying results**: "It was faster" vs "Latency dropped from 200ms to 3ms"
2. **Not explaining why DS was chosen**: "We used a Bloom filter because..." (explain trade-off)
3. **Not owning mistakes**: "The bug was in..." (show learning, not blame)
4. **Not mentioning constraints**: Every DS decision is constraint-driven
5. **Too theoretical**: Show you shipped it, not just designed it

## Preparation Checklist

- [ ] Prepare 2 STAR stories per company LP
- [ ] Each story mentions at least one advanced DS by name
- [ ] Quantify results (before/after numbers)
- [ ] Practice saying stories out loud (<2 min each)
- [ ] Prepare 1 story about a failure/learning
- [ ] Prepare 1 story about conflict resolution
- [ ] Research company-specific products that use these DS
- [ ] Practice with a friend doing mock behavioral interview