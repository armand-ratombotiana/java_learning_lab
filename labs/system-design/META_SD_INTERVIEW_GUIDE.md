# Meta (Facebook) System Design Interview Guide

> Comprehensive guide to system design interviews at Meta.
> Covers top problems, solution frameworks, evaluation criteria, and design philosophy.

---

## Table of Contents

1. [Meta's Interview Process](#1-metas-interview-process)
2. [Top 5 System Design Problems](#2-top-5-system-design-problems)
3. [Detailed Solution Frameworks](#3-detailed-solution-frameworks)
4. [Evaluation Criteria](#4-evaluation-criteria)
5. [Meta's Design Philosophy](#5-metas-design-philosophy)
6. [Real Interview Stories](#6-real-interview-stories)
7. [Prep Strategy](#7-prep-strategy)

---

## 1. Meta's Interview Process

### Rounds
- **Phone screen**: 45 min coding (LeetCode medium, sometimes system design for senior)
- **Onsite (virtual via CoderPad)**: 4-5 rounds
  - 2 coding rounds (CoderPad, typically Python or Java)
  - 1-2 system design rounds
  - 1 behavioral round ("Meta-specific" — focusing on impact and ambiguity)

### System Design Specifics
- **Format**: CoderPad + verbal discussion. You can draw, write, or both.
- **Duration**: 45 min per round (Meta is known for shorter, faster rounds)
- **Level**: E4 = 1 SD round; E5+ = 2 SD rounds
- **Structure**: Conversational. Start with requirements → design → scale discussion → deep dive
- **Expected pace**: 40 min to cover the whole design. Meta values speed.

### Key Difference
- Meta interviews are more conversational and collaborative than Google or Amazon
- Interviewers give hints and guide you — they want you to succeed
- "Move Fast" — don't spend 10 minutes estimating; get to the design quickly
- Focus is on the social aspect: how users interact, not just raw scale

---

## 2. Top 5 System Design Problems

| # | Problem | Frequency | Difficulty | Key Concepts |
|---|---------|-----------|------------|-------------|
| 1 | Design Facebook News Feed | Very High | Hard | Fanout, ranking, personalization, timeline |
| 2 | Design Facebook Messenger | Very High | Hard | Real-time messaging, WebSocket, ordering, storage |
| 3 | Design Instagram | Very High | Hard | Media pipeline, feed, stories, discover |
| 4 | Design Facebook Live | High | Hard | Streaming, low latency, comments, transcoding |
| 5 | Design WhatsApp | High | Hard | E2E encryption, group chat, XMPP, presence |

### Other Problems Meta Asks
- Design Facebook Search
- Design Facebook Ads
- Design Instagram Reels
- Design Facebook Marketplace
- Design Facebook Events
- Design Facebook Graph Search
- Design Facebook's friend recommendation
- Design Facebook's photo/video storage (Haystack)

---

## 3. Detailed Solution Frameworks

### Problem 1: Design Facebook News Feed

**Requirements**:
- 2B+ DAU, each user sees 1,500+ stories/day
- Mix of content: posts, photos, videos, shared links, ads
- Support ranking (relevance, not just chronological)
- Real-time updates

**Estimation**:
- 2B DAU × 50 feed loads/day = 100B feed requests/day = ~1.2M QPS
- Feed storage: 200 items × 500B = 100KB/user in cache
- Cache: 2B × 100KB = 200TB (can't cache everything, only active users)

**Design**:

```
┌──────────┐    ┌───────────┐    ┌───────────┐
│   User   │───→│  Graph    │───→│ News Feed │
│  Posts   │    │  Service  │    │  Service  │
└──────────┘    │ (TAO)    │    │           │
                └───────────┘    │ Cache     │
                                 │ (Memcache)│
┌──────────┐    ┌───────────┐    │           │
│  Friend  │───→│  Feed     │───→│           │
│  Request │    │  Ranker   │    │           │
└──────────┘    └───────────┘    └───────────┘
                                    │
                                    ▼
                              ┌──────────┐
                              │   Client │
                              │ (App/Web)│
                              └──────────┘
```

**Fanout Strategy**:

This is THE key decision for News Feed design. Three approaches:

| Approach | How It Works | Pros | Cons |
|----------|-------------|------|------|
| Fanout-on-write (push) | When user posts, push to all followers' feed caches | Fast reads (<10ms) | Write cost for celebrities |
| Fanout-on-read (pull) | When user loads feed, pull from friends | Low write cost | Expensive reads |
| Hybrid | Regular users (<5K followers) get push. Celebrities pull. | Best of both | Complexity |

**Meta's approach**: Hybrid fanout. For most users, fanout-on-write. For verified celebrities, fanout-on-read with the feed generated on demand.

**Ranking**:
1. **Signal collection**: Affinity score (how close you are to the poster), weight (photo > link > text), time decay, engagement signals
2. **ML model**: Deep learning model (multi-layer, billions of features) → predicts probability of engagement
3. **Re-ranking**: Diversity enforcement, ad insertion, content type balance

**Cache Architecture**:
- **Memcache cluster**: Each user's timeline (list of post IDs)
- **Multifeed**: Pre-generated feed items for "most recent" view
- **Blender**: Merges ranked feed with ads, suggestions, etc.

### Problem 2: Design Facebook Messenger

**Requirements**:
- 1B+ users, real-time delivery
- Multi-device sync, read receipts
- Group chat, file/media sharing
- E2E encryption (optional, "secret conversations")

**Design**:
- **Connection**: Persistent WebSocket (or MQTT-derived protocol)
- **Message handling**: Send to server → store in message store → push to recipient(s)
- **Message ordering**: Server-assigned sequence number per conversation
- **Storage**: Cassandra/HBase per conversation (partition by timestamp)

**Key decisions**:
- **Delivery guarantee**: At-least-once is standard. Exactly-once is hard — use idempotency keys.
- **Offline messages**: Store until delivery acknowledged or timeout
- **Multi-device**: Synchronize last-read watermark across devices

### Problem 3: Design Instagram

**Requirements**:
- 1B+ users, photo/video sharing
- Feed, stories (24h), explore/discover
- Filters, editing, direct messaging
- Reels (short videos)

**Design**:
- **Media pipeline**: Upload → temporary storage → processing (resize, filter, compress) → permanent storage (CDN)
- **Feed**: Fanout-on-write with gravitational model (closer friends first)
- **Stories**: Separate pipeline. User uploads → TTL 24h → periodic GC. View tracking with audience list.
- **Explore**: Computer vision (content-based) + collaborative filtering (social) → candidate images → ML ranking

**Stories Architecture**:
- Stories are stored with TTL. The GC runs every hour to clean expired content.
- View tracking: Write-optimized (many viewers per story) with async aggregation
- Roll: Pre-rendered story sequence for fast loading

### Problem 4: Design Facebook Live

**Requirements**:
- 2B+ viewers, sub-second latency
- Comments, reactions, likes in real-time
- Interactive features (Q&A, polls)

**Design**:
- **Ingress**: RTMP from streamer → ingest node
- **Transcoding**: On-the-fly transcoding to multiple resolutions
- **Distribution**: CDN (Akamai + Facebook internal CDN)
- **Comments**: Real-time comment fanout (similar to chat architecture but broadcast to viewers)
- **Low latency**: Use chunked encoding with short segments (1-2s vs 6s for VOD)

### Problem 5: Design WhatsApp

**Requirements**:
- 2B+ users, 100B+ messages/day
- E2E encryption (Signal protocol)
- Group chat (256 users), voice/video calls
- Backup to cloud

**Design**:
- **XMPP-based protocol**: Custom XMPP extensions for delivery receipts, presence
- **Message storage**: Eyrie (Meta's message store based on HBase)
- **Group chat**: Sender fanout — sender transmits once, server duplicates
- **E2E encryption**: Per-device keys, Double Ratchet algorithm, pre-key bundles

---

## 4. Evaluation Criteria

| Criterion | Weight | What Meta Looks For |
|-----------|--------|---------------------|
| Speed of design | 20% | Can you deliver in 45 min? |
| Requirements scoping | 15% | Do you ask clarifying questions? |
| Architecture quality | 25% | Clean components, clear data flow |
| Trade-off awareness | 20% | Explicit pros/cons, justified decisions |
| Collaboration | 20% | Conversational, responsive to hints |

### What Meta Evaluates Differently

- **Product sense**: How does your design serve users?
- **Data-driven mindset**: "How would you measure success of this design?"
- **A/B testing awareness**: How would you validate a design change with experiments?
- **Move Fast**: Don't over-analyze. Get to a solid solution quickly.

---

## 5. Meta's Design Philosophy

### Core Principles
1. **Social graph is the foundation**: All Meta products are built on the graph of users and connections
2. **Move fast with infrastructure**: Iterate quickly, evolve designs over time
3. **Data-driven everything**: Every design decision should be experimentally validated
4. **Think in terms of DAU and engagement**: Design choices should improve metrics

### Key Technologies
| Area | Technologies |
|------|-------------|
| Graph | TAO (The Actions and Objects) — distributed graph store |
| Cache | Memcache (scaled to exabytes across the fleet) |
| Storage | Haystack (photo storage), HBase, Cassandra |
| Compute | Presto (SQL), Spark, PyTorch for ML |
| Messaging | Scribe (log collection), PubSub, Thrift (RPC) |
| Data | Data Access Layer (DAL), UPM (User Profile Management) |

### Meta's "Design for Testability"
- Design should enable A/B testing of new features
- Feature flags must be integrated into the design
- Canary deployments and gradual rollouts

---

## 6. Real Interview Stories

### Story 1: E5 — Design News Feed
> **Key moment**: The candidate proposed a feed ranking system and the interviewer asked "How do you measure whether the new ranking is better?"
>
> **Excellent answer**: "I'd run an A/B test with online interleaving — present two rankings side-by-side and measure user engagement. Key metrics: time spent, scroll depth, likes, shares, and session length. I'd also measure feed refresh rate to ensure the ranking computation doesn't increase latency."
>
> **Interviewer reaction**: "Good — you understand that ML ranking is only useful if we can measure its impact."

### Story 2: E4 — Design Instagram Stories
> **Candidate approach**: Spent 5 min on requirements, 5 min on estimation, 20 min on design, 15 min on deep dive.
>
> **Deep dive topic**: "How do you handle the 24-hour delete?"
> - TTL-based at write time: story entries in DB and cache get TTL
> - Background GC: periodic (hourly) cleanup of expired stories
> - Lazy deletion: service checks TTL on read
> - Metadata cleanup: cascade delete story reactions, views, etc.
>
> **Result**: Strong hire. Showed depth in thinking about data lifecycle.

---

## 7. Prep Strategy

### 6-Week Prep Plan for Meta

**Weeks 1-2: Foundation & Pace**
- Master 3 designs: News Feed, Messenger, Instagram
- Practice delivering a complete design in 40 min
- Lab focus: 13-design-chat-system, 05-caching, 10-real-time-collaboration

**Weeks 3-4: Social Graph & Real-Time**
- Study TAO (Facebook's graph), Memcache architecture
- Practice: WhatsApp, Live Streaming, Search
- Mock interviews with time pressure

**Weeks 5-6: Full Mocks & Polish**
- 5+ full mock interviews
- Practice product-sense questions alongside design
- Prepare behavioral stories about impact

### Must-Read Before Interview
- Facebook TAO paper (USENIX ATC 2013)
- Memcache at Facebook paper (NSDI 2013)
- Scaling Memcache at Facebook
- Apache Cassandra paper (originally from Facebook)

### Common Meta Interview Mistakes
1. **Too slow**: Use 45 min effectively. Don't get stuck on estimation.
2. **Ignoring the social graph**: All Meta designs should leverage connections
3. **No experimental thinking**: Meta is data-driven — mention A/B testing
4. **Not mobile-optimized**: Meta is mobile-first, consider bandwidth, battery, offline
5. **Forgetting ads**: Almost every Meta product has an ads component
