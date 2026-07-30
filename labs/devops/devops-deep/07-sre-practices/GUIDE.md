# SRE Practices — Step-by-Step Guide

## 1. SLI Definition
- Latency: p99 ≤ 200ms over 5min window.
- Availability: requests with non-5xx status / total requests ≥ 99.9%.
- Throughput: requests per second.

## 2. SLO Targets
- 99.9% availability monthly → 43m 12s allowed downtime.
- 99.99% → 4m 19s allowed downtime.

## 3. Error Budget
- Error budget = 100% - SLO (e.g., 0.1% of total requests).
- Burn rate: how fast error budget is consumed.
- If burn rate > threshold → stop deployments, focus on reliability.

## 4. Toil Automation
- Toil: manual, repetitive, automatable work.
- Measure toil hours per week; target < 50%.
- Automate: runbooks (scripts), ChatOps, self-service tools.

## 5. Postmortem
- Blameless: focus on what happened, not who did it.
- Timeline, impact, root cause, action items.
- Share widely to prevent recurrence.

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/devops/deep/lab07/*.java
java --enable-preview -cp out com.devops.deep.lab07.SreLab
```
