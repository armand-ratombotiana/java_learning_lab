# Netflix Cloud Engineer Interview Guide

> Comprehensive prep guide for Netflix Cloud Engineer, SRE, and Platform Engineer roles.

---

## 1. Role Overview

### Cloud Engineer
- **Focus**: Manage AWS infrastructure, build internal tools, chaos engineering, content delivery.
- **Expectation**: You design and operate infrastructure at massive scale.
- **Levels**: Senior (IC5) → Staff (IC6) → Principal (IC7).
- **Languages**: Java (primary), Python, Go.

### Site Reliability Engineer (Less Common)
- **Focus**: Netflix has a "full site reliability" model — every engineer owns reliability.
- **Expectation**: SRE principles distributed to all teams.

### Platform Engineer
- **Focus**: Internal developer platform, tooling, Spinnaker, Titus.
- **Expectation**: Build platforms that enable other Netflix engineers.

---

## 2. Interview Process

```
Application → Recruiter Screen (30 min) → Technical Phone Screen (60 min) 
→ Onsite (4-5 rounds, 45 min each) → Offer
```

### Recruiter Screen
- **Length**: 30 minutes.
- **Content**: Culture fit, experience.
- **Tip**: Read Netflix's Culture Deck before this call. Know that "Freedom and Responsibility" is core.

### Technical Phone Screen
- **Length**: 60 minutes.
- **Format**: System design or coding (LeetCode Medium).
- **Content**: Architecture discussion about Netflix-scale systems.
- **Example**: "Design a video upload pipeline." "Design a CDN for video streaming."
- **Tip**: Think about failure, latency, and scale.

### Onsite Rounds

#### Round 1: System Design (45 min)
- **Topics**: Design Netflix CDN (Open Connect), encoding pipeline, personalization, AB testing.
- **Key areas**:
  - Content delivery: cache hierarchy, ISP peering, SSD caching (OCA).
  - Video encoding: chunked, per-title optimization.
  - Chaos engineering: failure injection, steady-state hypothesis.
- **Expectation**: Deep architectural thinking. Consider failure scenarios.

#### Round 2: System Design (45 min)
- **Another design problem**, possibly more focused.
- **Example**: "Design an AB experimentation platform." "Design the recommendation system."
- **Expectation**: Statistical significance, traffic splitting, metrics, rollback.

#### Round 3: Coding (45 min)
- **Difficulty**: LeetCode Medium.
- **Languages**: Java (primary), Python, Go.
- **Topics**: Data structures, algorithms, concurrency.
- **Tip**: Netflix uses Java heavily. Know Java concurrency, memory model, streams, Optional.

#### Round 4: Cultural/Contextual (45 min)
- **No STAR format** — direct, candid conversation.
- **Topics**:
  - "How do you make decisions without a rulebook?"
  - "Describe a project you started from nothing."
  - "Tell me about a time you had to make a trade-off."
  - "How do you handle a teammate who isn't performing?"
- **Tip**: Be honest, be specific, be candid.

#### Round 5: Operations/Chaos Engineering (45 min)
- **Scenario**:
  - "An AWS region goes down. How does Netflix survive?"
  - "Design a chaos experiment for a critical service."
  - "How do you measure resilience?"
- **Key concepts**: Fault domains, blast radius, steady-state hypothesis, automated rollback.

---

## 3. Key Technical Areas

### AWS at Scale
| Service | Depth | Netflix Specifics |
|---------|-------|-------------------|
| S3 | Expert | Petabytes of content, video chunks, CDN origin |
| EC2 | Expert | 100K+ instances, auto scaling, spot instances |
| DynamoDB | Expert | High-throughput metadata, user state |
| VPC | Expert | Multi-region networking, Direct Connect |
| CloudFront | Expert | Global CDN caching |
| IAM | Expert | Fine-grained roles for 1000s of services |
| Auto Scaling | Expert | Predictive scaling based on demand patterns |

### Chaos Engineering
| Concept | Description |
|---------|-------------|
| Steady state | Define normal behavior (metrics) |
| Hypothesis | "If I kill this, the system still works" |
| Blast radius | Limit scope of experiment |
| Failure injection | Chaos Monkey (kill instance), Chaos Kong (kill AZ), Latency Monkey |
| Automated rollback | If metrics degrade, stop experiment |

