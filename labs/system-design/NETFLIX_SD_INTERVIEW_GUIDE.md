# Netflix System Design Interview Guide

> Comprehensive guide to system design interviews at Netflix.

---

## Table of Contents

1. [Interview Process](#1-interview-process)
2. [Top 5 System Design Problems](#2-top-5-system-design-problems)
3. [Detailed Solution Frameworks](#3-detailed-solution-frameworks)
4. [Evaluation Criteria](#4-evaluation-criteria)
5. [Design Philosophy](#5-design-philosophy)
6. [Real Interview Stories](#6-real-interview-stories)
7. [Prep Strategy](#7-prep-strategy)

---

## 1. Interview Process

### Rounds
- **Phone screen**: 45 min (technical deep dive on your experience)
- **Onsite (virtual)**: 4-5 rounds
  - 2-3 system design rounds (Netflix focuses heavily on design)
  - 1-2 coding rounds (not LeetCode — real-world scenarios)
  - 1 cultural round ("Freedom & Responsibility")

### System Design Specifics
- **Format**: Whiteboard or shared document
- **Duration**: 60-90 min per round (longer than other companies)
- **Structure**: Less structured — deep conversation on a specific area
- **Level**: L3-L4 = 2 rounds; L5+ = 3+ rounds

### Key Difference
- Netflix does NOT do LeetCode-style coding
- Coding rounds involve debugging or extending a real service
- Cultural fit is evaluated throughout — "Freedom & Responsibility" is real
- Interviewers are senior engineers who deeply understand their domain

---

## 2. Top 5 System Design Problems

| # | Problem | Key Concepts |
|---|---------|-------------|
| 1 | Design Netflix Video Streaming | Transcoding ladder, CDN, adaptive bitrate, DASH/HLS |
| 2 | Design Netflix Recommendation | ML pipeline, collaborative filtering, multi-armed bandit |
| 3 | Design Open Connect CDN | ISP appliance, pre-population, traffic routing |
| 4 | Design API Gateway (Zuul) | Routing, circuit breaker, rate limiting, resilience |
| 5 | Design Chaos Engineering Platform | Failure injection, blast radius, automated mitigation |

### Other Problems
- Design content ingestion pipeline
- Design Netflix's data platform (Big Data infrastructure)
- Design A/B testing platform (unified experimentation)
- Design Netflix's encoding optimization (per-title encoding)

---

## 3. Detailed Solution Frameworks

### Problem 1: Design Video Streaming

**Requirements**: 240M+ subscribers, 100M+ concurrent streams, 4K HDR

**Architecture**:
- **Ingestion**: Source mastering → mezzanine file → encoding pipeline
- **Encoding**: Per-title optimized encoding (not one-size-fits-all). Resolution ladder determined by content complexity.
- **Storage**: Amazon S3 for origin (multi-region)
- **CDN**: Open Connect appliances embedded in ISP networks
- **Playback**: DASH/HLS manifest → client selects segments at appropriate resolution

**Key Innovation: Per-Title Encoding**
- Simple content (talking head): Lower bitrate needed
- Complex content (action movie): Higher bitrate needed
- ML model predicts optimal encoding parameters per scene

### Problem 2: Design Recommendation

**Pipeline**:
1. **Offline training**: Daily batch processing → user embeddings, content embeddings
2. **Nearline**: Real-time feature computation (views, likes, dwell time)
3. **Online**: User request → candidate generation → ranking → blending → personalization

**Algorithms**:
- Collaborative filtering (user-user, item-item)
- Content-based (genre, director, actor similarity)
- Deep learning (Autoencoders, NCF, DNN)

### Problem 3: Design Open Connect CDN

**Architecture**:
- ISP-partnered appliances deployed in ISP data centers
- DNS-based routing (route users to nearest appliance)
- Pre-population: Popular content proactively pushed to appliances
- On-demand: Less popular content fetched from regional cache
- Traffic optimization: Serve 95%+ of traffic from Open Connect

---

## 4. Evaluation Criteria

| Criterion | Weight | Netflix Expectation |
|-----------|--------|---------------------|
| Resilience obsession | 25% | Failure is inevitable — design for it |
| Trade-off articulation | 20% | Clear cost-benefit analysis |
| Operational maturity | 20% | Monitoring, runbooks, canary deployment |
| Depth vs breadth | 20% | Can go deep when asked |
| Innovation mindset | 15% | Challenges assumptions, proposes creative solutions |

---

## 5. Design Philosophy

### Core Principles
1. **Build for failure**: Chaos Monkey kills instances in production. Design accordingly.
2. **Freedom and responsibility**: Engineers own their services end-to-end
3. **Data-driven decision making**: Everything is A/B tested
4. **CDN-first**: Open Connect is the backbone of Netflix's delivery

### Netflix's Technology Stack
| Area | Technology |
|------|-----------|
| Cloud | AWS (multi-region) |
| CDN | Open Connect (custom hardware in ISPs) |
| Compute | Titus (container platform), EVCache (caching on EC2) |
| DB | Cassandra, EVCache, MySQL, Elasticsearch |
| Streaming | DASH/HLS, DASH Industry Forum |
| Monitoring | Atlas (time-series), Spinnaker (deployment) |
| Resilience | Hystrix (circuit breaker), Chaos Monkey, Chaos Kong |

---

## 6. Real Interview Stories

### Story 1: Senior — Video Pipeline Resilience
> **Question**: "What happens when our CDN fails?"
> **Candidate solution**: Multi-CDN strategy with automatic failover, regional cache layers, and adaptive bitrate that degrades gracefully before buffering.

### Story 2: Staff — Open Connect Capacity Planning
> **Question**: "How do you handle Stranger Things season premiere (50x traffic spike)?"
> **Candidate proposal**: Predictive pre-population based on release calendar, subscriber interest signals, and regional popularity forecasts.

---

## 7. Prep Strategy

### Study Focus
- Hystrix, Chaos Monkey, Eureka (Netflix OSS)
- Open Connect architecture (Netflix Tech Blog)
- Per-title encoding optimization
- Multi-armed bandit for recommendation exploration
- CDN routing and DNS-based traffic steering

### Labs to Focus On
- 03-availability (failure handling, SLA, redundancy)
- 05-caching (CDN, cache strategies)
- 08-observability (monitoring, metrics, tracing)
- 15-design-video-streaming (DASH/HLS, CDN)

### Must-Read
- Netflix Tech Blog (medium.com/netflix-techblog)
- Hystrix documentation and papers
- Chaos Engineering (book by Casey Rosenthal)
- "Performance Under Pressure" — Netflix engineering talks
