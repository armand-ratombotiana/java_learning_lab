# Module 64: Chaos Engineering - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the relationship between Chaos Engineering and Fallacies of Distributed Computing?
**Answer**:
The "Fallacies of Distributed Computing" (e.g., "The network is reliable", "Latency is zero", "Bandwidth is infinite") are dangerous assumptions engineers make when building microservices. 
Chaos Engineering is the deliberate, active methodology used to prove that these fallacies exist and to test how the system reacts to them. Instead of assuming the network is reliable, a chaos engineer actively introduces 5 seconds of latency into the network via an assault and verifies whether the system's timeout and retry configurations are robust enough to handle reality.

### Q2: Why is it highly recommended to execute Chaos experiments in Production environments?
**Answer**:
Staging environments are notoriously inaccurate. They rarely mirror the true scale, traffic patterns, database sizes, or network topology of Production. An experiment that passes perfectly in Staging might cause a catastrophic cascading failure in Production.
While engineers should *start* in staging to gain confidence, the ultimate goal of Chaos Engineering is to run experiments directly in Production (during normal business hours, tightly monitored) because that is the only environment that provides genuine, undeniable proof that the system is truly resilient to real-world failures.

### Q3: What is the "Steady State" and why must it be defined before injecting chaos?
**Answer**:
The Steady State is a measurable baseline of the system's normal, healthy behavior (e.g., "P99 latency is under 200ms, and 99.9% of HTTP requests return 200 OK").
If you do not define and measure the Steady State first, injecting chaos is just randomly breaking servers without scientific purpose. You must measure the Steady State, form a hypothesis ("If I kill Node A, the Steady State will not change"), inject the chaos, and observe. If the system deviates from the Steady State, your hypothesis was wrong, and you've discovered an architectural flaw.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: The "Game Day" Setup
**Problem**: An interviewer says, "Our CTO wants to start doing Chaos Engineering, but the dev team is terrified we will break the production billing database. How would you plan and execute our very first Chaos 'Game Day' to ensure total safety?"

**Solution**:
A Game Day must be heavily structured.
1. **Choose a Safe Target**: Do not target the billing database first. Choose a non-critical, stateless service (e.g., the Recommendation Engine or User Avatars).
2. **Define the Hypothesis**: "If the Recommendation Engine goes down, the homepage will still load, but with a generic default list instead of personalized items."
3. **Establish Metrics**: Look at the Grafana dashboard for the homepage load time and error rates (The Steady State).
4. **Define the Abort Condition**: "If global error rates spike by more than 1%, or if the homepage completely fails to load, we will abort the experiment immediately."
5. **Execute**: Manually scale the Recommendation deployment down to 0 pods, or inject a massive latency delay.
6. **Observe & Revert**: Monitor the dashboard. Note the results. Immediately revert the chaos.
7. **Post-Mortem**: Document findings and create Jira tickets to fix any exposed weaknesses.