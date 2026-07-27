# Google System Design Interview Guide

> Comprehensive guide to system design interviews at Google.
> Covers top problems, solution frameworks, evaluation criteria, and design philosophy.

---

## Table of Contents

1. [Google's Interview Process](#1-googles-interview-process)
2. [Top 5 System Design Problems](#2-top-5-system-design-problems)
3. [Detailed Solution Frameworks](#3-detailed-solution-frameworks)
4. [Evaluation Criteria](#4-evaluation-criteria)
5. [Google's Design Philosophy](#5-googles-design-philosophy)
6. [Real Interview Stories](#6-real-interview-stories)
7. [Prep Strategy](#7-prep-strategy)

---

## 1. Google's Interview Process

### Rounds
- **Phone screen**: 45 min coding (typically 1-2 LeetCode medium/hard problems)
- **Onsite (virtual)**: 4-6 rounds
  - 2-3 coding rounds (CoderPad, Java/Python/C++)
  - 1-2 system design rounds (Google Docs shared document)
  - 1 Googleyness/behavioral round
  - 1 extra round (varies by level and committee decision)

### For System Design Rounds
- **Format**: You type in Google Docs while the interviewer watches and discusses
- **Duration**: 45 min per round
- **Level**: L4 (SWE III) = 1 SD round; L5+ = 2+ SD rounds
- **Structure**: Requirements (5 min) → Estimations (5 min) → Architecture (20 min) → Deep dives (10 min) → Trade-offs (5 min)

### Key Differences from Other Companies
- **Stronger focus on distributed systems theory**: Expect CAP, Paxos/Raft, consistency models
- **Less focus on API design** compared to Amazon
- **Quantitative rigor**: You must estimate everything (QPS, storage, bandwidth, cost)
- **Simplicity bias**: Google values clean, simple designs over complex solutions
- **Googleyness matters**: Humility, collaboration, intellectual curiosity

---

## 2. Top 5 System Design Problems

| # | Problem | Frequency | Difficulty | Key Concepts |
|---|---------|-----------|------------|-------------|
| 1 | Design Google Search | Very High | Hard | Crawling, indexing, PageRank, query serving |
| 2 | Design YouTube | Very High | Hard | Video transcoding, CDN, adaptive bitrate |
| 3 | Design Google Maps | High | Hard | Spatial indexing, routing, real-time traffic |
| 4 | Design Gmail | High | Hard | Distributed storage, search, real-time delivery |
| 5 | Design Google Drive | Medium | Hard | Sync, conflict resolution, deduplication |

### Other Problems Google Asks
- Design a Web Crawler
- Design Google Docs (collaboration)
- Design Google Calendar
- Design Google Photos
- Design a Search Autocomplete System
- Design Google Ads
- Design Google Cloud Storage (with Spanner-like DB)

---

## 3. Detailed Solution Frameworks

### Problem 1: Design Google Search

**Requirements**:
- Index 100B+ web pages
- Serve 10B+ queries/day globally
- <100ms query latency
- Handle updates (new pages, re-crawls)
- Support advanced queries (phrases, Boolean, filters)

**Estimation**:
- Queries: 10B/day → ~115K QPS avg, ~350K QPS peak
- Pages: 100B pages, avg 100KB = 10PB raw text → ~1PB after compression
- Index size: Inverted index roughly 30% of source → ~300TB
- Crawl bandwidth: 100B pages / 30 days / 86400 = ~38K pages/sec

**Design**:

```
┌─────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│Crawler  │───→│ Indexer  │───→│ Index DB │←───│ Query    │
│(URL     │    │(parse,   │    │(sharded  │    │ Frontend │
│ frontier│    │analyze,  │    │by term)  │    │          │
│)        │    │invert)   │    │          │    │          │
└─────────┘    └──────────┘    └──────────┘    └──────────┘
     │               │                             │
     │               │                             │
     ▼               ▼                             ▼
┌─────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│URL DB   │    │Doc Store │    │Cache     │    │Ranking   │
│(visited,│    │(compres- │    │(popular  │    │(PageRank,│
│priority)│    │sed HTML) │    │queries)  │    │ML model) │
└─────────┘    └──────────┘    └──────────┘    └──────────┘
```

**Crawler Subsystem**:
- URL frontier: Priority queue with politeness domain queues
- Fetch: Distributed workers, 10K+ machines
- Duplicate detection: Bloom filter for URL de-duplication
- Politeness: Per-domain delay (configurable, typically 1-10s)

**Indexing Subsystem**:
- Parse HTML → extract text, links, metadata
- Build inverted index: Map term → Posting (docID, position, tf-idf)
- Index sharding: Document-partitioned (smaller index per shard) or term-partitioned (faster for single-term queries)
- LSM-tree based storage for append-heavy index updates

**Query Serving**:
1. Query understanding: Spelling correction, query expansion, intent classification
2. Document retrieval: Inverted index lookup → candidate document list
3. Ranking: PageRank + topical relevance + freshness + personalization (ML model)
4. Snippet generation: Extract relevant passage from document
5. Result serving: Cache popular results, serve from CDN for repeated queries

**Trade-offs**:
| Trade-off | Option A | Option B | Google's Choice |
|-----------|----------|----------|----------------|
| Crawl policy | Redundant crawling (freshness) | Politeness (coverage) | Politeness-first with freshness scoring |
| Index shard | Document-partitioned | Term-partitioned | Hybrid (term partitioning for common terms) |
| Rank freshness | Real-time re-ranking | Periodic batch ranking | Batch + real-time signals |
| Cache policy | LRU | Frequency-based | LFU for query cache |

### Problem 2: Design YouTube

**Requirements**:
- 1B+ users, 500h video uploaded/min
- Support 4K HDR 60fps
- <200ms playback start
- Global delivery, recommendations

**Estimation**:
- Upload: 500h/min × 60 = 30,000h/hour = ~15TB/min storage (raw)
- Storage after encoding: ~500MB/h × 500h/min × 60 = 15TB/min → 22PB/day
- Bandwidth: 1B users × 30min/day × 5Mbps avg = ~150Tbps peak
- Transcoding: 500h/min × 1.5x real-time encoding = 750 compute-hours/min

**Design**:
- Upload pipeline: Resumable chunked upload → video metadata → transcode job queue
- Transcoding: Resolution ladder (144p, 240p, 360p, 480p, 720p, 1080p, 4K), codec (VP9, H.264, AV1)
- Storage: Colossus-like blob storage with hierarchical namespace, multi-region replication
- CDN: Edge cache → regional cache → origin. Google Global Cache (ISP-partnered)
- Playback: Client requests manifest (MPD/M3U8) → fetches chunks at appropriate bitrate

### Problem 3: Design Google Maps

**Requirements**:
- 1B+ users, real-time traffic, turn-by-turn navigation
- Global map data, search (POI), street view
- <1s route calculation

**Design**:
- Map rendering: Vector tiles (protobuf) → client-side rendering → smooth pan/zoom
- Routing: Road network graph → contraction hierarchies (pre-processing) → A* with traffic weights
- Traffic: Real-time GPS data → map-matching → speed aggregation → traffic layer
- Geocoding: Address → coordinates (inverted index on address components)
- Places: POI database with spatial index (quad-tree, S2 cells)

### Problem 4: Design Gmail

**Requirements**:
- 1.5B+ users, 15GB free storage/user
- Fast search, spam filtering, labels/folders
- Real-time push delivery, attachments

**Design**:
- Storage: Colossus for blobs (attachments), Bigtable for message metadata
- Mail delivery: SMTP → spam filter (ML) → virus scan → mailbox delivery
- Mailbox: Per-user mailbox stored across multiple Bigtable rows
- Search: Inverted index per mailbox, real-time indexing
- Push: Persistent TCP connection (QUIC/HTTP2) for real-time notification

### Problem 5: Design Google Drive

**Requirements**:
- 1B+ users, file sync across devices
- Conflict resolution, sharing, version history
- 15GB free, up to 30TB paid

**Design**:
- Client: File watcher → change detection → upload/delta sync
- Sync server: Metadata service (file tree, versions, sharing)
- Content storage: File chunked into 4MB blocks, content-addressable, deduplication
- Conflict resolution: Last-writer-wins for directories, create "conflicted copy" for files

---

## 4. Evaluation Criteria

| Criterion | Weight | What Google Looks For |
|-----------|--------|----------------------|
| Requirements Gathering | 10% | Do you ask clarifying questions or jump to solution? |
| Scalability Planning | 25% | Can you estimate scale and design for 10x-100x growth? |
| System Architecture | 25% | Clean diagram, logical components, data flow |
| Distributed Systems Depth | 20% | Consistency, partitioning, replication understanding |
| Trade-off Analysis | 15% | Explicit discussion of pros/cons, data-driven decisions |
| Communication | 5% | Clear, structured, collaborative |

### Key Questions Google Interviewers Ask Themselves
- "Would I trust this person to design a system used by billions?"
- "Can they reason about distributed systems trade-offs?"
- "Do they quantify their decisions?"
- "Are they intellectually honest about limitations of their design?"
- "Would this person be a good colleague for a design review?"

---

## 5. Google's Design Philosophy

### Core Principles
1. **Scale matters**: Design must work at planetary scale. If it can't handle 100x growth, it's not a Google-ready design.
2. **Simplicity**: Complex designs are fragile. Prefer clean, proven solutions.
3. **Quantify everything**: Latency, throughput, storage cost, bandwidth — all must be estimated.
4. **Fault tolerance**: Assume components fail. Design redundancy and graceful degradation.
5. **Data locality**: Minimize cross-datacenter traffic. Keep compute close to data.

### What Google Doesn't Care About (as much)
- **API design details**: URL structure, REST vs GraphQL debates (Amazon territory)
- **Frontend architecture**: Focus is on backend scalability
- **Specific technologies**: You don't need to know every Google internal tool
- **Perfect design**: They want to see your thinking process, not a perfect answer

### Technologies You Should Know
| Area | Technologies |
|------|-------------|
| Storage | GFS/Colossus, Bigtable, Spanner, BigQuery |
| Compute | Borg/Kubernetes, Google Cloud Run |
| Data | MapReduce, Flume, MillWheel, Dataflow |
| Networking | B4 (WAN), Jupiter (datacenter), gRPC |
| Consistency | Paxos, TrueTime, Chubby |

---

## 6. Real Interview Stories

### Story 1: L5 — Design Google Search (Query Serving Focus)
> **Setup**: The interviewer explicitly said "Don't design the crawler. Focus on the query path."
>
> **Candidate approach**: Started with query understanding (spell correction from query logs, query expansion using Google's Knowledge Graph), then document retrieval from inverted index, then ranking.
>
> **Key moment**: Interviewer asked "How do you handle the top 100 queries that make up 5% of traffic?" Candidate proposed a dedicated cache layer for these queries and pre-computed result sets.
>
> **Follow-up**: "What's in your snippet generation?" Candidate discussed passage extraction (position, relevance, query match density) and ML-based snippet selection.
>
> **Result**: Strong hire. Candidate showed deep understanding of search without getting lost in crawler details.

### Story 2: L4 — Design YouTube
> **Setup**: The candidate exactly replicated a known design from Alex Xu's book.
>
> **What went wrong**: The interviewer asked "What happens when 90% of views are for content uploaded in the last 7 days?" The candidate hadn't considered content lifecycle — their cache design didn't account for temporal locality.
>
> **Recovery**: Candidate acknowledged the gap and proposed: (1) Hot content pre-positioned at CDN edge, (2) Warm content at regional caches, (3) Cold content served from origin. Also suggested adaptive prefetching based on trending models.
>
> **Lesson**: Don't just memorize designs — understand the "why" behind each decision.

### Story 3: L6 — Design Google Maps ETA Prediction
> **Setup**: The interviewer was a Maps tech lead. They wanted real-time traffic data fusion.
>
> **Discussion**: How to combine historical baselines with real-time GPS probes. Candidate proposed Kalman filter approach fusing historical speed profiles with live probe data.
>
> **Deep dive**: "How do you handle cities with no probe data?" Candidate discussed transfer learning from similar cities based on road topology and demographics.

---

## 7. Prep Strategy

### 8-Week Prep Plan for Google

**Weeks 1-2: Foundations**
- Study DDIA chapters 1-6 (replication, partitioning, transactions)
- Practice estimation problems daily
- Lab focus: 04-consistency-models, 02-scalability

**Weeks 3-4: Distributed Systems Depth**
- Read Google papers: GFS, Bigtable, Spanner, MapReduce
- Lab focus: 09-distributed-database-design, 08-observability
- Practice: Design 2 systems/week with timer

**Weeks 5-6: Core Google Design Problems**
- Practice: Search, YouTube, Maps, Gmail, Drive
- Mock interviews (peers or Pramp)
- Lab focus: 12-design-url-shortener, 13-design-chat-system

**Weeks 7-8: Mock Interviews & Polish**
- 5+ full mock system design interviews
- Review DDIA chapters 7-9 (consensus, batch/stream processing)
- Practice going deep on your weak areas

### Must-Read Before Interview
- Designing Data-Intensive Applications (entire book)
- Google SRE Book (chapters 1-6)
- Papers: Google File System, MapReduce, Bigtable, Spanner
- Company engineering blog: Google AI Blog, Google Research

### Common Google Interview Mistakes
1. **Not estimating**: Never skip the estimation step
2. **Ignoring trade-offs**: Every design choice has trade-offs — discuss them
3. **Going too deep too fast**: Start high-level, let the interviewer guide deep dives
4. **Being rigid**: If the interviewer redirects, follow their lead
5. **Over-engineering**: Don't propose a distributed system for a single-server problem
