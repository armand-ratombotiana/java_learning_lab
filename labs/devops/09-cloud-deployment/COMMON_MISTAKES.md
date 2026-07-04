# Common Cloud Deployment Mistakes

1. **Not using managed services** — running databases on VMs adds operational overhead.
2. **Over-provisioning resources** — expensive idle capacity; use auto-scaling.
3. **Ignoring cloud costs** — forgotten resources, unattached volumes, unused IPs.
4. **No multi-region/HA** — single-AZ deployment causes outages.
5. **Security group sprawl** — overly permissive rules, too many open ports.
6. **Hardcoded credentials** — use IAM roles, workload identity, managed secrets.
7. **No tagging strategy** — can't track costs by team/project/environment.
8. **Forgetting data backup** — no RDS snapshots, no S3 versioning.
9. **Not using infrastructure as code** — manual console changes lead to drift.
10. **Overlooking service limits** — API rate limits, instance limits, S3 bucket limits.
11. **No cost alerts** — surprise bills from unmonitored scaling.
12. **Cross-region data transfer costs** — optimize service placement to minimize.
