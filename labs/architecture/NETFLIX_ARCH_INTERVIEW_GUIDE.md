# Netflix Architecture Interview Guide (Senior+)

> Senior/Staff Engineer system design and leadership evaluation at Netflix.

---

## Table of Contents

1. [Netflix's Engineering Culture](#1-netflixs-engineering-culture)
2. [Senior+ Level Expectations](#2-senior-level-expectations)
3. [System Design Interview Format](#3-system-design-interview-format)
4. [Common Netflix System Design Questions](#4-common-netflix-system-design-questions)
5. [Deep Dive: Design Netflix Content Delivery](#5-deep-dive-design-netflix-content-delivery)
6. [Deep Dive: Design Netflix Recommendation Engine](#6-deep-dive-design-netflix-recommendation-engine)
7. [Chaos Engineering Mindset](#7-chaos-engineering-mindset)
8. [Freedom and Responsibility: Behavioral Evaluation](#8-freedom-and-responsibility-behavioral-evaluation)
9. [Netflix Microservices Architecture](#9-netflix-microservices-architecture)
10. [Evaluation Rubric](#10-evaluation-rubric)
11. [Preparation Strategy](#11-preparation-strategy)

---

## 1. Netflix's Engineering Culture

### The Netflix Culture (Freedom and Responsibility)

- **Freedom with responsibility**: High autonomy, high expectations
- **Context over control**: Provide context, not directives — trust teams to decide
- **Highly aligned, loosely coupled**: Aligned on goals, independent on execution
- **Candor**: Direct, honest feedback is expected and valued
- **Keeper test**: Would you fight to keep this person? If not, let them go

### What Netflix Values at Senior+

- **Judgment**: Making smart decisions with incomplete information
- **Communication**: Clear, concise, candid communication
- **Curiosity**: Deep intellectual curiosity about technology and the business
- **Courage**: Willingness to take risks and challenge the status quo
- **Selflessness**: Putting the company and team ahead of yourself

---

## 2. Senior+ Level Expectations

### Senior Engineer

- Leads technically complex projects within a domain
- Makes architecture decisions that affect their team
- Mentors junior and mid-level engineers
- Deep expertise in one or more technical areas

### Staff Engineer

- Sets technical direction across multiple teams
- Drives architecture decisions with org-wide impact
- Recognized expert across the company
- Actively shapes engineering culture through mentoring, standards, and practices

### Netflix's Unique Leveling

Netflix does NOT have defined levels like Google or Amazon. Senior+ is loosely defined:
- **Senior Software Engineer**: Equivalent to L6 at other companies
- **Staff Software Engineer**: Equivalent to L7+

---

## 3. System Design Interview Format

### Structure

- **Duration**: 60 minutes
- **Format**: Open-ended discussion, whiteboard
- **Focus**: Netflix-specific infrastructure, microservices, chaos engineering

### Time Allocation

| Phase | Time | Activity |
|-------|------|----------|
| Problem clarification | 5 min | Understand what we're building and why |
| Requirements | 5 min | Functional and non-functional requirements |
| Scale estimation | 5 min | Traffic, storage, bandwidth |
| Architecture discussion | 25 min | Components, interactions, data flow |
| Deep dive | 15 min | Critical component deep discussion |
| Trade-offs | 5 min | Alternative approaches and why you chose this |

### Unique Aspects of Netflix Interviews

- **No script**: Interviews are organic conversations, not predefined questions
- **Business context**: Always discuss how technical decisions impact the business
- **Cost-performance**: Netflix is extremely conscious of infrastructure costs
- **Chaos engineering**: Design for failure from the start

---

## 4. Common Netflix System Design Questions

### Tier 1 (Netflix-specific)

| Question | Key Focus Areas |
|----------|----------------|
| Design Netflix CDN (Open Connect) | Content delivery, edge caching, peering |
| Design Netflix Recommendation Engine | Real-time ML, personalization, A/B testing |
| Design Netflix Video Encoding Pipeline | Transcoding, quality optimization, per-title encoding |
| Design Netflix Member Experience | Personalization, device sync, AB testing, profiles |
| Design Netflix Studio Platform | Content production, asset management, global workflow |

### Tier 2 (Infrastructure)

| Question | Key Focus Areas |
|----------|----------------|
| Design Netflix's Microservices Platform | Service mesh, container orchestration, CI/CD |
| Design Netflix Observability Platform | Monitoring, alerting, distributed tracing |
| Design Netflix Chaos Platform | Chaos Engineering, failure injection, resilience testing |

---

## 5. Deep Dive: Design Netflix Content Delivery

### Requirements

**Functional:**
- Deliver video content to 260M+ members worldwide
- Adaptive bitrate streaming for varying network conditions
- Instant start (minimal buffering)
- Content protection (DRM)
- Multi-device support (TV, mobile, web, tablet)

**Non-functional:**
- P99 startup time < 3 seconds
- 99.999% availability for popular content
- Global: 190+ countries
- Cost-efficient: minimize CDN and bandwidth costs

### Open Connect CDN Architecture

```
Netflix Origin → [Open Connect Appliances] → [ISP/IXP Peering]
    ↓                  ↓                          ↓
Transcoding → [Storage Servers] → [Edge Caches] → [Member Device]
(Encoding)   (SSD + HDD)       (RAM + SSD)
```

### Key Components

**Open Connect Appliances (OCAs):**
- Netflix-managed CDN servers deployed at ISP peering points
- Each OCA: 100TB+ storage, 100Gbps network throughput
- Distributed globally (1000+ locations)
- Pre-populated with popular content

**Content ingestion:**
- Content encoded in multiple bitrates (per-title encoding optimization)
- Stored as segmented MP4 files (DASH and HLS)
- Encrypted with DRM (Widevine, FairPlay, PlayReady)

**Adaptive bitrate (ABR) algorithm:**
- Per-title encoding: each title analyzed for optimal encoding parameters
- Multiple bitrates: 100Kbps to 40Mbps (depending on resolution)
- ABR logic on client selects optimal bitrate based on network conditions
- Buffer-based, throughput-based, or hybrid ABR

### Key Decisions

**Why build Open Connect instead of using commercial CDNs?**
- Cost: Netflix serves 15% of global internet traffic; commercial CDNs would be too expensive
- Control: Can optimize for streaming video specifically
- Performance: OCAs at ISP peering points reduce latency

**Content placement strategy:**
- Popular content: replicated to all OCAs
- Medium popularity: regional OCAs
- Long-tail content: on-demand fetch from origin

---

## 6. Deep Dive: Design Netflix Recommendation Engine

### Requirements

**Functional:**
- Personalized recommendations for 260M+ members
- Multiple recommendation surfaces (home page, genre rows, search)
- Real-time updates based on user interactions
- Support for A/B testing recommendation algorithms
- Explainability (why was this recommended?)

**Non-functional:**
- P99 latency < 200ms for recommendation generation
- 10M+ recommendation requests per second
- Model training: daily incremental, weekly full retrain
- Support for 1000+ A/B experiments simultaneously

### Architecture

```
Member Interaction → [Event Bus] → [Feature Pipeline]
                                      ↓
User → [Request] → [Candidate Generator] → [Ranking Model]
                          ↓                      ↓
                 [Multiple Sources]          [DNN Model]
                          ↓                      ↓
                  [Candidate List]        [Scored Candidates]
                          ↓                      ↓
                    [Blending & Re-ranking] → [Diversification]
                                                    ↓
                                            [Personalized Feed]
```

### Recommendation Pipeline

**Stage 1: Candidate generation (multiple sources):**
- Collaborative filtering: "Users who watched X also watched Y"
- Content-based: Similar genres, actors, directors
- Trending: What's popular in your region
- Continue watching: In-progress content
- Top picks: Personalized selections

**Stage 2: Ranking (deep neural network):**
- Features: user preferences, viewing history, time of day, device type
- Model: Multi-layer DNN with embedding layers
- Training: Daily incremental updates based on user interactions

**Stage 3: Blending and diversification:**
- Mix candidates from different sources
- Ensure diversity (avoid recommending same genre repeatedly)
- A/B testing: which algorithm wins for this user segment

### A/B Testing at Netflix

- Every recommendation change is A/B tested
- Metrics: engagement (hours watched, completion rate), retention, satisfaction
- 1000+ concurrent experiments
- Cell-based experimentation (users assigned to experiment cells)

---

## 7. Chaos Engineering Mindset

### Netflix's Chaos Engineering Principles

1. **Build systems assuming they will fail**: Design for failure from the start
2. **Test failure scenarios proactively**: Don't wait for failures to happen
3. **Automate resilience testing**: Regular, automated failure injection
4. **Learn from every failure**: Postmortems without blame

### Chaos Engineering Tools

| Tool | What It Does | What It Tests |
|------|-------------|---------------|
| Chaos Monkey | Randomly terminates instances | Service survivability |
| Chaos Kong | Simulates AWS region failure | Regional failover |
| Latency Monkey | Introduces artificial delays | Timeout and retry handling |
| Conformity Monkey | Finds instances not following best practices | Configuration drift |
| Doctor Monkey | Checks instance health | Health checks and monitoring |

### Interview Application

**When designing a system, always discuss:**
- "How does this system handle instance failure?"
- "What happens when a data center goes down?"
- "How do we test these failure scenarios?"
- "What's our blast radius for a service failure?"

---

## 8. Freedom and Responsibility: Behavioral Evaluation

### Key Behavioral Themes

**Judgment:**
- "Tell me about a high-stakes technical decision you made"
- "How do you make decisions when you have incomplete information?"
- "Describe a time you chose the harder technical path for the right reasons"

**Communication:**
- "Tell me about a time you delivered difficult feedback"
- "How do you explain complex technical concepts to non-technical stakeholders?"
- "Describe a situation where clear communication was critical to success"

**Curiosity:**
- "What's the most interesting technical challenge you've solved recently?"
- "How do you stay current with technology trends?"
- "Tell me about something you taught yourself outside of work"

**Courage:**
- "Tell me about a time you took a calculated risk"
- "Describe a situation where you challenged the status quo"
- "Tell me about a time you had to make an unpopular decision"

**Selflessness:**
- "How have you helped a teammate succeed at the expense of your own goals?"
- "Tell me about a time you prioritized the company's interests over your team's"
- "Describe a situation where you shared credit for success and took blame for failure"

### The Keeper Test

Every interviewer asks themselves: "Would I fight to keep this person on my team?" Your answers should make the answer a resounding yes.

---

## 9. Netflix Microservices Architecture

### Architecture Overview

```
┌──────────────────────────────────────────────────┐
│                  Client Tier                      │
│  (TV, Mobile, Web, Tablet Apps)                  │
└──────────────────────┬───────────────────────────┘
                       │
┌──────────────────────▼───────────────────────────┐
│                  Zuul API Gateway                 │
│  (Routing, Auth, Rate Limiting, Edge Services)   │
└──────┬─────────────┬─────────────┬───────────────┘
       │             │             │
┌──────▼───┐  ┌──────▼───┐  ┌──────▼──────────────┐
│  User    │  │ Content  │  │ Personalization     │
│  Service │  │ Service  │  │ Service             │
│          │  │          │  │ (Recommendations)    │
└──────────┘  └──────────┘  └─────────────────────┘
       │             │             │
┌──────┴─────────────┴─────────────┴───────────────┐
│              Service Mesh (Cloud)                 │
│  (Eureka, Ribbon, Hystrix, Archaius, Karyon)     │
└──────────────────────────────────────────────────┘
       │             │             │
┌──────┴─────────────┴─────────────┴───────────────┐
│              Data Tier                            │
│  (Cassandra, EVCache, S3, Kafka, Elasticsearch)  │
└──────────────────────────────────────────────────┘
```

### Key Characteristics

- **Fine-grained services**: Hundreds of microservices, each with a focused responsibility
- **Language agnostic**: Java, Node.js, Python, Go — best language for each service
- **Cloud-native**: Built for AWS, designed for failure
- **Chaos engineering**: Built-in resilience through continuous failure testing
- **Immutable infrastructure**: Everything is ephemeral; redeploy rather than fix

---

## 10. Evaluation Rubric

### Senior+ Scoring

| Criteria | Weight | Senior+ Expectation |
|----------|--------|-------------------|
| System Design & Architecture | 30% | Cloud-native, resilient, cost-aware |
| Chaos Engineering Mindset | 20% | Designs for failure, proactive testing |
| Technical Depth | 20% | Deep expertise demonstrated through discussion |
| Culture Fit (Freedom & Responsibility) | 20% | High autonomy, good judgment, candor |
| Communication | 10% | Clear, concise, persuasive |

### Common Rejection Reasons

1. **Not resilient enough**: Designs that don't handle failure scenarios
2. **Poor cultural fit**: Not aligned with Freedom and Responsibility
3. **No chaos engineering experience**: Unfamiliar with proactive failure testing
4. **Weak technical depth**: Surface-level understanding without depth
5. **No business awareness**: Technical decisions not connected to business outcomes

---

## 11. Preparation Strategy

### Week 1-2: Foundation
- Study Netflix's tech blog (netflixtechblog.com)
- Understand Open Connect CDN architecture in depth
- Review chaos engineering principles and tools

### Week 3-4: System Design Practice
- Design 5-7 Netflix-scale systems (CDN, recommendation, encoding, member experience)
- Practice incorporating chaos engineering into designs
- Time yourself (60 minutes per design)

### Week 5-6: Behavioral Preparation
- Prepare stories demonstrating freedom, responsibility, and good judgment
- Practice candid communication scenarios
- Prepare to discuss technical failures and lessons learned

### Must-Know Netflix Technologies

| Technology | Purpose | Interview Relevance |
|-----------|---------|-------------------|
| Open Connect | CDN | Content delivery design |
| Zuul | API Gateway | Edge services |
| Eureka | Service Discovery | Microservices |
| Hystrix | Circuit Breaker | Resilience |
| EVCache | Distributed Cache | Caching strategy |
| Cassandra | Database | Data storage |
| Spinnaker | CI/CD | Deployment |
| Titus | Container Platform | Container orchestration |
| Atlas | Monitoring | Observability |

---

*Combine this guide with the ACADEMY_INTERVIEW_GUIDE.md and COMPANY_INTERVIEW_GUIDE.md for complete Netflix Senior+ interview preparation.*

---

## Appendix A: Netflix System Design — Encoding Pipeline

### Video Encoding Architecture

```
Source Content → [Ingest] → [Analysis] → [Per-title Encoding Optimization]
                                          ↓
                              ┌─────────────────────┐
                              │ Multi-pass Encoding  │
                              │ (x265, VP9, AV1)    │
                              └──────────┬──────────┘
                                         ↓
                              ┌─────────────────────┐
                              │ Packaging (DASH/HLS) │
                              │ Encryption (DRM)     │
                              └──────────┬──────────┘
                                         ↓
                              ┌─────────────────────┐
                              │ CDN Distribution     │
                              │ (Open Connect)       │
                              └─────────────────────┘
```

**Per-title encoding:**
- Each title analyzed for optimal encoding parameters
- Encoding ladder tailored to content complexity
- Results in 20-50% bitrate savings vs fixed encoding ladders
- VMAF (Video Multi-Method Assessment Fusion) for quality evaluation

## Appendix B: Netflix Microservices Patterns

### Key Patterns Used at Netflix

**1. Circuit Breaker (Hystrix):**
- Every service-to-service call wrapped in a circuit breaker
- Fallback options for degraded behavior
- Real-time circuit monitoring dashboard

**2. Service Discovery (Eureka):**
- Each service registers on startup
- Clients discover service instances via Eureka
- Round-robin or zone-aware load balancing via Ribbon

**3. Externalized Configuration (Archaius):**
- Dynamic configuration changes without restart
- Property files updated at runtime
- A/B testing configuration variants

**4. Bulkhead Pattern:**
- Thread pool isolation per downstream dependency
- Prevents one slow dependency from consuming all threads

## Appendix C: Netflix Culture — Freedom and Responsibility

### Key Cultural Values for Interview Preparation

**Judgment:**
- "Tell me about a high-stakes decision with incomplete information"
- "How do you know when to make a decision vs gather more data?"

**Communication:**
- "Describe a time you gave difficult but necessary feedback"
- "How do you explain complex technical decisions to executives?"

**Curiosity:**
- "What's the most interesting technical problem you've solved?"
- "How do you stay current with industry trends?"

**Courage:**
- "Tell me about a calculated risk you took"
- "Describe a time you challenged the status quo"

**Selflessness:**
- "How have you prioritized the team's success over your own?"
- "Tell me about sharing credit and accepting blame"

## Appendix D: Chaos Engineering Interview Questions

1. "Design a system that can survive the failure of an entire AWS region"
2. "How do you test failure scenarios in production?"
3. "What metrics would you monitor to detect service degradation?"
4. "How do you design a load testing framework?"
5. "What's your approach to incident response and postmortem culture?"

### Key Chaos Engineering Concepts
- Blast radius reduction through cell-based architecture
- Automated failure injection in staging and production
- Game Days: scheduled chaos engineering exercises
- Postmortem culture: blameless, learning-focused incident analysis
