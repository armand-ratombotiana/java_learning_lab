# Google Architecture Interview Guide (L6+)

> Staff/Staff SE system design and leadership evaluation at Google.

---

## Table of Contents

1. [Google's Engineering Culture](#1-googles-engineering-culture)
2. [L6+ Level Expectations](#2-l6-level-expectations)
3. [System Design Interview Format](#3-system-design-interview-format)
4. [Common Google System Design Questions](#4-common-google-system-design-questions)
5. [Deep Dive: Design YouTube](#5-deep-dive-design-youtube)
6. [Deep Dive: Design Google Drive](#6-deep-dive-design-google-drive)
7. [Leadership Round (Googleyness)](#7-leadership-round-googleyness)
8. [Coding Expectations at L6+](#8-coding-expectations-at-l6)
9. [Evaluation Rubric](#9-evaluation-rubric)
10. [Preparation Strategy](#10-preparation-strategy)

---

## 1. Google's Engineering Culture

### Key Cultural Tenets

- **Data-driven decisions**: Everything is measured. "I think" is weak — "the data shows" is strong
- **Psychological safety**: Disagree openly, challenge ideas respectfully
- **Bias toward action**: Ship early, iterate based on data
- **Technical excellence**: Clean code, rigorous testing, thoughtful design
- **Collaboration over hierarchy**: Influence without authority is the norm

### What Google Values at L6+

- **Technical breadth AND depth**: T-shaped skills — deep in one area, broad across many
- **Strategic thinking**: Your decisions consider 12-24 month horizons
- **Organizational impact**: Your work sets direction for multiple teams
- **Mentorship**: You grow the engineers around you through design reviews, code reviews, and pairing

---

## 2. L6+ Level Expectations

### L6 (Staff Software Engineer)

- Sets technical direction for a team or small organization
- Makes trade-off decisions that balance short-term needs with long-term health
- Identifies what needs to be built before being asked
- Mentors senior engineers and conducts design reviews
- Has deep expertise in at least one domain

### L7 (Senior Staff Engineer)

- Sets technical direction for a large organization (multiple teams)
- Drives multi-year technical strategy
- Recognized as an expert across Google
- Influences company-wide technical decisions
- Publishes internal papers, leads architectural reviews

### Evaluation Criteria

| Criteria | L6 Expectation | L7 Expectation |
|----------|---------------|----------------|
| **Impact** | Team/organization level | Multi-org/company level |
| **Technical depth** | Deep expertise in one area | Expert in one, deep in several |
| **Technical breadth** | Broad knowledge across systems | Deep understanding across systems |
| **Leadership** | Technical direction for team | Technical strategy for org |
| **Judgment** | Sound decisions with data | Strategic decisions with vision |

---

## 3. System Design Interview Format

### Structure

- **Duration**: 45-60 minutes
- **Format**: Google Docs or whiteboard (remote: Google Draw or virtual whiteboard)
- **Focus**: Large-scale distributed systems

### Time Allocation

| Phase | Time | Activity |
|-------|------|----------|
| Requirements | 5 min | Clarify functional and non-functional requirements |
| Estimation | 5 min | Scale estimation (QPS, storage, bandwidth) |
| Data model | 5 min | Schema design, API design |
| High-level design | 10 min | Components and interactions |
| Deep dive | 15 min | Detailed design of 1-2 components |
| Summary | 5 min | Recap, trade-offs, alternatives |

### What Interviewers Evaluate

1. **How you approach ambiguity**: Do you ask clarifying questions or jump into a solution?
2. **Your ability to prioritize**: Do you focus on the most critical aspects first?
3. **Trade-off awareness**: Do you understand why you choose one approach over another?
4. **Failure analysis**: Do you proactively identify failure scenarios?
5. **Communication**: Can you explain complex ideas clearly?

---

## 4. Common Google System Design Questions

### Tier 1 (Most Common)

| Question | Key Focus Areas |
|----------|----------------|
| Design YouTube | Video upload, transcoding, CDN, recommendation, search |
| Design Google Search | Crawling, indexing, ranking, serving infrastructure |
| Design Google Drive | File storage, sync, conflict resolution, sharing |
| Design Google Maps | Geospatial indexing, routing, traffic, places |
| Design Google Photos | Image upload, storage optimization, ML tagging, sharing |

### Tier 2 (Common)

| Question | Key Focus Areas |
|----------|----------------|
| Design Gmail | Email storage, search, spam detection |
| Design Google Calendar | Event scheduling, resource management, sharing |
| Design Google Docs | Collaborative editing, OT/CRDT, version history |
| Design Google Analytics | Data ingestion, aggregation, querying |
| Design Google Chat | Real-time messaging, presence, history |

### Tier 3 (Less Common but Appear)

| Question | Key Focus Areas |
|----------|----------------|
| Design Google AdWords | Real-time bidding, auction, attribution |
| Design Google Cloud Storage | Object storage, durability, consistency |
| Design Spanner | Globally distributed database, TrueTime |
| Design Google Pub/Sub | Event ingestion, exactly-once, ordering |

---

## 5. Deep Dive: Design YouTube

### Requirements

**Functional:**
- Upload and process videos
- Watch videos with low latency
- Search for videos
- View recommendations
- Comment, like, share

**Non-functional:**
- 2B+ monthly active users
- 500+ hours of video uploaded per minute
- P99 latency < 200ms for video playback start
- 99.99% availability
- Global scale (200+ countries)

### Scale Estimation

```
DAU: 1B
Daily video views: 5B
Daily uploads: 720K hours (500 hours/min × 1440 min)
Storage per video: 50MB-10GB (depending on quality/resolution)
Daily storage: 720K × 500MB (avg) = 360TB/day
Bandwidth: 5B views × 100MB (avg) / 86400s = 5.8TB/s peak
Cache storage: 20% of popular videos = 100PB (read: 1:1 cache hit ratio critical)
```

### Architecture Components

**Upload path:**
```
User → [Upload Service] → [Transcoding Pipeline] → [CDN Origin]
                                         → [Object Store]
                                         → [Metadata DB]
```

**Watch path:**
```
User → [CDN Edge] → [API Gateway] → [Watch Service] → [Metadata Cache/CDN]
                                       → [Recommendation Service] → [ML Model]
```

### Key Decisions

**Storage:**
- Transcoding output stored in a distributed object store (Colossus/GFS)
- Hot videos on SSD cache, warm on HDD, cold on archival
- Multi-region replication for availability

**CDN:**
- Google's global CDN edge caches (400+ edge locations)
- Adaptive bitrate streaming (HLS/DASH) for varying network conditions
- Prefetching based on watch patterns

**Recommendation:**
- Two-tower neural network model
- Candidate generation (collaborative filtering → content-based)
- Ranking (deep neural network with user features)
- Real-time model updates based on user interactions

### Failure Scenarios

| Failure | Mitigation |
|---------|-----------|
| CDN edge down | Failover to adjacent edge |
| Transcoding failure | Retry with different encoder configuration |
| Upload corruption | Client-side checksum verification |
| Metadata DB overload | Read replicas, sharding by video ID |
| Recommendation cold start | Popular videos as fallback |

---

## 6. Deep Dive: Design Google Drive

### Requirements

**Functional:**
- Upload and download files
- File sync across devices
- File sharing with permissions
- Version history
- Real-time collaboration (Google Docs integration)

**Non-functional:**
- 1B+ users
- Files up to 5TB (enterprise)
- P99 latency < 500ms for file operations
- Strong consistency for file metadata
- Eventually consistent for file content

### Architecture

**Key components:**
```
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ Client Apps  │ │  Web Client │ │  Third-party │
│ (Desktop/    │ │  (Browser)  │ │  API         │
│  Mobile)     │ │             │ │               │
└──────┬───────┘ └──────┬──────┘ └──────┬────────┘
       │                │               │
       └────────────────┼───────────────┘
                        │
                  ┌─────▼──────┐
                  │ API Gateway│
                  │ (TLS, Auth)│
                  └─────┬──────┘
                        │
          ┌─────────────┼─────────────┐
          │             │             │
    ┌─────▼────┐ ┌─────▼────┐ ┌──────▼─────┐
    │ Metadata │ │ Sync     │ │ Content    │
    │ Service  │ │ Service  │ │ Service    │
    │ (Bigtable)│ │ (OT/CRDT)│ │ (Colossus) │
    └──────────┘ └──────────┘ └────────────┘
```

### Sync Conflict Resolution

- **Last writer wins** for simple conflicts
- **Operational Transformation** for collaborative editing
- **Version vector** for detecting conflicts across devices
- **Manual merge** for unresolvable conflicts

---

## 7. Leadership Round (Googleyness)

### What They Evaluate

- **Intellectual humility**: Do you acknowledge what you don't know?
- **Collaboration**: Can you work effectively with diverse teams?
- **Conflict resolution**: How do you handle disagreement?
- **Ambiguity**: Can you make progress without complete information?
- **Growth mindset**: Do you learn from failures and feedback?

### Sample Questions

1. "Tell me about a time you had to lead a team through a difficult technical challenge."
2. "Describe a situation where you had to influence someone without authority."
3. "Tell me about a time you received critical feedback and how you responded."
4. "How do you approach technical disagreements with peers?"
5. "Describe a project that failed and what you learned from it."
6. "Tell me about a time you had to make a decision with incomplete information."
7. "How do you stay current with technology and engineering best practices?"
8. "Tell me about a time you went above and beyond what was expected."

---

## 8. Coding Expectations at L6+

### LeetCode Level

- **Difficulty**: Hard (medium is the minimum)
- **Topics**: Graphs, trees, dynamic programming, string manipulation, concurrency
- **Style**: Clean, well-structured, object-oriented when appropriate

### Evaluation Criteria

| Criteria | Weight | What They Look For |
|----------|--------|-------------------|
| Correctness | 30% | Solution works for all edge cases |
| Efficiency | 25% | Optimal time and space complexity |
| Code quality | 20% | Clean, readable, well-structured code |
| Communication | 15% | Explains approach before coding |
| Testing | 10% | Identifies edge cases and tests them |

### Language Choice

- **Most common**: C++, Java, Python, Go
- **Google's preference**: C++ for systems, Java/Go for services, Python for scripting
- **Choose your strongest language**: You'll perform best in a language you use daily

---

## 9. Evaluation Rubric

### Overall Score (1-4)

| Score | Meaning | Hire Probability |
|-------|---------|----------------|
| 4 | Strong hire | >90% |
| 3 | Hire | 60-90% |
| 2 | Lean hire / mixed | 30-60% |
| 1 | No hire | <30% |

### L6+ Must-Haves

| Factor | Must Demonstrate |
|--------|-----------------|
| **Scale** | Designs for global scale (billions of users) |
| **Depth** | Deep expertise in at least one domain area |
| **Trade-offs** | Considers multiple alternatives with data |
| **Failure modes** | Proactively identifies and mitigates failure scenarios |
| **Leadership** | Evidence of technical direction setting |

### Common Rejection Reasons at L6+

1. **Could not handle scale**: Design worked for thousands but not billions
2. **No trade-off analysis**: Jumped to one solution without considering alternatives
3. **Missed failure scenarios**: Did not address what happens when components fail
4. **Weak leadership stories**: No evidence of technical influence beyond the team
5. **Poor communication**: Could not explain complex ideas clearly

---

## 10. Preparation Strategy

### Week 1-2: Foundation
- Review distributed systems fundamentals (Paxos, Raft, consistent hashing, gossip protocols)
- Practice back-of-envelope calculations
- Review Google-specific infrastructure (Borg, Colossus, Spanner, Bigtable, Chubby)

### Week 3-4: System Design Practice
- Design 4-5 Google-scale systems (YouTube, Drive, Search, Maps, Gmail)
- Time yourself (45-60 minutes per design)
- Practice explaining your thought process aloud

### Week 5-6: Behavioral Preparation
- Prepare 5-7 leadership stories using STAR format
- Practice Googleyness answers
- Prepare questions to ask interviewers

### Must-Know Google Technologies

| Technology | Purpose | Relevance |
|-----------|---------|-----------|
| Borg | Cluster management | Service deployment, resource management |
| Colossus/GFS | Distributed file system | Storage design |
| Spanner | Global database | Strong consistency at global scale |
| Bigtable | NoSQL database | Wide-column storage |
| Pub/Sub | Event messaging | Async communication |
| Dremel | Interactive analysis | Analytics design |
| MapReduce | Batch processing | Data pipeline design |

---

*Combine this guide with the ACADEMY_INTERVIEW_GUIDE.md for complete Google L6+ interview preparation.*

---

## Appendix A: Google System Design Question Deep Dive — Design Google Search

### Search Architecture Overview

```
Crawling → Indexing → Serving
  ↓           ↓         ↓
URL Frontier → Indexer → Query Processor
  ↓           ↓         ↓
Content Fetch  Inverted Index  Ranking (PageRank + ML)
```

**Crawling:**
- Distributed web crawlers (Caffeine system)
- URL frontier for prioritization (PageRank, freshness)
- Polite crawling: robots.txt, rate limiting, crawl delay
- Duplicate detection: content fingerprinting (simhash)

**Indexing:**
- Inverted index: term → list of documents
- Forward index: document → list of terms
- Compression: variable-length encoding, delta encoding
- Index segmentation: fresh index, main index

**Serving:**
- Query parsing and rewriting
- Document ranking (PageRank, TF-IDF, BERT)
- Result aggregation and formatting
- Snippets generation (query-biased summaries)

## Appendix B: Google Leadership Principles in Depth

### Technical Leadership (L6+)
Google evaluates technical leadership through: architectural vision, cross-team influence, and technical mentorship. Prepare stories demonstrating: setting technical direction for a team, influencing decisions across teams without authority, and mentoring senior engineers through design reviews and technical guidance.

### Googleyness Evaluation
- **Intellectual humility**: Acknowledge what you don't know, show willingness to learn
- **Collaboration**: Demonstrate how you've worked effectively across diverse teams
- **Conflict resolution**: Show how you've handled disagreement professionally
- **Ambiguity handling**: Provide examples of making progress without complete information

## Appendix C: Google Technology Stack Reference

| Technology | Type | Relevance |
|-----------|------|-----------|
| Borg/Omega | Cluster Management | Resource scheduling, multi-tenancy |
| Colossus/GFS2 | File System | Petabyte-scale storage |
| Bigtable | NoSQL DB | Wide-column storage for web indexing |
| Spanner | SQL DB | Global-scale transactions |
| Chubby | Lock Service | Distributed coordination |
| Pregel | Graph Processing | PageRank, social graph |
| MapReduce/Flume | Batch Processing | Data pipeline |
| Dremel | Interactive Analysis | Ad-hoc queries at scale |
| Pub/Sub | Messaging | Async event distribution |
| TensorFlow | ML Framework | ML model training and serving |

## Appendix D: Common Google Interview Mistakes

1. **Not asking clarifying questions** — Jumping to a solution before understanding the problem
2. **Ignoring non-functional requirements** — Designing only for functionality, not scale, latency, or reliability
3. **Single solution focus** — Not considering alternative approaches and their trade-offs
4. **No failure analysis** — Not discussing what happens when components fail
5. **Weak estimation** — Cannot compute approximate QPS, storage, or bandwidth
6. **No operational thinking** — Not discussing deployment, monitoring, or incident response