### Spinnaker (CI/CD)
| Feature | Purpose |
|---------|---------|
| Pipelines | Deploy stages (bake, deploy, verify, rollback) |
| Canary analysis | Automated comparison of baseline vs canary |
| Blue-green | Traffic switch between old and new |
| Manual judgment | Approval gates for production |

### Titus (Container Platform)
| Feature | Description |
|---------|-------------|
| Architecture | Container orchestration on AWS (pre-K8s) |
| Integration | Tightly coupled with Spinnaker |
| Networking | ENI-based per container |
| Storage | EBS volumes attached to containers |
| Scheduling | Resource-aware, affinity/anti-affinity |

### Content Delivery (Open Connect)
| Component | Description |
|-----------|-------------|
| Open Connect Appliance (OCA) | Custom hardware, cached content |
| ISP peering | Direct peering for lower cost |
| Cache hierarchy | Tier-1 (ISP), Tier-2 (region) |
| Streaming protocols | HLS, DASH, fMP4 |
| Adaptive bitrate | Context-aware (device, network, content) |

---

## 4. The Netflix Culture (Freedom and Responsibility)

### Core Values
1. **Judgment** — Make wise decisions despite ambiguity.
2. **Communication** — Be concise, candid, and transparent.
3. **Curiosity** — Learn rapidly and eagerly.
4. **Courage** — Say what you think, even if it's unpopular.
5. **Passion** — Inspire others with your love of technology.
6. **Selflessness** — Seek what's best for Netflix.
7. **Innovation** — Reject "that's how we've always done it."
8. **Inclusion** — Work with people from different backgrounds.

### Culture Deck Concepts
- **Freedom and Responsibility**: No vacation policy. No expense policy. Act in Netflix's best interest.
- **Context, not Control**: Provide context for decisions, not strict rules.
- **Highly Aligned, Loosely Coupled**: Alignment on goals, freedom in execution.
- **Stunning Colleagues**: Hire exceptional people.
- **Adequate Performance Gets a Generous Severance**: High performance bar.

### Interview Cultural Questions
| Question | What They Look For |
|----------|-------------------|
| "Tell me about a time you made a decision without approval." | Freedom and Responsibility |
| "How do you handle a teammate who isn't performing?" | Stunning colleagues, candor |
| "Describe a time you changed your mind." | Learning, judgment |
| "Tell me about a project you started with no direction." | Autonomy, innovation |

---

## 5. System Design — Netflix Focus

### Key Design Principles
1. **Assume everything fails**: Design for failure.
2. **Stateless where possible**: State in external services.
3. **Asynchronous communication**: Event-driven, queues.
4. **Defense in depth**: Multiple layers of resilience.
5. **Observability first**: Can't fix what you can't see.

### Common Design Problems
1. **Video encoding pipeline**: Input → analyze → chunked encode → package → CDN.
2. **CDN content delivery**: Open Connect cache → ISP edge → user.
3. **Recommendation system**: Personalization, collaborative filtering, real-time updates.
4. **AB experimentation platform**: Traffic splitting, metrics, statistical analysis.
5. **Chaos engineering platform**: Steady-state, hypothesis, injection, rollback.

---

## 6. Study Resources

### Books
- _Chaos Engineering_ (Rosenthal & Jones).
- _Building Microservices_ (Sam Newman).
- _Designing Data-Intensive Applications_ (Kleppmann).

### Online
- Netflix Tech Blog (medium.com/netflix-techblog).
- Netflix Culture Deck (slideshare).
- AWS Well-Architected Framework.
- Spinnaker Documentation.

---

## 7. Preparation Checklist

- [ ] Read and understand Netflix Culture Deck.
- [ ] Study chaos engineering principles.
- [ ] Master AWS at scale (S3, EC2, DynamoDB, CloudFront).
- [ ] Practice system design (5+ designs).
- [ ] Prepare candid cultural stories (no STAR).
- [ ] Practice Java (concurrency, streams, memory model).
- [ ] Study Spinnaker and Titus concepts.
- [ ] Understand CDN architecture.
- [ ] Prepare for "What's the hardest problem you've solved?"

---

_End of NETFLIX_DEVOPS_INTERVIEW_GUIDE.md_