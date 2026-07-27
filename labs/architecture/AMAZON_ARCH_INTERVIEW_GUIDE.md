# Amazon Architecture Interview Guide (Principal L7/L8)

> Principal Engineer system design and leadership evaluation at Amazon.

---

## Table of Contents

1. [Amazon's Engineering Culture](#1-amazons-engineering-culture)
2. [Principal Level Expectations](#2-principal-level-expectations)
3. [The 16 Leadership Principles](#3-the-16-leadership-principles)
4. [System Design Interview Format](#4-system-design-interview-format)
5. [Common Amazon System Design Questions](#5-common-amazon-system-design-questions)
6. [Deep Dive: Design Amazon Shopping Cart](#6-deep-dive-design-amazon-shopping-cart)
7. [Deep Dive: Design AWS IAM](#7-deep-dive-design-aws-iam)
8. [Bar Raiser Round](#8-bar-raiser-round)
9. [Working Backwards Methodology](#9-working-backwards-methodology)
10. [Principal Behavioral Expectations](#10-principal-behavioral-expectations)
11. [Evaluation Rubric](#11-evaluation-rubric)
12. [Preparation Strategy](#12-preparation-strategy)

---

## 1. Amazon's Engineering Culture

### The Amazon Way

- **Customer obsession**: Every decision starts with the customer and works backward
- **Day 1 mentality**: Maintain startup-like agility regardless of company size
- **Disagree and commit**: Debate passionately, commit fully once a decision is made
- **Frugality**: Accomplish more with less. Waste is not tolerated
- **Bias for action**: Speed matters in business. Many decisions are reversible

### What Amazon Values at Principal+

- **Think Big**: A vision that enables new capabilities for customers
- **Dive Deep**: Can operate at all levels — from strategy to implementation details
- **Ownership**: Thinks long-term, doesn't sacrifice long-term value for short-term results
- **Have Backbone**: Challenges decisions when they don't serve customers, even when uncomfortable

---

## 2. Principal Level Expectations

### L7 (Principal Engineer)

- Visionary leader who sets technical direction for a major organization (100+ engineers)
- Solves ambiguous problems where requirements don't exist yet
- Influence spans multiple teams, building consensus for technical strategy
- Deep expertise in a technical domain with broad knowledge across systems
- Mentors senior engineers and grows the next generation of technical leaders

### L8 (Senior Principal Engineer)

- Sets technical direction across the company
- Drives multi-year, company-wide technical initiatives
- Recognized externally as an industry expert
- Influences Amazon's overall engineering culture and practices

### Principal Tenets

| Tenet | Description |
|-------|-------------|
| **Scope** | Solves problems that span multiple teams/organizations |
| **Vision** | Creates technical strategy with 2-5 year horizon |
| **Delivery** | Drives execution of complex, cross-team initiatives |
| **Influence** | Builds consensus without authority |
| **Excellence** | Raises the technical bar across the organization |

---

## 3. The 16 Leadership Principles

### Must-Know for Every Interview Answer

| # | Principle | Key Phrase | Behavioral Story Focus |
|---|-----------|------------|----------------------|
| 1 | Customer Obsession | "Start with the customer and work backward" | Customer-driven feature, CX improvement |
| 2 | Ownership | "Never say 'that's not my job'" | End-to-end problem ownership |
| 3 | Invent and Simplify | "Expect and require innovation from your teams" | Simplifying complex systems |
| 4 | Are Right, A Lot | "Seek diverse perspectives, work to disconfirm your beliefs" | Making correct technical decisions |
| 5 | Hire and Develop the Best | "Raise the performance bar with every hire" | Mentoring, interviewing |
| 6 | Insist on the Highest Standards | "Continually raise the bar" | Quality improvement, incident prevention |
| 7 | Think Big | "A small thinking is a self-fulfilling prophecy" | Bold vision, ambitious goals |
| 8 | Bias for Action | "Speed matters in business" | Quick decision-making |
| 9 | Frugality | "Accomplish more with less" | Cost optimization |
| 10 | Learn and Be Curious | "Never stop learning" | Technology adoption, personal growth |
| 11 | Dive Deep | "Stay connected to the details" | Deep technical analysis |
| 12 | Have Backbone; Disagree and Commit | "Challenge decisions when you disagree" | Standing up for what's right |
| 13 | Deliver Results | "Focus on key inputs and deliver with quality" | Project completion, measurable impact |
| 14 | Strive to be Earth's Best Employer | "Create a safe, productive work environment" | Team culture |
| 15 | Success and Scale Bring Broad Responsibility | "Start with humility" | Ethical decision-making |
| 16 | Earn Trust | "Listen attentively, speak candidly" | Building credibility |

### How to Use LPs in Interviews

**Do**: Weave LPs naturally into your answers. "Because of my focus on Customer Obsession, I..."

**Don't**: List LPs explicitly. "This demonstrates Customer Obsession, Ownership, and Dive Deep."

**Pattern**:
- Start with the LP principle that drove your decision
- Show how the LP conflicted with another LP and how you resolved it
- Conclude with the LP-aligned outcome

---

## 4. System Design Interview Format

### Structure

- **Duration**: 60 minutes
- **Format**: Whiteboard (remote: virtual whiteboard)
- **Focus**: AWS-native, scalable, cost-optimized architecture

### Time Allocation

| Phase | Time | Activity |
|-------|------|----------|
| Press release / FAQ | 5 min | Write a press release for your design |
| Requirements | 5 min | Functional and non-functional requirements |
| Scale estimation | 5 min | QPS, storage, bandwidth, cost |
| High-level design | 15 min | AWS service selection, architecture diagram |
| Deep dive | 20 min | Detailed component design, trade-offs |
| Cost analysis | 5 min | Rough cost estimate |
| Summary | 5 min | Recap, alternatives, future work |

### What Interviewers Evaluate

1. **AWS service knowledge**: Can you design using AWS services effectively?
2. **Cost awareness**: Do you consider Frugality in your design?
3. **Customer focus**: Does your design prioritize customer experience?
4. **Trade-off analysis**: Do you consider multiple approaches?
5. **Operational excellence**: Does your design handle failures gracefully?

---

## 5. Common Amazon System Design Questions

### Tier 1 (Amazon-specific)

| Question | Key Focus Areas |
|----------|----------------|
| Design Amazon Shopping Cart | Order management, inventory, pricing |
| Design Amazon Recommendation Engine | Real-time ML, personalization |
| Design Amazon Delivery Logistics | Route optimization, real-time tracking |
| Design Amazon Search | Product indexing, faceted search, relevance |
| Design Amazon Payment System | Authorization, fraud detection, settlements |

### Tier 2 (AWS-focused)

| Question | Key Focus Areas |
|----------|----------------|
| Design AWS IAM | Identity, authorization, scaling |
| Design DynamoDB | Distributed database, consistency, replication |
| Design AWS Lambda | Serverless compute, cold starts, scaling |
| Design Amazon S3 | Object storage, durability, consistency model |
| Design AWS Kinesis | Stream ingestion, processing, replay |

### Tier 3 (Cross-cutting)

| Question | Key Focus Areas |
|----------|----------------|
| Design Amazon Prime Video | Streaming, CDN, digital rights management |
| Design Alexa | Voice processing, NLU, device management |
| Design Amazon Go (Just Walk Out) | Computer vision, sensor fusion, real-time processing |

---

## 6. Deep Dive: Design Amazon Shopping Cart

### Requirements

**Functional:**
- Add/remove items from cart
- Update quantities
- Save for later
- Apply promotions/coupons
- Calculate estimated total (with tax, shipping)
- Multi-device sync

**Non-functional:**
- 300M+ active customers
- P99 latency < 100ms for cart operations
- Strong consistency (don't lose items)
- 99.99% availability during Prime Day
- Global: 20+ marketplaces

### Press Release (Working Backwards)

> **Title**: Amazon introduces unified shopping cart that works across all devices
>
> **Subtitle**: Customers can now start shopping on their phone, continue on their laptop, and purchase on their tablet — without losing a single item
>
> **Summary**: Today, Amazon launched a new shopping cart that works seamlessly across all devices. Customers can add items from any device and see their cart instantly updated on all others. The cart intelligently applies the best promotions and provides real-time delivery estimates.

### Architecture

```
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ Mobile App   │ │ Web Client   │ │ Alexa        │
│ iOS/Android  │ │ Browser      │ │ Voice        │
└──────┬───────┘ └──────┬───────┘ └──────┬───────┘
       │                │                │
       └────────────────┼────────────────┘
                        │
                  ┌─────▼──────┐
                  │ CloudFront │
                  │  + WAF     │
                  └─────┬──────┘
                        │
                  ┌─────▼──────┐
                  │  API GW    │
                  └─────┬──────┘
                        │
          ┌─────────────┼─────────────┐
          │             │             │
    ┌─────▼────┐ ┌─────▼────┐ ┌──────▼─────┐
    │ Cart     │ │ Pricing  │ │ Inventory  │
    │ Service  │ │ Service  │ │ Service    │
    │          │ │          │ │            │
    └────┬─────┘ └────┬─────┘ └──────┬─────┘
         │            │              │
    ┌────▼────┐  ┌────▼────┐  ┌─────▼─────┐
    │DynamoDB │  │Elasti-  │  │ DynamoDB  │
    │ Cart    │  │Cache    │  │ Inventory │
    │ Table   │  │(Pricing)│  │ Table     │
    └─────────┘  └─────────┘  └───────────┘
```

### Key Decisions

**Storage:**
- DynamoDB for cart items (key: customer ID + marketplace)
- DAX (DynamoDB Accelerator) for read-heavy operations
- Global Tables for multi-region cart access

**Consistency:**
- Strong consistency for cart operations within a region
- Eventually consistent for cross-region cart access
- Conflict resolution: last-writer-wins with merge

**Prime Day Scale:**
- Read-heavy: cache cart (ElastiCache) + DynamoDB DAX
- Write-heavy: DynamoDB auto-scaling with on-demand capacity
- Cart merge on login: SQS + Lambda for merging anonymous → authenticated cart

---

## 7. Deep Dive: Design AWS IAM

### High-Level Design

```
┌──────────┐  ┌──────────┐  ┌──────────┐
│ User     │  │ Service  │  │ AWS CLI  │
│ Console  │  │ (EC2)    │  │          │
└────┬─────┘  └────┬─────┘  └────┬─────┘
     │             │             │
     └─────────────┼─────────────┘
                   │
             ┌─────▼──────┐
             │ IAM API    │
             │ Endpoints  │
             │ (Regional) │
             └─────┬──────┘
                   │
          ┌────────┴────────┐
          │                 │
    ┌─────▼─────┐    ┌─────▼─────┐
    │ Auth      │    │ Policy    │
    │ Service   │    │ Evaluation│
    │           │    │ Engine    │
    └─────┬─────┘    └─────┬─────┘
          │                │
    ┌─────▼─────┐    ┌─────▼─────┐
    │ DynamoDB  │    │ DynamoDB  │
    │ Users/    │    │ Policies  │
    │ Roles     │    │           │
    └───────────┘    └───────────┘
```

### Policy Evaluation Logic

```
Request Context:
├── Principal (user/role)
├── Action (s3:GetObject)
├── Resource (arn:aws:s3:::my-bucket/*)
├── Conditions (IP, time, MFA)
└── Request Context (EC2 instance, VPC)

Evaluation:
1. Deny Evaluation: Any explicit deny → DENY
2. Allow Evaluation: Allow statement matches → ALLOW
3. Default: No match → DENY (implicit)
```

---

## 8. Bar Raiser Round

### Purpose

The Bar Raiser is an experienced interviewer who ensures the hiring bar is consistently high. They have veto power and are not part of the hiring team.

### What the Bar Raiser Evaluates

1. **Will this person raise the bar?** Would they be in the top 20% of current engineers?
2. **Can they operate at this level?** Do they have L7+ scope and impact?
3. **Do they embody Amazon's culture?** LPs, ownership, customer obsession
4. **Can succeed at Amazon?** Will they thrive in Amazon's unique culture?

### Common Bar Raiser Questions

- "Tell me about the most technically challenging problem you've solved"
- "Describe a time you had to convince a team to change direction"
- "Tell me about a project where you failed and what you learned"
- "How have you grown the engineers around you?"
- "Describe a time you had to make a quick decision with incomplete data"

### Bar Raiser Tips

- Be prepared for pushback — Bar Raisers challenge your answers to test depth
- Don't get defensive; view it as an opportunity to demonstrate depth
- Have concrete, measurable results for every story
- Show self-awareness: acknowledge what you could have done better

---

## 9. Working Backwards Methodology

### The Process

```
1. Start with the customer
   → Who is the customer? What do they need?

2. Write a press release
   → Announce the product as if it's already launched
   → Title, subtitle, summary, problem, solution, customer quotes

3. Write an FAQ
   → Internal FAQ: how does it work? What are the trade-offs?

4. Define the customer experience
   → Walk through every step of the customer journey

5. Write the user manual
   → How does the customer use this? What are the APIs?
```

### Example: Shopping Cart Press Release

```
FOR IMMEDIATE RELEASE

Amazon Launches Universal Shopping Cart

SEATTLE — July 15, 2024 — Amazon today announced a universal shopping cart
that works seamlessly across all devices. Customers can start shopping on
their phone, continue on their laptop, and purchase on their tablet.

"Previously, I'd add items to my cart on my phone and lose them when I
switched to my laptop," said John D., an Amazon customer. "Now my cart
follows me everywhere."

The universal cart intelligently applies promotions and provides real-time
delivery estimates. It's available today to all Amazon customers worldwide.
```

---

## 10. Principal Behavioral Expectations

### Must-Prepare Stories

**Customer Obsession (must have 2 stories):**
- "Tell me about a time you went above and beyond for a customer"
- "How have you used customer feedback to drive technical decisions?"

**Ownership (must have 2 stories):**
- "Describe a project you owned from end to end"
- "Tell me about a time you took on a problem outside your area"

**Think Big (must have 2 stories):**
- "Describe a bold technical vision you had"
- "How have you driven multi-year technical strategy?"

**Dive Deep (must have 2 stories):**
- "Tell me about a time you found a root cause that others missed"
- "Describe a situation where you had to understand the details to fix a problem"

**Have Backbone (must have 2 stories):**
- "Tell me about a time you disagreed with your manager"
- "Describe a situation where you challenged a technical decision"

**Deliver Results (must have 2 stories):**
- "Tell me about the most impactful project you delivered"
- "Describe a time you overcame significant obstacles to deliver"

### Story Template (STAR+LPs)

```
S: The customer problem we needed to solve
T: My role and what I needed to achieve
A: (LP1) I insisted on understanding customer needs...
   (LP2) I thought big and proposed a bold solution...
   (LP3) I dived deep into the implementation details...
   (LP4) I had backbone when others disagreed...
R: The measurable outcome: X% improvement, $Y savings, Z customer impact
```

---

## 11. Evaluation Rubric

### L7 Principal Scoring

| Criteria | Weight | L7 Expectation |
|----------|--------|---------------|
| System Design | 30% | AWS-native, multi-service, cost-aware, fault-tolerant |
| Leadership Principles | 25% | Stories demonstrate 8+ LPs naturally |
| Technical Depth | 20% | Deep expertise validated by challenging questions |
| Bar Raiser | 15% | Would re-hire; raises the bar |
| Communication | 10% | Clear, structured, persuasive |

### Common Rejection Reasons

1. **Not thinking big enough**: Designs that don't scale to Amazon's size
2. **Not diving deep enough**: Surface-level understanding without depth
3. **Poor LP alignment**: Stories don't demonstrate Amazon's principles
4. **No ownership mentality**: Blaming others, not taking responsibility
5. **Weak leadership evidence**: No multi-team influence or technical strategy

---

## 12. Preparation Strategy

### Week 1-2: Foundation
- Internalize ALL 16 Leadership Principles
- Prepare 2 stories per LP (32 total)
- Review AWS core services (S3, DynamoDB, Lambda, API Gateway, CloudFront, SQS, SNS, Kinesis)

### Week 3-4: System Design Practice
- Design 5-7 Amazon-scale systems using AWS services
- Practice "working backwards" — write press releases for each design
- Time yourself (60 minutes per design)

### Week 5-6: Behavioral Deep Practice
- Refine STAR+LPs stories
- Practice with peers who will push back like Bar Raisers
- Prepare questions about Amazon's technical challenges

### Must-Know AWS Services

| Service | Category | Relevance |
|---------|----------|-----------|
| DynamoDB | Database | Most exam-related service |
| Lambda | Compute | Serverless architecture |
| S3 | Storage | Object storage, durability |
| API Gateway | API | API management |
| CloudFront | CDN | Content delivery |
| SQS/SNS | Messaging | Async communication |
| Kinesis | Streaming | Event ingestion |
| Step Functions | Orchestration | Workflow |
| ECS/EKS | Container | Container orchestration |
| Route 53 | DNS | Traffic management |

---

*Combine this guide with the ACADEMY_INTERVIEW_GUIDE.md and COMPANY_INTERVIEW_GUIDE.md for complete Amazon Principal interview preparation.*
