# Common Advanced Orchestration Mistakes

1. **No resource requests/limits** — HPA won't work, QoS is BestEffort, overcommit issues.
2. **Wrong probe configuration** — too aggressive (thrashing) or too lenient (delayed recovery).
3. **No startup probe for slow apps** — container repeatedly killed before it can start.
4. **PDB blocking all operations** — `minAvailable: 100%` on single-replica deployment blocks updates.
5. **HPA thrashing** — no stabilization window; rapid scale up/down.
6. **Ignoring HPA metrics** — external/custom metrics not collected; HPA inactive.
7. **Overly restrictive resource quotas** — prevents legitimate scaling.
8. **Missing PDBs for critical services** — node drain kills all replicas.
9. **Not testing rollbacks** — untested undo paths cause incidents.
10. **Manual intervention on auto-scaled deployments** — HPA resets replica count.
11. **Canary without monitoring** — can't detect v2 issues, proceeds blindly.
12. **No maxSurge for zero-downtime** — maxUnavailable: 25% allows 1 pod down at any time.
