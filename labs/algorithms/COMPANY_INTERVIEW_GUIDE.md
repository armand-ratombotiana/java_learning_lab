# Company Interview Guide — Algorithm-Heavy Roles

> Comprehensive interview process guide for algorithm-intensive positions at top technology and quantitative firms. Covers rounds, timelines, focus areas, real interview stories, and compensation.

---

## Table of Contents
1. [Google](#google)
2. [Microsoft](#microsoft)
3. [Amazon](#amazon)
4. [Meta (Facebook)](#meta-facebook)
5. [Apple](#apple)
6. [Netflix](#netflix)
7. [Uber](#uber)
8. [Stripe](#stripe)
9. [Two Sigma](#two-sigma)
10. [Palantir](#palantir)
11. [Citadel](#citadel)
12. [Jane Street](#jane-street)
13. [Bloomberg](#bloomberg)
14. [TikTok (ByteDance)](#tiktok-bytedance)

---

## Google

### Interview Process
| Round | Type | Duration | Algorithm Focus |
|-------|------|----------|-----------------|
| Phone Screen | Coding + Algorithms | 45 min | Medium LC, data structures, problem decomposition |
| Onsite R1 | Algorithm Design | 45 min | DP, graphs, greedy with complex constraints |
| Onsite R2 | Data Structures | 45 min | Trees, hash maps, heaps, segment trees, tries |
| Onsite R3 | System Design / Coding | 45 min | Large-scale algorithm design for distributed systems |
| Onsite R4 | Googleyness + Leadership | 45 min | Behavioral + algorithmic decision-making scenarios |
| Onsite R5 (optional) | Extra Coding | 45 min | Hard LC, NP-hard reductions, approximation reasoning |

### Timeline
Application → Recruiter screen (1-2 weeks) → Phone screen (1 week) → Onsite scheduling (2-4 weeks) → Onsite → HC review (1-2 weeks) → Offer/Rejection (1 week). Total: 4-10 weeks.

### Algorithm Focus Areas
- **Dynamic Programming**: Knapsack, LCS, LIS, matrix DP, DP with bitmasking
- **Graph Algorithms**: BFS/DFS, Dijkstra, Topological sort, union-find, strongly connected components
- **String Algorithms**: KMP, Rabin-Karp, suffix arrays, Z-algorithm
- **Greedy**: Interval scheduling, Huffman, activity selection with proofs
- **Trees**: Segment trees, Fenwick trees, LCA, binary tree properties
- **Probability & Math**: Expected value, reservoir sampling, random algorithms

### Interview Stories
1. **SDE (L4), Mountain View**: "Phone screen asked 'Design a time-based key-value store supporting set/get with timestamps.' Solved with TreeMap + HashMap. Onsite had 'Find minimum number of refueling stops' — pure DP with greedy optimization."
2. **SWE, Zurich**: "Asked 'Word Break II' with memoization — DFS + DP combo. Follow-up involved enormous input and needed pruning. Second round: 'Alien Dictionary' topological sort on character ordering."
3. **Research Engineer**: "Onsite question: 'Given a billion URLs, find the top-10 most frequent.' MapReduce-style solution expected. Discussed tradeoffs of hash-based vs trie-based partitioning."

### Tips
- Master DP — it appears in nearly every Google interview. Practice 30+ DP problems before interviewing.
- Focus on optimal time & space complexity analysis — Google expects exact Big-O and justification.
- Demonstrate problem decomposition: think out loud, state brute-force first, then optimize iteratively.
- Practice on a Google Doc — that is the coding environment for phone screens.
- For HC (Hiring Committee), ensure behavioral answers tie back to algorithmic impact and leadership.

### Compensation (Algorithm-Heavy Roles)
| Level | Base | Bonus | RSU (4yr) | TC (1st yr) |
|-------|------|-------|-----------|-------------|
| L3 (SWE II) | $130-160k | $20-35k | $100-200k | $175-220k |
| L4 (SWE III) | $160-210k | $30-60k | $200-400k | $240-310k |
| L5 (Senior) | $200-260k | $50-100k | $400-800k | $350-480k |
| L6 (Staff) | $250-320k | $70-120k | $800-1.5M | $500-750k |

---

## Microsoft

### Interview Process
| Round | Type | Duration | Algorithm Focus |
|-------|------|----------|-----------------|
| Phone Screen | Technical Screening | 30-45 min | LC Medium: sorting, trees, string manipulation |
| Onsite R1 | Algorithm Design | 45 min | Tree/Graph problems, recursion, divide-and-conquer |
| Onsite R2 | Data Structures & Coding | 45 min | Arrays, strings, hash maps, linked lists |
| Onsite R3 | System Design / Architecture | 45 min | Algorithm selection for large systems |
| Onsite R4 | Behavioral + Technical | 45 min | Algorithm decision-making, trade-off reasoning |

### Timeline
Application → Recruiter call (1 week) → Phone screen (1 week) → Onsite (2-4 weeks) → Offer decision (1-2 weeks). Total: 4-8 weeks.

### Algorithm Focus Areas
- **Trees & Binary Search Trees**: In-order traversal, balancing, LCA, Morris traversal
- **Sorting & Searching**: Merge sort, quicksort, binary search variants, count inversions
- **String Algorithms**: Pattern matching, palindrome, substring problems, KMP
- **Divide & Conquer**: Closest pair, Strassen's, max subarray
- **Dynamic Programming**: Memoization-heavy, interval DP, DP on trees
- **Recursion/Backtracking**: Permutations, subsets, N-Queens, Sudoku solver

### Interview Stories
1. **SDE, Redmond**: "Phone screen: 'Serialize and deserialize a binary tree.' Used BFS with level-order traversal. Onsite round: 'LRU Cache' — expected combination of HashMap + Doubly Linked List."
2. **SWE, Vancouver**: "Onsite asked 'Find k closest points to origin using divide and conquer.' Discussed quicksort-based partitioning vs heap approach. Later: 'Word ladder' — BFS shortest path in graph of words."
3. **Azure Engineer**: "Algorithm round: 'Design a consistent hash ring for distributed caching.' Asked about virtual nodes, partitioning strategies, and replication."

### Tips
- Microsoft heavily tests tree and string algorithms. Practice tree traversals and pattern matching.
- Prepare for multiple interviewers from the same team — questions may overlap.
- Coding environment is typically Microsoft Whiteboard (in-person) or OneNote (remote).
- Know Windows-ecosystem internals but algorithm questions remain language-agnostic.
- Behavioral rounds often ask about conflict resolution in algorithm choices within teams.

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC (1st yr) |
|-------|------|-------|-----------|-------------|
| 59-60 | $100-140k | $15-30k | $80-160k | $135-190k |
| 61-62 | $130-180k | $25-50k | $150-300k | $190-280k |
| 63-64 | $160-220k | $40-80k | $300-600k | $280-420k |
| 65-66 | $190-280k | $60-120k | $500-1M | $400-600k |

---

## Amazon

### Interview Process
| Round | Type | Duration | Algorithm Focus |
|-------|------|----------|-----------------|
| Phone Screen | Technical + LP | 45 min | LC Medium: arrays, strings, hash maps |
| Onsite R1 | Algorithm Design | 45-60 min | Data structures, recursion, greedy algorithms |
| Onsite R2 | Algorithm Design | 45-60 min | DP, graph traversal, optimization problems |
| Onsite R3 | System Design | 45-60 min | Scalable algorithm design (distributed) |
| Onsite R4 | Bar Raiser (LP + Tech) | 45-60 min | Leadership principles + algorithm decisions |

### Timeline
Application → OA or Recruiter screen (1 week) → Phone screen (1 week) → Onsite (2-3 weeks) → Offer (1 week). Total: 4-7 weeks.

### Algorithm Focus Areas
- **Arrays & Strings**: Two-pointer, sliding window, prefix sum, string manipulation
- **Dynamic Programming**: 1D/2D DP, stock problems, knapSack variants, longest common subsequence
- **Greedy Algorithms**: Interval scheduling, task scheduling, Huffman coding
- **Graph Algorithms**: BFS, DFS, Dijkstra, MST (Kruskal/Prim), topological sort
- **Heap/Priority Queue**: K-th largest, merge k sorted lists, median in stream
- **Simulation**: Complex problem simulation with multiple data structures

### Interview Stories
1. **SDE II, Seattle**: "Phone screen: 'Number of islands' — standard BFS/DFS with union-find follow-up. Onsite: 'Design a parking lot algorithm' — expected OOP with optimal slot-finding using min-heap per size."
2. **Applied Scientist, ML**: "Algorithm round: 'Find optimal meeting point minimizing Manhattan distance' — median in 2D grid. Discussed proof of why median minimizes L1 distance."
3. **SDE, AWS**: "Onsite bar raiser: 'Top K frequent words in a stream' — designed trie + min-heap combination. Detailed trade-off analysis of counting vs sampling approaches."

### Tips
- Amazon emphasizes leadership principles (LP) in every round — prepare LP stories about algorithm decisions, trade-offs, and failures.
- Expect component-based system design with algorithm-heavy components (e.g., cache eviction, rate limiting).
- Coding is done on a shared IDE (LiveCode or similar). No auto-complete.
- Master sliding window and two-pointer patterns — they are Amazon favorites.
- Use STAR format even for technical algorithm discussions.

### Compensation
| Level | Base | Bonus (1st yr) | RSU (4yr) | TC (1st yr) |
|-------|------|----------------|-----------|-------------|
| L4 (SDE I) | $110-150k | $25-50k | $80-160k | $155-220k |
| L5 (SDE II) | $140-190k | $40-80k | $160-350k | $220-340k |
| L6 (SDE III) | $170-240k | $60-120k | $350-700k | $320-500k |
| L7 (Principal) | $220-300k | $100-200k | $700-1.5M | $500-800k |

---

## Meta (Facebook)

### Interview Process
| Round | Type | Duration | Algorithm Focus |
|-------|------|----------|-----------------|
| Phone Screen | Coding | 45 min | LC Medium: arrays, strings, recursion |
| Onsite R1 | Algorithm Problem Solving | 45 min | DP, graphs, backtracking, combinatorics |
| Onsite R2 | Algorithm Problem Solving | 45 min | Data structures, greedy, recursion |
| Onsite R3 | System Design | 45 min | Large-scale algorithm system design |
| Onsite R4 | Behavioral | 45 min | Technical leadership, impact, algorithm decisions |

### Timeline
Application → Recruiter call (1-2 weeks) → Phone screen (1 week) → Onsite (2-4 weeks) → Offer (1-2 weeks). Total: 5-9 weeks.

### Algorithm Focus Areas
- **Graph Algorithms**: BFS/DFS, shortest path, connected components, graph coloring, bipartite matching
- **Dynamic Programming**: Subset DP, combinatorics DP, DP with state compression
- **Backtracking**: Permutations, combinations, N-Queens, constraint satisfaction problems
- **String Algorithms**: Palindrome, substring, pattern matching with KMP/Rabin-Karp
- **Binary Search**: Search in rotated array, finding peaks, search space optimization
- **Trie & Advanced DS**: Prefix trees, union-find with path compression

### Interview Stories
1. **SWE, Menlo Park**: "Phone screen: 'Minimum window substring' — sliding window with hash map counter. Onsite: 'Merge intervals' followed by 'Insert interval' — both solved with sorting and linear scan."
2. **Production Engineer**: "Algorithm round: 'Design a URL shortening service' — base-62 conversion, hash collision resolution, consistent hashing for distributed DB."
3. **Research Scientist**: "Asked about community detection in graphs — Louvain algorithm modifications. Discussed complexity and scalability to billion-node graphs."

### Tips
- Meta interviews are algorithm-heavy with 2 coding rounds versus 1 system design.
- Focus on speed — you need to solve 2 problems per 45-minute round.
- Coding environment is CoderPad (no auto-complete). Practice writing syntactically correct code.
- Meta heavily asks graph problems and backtracking — be very comfortable with adjacency lists and DFS/BFS.
- Behavioral questions probe impact: always quantify algorithm improvements (e.g., "reduced latency by 60%").

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC (1st yr) |
|-------|------|-------|-----------|-------------|
| E3 (SWE) | $110-150k | $15-30k | $80-160k | $145-220k |
| E4 (SWE) | $150-200k | $25-50k | $200-400k | $225-340k |
| E5 (Senior) | $180-250k | $40-80k | $400-800k | $320-500k |
| E6 (Staff) | $220-320k | $60-120k | $800-1.6M | $480-750k |

---

## Apple

### Interview Process
| Round | Type | Duration | Algorithm Focus |
|-------|------|----------|-----------------|
| Recruiter Screen | Technical Discussion | 30 min | Background in algorithms, domain match |
| Phone Screen | Coding + Algorithms | 45-60 min | LC Medium: data structures, recursion |
| Onsite R1 | Algorithm Design | 45 min | System-specific algorithm (OS, ML, graphics) |
| Onsite R2 | Coding + Data Structures | 45 min | Trees, graphs, hash maps, performance analysis |
| Onsite R3 | System Design | 45 min | Algorithm architecture for hardware/software |
| Onsite R4 | Manager + Cross-functional | 45 min | Behavioral + team-specific algorithm knowledge |

### Timeline
Application → Recruiter (1-2 weeks) → Phone screen (1-2 weeks) → Onsite (2-6 weeks) → Offer (1-3 weeks). Teams vary significantly. Total: 5-12 weeks.

### Algorithm Focus Areas
- **Tree Algorithms**: Binary trees, B-trees, R-trees, spatial indexing algorithms
- **Sorting & Searching**: Optimized sort variants, cache-aware algorithms, external sorting
- **Geometry Algorithms**: Computational geometry for graphics/AR teams
- **Machine Learning Algorithms**: If ML team: regression, neural networks, optimization
- **DP + Combinatorics**: For performance-critical applications
- **Low-Level Algorithms**: Bit manipulation, memory-efficient algorithms, SIMD-aware design

### Interview Stories
1. **SDE, Core OS**: "Algorithm round: 'Implement spin-lock with minimal memory barriers' — low-level concurrency with detailed memory model discussion. Follow-up: 'Lock-free queue with CAS operations.'"
2. **ML Engineer, Siri**: "Phone screen: 'Text similarity using TF-IDF and cosine similarity' — algorithm-heavy with complexity analysis. Onsite: 'Design a real-time recommendation system' — matrix factorization + locality-sensitive hashing."
3. **Graphics Engineer**: "Onsite asked 'Bresenham line drawing algorithm' derivation. Later: 'Quadtree for spatial queries' with nearest-neighbor search."

### Tips
- Apple interviews are team-specific — research your team's algorithm domain deeply.
- Practical algorithm efficiency matters: expect questions about cache performance, memory bandwidth, and instruction-level optimization.
- Coding environment is whiteboard (in-person) or shared document (remote).
- Apple values craft and simplicity — clean, well-structured solutions matter.
- Prepare for questions at the intersection of algorithms and hardware constraints.

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC (1st yr) |
|-------|------|-------|-----------|-------------|
| ICT2 | $110-150k | $10-25k | $60-140k | $135-200k |
| ICT3 | $140-190k | $20-40k | $140-300k | $195-300k |
| ICT4 | $170-240k | $30-60k | $300-600k | $275-450k |
| ICT5 | $200-300k | $50-100k | $600-1.2M | $400-650k |

---

## Netflix

### Interview Process
| Round | Type | Duration | Algorithm Focus |
|-------|------|----------|-----------------|
| Recruiter Screen | Fit + Background | 30 min | Algorithm experience evaluation |
| Technical Phone | Coding + Algorithms | 45 min | LC Medium: data structures, runtime analysis |
| Onsite R1 | Algorithm Deep Dive | 60 min | Hard algorithm problem with optimization |
| Onsite R2 | System Design | 60 min | Algorithm-driven system design |
| Onsite R3 | Team Collaboration | 60 min | Algorithm decision-making in group setting |
| Onsite R4 | Executive / Cultural | 45 min | Behavioral with algorithm trade-off values |

### Timeline
Application → Recruiter (1-2 weeks) → Phone screen (1-2 weeks) → Onsite (2-4 weeks) → Offer (1-2 weeks). Total: 5-9 weeks.

### Algorithm Focus Areas
- **Caching Algorithms**: LRU, LFU, ARC, 2Q, write-through vs write-back policies
- **Recommendation Algorithms**: Collaborative filtering, matrix factorization, bandit algorithms
- **Content Delivery Algorithms**: Consistent hashing, CDN routing, video chunking strategies
- **Encoding Algorithms**: Compression, bitrate optimization, adaptive bitrate streaming
- **Distributed Algorithms**: Leader election, Paxos/Raft, gossip protocols
- **Streaming Algorithms**: Count-Min Sketch, HyperLogLog, heavy hitters

### Interview Stories
1. **Platform Engineer**: "Algorithm round: 'Design a cache eviction policy that handles variable-sized items with expiration.' Designed ARC variant with size-aware eviction."
2. **ML Engineer, Recommendations**: "Onsite: 'Design a real-time personalized ranking algorithm.' Discussed Bayesian bandits, Thompson sampling, and cold-start strategies with matrix factorization."
3. **CDN Engineer**: "Algorithm deep dive: 'Optimize video chunk selection for varying network conditions.' Proposed adaptive bitrate algorithm with throughput prediction."

### Tips
- Netflix values freedom and responsibility — demonstrate ownership of algorithm decisions.
- Focus on caching and streaming algorithms — they are Netflix-specific favorites.
- No whiteboarding in remote interviews — use an online collaborative editor.
- Compensation is typically front-loaded with cash instead of RSU-heavy.
- Prepare to discuss trade-offs openly — Netflix expects intellectual honesty about algorithm limitations.

### Compensation
| Role | Base | TC (typical) |
|------|------|-------------|
| SWE (Mid) | $250-400k | $250-400k |
| SWE (Senior) | $350-600k | $350-700k |
| Staff | $450-800k | $450-900k |
| (Netflix offers mostly cash, no standard RSU structure) |

---

## Uber

### Interview Process
| Round | Type | Duration | Algorithm Focus |
|-------|------|----------|-----------------|
| Phone Screen | Coding | 45 min | LC Medium: arrays, graphs, string manipulation |
| Onsite R1 | Algorithm Problem Solving | 45 min | Graph algorithms, shortest path, optimization |
| Onsite R2 | Algorithm Problem Solving | 45 min | DP, greedy, data structure design |
| Onsite R3 | System Design | 45 min | Geospatial algorithms, dispatch/routing systems |
| Onsite R4 | Behavioral + Manager | 45 min | Algorithm decisions under ambiguity |

### Timeline
Application → Recruiter (1-2 weeks) → Phone screen (1 week) → Onsite (2-3 weeks) → Offer (1 week). Total: 4-7 weeks.

### Algorithm Focus Areas
- **Graph Algorithms**: Dijkstra, A*, Bellman-Ford, Floyd-Warshall, shortest path variants
- **Geospatial Algorithms**: KD-trees, R-trees, Voronoi diagrams, nearest neighbor, geohashing
- **Optimization Algorithms**: Assignment problems, bipartite matching, Hungarian algorithm
- **DP + Knapsack**: Resource allocation, ride dispatch optimization
- **Randomized Algorithms**: Monte Carlo for ETA estimation, reservoir sampling
- **String Matching**: Address matching, fuzzy search, Levenshtein distance

### Interview Stories
1. **Backend Engineer, Maps**: "Phone screen: 'Design a nearest driver finder' — used quadtree for spatial indexing with Manhattan distance heuristic."
2. **Data Scientist, Marketplace**: "Onsite: 'Match riders to drivers in a surge zone' — Hungarian algorithm for assignment with dynamic pricing constraints. Discussed time complexity vs approximation."
3. **Platform Engineer**: "Algorithm round: 'Bike rebalancing problem' — modeled as min-cost flow. Discussed scalability to 500+ bike stations."

### Tips
- Uber heavily focuses on graph and geospatial algorithms. Practice A* and Voronoi.
- Expect optimization problems with real-world constraints (latency, cost, capacity).
- Coding environment is CodeSignal or CoderPad.
- Show understanding of trade-offs between exact vs approximate solutions.
- Uber values hustle — demonstrate urgency in algorithm optimization.

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC (1st yr) |
|-------|------|-------|-----------|-------------|
| L4 (SWE I) | $120-160k | $15-30k | $100-200k | $160-240k |
| L5 (SWE II) | $150-210k | $25-50k | $200-400k | $225-350k |
| L6 (Senior) | $180-260k | $40-80k | $400-800k | $320-500k |

---

## Stripe

### Interview Process
| Round | Type | Duration | Algorithm Focus |
|-------|------|----------|-----------------|
| Phone Screen | Coding | 45 min | LC Medium: data structures, string algorithms |
| Onsite R1 | Algorithm / Coding | 60 min | Hard algorithm with multiple constraints |
| Onsite R2 | System Design | 60 min | Payment algorithm design, consistency models |
| Onsite R3 | Debugging + Integration | 60 min | Algorithm correctness, edge case reasoning |
| Onsite R4 | Behavioral + API Design | 45 min | Communication, collaboration on algorithm design |

### Timeline
Application → Recruiter (1-2 weeks) → Phone screen (1 week) → Onsite (2-4 weeks) → Offer (1-2 weeks). Total: 4-8 weeks.

### Algorithm Focus Areas
- **Hashing & Encryption**: Consistent hashing, cryptographic hash algorithms, HMAC
- **Distributed Algorithms**: Paxos/Raft consensus, distributed counters, idempotency keys
- **Math Algorithms**: Modular arithmetic, elliptic curves, number theory basics
- **String Algorithms**: Tokenization, parsing, regex-like pattern matching
- **Concurrency Algorithms**: Lock-free data structures, optimistic concurrency
- **Rate Limiting Algorithms**: Token bucket, sliding window, leaky bucket

### Interview Stories
1. **SWE, Payments**: "Phone screen: 'Design an idempotency key system' — discussed hash-based key generation, TTL-based expiration, and race condition handling."
2. **Platform Engineer**: "Onsite: 'Implement a distributed rate limiter' — sliding window with Redis sorted sets. Follow-up: consistent hashing for multi-region deployment."
3. **Infrastructure Engineer**: "Algorithm deep dive: 'Design a system for real-time duplicate payment detection' — bloom filters with false positive analysis and reconciliation strategy."

### Tips
- Stripe emphasizes correctness and edge cases — write thorough code with boundary checks.
- API design and system design rounds require strong algorithm foundations.
- Coding environment is a collaborative editor with no auto-complete.
- Stripe values precise written communication — write clear, well-commented code.
- Understand distributed systems algorithms deeply: consensus, replication, partitioning.

### Compensation
| Level | Base | RSU (4yr) | TC (1st yr) |
|-------|------|-----------|-------------|
| L2 (SWE) | $130-170k | $200-400k | $180-280k |
| L3 (SWE) | $160-220k | $400-800k | $260-420k |
| L4 (Senior) | $200-290k | $800-1.6M | $400-600k |

---

## Two Sigma

### Interview Process
| Round | Type | Duration | Algorithm Focus |
|-------|------|----------|-----------------|
| Phone Screen | Algorithms + Probability | 45 min | Math + probability + LC Medium algorithms |
| Online Assessment | Coding Challenge | 90-120 min | Multi-step algorithm problems with math component |
| Onsite R1 | Algorithm Design | 45 min | Probability + DP + combinatorics |
| Onsite R2 | System Design / Distributed | 45 min | Distributed algorithms for trading systems |
| Onsite R3 | Statistical Modeling | 45 min | Statistics, probability, hypothesis testing |
| Onsite R4 | Behavioral + Culture | 45 min | Algorithm research, collaboration, intellectual curiosity |

### Timeline
Application → Recruiter (1-2 weeks) → Phone screen (1 week) → OA (1 week) → Onsite (2-4 weeks) → Offer (1-2 weeks). Total: 5-9 weeks.

### Algorithm Focus Areas
- **Probability & Statistics**: Expected value, variance, Markov chains, Monte Carlo methods
- **Math Algorithms**: Number theory, modular arithmetic, combinatorics, linear algebra
- **Randomized Algorithms**: Random sampling, shuffle, Monte Carlo, Las Vegas
- **Dynamic Programming**: Probability DP, expected value DP, combinatorial DP
- **Game Theory**: Nash equilibrium, minimax, optimal strategy under uncertainty
- **Numerical Algorithms**: Floating-point precision, numerical optimization, gradient descent

### Interview Stories
1. **Quantitative Researcher**: "Phone screen: 'Expected number of coin flips to get HTH sequence.' Solved with Markov chain absorption probability. Onsite: 'Design a Monte Carlo algorithm for option pricing.'"
2. **Software Engineer**: "OA: 'Maximum subarray sum after k concatenations' — DP with modulo handling. Onsite: 'Russian doll envelopes' — LIS with custom comparator."
3.**Quant Developer**: "Onsite: 'Given an unfair die, design an algorithm to generate uniform random numbers using rejection sampling.' Discussed optimal acceptance rate."

### Tips
- Two Sigma interviews are math-heavy. Refresh probability, linear algebra, and combinatorics.
- Prepare for phone screens with probability puzzles (e.g., birthday problem, Monty Hall, dice rolls).
- Coding environment is typically HackerRank for OA, whiteboard for on-site.
- Expect at least one round on distributed systems algorithms (Paxos, consistent hashing, logical clocks).
- Demonstrate intellectual curiosity — discuss reading papers, algorithm research, side projects.

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC (1st yr) |
|-------|------|-------|-----------|-------------|
| Associate | $120-160k | $30-60k | N/A | $150-250k |
| VP | $160-220k | $60-120k | N/A | $220-380k |
| Director | $200-300k | $100-300k | N/A | $350-600k |

---

## Palantir

### Interview Process
| Round | Type | Duration | Algorithm Focus |
|-------|------|----------|-----------------|
| Phone Screen | Coding + Problem Solving | 45 min | LC Medium: data structures, graph algorithms |
| Phone Screen R2 | Algorithm Design | 45 min | Optimization, system modeling, algorithm selection |
| Onsite R1 | Decomposition | 60 min | Break down ambiguous problem into algorithm components |
| Onsite R2 | Algorithm Coding | 60 min | Hard LC: graphs, DP, optimization under constraints |
| Onsite R3 | System Design | 60 min | Large-scale algorithm design for intelligence platforms |
| Onsite R4 | Integration + Collaboration | 60 min | Pair programming on algorithm problem |
| Onsite R5 | Behavioral + Product | 45 min | Mission alignment, ethical algorithm design |

### Timeline
Application → Recruiter (1-2 weeks) → Phone screens (2-3 weeks) → Onsite (3-6 weeks) → Offer (1-2 weeks). Total: 6-12 weeks.

### Algorithm Focus Areas
- **Graph Algorithms**: Community detection, centrality measures, shortest path, graph coloring
- **Optimization Algorithms**: Integer programming, constraint satisfaction, greedy approximations
- **Trie & String Algorithms**: Pattern matching on large text corpora, fuzzy search
- **Geospatial Algorithms**: Clustering (DBSCAN, k-means), spatial joins, trajectory analysis
- **Data Structures**: Union-find, segment trees, Fenwick trees, bloom filters
- **Online Algorithms**: Stream processing, anomaly detection in real-time data

### Interview Stories
1. **Forward Deployed Engineer**: "Decomposition round: 'Design a system to detect supply chain fraud from shipping data.' Designed graph-based anomaly detection with PageRank-like centrality."
2. **Software Engineer, Gotham**: "Onsite: 'Disjoint set union with rollback operations' — supporting undo operations. Follow-up: 'Dynamic graph connectivity with offline queries using divide-and-conquer.'"
3. **Deployment Strategist**: "Integration round: 'Build a constraint-satisfaction solver for military logistics.' Discussed SAT solver basics, clause learning, and optimization heuristics."

### Tips
- Palantir values ability to decompose vague, real-world problems into algorithmic components.
- Graph algorithms are central — master union-find, shortest path, MST, and graph clustering.
- Coding environment is CoderPad or CodeSignal. Expect pair programming-style collaboration.
- Prepare for a "decomposition" round where the interviewer deliberately gives ambiguous requirements.
- Demonstrate mission alignment — understand how algorithms apply to Palantir's defense/intelligence domain.

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC (1st yr) |
|-------|------|-------|-----------|-------------|
| SWE (L1) | $120-160k | $15-30k | $200-400k | $190-290k |
| SWE (L2) | $150-210k | $25-50k | $400-800k | $275-460k |
| SWE (L3) | $190-270k | $40-80k | $800-1.6M | $430-670k |

---

## Citadel

### Interview Process
| Round | Type | Duration | Algorithm Focus |
|-------|------|----------|-----------------|
| Recruiter Screen | Math + Background | 30 min | Quick math/algorithm experience check |
| Phone Screen | Algorithms + Probability | 45-60 min | Hard algorithm problems combining math and CS |
| Onsite R1 | Algorithm Design | 45 min | Extremely hard LC + probability/math twist |
| Onsite R2 | System Design | 45 min | High-frequency trading system algorithms |
| Onsite R3 | Quantitative Reasoning | 45 min | Probability puzzles, game theory, expected value |
| Onsite R4 | Behavioral + Fit | 45 min | Competitive drive, intellectual intensity |

### Timeline
Application → Recruiter (1 week) → Phone screen (1-2 weeks) → Onsite (2-3 weeks) → Offer (1 week). Total: 4-7 weeks.

### Algorithm Focus Areas
- **Probability & Expected Value**: Random processes, martingales, Brownian motion basics
- **Game Theory**: Nash equilibrium, optimal strategy, adversarial search
- **Math Algorithms**: Number theory, combinatorics, modular arithmetic
- **Graph Algorithms**: Shortest path on large graphs, flow networks
- **DP + Optimization**: Trading strategy optimization, risk minimization DP
- **Low-Latency Algorithms**: Cache-optimized algorithms, branch prediction, SIMD

### Interview Stories
1. **Quant Developer**: "Phone screen: 'Simulate a dice game with optimal stopping' — solved with Bellman equation backward induction. Onsite: 'Design a limit order book' — price-time priority matching algorithm with O(log n) operations."
2. **SWE, Core Systems**: "Onsite: 'Generate all valid bracket sequences with constraints' — backtracking with pruning. Follow-up: 'Find arbitrage in currency exchange rates using Bellman-Ford.'"
3. **Quantitative Researcher**: "Onsite quantitative round: 'You have a biased coin — design an algorithm to produce a fair coin toss.' Then: 'What's the expected number of flips?'"

### Tips
- Citadel interviews are extremely challenging — expect harder-than-FAANG algorithm questions.
- Probability and math fundamentals are non-negotiable. Master expected value calculations.
- Coding environment is typically whiteboard. Write clean, fast, correct code.
- Prepare for a quantitative puzzle round separate from coding.
- Show competitive drive — Citadel values candidates who push for optimal solutions under time pressure.

### Compensation
| Level | Base | Bonus | TC (1st yr) |
|-------|------|-------|-------------|
| Associate | $120-180k | $50-150k | $200-350k |
| VP | $180-250k | $150-400k | $350-650k |
| Director | $250-400k | $300-800k | $600-1.2M |

---

## Jane Street

### Interview Process
| Round | Type | Duration | Algorithm Focus |
|-------|------|----------|-----------------|
| Recruiter Screen | Math + Stats | 30 min | Quick assessment of mathematical maturity |
| Phone Screen | Probability + Algorithms | 60 min | Probability puzzles with algorithm components |
| Onsite R1 | Trading Game | 45 min | Live probability/expected-value game with interviewers |
| Onsite R2 | Algorithm Coding | 45 min | Functional programming style, recursion, math-heavy |
| Onsite R3 | Probability + Statistics | 45 min | Advanced probability, stochastic processes |
| Onsite R4 | System Design / Architecture | 45 min | Low-latency trading system algorithms |

### Timeline
Application → Recruiter (1 week) → Phone screen (1-2 weeks) → Onsite invitation (1-2 weeks) → Onsite → Offer (1 week). Total: 4-8 weeks.

### Algorithm Focus Areas
- **Probability & Statistics**: Random variables, expected value, law of large numbers, central limit theorem
- **Combinatorics**: Counting, generating functions, combinatorial identities
- **Functional Algorithms**: Recursion, pattern matching, immutability (OCaml preferred)
- **Game Theory**: Prisoner's dilemma, optimal strategy, minimax, adversarial reasoning
- **Mathematical Optimization**: Convex optimization, gradient-based methods
- **Number Theory**: Prime distribution, modular arithmetic, cryptographic foundations

### Interview Stories
1. **Quantitative Trader**: "Phone screen: 'What's the expected time to get two consecutive heads?' Solved using Markov chain with absorbing states. Onsite: Played a dice-betting game where I had to compute EV across strategies."
2. **Software Engineer**: "Onsite: 'Implement a balanced binary search tree in OCaml' — expected type-safe implementation with pattern matching. Discussed persistence properties of functional data structures."
3. **Researcher**: "Trading game round: Interviewer proposed a bidding game with incomplete information. Needed to compute Bayesian Nash equilibrium dynamically."

### Tips
- Jane Street is unique — interview focused heavily on probability and math, not standard LC.
- Learn OCaml basics — Jane Street uses OCaml and functional programming is valued.
- Prepare for "trading games" — interactive probability challenges with EV computation.
- Mathematical maturity is more important than LeetCode speed.
- Ask questions about OCaml's type system and Jane Street's use of formal verification.

### Compensation
| Level | Base | Bonus | TC (1st yr) |
|-------|------|-------|-------------|
| New Grad | $150-200k | $50-150k | $225-350k |
| Experienced | $200-300k | $150-500k | $400-800k |
| Senior | $300-450k | $300-1M | $750-1.5M |

---

## Bloomberg

### Interview Process
| Round | Type | Duration | Algorithm Focus |
|-------|------|----------|-----------------|
| Phone Screen | Coding + Data Structures | 45 min | LC Medium: arrays, linked lists, trees |
| Onsite R1 | Algorithm Coding | 45 min | Data structures, recursion, sorting/searching |
| Onsite R2 | Algorithm Coding | 45 min | DP, graphs, advanced data structures |
| Onsite R3 | System Design | 45 min | Real-time financial data algorithm design |
| Onsite R4 | Manager + Fit | 45 min | Behavioral, collaborative problem solving, technical curiosity |

### Timeline
Application → Recruiter (1-2 weeks) → Phone screen (1 week) → Onsite (2-3 weeks) → Offer (1-2 weeks). Total: 4-7 weeks.

### Algorithm Focus Areas
- **Data Structures**: Linked lists, trees, hash maps, heaps — fundamental mastery expected
- **Real-Time Algorithms**: High-throughput data processing, real-time sorting, streaming algorithms
- **Financial Algorithms**: Time-series analysis, stock prediction algorithms, arbitrage detection
- **String Algorithms**: Text processing for financial news, sentiment analysis basics
- **Graph Algorithms**: Mutual fund relationships, portfolio optimization
- **Lock-Free & Concurrent Algorithms**: For high-frequency market data feeds

### Interview Stories
1. **SWE, Core Systems**: "Phone screen: 'LRU Cache' — implemented with HashMap + Doubly Linked List. Onsite: 'Design a real-time stock price aggregator' — merging sorted feeds from multiple exchanges."
2. **Senior SWE**: "Onsite: 'Design a system to detect correlated financial instruments' — using sliding window correlation computation with incremental updates."
3. **Data Engineer**: "Algorithm round: 'Find the k most volatile stocks in a sliding time window' — designed heap + deque combination for O(n log k) complexity."

### Tips
- Bloomberg emphasizes data structure fundamentals — master the basics thoroughly.
- Real-time and streaming algorithm knowledge is highly valued.
- Coding environment is a Bloomberg terminal-style editor or whiteboard.
- Expect questions about processing financial data at low latency.
- Bloomberg values long-term stability — demonstrate interest in financial technology.

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC (1st yr) |
|-------|------|-------|-----------|-------------|
| SWE (Entry) | $100-140k | $10-20k | N/A | $110-160k |
| SWE (Mid) | $130-190k | $20-50k | N/A | $150-280k |
| Senior | $170-240k | $40-80k | N/A | $210-350k |

---

## TikTok (ByteDance)

### Interview Process
| Round | Type | Duration | Algorithm Focus |
|-------|------|----------|-----------------|
| Phone Screen | Coding + Algorithms | 45 min | LC Medium: data structures, recursion |
| Onsite R1 | Algorithm Design | 45 min | Hard LC: DP, graphs, optimization |
| Onsite R2 | Algorithm + System Design | 45 min | Distributed algorithms, recommendation systems |
| Onsite R3 | Backend / Infrastructure | 45 min | Performance optimization, large-scale data processing |
| Onsite R4 | Behavioral + Manager | 45 min | Technical leadership, initiative, algorithm decisions |

### Timeline
Application → Recruiter (1 week) → Phone screen (1 week) → Onsite (2-3 weeks) → Offer (1-2 weeks). Total: 4-7 weeks.

### Algorithm Focus Areas
- **Recommendation Algorithms**: Collaborative filtering, content-based filtering, deep learning ranking
- **Graph Algorithms**: Social graph analysis, community detection, influence maximization
- **String Algorithms**: I18n, multilingual processing, real-time subtitle generation
- **DP + Optimization**: Resource allocation for content delivery, caching optimization
- **Streaming Algorithms**: Real-time video processing, bandwidth estimation, adaptive bitrate
- **Data Compression**: Video/image compression algorithms, H.264/H.265 basics

### Interview Stories
1. **Backend SWE, Recommendation**: "Phone screen: 'Find median of two sorted arrays' — binary search solution with careful edge handling. Onsite: 'Design a real-time trending topics system' — Count-Min Sketch with time-decaying weights."
2. **Infrastructure Engineer**: "Onsite: 'Design a distributed cache for recommendation features' — consistent hashing with replication factor. Discussed bloom filters for cache-hit prediction."
3. **ML Engineer**: "Algorithm round: 'Implement word2vec's negative sampling' — discussed softmax vs hierarchical softmax. Follow-up: 'Optimize for 10M+ vocabulary.'"

### Tips
- TikTok interviews are algorithm-intensive with strong ML/recommendation focus for many roles.
- Expect hard LC problems — prepare at the level of Google or Citadel.
- Coding environment is typically online editor with shared screen.
- ByteDance values speed — demonstrate fast thinking and quick iteration.
- For recommendation-heavy roles, understand ranking metrics (NDCG, MAP, recall).

### Compensation
| Level | Base | RSU (4yr) | TC (1st yr) |
|-------|------|-----------|-------------|
| 1-2 (SWE) | $120-160k | $100-200k | $150-220k |
| 2-1 (Senior) | $160-220k | $200-400k | $210-340k |
| 2-2 (Staff) | $200-290k | $400-800k | $300-500k |
| 3-1 (Principal) | $250-350k | $800-1.6M | $450-700k |

---

## General Preparation Strategy

### Across All Companies
1. **Master the core 10 patterns**: Two-pointer, sliding window, binary search, BFS/DFS, DP, backtracking, graph algorithms, string matching, greedy, divide-and-conquer.
2. **Practice with the Algorithms Academy**: Complete labs 01-40 systematically. Each lab maps to interview patterns.
3. **Use LEETCODE_SOLUTIONS directories**: Each lab contains Java solutions to relevant LeetCode problems.
4. **Mock interviews**: Use per-lab MOCK_INTERVIEW.md files for targeted practice.
5. **Complexity analysis**: Every solution must be accompanied by time and space complexity.
6. **Edge cases**: Practice identifying and handling edge cases before writing code.

### Week-by-Week Preparation
- **Weeks 1-2**: Sorting, binary search, arrays, strings (labs 01-08)
- **Weeks 3-4**: Recursion, backtracking, divide-and-conquer, DP basics (labs 09-16)
- **Weeks 5-6**: Graphs, trees, shortest path, MST (labs 17-24)
- **Weeks 7-8**: Advanced topics matching target companies (labs 25-40)
- **Weeks 9-10**: Company-specific practice, mock interviews, behavioral preparation
