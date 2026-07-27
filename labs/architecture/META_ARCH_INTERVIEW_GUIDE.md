# Meta Architecture Interview Guide (E6+)

> Staff/Staff SE system design and leadership evaluation at Meta.

---

## Table of Contents

1. [Meta's Engineering Culture](#1-metas-engineering-culture)
2. [E6+ Level Expectations](#2-e6-level-expectations)
3. [System Design Interview Format](#3-system-design-interview-format)
4. [Common Meta System Design Questions](#4-common-meta-system-design-questions)
5. [Deep Dive: Design News Feed](#5-deep-dive-design-news-feed)
6. [Deep Dive: Design WhatsApp Messenger](#6-deep-dive-design-whatsapp-messenger)
7. [Production Engineering Round](#7-production-engineering-round)
8. [Behavioral and Leadership Evaluation](#8-behavioral-and-leadership-evaluation)
9. [Coding Expectations at E6+](#9-coding-expectations-at-e6)
10. [Evaluation Rubric](#10-evaluation-rubric)
11. [Preparation Strategy](#11-preparation-strategy)

---

## 1. Meta's Engineering Culture

### Key Cultural Tenets

- **Move fast with stable infra**: Ship quickly, iterate based on data, maintain reliability
- **Be open**: Default to transparency, share information broadly
- **Build social value**: Technology should connect people and build community
- **Focus on impact**: Not hours worked, but results achieved
- **Meta-scale thinking**: Problems must be understood at billion-user scale

### What Meta Values at E6+

- **Full-stack product thinking**: Understanding client and server, product and infrastructure
- **Speed without sacrificing quality**: Finding the right balance
- **Data-informed decisions**: Using experimentation to validate every change
- **Technical leadership through influence**: Setting direction without managerial authority
- **Raising the bar**: Improving people and processes around you

---

## 2. E6+ Level Expectations

### E6 (Staff Software Engineer)

- Independently identifies and leads technically complex, multi-quarter projects
- Sets technical direction for a team or multiple teams
- Unblocks teams through architecture and design decisions
- Mentors senior engineers and conducts design reviews
- Influences org-wide technical decisions

### E7 (Senior Staff Engineer)

- Defines technical vision for a large organization
- Drives cross-org initiatives that affect Meta's platforms
- Recognized as a domain expert company-wide
- Shapes engineering culture and practices across Meta

### E6+ Evaluation Criteria

| Criteria | E6 Expectation | E7 Expectation |
|----------|---------------|----------------|
| **Technical scope** | Complex, cross-team initiatives | Cross-org, company-wide |
| **Execution** | Delivers results through teams | Defines strategy for execution |
| **Leadership** | Sets technical direction for team | Sets technical vision for org |
| **Impact** | Org-level measurable results | Company-level measurable results |
| **Mentorship** | Grows senior engineers | Grows staff engineers |

---

## 3. System Design Interview Format

### Structure

- **Duration**: 60 minutes per round (E6+ gets 2 system design rounds)
- **Format**: Whiteboard or CoderPad with diagram support
- **Focus**: Full-stack, product-aware, Meta-scale architecture

### Time Allocation

| Phase | Time | Activity |
|-------|------|----------|
| Product requirements | 5 min | Understand the product, user flows, metrics |
| Scale estimation | 5 min | DAU, QPS, storage, bandwidth |
| Data model | 5 min | Schema design for social graph |
| High-level design | 10 min | Client, server, storage, infrastructure |
| Deep dive | 15 min | 1-2 critical components in depth |
| Trade-offs | 10 min | Alternatives considered, why chosen |
| Summary | 5 min | Recap key decisions, failure modes |

### Key Differences from Google/Amazon

- **Product-first**: Meta expects you to understand the product and user experience
- **Full-stack**: Your design should include client considerations (mobile, web)
- **Experimentation**: How will you validate this with A/B testing?
- **Social graph**: Almost every system involves the social graph

---

## 4. Common Meta System Design Questions

### Tier 1 (Most Common)

| Question | Key Focus Areas |
|----------|----------------|
| Design News Feed | Ranking algorithm, real-time, push/pull, personalization |
| Design WhatsApp Messenger | Real-time messaging, presence, delivery, end-to-end encryption |
| Design Instagram Stories | Ephemeral content, viewing patterns, creation tools |
| Design Facebook Live | Live video streaming, comments, reactions |
| Design Facebook Search | Social search, entity ranking, real-time indexing |

### Tier 2 (Common)

| Question | Key Focus Areas |
|----------|----------------|
| Design Instagram Feed | Photo/video sharing, feed ranking, stories integration |
| Design Facebook Marketplace | Listing, search, messaging, trust/safety |
| Design Facebook Events | Event creation, RSVP, recommendations, calendar |
| Design Meta Ad Platform | Real-time bidding, targeting, attribution, ROI measurement |
| Design Facebook Groups | Group management, membership, content moderation |

### Tier 3 (Infrastructure)

| Question | Key Focus Areas |
|----------|----------------|
| Design TAO (Meta's Graph) | Distributed graph storage, caching |
| Design Presto | Distributed SQL query engine |
| Design Memcached at Meta | Distributed caching, scaling cache infrastructure |

---

## 5. Deep Dive: Design News Feed

### Requirements

**Functional:**
- Display relevant posts from friends, pages, groups
- Real-time updates as new content is published
- Support for different content types (text, photo, video, link, live)
- User interactions (like, comment, share, save)
- Personalized ranking based on user interests and behavior

**Non-functional:**
- 2B+ DAU
- P99 latency < 200ms for feed load
- Real-time: new posts appear within seconds
- Write-heavy: millions of posts per minute
- Read-heavy: billions of feed loads per day

### Scale Estimation

```
DAU: 2B
Daily feed loads: 10B (5 per user per day)
Daily posts: 1B+
Feed items per load: 200 posts
Storage per post: 1KB (metadata) + media (CDN)
Feed generation QPS: 10B / 86400 ≈ 115K QPS (peak 300K)
Write QPS: 1B / 86400 ≈ 12K QPS
```

### Architecture

**Fanout-on-write (push) for most users:**
```
User Posts → [Post Service] → [Fanout Service] → [Redis/TAO] → Friends' Feeds
                                              → [ML Ranking] → Ranked Feed
```

**Fanout-on-read (pull) for celebrities:**
```
User Requests Feed → [Feed Service] → [Pull from celebrity posts]
                                    → [Merge with pushed content]
                                    → [ML Ranking]
                                    → Ranked Feed
```

### Key Decisions

**Fanout strategy:**
- Push for regular users (< 5K followers): pre-compute feeds
- Pull for celebrities (> 5K followers): compute on read
- Hybrid: push to active users, pull for inactive

**Ranking:**
- Machine learning model (neural network with user features)
- Features: affinity score, content freshness, content type, engagement prediction
- A/B testing for every ranking change

**Storage:**
- TAO (graph) for social graph (friendships, likes, comments)
- MySQL for post metadata (sharded by post ID)
- HDFS/CDN for media content
- Redis for real-time counters (like counts, comment counts)

**Real-time updates:**
- WebSocket connections for live feed updates
- MQTT for push notifications
- Polling with incremental updates

---

## 6. Deep Dive: Design WhatsApp Messenger

### Requirements

**Functional:**
- Send and receive messages (text, image, video, document)
- Group chats (up to 1024 participants)
- Voice and video calls
- End-to-end encryption
- Message delivery status (sent, delivered, read)
- Message sync across devices

**Non-functional:**
- 2B+ active users
- 100B+ messages per day
- P99 latency < 100ms for message delivery
- End-to-end encryption (no server access to message content)
- High availability (always connected)

### Architecture

```
Device A ─→ [WebSocket Connection] ─→ [Message Router]
                                         │
                                    [Message Store]
                                    (encrypted blobs)
                                         │
Device B ←─ [WebSocket Connection] ←── [Message Router]
```

### Key Decisions

**Connection management:**
- Persistent WebSocket connections per device
- Connection pool across servers
- Session management (resume on reconnect)

**End-to-end encryption:**
- Signal Protocol for message encryption
- Each message encrypted with recipient's public key
- Server stores encrypted blobs, never sees plaintext
- Key exchange handled client-side

**Message delivery:**
- In-memory message queue per connection
- Store-and-forward for offline users
- Delivery receipts processed asynchronously
- Exponential backoff for retry

**Group chat:**
- Each group has a unique encryption key
- Sender encrypts once, server fan-outs encrypted message
- Group membership changes trigger key rotation

---

## 7. Production Engineering Round

### Purpose

Evaluates your ability to operate and debug production systems at scale.

### Common Scenarios

1. **Debug a performance issue**: "A service's latency has increased 10x. Walk through your debugging approach."
2. **Capacity planning**: "A service is approaching capacity limits. How do you handle it?"
3. **Incident response**: "A critical service is down. Walk through the incident response process."
4. **Monitoring design**: "How would you monitor a new distributed system?"

### Evaluation Criteria

| Criteria | What They Look For |
|----------|-------------------|
| Systematic approach | Structured debugging process, not guessing |
| Tool knowledge | Familiarity with profiling, tracing, monitoring tools |
| Root cause analysis | Goes beyond symptoms to find true root cause |
| Mitigation | Balances immediate fix with long-term solution |
| Communication | Clear updates during incident, postmortem writing |

---

## 8. Behavioral and Leadership Evaluation

### Key Behavioral Themes

**Impact at scale:**
- "Tell me about a project that impacted millions of users"
- "How do you measure the success of your projects?"

**Moving fast:**
- "Tell me about a time you had to ship quickly"
- "How do you balance speed and quality?"

**Technical leadership:**
- "How have you set technical direction for your team?"
- "Describe a technical decision you made that others disagreed with"

**Cross-functional collaboration:**
- "How do you work with product managers and designers?"
- "Tell me about a time you had to compromise to move forward"

**Mentorship:**
- "How have you grown engineers on your team?"
- "Tell me about a time you helped someone overcome a technical challenge"

### Meta-Specific Values

- **Building social value**: How does your work connect people?
- **Be open**: How have you shared knowledge and been transparent?
- **Move fast**: How do you ship quickly while maintaining quality?
- **Focus on impact**: How do you prioritize what matters?

---

## 9. Coding Expectations at E6+

### LeetCode Level

- **Difficulty**: Medium-Hard
- **Topics**: Graphs (social graph problems), trees, dynamic programming, string algorithms
- **Style**: Clean, fast, focused on correctness

### Common Coding Themes

- Graph traversal (friendship connections, content distribution)
- System design coding (implement a simplified version of a system)
- Performance optimization (identify and fix performance issues)

### Language Choice

- **Most common**: C++, Python, Java
- **Meta's preference**: C++ for performance-critical code, Python for scripting and data work, Hack (Meta's PHP dialect) for web

---

## 10. Evaluation Rubric

### E6+ Scoring

| Criteria | Weight | E6 Expectation |
|----------|--------|---------------|
| System Design | 40% | Full-stack, product-aware, social-scale |
| Production Excellence | 20% | Debugging, monitoring, performance |
| Coding | 20% | Clean, optimal, well-communicated |
| Leadership | 20% | Technical direction, mentorship, influence |

### Common Rejection Reasons

1. **Not product-aware**: Designs that don't consider user experience
2. **Not full-stack**: Only server-side thinking, ignoring client constraints
3. **No data-driven approach**: Not discussing A/B testing or metrics
4. **Can't handle Meta scale**: Designs break down at billions of users
5. **Weak leadership evidence**: No examples of setting technical direction

---

## 11. Preparation Strategy

### Week 1-2: Foundation
- Understand Meta's infrastructure: TAO graph, Presto, Unicorn search, Memcached
- Practice social graph data modeling (adjacency lists, inverse indexes)
- Review recommendation system fundamentals (collaborative filtering, content-based, deep learning)

### Week 3-4: System Design Practice
- Design 5-7 Meta-scale systems (News Feed, Messenger, Instagram, Search, Ads)
- Practice full-stack thinking for each design
- Time yourself (60 minutes per round)

### Week 5-6: Production & Behavioral
- Practice debugging scenarios (systematic approach)
- Prepare 5-7 behavioral stories with measurable impact
- Practice explaining technical concepts to non-technical audiences

### Must-Know Meta Technologies

| Technology | Purpose | Interview Relevance |
|-----------|---------|-------------------|
| TAO | Graph data store | Social graph design |
| Presto | Distributed SQL | Analytics system design |
| Unicorn | Search index | Search/ranking design |
| Memcached | Distributed cache | Caching strategy |
| FBLearner | ML platform | Recommendation design |
| Thrift | RPC framework | Service communication |
| React | UI framework | Client-side design |

---

*Combine this guide with the ACADEMY_INTERVIEW_GUIDE.md for complete Meta E6+ interview preparation.*

---

## Appendix A: Meta System Design — Design Facebook Live

### Live Video Streaming Architecture

```
Broadcaster → [RTMP/RTMPS] → [Ingestion Service] → [Transcoding Pipeline]
                                                        ↓
                                                   [HLS/DASH Packaging]
                                                        ↓
Viewer ← [CDN] ← ---- [Edge Cache] ← [Origin Server]
                                                        ↓
                                              [Real-time Events]
                                              (Comments, Reactions, Likes)
```

**Key challenges:**
- Sub-second latency for real-time interaction
- Massive scale (millions of concurrent viewers)
- Adaptive bitrate for varying network conditions
- Recording for replay

**Solutions:**
- WebRTC for sub-second latency (small groups)
- HLS/DASH for broadcast-scale latency (5-30 seconds)
- SFU (Selective Forwarding Unit) for video routing
- Edge transcoding for adaptive bitrate

## Appendix B: Meta Infrastructure Deep Dive

### TAO (The Associations and Objects)
Distributed graph data store powering Facebook's social graph. Key characteristics:
- Objects and associations (nodes and edges)
- Read-optimized: heavily cached
- Eventually consistent with weak consistency
- Hundreds of thousands of read-only cache servers

### Presto
Distributed SQL query engine for interactive analytics:
- Query petabytes of data in seconds
- Connects to multiple data sources (Hive, Kafka, MySQL)
- Used for ad-hoc analysis, not production queries

### Memcached at Meta
World's largest memcached deployment:
- Thousands of servers, tens of terabytes of RAM
- Regionally distributed with pool-based architecture
- Auto-discovery and failure handling
- Cache invalidation via mcsqueal (database change capture)

## Appendix C: Meta E6+ Behavioral Preparation

### Must-Prepare Stories by Theme

**Impact at scale (2 stories):**
- "Tell me about a project that impacted millions of users"
- "How did you measure the success of that project?"

**Moving fast (2 stories):**
- "Tell me about a time you shipped quickly under pressure"
- "How do you balance speed with quality?"

**Technical leadership (2 stories):**
- "How have you set technical direction for your team?"
- "Describe a technical decision that others initially disagreed with"

**Cross-functional collaboration (2 stories):**
- "How do you work with product managers and designers?"
- "Tell me about a time you had to compromise to move a project forward"

## Appendix D: Meta Common Rejection Reasons

1. **Not product-aware**: Designs show no understanding of user experience or product metrics
2. **Not full-stack**: Only server-side thinking without considering mobile/web client constraints
3. **No data-driven approach**: Failure to discuss A/B testing, metrics, or experimentation
4. **Cannot handle Meta scale**: Designs break at billions of users
5. **Weak leadership evidence**: No examples of technical direction setting or influence
