# Canary Deployments — Step-by-Step Guide

## 1. Traffic Shifting
- Start: 100% stable, 0% canary.
- Increment: 10% → 25% → 50% → 75% → 100%.
- Strategies: weighted random, header-based (specific users), cookie-based.

## 2. Metrics Gates
- Monitor: error rate (p99 latency, HTTP 5xx, throughput).
- If error rate > threshold → auto-rollback (revert to 100% stable).
- If metrics healthy → promote to next step.

## 3. Argo Rollouts
- `Rollout` resource replaces `Deployment`.
- Strategies: `canary` or `blueGreen`.
- Steps: `setWeight`, `pause`, `analysis`.
- `AnalysisTemplate` defines metric queries (Prometheus, Datadog).

## 4. Rollback
- If analysis fails → Rollout reverts to previous stable replica set.
- Manual rollback via `kubectl argo rollouts abort`.

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/devops/deep/lab06/*.java
java --enable-preview -cp out com.devops.deep.lab06.CanaryLab
```
