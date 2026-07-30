# Interview Questions — SRE Practices

## Q1: What is the difference between SLO, SLI, and SLA?
**A:** SLI is a metric (e.g., latency p99). SLO is a target value for the SLI (e.g., p99 ≤ 200ms). SLA is a contractual commitment to the SLO, often with penalties.

## Q2: How does error budget drive decision-making?
**A:** Error budget is the acceptable amount of unreliability. If remaining budget is high, teams can deploy risky changes. If budget is low or depleted, teams focus on reliability (stop deployments, fix bugs).

## Q3: What is toil in SRE terms?
**A:** Toil is manual, repetitive, automatable, tactical work with no enduring value. Examples: restarting pods, manually scaling, answering pages for non-urgent alerts.

## Q4: What makes a postmortem blameless?
**A:** Focus on systemic failures, not individual mistakes. Blameless postmortems encourage honest incident analysis, leading to better action items and fewer repeat incidents.
