# Lab 06 — Production Deployment Rollback: Interview Questions

**Q1: What is a canary deployment and how does it reduce deployment risk?**

**Answer:** A canary deployment routes a small percentage of traffic (e.g., 1%) to the new version before rolling to 100%. If the canary shows errors, latency spikes, or error budget consumption, the deployment is automatically rolled back. Benefits: limits blast radius, provides real traffic validation, enables automatic rollback based on metrics. The canary should run for at least 5-10 minutes to catch latent issues like memory leaks.

**Q2: What's the difference between rollback and rollforward?**

**Answer:** Rollback: revert to the previous version by redeploying the old artifact. Fast but loses the new features. Rollforward: deploy a new version that fixes the issue introduced by the previous deployment. Preserves features but takes longer to develop. Decision factors: severity (rollback for P0, rollforward for minor issues), time to fix (rollback if fix takes > 1 hour), data compatibility (rollback may not work if schema changed).

**Q3: Design a deployment pipeline with automatic rollback.**

**Answer:** 1) Build phase: compile, unit test, static analysis. 2) Deploy to staging: integration test, load test, security scan. 3) Deploy to canary (1%): monitor error rate, latency, CPU, memory for 5 min. 4) If canary passes → deploy to 10% for 5 min. 5) If 10% passes → deploy to 50% for 5 min. 6) If 50% passes → deploy to 100%. 7) At any stage: if error budget burn rate > threshold → automatic rollback. 8) Post-deployment: monitor for 30 min, then mark deployment as complete.

**Q4: How do you handle database schema changes during deployment rollback?**

**Answer:** Schema changes are the hardest to roll back. Strategies: 1) Separate schema migration from application deployment (migrate first, deploy later). 2) Make all schema changes backward-compatible (add columns as nullable, don't drop columns). 3) For column renames: add new column, dual-write, migrate reads, drop old column in separate deployment. 4) For table drops: rename instead of drop, keep for N days as safety net. 5) Use feature flags to gate code that depends on new schema.

**Q5: You deploy a new version and immediately see 500 errors. What do you do?**

**Answer:** 1) Verify the errors are from the new deployment (not pre-existing). 2) If deployment is still in progress (canary), stop and roll back the canary. 3) If fully deployed, initiate rollback immediately — do NOT try to debug first. 4) Rollback: redeploy the previous known-good version. 5) After rollback, verify error rate returns to normal. 6) Investigate root cause in staging or development environment. 7) Create a post-mortem on why testing didn't catch the issue.

**Q6: What metrics should trigger an automatic deployment rollback?**

**Answer:** 1) Error rate increase > 0.5% from baseline (or any increase from zero). 2) P99 latency increase > 50% from baseline. 3) Error budget burn rate > 2x for the deployment window. 4) CPU/memory utilization > 90%. 5) Health check failures > 10%. 6) Circuit breaker trip events > 0. 7) Business metrics: conversion rate, signup rate, checkout completion rate drop > 5%.

**Q7: What's the difference between blue-green deployment and rolling deployment?**

**Answer:** Blue-green: maintain two identical environments. Route all traffic to blue, deploy to green, switch traffic. Instant rollback by switching back to blue. Rolling: gradually replace instances in the same environment. Blue-green is simpler to roll back (DNS change) but requires 2x infrastructure cost. Rolling is more resource-efficient but slower rollback (redeploy previous version to each instance).

**Q8: How do you test a rollback procedure?**

**Answer:** 1) Chaos engineering: inject failure into new version, verify automatic rollback triggers. 2) Regular drills: schedule monthly rollback drills where you deliberately deploy a bad version and practice the rollback. 3) Measure: time to detect failure, time to initiate rollback, time to complete rollback, verification time. 4) Document and improve the runbook based on each drill. 5) Automate the entire rollback process — no manual steps.

**Q9: Your team deploys every 2 weeks. How do you reduce the risk of each deployment?**

**Answer:** 1) Deploy more frequently (smaller changes = less risk). 2) Implement feature flags — deploy code off, turn on when ready. 3) Canary deployments with automatic rollback. 4) Pre-deployment load testing. 5) Database migration automation with backward compatibility. 6) Post-deployment monitoring dashboard. 7) Rollback runbook tested regularly. 8) Deployment freeze during critical business periods.

**Q10: How does infrastructure as code (IaC) help with deployment rollbacks?**

**Answer:** IaC (Terraform, CloudFormation) versions infrastructure changes alongside application code. Rollback: apply the previous version of the IaC configuration. Benefits: 1) Infrastructure changes are tested, reviewed, versioned. 2) Side-by-side environments (blue-green) for instant switch. 3) State management tracks what was applied. 4) Automation eliminates manual configuration drift. 5) Entire environment (app + infra) can be rolled back atomically.

**Q11: Tell me about a deployment incident you handled. (STAR)**

**Answer:** Situation: A routine deployment to payment processing service caused 500 errors on 100% of transactions. Task: As deployment lead, I needed to restore service. Action: I was monitoring the deployment dashboard and saw error rate spike from 0.1% to 35% within 2 minutes of the canary. I initiated automatic rollback, which redeployed the previous version in 3 minutes. Error rate returned to 0.1%. I investigated and found a missing environment variable in the new configuration. Result: Rollback completed in 5 minutes, customer impact limited to ~2% of traffic. Added automated configuration validation to CI/CD.

**Q12: How do you design a deployment system for a microservice architecture with 200+ services?**

**Answer:** 1) Dependency graph: determine deployment order (downstream dependencies first). 2) Independent deployment: each service deploys independently with backward/forward compatibility. 3) API versioning: never break backward compatibility; add new API versions. 4) Service mesh: traffic splitting at mesh level for canary deployments. 5) Automated rollback: each service has defined health metrics for automatic rollback. 6) Deployment coordination: use a deployment orchestrator (Spinnaker, ArgoCD) that handles ordering. 7) Global deployment view: dashboard showing deployment status across all services.

**Q13: What's the most important principle for safe deployments?**

**Answer:** Deploy in small, reversible increments. Every deployment should be: 1) Small (few lines of code, not hundreds), 2) Independent (no cross-service dependencies), 3) Reversible (instant rollback via feature flag or version switch), 4) Observable (monitored with clear success/failure criteria), 5) Incremental (canary → percentage → full). If a deployment can't be rolled back in under 5 minutes, it's too risky.

**Q14: How do feature flags complement deployment rollbacks?**

**Answer:** Feature flags provide instant kill-switch for new features without redeployment. When combined with deployments: deploy new code with feature flag OFF. Enable flag for internal testers, then 1%, 10%, 50%, 100%. If issue found: disable flag (instant rollback of the feature). This separates deployment risk from feature enablement risk. Tools: LaunchDarkly, Unleash, or self-built with configuration service.

**Q15: How do you handle a deployment that fails with data migration issues?**

**Answer:** 1) Stop the deployment immediately. 2) Assess data state: did the migration run? Partially? Fully? 3) If migration ran and is backward-compatible: rollback application, keep migration. 4) If migration is NOT backward-compatible: execute rollback migration script. 5) Verify data integrity after rollback. 6) Fix migration to be backward-compatible before retrying. 7) Test migration rollback in staging before production.
