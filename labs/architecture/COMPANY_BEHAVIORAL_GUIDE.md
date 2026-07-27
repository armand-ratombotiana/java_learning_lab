# Staff+ Behavioral Interview Guide

> Comprehensive guide to behavioral interviews for Staff/Principal/Distinguished Engineer roles — leading technical decisions, architecture influence, and organizational impact.

---

## Table of Contents

1. [The Staff+ Behavioral Framework](#1-the-staff-behavioral-framework)
2. [Leading Technical Decisions](#2-leading-technical-decisions)
3. [Driving Architectural Change](#3-driving-architectural-change)
4. [Mentoring and Growing Engineers](#4-mentoring-and-growing-engineers)
5. [Cross-Team Collaboration](#5-cross-team-collaboration)
6. [Handling Technical Debt](#6-handling-technical-debt)
7. [Influencing Without Authority](#7-influencing-without-authority)
8. [Defining Technical Strategy](#8-defining-technical-strategy)
9. [Company-Specific Behavioral Themes](#9-company-specific-behavioral-themes)
10. [STAR Answer Framework](#10-star-answer-framework)
11. [Common Behavioral Questions Bank](#11-common-behavioral-questions-bank)
12. [Anti-Patterns and Red Flags](#12-anti-patterns-and-red-flags)

---

## 1. The Staff+ Behavioral Framework

### What Staff+ Behavioral Interviews Evaluate

At the staff+ level, behavioral interviews are NOT about whether you're a nice person or a hard worker. They evaluate your ability to operate at scale — organizational scale, technical scale, and impact scale.

**The five dimensions of staff+ behavioral evaluation:**

| Dimension | What They Assess | Why It Matters |
|-----------|-----------------|---------------|
| **Technical Leadership** | Setting technical direction, making architecture decisions, raising the bar | Staff+ defines "how we build" not just "what we build" |
| **Organizational Impact** | Cross-team influence, breaking down silos, scaling yourself | Staff+ amplifies their impact through others |
| **Strategic Thinking** | Long-term planning, trade-off evaluation, business alignment | Staff+ connects technical decisions to business outcomes |
| **Execution at Scale** | Shipping complex projects, managing ambiguity, navigating dependencies | Staff+ delivers results through multi-team initiatives |
| **People Development** | Mentoring, hiring, creating growth opportunities | Staff+ builds the next generation of leaders |

### The Staff+ Mindset Shift

| Junior → Senior | Senior → Staff | Staff → Principal |
|----------------|---------------|------------------|
| "How do I solve this?" | "How does my team solve this?" | "How does the org solve this?" |
| Execute tasks | Plan projects | Define strategy |
| Write code | Design architecture | Influence organization |
| My contribution | My team's output | The org's technical direction |
| Seek guidance | Provide guidance | Create guidance systems |
| Follow best practices | Define best practices | Evolve best practices |

---

## 2. Leading Technical Decisions

### What Interviewers Look For

- **Decisiveness**: Can you make decisions with incomplete information?
- **Depth**: Do you understand the trade-offs of your decisions?
- **Communication**: Can you explain complex decisions to diverse audiences?
- **Outcome orientation**: Do your decisions lead to measurable results?

### Framework for Explaining Technical Decisions

```
Situation → Problem → Options → Decision → Outcome → Learning
```

### Sample Answer: Architecture Decision

**Question**: "Tell me about a time you made a significant architecture decision."

**STAR Answer**:

**Situation**: Our team was building a new payment processing platform. The existing system had grown organically for 5 years and could not handle the expected 10x growth in transaction volume.

**Task**: I needed to decide between evolving the monolith or migrating to a microservices architecture.

**Action**:
1. **Gathered data**: Analyzed transaction patterns, team velocity, deployment frequency, and incident metrics over 6 months
2. **Evaluated options**:
   - Option A (Evolve monolith): Lower risk, faster to implement, but limited scalability and team autonomy
   - Option B (Microservices): Higher initial cost, better long-term scalability, team independence
   - Option C (Hybrid): Decompose only the payment processing domain into services
3. **Built consensus**: Presented analysis to engineering leadership, product, and operations teams. Ran a 2-week architecture spike for the most complex service boundary
4. **Made decision**: Chose microservices with strangler fig migration. Documented the decision with ADRs (Architecture Decision Records)

**Result**:
- System processed 15x peak volume without incidents in first year
- Three teams now independently own and deploy their services
- Deployment frequency increased from bi-weekly to daily
- On-call incidents decreased by 60% due to better isolation

**Learning**: The data model decomposition was harder than expected. I would invest more time in database splitting strategy earlier.

### Key Technical Decision Questions

| Question | What They Evaluate |
|----------|------------------|
| Tell me about the most complex system you designed | Architecture depth, trade-off analysis |
| Describe a technical decision you made that you later regretted | Learning orientation, humility |
| How do you decide when to build vs buy? | Strategic thinking, cost awareness |
| Tell me about a time you disagreed with a technology choice | Influence, technical conviction |
| How do you evaluate new technologies? | Learning ability, systematic thinking |

---

## 3. Driving Architectural Change

### The Change Process

### 1. Identify the Need

Articulate why the change matters in terms stakeholders care about:
- **Business impact**: "This change will reduce time-to-market by 40%"
- **Cost impact**: "This will save $2M/year in infrastructure costs"
- **Risk impact**: "This will prevent the recurring P0 incidents we've had"

### 2. Build a Coalition

- **Identify stakeholders**: Who needs to support this? Who will resist?
- **Find allies**: Other teams who share your pain or vision
- **Create a working group**: Cross-team group to define the approach
- **Executive sponsor**: Get leadership buy-in for resources and prioritization

### 3. Create a Migration Plan

- **Phase 1 (Proof of concept)**: Small scope, 2-4 weeks, de-risk the approach
- **Phase 2 (Pilot)**: One team/domain, measure results, iterate
- **Phase 3 (Rollout)**: Gradual adoption with clear migration criteria
- **Phase 4 (Optimization)**: Learn from rollout, improve the approach

### 4. Communicate and Document

- **ADR (Architecture Decision Record)**: Document the decision, context, options, and trade-offs
- **RFC (Request for Comments)**: Socialize the proposal before implementation
- **Runbook**: Document migration instructions for teams

### 5. Measure Success

Define metrics before starting:
- Before/after comparison of key metrics
- Adoption rate across teams
- Developer satisfaction scores
- Incident reduction

### Sample Answer: Driving Change

**Question**: "Tell me about a time you drove a significant architectural change."

**Situation**: Our 5-year-old monolith had 1M+ lines of code, 6-month release cycles, and was blocking business growth. Every feature required coordination across 10+ engineers.

**Task**: I needed to lead the migration from monolith to microservices without disrupting ongoing business commitments.

**Action**:
- **Phase 0 (Readiness)**: Spent 4 weeks understanding the domain boundaries using DDD (domain storytelling, event storming). Identified 7 bounded contexts.
- **Phase 1 (Strangle)**: Created an API gateway. Extracted the first domain (user management) as a service. Ran both in parallel for 4 weeks.
- **Phase 2 (Accelerate)**: With proven pattern, extracted 2 more services per quarter. Created a shared library for common patterns (auth, logging, service discovery).
- **Phase 3 (Complete)**: After 18 months, the monolith only handled the remaining legacy domain. Set a final deprecation date.
- **Coordination**: Weekly architecture sync, monthly progress review with leadership, quarterly retro on migration learnings.
- **Resistance handling**: When one team resisted because of feature pressure, I offered to pair their engineers on the extraction, reducing their burden.

**Result**:
- Release cycle went from 6 months to weekly
- Team autonomy increased: 5 teams can now independently ship
- Incident severity dropped by 70% (better isolation)
- Infrastructure cost reduced by 30% (right-sized services)

---

## 4. Mentoring and Growing Engineers

### Mentoring at Staff+ Level

At the staff+ level, mentoring is not optional — it's expected. You must demonstrate how you've grown other engineers.

### Mentoring Strategies

**1. Formal mentorship:**
- Weekly 1:1s with defined goals
- Career progression planning
- Technical skill development plans

**2. Technical mentoring:**
- Design document reviews (leave the code to senior engineers, focus on architecture)
- Architecture discussion sessions
- Technical interview training

**3. Creating growth opportunities:**
- Delegating meaningful technical decisions
- Creating speaking opportunities (tech talks, conferences)
- Introducing engineers to cross-team projects
- Pushing engineers outside their comfort zone

**4. Feedback culture:**
- Specific, actionable, timely feedback
- Positive feedback in public, constructive feedback in private
- Regular feedback cadence (not just during review cycles)

### Sample Answer: Mentoring

**Question**: "Tell me about how you've helped grow engineers on your team."

**Situation**: Our team had 3 mid-level engineers who wanted to reach senior level but were struggling with technical scope and cross-team impact.

**Action**:
- Created individual growth plans for each engineer based on the org's senior+ criteria
- Each engineer owned a technically challenging project end-to-end
- I reviewed their design documents and provided structured feedback focused on trade-off thinking
- Paired them with cross-team stakeholders to practice influence without authority
- Created a weekly "architecture deep dive" session where engineers presented and defended designs

**Result**:
- 2 engineers were promoted to senior within 12 months
- 1 engineer became the team's go-to person for the billing domain
- The architecture deep dive series expanded to include 4 other teams

---

## 5. Cross-Team Collaboration

### The Staff+ Cross-Team Challenge

As a staff+ engineer, your work inevitably spans multiple teams. Your technical decisions affect teams you don't manage. Your success depends on collaboration, not authority.

### Collaboration Strategies

**1. Shared goals:**
- Frame the project as solving a shared problem, not "your team helping my team"
- Identify mutual benefits: "If we do X, both our teams get Y"
- Create joint success criteria

**2. Communication:**
- Over-communicate context: Why this matters, what's changing, when
- Written communication for async alignment (RFCs, design docs)
- Regular cross-team syncs with clear agendas and action items

**3. Dependency management:**
- Map all dependencies at the start of the project
- Create a dependency tracking document
- Have regular dependency check-ins, not just at standup
- Escalate blockers early with proposed solutions

**4. Conflict resolution:**
- Focus on data, not opinions
- A/B test when there's disagreement on approach
- Escalate with recommendations, not problems
- Build relationships before conflicts arise

### Sample Answer: Cross-Team Collaboration

**Question**: "Tell me about a time you had to collaborate across multiple teams."

**Situation**: Our team was building a new event platform that required changes to 7 different teams' services. Each team had competing priorities and different timelines.

**Action**:
- Created a cross-team working group with representatives from all 7 teams
- Mapped all dependencies and created a shared timeline
- Identified which changes were blocking vs parallelizable
- Created a progressive adoption approach: teams could adopt incrementally
- Held bi-weekly syncs with clear action items and owners
- When 2 teams couldn't commit to the timeline, I worked with their managers to find resources and offered to contribute engineers to their work

**Result**:
- Event platform launched on schedule with 7 teams integrated
- 3 teams adopted the platform ahead of schedule
- The cross-team working group became the model for future cross-team initiatives
- Saved an estimated 6 months compared to sequential dependency resolution

---

## 6. Handling Technical Debt

### Technical Debt at Staff+ Level

Staff+ engineers need a nuanced view of technical debt. You can't eliminate all debt — you need to manage it strategically.

### Strategic Debt Management

**1. Classification:**
- **Prudent and intentional**: Deliberate shortcuts with a plan to repay (e.g., "We'll use the simple approach now and refactor after the launch")
- **Reckless and intentional**: "We'll never fix this" (avoid)
- **Prudent and unintentional**: "We didn't know this would happen" (learning opportunity)
- **Reckless and unintentional**: "We have no idea how bad this is" (danger zone)

**2. Prioritization:**
- **Critical**: Actively blocking development or causing incidents → fix now
- **High**: Near-blocking, increasing maintenance cost → plan within 1-2 quarters
- **Medium**: Known inefficiency, not blocking → prioritize alongside features
- **Low**: Cleanup, nice-to-have → backlog / when in the area

**3. Communication:**
- Frame debt in business terms: "This debt adds 30% to feature development time"
- Make debt visible: tracked in the backlog with cost estimates
- Create a "technical debt budget": allocate 20% of capacity to debt reduction
- Celebrate debt reduction: "We reduced our test suite execution time by 15 minutes"

### Sample Answer: Technical Debt

**Question**: "Tell me about a time you had to balance shipping features with addressing technical debt."

**Situation**: Our team was on a tight deadline to ship a critical feature, but our codebase had accumulated significant test debt. Build times were 45 minutes, and flaky tests caused 3-4 build failures per week.

**Action**:
- Acknowledged with the team and product manager that test debt was a problem, not a nice-to-have
- Proposed a "20% rule": 1 day per week for test infrastructure improvement
- Prioritized the highest-impact fixes: flaky tests (immediate relief) → build optimization → test coverage
- Tracked metrics weekly: build time, flaky test rate, test coverage %
- After 3 months, proposed a "test infrastructure sprint" to resolve remaining issues

**Result**:
- Build time reduced from 45 to 12 minutes in 4 months
- Flaky test rate dropped from 15% to <1%
- Team velocity increased 30% (less time waiting for builds/fixing flaky tests)
- The 20% rule became team policy for ongoing technical health

---

## 7. Influencing Without Authority

### Why It Matters

As a staff+ engineer, you will regularly need to influence:
- Teams you don't manage
- Technical decisions you don't control
- Organizational priorities you don't set

You have zero authority but full responsibility for technical outcomes.

### Influence Strategies

**1. Build relationships before you need them:**
- Regular 1:1s with key engineers across teams
- Invest in understanding their priorities and constraints
- Show genuine interest in their work

**2. Lead with data, not opinion:**
- "I believe X is the right approach" (weak)
- "Our data shows Y incurs 40% more cost than X, with similar reliability" (strong)
- Measure before proposing change

**3. Make it easy to say yes:**
- Present options, not just a single recommendation
- Do the prep work: prototype, ADR, migration plan
- Address objections before they're raised

**4. Find the win-win:**
- Demonstrate how your proposal helps their team
- "If we adopt this approach, your team will benefit from..."
- Be willing to compromise on non-essentials

**5. Build a reputation:**
- Be known for being right, fair, and honest
- Consistently deliver on commitments
- Acknowledge when you're wrong

### Sample Answer: Influencing Without Authority

**Question**: "Tell me about a time you had to influence a decision without having authority."

**Situation**: I believed our team should adopt event-driven architecture for a new initiative, but another team's architect was advocating for synchronous REST-based integration.

**Action**:
- Instead of arguing, I proposed a 2-week architecture spike that implemented both approaches for a non-critical use case
- Defined evaluation criteria: latency, coupling, scalability, team autonomy
- Invited the other architect to co-evaluate the results
- When data showed event-driven was 10x more scalable and significantly reduced coupling, the architect agreed
- We wrote a joint ADR documenting the decision and rationale

**Result**:
- Adopted event-driven architecture, which later enabled 3 new features that would have been difficult with sync communication
- Built a stronger relationship with the other architect — we now co-author architecture decisions
- The spike-and-evaluate process became our org's standard for resolving technical disagreements

---

## 8. Defining Technical Strategy

### What Technical Strategy Looks Like at Staff+

**Not a project plan. Not a list of technologies.** Technical strategy is:
- A coherent set of principles and decisions that guide technical direction
- A framework for making future decisions
- An articulation of trade-offs and priorities

### Components of Technical Strategy

**1. Vision (2-3 years):**
- Where will our technical architecture be in 2-3 years?
- What business outcomes will it enable?
- What will be different from today?

**2. Principles:**
- "We prioritize evolvability over optimization"
- "We standardize on event-driven integration"
- "We prefer managed services over self-hosted"
- 5-7 principles that guide decision-making

**3. Focus Areas:**
- What to invest in (scalability, reliability, developer experience)
- What to deprecate (legacy systems, unsupported patterns)
- What to maintain (keep stable, don't invest further)

**4. Roadmap (6-18 months):**
- Key initiatives aligned with focus areas
- Dependencies and sequencing
- Resource requirements

### Sample Answer: Defining Technical Strategy

**Question**: "Tell me about how you've defined technical strategy."

**Situation**: As the org grew from 50 to 200 engineers, we had no coherent technical strategy. Each team made independent decisions, leading to fragmentation and integration challenges.

**Action**:
- Interviewed 20+ engineers, architects, and product leaders to understand pain points and needs
- Synthesized findings into a technical strategy document with:
  - Vision: "A platform where teams can independently build and ship features with minimal coordination"
  - Principles: Event-driven integration, API-first design, managed infrastructure
  - Focus areas: Standardize on event platform, consolidate legacy systems, improve developer experience
- Socialized the strategy through RFCs, presentations, and 1:1 discussions
- Iterated based on feedback and published v1.0
- Established a quarterly review process to update the strategy

**Result**:
- 80% adoption of the event platform across teams within 1 year
- 3 legacy systems deprioritized for deprecation
- Developer satisfaction score increased from 3.2 to 4.1 (out of 5)
- The strategy document became the reference point for cross-team architecture decisions

---

## 9. Company-Specific Behavioral Themes

### Google: Googleyness + Leadership

**Key themes:**
- **Collaboration**: "How have you worked with teams that had competing priorities?"
- **Humility**: "Tell me about a time you were wrong"
- **Intellectual curiosity**: "What's the most interesting technical problem you've solved?"
- **Ambiguity**: "Tell me about a time you had to make decisions with incomplete information"

### Amazon: Leadership Principles

**Must-prepare stories for:**
- **Customer Obsession**: "Tell me about a time you went above and beyond for a customer"
- **Ownership**: "Tell me about a project you owned from end to end"
- **Think Big**: "Describe a bold vision you had and how you made it real"
- **Dive Deep**: "Tell me about a time you had to get into the details to solve a problem"
- **Have Backbone**: "Tell me about a time you disagreed with a decision and challenged it"
- **Deliver Results**: "Tell me about a challenging goal you achieved"
- **Bias for Action**: "Tell me about a time you moved quickly"

### Meta: Building Social Value + Impact

**Key themes:**
- **Impact at scale**: "Describe a project that impacted millions of users"
- **Move fast**: "Tell me about a time you had to ship quickly without compromising quality"
- **Technical leadership**: "How have you set technical direction for your team?"
- **Cross-functional collaboration**: "Describe working with product and design on a complex feature"

### Microsoft: Customer Focus + Growth Mindset

**Key themes:**
- **Customer focus**: "How do you ensure your architecture decisions serve customer needs?"
- **Growth mindset**: "Tell me about a time you learned from a failure"
- **Inclusive leadership**: "How have you fostered diversity and inclusion on your team?"
- **Cross-group collaboration**: "Describe a project that required coordination across multiple divisions"

### Netflix: Freedom and Responsibility

**Key themes:**
- **Judgment**: "Tell me about a high-stakes decision you made with incomplete information"
- **Communication**: "Describe a time you had to deliver difficult feedback"
- **Curiosity**: "What do you do to stay current with technology trends?"
- **Courage**: "Tell me about a time you took a calculated risk"
- **Selflessness**: "How have you helped a teammate succeed at their own expense?"

### Uber: Customer Obsession + Bias for Action

**Key themes:**
- **Customer obsession**: "Tell me about a time you went above and beyond for your users"
- **Bias for action**: "Describe a time you made a decision quickly even though you had incomplete information"
- **Ownership**: "Tell me about a project you drove from idea to production"
- **Data-informed**: "Describe a technical decision you made based on data"

### Stripe: Empathy + Rigor

**Key themes:**
- **Technical rigor**: "Tell me about a complex technical problem you solved"
- **User empathy**: "How do you ensure your architecture decisions serve developers?"
- **Communication**: "Describe a time you had to write a detailed technical proposal"
- **Transparency**: "Tell me about a time you shared bad news with stakeholders"

---

## 10. STAR Answer Framework

### STAR Structure

| Component | What to Include | Example |
|-----------|----------------|---------|
| **Situation** | Context, background, who, what, when | "Our team of 8 engineers was responsible for migrating 50 microservices..." |
| **Task** | Your specific responsibility and goal | "I needed to define the migration strategy and coordinate across 5 teams..." |
| **Action** | What YOU did (not the team), with specific details | "I created a migration playbook, prioritized services by dependency, and..." |
| **Result** | Measurable outcome, quantified | "All 50 services migrated within 6 months with zero downtime..." |

### STAR Tips for Staff+

- **Own the "I"**: Use "I" not "we" for your actions. You can mention the team, but highlight your specific contribution
- **Depth over breadth**: One detailed story is better than three shallow ones
- **Numbers matter**: Quantify everything (time saved, cost reduced, performance improved)
- **Trade-offs**: Mention what you didn't do (and why)
- **Learning**: End with what you learned or would do differently

### Anti-STAR: Common Mistakes

| Mistake | Why It Fails |
|---------|-------------|
| Too much situation | Interviewer wants action, not context |
| Vague results | "It went well" is not a result |
| "We" not "I" | Interviewer can't evaluate your contribution |
| No learning | Staff+ should show growth from every experience |
| Perfect story | No trade-offs, no mistakes = not believable |

---

## 11. Common Behavioral Questions Bank

### Technical Leadership

1. Tell me about the most technically complex project you've led.
2. Describe a time you had to make a difficult technical decision with limited information.
3. How do you stay current with technology trends? Give an example of a technology you adopted.
4. Tell me about a time your technical judgment was challenged and how you responded.
5. Describe a situation where you had to choose between speed and quality.
6. How do you evaluate whether a technology is right for your organization?
7. Tell me about a system you designed that evolved significantly over time.
8. Describe a time you had to simplify a complex technical problem for non-technical stakeholders.

### Driving Change

9. Tell me about a time you drove a significant process or architecture change.
10. Describe a situation where you had to get buy-in from a skeptical team.
11. How have you dealt with resistance to technical change?
12. Tell me about a migration or transition you led. What was the approach?
13. Describe a time you championed a new technology or approach that was initially rejected.

### Mentoring and Growth

14. Tell me about a time you helped someone grow in their career.
15. How do you create growth opportunities for engineers on your team?
16. Describe your approach to code and design reviews.
17. Tell me about a time you gave difficult feedback and how it was received.
18. How have you contributed to hiring and raising the technical bar?

### Cross-Team Collaboration

19. Tell me about a project that required coordination across multiple teams.
20. Describe a time you had to resolve a technical disagreement between teams.
21. How have you handled a situation where another team was blocking your project?
22. Tell me about a time you built strong relationships that helped you achieve a technical goal.

### Technical Debt and Quality

23. How do you balance shipping features with maintaining code quality?
24. Tell me about a time you convinced your team to address technical debt.
25. Describe a situation where technical debt caused a significant problem.
26. How do you know when to refactor vs. build something new?

### Failure and Learning

27. Tell me about a project that failed or didn't meet expectations.
28. Describe a technical decision you made that you later regretted.
29. What's your biggest technical mistake and what did you learn from it?
30. Tell me about a time you had to pivot or change direction on a project.

### Strategy and Vision

31. How do you define technical strategy for your organization?
32. Tell me about a long-term technical initiative you drove.
33. How do you align technical decisions with business goals?
34. Describe the technical vision you have for your current organization.

---

## 12. Anti-Patterns and Red Flags

### Behavioral Anti-Patterns (What Interviewers Flag)

| Anti-Pattern | How to Avoid |
|-------------|-------------|
| **Taking sole credit** | Acknowledge team contributions while highlighting your specific role |
| **No measurable impact** | Always quantify: "Reduced latency by 40%", "Saved $500K/year" |
| **Blame external factors** | Take ownership even when factors outside your control contributed |
| **Too theoretical** | Ground every answer in real experience, not hypotheticals |
| **No learning** | Every challenge story should end with what you learned |
| **Defensive** | Accept feedback gracefully, acknowledge mistakes openly |
| **Generic answers** | Every story should be specific to your unique experience |
| **Missing context** | Explain the "why" behind decisions, not just the "what" |

### Red Flags for Interviewers

- **No cross-team experience**: "I only worked within my team" — problem for staff+
- **No failure stories**: "Everything I touch succeeds" — not believable or lacks growth
- **No people development**: "I focus on technical work" — staff+ is expected to grow others
- **No strategic thinking**: Only tactical execution stories
- **Cannot articulate trade-offs**: Staff+ must explain why they chose one option over another
- **Overly technical, no business context**: Staff+ connects technology to business outcomes

---

## Behavioral Story Bank Template

Create 10-15 stories covering:

| # | Category | Story Topic | Key Message | Company LP Mapping |
|---|----------|-------------|-------------|-------------------|
| 1 | Technical Leadership | Architecture migration | Long-term thinking | Think Big (Amazon) |
| 2 | Driving Change | Adopting event-driven arch | Influencing others | Have Backbone (Amazon) |
| 3 | Mentoring | Growing a senior engineer | People development | Hire & Develop (Amazon) |
| 4 | Cross-Team | Multi-team platform launch | Collaboration | Deliver Results (Amazon) |
| 5 | Technical Debt | Test infrastructure overhaul | Quality focus | Insist on Highest Standards |
| 6 | Failure | Failed migration attempt | Learning | Learn and Be Curious |
| 7 | Strategy | Technical roadmap creation | Vision setting | Think Big |
| 8 | Conflict | Resolved architecture disagreement | Communication | Have Backbone |
| 9 | Customer | Feature built from customer feedback | Customer focus | Customer Obsession |
| 10 | Bias for Action | Shipped feature ahead of schedule | Speed | Bias for Action |

---

*This guide covers the behavioral expectations for staff+ engineering roles. Practice with the company-specific guides for targeted preparation.*
