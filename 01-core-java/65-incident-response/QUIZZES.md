# Module 65: Incident Response & SRE - Quizzes

---

## Q1: SRE Terminology
What is the difference between an SLO and an SLA?

A) An SLO is a technical objective set by engineering (e.g., 99.9% uptime); an SLA is a business contract with customers that dictates financial penalties if the SLO is breached.
B) An SLA is used for databases, while an SLO is used for web servers.
C) An SLO is the actual measured performance, while an SLA is the goal.
D) There is no difference.

**Answer**: A
**Explanation**: SLAs involve lawyers and money. SLOs involve engineers and metrics. To ensure the SLA is never breached, engineering teams set their internal SLOs much stricter than the SLA requires.

---

## Q2: Error Budgets
What is the primary purpose of an "Error Budget" in Site Reliability Engineering?

A) To track how much money was lost during an outage.
B) To act as a control mechanism between Development (who want to ship features fast) and Operations (who want system stability). If the error budget is depleted, feature freezes are enacted until stability recovers.
C) To limit the number of logs stored in Elasticsearch.
D) To fine developers who introduce bugs.

**Answer**: B
**Explanation**: The Error Budget aligns incentives. It objectively proves whether the system is stable enough to tolerate the inherent risk of a new deployment. If the 30-day error budget is exhausted, deployments must stop, and the team must pivot entirely to fixing technical debt.

---

## Q3: Post-Mortems
Why must an Incident Post-Mortem be "blameless"?

A) Because human error is never the cause of a system outage.
B) To protect the company from legal liability.
C) Because if engineers fear punishment or firing, they will hide the truth, cover up their actions, and the organization will never learn the true root cause necessary to prevent the outage from happening again.
D) Because SREs are not allowed to be fired.

**Answer**: C
**Explanation**: You cannot fix a systemic problem if people are afraid to talk about what they did leading up to the outage. Blameless culture assumes everyone acted with the best intentions based on the information they had at the time, and focuses strictly on fixing the tooling/automation that allowed the error to occur.