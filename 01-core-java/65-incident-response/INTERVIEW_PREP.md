# Module 65: Incident Response & SRE - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the "Error Budget" and how does it balance the relationship between Development and Operations?
**Answer**:
Traditionally, Developers want to deploy new features as fast as possible (which introduces instability), while Operations wants to restrict deployments to maintain 100% uptime (which stifles innovation). 
The **Error Budget** is derived from the Service Level Objective (SLO). If the SLO is 99.9% uptime, the Error Budget is 0.1% downtime (about 43 minutes a month). 
This budget acts as a mathematical agreement between Dev and Ops. Developers can deploy as fast and as riskily as they want, *as long as* the error budget is not depleted. Once the 43 minutes are consumed, the budget is gone. All feature deployments immediately freeze, and the entire team must pivot to fixing reliability issues (technical debt) until the rolling 30-day window restores the budget.

### Q2: What are the three primary roles in an Incident Command Structure?
**Answer**:
1. **Incident Commander (IC)**: The leader. They do not write code or look at logs. They orchestrate the response, assign tasks, ensure communication is flowing, and make high-level decisions (e.g., "We are rolling back now").
2. **Communications Lead**: Handles all external messaging. Updates the public status page, manages stakeholder updates, and shields the engineers from executives asking "Is it fixed yet?".
3. **Operations / Subject Matter Expert (SME)**: The engineers who are actually looking at Datadog, reading logs, and writing the hotfix or rollback commands. They report directly to the IC.

### Q3: Why is "Fixing Forward" considered an anti-pattern during a SEV-1 outage?
**Answer**:
When the system is completely down and losing money by the second, the goal is **Mitigation**, not resolution. 
"Fixing Forward" means writing new code to patch the bug while the system is bleeding. This takes time, requires testing, and introduces the high risk of a panic-induced secondary bug.
The fastest way to mitigate is almost always a **Rollback** to the previously known good state. You stop the bleeding instantly, restore service for the customers, and *then* take your time to debug, write the fix, and deploy it calmly the next day.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Conducting a "5 Whys" Root Cause Analysis
**Problem**: During an interview, the hiring manager says: "Yesterday, our main application crashed. The initial report says it crashed because the hard drive ran out of space. As the SRE, walk me through a '5 Whys' exercise to find the actual systemic root cause and propose an action item."

**Solution**:
1. **Why did the app crash?** Because the server's hard drive ran out of space.
2. **Why did the hard drive run out of space?** Because the application generated 500GB of log files in 2 hours.
3. **Why did it generate so many logs?** Because the application was stuck in an infinite retry loop, logging a `NullPointerException` 10,000 times a second.
4. **Why didn't anyone notice the drive filling up?** Because we don't have disk-space alerts configured in Datadog; we only have CPU and Memory alerts.
5. **Why was it stuck in an infinite retry loop?** Because we implemented a retry mechanism without an Exponential Backoff or a Circuit Breaker.

**Action Items**:
- Add disk space monitoring and alerting to all nodes.
- Implement Resilience4j Circuit Breakers to open the circuit after 5 failures, stopping the infinite loop and the log spam.