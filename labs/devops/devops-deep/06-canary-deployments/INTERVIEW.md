# Interview Questions — Canary Deployments

## Q1: How does traffic shifting work in a canary deployment?
**A:** Traffic is gradually shifted from the stable version to the canary version using weighted routing (e.g., 10% canary → 25% → 50% → 100%). If metrics degrade at any step, the deployment rolls back.

## Q2: What metrics would you monitor during a canary?
**A:** HTTP 5xx error rate, p99 latency, request throughput, and business metrics (conversion rate, revenue per user). Compare canary vs stable metrics.

## Q3: How does Argo Rollouts differ from a standard Kubernetes Deployment?
**A:** Argo Rollouts provides progressive delivery strategies (canary, blue-green) with automated analysis and rollback. Standard Deployments only support rolling update without traffic shaping or analysis gates.

## Q4: What happens if a canary analysis fails mid-rollout?
**A:** The Rollout controller aborts the canary and scales down the canary replicas. Traffic reverts to 100% stable. The previous stable ReplicaSet remains intact.
